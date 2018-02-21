package org.gmagnotta.bitcoin.blockchain;

import java.util.LinkedHashMap;
import java.util.Map;

public class BlockCache {

	private LinkedHashMap<String, ValidatedBlockHeader> mapByHash;
	private LinkedHashMap<Integer, ValidatedBlockHeader> mapByIndex;

	public BlockCache(final long size) {
		this.mapByHash = new LinkedHashMap<String, ValidatedBlockHeader>() {
			@Override
			protected boolean removeEldestEntry(final Map.Entry eldest) {
				return size() > size;
			}
		};
		this.mapByIndex = new LinkedHashMap<Integer, ValidatedBlockHeader>() {
			@Override
			protected boolean removeEldestEntry(final Map.Entry eldest) {
				return size() > size;
			}
		};
	}

	public ValidatedBlockHeader getBlockHeader(int index) {
		return mapByIndex.get(index);
	}

	public ValidatedBlockHeader getBlockHeader(String hash) {
		return mapByHash.get(hash);
	}

	public void putBlockHeader(int index, String hash, ValidatedBlockHeader header) {
		
		mapByIndex.put(index, header);
		mapByHash.put(hash, header);
		
	}

}
