package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Vector;

import state.Settings;

import Main.SicComponents;


import file.FileIO;

public class SicUploader {

	private MulticastSocket dataSocket;
	private Vector<TransferCommander> cmdSockets;
	
	private InetAddress group;
	private FileIO fio;
	
	private int fragmentsNeeded;			//number of fragments needed for a file
	
	private byte[] fragment;				//fragment about to be sent
	private byte[] fileData;				//data read from file
	
	private DatagramPacket cmdPacket;		//multicast packet containing a command
	private byte[] cmdBuffer;				//command packet buffer
	private DatagramPacket sendData;
	
	SicComponents components;
	
	public SicUploader(MulticastSocket listener, SicComponents components) {
		this.dataSocket = listener;
		try {
			this.dataSocket.setSoTimeout(50);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.group = components.settings.get_multicastGroup();
		this.components = components;
		fio = new FileIO();
		
		
		//Initialize cmd components
		cmdBuffer = new byte[SicNetworkProtocol.cmdPacketSize];
		cmdPacket = new DatagramPacket(cmdBuffer, SicNetworkProtocol.cmdPacketSize, group, SicNetworkProtocol.port);
		
		//initialize data components
		fragment = new byte[SicNetworkProtocol.dataPacketSize];
		fragment[0] = 1;	//mark this as a data packet
		sendData = new DatagramPacket(fragment, fragment.length, group, SicNetworkProtocol.port);
	}
	
	
	
	
	public void initateUpload(Vector<File> filesChanged, Vector<File> filesDeleted, String directoryPath, int revision) throws IOException {
		System.out.println("Uploading " + filesChanged.size() + " files to peers");
		
		//prepare socket to listen for tcp connections
		cmdSockets = new Vector<TransferCommander>();
		ServerSocket responces = new ServerSocket(SicNetworkProtocol.transferCmdPort);
		responces.setSoTimeout(50);
		
		//send cmdBuffer packet notifying of revision update
		for (int i = 0; i < SicNetworkProtocol.cmdPacketSize; ++i) cmdBuffer[i] = 0;

		SicNetworkProtocol.setIP(cmdBuffer);
		cmdBuffer[1] = SicNetworkProtocol.pushRevision;
		for (int i = 0; i < 30; ++i) {
			dataSocket.send(cmdPacket);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		
		//wait for clients to attempt a tcp connection
		//accept any inbound connections
		//TODO: calibrate wait time
		long waitTime = System.currentTimeMillis() + 5000;
		while(waitTime > System.currentTimeMillis()){
			try{
				TransferCommander cmd = new TransferCommander(responces.accept());
				cmd.writer.println(filesChanged.size());
				
				cmdSockets.add(cmd);
				
				
				System.out.println("Recieved connection from " + cmd.sock.getInetAddress().getHostAddress());
			} catch(SocketTimeoutException ste){
				continue;
			}
		}
		responces.close();
		if (cmdSockets.size() == 0) {
			System.out.println("No Clients Found on Network, not sending file");
			responces.close();
			return;
		}
		
		//What files should be deleted
		for (File delete : filesDeleted) {
			String relativePath = delete.getAbsolutePath().substring(directoryPath.length());
			
			for (TransferCommander client : cmdSockets) {
				client.writer.println(relativePath);
			}
			
		}
		
		//Signal clients to begin file upload stage
		for (TransferCommander client : cmdSockets) {
			client.writer.println();
		}
		
		//update fragment header with current revision number
		SicNetworkProtocol.setDataFragmentId(fragment, revision);
		
		
		//loop through files
		int fileCounter = 0;
		for (File file : filesChanged) {
			
			sendFile(file, fileCounter, directoryPath);
			fileCounter++;
			
		}
		
		components.dirMonitor.clearVectors();
	}
	
	
	public void sendFile(File file, int fileCounter, String rootPath) throws IOException {
		
		//read file to buffer
		fileData = fio.readFile(file);
		
		
		//calculate number of fragments required for file
		fragmentsNeeded = (int) Math.ceil((double)(fileData.length)/((double)SicNetworkProtocol.dataPacketDataCapacity));
		
		//find the relative path to the file from root directory.
		String relativePath = file.getAbsolutePath().substring(rootPath.length());
		
		//send startFile packet
		for (TransferCommander client : cmdSockets) {
			//send size
			client.writer.println(fileData.length);
			
			//send relative path
			client.writer.println(relativePath);
		
		}
		
		System.out.println("File sent: "+relativePath+ ", " +fileData.length + " Bytes via " + fragmentsNeeded + " fragments." );
		
		
		
		//send file in fragments
		for (int fragID = 0; fragmentsNeeded > fragID; ++fragID) {
			
//			byte[] frag = 
			formatFragment(fragID);
			
			//send the fragment
			dataSocket.send(sendData);
			
			//TODO: Calibrate wait time
			try {
				Thread.sleep(components.settings.getDelay());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//notify client that done sending fragments
		for (TransferCommander c : cmdSockets) {
			c.writer.println();
		}
		
		System.out.println("\tFinished sending " + relativePath + " waiting for acks");

		//go through every client ask for an ack or process there nacks
		//TODO: optimize ack loop, don't want to wait for people who are saving a 10 gig file to hard disk
		for (TransferCommander cmd : cmdSockets) {

			ackLoop:
			while (true) {
				int rcv = Integer.parseInt(cmd.reader.readLine());
				
				if (rcv == SicNetworkProtocol.fileCompleteAck) {
					//ack
					break ackLoop;
				} else {
					//nack, re-send missed fragment over tcp
					byte[] frag = formatFragment(rcv);
					cmd.fragWriter.write(frag);
					cmd.writer.println();
					
				}
			}
		}
		
	}
	
	
	/**
	 * this class creates a fragment 
	 * @param fragID
	 * @return
	 * @throws IOException
	 */
	public byte[] formatFragment(int fragID) throws IOException {
		
		//set fragment id number
		SicNetworkProtocol.setDataFragmentId(fragment, fragID);
		
		//if it is the last fragment to be sent then handle it specially
		if (fragID+1 == fragmentsNeeded) {
			
			int remainingData = fileData.length - fragID * SicNetworkProtocol.dataPacketDataCapacity;
			for (int i = SicNetworkProtocol.dataPacketHeaderSize + remainingData; i < fragment.length; ++i) {
				fragment[i] = 0;
			}
			
			for (int i = 0; i < remainingData; ++i) {
				fragment[SicNetworkProtocol.dataPacketHeaderSize + i] 
						= fileData[fragID * SicNetworkProtocol.dataPacketDataCapacity + i];
			}
			
		} else {
		
			//copy file data to fragment
			for (int dataPtr = 0; dataPtr < SicNetworkProtocol.dataPacketDataCapacity; ++dataPtr) {
				int readByte =   fragID * SicNetworkProtocol.dataPacketDataCapacity + dataPtr;
				
				fragment[SicNetworkProtocol.dataPacketHeaderSize + dataPtr] = fileData[readByte];
				
			}
		}
		
		//create a checksum to
		SicNetworkProtocol.generateChecksum(fragment);
		
		
		return fragment;
	}
	

	public static void main(String[] args) throws IOException {
		

//		
//		String root = "E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/";
//		String root = "C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles";
//		String root = "/Users/sherryliao_1/desktop";
		String root  = "/Users/VietPhan/Desktop/Jones/";
		
		Vector<File> filesChanged = new Vector<File>();
		Vector<File> filesDeleted =  new Vector<File>();
		
		filesDeleted.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.exe"));
		
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.exe"));
		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test1.exe"));
		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test2.exe"));

//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.txt"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image.jpeg"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image(1).jpeg"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image(2).jpeg"));
		
//		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.exe"));
//		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.txt"));
//		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image.jpeg"));
		
		
		
		MulticastSocket listener = new MulticastSocket (SicNetworkProtocol.port);
		InetAddress group = InetAddress.getByName("230.0.0.10");
		listener.joinGroup(group); //join the multicast group
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		SicComponents c = new SicComponents();
		c.settings = new Settings();
		c.settings.set_multicastGroup(group);
		
		SicUploader uploader = new SicUploader(listener, c);
		uploader.initateUpload(filesChanged, filesDeleted,root,1);
		
	}

}
