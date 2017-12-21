package org.gmagnotta.bitcoin.message.impl;

import java.math.BigInteger;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Ping Message
 * 
 * The ping message is sent primarily to confirm that the TCP/IP connection is still valid. An error in transmission
 * is presumed to be a closed connection and the address is removed as a current peer
 * @author giuseppe
 */
public class BitcoinPongMessage implements BitcoinMessage {

	private BigInteger nonce;

	public BitcoinPongMessage(BigInteger nonce) {

		this.nonce = nonce;

	}

	public BigInteger getNonce() {
		return nonce;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.PONG;
	}
	
	@Override
	public String toString() {
		return String.format("%s: nonce %s",
				BitcoinCommand.PONG, nonce);
	}

}
