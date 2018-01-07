package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Addr Message
 * @author giuseppe
 */
public class BitcoinAddrMessage implements BitcoinMessage {

	private List<NetworkAddress> networkAddresses;

	public BitcoinAddrMessage(List<NetworkAddress> networkAddresses) {

		this.networkAddresses = networkAddresses;

	}
	
	public List<NetworkAddress> getNetworkAddress() {
		return networkAddresses;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.ADDR;
	}
	
	@Override
	public String toString() {
		return String.format("%s: count %s; address %s",
				BitcoinCommand.ADDR, networkAddresses.size(), networkAddresses);
	}

}
