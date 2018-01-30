package org.gmagnotta.bitcoin.peer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;

import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinBlockMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetDataMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVerackMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinVersionMessage;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.parser.BitcoinFrameParserStream;
import org.gmagnotta.bitcoin.parser.EndOfStreamException;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.BitcoinFrame;
import org.gmagnotta.bitcoin.wire.BitcoinFrame.BitcoinFrameBuilder;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.bitcoin.wire.exception.BitcoinFrameBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinPeerImpl implements BitcoinPeer {

	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinPeerImpl.class);

	private Socket socket;
	private MagicVersion magicVersion;
	private Thread receiver;
	private BitcoinPeerCallback bitcoinPeerManagerCallbacks;
	private BitcoinFrameParserStream bitcoinFrameParserStream;

	private BigInteger nodeServices;
	private String userAgent;
	private long startHeight;
	private BlockChain blockChain;
	private ResponseManager responseManager;
	
	public BitcoinPeerImpl(MagicVersion magicVersion, Socket socket, BitcoinPeerCallback bitcoinPeerManagerCallbacks,
			BlockChain blockChain)
			throws Exception {

		this.magicVersion = magicVersion;
		this.socket = socket;
		this.bitcoinFrameParserStream = new BitcoinFrameParserStream(magicVersion, socket.getInputStream());
		this.bitcoinPeerManagerCallbacks = bitcoinPeerManagerCallbacks;
		this.blockChain = blockChain;
		this.responseManager = new ResponseManager();

		connect();

	}

	private void connect() throws Exception {

		NetworkAddress emitting = new NetworkAddress(0, new BigInteger("1"),
				InetAddress.getByAddress(new byte[] { 0, 0, 0, 0 }), socket.getLocalPort());

		NetworkAddress receiving = new NetworkAddress(0, new BigInteger("1"), socket.getInetAddress(),
				socket.getPort());

		BitcoinVersionMessage versionMessage = new BitcoinVersionMessage(70012L, new BigInteger("1"),
				new BigInteger("" + System.currentTimeMillis() / 1000), receiving, emitting, new BigInteger("123"),
				"/BitcoinPeppe:0.0.1/", blockChain.getBestChainLenght(), false);

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
		receiver = new Thread(new ReaderRunnable(bitcoinFrameParserStream), "receiver-" + socket.getInetAddress());

		receiver.start();

	}

	@Override
	public void disconnect() throws Exception {

		if (receiver.isAlive()) {
			receiver.interrupt();
		}
		
		if (!socket.isClosed()) {
			socket.close();
		}

	}

	@Override
	public void sendAddrMessage(BitcoinAddrMessage bitcoinAddrMessage) throws Exception {
		// clientState.sendAddrMessage(bitcoinAddrMessage);

	}

	@Override
	public BitcoinPongMessage sendPing(BitcoinPingMessage bitcoinPingMessage) throws Exception {

		return (BitcoinPongMessage) sendRecvMessage(bitcoinPingMessage, BitcoinCommand.PONG, 60000);

	}

	@Override
	public void sendPong(BitcoinPongMessage bitcoinPongMessage) throws Exception {
		
		sendMessage(bitcoinPongMessage);
		
	}
	
	@Override
	public BitcoinAddrMessage sendGetAddrMessage(BitcoinGetAddrMessage bitcoinGetAddrMessage) throws Exception {

		return (BitcoinAddrMessage) sendRecvMessage(bitcoinGetAddrMessage, BitcoinCommand.ADDR, 60000);
		
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
	
	private void processReceivedMessage(BitcoinMessage bitcoinMessage) {

		if (!responseManager.responseArrived(bitcoinMessage)) {
			
			bitcoinPeerManagerCallbacks.onMessageReceived(bitcoinMessage, this);
			
		}

	}
	
	private void sendMessage(BitcoinMessage bitcoinMessage) throws Exception {
		
		BitcoinFrameBuilder builder = new BitcoinFrameBuilder();

		BitcoinFrame frame = builder.setMagicVersion(magicVersion).setBitcoinMessage(bitcoinMessage).build();
		
		OutputStream outputStream = socket.getOutputStream();

		outputStream.write(BitcoinFrame.serialize(frame));
		
	}
	
	private BitcoinMessage sendRecvMessage(BitcoinMessage bitcoinMessage, BitcoinCommand expectedResponse, long timeout) throws Exception {
		
		sendMessage(bitcoinMessage);
		
		return responseManager.waitResponse(expectedResponse, timeout);
		
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

			while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {

				try {

					BitcoinFrame frame = bitcoinFrameParserStream.getBitcoinFrame();
					
					BitcoinMessage bitcoinMessage = frame.getPayload();
					
					processReceivedMessage(bitcoinMessage);

				} catch (EndOfStreamException ex) {
					
					LOGGER.error("EndOfStreamException", ex);
					
					bitcoinPeerManagerCallbacks.onConnectionClosed(BitcoinPeerImpl.this);
					
					break;
					
				} catch (IOException ex) {
					
					LOGGER.error("IOException", ex);
					
					if (socket.isClosed()) {
					
						bitcoinPeerManagerCallbacks.onConnectionClosed(BitcoinPeerImpl.this);
						
						break;

					}
					
				} catch (BitcoinFrameBuilderException ex) {

					LOGGER.error("BitcoinFrameBuilderException", ex);
					
				}

			}
			
		}

	}

	@Override
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	@Override
	public void sendHeaders(BitcoinHeadersMessage bitcoinHeadersMessage) throws Exception {
		
		sendMessage(bitcoinHeadersMessage);
		
	}

	@Override
	public BitcoinHeadersMessage sendGetHeaders(BitcoinGetHeadersMessage bitcoinGetHeadersMessage) throws Exception {

		return (BitcoinHeadersMessage) sendRecvMessage(bitcoinGetHeadersMessage, BitcoinCommand.HEADERS, 60000);
		
	}

	@Override
	public BitcoinBlockMessage sendGetData(BitcoinGetDataMessage bitcoinGetDataMessage) throws Exception {

		return (BitcoinBlockMessage) sendRecvMessage(bitcoinGetDataMessage, BitcoinCommand.BLOCK, 60000);
		
	}

}
