package org.gmagnotta.bitcoin.script.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Stack;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.gmagnotta.bitcoin.message.impl.Transaction;
import org.gmagnotta.bitcoin.message.impl.TransactionInput;
import org.gmagnotta.bitcoin.message.impl.TransactionOutput;
import org.gmagnotta.bitcoin.parser.script.BitcoinScriptParserStream;
import org.gmagnotta.bitcoin.parser.script.OpCode;
import org.gmagnotta.bitcoin.script.BitcoinScript;
import org.gmagnotta.bitcoin.script.BitcoinScriptSerializer;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.wire.serializer.impl.TransactionSerializer;
import org.spongycastle.util.Arrays;

public class OpCheckSig extends ScriptElement {

	public OpCheckSig(OpCode opCode) {
		super(opCode);
	}
	
	// check http://www.righto.com/2014/02/bitcoins-hard-way-using-raw-bitcoin.html
	// check https://en.bitcoin.it/w/images/en/7/70/Bitcoin_OpCheckSig_InDetail.png
	
	@Override
	public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
		
		// 1. the public key and the signature are popped from the stack
		byte[] pubKey = stack.pop();
		byte[] signature = stack.pop();
		
		if (signature.length == 0) {
			stack.push(new byte[] { 0 });
		}
		
		// 2. 
		byte[] b = scriptContext.getTransactionOutput().getScriptPubKey();
		
		// 4. remove op_codeseparator from subscript
		BitcoinScriptParserStream bitcoinScriptParserStream = new BitcoinScriptParserStream(new ByteArrayInputStream(b));
		
		BitcoinScript script = bitcoinScriptParserStream.getBitcoinScript();
		
		int index = script.lastIndexOf(new ScriptElement(OpCode.OP_CODESEPARATOR));
		byte[] subscript;
		if (index != -1) {
			
			subscript = new BitcoinScriptSerializer().serialize(script.subScript(index+1));
			
		} else {
			
			subscript = new BitcoinScriptSerializer().serialize(script);

		}
		
		// 5. extract hashtype from signature
		
		int hashTypeCode = signature[signature.length-1];
		signature = Arrays.copyOfRange(signature, 0, signature.length-1);
		
		// 6. copy transaction
		Transaction txCopy = org.gmagnotta.bitcoin.utils.Utils.cloneTransaction(scriptContext.getTransaction());
		
		// 7.set all txin scripts in txcopy to empty
		
		for (TransactionInput in : txCopy.getTransactionInput()) {
			in.setScriptSig(new byte[] {});
		}
		
		// 8. copy subscript into the current txin script
		TransactionInput txIn = txCopy.getTransactionInput().get((int) scriptContext.getIndex());

		txIn.setScriptSig(subscript);
		
		// Check hashType
		
		if (hashTypeCode == 2) {
			
			// SIGHASH_NONE
			
			txCopy.setTransactionOutput(new ArrayList<TransactionOutput>());
			
			for (int idx = 0; idx < txCopy.getTransactionInput().size(); idx++) {
				
				if (idx != scriptContext.getIndex()) {
				
					TransactionInput input = txCopy.getTransactionInput().get(idx);
					
					input.setSequence(0);
				
				}
				
			}
			
		} else if (hashTypeCode == 3) {
			
			// SIGHASH_SINGLE
			throw new Exception("SIGHASH_SINGLE not yet implemented!");
			
		} else if (hashTypeCode == 128) {
		
			// SIGHASH_ANYONECANPAY
			throw new Exception("SIGHASH_ANYONECANPAY not yet implemented!");
			
		}
		
		// SIGHASH_ALL
		
		// 9a. serialize txcopy
		byte[] txNewSerialized = new TransactionSerializer().serialize(txCopy);
		
		// 9b . append 4 bytes hashTypeCode
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(txNewSerialized);
		baos.write(org.gmagnotta.bitcoin.wire.Utils.writeInt32LE(hashTypeCode));
		
		byte[] toSign = baos.toByteArray();
		
		// 10. verify signature
		Sha256Hash twice = Sha256Hash.twiceOf(toSign);
		
		ECKey key = ECKey.fromPublicOnly(pubKey);
		
		boolean ok = key.verify(org.bitcoinj.core.Sha256Hash.wrap(twice.getBytes()), ECDSASignature.decodeFromDER(signature));
		
		if (ok) {
			stack.push(new byte[] { 1 });
		} else {
			stack.push(new byte[] { });
		}
		
	}

}
