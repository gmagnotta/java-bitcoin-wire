package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
import org.spongycastle.util.encoders.Hex;

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
			
			BitcoinGetHeadersMessage bitcoinGetHeadersMessage = (BitcoinGetHeadersMessage) bitcoinMessage;
			
			List<Sha256Hash> hashList = bitcoinGetHeadersMessage.getHash();
			
			long lastKnownIndex = 0;
			
			// find last common block. This can go back to genesis block
			for (Sha256Hash hash : hashList) {
				
				BlockHeader blockHeader = blockChain.getBlockHeader(Hex.toHexString(hash.getBytes()));
				
				if (blockHeader != null) {
				
					lastKnownIndex = blockChain.getIndexFromHash(Hex.toHexString(hash.getBytes()));
					
					break;
					
				}
				
			}
			
			// send from next known block the list max 2000 values
			BitcoinHeadersMessage headers = new BitcoinHeadersMessage(blockChain.getBlockHeaders(lastKnownIndex + 1, 2000));
				
			try {
				
				bitcoinPeer.sendHeaders(headers);
				
			} catch (Exception e) {
				
				LOGGER.error("Exception", e);
				
			}
			
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
		
		if (blockChain.getLastKnownIndex() == 0) {

			// we are just born: our blockchain is empty. we need to download all the blocks of the other peer
		
			LOGGER.info("This is the first blockchain sync. We need to download all peers' {} blocks", bitcoinClient.getBlockStartHeight());
			
			while (blockChain.getLastKnownIndex() < bitcoinClient.getBlockStartHeight()) {
			
				List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
				
				long lastKnownIndex = blockChain.getLastKnownIndex();
				
				if (lastKnownIndex == 0) {
					
					hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlockHeader(0)));
					
				} else if (lastKnownIndex < 100) {
					
					hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlockHeader(0)));
					
					hashes.addAll(blockChain.getHashList(1, lastKnownIndex));
	
				} else {
					
					long start = (lastKnownIndex - 100) + 1;
					
					hashes = blockChain.getHashList(start, 100);
	
				}
				
				// Reverse list!!!!
				Collections.reverse(hashes);
				
				BitcoinGetHeadersMessage bitcoinGetHeadersMessage = new BitcoinGetHeadersMessage(70012, hashes);
				
				BitcoinHeadersMessage bitcoinHeaders = bitcoinClient.sendGetHeaders(bitcoinGetHeadersMessage);
				
				LOGGER.info("Peer {} returned {} headers!", bitcoinClient, bitcoinHeaders.getHeaders().size());
				
				for (BlockHeader b : bitcoinHeaders.getHeaders()) {
					
					blockChain.addBlockHeader(b);
					
				}
				
				LOGGER.info("Best chain is now: " + blockChain.getLastKnownIndex() +  " " + (blockChain.getLastKnownIndex()*100.0)/bitcoinClient.getBlockStartHeight());

			}

			LOGGER.info("Best chain is now {}", blockChain.getLastKnownIndex());
			
		} else if (blockChain.getLastKnownIndex() < bitcoinClient.getBlockStartHeight()) {
			
			// Our blockchain is fewer than the peer's. We can be back or we can be on another chain (fork)
			
			List<Sha256Hash> hashes = new ArrayList<Sha256Hash>();
			
			long lastKnownIndex = blockChain.getLastKnownIndex();
			
			long start = (lastKnownIndex - 50) + 1;
			
			// Add block zero
			hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlockHeader(0)));
			
			// Add a random value between 1 and half 
			int randomNum = ThreadLocalRandom.current().nextInt(1, (int)(lastKnownIndex/2)-1);
			BlockHeader header = blockChain.getBlockHeader(randomNum);
			hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(header));
			
			// Add last 50 values
			header = blockChain.getBlockHeader((int) (lastKnownIndex/2));
			hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(header));
			
			hashes.addAll(blockChain.getHashList(start, 50));
			
			// Reverse list!!!!
			Collections.reverse(hashes);
			
			BitcoinGetHeadersMessage bitcoinGetHeadersMessage = new BitcoinGetHeadersMessage(70012, hashes);
			
			BitcoinHeadersMessage bitcoinHeaders = bitcoinClient.sendGetHeaders(bitcoinGetHeadersMessage);
			
			for (BlockHeader b : bitcoinHeaders.getHeaders()) {
				
				blockChain.addBlockHeader(b);
				
			}
			
			LOGGER.info("Best chain is now {}", blockChain.getLastKnownIndex());
			
		} else {
			
			LOGGER.info("Our chain is the same length of the peer. Doing nothing now");
			
		}
		
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
						
						hashes.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlockHeader(0)));
						
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
