package org.molgenis.matrix.component.general;

import org.molgenis.framework.db.QueryRule;

public class Filter {

	/**
	 * The queryrule of this filter. @see: SliceableMatrix
	 */
	private QueryRule queryRule;

	/**
	 * The type of filter that was applied. @see: SliceableMatrix
	 *
	 */
	public enum Type {
		rowHeader, colHeader, rowValues, colValues, index, paging
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
