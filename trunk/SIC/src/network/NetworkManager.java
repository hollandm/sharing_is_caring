package network;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Vector;

import state.Settings;

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

	/**
	 * This is the amount of time to wait after no files have been modified
	 * before we send off a revision update
	 */
	public final int fileWaitTime = 3000;
	
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
	
	//TODO If autoUpdate is disabled then don't do this!!
	
	public void begin() {
		try {

			byte[] cmdIN = new byte[SicNetworkProtocol.cmdPacketSize];

			DatagramPacket recvCmd = new DatagramPacket(cmdIN, SicNetworkProtocol.cmdPacketSize); //DatagramPacket for receiving packets of length 10
			System.out.println("Listening to traffic");

			while (true) {

				
				if (components.settings.is_auto_updates_enabled()) {
					
					if (components.dirMonitor.getLastModTime() > System.currentTimeMillis() + fileWaitTime) {
						
						Vector<File> filesRemoved = components.dirMonitor.getFilesRemoved();
						Vector<File> filesChanged = components.dirMonitor.getFilesChanged();
						if (!filesRemoved.isEmpty() || !filesChanged.isEmpty()) {
							
							System.out.println("Local Changes Detected, sending files since revision: ");

							uploader.initateUpload(components.dirMonitor.getFilesChanged(), 
									components.dirMonitor.getFilesRemoved(), 
									components.settings.getDirectory(),
									components.settings.getRevision());
							
							
							components.dirMonitor.clearVectors();
							
						}
					}
					
					
				}
				
				listener.receive(recvCmd); //fills command buffer with data received
				if(SicNetworkProtocol.getCmdType(cmdIN) == SicNetworkProtocol.pushRevision) {
					System.out.println("File Transfer Initiated");
					downloader.initiateFileDownload(cmdIN);
					
					//our buffer was just filled with changed caused by downloading files
					//ignore those
					components.dirMonitor.clearVectors();
				}
				
				if(SicNetworkProtocol.getCmdType(cmdIN) == SicNetworkProtocol.pullRevision) {
					System.out.println("File Transfer Requested, sending files since revision: ");

					uploader.initateUpload(components.dirMonitor.getFilesChanged(), 
							components.dirMonitor.getFilesRemoved(), components.settings.getDirectory(),
							components.settings.getRevision());
					
					components.dirMonitor.clearVectors();
					
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
