package org.gmagnotta.bitcoin.user;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.BitcoinPongMessage;
import org.gmagnotta.bitcoin.parser.BitcoinFrameParserStream;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinFrameSerializer;

public class BitcoinClient {

	private String host;
	private int port;
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BitcoinFrameParserStream parser;
	private ReaderRunnable readerRunnable;
	
	private LinkedBlockingQueue<BitcoinMessage> queue;

	public BitcoinClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.queue = new LinkedBlockingQueue<BitcoinMessage>();
	}

	public void connect() throws Exception {

		clientSocket = new Socket(host, port);

		outputStream = clientSocket.getOutputStream();

		inputStream = clientSocket.getInputStream();

		parser = new BitcoinFrameParserStream(inputStream);
		
		new Thread(new ReaderRunnable(parser, queue)).start();

	}

	public BitcoinMessage getMessage() throws Exception {
		
		return queue.take();

	}

	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {

		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame frame = builder.setMagicVersion(MagicVersion.TESTNET).setBitcoinMessage(bitcoinMessage).build();

		outputStream.write(new BitcoinFrameSerializer().serialize(frame));

	}
	
	public void disconnect() throws Exception {
		
		clientSocket.close();
		
	}
	
	private class ReaderRunnable implements Runnable {

		private BitcoinFrameParserStream bitcoinFrameParserStream;
		private LinkedBlockingQueue<BitcoinMessage> queue;
		
		public ReaderRunnable(BitcoinFrameParserStream bitcoinFrameParserStream, LinkedBlockingQueue<BitcoinMessage> queue) {
			this.bitcoinFrameParserStream = bitcoinFrameParserStream;
			this.queue = queue;
		}
		
		@Override
		public void run() {

			try {
				
				while (true) {
					
					BitcoinFrame frame = bitcoinFrameParserStream.getFrame();
					
					if (frame.getCommand().equals(BitcoinCommand.PING)) {
						
						BitcoinPingMessage ping = (BitcoinPingMessage) frame.getPayload();
						
						BigInteger nonce = ping.getNonce();
						
						BitcoinPongMessage pong = new BitcoinPongMessage(nonce);
						
						writeMessage(pong);
						
					} else {
					
						queue.put(frame.getPayload());
					
					}
					
				}
			
			} catch (Exception ex) {
				
				ex.printStackTrace();
				
			}
			
		}
		
	}

}
