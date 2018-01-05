package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinPeerManagerImpl implements BitcoinPeerCallback, BitcoinPeerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinPeerManagerImpl.class);
	
	private MagicVersion magicVersion;
	private List<BitcoinPeer> peers;
	
	public BitcoinPeerManagerImpl(MagicVersion magicVersion) {
		this.magicVersion = magicVersion;
		this.peers = new ArrayList<BitcoinPeer>();
	}

	@Override
	public void onMessageReceived(BitcoinMessage bitcoinMessage, BitcoinPeer bitcoinPeer) {

		LOGGER.info("onMessageReceived {} {}", bitcoinMessage, bitcoinPeer);
		
		if (bitcoinMessage.getCommand().equals(BitcoinCommand.PING)) {
			
			BigInteger nonce = ((BitcoinPingMessage) bitcoinMessage).getNonce();
			
			BitcoinPongMessage pong = new BitcoinPongMessage(nonce);
			
			try {
				
				bitcoinPeer.sendPong(pong);
				
			} catch (Exception e) {
				
				LOGGER.error("Exception", e);
				
			}

		} else if (bitcoinMessage.getCommand().equals(BitcoinCommand.GETHEADERS)) {
			
//			BitcoinHeadersMessage headers = new BitcoinHeadersMessage(headers);
			
		}
	}

	@Override
	public void connect(String address, int port) throws Exception {
		
		Socket socket = new Socket(address, port);
		
		BitcoinPeerImpl bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, this);
		
		peers.add(bitcoinClient);
		
	}
	
	@Override
	public List<BitcoinPeer> getConnectedPeers() {
		return peers;
	}

	@Override
	public void disconnect(BitcoinPeer bitcoinPeer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listen(int port) throws Exception {
		
		ServerSocket serverSocket = new ServerSocket(port);
		
		while (true) {
			
			Socket socket = serverSocket.accept();
			
			BitcoinPeerImpl bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, this);
			
			peers.add(bitcoinClient);
		
		}
		
	}

}
