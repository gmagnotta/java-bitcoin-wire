package org.gmagnotta.bitcoin.blockchain;

import org.bitcoinj.core.Sha256Hash;

public interface Block {
	
	/**
	 * Return block hash
	 * @return
	 */
	public Sha256Hash getHash();

}
