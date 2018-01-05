package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;

import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;

public interface BitcoinPeer {
	
	/**
	 * Returns Peer services
	 * @return
	 */
	public BigInteger getNodeServices();
	
	/**
	 * Returns Peers user agent
	 * @return
	 */
	public String getUserAgent();
	
	/**
	 * Return block start height
	 * @return
	 */
	public long getBlockStartHeight();
	
	/**
	 * Send Addr message to Peer
	 * @param bitcoinAddrMessage
	 * @throws Exception 
	 */
	public void sendAddrMessage(BitcoinAddrMessage bitcoinAddrMessage) throws Exception;
	
	/**
	 * Send a Ping to Peer
	 * @param bitcoinPingMessage
	 * @throws Exception 
	 */
	public BitcoinPongMessage sendPing(BitcoinPingMessage bitcoinPingMessage) throws Exception;
	
	/**
	 * Send a Poing to Peer
	 * @param bitcoinPongMessage
	 * @throws Exception 
	 */
	public void sendPong(BitcoinPongMessage bitcoinPongMessage) throws Exception;
	
	/**
	 * Terminates connection with the peer
	 * @throws Exception 
	 */
	public void disconnect() throws Exception;
	
}
