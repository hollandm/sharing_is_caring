package state;

/**
 * Data related to "friends"
 * 
 * includes name which is just and identifier for human input
 * and an IP address to send data to
 */


import java.io.Serializable;
import java.net.InetAddress;

public class Freind implements Serializable {

	
	private static final long serialVersionUID = -7387424258418995938L;
	
	String _name;
	InetAddress _ip;
	
	
}
