package network;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import file.FileIO;

/**
 * UpdateThread is used when a client requests an revision update.
 * 
 *
 *	TODO: investigate pros/cons of fragmenting data before sending
 *	TODO: Actually test this module
 */

public class UpdateThread implements Runnable{

	int port = SicNetworkProtocol.port;
	final byte[] sendData;
	
	public UpdateThread(String addr, byte[] data) {
		sendData = data;
		
		
	}
	
	@Override
	public void run() {
		
		
		
	}

	
	public static void main(String[] args) throws IOException {
		
		
		FileIO giveMeData = new FileIO();

		File file = new File("E:/Dropbox/Sophmor Spring Semester/CS 445/test.exe");
		
		byte[] data = giveMeData.readFile(file);
		
		byte[] send = new byte[SicNetworkProtocol.dataPacketSize];
		
		for (int i = 0; i < SicNetworkProtocol.dataPacketHeaderSize; i++) {
			send[i] = 0;
		}
		
		
		MulticastSocket sock = new MulticastSocket(SicNetworkProtocol.port);
		
		InetAddress addr = InetAddress.getByName("224.0.0.1");
//		InetSocketAddress addr = new InetSocketAddress("10.12.18.26", SicNetworkProtocol.port);
//		sock.connect(addr);
		DatagramPacket datagram = new DatagramPacket(send, SicNetworkProtocol.dataPacketSize,addr,SicNetworkProtocol.port);
		
		int segmentCount = 0;
		int sentAmount = 0;
		int fileSize = data.length;
		
		
		while (fileSize > sentAmount) {

			int remainingData = SicNetworkProtocol.dataPacketSize-SicNetworkProtocol.dataPacketHeaderSize;
			for (int i = 0; i < remainingData/8; ++i) {
				if (fileSize < sentAmount+i) {
					byte a = data[sentAmount+i];
					send[SicNetworkProtocol.dataPacketHeaderSize+i] = a;
				} else {
					send[SicNetworkProtocol.dataPacketHeaderSize+i] = 0;
				}
			}
			send[SicNetworkProtocol.dataPacketHeaderSize-1] = (byte) segmentCount;
			
			sentAmount += remainingData;
			
			segmentCount++;
			sock.send(datagram);
			System.out.println("Sent segment: "+segmentCount);
			
			
			
		}
	}
	
	
}


