package org.gmagnotta.bitcoin.message;

import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * Represents a Bitcoin message.
 */
public interface BitcoinMessage {
	
	/**
	 * Returns the associated BitcoinCommand
	 * @return
	 */
	public BitcoinCommand getCommand();
	
}
