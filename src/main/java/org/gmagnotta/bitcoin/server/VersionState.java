package org.gmagnotta.bitcoin.server;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;

import org.gmagnotta.bitcoin.message.BitcoinVerackMessage;
import org.gmagnotta.bitcoin.message.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.NetworkAddress;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinFrameSerializer;

/**
 * Here we expect to receive a version from the peer 
 */
public class VersionState implements ServerState {
	
	private ServerContext serverContext;
	private OutputStream outputStream;
	
	public VersionState(ServerContext serverContext, OutputStream outputStream) {
		this.serverContext = serverContext;
		this.outputStream = outputStream;
	}

	@Override
	public void receiveFrame(BitcoinFrame frame) throws Exception {
		
		// if we receive something different than VERSION, throw exception
		if (!frame.getCommand().equals(BitcoinCommand.VERSION)) {
			throw new Exception("Unexpected frame!");
		}
		
		BitcoinVersionMessage version = (BitcoinVersionMessage) frame.getPayload();
		
		if (version.getVersion() < 70001L) {
			throw new Exception("Unknown version!");
		}
		
		// Send our version
		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("0"), InetAddress.getLocalHost(), 0);

		BitcoinVersionMessage myVersion = new BitcoinVersionMessage(70001L, new BigInteger("0"), new BigInteger("" + System.currentTimeMillis() / 1000), receiving, receiving, new BigInteger("123"), "PeppeLibrary", 0, false);
		
		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame outFrame = builder.setMagicVersion(MagicVersion.TESTNET).setBitcoinMessage(myVersion).build();

		outputStream.write(new BitcoinFrameSerializer().serialize(outFrame));
		
		// send ACK
		
		BitcoinVerackMessage bitcoinVerackMessage = new BitcoinVerackMessage();
		
		builder = new BitcoinFrameBuilder();

		outFrame = builder.setMagicVersion(MagicVersion.TESTNET).setBitcoinMessage(bitcoinVerackMessage).build();
		
		outputStream.write(new BitcoinFrameSerializer().serialize(outFrame));
		
		ReadyState readyState = new ReadyState(serverContext, outputStream);
		serverContext.setNextState(readyState);
	}

}
