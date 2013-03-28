package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

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

public class NetworkManager implements Runnable{

	@Override
	public void run() {

		try {
			MulticastSocket listener = new MulticastSocket (9001);
			
			byte[] dataIN = new byte[SicNetworkProtocol.cmdPacketSize];
			
			DatagramPacket recv = new DatagramPacket(dataIN, SicNetworkProtocol.cmdPacketSize);
			InetAddress add = InetAddress.getByName("224.0.0.1");
			listener.joinGroup(add);
			
			listener.setReceiveBufferSize(SicNetworkProtocol.dataPacketSize);
			
			System.out.println("Listening to traffic");
			
			while (true) {
				
				listener.receive(recv);
				parseData(dataIN);
				
			}
			
//			listener.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This method is called when a file transfer has been initiated.
	 * It will process file fragments and then save them to the reassembled file to the disk
	 * If a packet is lost it will send a NACK notifying the sender that it was not received.
	 * 
	 * 
	 */
	public void parseData(byte[] cmd) {
		
		
		
	}

	
	
}
