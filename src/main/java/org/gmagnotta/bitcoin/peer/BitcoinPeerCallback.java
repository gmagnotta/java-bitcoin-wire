package org.gmagnotta.bitcoin.peer;

import org.gmagnotta.bitcoin.message.BitcoinMessage;

public interface BitcoinPeerCallback {
	
	/**
	 * Tell the manager that an unsolicied message was received by a Peer
	 * 
	 * @param bitcoinAddrMessage
	 * @param bitcoinPeer
	 */
	public void onMessageReceived(BitcoinMessage bitcoinMessage, BitcoinPeer bitcoinPeer);
	
	/**
	 * Tell the manager that the connection with the peer is ready
	 * 
	 * @param bitcoinPeer
	 */
	public void onConnectionEstablished(BitcoinPeer bitcoinPeer);
	
	/**
	 * Tell the manager that the connection with the peer was closed
	 * 
	 * @param bitcoinPeer
	 */
	public void onConnectionClosed(BitcoinPeer bitcoinPeer);
	
}
