package org.molgenis.matrix.component.interfaces;

import java.util.List;

public interface BasicMatrix<R, C, V> {
	
	//renamed to getRowHeaders to be consistent with SliceableMatrix
	@Deprecated
	public List<R> getVisibleRows() throws Exception;
	
	/**
	 * The currently selected rows that are going to be rendered in the view.
	 */
	public List<R> getRowHeaders() throws Exception;
	
	//renamed to getColHeaders to be consistent with SliceableMatrix
	@Deprecated
	public List<C> getVisibleCols() throws Exception;
	
	/**
	 * The currently selected columns that are going to be rendered in the view.
	 */
	public List<C> getColHeaders() throws Exception;
	
	/**
	 * The indices of the currently selected row elements.
	 */
	public List<Integer> getRowIndices() throws Exception;
	
	/**
	 * The indices of the currently selected column elements.
	 */
	public List<Integer> getColIndices() throws Exception;
	
	@Deprecated
	//renamed to getValues() as basicmatrix is not sliced
	public V[][] getVisibleValues() throws Exception;
	
	/**
	 * The currently selected matrix values that are going to be rendered in the view.
	 */
	public V[][] getValues() throws Exception;
	
	
}
