package network;

/**
 * SicNetworkProtocol is a simple class to store
 * information about the protocol in a single
 * location in order to simplify modifications
 * down the road
 * 
 * TODO: dear god please change the protocol strings
 * 
 * TODO: flesh out protocol with the other things we said we would do
 */

public final class SicNetworkProtocol {

	public static final int port = 7532;
	
	public static final int cmdPacketSize = 10;		//size of command size 
	public static final int dataPacketSize = 100;	//maximum size of a data packet
	
	
	/**
	 * first byte in data represents whether or not the packet is a command or a piece of data
	 * if 0 then packet is a cmd, otherwise it is data
	 */
	public static final byte cmdMarker = 0;
	// if packet is commandpacket, second byte is actual command
	public static final byte dataMarker = 1;
	
	/*
	 * commands
	 * pushRevision:	sender is indicating that receiver should be prepared
	 * 					to receive an update
	 * requestRevision:	sender is indicating that receiver should send out
	 * 					an update
	 */
	public static final byte pushRevision = 1;
	public static final byte requestRevision = 2;
	
	
	
	/**
	 * parseRevisionResponse takes data and returns an integer representing the revision
	 * 
	 * @param response the data received over the network
	 * @return
	 */
	public static int parseRevisionResponse(byte[] response) {
		return 0;
		
	}

	
}
