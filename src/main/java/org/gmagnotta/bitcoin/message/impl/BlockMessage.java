package org.gmagnotta.bitcoin.message.impl;

import java.util.List;
import java.util.Map;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

public class BlockMessage implements BitcoinMessage {
	
	private BlockHeader blockHeader;
	private List<Transaction> txns;
	private Map<Sha256Hash, Transaction> indexedTxns;

	public BlockMessage(BlockHeader blockHeader, List<Transaction> txns, Map<Sha256Hash, Transaction> indexedTxns) {
		this.blockHeader = blockHeader;
		this.txns = txns;
		this.indexedTxns = indexedTxns;
	}
	
	public List<Transaction> getTxns() {
		return txns;
	}
	
	public Map<Sha256Hash, Transaction> getIndexedTxns() {
		return indexedTxns;
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
