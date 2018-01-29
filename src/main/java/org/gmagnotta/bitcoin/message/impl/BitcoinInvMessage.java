package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

/**
 * This class represents Bitcoin Ping Message
 * 
 * The ping message is sent primarily to confirm that the TCP/IP connection is still valid. An error in transmission
 * is presumed to be a closed connection and the address is removed as a current peer
 * @author giuseppe
 */
public class BitcoinInvMessage implements BitcoinMessage {

	private List<InventoryVector> inventoryVectors;

	public BitcoinInvMessage(List<InventoryVector> inventoryVectors) {

		this.inventoryVectors = inventoryVectors;

	}
	
	public List<InventoryVector> getInventoryVectors() {
		return inventoryVectors;
	}

	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.INV;
	}
	
	@Override
	public String toString() {
		return String.format("%s: ",
				BitcoinCommand.INV);
	}

}
