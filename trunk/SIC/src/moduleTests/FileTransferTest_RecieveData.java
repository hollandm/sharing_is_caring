package moduleTests;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import network.NetworkManager;

public class FileTransferTest_RecieveData {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Vector<File> filesChanged = new Vector<File>();
		filesChanged.add(new File("E:/Dropbox/Sophmor Spring Semester/CS 445/test.exe"));
//		filesChanged.add(new File("C:/Users/Matthew.Matt-Desktop/Dropbox/Sophmor Spring Semester/CS 445/test.exe"));
		
		NetworkManager net = new NetworkManager();
		net.initalizeConnection();
		
		net.listen();
		
		net.terminateConnection();
		
	}

}
