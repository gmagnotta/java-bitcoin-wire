package org.gmagnotta.bitcoin.server.state;
import java.math.BigInteger;

import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinRejectMessage;
import org.gmagnotta.bitcoin.server.ServerContext;
import org.gmagnotta.bitcoin.server.ServerState;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;

/**
 * Here we expect to receive a version from the peer 
 */
public class ReadyState implements ServerState {
	
	private ServerContext serverContext;
	
	public ReadyState(ServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public void receiveFrame(BitcoinFrame frame) throws Exception {
		
		if (frame.getCommand().equals(BitcoinCommand.PING)) {
			
			BitcoinPingMessage ping = (BitcoinPingMessage) frame.getPayload();
			
			BigInteger nonce = ping.getNonce();
			
			BitcoinPongMessage pong = new BitcoinPongMessage(nonce);
			
			serverContext.writeMessage(pong);
			
		} else {
			
			BitcoinRejectMessage reject = new BitcoinRejectMessage(frame.getCommand().getCommand(), (byte) 0x10, "Unknown command", new byte[] {});
			
			serverContext.writeMessage(reject);
			
		}
		
	}

}
