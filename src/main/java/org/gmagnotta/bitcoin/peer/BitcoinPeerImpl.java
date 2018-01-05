package org.gmagnotta.bitcoin.peer;

import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVerackMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.parser.BitcoinFrameParserStream;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinPeerImpl implements BitcoinPeer {

	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinPeerImpl.class);

	private Socket socket;
	private MagicVersion magicVersion;
	private Thread receiver;
	private BitcoinPeerCallback bitcoinPeerManagerCallbacks;
	private BitcoinFrameParserStream bitcoinFrameParserStream;
	private final Object syncObj;
	private BitcoinMessage receivedMessage;
	private BitcoinCommand waiting;

	private BigInteger nodeServices;
	private String userAgent;
	private long startHeight;
	
	public BitcoinPeerImpl(MagicVersion magicVersion, Socket socket, BitcoinPeerCallback bitcoinPeerManagerCallbacks)
			throws Exception {

		this.magicVersion = magicVersion;
		this.socket = socket;
		this.bitcoinFrameParserStream = new BitcoinFrameParserStream(magicVersion, socket.getInputStream());
		this.bitcoinPeerManagerCallbacks = bitcoinPeerManagerCallbacks;
		this.syncObj = new Object();

		connect();

	}

	private void connect() throws Exception {

		NetworkAddress emitting = new NetworkAddress(0, new BigInteger("1"),
				InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 }), socket.getLocalPort());

		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("1"), socket.getInetAddress(),
				socket.getPort());

		BitcoinVersionMessage versionMessage = new BitcoinVersionMessage(70012L, new BigInteger("1"),
				new BigInteger("" + System.currentTimeMillis() / 1000), receiving, emitting, new BigInteger("123"),
				"/BitcoinPeppe:0.0.1/", 0, false);

		synchronized (syncObj) {

			sendMessage(versionMessage);

			BitcoinFrame receivedFrame = bitcoinFrameParserStream.getBitcoinFrame();

			if (!receivedFrame.getCommand().equals(BitcoinCommand.VERSION)) {

				throw new Exception("Unexpected response!");

			}

			// Verify that version is compatible to our!

			// update peer data
			BitcoinVersionMessage bitcoinVersionMessage = (BitcoinVersionMessage) receivedFrame.getPayload();

			// initialize node info
			nodeServices = bitcoinVersionMessage.getServices();
			userAgent = bitcoinVersionMessage.getUserAgent();
			startHeight = bitcoinVersionMessage.getStartHeight();

			BitcoinVerackMessage verack = new BitcoinVerackMessage();

			sendMessage(verack);

			receivedFrame = bitcoinFrameParserStream.getBitcoinFrame();

			if (!receivedFrame.getCommand().equals(BitcoinCommand.VERACK)) {

				throw new Exception("Unexpected response!");

			}

			// handshake complete!

			// Start receiving thread
			receiver = new Thread(new ReaderRunnable(bitcoinFrameParserStream), "receiver");

			receiver.start();

		}

	}

	@Override
	public void disconnect() throws Exception {

		receiver.interrupt();
		
		socket.close();

	}

	@Override
	public void sendAddrMessage(BitcoinAddrMessage bitcoinAddrMessage) throws Exception {
		// clientState.sendAddrMessage(bitcoinAddrMessage);

	}

	@Override
	public BitcoinPongMessage sendPing(BitcoinPingMessage bitcoinPingMessage) throws Exception {

		sendMessage(bitcoinPingMessage);
		
		return (BitcoinPongMessage) waitResponse(BitcoinCommand.PONG, 10000);

	}

	@Override
	public void sendPong(BitcoinPongMessage bitcoinPongMessage) throws Exception {
		
		sendMessage(bitcoinPongMessage);
		
	}

	@Override
	public BigInteger getPeerServices() {
		
		return nodeServices;
		
	}

	@Override
	public String getUserAgent() {
		
		return userAgent;
		
	}

	@Override
	public long getBlockStartHeight() {
		
		return startHeight;
		
	}
	
	private BitcoinMessage waitResponse(BitcoinCommand command, long timeout) throws InterruptedException {

		synchronized (syncObj) {
			
			waiting = command;
			
			while (receivedMessage == null) {
				
				syncObj.wait(timeout);
				
			}
			
			return receivedMessage;
			
		}
		
	}

	private void processReceivedMessage(BitcoinMessage bitcoinMessage) {

		synchronized (syncObj) {

			if (waiting != null && waiting.equals(bitcoinMessage.getCommand())) {

				receivedMessage = bitcoinMessage;

				syncObj.notify();

			} else {

				bitcoinPeerManagerCallbacks.onMessageReceived(bitcoinMessage, this);

			}

		}

	}
	
	private void sendMessage(BitcoinMessage bitcoinMessage) throws Exception {
		
		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame frame = builder.setMagicVersion(magicVersion).setBitcoinMessage(bitcoinMessage).build();
		
		OutputStream outputStream = socket.getOutputStream();

		outputStream.write(BitcoinFrame.serialize(frame));
		
	}

	/**
	 * This runnable read a frame form inputstream and put it into the input
	 * queue
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

					BitcoinMessage bitcoinMessage = frame.getPayload();

					processReceivedMessage(bitcoinMessage);

				} catch (InterruptedException ex) {

					LOGGER.info("Interrupting...");

					break;

				} catch (Exception ex) {

					LOGGER.error("Exception", ex);

				}

			}

		}

	}

}
