package network;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class transferCommander {

	public Socket sock;
	public PrintWriter writer;
	public BufferedReader reader;
	
	public transferCommander(Socket socket) throws IOException {
		this.sock = socket;
		
		this.writer = new PrintWriter(new BufferedOutputStream(sock.getOutputStream()),true);
		this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
	}
	
}
