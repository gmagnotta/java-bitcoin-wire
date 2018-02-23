package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.parser.script.BitcoinScriptParserStream;
import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.spongycastle.util.Arrays;

public class TransactionValidator {
	
	private BlockChain blockChain;
	private Stack<byte[]> stack;
	
	public TransactionValidator(BlockChain blockChain, BlockMessage blockMessage) {
		this.blockChain = blockChain;
		this.stack = new Stack<byte[]>();
	}
	
	/**
	 * Execute the script and returns true if script is corrent, otherwise false
	 * @param script
	 * @return
	 * @throws Exception 
	 */
	public boolean isValid(final Transaction transaction) throws Exception {
		
		List<TransactionInput> txInputs = transaction.getTransactionInput();
		
		for (int index = 0; index < txInputs.size(); index++) {
			
			final TransactionInput txInput = txInputs.get(index);
			
			final int indexCopy = index;
			
			Transaction txPrev = blockChain.getTransaction(txInput.getPreviousOutput().getHash().toString());
			
			final TransactionOutput previousOut = txPrev.getTransactionOutput().get((int) txInput.getPreviousOutput().getIndex());
			
			// To verify a transaction, the scriptSig executed followed by the scriptPubKey
			byte[] b = Arrays.concatenate(txInput.getScriptSig(), previousOut.getScriptPubKey());
			
			BitcoinScriptParserStream bitcoinScriptParserStream = new BitcoinScriptParserStream(new ByteArrayInputStream(b));
			
			BitcoinScript script = bitcoinScriptParserStream.getBitcoinScript();
			
			ScriptContext scriptContext = new ScriptContext() {
				
				@Override
				public TransactionOutput getTransactionOutput() {
					return previousOut;
				}
				
				@Override
				public TransactionInput getTransactionInput() {
					return txInput;
				}
				
				@Override
				public long getIndex() {
					return indexCopy;
				}

				@Override
				public Transaction getTransaction() {
					return transaction;
				}
				
			};
			
			for (ScriptItem scriptItem : script.getItems()) {
				
				scriptItem.doOperation(stack, scriptContext);
				
			}
		
			if (stack.isEmpty()) return false;
			
			if (ArrayUtils.isEmpty(stack.pop())) {
				return false;
			}
			
		}
		
		return true;
		
	}

}
