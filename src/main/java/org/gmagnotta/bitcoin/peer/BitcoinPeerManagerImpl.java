package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
	private boolean isSyncing;
	
	public BitcoinPeerManagerImpl(MagicVersion magicVersion, BlockChain blockChain) {
		this.magicVersion = magicVersion;
		this.peers = new ArrayList<BitcoinPeer>();
		this.blockChain = blockChain;
		this.isSyncing = false;
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
			
			for (NetworkAddress networkAddress : addrMessage.getNetworkAddress()) {
				
				List<BitcoinPeer> connected = getConnectedPeers();
				
				if (connected.size() < MAX_PEERS_CONNECTED && !isConnected(connected, networkAddress.getInetAddress())) {
					
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
		
		addPeer(bitcoinClient);
		
		syncBC(bitcoinClient);
		
		LOGGER.info("Best chain is {}", blockChain.getBestChainLenght());
			
	}
	
	private void syncBC(BitcoinPeer bitcoinPeer) throws Exception {
		
		// if peer has more blocks than us
		if ((blockChain.getBestChainLenght() < bitcoinPeer.getBlockStartHeight()) && !isSynchInProgress()) {
			
			try {
				
				setSync(true);
			
				long cycles = Math.round((bitcoinPeer.getBlockStartHeight() - blockChain.getBestChainLenght()) / 2000) + 1;
				
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
					
					LOGGER.info("Sync in progress. Last seen: {} {}%", blockChain.getBestChainLenght(), (blockChain.getBestChainLenght()*100.0)/bitcoinPeer.getBlockStartHeight());
		
				}
			
			} finally {
				
				setSync(false);
				
			}
		
		}
	}
	
	private synchronized boolean isSynchInProgress() {
		return isSyncing;
	}
	
	private synchronized void setSync(boolean progress) {
		this.isSyncing = progress;
	}
	
	@Override
	public synchronized List<BitcoinPeer> getConnectedPeers() {
		return peers;
	}
	
	private synchronized void addPeer(BitcoinPeer bitcoinPeer) {
		peers.add(bitcoinPeer);
	}
	
	private synchronized void removePeer(BitcoinPeer bitcoinPeer) {
		peers.remove(bitcoinPeer);
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
	
	private void openConnection(final NetworkAddress networkAddress, final BitcoinPeerCallback callback) {
		
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				BitcoinPeerImpl bitcoinClient = null;
				
				try {
				
					Socket socket = new Socket(networkAddress.getInetAddress(), networkAddress.getPort());
					
					bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, callback, blockChain);
					
					addPeer(bitcoinClient);
					
					syncBC(bitcoinClient);

					LOGGER.info("Best chain is {}", blockChain.getBestChainLenght());
				
				} catch (Exception e) {
					
					LOGGER.error("Error", e);
					
					if (bitcoinClient != null)
						removePeer(bitcoinClient);
					
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
