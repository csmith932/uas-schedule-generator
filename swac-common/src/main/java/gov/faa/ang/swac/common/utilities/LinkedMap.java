package gov.faa.ang.swac.common.utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException; import java.util.Set;

/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * A <code>Map</code> implementation that maintains the order of the entries. In
 * this implementation order is maintained by original insertion.
 * <p>
 * This class is based on the org.apache.commons.collections.map.LinkedMap class
 * in the Apache commons LinkedMap class, with several variations from that
 * class:
 *
 * - The Apache LinkedMap class is part of a map hierarchy of multiple classes.
 * This implementation flattens the hierarchy into one class. It does not depend
 * on any Apache commons class.
 *
 * - removeFirst() and removeLast() are in constant time.
 *
 * - Ability to biIterate (iterate in either direction) on keys, values, and
 * entries. Iteration can start from the beginning entry (earliest inserted
 * entry), ending entry (latest inserted entry), or any entry in between via one
 * of the 12 biIterator methods.
 *
 * - Iterations follow the LinkedMap.BiIterator interface, similar to the apache
 * OrderedMapIterator.  Iteration removals are in constant time.
 *
 * - No LinkedMapList functionality
 *
 */
public class LinkedMap<K, V> implements Map<K, V>, Serializable, Cloneable {
	private static final long serialVersionUID = 4645513094845961908L;

	
	public interface BiIterator<T> extends Iterator<T> {
		boolean hasPrevious();
		T previous();
		
		/**
		 * Moves iterator to oldest item in map.
		 */
		void reset();
		/**
		 * Moves iterator beyond latest item in map
		 */
	    void complete();
	}
	
	/** The default capacity to use */
    protected static final int DEFAULT_CAPACITY = 16;
    /** The default threshold to use */
    protected static final int DEFAULT_THRESHOLD = 12;
    /** The default load factor to use */
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;
    /** The maximum capacity allowed */
    protected static final int MAXIMUM_CAPACITY = 1 << 30;
    /** An object for masking null */
    protected static final Object NULL = new Object();
    
    /** Load factor, normally 0.75 */
    private transient float loadFactor;
    /** Size at which to rehash */
	private transient int threshold;
	
	/** The size of the map */
	private transient int size;
	/** Map entries */
	private transient LinkEntry<K, V>[] data;
	/** Header in the linked list */
	private transient LinkEntry<K, V> header;
	/** Modification count for iterators */
	private int modCount;
	
	public LinkedMap() {
		this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
	}
	
