package org.gmagnotta.bitcoin.user;

import org.gmagnotta.bitcoin.message.BitcoinMessage;

public interface ClientContext {

	/**
	 * Jump to another state
	 * 
	 * @param serverState
	 */
	public void setNextState(ClientState clientState);

	/**
	 * Write message
	 * 
	 * @param bitcoinMessage
	 */
	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception;

}
