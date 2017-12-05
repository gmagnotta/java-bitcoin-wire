package org.gmagnotta.bitcoin.message;

import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.raw.BitcoinCommand;

/**
 * This class represents Bitcoin Ping Message
 * 
 * The ping message is sent primarily to confirm that the TCP/IP connection is still valid. An error in transmission
 * is presumed to be a closed connection and the address is removed as a current peer
 * @author giuseppe
 */
public class BitcoinGetBlocksMessage implements BitcoinMessage {

	private long version;
	private List<Sha256Hash> hashes;

	public BitcoinGetBlocksMessage(long version, List<Sha256Hash> hashes) {

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
		return BitcoinCommand.GETBLOCKS;
	}
	
	@Override
	public String toString() {
		return String.format("%s: nonce %s",
				BitcoinCommand.GETBLOCKS, version);
	}

}
