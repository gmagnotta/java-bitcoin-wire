package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.InetAddress;

import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetDataMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;

public interface BitcoinPeer {
	
	/**
	 * Return inetaddress of remote peer
	 * 
	 * @return
	 */
	public InetAddress getInetAddress();
	
	/**
	 * Returns Peer services
	 * @return
	 */
	public BigInteger getPeerServices();
	
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
	 * Send getaddr Message to Peer
	 * 
	 * @param bitcoinGetAddrMessage
	 * @return
	 * @throws Exception
	 */
	public BitcoinAddrMessage sendGetAddrMessage(BitcoinGetAddrMessage bitcoinGetAddrMessage) throws Exception;
	
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
	 * 
	 * @param bitcoinHeadersMessage
	 * @throws Exception
	 */
	public void sendHeaders(BitcoinHeadersMessage bitcoinHeadersMessage) throws Exception;
	
	/**
	 * Send get headers
	 * @param bitcoinGetHeadersMessage
	 * @return
	 * @throws Exception
	 */
	public BitcoinHeadersMessage sendGetHeaders(BitcoinGetHeadersMessage bitcoinGetHeadersMessage) throws Exception;
	
	/**
	 * 
	 * @param bitcoinGetDataMessageSerializer
	 * @throws Exception
	 */
	public BlockMessage sendGetData(BitcoinGetDataMessage bitcoinGetDataMessage) throws Exception;
	
	/**
	 * Terminates connection with the peer
	 * @throws Exception 
	 */
	public void disconnect() throws Exception;
	
}
