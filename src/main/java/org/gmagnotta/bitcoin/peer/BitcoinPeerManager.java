package org.gmagnotta.bitcoin.peer;

import java.util.List;

public interface BitcoinPeerManager {
	
	/**
	 * Start a connection to the peer specified by address and port
	 * 
	 * @param address
	 * @param port
	 * @throws Exception 
	 */
	public void connect(String address, int port) throws Exception;
	
	/**
	 * Returns a list of all connected BitcoinPeer
	 * @return
	 */
	public List<BitcoinPeer> getConnectedPeers();
	
	/**
	 * Disconnect from the specified BitcoinPeer
	 * 
	 * @param bitcoinPeer
	 */
	public void disconnect(BitcoinPeer bitcoinPeer);
	
	/**
	 * Start listening for connections on the specified port. This method will block
	 * 
	 * @param port
	 * @throws Exception 
	 */
	public void listen(int port) throws Exception;
	
	/**
	 * 
	 */
	public boolean isSyncing();
	
	/**
	 * 
	 */
	public void stopSync();

}
