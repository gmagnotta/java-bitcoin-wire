package org.gmagnotta.bitcoin.peer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.ValidatedBlockHeader;
import org.gmagnotta.bitcoin.message.BitcoinMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetDataMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinGetHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinHeadersMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinInvMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPingMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinPongMessage;
import org.gmagnotta.bitcoin.message.impl.BitcoinRejectMessage;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.InventoryVector;
import org.gmagnotta.bitcoin.message.impl.InventoryVector.Type;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.script.TransactionValidator;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.utils.Utils;
import org.gmagnotta.bitcoin.wire.BitcoinCommand;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public class BitcoinPeerManagerImpl implements BitcoinPeerCallback, BitcoinPeerManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BitcoinPeerManagerImpl.class);
	
	private MagicVersion magicVersion;
	private List<BitcoinPeer> peers;
	private BlockChain blockChain;
	private final Object syncObj;
	private boolean isSyncing;
	private final Timer inputTimer;
	private final int maxConnectedPeers;
	
	public BitcoinPeerManagerImpl(MagicVersion magicVersion, BlockChain blockChain, int maxConnectedPeers) {
		this.magicVersion = magicVersion;
		this.peers = new ArrayList<BitcoinPeer>();
		this.blockChain = blockChain;
		this.isSyncing = false;
		this.syncObj = new Object();
		this.inputTimer = new Timer();
		this.maxConnectedPeers = maxConnectedPeers;
	}

	@Override
	public void onMessageReceived(final BitcoinMessage bitcoinMessage, final BitcoinPeer bitcoinPeer) {

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
			
			// take snapshot of bestchain
			
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
			
			/*BitcoinAddrMessage addrMessage = (BitcoinAddrMessage) bitcoinMessage;
			
			int randomElement = ThreadLocalRandom.current().nextInt(addrMessage.getNetworkAddress().size());
			
			final NetworkAddress networkAddress = addrMessage.getNetworkAddress().get(randomElement);
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
			
					openConnection(networkAddress.getInetAddress().getHostAddress(), networkAddress.getPort(), BitcoinPeerManagerImpl.this);
					
				}
				
			});
			
			t.start();*/
			
		}  else if (bitcoinMessage.getCommand().equals(BitcoinCommand.INV)) {
			
			LOGGER.info("Received INV!");
			
			final BitcoinInvMessage invMessage = (BitcoinInvMessage) bitcoinMessage;
			
			Thread t = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					synchronized (syncObj) {
						
						if (isSyncing) {
							
							LOGGER.info("Sync already in progress. Skip");
							
							return;
							
						} else {
						
							// set we are syncing
							isSyncing = true;
						
						}
						
					}
						
					try {

						syncBC(bitcoinPeer);
					
					} catch (Exception ex) {
						
						LOGGER.error("Exception while sync", ex);
						
						onConnectionClosed(bitcoinPeer);
						
					} finally {
						
						synchronized (syncObj) {
							
							isSyncing = false;
							
						}
						
					}					
				}
			});
			
			t.start();
			
			/*if (org.gmagnotta.bitcoin.utils.Utils.isPeerNetworkNode(bitcoinPeer.getPeerServices())) {
				
				LOGGER.info("Downloading block!");
			
				BitcoinInvMessage bitcoinInvMessage = (BitcoinInvMessage) bitcoinMessage;
				
				BitcoinGetDataMessage bitcoinGetDataMessage = new BitcoinGetDataMessage(bitcoinInvMessage.getInventoryVectors());
				
				try {
				
					BitcoinBlockMessage block = bitcoinPeer.sendGetData(bitcoinGetDataMessage);
					
					LOGGER.info("Peer {} returned header!", bitcoinPeer);
					
					blockChain.addBlockHeader(block.getHeader());
						
				} catch (Exception ex) {
					
					LOGGER.error("Exception", ex);
					
				}
			
			} else {
				
				LOGGER.info("Peer doesn't allow to download!");
				
			}*/
			
		} else if (bitcoinMessage.getCommand().equals(BitcoinCommand.GETDATA)) {
			
			try {
				
				BitcoinGetDataMessage getData = (BitcoinGetDataMessage) bitcoinMessage;
				
				for (InventoryVector i : getData.getInventoryVectors()) {
					
					if (i.getType().equals(InventoryVector.Type.MSG_BLOCK)) {
						
						BlockMessage block = blockChain.getBlock(i.getHash().toString());
						
						bitcoinPeer.sendBlock(block);
						
					} else {
						
						bitcoinPeer.sendReject(new BitcoinRejectMessage("Error", (byte)0x10, "Not implemented", new byte[]{ }));

					}
					
				}
			
			} catch (Exception ex) {
				
				LOGGER.error("Exception", ex);
			}
			
		}
	}

	private void syncBC(BitcoinPeer bitcoinPeer) throws Exception {
		
		LOGGER.info("Start sync");
		
		// Calculate best chain
		
		List<Sha256Hash> inverted = new ArrayList<Sha256Hash>();
		
		Sha256Hash lastReceivedHash = null;
		
		long lastKnownIndex = blockChain.getBestChainLenght();
		
		if (lastKnownIndex == 0) {
			
			inverted.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(magicVersion.getBlockChainParameters().getGenesis()).getReversed());
			
		} else if (lastKnownIndex < 20) {
			
			List<Sha256Hash> hashes = blockChain.getHashList(1, lastKnownIndex);
			
			for (Sha256Hash hash : hashes) {
				
				inverted.add(hash);
				
			}
			
			// Reverse list!!!!
			Collections.reverse(inverted);
			
			inverted.add(org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(magicVersion.getBlockChainParameters().getGenesis()).getReversed());

		} else {
			
			long start = (lastKnownIndex - 20) + 1;
			
			List<Sha256Hash> hashes = blockChain.getHashList(start, 20);
			
			for (Sha256Hash hash : hashes) {
				
				inverted.add(hash);
				
			}
			
			// Reverse list!!!!
			Collections.reverse(inverted);
			
			hashes = blockChain.getHashList((lastKnownIndex / 5) * 4, 1);
			
			for (Sha256Hash hash : hashes) {
				
				inverted.add(hash);
				
			}
			
			hashes = blockChain.getHashList((lastKnownIndex / 5) * 3, 1);
			
			for (Sha256Hash hash : hashes) {
				
				inverted.add(hash);
				
			}
			
			hashes = blockChain.getHashList((lastKnownIndex / 5) * 2, 1);
			
			for (Sha256Hash hash : hashes) {
				
				inverted.add(hash);
				
			}
			
			hashes = blockChain.getHashList((lastKnownIndex / 5) * 1, 1);
			
			for (Sha256Hash hash : hashes) {
				
				inverted.add(hash);
				
			}
			
			// LAST ELEMENT
			hashes = blockChain.getHashList(0, 1);
			
			for (Sha256Hash hash : hashes) {
				
				inverted.add(hash);
				
			}
			
		}
		
		long receivedHeaders = 0;
		do {
			
			if (lastReceivedHash != null) {
				
				inverted = new ArrayList<Sha256Hash>();
				inverted.add(lastReceivedHash);
				
			}
			
			BitcoinGetHeadersMessage bitcoinGetHeadersMessage = new BitcoinGetHeadersMessage(70012, inverted);
			
			LOGGER.debug("Get header");
			BitcoinHeadersMessage bitcoinHeaders = bitcoinPeer.sendGetHeaders(bitcoinGetHeadersMessage);
			
			receivedHeaders = bitcoinHeaders.getHeaders().size();
			
			LOGGER.info("Peer {} returned {} headers!", bitcoinPeer, receivedHeaders);
			
			for (BlockHeader receivedHeader : bitcoinHeaders.getHeaders()) {
				
				blockChain.getTransactionManager().startTransaction();
				
				try {
					
					LOGGER.info("Managing block header {}", receivedHeader);
					
					// compute hash of received header
					Sha256Hash receivedHeaderHash = org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(receivedHeader);

					// Check if it is already present
					if (blockChain.getBlockHeaderFromAll(receivedHeaderHash.toReversedString()) != null) {

						throw new Exception("Blockchain already contains block " + receivedHeader);

					} 
					
					// Retrieve previous header referenced
					ValidatedBlockHeader previousHeader = blockChain.getBlockHeaderFromAll(receivedHeader.getPrevBlock().toString());
					
					if (previousHeader == null) {
						
						throw new Exception("BlockHeader " + receivedHeader + " references an unknown block " + receivedHeader.getPrevBlock().toString());
						
					}
					
					// we can now create auxiliary tables starting from previous block
					LOGGER.info("Creating aux tables");
					blockChain.createAuxiliaryTables(previousHeader.getHash());
					
					// Calculate 
					int currentTarget = (int) Utils.getNextWorkRequired(previousHeader.getNumber(), blockChain, receivedHeader, magicVersion.getBlockChainParameters());
					
					if (!Utils.isShaMatchesTarget(receivedHeaderHash, currentTarget)) {

						throw new Exception("Block Header " + receivedHeaderHash + " doesn't match expected target" + currentTarget);

					}
					
					// ok
					blockChain.insertHeader(receivedHeader, receivedHeaderHash.toReversedString(), previousHeader);
					LOGGER.info("Inserted header {}", receivedHeader);
					
					LOGGER.debug("Dowloading block");
					BlockMessage block = downloadBlocks(bitcoinPeer, receivedHeaderHash.getReversed());
					
					LOGGER.info("Starting calculating merkle tree");
					Sha256Hash calculatedMerkleRoot = Utils.calculateMerkleRootTransaction(block.getTxns()).getReversed();
					
					if (!calculatedMerkleRoot.equals(block.getBlockHeader().getMerkleRoot())) {
						throw new Exception("Calculated merkle root is different from the header! Skipping block");
					}
						
					LOGGER.info("Calculated {}", calculatedMerkleRoot);

					// check all txs...
					final TransactionValidator scriptEngine = new TransactionValidator(blockChain, block);
					
					LOGGER.info("Validating txs");
					for (Transaction tx : block.getTxns()) {
						
						if (!scriptEngine.isValid(tx)) {
							
							throw new Exception("tx is not valid: " + tx);
							
						}
						
					}
				
					LOGGER.info("OK, block valid!. Add block");
					blockChain.addBlock(block);
						
					// do commit
					blockChain.getTransactionManager().commitTransaction();
					
					lastReceivedHash = org.gmagnotta.bitcoin.utils.Utils.computeBlockHeaderHash(receivedHeader).getReversed(); 

					if (Thread.interrupted()) {
						LOGGER.warn("Interrupted!");
						return;
					}
					
				} catch (Exception ex) {
					
					LOGGER.error("Exception while adding block, rollback", ex);
					blockChain.getTransactionManager().rollbackTransaction();
					
					return;
					
				}
				
				// DELETE TEMP TABLE
				
			}
			
			LOGGER.info("Sync in progress {}%", (blockChain.getBestChainLenght()*100.0)/bitcoinPeer.getBlockStartHeight());
			
		} while (receivedHeaders != 0);

		LOGGER.info("Done Sync {}", blockChain.getBestChainLenght());
		
	}
	
	private BlockMessage downloadBlocks(BitcoinPeer bitcoinPeer, Sha256Hash hash) throws Exception {
		
		List<InventoryVector> list = new ArrayList<InventoryVector>();
		
		list.add(new InventoryVector(Type.MSG_BLOCK, hash));
		
		BitcoinGetDataMessage getdata = new BitcoinGetDataMessage(list);
		
		return bitcoinPeer.sendGetData(getdata);
		
	}
		
	private void addPeer(BitcoinPeer bitcoinPeer) {
		
		peers.add(bitcoinPeer);
		
	}
	
	private void removePeer(BitcoinPeer bitcoinPeer) {
		
		synchronized (syncObj) {
			
			if (peers.contains(bitcoinPeer)) {
				
				peers.remove(bitcoinPeer);
				
			}

		}
		
	}

	public void disconnect(BitcoinPeer bitcoinPeer) {
		
		try {
			
			bitcoinPeer.disconnect();
		
		} catch (Exception e) {
			
			LOGGER.error("Exception disconneting", e);
			
		}
		
	}

	@Override
	public void listen(int port) throws Exception {
		
		ServerSocket serverSocket = new ServerSocket(port);
		
		while (true) {
			
			try {
			
				Socket socket = serverSocket.accept();
				
				LOGGER.info("Received connection!");
				
				BitcoinPeerImpl bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, this, blockChain);
				
				peers.add(bitcoinClient);
			
			} catch (Exception ex) {
				
				LOGGER.error("Exception", ex);
				
			}
		
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
		
			synchronized (syncObj) {
				
				removePeer(bitcoinPeer);

			}

		}
		
	}
	
	private void openConnection(final String address, int port) {
		
		if (!isConnected(address)) {
		
			BitcoinPeerImpl bitcoinClient = null;
			
			try {
				
				Socket socket = new Socket();
				socket.setKeepAlive(true);
				socket.connect(new InetSocketAddress(address, port), 10000);
				
				bitcoinClient = new BitcoinPeerImpl(magicVersion, socket, this, blockChain);
				
				// signal that connection is established
				onConnectionEstablished(bitcoinClient);
					
			} catch (Exception e) {
				
				LOGGER.error("Error", e);
				
			}
		
		} else {
			
			LOGGER.warn("Already connected to {}", address);
			
		}
		
	}
	
	private boolean isConnected(String address) {
		
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
	public void onConnectionEstablished(final BitcoinPeer bitcoinPeer) {
		
		LOGGER.info("Connected with {} ", bitcoinPeer);
		
		//
		synchronized (syncObj) {

			addPeer(bitcoinPeer);
			
			if (isSyncing) {
				
				LOGGER.info("Sync already in progress. Skip");
				
				return;
				
			} else {
			
				// set we are syncing
				isSyncing = true;
			
			}
			
		}
			
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				try {
					
					if (blockChain.getBestChainLenght() < bitcoinPeer.getBlockStartHeight()) {
						
						LOGGER.info("The peer have a better chain lenght {} than our {}. Start sync", bitcoinPeer.getBlockStartHeight(),blockChain.getBestChainLenght());
						
						syncBC(bitcoinPeer);
						
					}
					
					if (blockChain.getBestChainLenght() < bitcoinPeer.getBlockStartHeight()) {
						
						throw new Exception("We need still to update!");
						
					}
					

				} catch (Exception ex) {
					
					LOGGER.error("Exception while sync", ex);
					
					onConnectionClosed(bitcoinPeer);
					
				} finally {
					
					synchronized (syncObj) {
					
						isSyncing = false;
					
					}
						
				}
				
			}

		}).start();
			
	}

	@Override
	public void start() throws Exception {

		inputTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {

				if ( !((peers.size() > 0) && (peers.size() <= maxConnectedPeers)) ) {
					
					LOGGER.info("Manager need other peers");
				
					int randomElement = ThreadLocalRandom.current().nextInt(magicVersion.getBlockChainParameters().getSeeds().length);
					
					try {
						
						LOGGER.info("Connecting to a peer");
						
						String seed = magicVersion.getBlockChainParameters().getSeeds()[randomElement];
						
						openConnection(seed, magicVersion.getBlockChainParameters().getPort());
						
					} catch (Exception e) {
						
						LOGGER.error("Exception connecting", e);
						
					}
				
				}
				
			}
			
		}, 0, 15000);
		
	}

	@Override
	public void stop() throws Exception {

		inputTimer.cancel();
		
		// for each connected peer, disconnect
		
		for (BitcoinPeer peer : peers) {
			
			peer.disconnect();
			
		}
		
	}

}
