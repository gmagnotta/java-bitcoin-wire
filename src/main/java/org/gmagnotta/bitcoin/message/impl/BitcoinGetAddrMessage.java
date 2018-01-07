package org.gmagnotta.bitcoin.message.impl;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Get addr Message
 * 
 * The ping message is sent primarily to confirm that the TCP/IP connection is still valid. An error in transmission
 * is presumed to be a closed connection and the address is removed as a current peer
 * @author giuseppe
 */
public class BitcoinGetAddrMessage implements BitcoinMessage {

	public BitcoinGetAddrMessage() {
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.GETADDR;
	}
	
	@Override
	public String toString() {
		return String.format("%s: ",
				BitcoinCommand.GETADDR);
	}

}
