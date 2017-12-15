package org.gmagnotta.bitcoin.user;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.message.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.BlockHeaders;
import org.gmagnotta.bitcoin.message.NetworkAddress;
import org.gmagnotta.bitcoin.parser.BitcoinFrameParserStream;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.serializer.BitcoinFrameSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class BitcoinClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinClient.class);

	private String host;
	private int port;
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BitcoinFrameParserStream parser;
	private MagicVersion magicVersion;
	private boolean waitForResponse;
	
	private Object sync;
	
	private final LinkedBlockingQueue<BitcoinMessage> inputQueue;
	private final LinkedBlockingQueue<BitcoinMessage> outputQueue;
	private final LinkedBlockingQueue<BitcoinMessage> responseQueue;

	public BitcoinClient(MagicVersion magicVersion, String host, int port) {
		this.magicVersion = magicVersion;
		this.host = host;
		this.port = port;
		this.inputQueue = new LinkedBlockingQueue<BitcoinMessage>();
		this.outputQueue = new LinkedBlockingQueue<BitcoinMessage>();
		this.responseQueue = new LinkedBlockingQueue<BitcoinMessage>();
		this.waitForResponse = false;
		this.sync = new Object();
	}

	public void connect() throws Exception {

		clientSocket = new Socket(host, port);

		outputStream = clientSocket.getOutputStream();

		inputStream = clientSocket.getInputStream();

		parser = new BitcoinFrameParserStream(magicVersion, inputStream);
		
		new Thread(new ReaderRunnable(parser)).start();
		
		new Thread(new WriterRunnable()).start();
		
		new Thread(new MessageRunnable()).start();
		
		NetworkAddress emitting = new NetworkAddress(0, new BigInteger("1"), InetAddress.getByAddress(new byte[] {0, 0, 0, 0}), clientSocket.getLocalPort());
		
		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("1"), InetAddress.getByName(host), port);

		BitcoinVersionMessage versionMessage = new BitcoinVersionMessage(70012L, new BigInteger("1"), new BigInteger("" + System.currentTimeMillis() / 1000), receiving, emitting, new BigInteger("123"), "/BitcoinPeppe:0.0.1/", 0, false);
		
		// SEND VERSION
		writeMessage(versionMessage);
		
		BitcoinMessage message = getMessage();
		
		if (!message.getCommand().equals(BitcoinCommand.VERACK)) {
			
			throw new Exception("Unexpected response!");
			
		}

	}

	public BitcoinMessage getMessage() throws Exception {
		
		try {
		
			return responseQueue.take();
		
		} finally {
			
			synchronized (sync) {
				waitForResponse = false;
			}
		
		}

	}
	
	public BitcoinMessage getMessage(long timeout, TimeUnit unit) throws Exception {
		
		try {
		
			return responseQueue.poll(timeout, unit);
		
		} finally {
			
			synchronized (sync) {
				waitForResponse = false;
			}
			
		}

	}

	public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {

		outputQueue.put(bitcoinMessage);
			
		synchronized (sync) {
			
			waitForResponse = true;
			
		}

	}
	
	public void disconnect() throws Exception {
		
		clientSocket.close();
		
	}
	
	private class ReaderRunnable implements Runnable {

		private BitcoinFrameParserStream bitcoinFrameParserStream;
		
		public ReaderRunnable(BitcoinFrameParserStream bitcoinFrameParserStream) {
			this.bitcoinFrameParserStream = bitcoinFrameParserStream;
		}
		
		@Override
		public void run() {

			try {
				
				while (!Thread.currentThread().isInterrupted()) {
					
					BitcoinFrame frame = bitcoinFrameParserStream.getFrame();
					
					inputQueue.put(frame.getPayload());
						
				}
			
			} catch (Exception ex) {
				
				LOGGER.error("Exception", ex);
				
			}
			
		}
		
	}
	
	private class WriterRunnable implements Runnable {
		
		private BitcoinFrameBuilder builder;
		
		public WriterRunnable() {
			
			this.builder = new BitcoinFrameBuilder();
			
		}

		@Override
		public void run() {

			try {
				
				while (!Thread.currentThread().isInterrupted()) {
				
					BitcoinMessage outputMessage = outputQueue.take();

					BitcoinFrame frame = builder.setMagicVersion(magicVersion).setBitcoinMessage(outputMessage).build();

					outputStream.write(new BitcoinFrameSerializer().serialize(frame));
					
				}
			
			} catch (Exception ex) {
				
				LOGGER.error("Exception", ex);
				
			}
			
		}
		
	}
	
	private class MessageRunnable implements Runnable {

		@Override
		public void run() {

			try {
				
				while (!Thread.currentThread().isInterrupted()) {
					
						BitcoinMessage message = inputQueue.take();
						
						if (message.getCommand().equals(BitcoinCommand.VERSION)) {
							
							// mange peer version
							LOGGER.info("RECEIVED VERSION");
							
						} else if (message.getCommand().equals(BitcoinCommand.PING)) {
							
							LOGGER.info("RECEIVED PING");
							
							BitcoinPingMessage ping = (BitcoinPingMessage) message;
							
							BigInteger nonce = ping.getNonce();
							
							BitcoinPongMessage pong = new BitcoinPongMessage(nonce);
							
							writeMessage(pong);
							
						} else if (message.getCommand().equals(BitcoinCommand.GETHEADERS)) {
							
							// MANAGE HEADERS
							
							List<BlockHeaders> h = new ArrayList<BlockHeaders>();
//							
							BitcoinHeadersMessage headersMessage = new BitcoinHeadersMessage(h);
							
							writeMessage(headersMessage);
							
							LOGGER.info("RECEIVED GETHEADERS");
							
						} else {
						
							synchronized (sync) {
								
								if (waitForResponse) {
									
									responseQueue.put(message);
									
								} else {
									
									LOGGER.info("RECEIVED " + message.getCommand());
									
								}
								
							}
							
						}
						
					
				}
			
			} catch (Exception ex) {
				
				LOGGER.error("Exception", ex);
				
			}
			
		}
		
	}

}