	public LinkedMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	@SuppressWarnings("unchecked")
	protected LinkedMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 1) {
			throw new IllegalArgumentException("Initial capacity must be greater than 0");
		}
		if (loadFactor <= 0.0f || Float.isNaN(loadFactor)) {
			throw new IllegalArgumentException("Load factor must be greater than 0");
		}
		
		this.modCount = 0;
		this.loadFactor = loadFactor;
		initialCapacity = calculateNewCapacity(initialCapacity);
		this.threshold = calculateThreshold(initialCapacity, loadFactor);
		
		this.size = 0;
		this.data = new LinkEntry[initialCapacity];
		this.header = new LinkEntry<K,V>();
		this.header.before = header.after = header;
	}
	
	@Override
	public int size() { return size; }
	
	@Override
	public void clear() {
        modCount++;
        LinkEntry<K,V>[] data = this.data;
        for (int i = data.length - 1; i >= 0; i--) {
            data[i] = null;
        }
        size = 0;
        header = new LinkEntry<K,V>();
        header.before = header.after = header;
    }

	@Override
	public V get(Object key) {
		LinkEntry<K, V> entry = getEntry(key);
		if (entry != null)
			return entry.getValue();
		return null;
	}

	@Override
	public V put(K key, V value) {
		modCount++;
		
		int hashCode = hash((key == null) ? NULL : key);
		int bucketIndex = bucketIndexFromHash(hashCode, data.length);
		
		LinkEntry<K, V> node = data[bucketIndex];
		while(node != null) {
			if (node.keyHashCode() == hashCode && isEqualKey(key, node.key)) {
				V oldData = node.value;
				node.value = value;
				return oldData;
			}
			node = node.next;
		}
		
		LinkEntry<K, V> newNode = new LinkEntry<K, V>(key, value, hashCode, data[bucketIndex], header);
		data[bucketIndex] = newNode;
		
		size++;
		checkCapacity();
		return null;
	}


	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * This implementation iterates over the specified map's <tt>entrySet()</tt>
	 * collection, and calls this map's <tt>put</tt> operation once for each
	 * entry returned by the iteration.
	 * 
	 * <p>
	 * Note that this implementation throws an
	 * <tt>UnsupportedOperationException</tt> if this map does not support the
	 * <tt>put</tt> operation and the specified map is nonempty.
	 * 
	 * @throws UnsupportedOperationException
	 *             {@inheritDoc}
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             {@inheritDoc}
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}
	
	@Override
	public V remove(Object key) {
		LinkEntry<K, V> entryToRemove = getEntry(key);
		if (entryToRemove != null) {
			removeEntry(entryToRemove);
			return entryToRemove.getValue();
		}
		
		return null;
	}
	
	/**
     * Checks whether the map is currently empty.
     * 
     * @return true if the map is currently size zero
     */
    @Override
	public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Checks whether the map contains the specified key.
     * 
     * @param key  the key to search for
     * @return true if the map contains the key
     */
    @Override
	public boolean containsKey(Object key) {
    	return (getEntry(key) != null);
        
    }

    /**
     * Checks whether the map contains the specified value.
     * 
     * @param value  the value to search for
     * @return true if the map contains the value
     */
    @Override
	public boolean containsValue(Object value) {
        if (value == null) {
            for (int i = 0, isize = data.length; i < isize; i++) {
                LinkEntry<K, V> entry = data[i];
                while (entry != null) {
                    if (entry.getValue() == null) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        } else {
            for (int i = 0, isize = data.length; i < isize; i++) {
            	LinkEntry<K, V> entry = data[i];
                while (entry != null) {
                    if (isEqualValue(value, entry.getValue())) {
                        return true;
                    }
                    entry = entry.next;
                }
            }
        }
        return false;
    }
	
	/**
	 * Gets the first key in the map, which is the most recently inserted.
	 * 
	 * @return the most recently inserted key
	 */
	public K firstKey() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return header.after.getKey();
	}
	
	/**
	 * Gets the first value in the map, which is the most recently inserted.
	 * 
	 * @return the most recently inserted key
	 */
	public V firstValue() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return header.after.getValue();
	}
	
	/**
	 * Gets the first entry in the map, which is the most recently inserted.
	 * 
	 * @return the most recently inserted key
	 */
	public Entry<K, V> firstEntry() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return header.after;
	}

	/**
	 * Gets the last key in the map, which is the first inserted.
	 * 
	 * @return the eldest key
	 */
	public K lastKey() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return header.before.getKey();
	}
	
	/**
	 * Gets the last value in the map, which is the first inserted.
	 * 
	 * @return the eldest key
	 */
	public V lastValue() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return header.before.getValue();
	}
	
	/**
	 * Gets the last entry in the map, which is the first inserted.
	 * 
	 * @return the eldest key
	 */
	public Entry<K, V> lastEntry() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return header.before;
	}
	
	/**
	 * Removes the first entry in the map, which is the most recently inserted.
	 */
	public Map.Entry<K, V> removeFirst() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return removeEntry(header.after);
		
	}
	
	/**
	 * Removes the last entry in the map, which was the first inserted.
	 * @return
	 */
	public LinkEntry<K, V> removeLast() {
		if (size == 0) {
			throw new NoSuchElementException("Map is empty");
		}
		return removeEntry(header.before);
	}
	
	
	/**
	 * Gets the next key in sequence.
	 * 
	 * @param key
	 *            the key to get after
	 * @return the next key
	 */
	public K nextKey(Object key) {
		LinkEntry<K, V> entry = getEntry(key);
		return (entry == null || entry.after == header ? null : entry.after.getKey());
	}

	/**
	 * Gets the previous key in sequence.
	 * 
	 * @param key
	 *            the key to get before
	 * @return the previous key
	 */
	public K previousKey(Object key) {
		LinkEntry<K, V> entry = getEntry(key);
		return (entry == null || entry.before == header ? null : entry.before.getKey());
	}

	/**
     * Gets the entry mapped to the key specified.
     * <p>
     * This method exists for subclasses that may need to perform a multi-step
     * process accessing the entry. The public methods in this class don't use this
     * method to gain a small performance boost.
     * 
     * @param key  the key
     * @return the entry, null if no match
     */
    protected LinkEntry<K, V> getEntry(Object key) {
        int hashCode = hash((key == null) ? NULL : key);
        LinkEntry<K, V> entry = data[bucketIndexFromHash(hashCode, data.length)];
        
        while (entry != null) {
            if (entry.keyHashCode() == hashCode && isEqualKey(key, entry.key)) {
                return entry;
            }
            entry = entry.next;
        }
        return null;
    }
    
	private LinkEntry<K, V>  removeEntry(LinkEntry<K, V> nodeToRemove) {
		if (nodeToRemove.prior == null) {
			int bucketIndex = bucketIndexFromHash(nodeToRemove.keyHashCode(), data.length);
			data[bucketIndex] = nodeToRemove.next;
		} else {
			nodeToRemove.prior.next = nodeToRemove.next;
		}
		if (nodeToRemove.next != null) {
			nodeToRemove.next.prior = nodeToRemove.prior;
		}
		
		nodeToRemove.before.after = nodeToRemove.after;
		nodeToRemove.after.before = nodeToRemove.before;
		nodeToRemove.after = null;
		nodeToRemove.before = null;
        
		modCount++;
		size--;
		return nodeToRemove;
	}
	
	
	protected static int hash(Object key) {
        // same as JDK 1.4
        int h = key.hashCode();
        h += ~(h << 9);
        h ^=  (h >>> 14);
        h +=  (h << 4);
        h ^=  (h >>> 10);
        return h;
    }
	
	protected static int bucketIndexFromHash(int hashCode, int dataSize) {
        return hashCode & (dataSize - 1);
    }

	/**
     * Compares two keys, in internal converted form, to see if they are equal.
     * This implementation uses the equals method and assumes neither key is null.
     * Subclasses can override this to match differently.
     * 
     * @param key1  the first key to compare passed in from outside
     * @param key2  the second key extracted from the entry via <code>entry.key</code>
     * @return true if equal
     */
    protected boolean isEqualKey(Object key1, Object key2) {
        return (key1 == key2 || key1.equals(key2));
    }
    
    /**
     * Compares two values, in external form, to see if they are equal.
     * This implementation uses the equals method and assumes neither value is null.
     * Subclasses can override this to match differently.
     * 
     * @param value1  the first value to compare passed in from outside
     * @param value2  the second value extracted from the entry via <code>getValue()</code>
     * @return true if equal
     */
    protected boolean isEqualValue(Object value1, Object value2) {
        return (value1 == value2 || value1.equals(value2));
    }
    
	/**
	 * Checks the capacity of the map and enlarges it if necessary.
	 * <p>
	 * This implementation uses the threshold to check if the map needs
	 * enlarging
	 */
	protected void checkCapacity() {
		if (size >= threshold) {
			int newCapacity = data.length * 2;
			if (newCapacity <= MAXIMUM_CAPACITY) {
				ensureCapacity(newCapacity);
			}
		}
	}

	/**
	 * Changes the size of the data structure to the capacity proposed.
	 * 
	 * @param newCapacity
	 *            the new capacity of the array (a power of two, less or equal
	 *            to max)
	 */
	@SuppressWarnings("unchecked")
	protected void ensureCapacity(int newCapacity) {
		int oldCapacity = data.length;
		if (newCapacity <= oldCapacity) {
			return;
		}
		
		if (size == 0) {
			threshold = calculateThreshold(newCapacity, loadFactor);
			data = new LinkEntry[newCapacity];
			header = new LinkEntry<K,V>();
			header.before = header.after = header;
		} else {
			modCount++;
			LinkedMap<K, V> newMap = new LinkedMap<K, V>(newCapacity, loadFactor);
			
			for (LinkEntry<K, V> node = this.header.after; node != header; node = node.after) {
				newMap.put(node.key, node.value);
				//remove(node.key); // is this necessary?
			}

			data = newMap.data;
			header = newMap.header;
			threshold = newMap.threshold;
		}
	}
	
	protected int calculateNewCapacity(int proposedCapacity) {
		int newCapacity = 1;
		if (proposedCapacity > MAXIMUM_CAPACITY) {
			newCapacity = MAXIMUM_CAPACITY;
		} else {
			while (newCapacity < proposedCapacity) {
				newCapacity <<= 1; // multiply by two
			}
			if (newCapacity > MAXIMUM_CAPACITY) {
				newCapacity = MAXIMUM_CAPACITY;
			}
		}
		return newCapacity;
	}

	protected int calculateThreshold(int newCapacity, float factor) {
		return (int) (newCapacity * factor);
	}

	   
	/**
     * Writes the map data to the stream.
     * 
     * @param out  the output stream
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        
        out.writeFloat(loadFactor);
        out.writeInt(data.length);
        out.writeInt(size);
        for (Iterator<Entry<K, V>> it = entryBiIterator(); it.hasNext();) {
        	java.util.Map.Entry<K, V> entry = it.next();
            out.writeObject(entry.getKey());
            out.writeObject(entry.getValue());
        }
    }

    /**
     * Reads the map data from the stream.
     * 
     * @param in  the input stream
     */
    @SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        this.header = new LinkEntry<K,V>();
		this.header.before = header.after = header;
		
        loadFactor = in.readFloat();
        int capacity = in.readInt();
        int size = in.readInt();
        threshold = calculateThreshold(capacity, loadFactor);
        data = new LinkEntry[capacity];
        for (int i = 0; i < size; i++) {
            K key = (K) in.readObject();
            V value = (V) in.readObject();
            put(key, value);
        }
    }
 
    /**
     * Creates a key biiterator.
     * 
     * @return the keySet iterator
     */
    public BiIterator<K> keyBiIterator() {
        if (size() == 0) {
            return new EmptyBiIterator<K>();
        }
        return new KeyBiIterator<K,V>(this);
    }
    
    public Iterable<K> keyIterable() {
    	return new Iterable<K>() {
			@Override
			public Iterator<K> iterator() {
				return keyBiIterator();
			}
    	};
    }
    
    public BiIterator<K> keyBiIteratorAt(Object key) {
    	return keyBiIteratorAt(key, true);
    }
    
    public BiIterator<K> keyBiIteratorAt(Object key, boolean atNext) {
    	LinkEntry<K, V> entry = getEntry(key);
    	if (entry == null)
    		return new EmptyBiIterator<K>();
    	
    	if (! atNext) {
    		entry = entry.after;
    	}
    	
    	return new KeyBiIterator<K, V>(this, entry);
    }
    
    public BiIterator<K> keyBiIteratorAtLast() {
    	if (size() == 0) {
            return new EmptyBiIterator<K>();
        }
    	return new KeyBiIterator<K, V>(this, header);
    }
    
    /**
     * Gets the keySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the keys, use {@link #mapIterator()}.
     * 
     * @return the keySet view
     */
    @Override
	public Set<K> keySet() {
    	return new KeySet<K, V>(this);
    }
    
	 /**
     * KeySet implementation.
     */
    protected static class KeySet<K, V> extends AbstractSet<K> {
        /** The parent map */
        protected final LinkedMap<K, V> parent;
        
        protected KeySet(LinkedMap<K, V> parent) {
            super();
            this.parent = parent;
        }

        @Override
		public int size() {
            return parent.size();
        }
        
        @Override
		public void clear() {
            parent.clear();
        }
        
        @Override
		public boolean contains(Object key) {
            return parent.containsKey(key);
        }
        
        @Override
		public boolean remove(Object key) {
            boolean result = parent.containsKey(key);
            parent.remove(key);
            return result;
        }

        @Override
		public Iterator<K> iterator() {
            return parent.keyBiIterator();
        }
        
        public BiIterator<K> biIterator() {
        	return parent.keyBiIterator();
        }
    }
    /**
     * KeySet iterator.
     */
    protected static class KeyBiIterator<K, V> extends HashBiIterator<K, V, K> {
        
        protected KeyBiIterator(LinkedMap<K, V> parent) {
            super(parent);
        }
        
        protected KeyBiIterator(LinkedMap<K, V> parent, LinkEntry<K, V> next) {
            super(parent, next);
        }

        @Override
        public K next() {
            return nextEntry().getKey();
        }

		@Override
		public K previous() {
			return previousEntry().getKey();
		}
    }
    
   
    /**
     * Creates an entry set iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the entrySet iterator
     */
    public BiIterator<Entry<K,V>> entryBiIterator() {
        if (size() == 0) {
            return new EmptyBiIterator<Entry<K, V>>();
        }
        return new EntrySetIterator<K, V>(this);
    }
    
    public BiIterator<Entry<K,V>> entryBiIteratorAt(Object key) {
    	return entryBiIteratorAt(key, true);
    }
    
    public BiIterator<Entry<K,V>> entryBiIteratorAt(Object key, boolean atNext) {
    	LinkEntry<K, V> entry = getEntry(key);
    	if (entry == null)
    		return new EmptyBiIterator<Entry<K,V>>();
    	
    	if (! atNext) {
    		entry = entry.after;
    	}
    	
    	return new EntrySetIterator<K, V>(this, entry);
    }
    
    public BiIterator<Entry<K,V>> entryBiIteratorAtLast() {
    	return new EntrySetIterator<K, V>(this, header);
    }
    
    /**
     * Gets the entrySet view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the entries, use {@link #mapIterator()}.
     * 
     * @return the entrySet view
     */
    @Override
	public Set<Entry<K, V>> entrySet() {
        return new EntrySet<K, V>(this);
    }
    
    
    /**
     * EntrySet iterator.
     */
    protected static class EntrySetIterator<K,V> extends HashBiIterator<K, V, Map.Entry<K, V>> {
        
        protected EntrySetIterator(LinkedMap<K,V> parent) {
            super(parent);
        }

        protected EntrySetIterator(LinkedMap<K,V> parent, LinkEntry<K, V> next) {
            super(parent, next);
        }
        
        @Override
        public Map.Entry<K, V> next() {
            return nextEntry();
        }

		@Override
		public Entry<K, V> previous() {
			return previousEntry();
		}
    }
    

    /**
     * EntrySet implementation.
     */
	protected static class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>> {
		/** The parent map */
		protected final LinkedMap<K, V> parent;

		protected EntrySet(LinkedMap<K, V> parent) {
			super();
			this.parent = parent;
		}

		@Override
		public int size() {
			return parent.size();
		}

		@Override
		public void clear() {
			parent.clear();
		}

		@Override
		public boolean contains(Object entry) {
			if (entry instanceof Map.Entry) {
				@SuppressWarnings("unchecked")
				Map.Entry<K, V> e = (Map.Entry<K, V>) entry;
				Entry<K, V> match = parent.getEntry(e.getKey());
				return (match != null && match.equals(e));
			}
			return false;
		}

		@Override
		public boolean remove(Object obj) {
			if (obj instanceof Map.Entry == false) {
				return false;
			}
			if (contains(obj) == false) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Map.Entry<K,V> entry = (Map.Entry<K,V>) obj;
			Object key = entry.getKey();
			parent.remove(key);
			return true;
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return parent.entryBiIterator();
		}
	}

    /**
     * Creates a value biiterator.
     * 
     * @return biIterator of values
     */
    public BiIterator<V> valueBiIterator() {
        if (size() == 0) {
            return new EmptyBiIterator<V>();
        }
        return new ValueBiIterator<K,V>(this);
    }
    
    public Iterable<V> valueIterable() {
    	return new Iterable<V>() {
			@Override
			public Iterator<V> iterator() {
				return new ValueBiIterator<K, V>(LinkedMap.this);
			}
    	};
    }
    
    public BiIterator<V> valueBiIteratorAt(Object key) {
    	return valueBiIteratorAt(key, true);
    }
    
    public BiIterator<V> valueBiIteratorAt(Object key, boolean atNext) {
    	LinkEntry<K, V> entry = getEntry(key);
    	if (entry == null)
    		return new EmptyBiIterator<V>();
    	
    	if (! atNext) {
    		entry = entry.after;
    	}
    	
    	return new ValueBiIterator<K, V>(this, entry);
    }
    
    public BiIterator<V> valueBiIteratorAtLast() {
    	if (size() == 0) {
            return new EmptyBiIterator<V>();
        }
    	return new ValueBiIterator<K, V>(this, header);
    }

    /**
     * Gets the values view of the map.
     * Changes made to the view affect this map.
     * To simply iterate through the values, use {@link #mapIterator()}.
     * 
     * @return the values view
     */
    @Override
	public Collection<V> values() {
        return new Values<K, V>(this);
    }

    /**
     * Creates a values iterator.
     * Subclasses can override this to return iterators with different properties.
     * 
     * @return the values iterator
     */
    protected Iterator<V> createValuesIterator() {
        if (size() == 0) {
            return Collections.<V>emptyList().iterator();
        }
        return new ValueBiIterator<K, V>(this);
    }
    
    /**
     * Values implementation.
     */
    protected static class Values<K, V> extends AbstractCollection<V> {
        /** The parent map */
        protected final LinkedMap<K, V> parent;
        
        protected Values(LinkedMap<K, V> parent) {
            super();
            this.parent = parent;
        }

        @Override
		public int size() {
            return parent.size();
        }
        
        @Override
		public void clear() {
            parent.clear();
        }
        
        @Override
		public boolean contains(Object value) {
            return parent.containsValue(value);
        }
        
        @Override
		public Iterator<V> iterator() {
            return parent.createValuesIterator();
        }
    }

    /**
     * Values iterator.
     */
    protected static class ValueBiIterator<K, V> extends HashBiIterator<K, V, V> {
        
        protected ValueBiIterator(LinkedMap<K, V> parent) {
            super(parent);
        }

        protected ValueBiIterator(LinkedMap<K, V> parent, LinkEntry<K, V> next) {
            super(parent, next);
        }
        
        @Override
		public V next() {
            return nextEntry().getValue();
        }

		@Override
		public V previous() {
			return previousEntry().getValue();
		}
    }

    protected static class EmptyBiIterator<T> implements BiIterator<T> {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public T next() {
			return null;
		}

		@Override
		public void remove() {}
		
		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public T previous() {
			return null;
		}

		@Override
		public void reset() {
		}

		@Override
		public void complete() {
		}
    }
    
	private static abstract class HashBiIterator<K, V, T> implements BiIterator<T> {
		/** The parent map */
	    protected final LinkedMap<K,V> parent;
	    /** The current (last returned) entry */
	    protected LinkEntry<K, V> last;
	    /** The next entry */
	    protected LinkEntry<K, V> next;
	    /** The modification count expected */
	    protected int expectedModCount;
	    
	    protected HashBiIterator(LinkedMap<K,V> parent) {
	        this.parent = parent;
	        this.next = parent.header.after;
	        this.expectedModCount = parent.modCount;
	    }
	
	    protected HashBiIterator(LinkedMap<K,V> parent, LinkEntry<K, V> next) {
	        this.parent = parent;
	        this.next = next;
	        this.expectedModCount = parent.modCount;
	    }
	    
	    @Override
		public abstract T next();
	    @Override
		public abstract T previous();
	    
	    @Override
		public boolean hasNext() {
            return (next != parent.header);
        }
        
        @Override
		public boolean hasPrevious() {
            return (next.before != parent.header);
        }
	    
	    protected LinkEntry<K,V> nextEntry() {
	        if (parent.modCount != expectedModCount) {
	            throw new ConcurrentModificationException();
	        }
	        if (next == parent.header)  {
	            throw new NoSuchElementException("No next() entry in the iteration");
	        }
	        last = next;
            next = next.after;
            return last;
	    }
	
	    protected LinkEntry<K,V> previousEntry() {
	        if (parent.modCount != expectedModCount) {
	            throw new ConcurrentModificationException();
	        }
	        LinkEntry<K,V> previous = next.before;
	        if (previous == null)  {
	            throw new NoSuchElementException("No previous() entry in the iteration");
	        }
	        next = previous;
	        last = previous;
	        return last;
	    }
	    
	    @Override
		public void remove() {
	        if (last == null) {
	            throw new IllegalStateException("remove() can only be called once after next()");
	        }
	        if (parent.modCount != expectedModCount) {
	            throw new ConcurrentModificationException();
	        }
	        parent.removeEntry(last);
	        last = null;
	        expectedModCount = parent.modCount;
	    }
	    
	    @Override
		public void reset() {
	        last = null;
	        next = parent.header.after;
	    }
	    
	    @Override
		public void complete() {
	        last = null;
	        next = parent.header.before;
	    }
	
	    @Override
		public String toString() {
	        if (last != null) {
	            return "Iterator[" + last.key + "=" + last.value + "]";
	        } else {
	            return "Iterator[]";
	        }
	    }
	}

	static int idGen = 0;
	static public class LinkEntry<K, V> implements Map.Entry<K, V>{
		K key;
		V value;
		int hashCode = -1;
		LinkEntry<K, V> before;
		LinkEntry<K, V> after;
		LinkEntry<K, V> prior;
		LinkEntry<K, V> next;
		boolean debugSet = false;
		int id = ++idGen;
		
		public LinkEntry() {
		}
		
		public LinkEntry(K k, V v, int _hashCode, LinkEntry<K, V> _next, LinkEntry<K, V> _after) {
			assert(k != null);
			key = k;
			value = v;
			hashCode = _hashCode;
			debugSet = true;
			next = _next;
			if (_next != null)
				_next.prior = this;
			
			after  = _after;
	        before = _after.before;
	        _after.before.after = this;
	        _after.before = this;
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
		public V setValue(V _value) {
			V old = value;
			value = _value;
			return old;
		}
		
		public int keyHashCode() {
			if (! debugSet)
				throw new IllegalStateException("hashCode not set");
			return hashCode;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof Map.Entry == false) {
				return false;
			}
			Map.Entry other = (Map.Entry) obj;
			return (getKey() == null ? other.getKey() == null : getKey().equals(other.getKey()))
					&& (getValue() == null ? other.getValue() == null : getValue().equals(other.getValue()));
		}

		@Override
		public int hashCode() {
			return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
		}

	}
}
