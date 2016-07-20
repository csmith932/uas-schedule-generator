/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government under Contract No.
 * DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.utilities;

import java.util.Iterator;



/**
 * 
 * @author cunningham
 */
public class UnmodifiableIterator<T> implements Iterator<T> {
	
	private Iterator<T> innerSet;

	public UnmodifiableIterator(Iterator<T> inner) {
		this.innerSet = inner;
	}

	public boolean hasNext() {
		return innerSet.hasNext();
	}

	public T next() {
		return innerSet.next();
	}

	public void remove() {
		throw new IllegalStateException("unmodifiable");
	}
	
}