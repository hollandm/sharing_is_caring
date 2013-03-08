package file;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * FileIO class contains the methods to read and write data
 * to the syced directory(s).
 * 
 * TODO: Implement methods
 * 		readFile
 * 		writeFile
 * 		deleteFile
 * 
 * TODO: determine data type to read/write data from/to
 * 		currently type Object
 */


public class FileIO {

	/**
	 * readFile reads a file at the specified path and returns 
	 * the contents in ###<DATA TYPE TO BE SPECIFIED LATER>### form.
	 * 
	 * @param file is the file to be read
	 * 
	 * @throws FileNotFoundException if the file does not exist
	 */
	public Object readFile(File file) throws FileNotFoundException {
		
		if (!file.exists()) {
			throw new FileNotFoundException("File at "+file.getAbsolutePath()+" was not found");
		}
		
		
		return null;
		
	}
	
	/**
	 * writeFile writes data to a file
	 * if that file does not exist then it will be created
	 * 
	 * @param file is the file to be read
	 * @param data is the data to write to the file
	 */
	public void writeFile(File file, Object data) {
		
		
		
	}
	
	/** 
	 * deleteFile deletes the given file
	 * 
	 * @param file is the file to be deleted
	 * 
	 * @throws FileNotFoundException if the file does not exist
	 */
	public void deleteFile(File file) throws FileNotFoundException {
		
		if (!file.exists()) {
			throw new FileNotFoundException("File at "+file.getAbsolutePath()+" was not found");
		}
		
		
		
	}
	
	
}
