package org.gmagnotta.bitcoin.blockchain;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;

public class ValidatedBlockHeader extends BlockHeader {
	
	private Sha256Hash hash;
	private long number;

	public ValidatedBlockHeader(long version, Sha256Hash prevBlock, Sha256Hash merkleRoot, long timestamp, long bits,
			long nonce, long txnCount, Sha256Hash hash, long number) {
		super(version, prevBlock, merkleRoot, timestamp, bits, nonce, txnCount);
		
		this.hash = hash;
		this.number = number;
	}

	public Sha256Hash getHash() {
		return hash;
	}

	public long getNumber() {
		return number;
	}

}
