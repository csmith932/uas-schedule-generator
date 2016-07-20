/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government under Contract No.
 * DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.utilities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;


/**
 * This is a quick and dirty unmodifiable navigable map implementation. java 1.8 provides an implementation via
 * Collections.unmodifiableNavigable that does this very thing, but since SWAC still has to support java 1.6 and java
 * 1.7, this implementation is still needed.  Once the minimium jdk level needed to be supported by SWAC is 1.8 or 
 * higher, this class can be deleted.
 * 
 * This was constructed with speed of development as the top priority. To that end, each method was developed in one of
 * two ways: 
 * - Instantiate an unmodifiable sorted map, which java 1.6 does provide, and delegate to that where possible, using
 * eclipse's Generate Delegate Methods feature off the source method. 
 * 
 * - Generate the remaining methods by delegating from the passed in Navigable map, again using eclipse's Generate 
 * Delegate Methods feature, and adding IllegalStateExceptions on any update method  
 * 
 * @author cunningham
 */
public class UnmodifiableNavigableMap<K,V> implements NavigableMap<K,V>, Serializable {
	private static final long serialVersionUID = 1325074239754600306L;
	
	private NavigableMap<K,V> innerMap;
	private SortedMap<K,V> innerSortedMap;
	
	public UnmodifiableNavigableMap(NavigableMap<K,V> inner_map) { 
		this.innerMap = inner_map;
		this.innerSortedMap = Collections.unmodifiableSortedMap(inner_map);
	}

	public Comparator<? super K> comparator() {
		return innerSortedMap.comparator();
	}

	public SortedMap<K, V> subMap(K fromKey, K toKey) {
		return innerSortedMap.subMap(fromKey, toKey);
	}

	public int size() {
		return innerSortedMap.size();
	}

	public boolean isEmpty() {
		return innerSortedMap.isEmpty();
	}

	public boolean containsKey(Object key) {
		return innerSortedMap.containsKey(key);
	}

	public SortedMap<K, V> headMap(K toKey) {
		return innerSortedMap.headMap(toKey);
	}

	public boolean containsValue(Object value) {
		return innerSortedMap.containsValue(value);
	}

	public V get(Object key) {
		return innerSortedMap.get(key);
	}

	public SortedMap<K, V> tailMap(K fromKey) {
		return innerSortedMap.tailMap(fromKey);
	}

	public V put(K key, V value) {
		return innerSortedMap.put(key, value);
	}

	public K firstKey() {
		return innerSortedMap.firstKey();
	}

	public K lastKey() {
		return innerSortedMap.lastKey();
	}

	public Set<K> keySet() {
		return innerSortedMap.keySet();
	}

	public Collection<V> values() {
		return innerSortedMap.values();
	}

	public V remove(Object key) {
		return innerSortedMap.remove(key);
	}

	public Set<Map.Entry<K, V>> entrySet() {
		return innerSortedMap.entrySet();
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		innerSortedMap.putAll(m);
	}

	public void clear() {
		innerSortedMap.clear();
	}

	public boolean equals(Object o) {
		return innerSortedMap.equals(o);
	}

	public int hashCode() {
		return innerSortedMap.hashCode();
	}

	public String toString() { 
		return innerSortedMap.toString();
	}
	
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	

	public Map.Entry<K, V> lowerEntry(K key) {
		return createUnmodEntry(innerMap.lowerEntry(key));
	}

	public K lowerKey(K key) {
		return innerMap.lowerKey(key);
	}

	public Map.Entry<K, V> floorEntry(K key) {
		return createUnmodEntry(innerMap.floorEntry(key));
	}

	public K floorKey(K key) {
		return innerMap.floorKey(key);
	}

	public Map.Entry<K, V> ceilingEntry(K key) {
		return createUnmodEntry(innerMap.ceilingEntry(key));
	}

	public K ceilingKey(K key) {
		return innerMap.ceilingKey(key);
	}

	public Map.Entry<K, V> higherEntry(K key) {
		return createUnmodEntry(innerMap.higherEntry(key));
	}

	public K higherKey(K key) {
		return innerMap.higherKey(key);
	}

	public Map.Entry<K, V> firstEntry() {
		return createUnmodEntry(innerMap.firstEntry());
	}

	public Map.Entry<K, V> lastEntry() {
		return createUnmodEntry(innerMap.lastEntry());
	}

	public Map.Entry<K, V> pollFirstEntry() {
		throw new IllegalStateException("Unmodifiable");
	}

	public Map.Entry<K, V> pollLastEntry() {
		throw new IllegalStateException("Unmodifiable");
	}

	public NavigableMap<K, V> descendingMap() {
		return new UnmodifiableNavigableMap<K,V>(innerMap.descendingMap());
	}

	public NavigableSet<K> navigableKeySet() {
		return new UnmodifiableNavigableSet<K>(innerMap.navigableKeySet());
	}

	public NavigableSet<K> descendingKeySet() {
		return new UnmodifiableNavigableSet<K>(innerMap.descendingKeySet());
	}

	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		return new UnmodifiableNavigableMap<K,V>(innerMap.subMap(fromKey, fromInclusive, toKey, toInclusive));
	}

	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return new UnmodifiableNavigableMap<K,V>(innerMap.headMap(toKey, inclusive));
	}

	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return new UnmodifiableNavigableMap<K,V>(innerMap.tailMap(fromKey, inclusive));
	}

	private Entry<K,V> createUnmodEntry(Entry<K,V> entry) {
		return entry == null ? null : new UnmodEntry<K,V>(entry);
	}
	
	private static class UnmodEntry<K,V> implements Map.Entry<K,V> {

		private Map.Entry<K,V> innerEntry;
		
		public UnmodEntry(Map.Entry<K,V> innerEntry) {
			assert(innerEntry != null);
			this.innerEntry = innerEntry;
		}

		public K getKey() {
			return innerEntry.getKey();
		}

		public V getValue() {
			return innerEntry.getValue();
		}

		public V setValue(V value) {
			throw new IllegalStateException("unmodifiable");
		}

		public boolean equals(Object o) {
			return innerEntry.equals(o);
		}

		public int hashCode() {
			return innerEntry.hashCode();
		}
		
		public String toString() { 
			return innerEntry.toString();
		}
	}
}