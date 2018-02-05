package org.gmagnotta.bitcoin.message.impl;

import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;

public class BlockMessage extends BlockHeader implements BitcoinMessage {
	
	private List<Object> txns;

	public BlockMessage(long version, Sha256Hash prevBlock, Sha256Hash merkleRoot, long timestamp, long bits, long nonce,
			long txnCount, List<Object> txns) {
		super(version, prevBlock, merkleRoot, timestamp, bits, nonce, txnCount);
		
		this.txns = txns;
		
	}
	
	public List<Object> getTxns() {
		return txns;
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
