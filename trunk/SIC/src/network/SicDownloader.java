package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


import file.FileIO;

public class SicDownloader {

	private MulticastSocket dataSocket;
	
	
	private TransferCommander cmd;
	
	
	private FileIO fio;
	
	byte[] fragReceived;
	DatagramPacket recvData;

	String rootPath;
	
	private byte[] fileData;
	
	public SicDownloader(MulticastSocket listener) {
		
		try {
			listener.setReceiveBufferSize(SicNetworkProtocol.dataPacketSize);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.dataSocket = listener;

		fragReceived = new byte[SicNetworkProtocol.dataPacketSize]; 
		recvData = new DatagramPacket(fragReceived, SicNetworkProtocol.dataPacketSize);
		
		fio = new FileIO();
		
	}
	
	public void initiateFileDownload(byte[] initiationPacket) throws IOException {
		
		//TODO: initiate rootPath better
		rootPath = "C:/Users/Matt/Desktop/testFiles";
//		rootPath = "C:/Users/Matthew.Matt-Desktop/Desktop/testFiles";
		
		//get the address of host who just sent us a message
		String addr = SicNetworkProtocol.getIP(initiationPacket);
		
		
		//attempt to connect to the server
		try {
			cmd = new TransferCommander( new Socket(addr, SicNetworkProtocol.transferCmdPort));
		} catch (IOException e2) {
			System.err.println("F#@$!\n");
			e2.printStackTrace();
			System.exit(0);
		}
		
		
		//get number of files to download
		int numFiles = Integer.parseInt(cmd.reader.readLine());
		

		System.out.println("File Transfer Initiated, reciving "+numFiles+" files");

		dataSocket.setSoTimeout(250);
		//LETS DOWNLOAD SOME FILES NOW!
		for (int curFile = 0; curFile < numFiles; ++curFile) {
			downloadFile();
		}
		dataSocket.setSoTimeout(0);
		
		
		
	}

	
	public void downloadFile() throws IOException {

		//find out how big the file we are downloading is, then create the buffer to store it
		int fileSize = Integer.parseInt(cmd.reader.readLine());
		fileData = new byte[fileSize];
		
		//find out where to save the file
		String relativePath = cmd.reader.readLine();
		
		System.out.println("Downloading File: "+relativePath+", it is "+fileSize+" bytes big");
		
		
		//TODO: keep track of which fragments received so far
		int fragmentsExpected = (int) Math.ceil(fileSize / SicNetworkProtocol.dataPacketDataCapacity);
		
		boolean[] fragsRecived = new boolean[fragmentsExpected];
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
				fragsRecived[id] = true;
				++fragsRecivedCount;
			}
			
		}
		//chomp the end of file packet
		cmd.reader.readLine();
		
		//request any missed or damaged packets
		
		if (fragsRecivedCount < fragmentsExpected) {
			for (int i = 0; i < fragmentsExpected; ++i) {
				if (fragsRecived[i] == false) {
					System.out.println("Missed fragment " + i);
					cmd.writer.println(i);
					
					cmd.fragReader.read(fragReceived);
					cmd.reader.readLine();
					processDataFragment();
				}
			}
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
		
//		System.out.println(fragID);
		
		//copy data after header to fileData buffer
		for (int i = 0; i < SicNetworkProtocol.dataPacketDataCapacity; ++i) {
			
			int writeLoc = fragID*(SicNetworkProtocol.dataPacketDataCapacity) + i;
			
			if (writeLoc < fileData.length) {
				//TODO: check header for segment # and place accordingly instead of just placing them in order received.
				
				byte bleh = fragReceived[SicNetworkProtocol.dataPacketHeaderSize + i];
				fileData[writeLoc] = bleh;
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
		
		
		SicDownloader downloader = new SicDownloader(listener);
		downloader.initiateFileDownload(cmdIN);
		

	}

}
