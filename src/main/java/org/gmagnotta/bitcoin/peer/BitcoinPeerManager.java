package org.gmagnotta.bitcoin.peer;

public interface BitcoinPeerManager {
	
	/**
	 * Start connection manager
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception;
	
	/**
	 * Start listening for connections on the specified port. This method will block
	 * 
	 * @param port
	 * @throws Exception 
	 */
	public void listen(int port) throws Exception;
	
	/**
	 * Stop all connections
	 */
	public void stop() throws Exception;

}
