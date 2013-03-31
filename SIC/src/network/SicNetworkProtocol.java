package network;

import java.net.InetAddress;

/**
 * SicNetworkProtocol is a simple class to store
 * information about the protocol in a single
 * location in order to simplify modifications
 * down the road
 * 
 * TODO: flesh out protocol with the other things we said we would do
 */

public final class SicNetworkProtocol {

	public static final int port = 7532;
	
	public static final int cmdPacketSize = 100;		//size of command size 
	
	
	public static final int dataPacketHeaderSize = 13;	//Data packet header size
		//TODO: propose we cut all but fragment # from data packet header
		//first byte says its a data packet
		//next 4 bytes go to revision #
		//next 4 bytes goes to file #
		//next 4 bytes goes to fragment #
	public static final int dataPacketSize = 1000;	//maximum size of a data packet
	public static final int dataPacketDataCapacity = dataPacketSize - dataPacketHeaderSize;	//how much left over space is there with header

	
	/**
	 * first byte in data represents whether or not the packet is a command or a piece of data
	 * if 0 then packet is a cmd, otherwise it is data
	 */
	public static final byte cmdMarker = 0;
	// if packet is a command packet, second byte is actual command
	public static final byte dataMarker = 1;
	
	/*
	 * commands
	 * pushRevision:	sender is indicating that receiver should be prepared
	 * 					to receive an update
	 * requestRevision:	sender is indicating that receiver should send out
	 * 					an update
	 */
	public static final byte pushRevision = 1;		//[4 bytes: revision #][4 bytes: # of files]
		public static int getNumFiles(byte[] cmdPacket) {
			return getIntFromByteArray(cmdPacket,6);
		}
		public static void setNumFiles(byte[] cmdPacket, int numFiles) {
			placeIntInByteArray(cmdPacket, 6, numFiles);
		}
	
	public static final byte requestRevision = 2;	//[4 bytes: requested revision #]
	
	public static final byte startFile = 50;		//[4 bytes: size of file in bytes][95 bytes: file path]
		public static int getFileSize(byte[] cmdPacket) {
			return getIntFromByteArray(cmdPacket, 2);
		}
		public static void setFileSize(byte[] cmdPacket, int size) {
			placeIntInByteArray(cmdPacket, 2, size);
		}
	
	
	public static final byte requestFragment = 70;	//[4 bytes: file #][4 bytes: fragment #]
	
	
	
	/**
	 * parseRevisionResponse takes data and returns an integer representing the revision
	 * 
	 * @param response the data received over the network
	 * @return
	 */
	public static int parseRevisionResponse(byte[] response) {
		return 0;
		
	}

	
	
	public static void setDataFragmentId(byte[] dataPacket, int id) {
		placeIntInByteArray(dataPacket,9,id);
	}
	
	public static int getDataFragmentId(byte[] dataPacket) {
		return getIntFromByteArray(dataPacket, 9);
	}
	
	private static int getIntFromByteArray(byte[] array, int location) {
		//TODO: optimize this with a shift instead of multiplication
		return 	  array[location  ]*256*256*256 
				+ array[location+1]*256*256 
				+ array[location+2]*256
				+ array[location+3];
		
	}
	
	private static void placeIntInByteArray(byte[] array, int location, int placeMe) {
		//TODO: optimize this
		array[location+3] = (byte)(placeMe % 256);
		placeMe /= 256;
		array[location+2] = (byte)(placeMe % 256);
		placeMe /= 256;
		array[location+1] = (byte)(placeMe % 256);
		placeMe /= 256;
		array[location]   = (byte)(placeMe);
	}
	
}
