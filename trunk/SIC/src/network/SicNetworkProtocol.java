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

	public static final String _RevisionRequest = "Yo Dawg, what revision are we at?";
	public static final String _RevisionRequestResponce = "Ya dawg, we are at revision ";
	
	
	/**
	 * parseRevisionResponce takes data and returns an integer representing the revision
	 * 
	 * @param responce the string received over the network
	 * @return
	 */
	public static int parseRevisionResponce(String responce) {
		String[] revision = responce.split(_RevisionRequestResponce);
		
		return Integer.parseInt(revision[1]);
		
	}

	public static void main(String[] args) {
		
		int rev = parseRevisionResponce("Ya dawg, we are at revision 21");
		System.out.println(rev);
	}
	
}
