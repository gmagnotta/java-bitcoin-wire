package org.gmagnotta.bitcoin.server.state;
import java.math.BigInteger;
import java.net.InetAddress;

import org.gmagnotta.bitcoin.message.impl.BitcoinRejectMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVerackMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.server.ServerContext;
import org.gmagnotta.bitcoin.server.ServerState;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;

/**
 * Here we expect to receive a version from the peer 
 */
public class VersionState implements ServerState {
	
	private ServerContext serverContext;
	
	public VersionState(ServerContext serverContext) {
		this.serverContext = serverContext;
	}

	@Override
	public void receiveFrame(BitcoinFrame frame) throws Exception {
		
		// if we receive something different than VERSION, throw exception
		if (!frame.getCommand().equals(BitcoinCommand.VERSION)) {
			
			BitcoinRejectMessage rejectMessage = new BitcoinRejectMessage(frame.getCommand().getCommand(), (byte) 0x10, "Unexpected frame", null);
			
			serverContext.writeMessage(rejectMessage);
			
			throw new Exception("Unexpected frame!");
			
			
		}
		
		BitcoinVersionMessage version = (BitcoinVersionMessage) frame.getPayload();
		
		if (version.getVersion() < 70012L) {
			
			BitcoinRejectMessage rejectMessage = new BitcoinRejectMessage(frame.getCommand().getCommand(), (byte) 0x10, "Unsupported version", null);
			
			serverContext.writeMessage(rejectMessage);
			
			throw new Exception("Unknown version!");
			
		}
		
		// Send our version
		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("0"), InetAddress.getLocalHost(), 0);

		BitcoinVersionMessage myVersion = new BitcoinVersionMessage(70012L, new BigInteger("0"), new BigInteger("" + System.currentTimeMillis() / 1000), receiving, receiving, new BigInteger("123"), "/BitcoinPeppe:0.0.1/", 0, false);
		
		serverContext.writeMessage(myVersion);

		// send ACK
		
		BitcoinVerackMessage bitcoinVerackMessage = new BitcoinVerackMessage();
		
		serverContext.writeMessage(bitcoinVerackMessage);
		
		// Now jump to ready state
		
		ReadyState readyState = new ReadyState(serverContext);
		serverContext.setNextState(readyState);
	}

}
