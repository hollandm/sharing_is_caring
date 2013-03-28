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
	 * first bit in data represents weather or not the packet is a command or a piece of data
	 * if 0 then packet is a cmd, otherwiser it is data
	 */
	public static final int cmdMarker = 0;
	public static final int dataMarker = 1;
	
	public static final int pushRevision = 1;
	public static final int requestRevision = 2;
	
	
	
	/**
	 * parseRevisionResponce takes data and returns an integer representing the revision
	 * 
	 * @param responce the data received over the network
	 * @return
	 */
	public static int parseRevisionResponce(byte[] responce) {
		return 0;
		
	}

	
}
