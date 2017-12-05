package org.gmagnotta.bitcoin.user;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.server.ServerState;

public interface ClientContext {
	
	/**
	 * Jump to another state
	 * 
	 * @param serverState
	 */
	public void setNextState(ServerState serverState);
	
	/**
	 * Write message
	 * @param bitcoinMessage
	 */
	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception;

}
