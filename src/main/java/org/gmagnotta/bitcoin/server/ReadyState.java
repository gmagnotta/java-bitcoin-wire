package org.gmagnotta.bitcoin.server;
import java.io.OutputStream;
import java.math.BigInteger;

import org.gmagnotta.bitcoin.message.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.BitcoinPongMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinFrameSerializer;

/**
 * Here we expect to receive a version from the peer 
 */
public class ReadyState implements ServerState {
	
	private ServerContext serverContext;
	private OutputStream outputStream;
	
	public ReadyState(ServerContext serverContext, OutputStream outputStream) {
		this.serverContext = serverContext;
		this.outputStream = outputStream;
	}

	@Override
	public void receiveFrame(BitcoinFrame frame) throws Exception {
		
		// if we receive something different than VERSION, throw exception
		if (frame.getCommand().equals(BitcoinCommand.PING)) {
			
			BitcoinPingMessage ping = (BitcoinPingMessage) frame.getPayload();
			
			BigInteger nonce = ping.getNonce();
			
			BitcoinPongMessage pong = new BitcoinPongMessage(nonce);
			
			BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

			BitcoinFrame outFrame = builder.setMagicVersion(MagicVersion.TESTNET).setBitcoinMessage(pong).build();

			outputStream.write(new BitcoinFrameSerializer().serialize(outFrame));
			
		}
		
	}

}
