package org.gmagnotta.bitcoin.server;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;

public interface ServerState {

	public void receiveFrame(BitcoinFrame frame) throws Exception;
	
}
