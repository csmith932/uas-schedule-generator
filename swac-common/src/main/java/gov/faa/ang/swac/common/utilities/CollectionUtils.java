/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government under Contract No.
 * DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.utilities;

import gov.faa.ang.swac.common.datatypes.Timestamp;

import java.util.Iterator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;



/**
 * 
 * @author cunningham
 */
public class CollectionUtils {

	private CollectionUtils() { throw new IllegalStateException("static class only"); }
	
	public static <K,V> NavigableMap<K,V> unmodifiableNavigableMap(NavigableMap<K,V> map) {
		return new UnmodifiableNavigableMap<K, V>(map);
	}

	public static <K,V> NavigableMap<K,V> emptyUnmodifiableNavigableMap() {
		return new UnmodifiableNavigableMap<K, V>(new TreeMap<K,V>()); 
	}
	
	public static <K> NavigableSet<K> unmodifiableNavigableSet(NavigableSet<K> set) {
		return new UnmodifiableNavigableSet<K>(set); 
	}
	
	public static <T> Iterator<T> unmodifiableIterator(Iterator<T> iterator) {
		return new UnmodifiableIterator<T>(iterator); 
	}
	
	public static <T> Iterable<T> unmodifiableIterable(final Iterator<T> iterator) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new UnmodifiableIterator<T>(iterator);
			} 
			
		};
	}

//	public static <M extends Map<K, V>,K,V> Iterator<Entry<K,V>> mapsOfMapEntryIterator(Map<?, M> mapOMaps) {
//		return new MapOfMapsEntriesIterator<M, K, V>(mapOMaps);
//	}
//	
//	public static <M extends Map<?, V>,V> Iterator<V> mapsOfMapValuesIterator(Map<?, M> mapOMaps) {
//		return new MapOfMapsValuesIterator<M, V>(mapOMaps);
//	}
}