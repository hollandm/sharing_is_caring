package network;

import file.FileIO;
import java.io.IOException;
import java.io.EOFException;
import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
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

public class NetworkManager implements Runnable{

	@Override
	public void run() {
		
		Vector<byte[]> bigBuffer = new Vector<byte[]>();
		
		try {
			MulticastSocket listener = new MulticastSocket (9001);
			
			byte[] cmdIN = new byte[SicNetworkProtocol.cmdPacketSize]; //10
			byte[] dataIN = new byte[SicNetworkProtocol.dataPacketSize]; //100
			
			DatagramPacket recvCmd = new DatagramPacket(cmdIN, SicNetworkProtocol.cmdPacketSize); //DatagramPacket for receiving packets of length 10
			DatagramPacket recvData = new DatagramPacket(dataIN, SicNetworkProtocol.dataPacketSize);
			
			InetAddress add = InetAddress.getByName("224.0.0.1");
			listener.joinGroup(add); //join the multicast group
			
			listener.setReceiveBufferSize(SicNetworkProtocol.cmdPacketSize); //sets buffer size to 100
			
			System.out.println("Listening to traffic");
			
			while (true) {
				
				listener.receive(recvCmd); //fills command buffer with data received
				if(receiveCommand(recvCmd)) {
					listener.setReceiveBufferSize(SicNetworkProtocol.dataPacketSize);
					try {
						while(true) {
							listener.receive(recvData);
							bigBuffer.add(recvData.getData());
							
						}
					}
					catch(EOFException e) {
						bigBuffer.add(recvData.getData());
					}
					File file = new File("Desktop\testFile.txt");
					FileIO fio = new FileIO();
					for(int i = 0; i < bigBuffer.size(); i++) {
						fio.writeFile(file, bigBuffer.elementAt(i));
					}
				}
				listener.setReceiveBufferSize(SicNetworkProtocol.cmdPacketSize);
				bigBuffer.clear();
				
				
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
	
	public boolean receiveCommand(DatagramPacket packet) {
		byte[] data = packet.getData();
		if(data[1] == SicNetworkProtocol.pushRevision) {
			return true;
		}
		return false;
		
	}

	
	
}
