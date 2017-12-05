package org.gmagnotta.bitcoin;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.raw.BitcoinFrame;
import org.gmagnotta.bitcoin.raw.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.raw.MagicVersion;
import org.gmagnotta.bitcoin.raw.serializer.BitcoinFrameSerializer;

public class BitcoinClient {

	private String host;
	private int port;
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BitcoinFrameParserStream parser;

	public BitcoinClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void connect() throws Exception {

		clientSocket = new Socket(host, port);

		outputStream = clientSocket.getOutputStream();

		inputStream = clientSocket.getInputStream();

		this.parser = new BitcoinFrameParserStream(inputStream);

	}

	public BitcoinMessage getMessage() throws Exception {

		BitcoinFrame frame = parser.getFrame();
		
		return frame.getPayload();

	}

	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {

		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame frame = builder.setMagicVersion(MagicVersion.TESTNET).setBitcoinMessage(bitcoinMessage).build();

		outputStream.write(new BitcoinFrameSerializer().serialize(frame));

	}
	
	public void disconnect() throws Exception {
		
		clientSocket.close();
		
	}

}
