package org.molgenis.matrix.component;

import org.molgenis.framework.db.QueryRule;

public class Filter {

	/**
	 * The queryrule of this filter. TODO: some explaining
	 */
	private QueryRule queryRule;

	/**
	 * Filter on 'rowHeader' means: e.g. if row header are entities of type
	 * Indivual, and the query is 'age < 30', then the resulting matrix will
	 * have fewer row headers (rows). TODO: other examples.
	 * 
	 */
	private enum FilterType {
		rowHeader, colHeader, rowValues, colValues
	}
	private FilterType filterType;
	
	/**
	 * The index of the element in the matrix to apply the filter to.
	 */
	private int index;
	
	public Filter(QueryRule queryRule, FilterType filterType, int index){
		this.queryRule = queryRule;
		this.filterType = filterType;
		this.index = index;
	}

	public QueryRule getQueryRule() {
		return queryRule;
	}

	public FilterType getFilterType() {
		return filterType;
	}

	public int getIndex() {
		return index;
	}
	
}
