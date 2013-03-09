import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class FileReadWrite {

	public static void main(String[] args) {

		String PathRead = "E:/Dropbox/Sophmor Spring Semester/CS 445/Workspace/test.exe";
		String PathWrite = "E:/Dropbox/Sophmor Spring Semester/CS 445/Workspace/test2.exe";

		
		DataInputStream in = null;
		DataOutputStream out = null;
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(PathRead)));
//			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(PathWrite)));
			out = new DataOutputStream(new FileOutputStream(PathWrite));

			byte[] data = new byte[in.available()];
			in.readFully(data);
			
			for (byte i : data) {
//				System.out.println(i);
				out.writeByte(i);
			}
			
			
//			out.write(data);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}



		
	




	}

}
