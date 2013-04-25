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

public class Friend implements Serializable {

	
	private static final long serialVersionUID = -7387424258418995938L;
	
	private String _name;
	private InetAddress _ip;
	
	public Friend(String name, InetAddress ip){
		_name = name;
		_ip = ip;
	}
	
	/**
	 * Changes the name of the friend
	 * @param new_name
	 */
	public void set_name(String new_name){
		_name = new_name;
	}
	
	/**
	 * @return name of the friend
	 */
	public String get_name(){
		return _name;
	}
	
	/**
	 * Modifies the IP address of a friend
	 * @param new_ip
	 */
	public void set_ip(InetAddress new_ip){
		_ip = new_ip;
	}
	
	/**
	 * @return returns the IP address of a friend.
	 */
	public InetAddress get_ip(){
		return _ip;
	}
	
	
}
