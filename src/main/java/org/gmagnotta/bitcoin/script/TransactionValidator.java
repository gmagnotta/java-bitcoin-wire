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
import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;

public class TransactionValidator {
	
	private BlockChain blockChain;
	private Stack<byte[]> stack;
	private BlockMessage blockMessage;
	private TransactionValidatorStatus transactionValidatorStatus;
	
	public TransactionValidator(BlockChain blockChain, BlockMessage blockMessage) {
		this.blockChain = blockChain;
		this.blockMessage = blockMessage;
		this.stack = new Stack<byte[]>();
		this.transactionValidatorStatus = new SequentialTransactionValidatorStatus();
	}
	
	/**
	 * Execute the script and returns true if script is corrent, otherwise false
	 * @param script
	 * @return
	 * @throws Exception 
	 */
	public boolean isValid(final Transaction transaction) throws Exception {
		
		List<TransactionInput> txInputs = transaction.getTransactionInput();
		
		/*
		 * We check all transaction inputs
		 */
		for (int index = 0; index < txInputs.size(); index++) {
			
			final TransactionInput txInput = txInputs.get(index);
			
			final int indexCopy = index;
			
			if ("0000000000000000000000000000000000000000000000000000000000000000".equals(txInput.getPreviousOutput().getHash().toString())) {
				return true;
			}
			
			// Check that input is not already spent in persisted BC
			if (blockChain.isTransactionInputAlreadySpent(txInput)) {
				
				throw new Exception("Transaction input already spent!");
				
			}
			
			// Check that input is not already spent in persisted BC in receiving block
			
			// Fetch input transaction
			Transaction txPrev = blockChain.getTransaction(txInput.getPreviousOutput().getHash().toString());
			
			if (txPrev == null) {
				
				// search transaction in block
				txPrev = blockMessage.getIndexedTxns().get(txInput.getPreviousOutput().getHash().getReversed());
				
				if (txPrev == null) throw new Exception("cannot find " + txInput.getPreviousOutput().getHash().toString());
			}
			
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

				@Override
				public TransactionValidatorStatus getTransactionValidatorStatus() {
					return transactionValidatorStatus;
				}

				@Override
				public void setTransactionValidatorStatus(TransactionValidatorStatus transactionValidatorStatus) {
					TransactionValidator.this.transactionValidatorStatus = transactionValidatorStatus;
					
				}
				
			};
			
			for (ScriptElement scriptElement : script.getElements()) {
				
				transactionValidatorStatus.executeScript(scriptElement, stack, scriptContext);
				
			}
			
			// False is zero or negative zero (using any number of bytes) or an empty array, and True is anything else.
			// A transaction is valid if nothing in the combined script triggers failure and the top stack item is True (non-zero) when the script exits
			
			if (stack.isEmpty()) return false;
			
			if (ArrayUtils.isEmpty(stack.pop())) {
				return false;
			}
			
		}
		
		return true;
		
	}

}
