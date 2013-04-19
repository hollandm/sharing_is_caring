package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

import file.FileIO;

public class SicDownloader {

	private MulticastSocket listener;
	private FileIO fio;
	
	byte[] dataIN;
	byte[] ackArray;
	DatagramPacket recvData;
	DatagramPacket ack;

	String rootPath;
	
	private byte[] fileData;
	
	public SicDownloader(MulticastSocket listener) {
		ackArray = new byte[SicNetworkProtocol.cmdPacketSize];
		ackArray[0] = 0;
		
		
		for (int i = 1; i < SicNetworkProtocol.cmdPacketSize; ++i) ackArray[i] = 1;
		ack = new DatagramPacket(ackArray, SicNetworkProtocol.cmdPacketSize);
		try {
			ack.setAddress(InetAddress.getByName("230.0.0.10"));
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		//TODO use ip from settings
		
		try {
			listener.setReceiveBufferSize(SicNetworkProtocol.dataPacketSize);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.listener = listener;

		dataIN = new byte[SicNetworkProtocol.dataPacketSize]; 
		recvData = new DatagramPacket(dataIN, SicNetworkProtocol.dataPacketSize);
		
		fio = new FileIO();
		
	}
	
	public void initiateFileDownload(byte[] initiationPacket) throws IOException {
		
		//TODO: initiate rootPath better
		rootPath = "C:/Users/Matt/Desktop/testFiles";
		
		
		//Sends and acknowledgment after receiving the initial request
		ackArray[1] = SicNetworkProtocol.ackTransfer;
		listener.send(ack);
		
		//get number of files to download
		int numFiles = SicNetworkProtocol.getNumFiles(initiationPacket);
		

		System.out.println("File Transfer Initiated, reciving "+numFiles+" files");
		
		//download every file
		for (int curFile = 0; numFiles > curFile; ++curFile) {
			downloadFile();
		}
		
		
		
	}

	
	public void downloadFile() throws IOException {
		
		//Receive info packet
		do {
			listener.receive(recvData);
			if (dataIN[1] != SicNetworkProtocol.startFile) {
				System.err.println("Did not recieve file start packet");
			}
		} while (dataIN[1] != SicNetworkProtocol.startFile);
			
//		if (dataIN[1] != SicNetworkProtocol.startFile) {
//			System.err.println("Did not recieve file start packet");
//		}
		//Sends and acknowledgment after receiving the initial request
		ackArray[1] = SicNetworkProtocol.ackFileStart;
		listener.send(ack);
		
		
		int fileSize = SicNetworkProtocol.getFileSize(dataIN);

		char[] rPath = new char[95];
		for (int i = 0; i < 95; ++i) {
			rPath[i] = (char) dataIN[7+i];
		}
		String relativePath = String.valueOf(rPath).trim();
		System.out.println("Downloading File: "+relativePath+", it is "+fileSize+" bytes big");
		
		int fragments = fileSize / SicNetworkProtocol.dataPacketDataCapacity + 1;
		fileData = new byte[fileSize];
		
		//TODO: keep track of which fragments received so far
		
		
		//download all fragments
		for (int fragNum = 0; fragNum < fragments; ++fragNum) {
			//TODO: figure out if server is done sending fragments so you can send ack/nack
			//Receive fragment
			listener.receive(recvData);
			
			if (SicNetworkProtocol.isDataPacket(dataIN)) {
				processDataFragment();
			} else {
				//TODO: do smart stuff like ignoring nacks
				//Possibly listening for acks
			}
			
			
		}
		
		//TODO: request any missed or damaged packets
		
		/**
		 * Send nacks whenever we receive a gap in packet numbers is detected or when an unexpected end of file packet is received
		 * when a end of file packet is received and the client has received all packets then send an ack
		 * repeat acks until a next message is received from server (beginning of next file transfer)
		 */
		
		System.out.println("\tFile Recieved!");
		
		//write data to disk
		File file = new File(rootPath+"/"+relativePath);
		if (!file.exists()) file.createNewFile();
		fio.writeFile(file, fileData);
		
		fileData = null;
	}
	
	public void processDataFragment() throws IOException {
		
		
		int fragID = SicNetworkProtocol.getDataFragmentId(dataIN);
		
		//copy data after header to fileData buffer
		for (int i = 0; i < SicNetworkProtocol.dataPacketDataCapacity; ++i) {
			
			int writeLoc = fragID*(SicNetworkProtocol.dataPacketDataCapacity) + i;
			
			if (writeLoc < fileData.length) {
				//TODO: check header for segment # and place accordingly instead of just placing them in order received.
				
				byte bleh = dataIN[SicNetworkProtocol.dataPacketHeaderSize + i];
				fileData[writeLoc] = bleh;
			}
			
		}
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		MulticastSocket listener = new MulticastSocket (SicNetworkProtocol.port);
		InetAddress group = InetAddress.getByName("230.0.0.10");
		listener.joinGroup(group); //join the multicast group
		
		byte[] cmdIN = new byte[SicNetworkProtocol.cmdPacketSize];
		DatagramPacket recvCmd = new DatagramPacket(cmdIN, SicNetworkProtocol.cmdPacketSize); //DatagramPacket for receiving packets of length 10
		listener.receive(recvCmd); //fills command buffer with data receive
		
		
		SicDownloader downloader = new SicDownloader(listener);
		downloader.initiateFileDownload(cmdIN);
		

	}

}
