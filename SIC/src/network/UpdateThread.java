package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * UpdateThread is used when a client requests an revision update.
 * This will allow us to send updates to multiple clients at once
 * without interfering with the normal operations of the program.
 *
 *	TODO: explore multicast as an alternative (Though I have my doubts that it will be suitable)
 *	TODO: investigate pros/cons of fragmenting data before sending
 *	TODO: Actually test this module
 */

public class UpdateThread implements Runnable{

	String addr;
	int port = SicNetworkProtocol.port;
	final byte[] sendData;
	
	public UpdateThread(String addr, byte[] data) {
		this.addr = addr;
		sendData = data;
	}
	
	@Override
	public void run() {
		
		Socket sock = new Socket();
		InetSocketAddress client = new InetSocketAddress(addr, port);
		
		try {
			//connect to recipient
			sock.connect(client);
			
			//Initialize data stream
			DataOutputStream oStream = new DataOutputStream(sock.getOutputStream());
			
			//send data
			oStream.write(sendData);
			
			//clean up
			oStream.close();
			sock.close();
			
		} catch (IOException e) {
			System.err.println("Failed to connect to recipient, terminating thread");
//			e.printStackTrace();
			return;
		}
		
		
	}

	
	public static void main(String[] args) {
		
		byte[] test = {(byte) 255,(byte) 233,(byte) 211,1};
		
		UpdateThread a = new UpdateThread("127.0.0.1",test);
//		UpdateThread b = new UpdateThread("127.0.0.1",test);
//		UpdateThread c = new UpdateThread("127.0.0.1",test);
		
		
		new Thread(a).start();
//		new Thread(b).start();
//		new Thread(c).start();
		
	}
	
	
}


