package state;

/**
 * Data related to "friends"
 * 
 * includes: 
 * 	name which is just an identifier for human input
 * 	IP address to send data to
 * 	a list of directories which are being shared with these friends
 */


import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;

public class Friend implements Serializable {

	
	private static final long serialVersionUID = -7387424258418995938L;
	
	private String _name;
	private InetAddress _ip;
	
	private ArrayList<Directory> _sharedDirs;
	
	public Friend(String name, InetAddress ip){
		_name = name;
		_ip = ip;
	}
	
	public void set_name(String new_name){
		_name = new_name;
	}
	
	public String get_name(){
		return _name;
	}
	
	public void set_ip(InetAddress new_ip){
		_ip = new_ip;
	}
	
	public InetAddress get_ip(){
		return _ip;
	}
	
	
}
