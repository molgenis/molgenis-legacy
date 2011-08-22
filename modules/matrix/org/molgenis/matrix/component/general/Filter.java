package org.molgenis.matrix.component.general;

public class Filter {

	/**
	 * The queryrule of this filter. @see: SliceableMatrix
	 */
	private MatrixQueryRule queryRule;

	/**
	 * The type of filter that was applied. @see: SliceableMatrix
	 *
	 */
	public enum Type {
		rowHeader, colHeader, rowValues, colValues, index, paging
	}
	private Type filterType;
	
	public Filter(Type filterType, MatrixQueryRule queryRule){
		this.queryRule = queryRule;
		this.filterType = filterType;
	}

	public MatrixQueryRule getQueryRule() {
		return queryRule;
	}

	public Type getFilterType() {
		return filterType;
	}

	public String toString(){
		return "Filter: type = " + this.getFilterType() + ", queryrule = " + this.getQueryRule().toString();
	}
}
