package org.gmagnotta.bitcoin.server;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.server.ServerState;

/**
 * Represents the Server Context used by states
 */
public interface ServerContext {
	
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
