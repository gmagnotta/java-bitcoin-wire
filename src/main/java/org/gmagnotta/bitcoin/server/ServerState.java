package org.gmagnotta.bitcoin.server;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;

/**
 * Represents a particular state in the Server
 * 
 */
public interface ServerState {

	/**
	 * Manage the frame receiving
	 * @param frame
	 * @throws Exception
	 */
	public void receiveFrame(BitcoinFrame frame) throws Exception;
	
}
