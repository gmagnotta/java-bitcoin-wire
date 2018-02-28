package org.gmagnotta.bitcoin.parser.script;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Stack;

import org.gmagnotta.bitcoin.script.BitcoinPayloadScriptElementSerializer;
import org.gmagnotta.bitcoin.script.BitcoinScriptItemSerializer;
import org.gmagnotta.bitcoin.script.PayloadScriptElement;
import org.gmagnotta.bitcoin.script.ScriptContext;
import org.gmagnotta.bitcoin.script.ScriptElement;
import org.gmagnotta.bitcoin.script.impl.OpCheckSig;
import org.gmagnotta.bitcoin.script.impl.OpEqual;
import org.gmagnotta.bitcoin.script.impl.OpNumEqual;
import org.gmagnotta.bitcoin.script.impl.OpVerify;
import org.gmagnotta.bitcoin.utils.Sha256Hash;
import org.gmagnotta.bitcoin.utils.Utils;
import org.spongycastle.util.encoders.Hex;

/**
 * All valid opcodes that can be used in Bitcoin Script
 */
public enum OpCode {
	
	// CONSTANTS
	OP_FALSE((byte)0x00),
	
	NA_1((byte)0x01, true),
	NA_2((byte)0x02, true),
	NA_3((byte)0x03, true),
	NA_4((byte)0x04, true),
	NA_5((byte)0x05, true),
	NA_6((byte)0x06, true),
	NA_7((byte)0x07, true),
	NA_8((byte)0x08, true),
	NA_9((byte)0x09, true),
	NA_10((byte)0x0a, true),
	NA_11((byte)0x0b, true),
	NA_12((byte)0x0c, true),
	NA_13((byte)0x0d, true),
	NA_14((byte)0x0e, true),
	NA_15((byte)0x0f, true),
	NA_16((byte)0x10, true),
	NA_17((byte)0x11, true),
	NA_18((byte)0x12, true),
	NA_19((byte)0x13, true),
	NA_20((byte)0x14, true),
	NA_21((byte)0x15, true),
	NA_22((byte)0x16, true),
	NA_23((byte)0x17, true),
	NA_24((byte)0x18, true),
	NA_25((byte)0x19, true),
	NA_26((byte)0x1a, true),
	NA_27((byte)0x1b, true),
	NA_28((byte)0x1c, true),
	NA_29((byte)0x1d, true),
	NA_30((byte)0x1e, true),
	NA_31((byte)0x1f, true),
	NA_32((byte)0x20, true),
	NA_33((byte)0x21, true),
	NA_34((byte)0x22, true),
	NA_35((byte)0x23, true),
	NA_36((byte)0x24, true),
	NA_37((byte)0x25, true),
	NA_38((byte)0x26, true),
	NA_39((byte)0x27, true),
	NA_40((byte)0x28, true),
	NA_41((byte)0x29, true),
	NA_42((byte)0x2a, true),
	NA_43((byte)0x2b, true),
	NA_44((byte)0x2c, true),
	NA_45((byte)0x2d, true),
	NA_46((byte)0x2e, true),
	NA_47((byte)0x2f, true),
	NA_48((byte)0x30, true),
	NA_49((byte)0x31, true),
	NA_50((byte)0x32, true),
	NA_51((byte)0x33, true),
	NA_52((byte)0x34, true),
	NA_53((byte)0x35, true),
	NA_54((byte)0x36, true),
	NA_55((byte)0x37, true),
	NA_56((byte)0x38, true),
	NA_57((byte)0x39, true),
	NA_58((byte)0x3a, true),
	NA_59((byte)0x3b, true),
	NA_60((byte)0x3c, true),
	NA_61((byte)0x3d, true),
	NA_62((byte)0x3e, true),
	NA_63((byte)0x3f, true),
	NA_64((byte)0x40, true),
	NA_65((byte)0x41, true),
	NA_66((byte)0x42, true),
	NA_67((byte)0x43, true),
	NA_68((byte)0x44, true),
	NA_69((byte)0x45, true),
	NA_70((byte)0x46, true),
	NA_71((byte)0x47, true),
	NA_72((byte)0x48, true),
	NA_73((byte)0x49, true),
	NA_74((byte)0x4a, true),
	NA_75((byte)0x4b, true),
	
	OP_PUSHDATA1((byte)0x4c, true),
	OP_PUSHDATA2((byte)0x4d, true),
	OP_PUSHDATA4((byte)0x4e, true),
	OP_1NEGATE((byte)0x4f),
	OP_TRUE((byte)0x51),
	
