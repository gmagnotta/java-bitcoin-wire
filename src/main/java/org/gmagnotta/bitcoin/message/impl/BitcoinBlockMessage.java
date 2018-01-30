package org.gmagnotta.bitcoin.message.impl;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Ping Message
 * 
 * The ping message is sent primarily to confirm that the TCP/IP connection is still valid. An error in transmission
 * is presumed to be a closed connection and the address is removed as a current peer
 * @author giuseppe
 */
public class BitcoinBlockMessage implements BitcoinMessage {

	private BlockHeader header;

	public BitcoinBlockMessage(BlockHeader header) {

		this.header = header;

	}
	
	public BlockHeader getHeader() {
		return header;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.BLOCK;
	}
	
	@Override
	public String toString() {
		return String.format("%s:",
				BitcoinCommand.BLOCK);
	}

}
