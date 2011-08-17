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
	public enum Type {
		rowHeader, colHeader, rowValues, colValues, rowIndex, colIndex
	}
	private Type filterType;
	
	/**
	 * The index of the element in the matrix to apply the filter to.
	 */
	private int index;
	
	public Filter(Type filterType, QueryRule queryRule){
		this.queryRule = queryRule;
		this.filterType = filterType;
	}
	
	public Filter(Type filterType, QueryRule queryRule, int index){
		this.queryRule = queryRule;
		this.filterType = filterType;
		this.index = index;
	}

	public QueryRule getQueryRule() {
		return queryRule;
	}

	public Type getFilterType() {
		return filterType;
	}

	public int getIndex() {
		return index;
	}
	
}