	OP_2((byte)0x52),
	OP_3((byte)0x53),
	OP_4((byte)0x54),
	OP_5((byte)0x55),
	OP_6((byte)0x56),
	OP_7((byte)0x57),
	OP_8((byte)0x58),
	OP_9((byte)0x59),
	OP_10((byte)0x5a),
	OP_11((byte)0x5b),
	OP_12((byte)0x5c),
	OP_13((byte)0x5d),
	OP_14((byte)0x5e),
	OP_15((byte)0x5f),
	OP_16((byte)0x60),
	
	// FLOW CONTROL
	OP_NOP((byte)0x61),
	OP_IF((byte)0x63),
	OP_NOTIF((byte)0x64),
	OP_ELSE((byte)0x67),
	OP_ENDIF((byte)0x68),
	OP_VERIFY((byte)0x69),
	OP_RETURN((byte)0x6a),
	
	// STACK
	OP_TOALTSTACK((byte)0x6b),
	OP_FROMALTSTACK((byte)0x6c),
	OP_2DROP((byte)0x6d),
	OP_2DUP((byte)0x6e),
	OP_3DUP((byte)0x6f),
	OP_2OVER((byte)0x70),
	OP_2ROT((byte)0x71),
	OP_2SWAP((byte)0x72),
	OP_IFDUP((byte)0x73),
	OP_DEPTH((byte)0x74),
	OP_DROP((byte)0x75),
	OP_DUP((byte)0x76),
	OP_NIP((byte)0x77),
	OP_OVER((byte)0x78),
	OP_PICK((byte)0x79),
	OP_ROLL((byte)0x7a),
	OP_ROT((byte)0x7b),
	OP_SWAP((byte)0x7c),
	OP_TUCK((byte)0x7d),
	
	// SPLICE
//	OP_CAT((byte)0x7e),
//	OP_SUBSTR((byte)0x7f),
//	OP_LEFT((byte)0x80),
//	OP_RIGHT((byte)0x81),
	OP_SIZE((byte)0x82),
	
	// BITWISE LOGIC
//	OP_INVERT((byte)0x83),
//	OP_AND((byte)0x84),
//	OP_OR((byte)0x85),
//	OP_XOR((byte)0x86),
	OP_EQUAL((byte)0x87),
	OP_EQUALVERIFY((byte)0x88),
	
	// ARITHMETIC
	OP_1ADD((byte)0x8b),
	OP_1SUB((byte)0x8c),
//	OP_2MUL((byte)0x8d),
//	OP_2DIV((byte)0x8e),
	OP_NEGATE((byte)0x8f),
	OP_ABS((byte)0x90),
	OP_NOT((byte)0x91),
	OP_0NOTEQUAL((byte)0x92),
	OP_ADD((byte)0x93),
	OP_SUB((byte)0x94),
//	OP_MUL((byte)0x95),
//	OP_DIV((byte)0x96),
//	OP_MOD((byte)0x97),
//	OP_LSHIFT((byte)0x98),
//	OP_RSHIFT((byte)0x99),
	OP_BOOLAND((byte)0x9a),
	OP_BOOLOR((byte)0x9b),
	OP_NUMEQUAL((byte)0x9c),
	OP_NUMEQUALVERIFY((byte)0x9d),
	OP_NUMNOTEQUAL((byte)0x9e),
	OP_LESSTHAN((byte)0x9f),
	OP_GREATERTHAN((byte)0xa0),
	OP_LESSTHANOREQUAL((byte)0xa1),
	OP_GREATERTHANOREQUAL((byte)0xa2),
	OP_MIN((byte)0xa3),
	OP_MAX((byte)0xa4),
	OP_WITHIN((byte)0xa5),
	
	// CRYPTO
	OP_RIPEMD160((byte)0xa6),
	OP_SHA1((byte)0xa7),
	OP_SHA256((byte)0xa8),
	OP_HASH160((byte)0xa9),
	OP_HASH256((byte)0xaa),
	OP_CODESEPARATOR((byte)0xab),
	OP_CHECKSIG((byte)0xac),
	OP_CHECKSIGVERIFY((byte)0xad),
	OP_CHECKMULTISIG((byte)0xae),
	OP_CHECKMULTISIGVERIFY((byte)0xaf),
	
	// LOCKTIME
	OP_CHECKLOCKTIMEVERIFY ((byte)0xb1),
	OP_CHECKSEQUENCEVERIFY ((byte)0xb2);
	
