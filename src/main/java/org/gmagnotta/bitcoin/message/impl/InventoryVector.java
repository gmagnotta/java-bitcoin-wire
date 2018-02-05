package org.gmagnotta.bitcoin.message.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.gmagnotta.bitcoin.utils.Sha256Hash;

public class InventoryVector {

	public enum Type {

		ERROR(0),

		MSG_TX(1),

		MSG_BLOCK(2),

		MSG_FILTERED_BLOCK(3),

		MSG_CMPCT_BLOCK(4);

		private int value;
		private static Map<Integer, Type> map = new HashMap<Integer, Type>();

		private Type(int value) {
			this.value = value;
		}

		static {
			
			for (Type type : Type.values()) {
				map.put(type.value, type);
			}
			
		}

		public static Type valueOf(int type) {
			return map.get(type);
		}

		public int getValue() {
			return value;
		}

	}

	private Type type;
	private Sha256Hash hash;

	public InventoryVector(Type type, Sha256Hash hash) {
		this.type = type;
		this.hash = hash;
	}

	public Type getType() {
		return type;
	}

	public Sha256Hash getHash() {
		return hash;
	}
	
	@Override
	public boolean equals(final Object object) {
		
		if (!(object instanceof InventoryVector))
			return false;
		
		if (this == object)
			return true;
		
		final InventoryVector other = (InventoryVector) object;
		
		return Objects.equals(type, other.type) &&
				Objects.equals(hash, other.hash);
		
	}
}
