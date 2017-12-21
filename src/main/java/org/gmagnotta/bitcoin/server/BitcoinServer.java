package org.gmagnotta.bitcoin.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.parser.BitcoinFrameParserStream;
import org.gmagnotta.bitcoin.server.state.VersionState;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.MagicVersion;

public class BitcoinServer implements ServerContext {
	
	private MagicVersion magicVersion;
	private ServerState serverState;
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BitcoinFrameParserStream parser;

	public BitcoinServer(MagicVersion magicVersion, Socket socket) {
		this.magicVersion = magicVersion;
		this.socket = socket;
		this.serverState = new VersionState(this);
	}

	public void start() throws Exception {

		outputStream = socket.getOutputStream();

		inputStream = socket.getInputStream();

		this.parser = new BitcoinFrameParserStream(magicVersion, inputStream);
		
		while (true) {
			
			BitcoinFrame frame = parser.getBitcoinFrame();
			
			serverState.receiveFrame(frame);
			
		}

	}

	@Override
	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {

		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame frame = builder.setMagicVersion(magicVersion).setBitcoinMessage(bitcoinMessage).build();

		outputStream.write(BitcoinFrame.serialize(frame));

	}
	
	public void close() throws Exception {
		
		socket.close();
		
	}

	@Override
	public void setNextState(ServerState serverState) {
		this.serverState = serverState;
	}

}
