package org.gmagnotta.bitcoin.message;

import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * Represents a BitCoin message.
 */
public interface BitcoinMessage {
	
	/**
	 * Returns the associated BitcoinCommand
	 * @return
	 */
	public BitcoinCommand getCommand();
	
}
