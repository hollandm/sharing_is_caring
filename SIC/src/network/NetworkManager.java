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
			
			byte[] dataIN = new byte[7];
			
			DatagramPacket recv = new DatagramPacket(dataIN, 7);
			InetAddress add = InetAddress.getByName("224.0.0.1");
			listener.joinGroup(add);
			
			listener.setReceiveBufferSize(7);
			
			System.out.println("Listening to traffic");
			
			while (true) {
				listener.receive(recv);
				
				
			}
			
//			listener.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	
	
}
