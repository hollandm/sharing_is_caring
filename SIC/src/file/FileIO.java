package file;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileIO class contains the methods to read and write data
 * to the synced directory(s).
 * 
 */
public class FileIO {

	/**
	 * readFile reads a file at the specified path and returns 
	 * the contents in byte array form.
	 * 
	 * @param file is the file to be read
	 * 
	 * @throws FileNotFoundException if the file does not exist
	 */
	public byte[] readFile(File file) throws FileNotFoundException {
		
		//Checks to see if the file exists.
		if (!file.exists()) {
			throw new FileNotFoundException("File at "+file.getAbsolutePath()+" was not found");
		}
		
		byte[] data = null;
		
		DataInputStream in;
		try {
			in = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getAbsolutePath())));
			//converts the file into a byte array
			data = new byte[in.available()];
			in.readFully(data);
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return data;
		
	}
	
	/**
	 * writeFile writes data to a file
	 * if that file does not exist then it will be created
	 * 
	 * @param file is the file to be read
	 * @param data is the data to write to the file
	 * @throws IOException 
	 */
	public void writeFile(File file, byte[] data) throws IOException {
		
		file.getAbsoluteFile().getParentFile().mkdirs();
		
		//If file hasn't been created yet, create it.
		if (!file.exists()) {
			file.createNewFile();
		}
		
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new FileOutputStream(file));
			//Writes file
			for (byte i : data) {
				out.writeByte(i);
			}
		} catch (IOException e) {
			
		}
	}
	
	/** 
	 * deleteFile deletes the given file
	 * 
	 * @param file is the file to be deleted
	 * 
	 */
	public void deleteFile(File file) throws FileNotFoundException {	
		if (file.exists()) {
			file.delete();
		}
	}

}
