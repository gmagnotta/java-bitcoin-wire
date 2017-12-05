package org.gmagnotta.bitcoin.user;

import org.gmagnotta.bitcoin.wire.BitcoinFrame;

public interface ClientState {
	
	/**
	 * Manage the frame receiving
	 * @param frame
	 * @throws Exception
	 */
	public void receiveFrame(BitcoinFrame frame) throws Exception;

}
