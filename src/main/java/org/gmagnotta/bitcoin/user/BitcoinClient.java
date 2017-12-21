package org.gmagnotta.bitcoin.user;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.parser.BitcoinFrameParserStream;
import org.gmagnotta.bitcoin.user.state.ConnectingState;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinClient.class);

	private String host;
	private int port;
	private Socket clientSocket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BitcoinFrameParserStream parser;
	private MagicVersion magicVersion;
	private ClientState clientState;
	private Thread reader, writer, message;
	
	private final LinkedBlockingQueue<BitcoinMessage> inputQueue;
	private final LinkedBlockingQueue<BitcoinMessage> outputQueue;

	public BitcoinClient(MagicVersion magicVersion, String host, int port) {
		
		this.magicVersion = magicVersion;
		this.host = host;
		this.port = port;
		this.inputQueue = new LinkedBlockingQueue<BitcoinMessage>();
		this.outputQueue = new LinkedBlockingQueue<BitcoinMessage>();
		this.clientState = new ConnectingState(new ClientContext() {
			
			@Override
			public void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {
				BitcoinClient.this.writeMessage(bitcoinMessage);
				
			}
			
			@Override
			public void setNextState(ClientState clientState) {
				BitcoinClient.this.setNextState(clientState);
				
			}
		});

	}

	public void connect() throws Exception {

		clientSocket = new Socket(host, port);

		outputStream = clientSocket.getOutputStream();

		inputStream = clientSocket.getInputStream();

		parser = new BitcoinFrameParserStream(magicVersion, inputStream);
		
		reader = new Thread(new ReaderRunnable(parser), "reader");
		
		reader.start();
		
		writer = new Thread(new WriterRunnable(), "writer");
		
		writer.start();
		
		message = new Thread(new MessageRunnable(), "message");
		
		message.start();
		
		NetworkAddress emitting = new NetworkAddress(0, new BigInteger("1"), InetAddress.getByAddress(new byte[] {0, 0, 0, 0}), clientSocket.getLocalPort());
		
		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("1"), InetAddress.getByName(host), port);

		BitcoinVersionMessage versionMessage = new BitcoinVersionMessage(70012L, new BigInteger("1"), new BigInteger("" + System.currentTimeMillis() / 1000), receiving, emitting, new BigInteger("123"), "/BitcoinPeppe:0.0.1/", 0, false);
		
		// SEND VERSION
		writeMessage(versionMessage);
		
	}

	private void writeMessage(BitcoinMessage bitcoinMessage) throws Exception {

		outputQueue.put(bitcoinMessage);
			
	}
	
	public void disconnect() throws Exception {
		
		reader.interrupt();
		
		writer.interrupt();
		
		message.interrupt();
		
		clientSocket.close();
		
	}
	
	/**
	 * This runnable read a frame form inputstream and put it into the input queue
	 */
	private class ReaderRunnable implements Runnable {

		private BitcoinFrameParserStream bitcoinFrameParserStream;
		
		public ReaderRunnable(BitcoinFrameParserStream bitcoinFrameParserStream) {
			this.bitcoinFrameParserStream = bitcoinFrameParserStream;
		}
		
		@Override
		public void run() {
				
			while (!Thread.currentThread().isInterrupted()) {

				try {
				
					BitcoinFrame frame = bitcoinFrameParserStream.getBitcoinFrame();
				
					inputQueue.put(frame.getPayload());
					
				} catch (InterruptedException ex) {
					
					LOGGER.info("Interrupting...");
					
					break;
					
				}  catch (Exception ex) {
					
					LOGGER.error("Exception", ex);
					
				}
					
			}
			
		}
		
	}
	
	/**
	 * This runnable will read from the output queue bitcoinmessages, then wrap into a frame and send to outputstream
	 */
	private class WriterRunnable implements Runnable {
		
		private BitcoinFrameBuilder builder;
		
		public WriterRunnable() {
			
			this.builder = new BitcoinFrameBuilder();
			
		}

		@Override
		public void run() {
			
			while (!Thread.currentThread().isInterrupted()) {

				try {
			
					BitcoinMessage outputMessage = outputQueue.take();

					BitcoinFrame frame = builder.setMagicVersion(magicVersion).setBitcoinMessage(outputMessage).build();

					outputStream.write(BitcoinFrame.serialize(frame));
				
				} catch (InterruptedException ex) {
					
					LOGGER.info("Interrupting...");
					
					break;
					
				} catch (Exception ex) {
					
					LOGGER.error("Exception", ex);
					
				}

			}
			
		}
		
	}
	
	private class MessageRunnable implements Runnable {

		@Override
		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				try {

					BitcoinMessage bitcoinMessage = inputQueue.take();
					
					getCurrentState().onMessageReceived(bitcoinMessage);
					
				} catch (InterruptedException ex) {
					
					LOGGER.info("Interrupting...");
					
					break;
					
				} catch (Exception ex) {
					
					LOGGER.error("Exception", ex);
					
				}
			
			}
			
		}
		
	}

	public synchronized void setNextState(ClientState clientState) {
		this.clientState = clientState;
	}
	
	private synchronized ClientState getCurrentState() {
		return clientState;
	}

}
