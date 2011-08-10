package org.molgenis.matrix.component;

import java.util.List;


/**
 * Tells the Matrix browser what to render. We leave it up to the user to construct or manipulate RenderableMatrix.
 * 
 * @author erikroos, joerivandervelde
 *
 * @param <R>
 * @param <C>
 * @param <V>
 */
public interface RenderableMatrix<R, C, V> {

	public String renderValue(V value);
	
	public String renderRow(R row);
	
	public String renderCol(C col);
	
	public List<R> getVisibleRows();
	
	public List<C> getVisibleCols();
	
	public V[][] getVisibleValues();
	
	public int getTotalNumberOfRows();
	
	public int getTotalNumberOfCols();

	public int getFilteredNumberOfRows();
	
	public int getFilteredNumberOfCols();
	
	public int getRowIndex();
	
	public int getColIndex();
	
	public List<Filter> getFilters();
	
	public String getConstraintLogic();
	
	// TODO: add StepSize?
	
}
