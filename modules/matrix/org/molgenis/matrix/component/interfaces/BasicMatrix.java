package org.molgenis.matrix.component.interfaces;

import java.util.List;

public interface BasicMatrix<R, C, V> {
	
	/**
	 * The currently selected rows that are going to be rendered in the view.
	 */
	public List<R> getVisibleRows() throws Exception;
	
	/**
	 * The currently selected columns that are going to be rendered in the view.
	 */
	public List<C> getVisibleCols() throws Exception;
	
	/**
	 * The indices of the currently selected row elements.
	 */
	public List<Integer> getRowIndices() throws Exception;
	
	/**
	 * The indices of the currently selected column elements.
	 */
	public List<Integer> getColIndices() throws Exception;
	
	/**
	 * The currently selected matrix values that are going to be rendered in the view.
	 */
	public V[][] getVisibleValues() throws Exception;
	
}
