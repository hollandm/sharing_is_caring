package network;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

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

	
	public static void main(String[] args) {
		
	}
	
	
}


