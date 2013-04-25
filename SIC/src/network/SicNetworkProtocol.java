package network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

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
	public static final int transferCmdPort = 7533;


	public static final int cmdPacketSize = 100;		//size of command size 

	public static final int dataPacketHeaderSize = 13;	//Data packet header size
	public static final int DATA_PACK_TAG = 0;			//first byte says its a data packet
	public static final int FILE_NUM_TAG = 1;//to 4		//next 4 bytes goes to file #
	public static final int FRAG_NUM_TAG = 5;//to 8		//next 4 bytes goes to fragment #
	public static final int CHKSUM_TAG = 9;//to 12		//next 4 bytes contain the checksum

	public static final int dataPacketSize = 1000;	//maximum size of a data packet
	public static final int dataPacketDataCapacity = dataPacketSize - dataPacketHeaderSize;	//how much left over space is there with header


	/**
	 * first byte in data represents whether or not the packet is a command or a piece of data
	 * if 0 then packet is a cmd, otherwise it is data
	 */
	public static final byte cmdMarker = 0;
	// if packet is a command packet, second byte is actual command
	public static final byte dataMarker = 1;

	/**
	 * commands
	 * pushRevision:	sender is indicating that receiver should be prepared
	 * 					to receive an update
	 * requestRevision:	sender is indicating that receiver should send out
	 * 					an update
	 * startFile: 		sender is indicating that a file is about to be sent
	 * 
	 */
	public static final byte pushRevision = 1;		//[4 bytes: revision #][4 bytes : ip address]
	/*
	 * NOTE: for pushRevision commands
	 * first byte = cmdMarker
	 * second byte = "pushRevision"
	 * 3 - 6 bytes = revision number
	 * 7 - 10 bytes = ip address
	 */
	public static final int IP_POS_IN_CMD = 7;//to 10

	
	/**
	 * getIP returns the IP address contained in the command packet
	 * 
	 * @param cmdPacket the packet asking to pushRevision,
	 * 		  with a specific IP address it is pushing to
	 * 
	 * @return a string containing the IP to push the revision to
	 */

	public static String getIP(byte[] cmdPacket) {
		
		//extracting the byte representation of the IP address

		int[] ip = new int[4];
		for (int i = 0; i < 4; ++i) {
			ip[i] = cmdPacket[IP_POS_IN_CMD+i];
			ip[i] = ip[i] & 0xff;
		}
		
		
//		return "192.168.1.143";
		return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
//		System.out.println(ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3]);
//		return "127.0.0.1";
		

	}
	public static void setIP(byte[] cmdPacket) {
		
		//get the ip address of the localhost
		try {
			byte[] ip = InetAddress.getLocalHost().getAddress();

			//insert the IP address into the packet
			for(int i = 0; i < 4; i++) {
				cmdPacket[IP_POS_IN_CMD+i] = ip[i];
			}
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		
		

	}

	public static final byte requestRevision = 2;	//[4 bytes: requested revision #]
	
	
	
	public static final int fileCompleteAck = -1;



	/**
	 * parseRevisionResponse takes data and returns an integer representing the revision
	 * 
	 * @param response the data received over the network
	 * @return
	 */
	public static int parseRevisionResponse(byte[] response) {
		//TODO: is this supposed to do something?
		return 0;	
	}



	public static void setDataFragmentId(byte[] dataPacket, int id) {
		placeIntInByteArray(dataPacket,FRAG_NUM_TAG,id);
	}

	public static int getDataFragmentId(byte[] dataPacket) {
		return getIntFromByteArray(dataPacket, FRAG_NUM_TAG);
	}

	/**
	 * generateChecksum pulls the data out of the fragment, sums it
	 * and places the result into the fragment in the checksum section
	 * 
	 * @param fragment the packet being sent across the network
	 */
	public static void generateChecksum(byte[] fragment) {
		
		//extract the data from the packet
		int sum = findChecksum(fragment);
		
		placeIntInByteArray(fragment, CHKSUM_TAG, sum);
		

	}
	/**
	 * checkChecksum pulls the expected checksum out of the packet,
	 * calculates a checksum for the packet from the data,
	 * and compares them.
	 * 
	 * @param fragment the packet being sent across the network
	 * @return true if the checksums match
	 */
	public static boolean checkChecksum(byte[] fragment) {
		
		//pull the checksum out of the packet
		
		int foundSum = findChecksum(fragment);
		int sentSum = getIntFromByteArray(fragment, CHKSUM_TAG);
		
		return (sentSum == foundSum);
	}

	private static int findChecksum(byte[] fragment) {
		int bytesToScan = fragment.length - dataPacketHeaderSize;
		
		int sum = 0;
		for(int i = 0; i < bytesToScan; i++) {
			sum = (sum + fragment[i+dataPacketHeaderSize]) % (Integer.MAX_VALUE-512);
		}
		return sum;
	}
	

	private static int getIntFromByteArray(byte[] array, int location) {

		return array[location] << 24 | (array[location+1] & 0xFF) << 16 | (array[location+2] & 0xFF) << 8 | (array[location+3] & 0xFF);
	}

	private static void placeIntInByteArray(byte[] array, int location, int placeMe) {
		array[location] = (byte) (placeMe >> 24);
		array[location+1] = (byte) (placeMe >> 16);
		array[location+2] = (byte) (placeMe >> 8);
		array[location+3] = (byte) placeMe;
	}

	public static void main(String[] args) {

		byte[] test = new byte[100];
		
		try {
			System.out.println(InetAddress.getLocalHost().getAddress());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setIP(test);
		String b  = getIP(test);
		System.out.println(b);
		
		
		
//		placeIntInByteArray(test, 10, 200);
//
//		System.out.println(getIntFromByteArray(test, 10));

	}

}
