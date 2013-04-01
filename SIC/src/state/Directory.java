package state;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * 
 * TODO: I HAVE AN IDEA!
 * 		At the root of the shared directory we have a text file containing a list 
 * 		of all files paired with a identifier number. We can let our program sync 
 * 		the file like any other ordinary file but that way we don't have to worry
 * 		about sending long strings in a small command packet
 *  
 */

//TODO: Have log be serialized to a file in the root directory

public class Directory implements Serializable{

	private static final long serialVersionUID = 312784493590177413L;

	private String _id;
	

	private String _netName;
	private String _path;
	
	private Log _log;
	
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_path() {
		return _path;
	}

	public void set_path(String _path) {
		this._path = _path;
	}

	public Log get_log() {
		return _log;
	}

	public String get_netName() {
		return _netName;
	}

	public void set_netName(String _netName) {
		this._netName = _netName;
	}

}
