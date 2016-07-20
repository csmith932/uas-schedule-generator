/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government under Contract No.
 * DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.utilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Kludge that allows to cast a List<Specific> to List<General>, assuming the Specific instances can be cast to a
 * General instance. This class will perform casts on adds and inserts. Lint will not be happy. ClassCastExceptions
 * could occur if used improperly.
 * 
 * This allows you to avoid copying the entire list. There is probably a better way to do this...
 * 
 * @author cunningham
 * @param <R>
 */
public class ForwardingList<G, S extends G> implements List<G> {
	private List<S> innerList;

	public ForwardingList(List<S> innerList) {
		this.innerList = innerList;
	}

	public int size() {
		return innerList.size();
	}

	public boolean isEmpty() {
		return innerList.isEmpty();
	}

	public boolean contains(Object o) {
		return innerList.contains(o);
	}

	public Iterator<G> iterator() {
		return listIterator();
	}

	public Object[] toArray() {
		return innerList.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return innerList.toArray(a);
	}

	public boolean add(G e) {
		S s = (S) e;
		return innerList.add(s);
	}

	public boolean remove(Object o) {
		return innerList.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		return innerList.containsAll(c);
	}

	public boolean addAll(Collection<? extends G> c) {
		for (G g : c) {
			S s = (S) g;
			innerList.add(s);
		}
		return ! c.isEmpty();
	}

	public boolean addAll(int index, Collection<? extends G> c) {
		for (G g : c) {
			S s = (S) g;
			innerList.add(index++, s);
		}
		return ! c.isEmpty();
	}

	public boolean removeAll(Collection<?> c) {
		return innerList.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return innerList.retainAll(c);
	}

	public void clear() {
		innerList.clear();
	}

	public boolean equals(Object o) {
		return innerList.equals(o);
	}

	public int hashCode() {
		return innerList.hashCode();
	}

	public G get(int index) {
		return innerList.get(index);
	}

	public G set(int index, G element) {
		throw new UnsupportedOperationException("");
	}

	public void add(int index, G element) {
		throw new UnsupportedOperationException("");
	}

	public S remove(int index) {
		return innerList.remove(index);
	}

	public int indexOf(Object o) {
		return innerList.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return innerList.lastIndexOf(o);
	}

	public ListIterator<G> listIterator() {
		return new MyListIterator<G, S>(innerList.listIterator());
	}

	public ListIterator<G> listIterator(int index) {
		return new MyListIterator<G, S>(innerList.listIterator(index));
	}

	
	public static class MyListIterator<G, S extends G> implements ListIterator<G> {
		private ListIterator<S> sIt;
		
		public MyListIterator(ListIterator<S> sIt) {
			this.sIt = sIt;
		}
		
		@Override
		public boolean hasNext() {
			return sIt.hasNext();
		}

		@Override
		public G next() {
			return sIt.next();
		}

		@Override
		public boolean hasPrevious() {
			return sIt.hasPrevious();
		}

		@Override
		public G previous() {
			return (G) sIt.previous();
		}

		@Override
		public int nextIndex() {
			return sIt.nextIndex();
		}

		@Override
		public int previousIndex() {
			return sIt.previousIndex();
		}

		@Override
		public void remove() {
			sIt.remove();
		}

		@Override
		public void set(G e) {
			sIt.set((S) e);
		}

		@Override
		public void add(G e) {
			sIt.add((S) e);				
		} 
		
	};

	public List<G> subList(int fromIndex, int toIndex) {
		return new ForwardingList<G,S>(innerList.subList(fromIndex, toIndex));
	}

	
	
}