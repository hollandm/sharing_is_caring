package network;

import file.FileIO;
import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Vector;

/**
 * Scans inbound network traffic for SIC data
 * 
 * responds to queries as appropriate
 * 
 * accepts revision updates:
 * 		1. halts local updates (because we will be triggering them when we update)
 * 		2. begins download
 * 		3. resumes local updates when download finishes
 *
 */

public class NetworkManager {

	private MulticastSocket listener;
	private InetAddress group;
	
	public SicUploader uploader;
	SicDownloader downloader;
	
	public NetworkManager() {
		downloader = new SicDownloader(listener);
	}
	
	
	public void listen() {
		try {

			byte[] cmdIN = new byte[SicNetworkProtocol.cmdPacketSize];

			DatagramPacket recvCmd = new DatagramPacket(cmdIN, SicNetworkProtocol.cmdPacketSize); //DatagramPacket for receiving packets of length 10

			System.out.println("Listening to traffic");

			while (true) {

				listener.receive(recvCmd); //fills command buffer with data received
				if(receiveCommand(recvCmd)) {
					System.out.println("File Transfer Initiated");
					downloader.initiateFileDownload(cmdIN);
				}


			}


		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void initalizeConnection() throws IOException {
		listener = new MulticastSocket (SicNetworkProtocol.port);
//		group = InetAddress.getByName("224.0.0.1");
		group = InetAddress.getByName("230.0.0.10");
		listener.joinGroup(group); //join the multicast group

		listener.setReceiveBufferSize(SicNetworkProtocol.cmdPacketSize); //sets buffer size to 100

	}
	
	public void terminateConnection() throws IOException {
		listener.leaveGroup(group);
		listener.close();
	}

	

	public boolean receiveCommand(DatagramPacket packet) {
		byte[] data = packet.getData();
		if(data[1] == SicNetworkProtocol.pushRevision) {
			return true;
		}
		return false;

	}


	
	

}
