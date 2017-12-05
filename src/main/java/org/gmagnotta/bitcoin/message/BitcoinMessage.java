package org.gmagnotta.bitcoin.message;

import org.gmagnotta.bitcoin.wire.BitcoinCommand;

public interface BitcoinMessage {
	
	public BitcoinCommand getCommand();
	
}
