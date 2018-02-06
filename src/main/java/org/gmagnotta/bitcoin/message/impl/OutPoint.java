package org.gmagnotta.bitcoin.message.impl;

import org.gmagnotta.bitcoin.utils.Sha256Hash;

public class OutPoint {
	
	private Sha256Hash hash;
	private long index;
	
	public OutPoint(Sha256Hash hash, long index) {
		this.hash = hash;
		this.index = index;
	}
	
	public Sha256Hash getHash() {
		return hash;
	}

	public long getIndex() {
		return index;
	}

}
