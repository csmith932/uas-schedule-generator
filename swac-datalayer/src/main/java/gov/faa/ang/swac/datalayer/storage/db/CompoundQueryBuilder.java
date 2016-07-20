/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Adapter that allows multiple queryBuilders to be used in a method that only takes one query builder. 
 * 
 * @author cunningham
 */
public class CompoundQueryBuilder implements QueryBuilder {
	private Collection<QueryBuilder> queryBuilders;

	public CompoundQueryBuilder(QueryBuilder... queryBuilders) {
		this(new ArrayList<QueryBuilder>(Arrays.asList(queryBuilders)));
	}
	
	public CompoundQueryBuilder(Collection<QueryBuilder> queryBuilders) { 
		this.queryBuilders = queryBuilders;
	}

	public String toQueryString() {
		throw new UnsupportedOperationException("toQueryString");
	}
	
	@Override
	public void addIntField(String name) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addIntField(name);
		}
	}

	@Override
	public void addIntField(String name, long minValue, long maxValue) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addIntField(name, minValue, maxValue);
		}
	}

	@Override
	public void addVarCharField(String name) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addVarCharField(name);
		}
	}

	@Override
	public void addVarCharField(String name, int size) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addVarCharField(name, size);
		}
	}

	@Override
	public void addDoubleField(String name) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addDoubleField(name);
		}
	}

	@Override
	public void addDoubleField(String name, int precision, int scale) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addDoubleField(name, precision, scale);
		}
	}

	@Override
	public void addBooleanField(String name) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addBooleanField(name);
		}
	}

	@Override
	public <T extends Enum<T>> void addEnumField(String name, Class<T> anEnum) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addEnumField(name, anEnum);
		}
	}

	@Override
	public void addDateField(String name) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addDateField(name);
		}
	}

	@Override
	public void addDateTimeField(String name) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addDateTimeField(name);
		}
	}

	@Override
	public void addFieldWithUnits(String name, String units) {
		for (QueryBuilder queryBuilder : queryBuilders) {
			queryBuilder.addFieldWithUnits(name, units);
		}
	}
}