	private byte value;
	private boolean requiresParameters;
	
	/**
	 * Construct a new OpCode without any required parameter
	 */
	private OpCode(byte value) {
		this(value, false);
	}

	/**
	 * Construct a new OpCode and specify if it requires parameters during parsing
	 */
	private OpCode(byte value, boolean hasParameters) {
		this.value = value;
		this.requiresParameters = hasParameters;
	}
	
	/** The map of all the opcodes */
	private static HashMap<Byte, OpCode> OPCODES_MAP = new HashMap<Byte, OpCode>();
	
	/* static block to initialize map for all elements */
	static {
		
		for (OpCode opcode : OpCode.values()) {
			
			OPCODES_MAP.put(opcode.getValue(), opcode);
			
		}
		
	}
	
	/**
	 * Retrieve the corresponding OpCode from the requested byte or throw an Exception if the opcode is not valid
	 * 
	 * @param value
	 * @return
	 * @throws Exception
	 */
	public static OpCode fromByte(byte value) throws Exception {
		
		OpCode opcode = OPCODES_MAP.get(value);
		
		if (opcode == null) throw new Exception("OpCode " + Hex.toHexString(new byte[] { value }) + " does not exists");
		
		return opcode;
		
	}
	
	/**
	 * Return the byte value of this OpCode
	 */
	public byte getValue() {
		return value;
	}

	/**
	 * Return true if the OpCode requires other parameters
	 */
	public boolean requiresParameters() {
		return requiresParameters;
	}
	
	public BitcoinScriptItemSerializer getSerializer() {
		switch (this) {
		case NA_1:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_2:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_3:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_4:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_5:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_6:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_7:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_8:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_9:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_10:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_11:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_12:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_13:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_14:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_15:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_16:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_17:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_18:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_19:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_20:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_21:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_22:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_23:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_24:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_25:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_26:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_27:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_28:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_29:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_30:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_31:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_32:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_33:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_34:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_35:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_36:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_37:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_38:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_39:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_40:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_41:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_42:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_43:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_44:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_45:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_46:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_47:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_48:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_49:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_50:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_51:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_52:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_53:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_54:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_55:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_56:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_57:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_58:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_59:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_60:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_61:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_62:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_63:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_64:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_65:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_66:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_67:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_68:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_69:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_70:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_71:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_72:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_73:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_74:
			return new BitcoinPayloadScriptElementSerializer();
		case NA_75:
			return new BitcoinPayloadScriptElementSerializer();
		case OP_PUSHDATA1:
			return new BitcoinScriptItemSerializer() {
				
				@Override
				public byte[] serialize(ScriptElement scriptElement) throws Exception {
					
					PayloadScriptElement payloadScriptElement = (PayloadScriptElement) scriptElement;
					
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					
					byteArrayOutputStream.write(scriptElement.getOpCode().value);
					byteArrayOutputStream.write((byte)payloadScriptElement.getPayload().length);
					byteArrayOutputStream.write(payloadScriptElement.getPayload());
					
					return byteArrayOutputStream.toByteArray();
					
				}
			};
		case OP_PUSHDATA2:
			return new BitcoinScriptItemSerializer() {
				
				@Override
				public byte[] serialize(ScriptElement scriptElement) throws Exception {
					
					PayloadScriptElement payloadScriptElement = (PayloadScriptElement) scriptElement;
					
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					
					byteArrayOutputStream.write(scriptElement.getOpCode().value);
					byteArrayOutputStream.write(org.gmagnotta.bitcoin.wire.Utils.writeInt16LE(payloadScriptElement.getPayload().length));
					byteArrayOutputStream.write(payloadScriptElement.getPayload());
					
					return byteArrayOutputStream.toByteArray();
					
				}
			};
		case OP_PUSHDATA4:
			return new BitcoinScriptItemSerializer() {
				
				@Override
				public byte[] serialize(ScriptElement scriptElement) throws Exception {
					
					PayloadScriptElement payloadScriptElement = (PayloadScriptElement) scriptElement;
					
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					
					byteArrayOutputStream.write(scriptElement.getOpCode().value);
					byteArrayOutputStream.write(org.gmagnotta.bitcoin.wire.Utils.writeInt32LE(payloadScriptElement.getPayload().length));
					byteArrayOutputStream.write(payloadScriptElement.getPayload());
					
					return byteArrayOutputStream.toByteArray();
					
				}
			};
		default:
			return new BitcoinScriptItemSerializer() {
				
				@Override
				public byte[] serialize(ScriptElement scriptElement) {
					
					return new byte[] { scriptElement.getOpCode().value };
					
				}
			};
		}
	}
	
