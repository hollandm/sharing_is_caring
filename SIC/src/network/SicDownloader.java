package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import file.FileIO;

public class SicDownloader {

	private MulticastSocket listener;
	private FileIO fio;
	
	byte[] dataIN;
	byte[] ackArray;
	DatagramPacket recvData;
	DatagramPacket ack;
	
	private byte[] fileData;
	
	public SicDownloader(MulticastSocket listener) {
		ackArray[0] = 0;
		for (int i = 1; i < SicNetworkProtocol.cmdPacketSize; ++i) ackArray[i] = 1;
		
		
		try {
			listener.setReceiveBufferSize(SicNetworkProtocol.dataPacketSize);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.listener = listener;

		dataIN = new byte[SicNetworkProtocol.dataPacketSize]; 
		recvData = new DatagramPacket(dataIN, SicNetworkProtocol.dataPacketSize);
		ack = new DatagramPacket(ackArray, SicNetworkProtocol.dataPacketSize);
		
		fio = new FileIO();
		
	}
	
	public void initiateFileDownload(byte[] initiationPacket) throws IOException {
		
		System.out.println("File Transfer Initiated");
		
		//Sends and acknowledgement after receiving the initial request.
		listener.send(ack);
		
		//get number of files to download
		int numFiles = SicNetworkProtocol.getNumFiles(initiationPacket);
		
		//download every file
		for (int curFile = 0; numFiles > curFile; ++curFile) {
			downloadFile();
		}
		
		
		
	}

	
	public void downloadFile() throws IOException {
		
		//Receive info packet
		listener.receive(recvData);
		int fileSize = SicNetworkProtocol.getFileSize(dataIN);
		System.out.println("File Size: "+fileSize);
		//TODO: I think fileSize may be one byte larger than it needs to be, investigate this
		
		int fragments = fileSize / SicNetworkProtocol.dataPacketDataCapacity + 1;
		fileData = new byte[fileSize];
		
		//TODO: keep track of which fragments received so far
		
		
		//download all fragments
		for (int fragID = 0; fragID < fragments; ++fragID) {
			downloadFragment(fragID);
		}
		
		//TODO: request any missed or damaged packets
		
		System.out.print("File Recieved!");
		
		//write data to disk
//		File file = new File("C:/Users/Matthew.Matt-Desktop/Desktop/testFile.txt");
//		File file = new File("C:/Users/Matt/Desktop/testFile.txt");
//		File file = new File("Macintosh HD/Users/VietPhan/Desktop/testFile.txt");
		File file = new File("/Users/VietPhan/Desktop/testFile.txt");
		if (!file.exists()) file.createNewFile();
		fio.writeFile(file, fileData);
		
		fileData = null;
	}
	
	public void downloadFragment(int fragID) throws IOException {
		
		//Receive file
		listener.receive(recvData);
		
		//copy data after header to fileData buffer
		for (int i = 0; i < SicNetworkProtocol.dataPacketDataCapacity; ++i) {
			
			if (i < fileData.length) {
				//TODO: check header for segment # and place accordingly instead of just placing them in order received.
				
				byte bleh = dataIN[SicNetworkProtocol.dataPacketHeaderSize + i];
				fileData[fragID*(SicNetworkProtocol.dataPacketDataCapacity) + i] = bleh;
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
//		downloader.downloadFile();
		

	}

}
