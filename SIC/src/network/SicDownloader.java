package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import state.Settings;

import Main.SicComponents;


import file.FileIO;

public class SicDownloader {

	private MulticastSocket dataSocket;
	private TransferCommander cmd;
	
	private FileIO fio;
	
	byte[] fragReceived;
	DatagramPacket recvData;

	String rootPath;
	
	private byte[] fileData;
	SicComponents components;
	
	public SicDownloader(MulticastSocket listener, SicComponents components) {
		
		try {
			listener.setReceiveBufferSize(SicNetworkProtocol.dataPacketSize);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.dataSocket = listener;

		this.components = components;
		
		fragReceived = new byte[SicNetworkProtocol.dataPacketSize]; 
		recvData = new DatagramPacket(fragReceived, SicNetworkProtocol.dataPacketSize);
		
		fio = new FileIO();
		
	}
	
	public void initiateFileDownload(byte[] initiationPacket) throws IOException {
		
		rootPath = components.settings.getDirectory();
		
		//get the address of host who just sent us a message
		String addr = SicNetworkProtocol.getIP(initiationPacket);
		
		
		//attempt to connect to the server
		try {
			System.out.println("Attempting to Connect to: "+addr);
			cmd = new TransferCommander( new Socket(addr, SicNetworkProtocol.transferCmdPort));
		} catch (IOException e2) {
			System.err.println("F#@$!\n");
			e2.printStackTrace();
			System.exit(0);
		}
		
		
		//get number of files to download
		int numFiles = Integer.parseInt(cmd.reader.readLine());
		

		System.out.println("File Transfer Initiated, reciving "+numFiles+" files");

		//delete these files
		String delete;
		while (true) { 
			delete = cmd.reader.readLine();
			if (delete.equals("")) break;
			
//			System.out.println("Delete this file: " + rootPath+"/"+delete );
			File del = new File((rootPath+"/"+delete));
			fio.deleteFile(del);
		}
		
		dataSocket.setSoTimeout(250);
		//LETS DOWNLOAD SOME FILES NOW!
		for (int curFile = 0; numFiles > curFile; ++curFile) {
			downloadFile();
		}
		dataSocket.setSoTimeout(0);
		
		cmd.close();
		System.out.println("All Files Recieved");
		
	}

	
	public void downloadFile() throws IOException {

		//find out how big the file we are downloading is, then create the buffer to store it
		int fileSize = Integer.parseInt(cmd.reader.readLine());
		fileData = new byte[fileSize];
		
		//find out where to save the file
		String relativePath = cmd.reader.readLine();
		
		System.out.println("Downloading File: "+relativePath+", it is "+fileSize+" bytes big");
		
		
		//TODO: keep track of which fragments received so far
		int fragmentsExpected = fileSize / SicNetworkProtocol.dataPacketDataCapacity + 1;
		
		boolean[] fragsRecived = new boolean[fragmentsExpected+1];
		for (int i = 0; i < fragmentsExpected; ++i) fragsRecived[i] = false;
		int fragsRecivedCount = 0;
		
		
		//download all fragments
		while (!cmd.reader.ready()) {

			//Receive fragment
			try {
				dataSocket.receive(recvData);
			} catch (SocketTimeoutException e) {
				continue;
			}
			//if the valid hasn't been corrupt then save it
			if (SicNetworkProtocol.checkChecksum(fragReceived)) {
				int id = processDataFragment();
//				System.out.println(""+id);
				if (id >= 0 && id < fragsRecived.length) {
					fragsRecived[id] = true;
					++fragsRecivedCount;
				}
			}
			
		}
		//chomp the end of file packet
		cmd.reader.readLine();
		
		//request any missed or damaged packets
		
		if (fragsRecivedCount < fragmentsExpected) {
			System.out.print("Missed fragments: ");
			for (int i = 0; i < fragmentsExpected; ++i) {
				if (fragsRecived[i] == false) {
					System.out.print(i+", ");
					cmd.writer.println(i);
					
					cmd.fragReader.read(fragReceived);
					cmd.reader.readLine();
					processDataFragment();
				}
			}
			System.out.println();
		}
		
		//write data to disk
		File file = new File(rootPath+"/"+relativePath);
		if (!file.exists()) file.createNewFile();
		fio.writeFile(file, fileData);
		
		//clear file buffer
		fileData = null;

		//ack the file
		cmd.writer.println(SicNetworkProtocol.fileCompleteAck);
				
		System.out.println("\tFile Recieved!");
	}
	
	public int processDataFragment() throws IOException {
		
		
		int fragID = SicNetworkProtocol.getDataFragmentId(fragReceived);
		
		//copy data after header to fileData buffer
		for (int i = 0; i < SicNetworkProtocol.dataPacketDataCapacity; ++i) {
			
			int writeLoc = fragID*(SicNetworkProtocol.dataPacketDataCapacity) + i;
			
			if (writeLoc < fileData.length) {
				
				fileData[writeLoc] = fragReceived[SicNetworkProtocol.dataPacketHeaderSize + i];
			}
			
		}
		
		return fragID;
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
		
		SicComponents c = new SicComponents();
		c.settings = new Settings();
		c.settings.getDirectoryList().add("C:/Users/matt/Desktop/testFiles");
		
		
		SicDownloader downloader = new SicDownloader(listener,c);
		downloader.initiateFileDownload(cmdIN);
		

	}

}
