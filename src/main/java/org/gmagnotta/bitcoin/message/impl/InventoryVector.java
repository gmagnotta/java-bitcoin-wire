package org.gmagnotta.bitcoin.message.impl;

import org.bitcoinj.core.Sha256Hash;

public class InventoryVector {
	
	public enum Type {
		
		ERROR,
		
		MSG_TX,
		
		MSG_BLOCK,
		
		MSG_FILTERED_BLOCK,
		
		MSG_CMPCT_BLOCK;
		
	}
	
	private Type type;
	private Sha256Hash hash;
	
	public InventoryVector(Type type, Sha256Hash hash) {
		this.type = type;
		this.hash = hash;
	}

	public Type getType() {
		return type;
	}
	
	public Sha256Hash getHash() {
		return hash;
	}
}
