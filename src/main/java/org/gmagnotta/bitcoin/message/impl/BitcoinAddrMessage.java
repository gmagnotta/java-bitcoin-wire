package org.gmagnotta.bitcoin.message.impl;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Addr Message
 * @author giuseppe
 */
public class BitcoinAddrMessage implements BitcoinMessage {

	private long count;
	private NetworkAddress networkAddress;

	public BitcoinAddrMessage(long count, NetworkAddress networkAddress) {

		this.count = count;
		this.networkAddress = networkAddress;

	}
	
	public NetworkAddress getNetworkAddress() {
		return networkAddress;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.ADDR;
	}
	
	@Override
	public String toString() {
		return String.format("%s: count %s; address %s",
				BitcoinCommand.ADDR, count, networkAddress);
	}

}
