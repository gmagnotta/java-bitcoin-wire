package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bitcoinj.core.Sha256Hash;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.ValidatedBlockHeader;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinAddrMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.NetworkAddress;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class BitcoinPeerManagerImpl implements BitcoinPeerCallback, BitcoinPeerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinPeerManagerImpl.class);
	
	private static final int MAX_PEERS_CONNECTED =  4;
	
	private MagicVersion magicVersion;
	private List<BitcoinPeer> peers;
	private BlockChain blockChain;
	private final Object syncObj;
	private boolean isSyncing;
	private SecureRandom secureRandom;
	
	public BitcoinPeerManagerImpl(MagicVersion magicVersion, BlockChain blockChain) {
		this.magicVersion = magicVersion;
		this.peers = new ArrayList<BitcoinPeer>();
		this.blockChain = blockChain;
		this.isSyncing = false;
		this.secureRandom = new SecureRandom();
		this.syncObj = new Object();
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
				
				ValidatedBlockHeader blockHeader = blockChain.getBlockHeader(Hex.toHexString(hash.getBytes()));
				
				if (blockHeader != null) {
				
					lastKnownIndex = blockHeader.getNumber();
					
					break;
					
				}
				
			}
			
			List<ValidatedBlockHeader> list = blockChain.getBlockHeaders(lastKnownIndex + 1, 2000);
			
			// Loose subtype
			List<BlockHeader> genericList = new ArrayList<BlockHeader>(list);
			
			// send from next known block the list max 2000 values
			BitcoinHeadersMessage headers = new BitcoinHeadersMessage(genericList);
				
			try {
				
				bitcoinPeer.sendHeaders(headers);
				
			} catch (Exception e) {
				
				LOGGER.error("Exception", e);
				
			}
			
		} else if (bitcoinMessage.getCommand().equals(BitcoinCommand.ADDR)) {
			
			BitcoinAddrMessage addrMessage = (BitcoinAddrMessage) bitcoinMessage;
			
			int randomElement = ThreadLocalRandom.current().nextInt(addrMessage.getNetworkAddress().size());
			
			NetworkAddress networkAddress = addrMessage.getNetworkAddress().get(randomElement);
			
//			List<BitcoinPeer> connected = getConnectedPeers();
//			
//			if (connected.size() < MAX_PEERS_CONNECTED && !isConnected(connected, networkAddress.getInetAddress().getHostAddress())) {
//				
//				LOGGER.info("Opening connection with {} ", bitcoinMessage);
				
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
				
						openConnection(networkAddress.getInetAddress().getHostAddress(), networkAddress.getPort(), BitcoinPeerManagerImpl.this);
						
					}
					
				});
				
				t.start();
				
//			}
			
		}
	}

	private void syncBC(BitcoinPeer bitcoinPeer) throws Exception {
		
		long cycles = Math.round((bitcoinPeer.getBlockStartHeight() - blockChain.getBestChainLenght()) / 2000) + 1;
		
		LOGGER.info("Start sync");
		
		for (int i = 0; i < cycles; i++) {
		
			List<Sha256Hash> inverted = new ArrayList<Sha256Hash>();
			
			long lastKnownIndex = blockChain.getBestChainLenght();
			
			if (lastKnownIndex == 0) {
				
				inverted.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlockHeader(0)));
				
			} else if (lastKnownIndex < 20) {
				
				inverted.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(blockChain.getBlockHeader(0)));
				
				List<Sha256Hash> hashes = blockChain.getHashList(1, lastKnownIndex);
				
				for (Sha256Hash hash : hashes) {
					
					inverted.add(Sha256Hash.wrap(hash.getReversedBytes()));
					
				}

			} else {
				
				long start = (lastKnownIndex - 20) + 1;
				
				List<Sha256Hash> hashes = blockChain.getHashList(start, 20);
				
				for (Sha256Hash hash : hashes) {
					
					inverted.add(Sha256Hash.wrap(hash.getReversedBytes()));
					
				}

			}
			
			BitcoinGetHeadersMessage bitcoinGetHeadersMessage = new BitcoinGetHeadersMessage(70012, inverted);
			
			BitcoinHeadersMessage bitcoinHeaders = bitcoinPeer.sendGetHeaders(bitcoinGetHeadersMessage);
			
			LOGGER.info("Peer {} returned {} headers!", bitcoinPeer, bitcoinHeaders.getHeaders().size());
			
			for (BlockHeader b : bitcoinHeaders.getHeaders()) {
				
				blockChain.addBlockHeader(b);
				
			}
			
			LOGGER.info("Sync in progress {}%", (blockChain.getBestChainLenght()*100.0)/bitcoinPeer.getBlockStartHeight());

		}
		
		LOGGER.info("Done Sync {}", blockChain.getBestChainLenght());
			
	}
	
	@Override
	public synchronized List<BitcoinPeer> getConnectedPeers() {
		return peers;
	}
	
	private synchronized void addPeer(BitcoinPeer bitcoinPeer) {
		peers.add(bitcoinPeer);
	}
	
	private synchronized void removePeer(BitcoinPeer bitcoinPeer) {
		
		if (peers.contains(bitcoinPeer)) {
			
			peers.remove(bitcoinPeer);
			
		}
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
			
		} finally {
		
			removePeer(bitcoinPeer);

		}
		
	}
	
	@Override
	public void connect(String address, int port) throws Exception {
		
		openConnection(address, port, this);
			
	}
	
	private void openConnection(final String address, int port, final BitcoinPeerCallback callback) {
		
		if (!isConnected(getConnectedPeers(), address) && getConnectedPeers().size() < MAX_PEERS_CONNECTED) {
		
			BitcoinPeerImpl bitcoinClient = null;
			
			try {
				
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(address, port), 10000);
				
				bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, callback, blockChain);
				
				// signal that connection is established
				callback.onConnectionEstablished(bitcoinClient);
					
			} catch (Exception e) {
				
				LOGGER.error("Error", e);
				
			}
		
		} else {
			
			LOGGER.warn("Already connected to {}", address);
			
		}
				
	}
	
	private static boolean isConnected(List<BitcoinPeer> peers , String address) {
		
		String ip;
		
		try {
			InetAddress resolvedAddr = InetAddress.getByName(address);
			ip = resolvedAddr.getHostAddress();
		} catch (UnknownHostException ex) {
			return true;
		}

		for (BitcoinPeer peer : peers) {
			if (peer.getInetAddress().getHostAddress().equals(ip)) {
				return true;
			}
		}
		
		return false;
		
	}

	@Override
	public void onConnectionEstablished(BitcoinPeer bitcoinPeer) {
		
		addPeer(bitcoinPeer);
		
		//
		synchronized (syncObj) {
			
			if (isSyncing) {
				
				LOGGER.info("Sync already in progress. Skip");
				
				return;
				
			}
			
			// set we are syncing
			isSyncing = true;
			
		}
		
		try {
		
			if (blockChain.getBestChainLenght() < bitcoinPeer.getBlockStartHeight()) {
				
				syncBC(bitcoinPeer);
				
			}
		
		} catch (Exception ex) {
			
			LOGGER.error("Exception while sync", ex);
			
		} finally {
			
			synchronized (syncObj) {
				
				isSyncing = false;
				
			}
			
		}
		
	}

}
