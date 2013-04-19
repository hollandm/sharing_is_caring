package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Vector;

import file.FileIO;

public class SicUploader {

	private MulticastSocket listener;
	private InetAddress group;
	private FileIO fio;
	
	private int fragmentsNeeded;			//number of fragments needed for a fike
	
	private byte[] fragment;				//fragment about to be sent
	private byte[] fileData;				//data read from file
	
	private DatagramPacket cmdPacket;		//multicast packet containing a command
	private byte[] cmdBuffer;				//command packet buffer
	private DatagramPacket sendData;
	
	private Vector<InetAddress> acksExpected;
	private DatagramPacket ackPacket;
	private byte[] ackBuffer;
	
	
	public SicUploader(MulticastSocket listener, InetAddress group) throws SocketException {
		this.listener = listener;
		this.listener.setSoTimeout(50);
		this.group = group;
		fio = new FileIO();
		
		ackBuffer = new byte[SicNetworkProtocol.cmdPacketSize];
		ackPacket = new DatagramPacket(ackBuffer, SicNetworkProtocol.cmdPacketSize);
		
		
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
		
		acksExpected = new Vector<InetAddress>();
		
		//send cmdBuffer packet notifying of revision update
		for (int i = 0; i < SicNetworkProtocol.cmdPacketSize; ++i) cmdBuffer[i] = 0;
		cmdBuffer[1] = SicNetworkProtocol.pushRevision;
		SicNetworkProtocol.setNumFiles(cmdBuffer, filesChanged.size());
		listener.send(cmdPacket);

		long waitTime = System.currentTimeMillis() + 1000;
		//TODO: create acknowledged packet type and assign number to it
		while(waitTime > System.currentTimeMillis()){
			try{
				listener.receive(ackPacket);
			} catch(SocketTimeoutException ste){
				continue;
			}
			acksExpected.add(ackPacket.getAddress());
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
		
		
		//send startFile packet
		cmdBuffer[1] = SicNetworkProtocol.startFile;
		SicNetworkProtocol.setFileSize(cmdBuffer, fileData.length);
		
//
//		int recivedAcks = 0;
//		Vector<InetAddress> acks = new Vector<InetAddress>();
//		System.out.println(acksExpected.size());
//		while (recivedAcks < acksExpected.size()) {
//			try {
//				listener.receive(ackPacket);
//				if (acks.contains(ackPacket.getAddress())) {
//					//dack so we can ignore it
//				} else if (acksExpected.contains(ackPacket.getAddress())){
//					//else if it is being sent from an expected host process it
//					acks.add(ackPacket.getAddress());
//					recivedAcks++;
//				}
//				System.out.println("Got one");
//			} catch (IOException e) {
//				continue;
//			}
//		}
		
		/** TODO
		 * Here we should be getting an ack from each client
		 * resend ctrPacket if we don't get a responce
		 * time out after a few failures, remove client from acksRecieved List
		 */
		
		//Setting the relative path to the file from root directory. Placing that path in cmd Packet
		String relativePath = file.getAbsolutePath().substring(rootPath.length());
		System.out.println("File sent: "+relativePath+ ", " +fileData.length + " Bytes via " + fragmentsNeeded + " fragments." );
		char[] rPath = relativePath.toCharArray();
		
		
		if (rPath.length > 95) {	//TODO: use constant value here
			System.err.println("path to long");
			System.exit(-1);
		}
		


		for (int i = 0; i < rPath.length; i++) {
			cmdBuffer[7+i] = (byte) rPath[i];
		}
		for (int i = rPath.length+7; i < cmdBuffer.length;++i) {
			cmdBuffer[i] = 0;
		}	
		listener.send(cmdPacket);
		
		//send file in fragments
		for (int fragID = 0; fragmentsNeeded > fragID; ++fragID) {
			
			sendFragment(fragID);
		}
		System.out.println("\tFinished sending "+relativePath);
		
		//TODO: waiting for reliability acks/nacks should remove the need for this delay
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendFragment(int fragID) throws IOException {
		
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
			
			listener.send(sendData);
			return;
		}
		
		//copy file data to fragment
		for (int dataPtr = 0; dataPtr < SicNetworkProtocol.dataPacketDataCapacity; ++dataPtr) {
			int readByte =   fragID * SicNetworkProtocol.dataPacketDataCapacity + dataPtr;
			
			fragment[SicNetworkProtocol.dataPacketHeaderSize + dataPtr] = fileData[readByte];
			
		}
		
		//send the fragment
		listener.send(sendData);
		//TODO: Calibrate wait time
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		

		
		String root = "E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/";
		
		Vector<File> filesChanged = new Vector<File>();
		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.exe"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test1.exe"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test2.exe"));
		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/test.txt"));
		
		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image.jpeg"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/0.txt"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image(1).jpeg"));
//		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/testFiles/image(2).jpeg"));
//		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/test.exe"));
		
		
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
