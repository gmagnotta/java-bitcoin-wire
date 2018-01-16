package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.utils.Utils;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitcoinPeerManagerImpl implements BitcoinPeerCallback, BitcoinPeerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinPeerManagerImpl.class);
	
	private static final int MAX_PEERS_CONNECTED =  1;
	
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
			
//			// Send our block list: only 24 known blocks
//			
//			long lastKnownIndex = blockChain.getLastKnownIndex();
//			
//			BitcoinHeadersMessage headers;
//			
//			if (lastKnownIndex == 0) {
//				
//				headers = new BitcoinHeadersMessage(blockChain.getBlockHeaders(0, 1));
//
//			} else {
//				
//				long start = lastKnownIndex - 24;
//				
//				headers = new BitcoinHeadersMessage(blockChain.getBlockHeaders(start, 24));
//				
//			}
			
			BitcoinHeadersMessage headers = new BitcoinHeadersMessage(new ArrayList<BlockHeader>());
			
			try {
				
				bitcoinPeer.sendHeaders(headers);
				
			} catch (Exception e) {
				
				LOGGER.error("Exception", e);
				
			}
			
			// we can use the one provided by the peer to check if he has something better than us (or maybe a new
			// fork appeared)
			
		} else if (bitcoinMessage.getCommand().equals(BitcoinCommand.ADDR)) {
			
//			BitcoinAddrMessage addrMessage = (BitcoinAddrMessage) bitcoinMessage;
//			
//			for (NetworkAddress networkAddress : addrMessage.getNetworkAddress()) {
//				
//				if (peers.size() < MAX_PEERS_CONNECTED && !isConnected(peers, networkAddress.getInetAddress())) {
//					
//					LOGGER.info("Opening connection with {} ", bitcoinMessage);
//					
//					openConnection(networkAddress, this);
//					
//				}
//				
//			}
			
		}
	}

	@Override
	public void connect(String address, int port) throws Exception {
		
		Socket socket = new Socket(address, port);
		
		BitcoinPeerImpl bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, this, blockChain);
		
		peers.add(bitcoinClient);
		
		while (blockChain.getLastKnownIndex() < bitcoinClient.getBlockStartHeight()) {
		
			List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
			
			hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlock((int) blockChain.getLastKnownIndex())));
			
			BitcoinGetHeadersMessage bitcoinGetHeadersMessage = new BitcoinGetHeadersMessage(70012, hashes);
			
			BitcoinHeadersMessage bitcoinHeaders = bitcoinClient.sendGetHeaders(bitcoinGetHeadersMessage);
			
			LOGGER.info("Peer {} returned {} headers!", bitcoinClient, bitcoinHeaders.getHeaders().size());
			
			for (BlockHeader b : bitcoinHeaders.getHeaders()) {
	//			
	//			LOGGER.info("Read {}", b.toString());
	//			
	//			LOGGER.info("Block difficulty is valid: {}", Utils.isShaMatchesTarget(Utils.computeBlockHeaderHash(b), (int) b.getBits()));
				
				if (Utils.isShaMatchesTarget(Utils.computeBlockHeaderHash(b), (int) b.getBits())) {
					
					blockChain.addBlockHeader(b);
				
				} else {
					
					LOGGER.error("Block hash doesn't match target: {}", b);
					
				}
				
				
			}
		
		}
		
		System.out.println("Best chain: " + blockChain);
		
//		BlockHeader l = bitcoinHeaders.getHeaders().get(1999);
//		
//		List<Sha256Hash> s = new ArrayList<Sha256Hash>();
//		
//		s.add(Utils.computeBlockHeaderHash(l));
//		
//		BitcoinGetHeadersMessage bitcoinGetHeadersMessage2 = new BitcoinGetHeadersMessage(70012, s);
//		
//		bitcoinHeaders = bitcoinClient.sendGetHeaders(bitcoinGetHeadersMessage2);
//		
//		LOGGER.info("Peer {} returned {} headers!", bitcoinClient, bitcoinHeaders.getHeaders().size());
//		
//		for (BlockHeader b : bitcoinHeaders.getHeaders()) {
//			
//			LOGGER.info("Read {}", b.toString());
//			
//			LOGGER.info("Block difficulty is valid: {}", Utils.isShaMatchesTarget(Utils.computeBlockHeaderHash(b), (int) b.getBits()));
//			
//		}
		
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
					
					// We want to send last 24 hashes.
					
					long lastKnownBlock = blockChain.getLastKnownIndex();
					
					List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
					
					if (lastKnownBlock == 0) {
						
						hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlock(0)));
						
					} else {

						// TODO add last known hashes
						
					}
					
					BitcoinGetHeadersMessage bitcoinGetHeadersMessage = new BitcoinGetHeadersMessage(70012, hashes);
					
					BitcoinHeadersMessage bitcoinHeaders = bitcoinClient.sendGetHeaders(bitcoinGetHeadersMessage);
					
					LOGGER.info("Peer {} returned {} headers!", bitcoinClient, bitcoinHeaders.getHeaders().size());
					
					for (BlockHeader b : bitcoinHeaders.getHeaders()) {
						
						LOGGER.info("Read {}", b.toString());
						
						LOGGER.info("Block difficulty is valid: {}", Utils.isShaMatchesTarget(Utils.computeBlockHeaderHash(b), (int) b.getBits()));
						
					}
				
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
