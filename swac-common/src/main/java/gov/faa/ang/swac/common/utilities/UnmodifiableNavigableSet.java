/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government under Contract No.
 * DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.utilities;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.SortedSet;



/**
 * This is a quick and dirty unmodifiable navigable set implementation. java 1.8 provides an implementation via
 * Collections.unmodifiableNavigableSet that does this very thing, but since SWAC still has to support java 1.6 and java
 * 1.7, this implementation is still needed.  Once the minimium jdk level needed to be supported by SWAC is 1.8 or 
 * higher, this class can be deleted.
 * 
 * This was constructed with speed of development as the top priority. To that end, each method was developed in one of
 * two ways: 
 * - Instantiate an unmodifiable sorted set, which java 1.6 does provide, and delegate to that where possible, using
 * eclipse's Generate Delegate Methods feature off the source method. 
 * 
 * - Generate the remaining methods by delegating from the passed in Navigable set, again using eclipse's Generate 
 * Delegate Methods feature, and adding IllegalStateExceptions on any update method  
 * 
 * @author cunningham
 */
public class UnmodifiableNavigableSet<K> implements NavigableSet<K> {
	
	private NavigableSet<K> innerSet;
	private SortedSet<K> innerSortedSet;

	public UnmodifiableNavigableSet(NavigableSet<K> inner) {
		this.innerSet = inner;
		this.innerSortedSet = Collections.unmodifiableSortedSet(inner);
	}

	public int size() {
		return innerSortedSet.size();
	}

	public boolean isEmpty() {
		return innerSortedSet.isEmpty();
	}

	public boolean contains(Object o) {
		return innerSortedSet.contains(o);
	}

	public Iterator<K> iterator() {
		return innerSortedSet.iterator();
	}

	public Comparator<? super K> comparator() {
		return innerSortedSet.comparator();
	}

	public Object[] toArray() {
		return innerSortedSet.toArray();
	}

	public SortedSet<K> subSet(K fromElement, K toElement) {
		return innerSortedSet.subSet(fromElement, toElement);
	}

	public <T> T[] toArray(T[] a) {
		return innerSortedSet.toArray(a);
	}

	public SortedSet<K> headSet(K toElement) {
		return innerSortedSet.headSet(toElement);
	}

	public boolean add(K e) {
		return innerSortedSet.add(e);
	}

	public SortedSet<K> tailSet(K fromElement) {
		return innerSortedSet.tailSet(fromElement);
	}

	public boolean remove(Object o) {
		return innerSortedSet.remove(o);
	}

	public K first() {
		return innerSortedSet.first();
	}

	public K last() {
		return innerSortedSet.last();
	}

	public boolean containsAll(Collection<?> c) {
		return innerSortedSet.containsAll(c);
	}

	public boolean addAll(Collection<? extends K> c) {
		return innerSortedSet.addAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return innerSortedSet.retainAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		return innerSortedSet.removeAll(c);
	}

	public void clear() {
		innerSortedSet.clear();
	}

	public boolean equals(Object o) {
		return innerSortedSet.equals(o);
	}

	public int hashCode() {
		return innerSortedSet.hashCode();
	}

	public K lower(K e) {
		return innerSet.lower(e);
	}

	public K floor(K e) {
		return innerSet.floor(e);
	}

	public K ceiling(K e) {
		return innerSet.ceiling(e);
	}

	public K higher(K e) {
		return innerSet.higher(e);
	}

	public K pollFirst() {
		throw new IllegalStateException("Unmodifiable");
	}

	public K pollLast() {
		throw new IllegalStateException("Unmodifiable");
	}

	public NavigableSet<K> descendingSet() {
		return new UnmodifiableNavigableSet<K>(innerSet.descendingSet());
	}
		
	public Iterator<K> descendingIterator() {
		return CollectionUtils.<K>unmodifiableIterator(innerSet.descendingIterator());
	}

	public NavigableSet<K> subSet(K fromElement, boolean fromInclusive, K toElement, boolean toInclusive) {
		return new UnmodifiableNavigableSet<K>(innerSet.subSet(fromElement, fromInclusive, toElement, toInclusive));
	}

	public NavigableSet<K> headSet(K toElement, boolean inclusive) {
		return new UnmodifiableNavigableSet<K>(innerSet.headSet(toElement, inclusive));
	}

	public NavigableSet<K> tailSet(K fromElement, boolean inclusive) {
		return new UnmodifiableNavigableSet<K>(innerSet.tailSet(fromElement, inclusive));
	}
}