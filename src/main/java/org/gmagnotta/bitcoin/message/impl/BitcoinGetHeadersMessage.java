package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Ping Message
 * 
 * The ping message is sent primarily to confirm that the TCP/IP connection is still valid. An error in transmission
 * is presumed to be a closed connection and the address is removed as a current peer
 * @author giuseppe
 */
public class BitcoinGetHeadersMessage implements BitcoinMessage {

	private long version;
	private List<Sha256Hash> hashes;

	public BitcoinGetHeadersMessage(long version, List<Sha256Hash> hashes) {

		this.version = version;
		this.hashes = hashes;

	}
	
	public long getVersion() {
		return version;
	}
	
	public List<Sha256Hash> getHash() {
		return hashes;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.GETHEADERS;
	}
	
	@Override
	public String toString() {
		return String.format("%s: version %s",
				BitcoinCommand.GETHEADERS, version);
	}

}
