package query.struct;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Map2<K, V>{
	private Map<K, V> forth;
	private Map<V, K> back;
	
	public Map<K, V> forth() { return forth; };
	public Map<V, K> back()  { return back;  };
	
	public Map2(){
		forth = new HashMap<K, V>();
		back = new HashMap<V, K>();
	}
	
	public Map2(Map<K, V> _forth, Map<V, K> _back){
		forth = _forth;
		back = _back;
	}
	
	public int size() {
		return forth.size();
	}

	public boolean isEmpty() {
		return forth.isEmpty();
	}

	public boolean containsKey(Object key) {
		return forth.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return back.containsKey(value);
	}

	public V getValue(Object key) {
		return forth.get(key);
	}
	
	public K getKey(Object value) {
		return back.get(value);
	}


	public Map.Entry<K, V> put(K key, V value) {
		V old_value = forth.get(key);
		Map.Entry<K, V> ret = new Entry<K, V>(key, old_value);
		
		forth.put(key, value);
		
		back.remove(old_value);
		back.put(value, key);
		
		return ret;
	}

	public V removeKey(Object key) {
		V value = forth.get(key);
		forth.remove(key);
		back.remove(value);
		return value;
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		forth.putAll(m);	
		for(Map.Entry<? extends K, ? extends V> entry : m.entrySet())
			back.put(entry.getValue(), entry.getKey());		
	}

	public void clear() {
		forth.clear();
		back.clear();
	}

	public Set<K> keySet() {
		return forth.keySet();
	}

	public Collection<V> values() {
		return forth.values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return forth.entrySet();
	}
	
}
