package org.gmagnotta.bitcoin.user;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.NetworkAddress;
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
	private MagicVersion magicVersion;
	
	private LinkedBlockingQueue<BitcoinMessage> queue;

	public BitcoinClient(MagicVersion magicVersion, String host, int port) {
		this.magicVersion = magicVersion;
		this.host = host;
		this.port = port;
		this.queue = new LinkedBlockingQueue<BitcoinMessage>();
	}

	public void connect() throws Exception {

		clientSocket = new Socket(host, port);

		outputStream = clientSocket.getOutputStream();

		inputStream = clientSocket.getInputStream();

		parser = new BitcoinFrameParserStream(magicVersion, inputStream);
		
		new Thread(new ReaderRunnable(parser, queue)).start();
		
		NetworkAddress emitting = new NetworkAddress(0, new BigInteger("1"), InetAddress.getByAddress(new byte[] {0, 0, 0, 0}), clientSocket.getLocalPort());
		
		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("1"), InetAddress.getByName(host), port);

		BitcoinVersionMessage versionMessage = new BitcoinVersionMessage(70012L, new BigInteger("1"), new BigInteger("" + System.currentTimeMillis() / 1000), receiving, emitting, new BigInteger("123"), "/BitcoinPeppe:0.0.1/", 0, false);
		
		// SEND VERSION
		writeMessage(versionMessage);
		
		BitcoinMessage message = getMessage();
		
		if (!message.getCommand().equals(BitcoinCommand.VERSION)) {
			
			throw new Exception("Unexpected response!");
			
		}

		BitcoinVersionMessage version = (BitcoinVersionMessage) message;
		
		if (version.getVersion() < 70012L) {
			throw new Exception("Unsupported version!");
		}
		
		message = getMessage();
		
		if (!message.getCommand().equals(BitcoinCommand.VERACK)) {
			
			throw new Exception("Unexpected response!");
			
		}

	}

	public BitcoinMessage getMessage() throws Exception {
		
		return queue.take();

	}
	
	public BitcoinMessage getMessage(long timeout, TimeUnit unit) throws Exception {
		
		return queue.poll(timeout, unit);

	}

	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {

		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame frame = builder.setMagicVersion(magicVersion).setBitcoinMessage(bitcoinMessage).build();

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
