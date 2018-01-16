package org.gmagnotta.bitcoin;

import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.BlockChainImpl;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManager;
import org.gmagnotta.bitcoin.peer.BitcoinPeerManagerImpl;
import org.gmagnotta.bitcoin.wire.MagicVersion;
import org.gmagnotta.log.LogLevel;
import org.gmagnotta.log.impl.system.ConsoleLogEventWriter;

public class Main {

	public static void main(String[] args) throws Exception {
		
		org.gmagnotta.log.LogEventCollector.getInstance().setLogLevelThreshold(LogLevel.INFO);
		
		org.gmagnotta.log.LogEventCollector.getInstance().addLogEventWriter(new ConsoleLogEventWriter());
		
		BlockChain bestChain = new BlockChainImpl();
		
		final BitcoinPeerManager bitcoinPeerManager = new BitcoinPeerManagerImpl(MagicVersion.TESTNET3, bestChain);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					bitcoinPeerManager.listen(4000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}, "bitcoinPeerManagerListener").start();
		
//		bitcoinPeerManager.connect("52.167.211.151", 19000);
		
		bitcoinPeerManager.connect("127.0.0.1", 18333);
//		
//		for (BitcoinPeer p : bitcoinPeerManager.getConnectedPeers()) {
//
//			long nonce = System.currentTimeMillis();
//			
//			BitcoinPingMessage bitcoinPingMessage = new BitcoinPingMessage(new BigInteger("" + nonce));
//			
//			BitcoinPongMessage pong = p.sendPing(bitcoinPingMessage);
//			
//		}
		
		System.in.read();
		
	}
	
}
