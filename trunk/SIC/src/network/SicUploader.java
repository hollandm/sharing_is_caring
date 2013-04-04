package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
	
	
	
	
	public SicUploader(MulticastSocket listener, InetAddress group) {
		this.listener = listener;
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
	
	
	
	
	public void initateUpload(Vector<File> filesChanged) throws IOException {

		//send cmdBuffer packet notifying of revision update
		for (int i = 0; i < SicNetworkProtocol.cmdPacketSize; ++i) cmdBuffer[i] = 0;
		cmdBuffer[1] = 1;
		SicNetworkProtocol.setNumFiles(cmdBuffer, filesChanged.size());
		listener.send(cmdPacket);

		
		//update fragment header with current revision number
//		byte[] revisionNum = ByteBuffer.allocate(4).putInt(0).array();
//		for (int i = 0; i < 4; ++i) {
//			fragment[i+1] = revisionNum[i];
//		}
		//TODO: implement revison numbers, currently always recvision 0
		
		//loop through files
		int fileCounter = 0;
		for (File file : filesChanged) {
			
			sendFile(file, fileCounter);
			fileCounter++;
			
		}
		

	}
	
	
	
	
	public void sendFile(File file, int fileCounter) throws IOException {
		
		//read file to buffer
		fileData = fio.readFile(file);
		
		
		//calculate number of fragments required for file
		fragmentsNeeded = (int) Math.ceil((double)(fileData.length)/((double)SicNetworkProtocol.dataPacketDataCapacity));
		
		System.out.println("Sedning " + fileData.length + " Bytes via " + fragmentsNeeded +"");
		
		//send startFile packet
		cmdBuffer[1] = 50;
		SicNetworkProtocol.setFileSize(cmdBuffer, fileData.length);
		//TODO: set file path
		listener.send(cmdPacket);
		
		//set file id number
//		byte[]  fileNum = ByteBuffer.allocate(4).putInt(fileCounter).array();
//		for (int i = 0; i < 4; ++i) {
//			fragment[i+5] = fileNum[i];
//		}
		
		//send file in fragments
		for (int fragID = 0; fragmentsNeeded > fragID; ++fragID) {
			
			sendFragment(fragID);
			
		}
	}
	
	public void sendFragment(int fragID) throws IOException {
		
		//set fragment id number
//		byte[]  fragmentNum = ByteBuffer.allocate(4).putInt(fragID).array();
//		for (int i = 0; i < 4; ++i) {
//			fragment[i+9] = fragmentNum[i];
//		}
		
		//if it is the last fragment to be sent then handle it specially
		if (fragID+1 == fragmentsNeeded) {
			System.out.println("last fragment = " + fragmentsNeeded);
			
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
			Thread.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Vector<File> filesChanged = new Vector<File>();
		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/test.exe"));
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
		uploader.initateUpload(filesChanged);

		
		
	}

}
