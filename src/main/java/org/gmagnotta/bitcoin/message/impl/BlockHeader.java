package org.gmagnotta.bitcoin.message.impl;

import java.util.Date;
import java.util.Objects;

import org.bitcoinj.core.Sha256Hash;

public class BlockHeader {
	
	private long version;
	private Sha256Hash prevBlock;
	private Sha256Hash merkleRoot;
	private long timestamp;
	private long bits;
	private long nonce;
	private long txnCount;
	
	public BlockHeader(long version, Sha256Hash prevBlock, Sha256Hash merkleRoot, long timestamp, long bits,
			long nonce, long txnCount) {
		this.version = version;
		this.prevBlock = prevBlock;
		this.merkleRoot = merkleRoot;
		this.timestamp = timestamp;
		this.bits = bits;
		this.nonce = nonce;
		this.txnCount = txnCount;
	}
	
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public Sha256Hash getPrevBlock() {
		return prevBlock;
	}

	public void setPrevBlock(Sha256Hash prevBlock) {
		this.prevBlock = prevBlock;
	}

	public Sha256Hash getMerkleRoot() {
		return merkleRoot;
	}

	public void setMerkleRoot(Sha256Hash merkleRoot) {
		this.merkleRoot = merkleRoot;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getBits() {
		return bits;
	}

	public void setBits(long bits) {
		this.bits = bits;
	}

	public long getNonce() {
		return nonce;
	}

	public void setNonce(long nonce) {
		this.nonce = nonce;
	}

	public long getTxnCount() {
		return txnCount;
	}

	public void setTxnCount(long txnCount) {
		this.txnCount = txnCount;
	}

	@Override
	public boolean equals(final Object object) {
		
		if (!(object instanceof BlockHeader))
			return false;
		
		if (this == object)
			return true;
		
		final BlockHeader other = (BlockHeader) object;
		
		return Objects.equals(version, other.version) &&
				Objects.equals(prevBlock, other.prevBlock) &&
				Objects.equals(merkleRoot, other.merkleRoot) &&
				Objects.equals(timestamp, other.timestamp) &&
				Objects.equals(bits, other.bits) &&
				Objects.equals(nonce, other.nonce) &&
				Objects.equals(txnCount, other.txnCount);
		
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(version, prevBlock,  merkleRoot, timestamp, bits, nonce, txnCount);
		
	}
	
	@Override
	public String toString() {
		
		return String.format("BlockHeaders: %d, %s, %s, %s, %d, %d, %d", version, prevBlock, merkleRoot, new Date(timestamp * 1000), bits, nonce, txnCount );
		
	}

}
