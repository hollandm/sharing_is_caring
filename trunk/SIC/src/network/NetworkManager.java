package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Main.SicComponents;

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

//	private SICComponents components;
	
	private MulticastSocket listener;
	private InetAddress group;
	
	public SicUploader uploader;
	public SicDownloader downloader;

	public SicComponents components;
	
	public NetworkManager(SicComponents components) {
		this.components = components;
		
//		uploader = new SicUploader(listener, group);
//		downloader = new SicDownloader(listener);
	}
	
	
	public void listen() {
		try {

			byte[] cmdIN = new byte[SicNetworkProtocol.cmdPacketSize];

			DatagramPacket recvCmd = new DatagramPacket(cmdIN, SicNetworkProtocol.cmdPacketSize); //DatagramPacket for receiving packets of length 10

			System.out.println("Listening to traffic");

			while (true) {

				listener.receive(recvCmd); //fills command buffer with data received
				if(SicNetworkProtocol.getCmdType(cmdIN) == SicNetworkProtocol.pushRevision) {
					System.out.println("File Transfer Initiated");
					downloader.initiateFileDownload(cmdIN);
				}
				
				if(SicNetworkProtocol.getCmdType(cmdIN) == SicNetworkProtocol.pullRevision) {

					//TODO: upload revisions since 
					
				}
				
				if(SicNetworkProtocol.getCmdType(cmdIN) == SicNetworkProtocol.requestRevisionNumber) {

					//TODO: respond with most recent revision number
					
				}

				//TODO: make a way to retrieve request Revision Number response
				
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void initalizeConnection() throws IOException {
		listener = new MulticastSocket (SicNetworkProtocol.port);
//		group = InetAddress.getByName("224.0.0.1");
//		group = InetAddress.getByName("230.0.0.10");
		listener.joinGroup(components.settings.get_multicastGroup()); //join the multicast group

		listener.setReceiveBufferSize(SicNetworkProtocol.cmdPacketSize); //sets buffer size to 100

	}
	
	public void terminateConnection() throws IOException {
		listener.leaveGroup(components.settings.get_multicastGroup());
		listener.close();
	}
	
	//TODO: call this when changed in gui
	public void changeGroup() {
		try {

			listener.leaveGroup(group);
			listener.joinGroup(components.settings.get_multicastGroup());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	

}
