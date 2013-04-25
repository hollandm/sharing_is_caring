package network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Container for all of our networking components.
 *
 */
public class TransferCommander {

	public Socket sock;
	public PrintWriter writer;
	public BufferedReader reader;

	public OutputStream fragWriter;
	public InputStream fragReader;
	
	public TransferCommander(Socket socket) throws IOException {
		this.sock = socket;
		
		this.writer = new PrintWriter(new BufferedOutputStream(sock.getOutputStream()),true);
		this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		
		this.fragWriter = sock.getOutputStream();
		this.fragReader = sock.getInputStream();

	}
	
}