	/**
	 * Return the corresponsing ScriptParserState for OpCodes that requires additional parameters
	 */
	public ScriptParserState getScriptParserState(Context context) {

		switch (this) {
		case NA_1:
			return new ReadDataState(context, this, 1);
		case NA_2:
			return new ReadDataState(context, this, 2);
		case NA_3:
			return new ReadDataState(context, this, 3);
		case NA_4:
			return new ReadDataState(context, this, 4);
		case NA_5:
			return new ReadDataState(context, this, 5);
		case NA_6:
			return new ReadDataState(context, this, 6);
		case NA_7:
			return new ReadDataState(context, this, 7);
		case NA_8:
			return new ReadDataState(context, this, 8);
		case NA_9:
			return new ReadDataState(context, this, 9);
		case NA_10:
			return new ReadDataState(context, this, 10);
		case NA_11:
			return new ReadDataState(context, this, 11);
		case NA_12:
			return new ReadDataState(context, this, 12);
		case NA_13:
			return new ReadDataState(context, this, 13);
		case NA_14:
			return new ReadDataState(context, this, 14);
		case NA_15:
			return new ReadDataState(context, this, 15);
		case NA_16:
			return new ReadDataState(context, this, 16);
		case NA_17:
			return new ReadDataState(context, this, 17);
		case NA_18:
			return new ReadDataState(context, this, 18);
		case NA_19:
			return new ReadDataState(context, this, 19);
		case NA_20:
			return new ReadDataState(context, this, 20);
		case NA_21:
			return new ReadDataState(context, this, 21);
		case NA_22:
			return new ReadDataState(context, this, 22);
		case NA_23:
			return new ReadDataState(context, this, 23);
		case NA_24:
			return new ReadDataState(context, this, 24);
		case NA_25:
			return new ReadDataState(context, this, 25);
		case NA_26:
			return new ReadDataState(context, this, 26);
		case NA_27:
			return new ReadDataState(context, this, 27);
		case NA_28:
			return new ReadDataState(context, this, 28);
		case NA_29:
			return new ReadDataState(context, this, 29);
		case NA_30:
			return new ReadDataState(context, this, 30);
		case NA_31:
			return new ReadDataState(context, this, 31);
		case NA_32:
			return new ReadDataState(context, this, 32);
		case NA_33:
			return new ReadDataState(context, this, 33);
		case NA_34:
			return new ReadDataState(context, this, 34);
		case NA_35:
			return new ReadDataState(context, this, 35);
		case NA_36:
			return new ReadDataState(context, this, 36);
		case NA_37:
			return new ReadDataState(context, this, 37);
		case NA_38:
			return new ReadDataState(context, this, 38);
		case NA_39:
			return new ReadDataState(context, this, 39);
		case NA_40:
			return new ReadDataState(context, this, 40);
		case NA_41:
			return new ReadDataState(context, this, 41);
		case NA_42:
			return new ReadDataState(context, this, 42);
		case NA_43:
			return new ReadDataState(context, this, 43);
		case NA_44:
			return new ReadDataState(context, this, 44);
		case NA_45:
			return new ReadDataState(context, this, 45);
		case NA_46:
			return new ReadDataState(context, this, 46);
		case NA_47:
			return new ReadDataState(context, this, 47);
		case NA_48:
			return new ReadDataState(context, this, 48);
		case NA_49:
			return new ReadDataState(context, this, 49);
		case NA_50:
			return new ReadDataState(context, this, 50);
		case NA_51:
			return new ReadDataState(context, this, 51);
		case NA_52:
			return new ReadDataState(context, this, 52);
		case NA_53:
			return new ReadDataState(context, this, 53);
		case NA_54:
			return new ReadDataState(context, this, 54);
		case NA_55:
			return new ReadDataState(context, this, 55);
		case NA_56:
			return new ReadDataState(context, this, 56);
		case NA_57:
			return new ReadDataState(context, this, 57);
		case NA_58:
			return new ReadDataState(context, this, 58);
		case NA_59:
			return new ReadDataState(context, this, 59);
		case NA_60:
			return new ReadDataState(context, this, 60);
		case NA_61:
			return new ReadDataState(context, this, 61);
		case NA_62:
			return new ReadDataState(context, this, 62);
		case NA_63:
			return new ReadDataState(context, this, 63);
		case NA_64:
			return new ReadDataState(context, this, 64);
		case NA_65:
			return new ReadDataState(context, this, 65);
		case NA_66:
			return new ReadDataState(context, this, 66);
		case NA_67:
			return new ReadDataState(context, this, 67);
		case NA_68:
			return new ReadDataState(context, this, 68);
		case NA_69:
			return new ReadDataState(context, this, 69);
		case NA_70:
			return new ReadDataState(context, this, 70);
		case NA_71:
			return new ReadDataState(context, this, 71);
		case NA_72:
			return new ReadDataState(context, this, 72);
		case NA_73:
			return new ReadDataState(context, this, 73);
		case NA_74:
			return new ReadDataState(context, this, 74);
		case NA_75:
			return new ReadDataState(context, this, 75);
		case OP_PUSHDATA1:
			return new IntermediateReadDataState(context, this, 1);
		case OP_PUSHDATA2:
			return new IntermediateReadDataState(context, this, 2);
		case OP_PUSHDATA4:
			return new IntermediateReadDataState(context, this, 4);
		default:
			return null;
		}
	}
	
