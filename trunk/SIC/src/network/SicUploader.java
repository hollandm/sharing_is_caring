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


import file.FileIO;

public class SicUploader {

	private MulticastSocket dataSocket;
	private Vector<TransferCommander> cmdSockets;
	
	private InetAddress group;
	private FileIO fio;
	
	private int fragmentsNeeded;			//number of fragments needed for a fike
	
	private byte[] fragment;				//fragment about to be sent
	private byte[] fileData;				//data read from file
	
	private DatagramPacket cmdPacket;		//multicast packet containing a command
	private byte[] cmdBuffer;				//command packet buffer
	private DatagramPacket sendData;
	
	
	public SicUploader(MulticastSocket listener, InetAddress group) throws SocketException {
		this.dataSocket = listener;
		this.dataSocket.setSoTimeout(50);
		this.group = group;
		fio = new FileIO();
		
		
		//Initialize cmd components
		cmdBuffer = new byte[SicNetworkProtocol.cmdPacketSize];
		cmdPacket = new DatagramPacket(cmdBuffer, SicNetworkProtocol.cmdPacketSize, group, SicNetworkProtocol.port);
		
		//initialize data components
		fragment = new byte[SicNetworkProtocol.dataPacketSize];
		fragment[0] = 1;	//mark this as a data packet
		sendData = new DatagramPacket(fragment, fragment.length, group, SicNetworkProtocol.port);
	}
	
	
	
	
	public void initateUpload(Vector<File> filesChanged, String directoryPath, int revision) throws IOException {
		System.out.println("Uploading " + filesChanged.size() + " files to peers");
		
		//prepare socket to listen for tcp connections
		cmdSockets = new Vector<TransferCommander>();
		ServerSocket responces = new ServerSocket(SicNetworkProtocol.transferCmdPort);
		responces.setSoTimeout(50);
		
		
		//send cmdBuffer packet notifying of revision update
		for (int i = 0; i < SicNetworkProtocol.cmdPacketSize; ++i) cmdBuffer[i] = 0;
		//TODO place hosts ip address in packet, InetAddress.getLocalHost().getHostAddress()
		cmdBuffer[1] = SicNetworkProtocol.pushRevision;
		dataSocket.send(cmdPacket);

		
		//wait for clients to attempt a tcp connection
		//accept any inbound connections
		long waitTime = System.currentTimeMillis() + 1000;
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
		
		//update fragment header with current revision number
		//TODO: implement revison numbers, currently always recvision 0
		
		//loop through files
		int fileCounter = 0;
		for (File file : filesChanged) {
			
			sendFile(file, fileCounter, directoryPath);
			fileCounter++;
			
		}
		

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
			
			//TODO: send checksum
		}
		
		System.out.println("File sent: "+relativePath+ ", " +fileData.length + " Bytes via " + fragmentsNeeded + " fragments." );
		
		
		
		//send file in fragments
		for (int fragID = 0; fragmentsNeeded > fragID; ++fragID) {
			
			byte[] frag = formatFragment(fragID);
			
			//send the fragment
			dataSocket.send(sendData);
			
			//TODO: Calibrate wait time
			try {
				Thread.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//TODO: notify client that done sending fragments
		for (TransferCommander c : cmdSockets) {
			c.writer.println();
		}
		
		System.out.println("\tFinished sending "+relativePath);
		
		
		//TODO: handle multiple clients acking
		System.out.println("Waiting for acks");
		
		TransferCommander cmd = cmdSockets.firstElement();
		
		while (true) {
			int rcv = Integer.parseInt(cmd.reader.readLine());
			
			if (rcv == 0) {
				//ack
				break;
			} else {
				//nack, re-send missed fragment over tcp
				byte[] frag = formatFragment(rcv);
				cmd.fragWriter.write(frag);
				
			}
		}
		
	}
	
	
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
			
			return fragment;
		}
		
		//copy file data to fragment
		for (int dataPtr = 0; dataPtr < SicNetworkProtocol.dataPacketDataCapacity; ++dataPtr) {
			int readByte =   fragID * SicNetworkProtocol.dataPacketDataCapacity + dataPtr;
			
			fragment[SicNetworkProtocol.dataPacketHeaderSize + dataPtr] = fileData[readByte];
			
		}
		return fragment;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		

//		
//		String root = "E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/";
		String root = "C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles";
		
		Vector<File> filesChanged = new Vector<File>();
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.exe"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test1.exe"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test2.exe"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.txt"));
		
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image.jpeg"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/0.txt"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image(1).jpeg"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image(2).jpeg"));
		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.exe"));
		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.txt"));
		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image.jpeg"));
		
		
		MulticastSocket listener = new MulticastSocket (SicNetworkProtocol.port);
//		group = InetAddress.getByName("224.0.0.1");
		InetAddress group = InetAddress.getByName("230.0.0.10");
		listener.joinGroup(group); //join the multicast group
		
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		SicUploader uploader = new SicUploader(listener, group);
		uploader.initateUpload(filesChanged,root,1);

		
		
	}

}
