package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;
import org.gmagnotta.bitcoin.blockchain.BlockChain;
import org.gmagnotta.bitcoin.message.impl.BlockMessage;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.parser.script.BitcoinScriptParserStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.Arrays;

public class TransactionValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionValidator.class);
	
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
		
		LOGGER.debug("Validating tx {}", transaction);
		
		List<TransactionInput> txInputs = transaction.getTransactionInput();
		
		BigInteger totalInputs = BigInteger.ZERO;
		
		/*
		 * We check all transaction inputs
		 */
		for (int index = 0; index < txInputs.size(); index++) {
			
			final TransactionInput txInput = txInputs.get(index);
			
			final int indexCopy = index;
			
			if ("0000000000000000000000000000000000000000000000000000000000000000".equals(txInput.getPreviousOutput().getHash().toString())) {
				return true;
			}
			
			// 
			LOGGER.debug("Check that input is not already spent in persisted BC");
			if (blockChain.isTransactionInputAlreadySpent(txInput, blockMessage.getBlockHeader().getPrevBlock())) {
				
				throw new Exception("Transaction input already spent!");
				
			}
			
			// Check that input is not already spent in persisted BC in receiving block
			
			LOGGER.debug("Fetch input transaction");
			Transaction txPrev = blockChain.getTransaction(txInput.getPreviousOutput().getHash().toString());
			
			if (txPrev == null) {
				
				// search transaction in block
				txPrev = blockMessage.getIndexedTxns().get(txInput.getPreviousOutput().getHash().getReversed());
				
				if (txPrev == null) throw new Exception("cannot find " + txInput.getPreviousOutput().getHash().toString());
			}
			
			final TransactionOutput previousOut = txPrev.getTransactionOutput().get((int) txInput.getPreviousOutput().getIndex());
			
			// Sum partial input
			totalInputs = totalInputs.add(previousOut.getValue());
			
			// To verify a transaction, the scriptSig executed followed by the scriptPubKey
			byte[] b = Arrays.concatenate(txInput.getScriptSig(), previousOut.getScriptPubKey());
			
			BitcoinScriptParserStream bitcoinScriptParserStream = new BitcoinScriptParserStream(new ByteArrayInputStream(b));
			
			LOGGER.debug("Parsing script");
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
			
			LOGGER.debug("Executing script");
			for (ScriptElement scriptElement : script.getElements()) {
				
				transactionValidatorStatus.executeScript(scriptElement, stack, scriptContext);
				
			}
			
			LOGGER.debug("Done executing");
			
			// False is zero or negative zero (using any number of bytes) or an empty array, and True is anything else.
			// A transaction is valid if nothing in the combined script triggers failure and the top stack item is True (non-zero) when the script exits
			
			if (stack.isEmpty()) return false;
			
			if (ArrayUtils.isEmpty(stack.pop())) {
				return false;
			}
			
		}
		
		// Sum all outputs
		List<TransactionOutput> txOutputs = transaction.getTransactionOutput();
		
		BigInteger totalOutputs = BigInteger.ZERO;
		
		LOGGER.debug("Sum all outputs");
		for (int index = 0; index < txOutputs.size(); index++) {
			
			totalOutputs = totalOutputs.add(txOutputs.get(index).getValue());
			
		}
		
		// Tx is valid if outputs are less or equal to input
		return (totalOutputs.compareTo(totalInputs) <= 0);
		
	}

}