	/**
	 * Returns the ScriptElement of the corresponding OpCode that does not requires parameters
	 */
	public ScriptElement getScriptElement() throws Exception {
		switch(this) {
		case OP_FALSE:
			return new PayloadScriptElement(this, new byte[] {}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_1NEGATE:
			return new PayloadScriptElement(this, new byte[] {(byte)-1}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_TRUE:
			return new PayloadScriptElement(this, new byte[] {(byte)1}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_2:
			return new PayloadScriptElement(this, new byte[] {(byte)2}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_3:
			return new PayloadScriptElement(this, new byte[] {(byte)3}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_4:
			return new PayloadScriptElement(this, new byte[] {(byte)4}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_5:
			return new PayloadScriptElement(this, new byte[] {(byte)5}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_6:
			return new PayloadScriptElement(this, new byte[] {(byte)6}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_7:
			return new PayloadScriptElement(this, new byte[] {(byte)7}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_8:
			return new PayloadScriptElement(this, new byte[] {(byte)8}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_9:
			return new PayloadScriptElement(this, new byte[] {(byte)9}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_10:
			return new PayloadScriptElement(this, new byte[] {(byte)10}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_11:
			return new PayloadScriptElement(this, new byte[] {(byte)11}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_12:
			return new PayloadScriptElement(this, new byte[] {(byte)12}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_13:
			return new PayloadScriptElement(this, new byte[] {(byte)13}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_14:
			return new PayloadScriptElement(this, new byte[] {(byte)14}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_15:
			return new PayloadScriptElement(this, new byte[] {(byte)15}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_16:
			return new PayloadScriptElement(this, new byte[] {(byte)16}) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					stack.push(getPayload());
				}
				
			};
		case OP_NOP:
			return new ScriptElement(this);
		case OP_IF:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.peek();
					
					BigInteger value = new BigInteger(top);
					
					if (!BigInteger.ZERO.equals(value)) {
						stack.pop();
					} else {
						throw new Exception("Transaction is invalid because top stack is zero");
					}
					
				}
				
			};
		case OP_NOTIF:
		case OP_ELSE:
		case OP_ENDIF:
			throw new Exception("Not yet implemented!");
		case OP_VERIFY:
			return new OpVerify(this);
		case OP_RETURN:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					throw new Exception("Transaction is invalid because OP_RETURN");
					
				}
				
			};
		case OP_EQUAL:
			return new OpEqual(this);
		case OP_EQUALVERIFY:
			return new OpEqual(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					super.doOperation(stack, scriptContext);
					
					// fake
					OpVerify opVerify = new OpVerify(OpCode.OP_VERIFY);
					opVerify.doOperation(stack, scriptContext);
					
				}
				
			};
		case OP_1ADD:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					BigInteger value = new BigInteger(top);
					
					value = value.add(BigInteger.ONE);
					
					stack.push(value.toByteArray());
					
				}
				
			};
		case OP_1SUB:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					BigInteger value = new BigInteger(top);
					
					value = value.subtract(BigInteger.ONE);
					
					stack.push(value.toByteArray());
					
				}
				
			};
		case OP_NEGATE:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					BigInteger value = new BigInteger(top);
					
					value = value.negate();
					
					stack.push(value.toByteArray());
					
				}
				
			};
		case OP_ABS:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					BigInteger value = new BigInteger(top);
					
					value = value.abs();
					
					stack.push(value.toByteArray());
					
				}
				
			};
		case OP_NOT:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					BigInteger value = new BigInteger(top);
					
					if (BigInteger.ZERO.equals(value) ||
							BigInteger.ONE.equals(value)) {
						value = value.negate();
						stack.push(value.toByteArray());
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_0NOTEQUAL:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					BigInteger value = new BigInteger(top);
					
					if (BigInteger.ZERO.equals(value)) {
						stack.push(new byte[] {0});
					} else {
						stack.push(new byte[] {1});
					}
					
				}
				
			};
		case OP_ADD:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					BigInteger valueA = new BigInteger(a);
					BigInteger valueB = new BigInteger(b);
					
					stack.push(valueA.add(valueB).toByteArray());
					
				}
				
			};
		case OP_SUB:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					BigInteger valueA = new BigInteger(a);
					BigInteger valueB = new BigInteger(b);
					
					stack.push(valueA.subtract(valueB).toByteArray());
					
				}
				
			};
		case OP_BOOLAND:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					String first = String.valueOf(a);
					String second = String.valueOf(b);
					
					if (!"".equals(first) &&
							!"".equals(second)) {
						stack.push(new byte[] {1});
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_BOOLOR:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					String first = String.valueOf(a);
					String second = String.valueOf(b);
					
					if (!"".equals(first) ||
							!"".equals(second)) {
						stack.push(new byte[] {1});
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_NUMEQUAL:
			return new OpNumEqual(this);
		case OP_NUMEQUALVERIFY:
			return new OpNumEqual(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					super.doOperation(stack, scriptContext);
					
					OpVerify opVerify = new OpVerify(OpCode.OP_VERIFY);
					opVerify.doOperation(stack, scriptContext);
					
				}
				
			};
		case OP_NUMNOTEQUAL:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					BigInteger first = new BigInteger(a);
					BigInteger second = new BigInteger(b);
					
					if (!first.equals(second)) {
						stack.push(new byte[] {1});
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_LESSTHAN:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					BigInteger first = new BigInteger(a);
					BigInteger second = new BigInteger(b);
					
					if (first.compareTo(second) < 0) {
						stack.push(new byte[] {1});
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_GREATERTHAN:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					BigInteger first = new BigInteger(a);
					BigInteger second = new BigInteger(b);
					
					if (first.compareTo(second) > 0) {
						stack.push(new byte[] {1});
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_LESSTHANOREQUAL:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					BigInteger first = new BigInteger(a);
					BigInteger second = new BigInteger(b);
					
					if (first.compareTo(second) <= 0) {
						stack.push(new byte[] {1});
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_GREATERTHANOREQUAL:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] a = stack.pop();
					byte[] b = stack.pop();
					
					BigInteger first = new BigInteger(a);
					BigInteger second = new BigInteger(b);
					
					if (first.compareTo(second) >= 0) {
						stack.push(new byte[] {1});
					} else {
						stack.push(new byte[] {0});
					}
					
				}
				
			};
		case OP_DUP:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.peek();
					stack.push(top);
					
				}
				
			};
		case OP_RIPEMD160:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					stack.push(Utils.hash160(top));
					
				}
				
			};
		case OP_SHA1:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					
					stack.push(Utils.sha1(top));
					
				}
				
			};
		case OP_SHA256:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					stack.push(Sha256Hash.of(top).getBytes());
					
				}
				
			};
		case OP_HASH160:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					Sha256Hash hash = Sha256Hash.of(top);
					
					stack.push(Utils.hash160(hash.getBytes()));
					
				}
				
			};
		case OP_HASH256:
			return new ScriptElement(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					byte[] top = stack.pop();
					Sha256Hash hash = Sha256Hash.twiceOf(top);
					
					stack.push(hash.getBytes());
					
				}
				
			};
		case OP_CODESEPARATOR:
			return new ScriptElement(this);
		case OP_CHECKSIG:
			return new OpCheckSig(this);
		case OP_CHECKSIGVERIFY:
			return new OpCheckSig(this) {
				
				@Override
				public void doOperation(Stack<byte[]> stack, ScriptContext scriptContext) throws Exception {
					
					// do OpChecksig
					super.doOperation(stack, scriptContext);
					
					// Create fake opVerify
					OpVerify opVerify = new OpVerify(OpCode.OP_VERIFY);
					opVerify.doOperation(stack, scriptContext);
				}
			};
			
		default: throw new Exception("Not yet implemented!");
		}
	}
	
}
