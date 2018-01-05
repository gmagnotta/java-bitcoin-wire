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
	
}
