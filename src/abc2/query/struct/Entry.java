package query.struct;

import java.util.Map;

public class Entry<K, V> implements Map.Entry<K, V>{
	private K key;
	private V value;

	public Entry(K _key, V _value){
		key = _key;
		value = _value;
	}
	
	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		return this.value = value;
	}
}