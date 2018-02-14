package org.gmagnotta.bitcoin.parser.script;

import java.util.HashMap;

public enum Opcode {
	
	// CONSTANTS
	OP_FALSE((byte)0x00),
	
	NA_1((byte)0x01),
	NA_2((byte)0x02),
	NA_3((byte)0x03),
	NA_4((byte)0x04),
	NA_5((byte)0x05),
	NA_6((byte)0x06),
	NA_7((byte)0x07),
	NA_8((byte)0x08),
	NA_9((byte)0x09),
	NA_10((byte)0x0a),
	NA_11((byte)0x0b),
	NA_12((byte)0x0c),
	NA_13((byte)0x0d),
	NA_14((byte)0x0e),
	NA_15((byte)0x0f),
	NA_16((byte)0x10),
	NA_17((byte)0x11),
	NA_18((byte)0x12),
	NA_19((byte)0x13),
	NA_20((byte)0x14),
	NA_21((byte)0x15),
	NA_22((byte)0x16),
	NA_23((byte)0x17),
	NA_24((byte)0x18),
	NA_25((byte)0x19),
	NA_26((byte)0x1a),
	NA_27((byte)0x1b),
	NA_28((byte)0x1c),
	NA_29((byte)0x1d),
	NA_30((byte)0x1e),
	NA_31((byte)0x1f),
	NA_32((byte)0x20),
	NA_33((byte)0x21),
	NA_34((byte)0x22),
	NA_35((byte)0x23),
	NA_36((byte)0x24),
	NA_37((byte)0x25),
	NA_38((byte)0x26),
	NA_39((byte)0x27),
	NA_40((byte)0x28),
	NA_41((byte)0x29),
	NA_42((byte)0x2a),
	NA_43((byte)0x2b),
	NA_44((byte)0x2c),
	NA_45((byte)0x2d),
	NA_46((byte)0x2e),
	NA_47((byte)0x2f),
	NA_48((byte)0x30),
	NA_49((byte)0x31),
	NA_50((byte)0x32),
	NA_51((byte)0x33),
	NA_52((byte)0x34),
	NA_53((byte)0x35),
	NA_54((byte)0x36),
	NA_55((byte)0x37),
	NA_56((byte)0x38),
	NA_57((byte)0x39),
	NA_58((byte)0x3a),
	NA_59((byte)0x3b),
	NA_60((byte)0x3c),
	NA_61((byte)0x3d),
	NA_62((byte)0x3e),
	NA_63((byte)0x3f),
	NA_64((byte)0x40),
	NA_65((byte)0x41),
	NA_66((byte)0x42),
	NA_67((byte)0x43),
	NA_68((byte)0x44),
	NA_69((byte)0x45),
	NA_70((byte)0x46),
	NA_71((byte)0x47),
	NA_72((byte)0x48),
	NA_73((byte)0x49),
	NA_74((byte)0x4a),
	NA_75((byte)0x4b),
	
	OP_PUSHDATA1((byte)0x4c),
	OP_PUSHDATA2((byte)0x4d),
	OP_PUSHDATA4((byte)0x4e),
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
	OP_CHECKMULTISIGVERIFY((byte)0xaf);
	
	private byte value;
	
	private Opcode(byte value) {
		this.value = value;
	}
	
	private static HashMap<Byte, Opcode> OPCODES_MAP = new HashMap<Byte, Opcode>();
	
	/* static block to initialize map for all elements */
	static {
		
		for (Opcode opcode : Opcode.values()) {
			
			OPCODES_MAP.put(opcode.getValue(), opcode);
			
		}
		
	}
	
	public static Opcode fromByte(byte value) throws Exception {
		
		Opcode opcode = OPCODES_MAP.get(value);
		
		if (opcode == null) throw new Exception("Operation does not exists");
		
		return opcode;
		
	}
	
	public byte getValue() {
		return value;
	}

}
