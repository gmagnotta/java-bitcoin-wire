package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

public class BlockMessage implements BitcoinMessage {
	
	private BlockHeader blockHeader;
	private List<Transaction> txns;

	public BlockMessage(BlockHeader blockHeader, List<Transaction> txns) {
		this.blockHeader = blockHeader;
		this.txns = txns;
		
	}
	
	public List<Transaction> getTxns() {
		return txns;
	}
	
	public BlockHeader getBlockHeader() {
		return blockHeader;
	}
	
	@Override
	public BitcoinCommand getCommand() {
		return BitcoinCommand.BLOCK;
	}
	
	@Override
	public String toString() {
		return String.format("%s: ",
				BitcoinCommand.BLOCK);
	}

}
