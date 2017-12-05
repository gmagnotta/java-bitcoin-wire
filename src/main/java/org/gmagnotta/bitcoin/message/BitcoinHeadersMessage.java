package org.gmagnotta.bitcoin.message;

import java.util.List;

import org.gmagnotta.bitcoin.raw.BitcoinCommand;
import org.gmagnotta.bitcoin.raw.BlockHeaders;

/**
 * This class represents Bitcoin Ping Message
 * 
 * The ping message is sent primarily to confirm that the TCP/IP connection is still valid. An error in transmission
 * is presumed to be a closed connection and the address is removed as a current peer
 * @author giuseppe
 */
public class BitcoinHeadersMessage implements BitcoinMessage {

	private List<BlockHeaders> headers;

	public BitcoinHeadersMessage(List<BlockHeaders> headers) {

		this.headers = headers;

	}
	
	public List<BlockHeaders> getHeaders() {
		return headers;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.HEADERS;
	}
	
	@Override
	public String toString() {
		return String.format("%s: nonce",
				BitcoinCommand.HEADERS);
	}

}
