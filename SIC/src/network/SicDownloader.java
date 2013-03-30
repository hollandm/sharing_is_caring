package network;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Vector;

import file.FileIO;

public class SicDownloader {

	private MulticastSocket listener;
	private FileIO fio;
	
	byte[] dataIN;
	DatagramPacket recvData;
	
	private byte[] fileData;
	
	public SicDownloader(MulticastSocket listener) {
		
		
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
	
	public void initiateFileDownload() throws IOException {

		
		
	}

	
	public void downloadFile() throws IOException {
		
		//Receive info packet
		listener.receive(recvData);
//		int fragments = 490;
		int fragments = 1;
		//TODO: set fileData buffer size based off size sent in info packet to fix bug
		//currently any remaining space in the data buffer will be added onto the sent files :(
		fileData = new byte[(fragments)*SicNetworkProtocol.dataPacketDataCapacity];
		
		//download all fragments
		for (int fragID = 0; fragID < fragments; ++fragID) {
			downloadFragment(fragID);
		}
		
		//TODO: request any missed or damaged packets
		
		//write data to disk
		File file = new File("C:/Users/Matthew.Matt-Desktop/Desktop/testFile.txt");
		if (!file.exists()) file.createNewFile();
		fio.writeFile(file, fileData);
	}
	
	public void downloadFragment(int fragID) throws IOException {
		
		//Receive file
		listener.receive(recvData);
		
		//copy data after header to fileData buffer
		for (int i = 0; i < SicNetworkProtocol.dataPacketDataCapacity; ++i) {
			byte bleh = dataIN[SicNetworkProtocol.dataPacketHeaderSize + i];
			fileData[fragID*(SicNetworkProtocol.dataPacketDataCapacity) + i] = bleh;
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
		
		SicDownloader downloader = new SicDownloader(listener);
		downloader.downloadFile();
		

	}

}
