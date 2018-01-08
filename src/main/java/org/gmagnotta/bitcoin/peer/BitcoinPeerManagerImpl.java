package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeaders;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinPeerManagerImpl implements BitcoinPeerCallback, BitcoinPeerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinPeerManagerImpl.class);
	
	private static final int MAX_PEERS_CONNECTED =  3;
	
	private MagicVersion magicVersion;
	private List<BitcoinPeer> peers;
	private BlockChain blockChain;
	
	public BitcoinPeerManagerImpl(MagicVersion magicVersion, BlockChain blockChain) {
		this.magicVersion = magicVersion;
		this.peers = new ArrayList<BitcoinPeer>();
		this.blockChain = blockChain;
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
			
			// Send our block list
			BitcoinHeadersMessage headers = new BitcoinHeadersMessage(blockChain.getBlockHeaders());
			
			try {
				
				bitcoinPeer.sendHeaders(headers);
				
			} catch (Exception e) {
				
				LOGGER.error("Exception", e);
				
			}
			
			// we can use the one provided by the peer to check if he has something better than us (or maybe a new
			// fork appeared)
			
			
			
			
		} else if (bitcoinMessage.getCommand().equals(BitcoinCommand.ADDR)) {
			
			BitcoinAddrMessage addrMessage = (BitcoinAddrMessage) bitcoinMessage;
			
			for (NetworkAddress networkAddress : addrMessage.getNetworkAddress()) {
				
				if (peers.size() < MAX_PEERS_CONNECTED && !isConnected(peers, networkAddress.getInetAddress())) {
					
					LOGGER.info("Opening connection with {} ", bitcoinMessage);
					
					openConnection(networkAddress, this);
					
				}
				
			}
			
		}
	}

	@Override
	public void connect(String address, int port) throws Exception {
		
		Socket socket = new Socket(address, port);
		
		BitcoinPeerImpl bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, this, blockChain);
		
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
			
			BitcoinPeerImpl bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, this, blockChain);
			
			peers.add(bitcoinClient);
		
		}
		
	}
	
	@Override
	public void onConnectionClosed(BitcoinPeer bitcoinPeer) {
		
		try {
			
			LOGGER.info("Disconnecting from {}", bitcoinPeer);
			bitcoinPeer.disconnect();
			
		} catch (Exception ex) {
			
			LOGGER.error("Exception while disconnecting", ex);
			
		}
		
		peers.remove(bitcoinPeer);
		
	}
	
	private void openConnection(final NetworkAddress networkAddress, final BitcoinPeerCallback callback) {
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
				
					Socket socket = new Socket(networkAddress.getInetAddress(), networkAddress.getPort());
					
					BitcoinPeerImpl bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, callback, blockChain);
					
					peers.add(bitcoinClient);
				
				} catch (Exception e) {
					
					LOGGER.error("Error", e);
					
				}
				
			}
		});
		
		t.start();
		
	}
	
	private static boolean isConnected(List<BitcoinPeer> peers , InetAddress inetaddress) {

		for (BitcoinPeer peer : peers) {
			if (peer.getInetAddress().equals(inetaddress)) {
				return true;
			}
		}
		
		return false;
		
	}

}
