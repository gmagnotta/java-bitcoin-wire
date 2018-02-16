package org.gmagnotta.bitcoin.script;

import java.io.ByteArrayInputStream;
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
	public boolean isValid(Transaction transaction) throws Exception {
		
		for (TransactionInput i : transaction.getTransactionInput()) {
			
			Transaction previousTx = blockChain.getTransaction(i.getPreviousOutput().getHash().toString());
			
			TransactionOutput previousOut = previousTx.getTransactionOutput().get((int) i.getPreviousOutput().getIndex());
			
			byte[] b = Arrays.concatenate(i.getSignatureScript(), previousOut.getPkScript());
			
			BitcoinScriptParserStream bitcoinScriptParserStream = new BitcoinScriptParserStream(new ByteArrayInputStream(b), i);
			
			BitcoinScript script = bitcoinScriptParserStream.getBitcoinScript();
			
			for (ScriptItem scriptItem : script.getItems()) {
				
				scriptItem.doOperation(stack);
				
			}
		
			byte[] top = stack.pop();
			
			if (ArrayUtils.isEmpty(top)) {
				return false;
			}
			
		}
		
		return true;
		
	}

}
