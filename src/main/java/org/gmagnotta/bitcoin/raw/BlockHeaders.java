package org.gmagnotta.bitcoin.raw;

import java.util.Objects;

import org.bitcoinj.core.Sha256Hash;

public class BlockHeaders {
	
	private long version;
	private Sha256Hash prevBlock;
	private Sha256Hash merkleRoot;
	private long timestamp;
	private long bits;
	private long nonce;
	private long txnCount;
	
	public BlockHeaders(long version, Sha256Hash prevBlock, Sha256Hash merkleRoot, long timestamp, long bits,
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
		
		if (!(object instanceof BlockHeaders))
			return false;
		
		if (this == object)
			return true;
		
		final BlockHeaders other = (BlockHeaders) object;
		
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
		
		return String.format("BlockHeaders: %d, %d, %s, %d");
		
	}

}
