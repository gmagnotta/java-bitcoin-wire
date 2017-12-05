package org.gmagnotta.bitcoin.message;

import org.gmagnotta.bitcoin.raw.BitcoinCommand;

/**
 * This class represents Bitcoin Verack Message
 * 
 * A "verack" packet shall be sent if the version packet was accepted.
 * 
 * @author giuseppe
 */
public class BitcoinVerackMessage implements BitcoinMessage {


	public BitcoinVerackMessage() {
	}


	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.VERACK;
	}
	
	@Override
	public String toString() {
		return String.format("%s: ", BitcoinCommand.VERACK);
	}

}
