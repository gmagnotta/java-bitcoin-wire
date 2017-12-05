package org.gmagnotta.bitcoin.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.parser.BitcoinFrameParserStream;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinFrameSerializer;

public class BitcoinServer implements ServerContext {
	
	private ServerState serverState;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BitcoinFrameParserStream parser;

	public BitcoinServer(Socket socket) {
		this.socket = socket;
	}

	public void start() throws Exception {

		outputStream = socket.getOutputStream();

		inputStream = socket.getInputStream();

		this.parser = new BitcoinFrameParserStream(inputStream);
		
		serverState = new VersionState(this, outputStream);
		
		while (true) {
			
			BitcoinFrame frame = parser.getFrame();
			
			serverState.receiveFrame(frame);
			
		}

	}

	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {

		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame frame = builder.setMagicVersion(MagicVersion.TESTNET).setBitcoinMessage(bitcoinMessage).build();

		outputStream.write(new BitcoinFrameSerializer().serialize(frame));

	}
	
	public void close() throws Exception {
		
		socket.close();
		
	}

	@Override
	public void setNextState(ServerState serverState) {
		this.serverState = serverState;
	}

}
