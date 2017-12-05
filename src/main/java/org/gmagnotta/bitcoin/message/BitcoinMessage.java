package org.gmagnotta.bitcoin.message;

import org.gmagnotta.bitcoin.raw.BitcoinCommand;

public interface BitcoinMessage {
	
	public BitcoinCommand getCommand();
	
}
