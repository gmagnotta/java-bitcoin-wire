import java.util.List;

import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.blockchain.ValidatedBlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockHeader;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.script.TransactionValidator;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.serializer.impl.TransactionDeserializedWrapper;
import org.gmagnotta.bitcoin.wire.serializer.impl.TransactionSerializer;
import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

public class ScriptEngineTest {

	@Test
	public void testEngine() throws Exception {
		
		final TransactionSerializer transactionSerializer = new TransactionSerializer();
		
		// Original tx
		final TransactionDeserializedWrapper transaction0 = transactionSerializer.deserialize(Hex.decode("0100000001b28b0c15ff67e3de8f01b9cc6e06379bfce8cb06e9e07e261256b074222b146d000000006b483045022100d2ab67b795bb2f3653e52482b4bf4abddb4749933aeb913125500ce520abf096022070e5bce199edf772c03d3357661389017c7c667814dd5cd61878269f65e358ec012102b82f8436cdb8ea68699dc60fd12f44473b8a261688c58236e66a662f00c65a0fffffffff0440420f00000000001976a914d6366a27b3e36dbb02b3da99f912294bcee61c7688ac40420f00000000001976a914216c3ba20b4df80814e6e30d31f54c865395587d88ac40420f00000000001976a914b9f5bf6cb6d78082b1b0e145a924740f1554c0af88ac30dc6c3b000000001976a9147c62e7f89f21633f4d5318087cfb06b6904f144388ac00000000"), 0, 0);
		
		// Spending signed
		final TransactionDeserializedWrapper transaction1 = transactionSerializer.deserialize(Hex.decode("010000000152fbf09171e6469e05247aff7848db279e710e0e39ce7fcb19289b6f7df270be010000006a4730440220303184757d2c1132b70d2763c73ca6b3111ed83654fd837fd1fe0bb98570663002201211d62bbd8a553e8334ef754e2756f059b57684233e02b0e3ac6f1ce584ad22012102aa884b5833dc5cca26abed63ebaaf8857f9bfba49d993dc636335ae710662acfffffffff0410270000000000001976a9148f41ad374f35de9799820b1a052916521a864f2f88ac0000000000000000536a4c50000000004000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000030750000000000001976a9148f41ad374f35de9799820b1a052916521a864f2f88acf07e0e00000000001976a91482b08870ae5b57ae5b76ead97a1388f390d7304788ac00000000"), 0, 0);
		
		// Spending unsigned
		final TransactionDeserializedWrapper transaction2 = transactionSerializer.deserialize(Hex.decode("010000000152fbf09171e6469e05247aff7848db279e710e0e39ce7fcb19289b6f7df270be0100000000ffffffff0310270000000000001976a9148f41ad374f35de9799820b1a052916521a864f2f88ac30750000000000001976a9148f41ad374f35de9799820b1a052916521a864f2f88acf07e0e00000000001976a91482b08870ae5b57ae5b76ead97a1388f390d7304788ac00000000"), 0, 0);
		
		final TransactionValidator scriptEngine = new TransactionValidator(new BlockChain() {
			
			@Override
			public Transaction getTransaction(String hash) {
				return transaction0.getTransaction();
			}
			
			@Override
			public List<Sha256Hash> getHashList(long index, long len) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public List<ValidatedBlockHeader> getBlockHeaders(long index, long len) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ValidatedBlockHeader getBlockHeader(String hash) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ValidatedBlockHeader getBlockHeader(int index) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public long getBestChainLenght() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public boolean addBlockHeader(BlockHeader header) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addBlock(BlockMessage blockMessage) {
				// TODO Auto-generated method stub
				return false;
			}
		}, new BlockMessage(null, null, null));

		//Assert.assertTrue(scriptEngine.isValid(transaction1.getTransaction()));
	}
}
