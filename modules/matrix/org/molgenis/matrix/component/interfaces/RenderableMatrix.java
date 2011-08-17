package org.molgenis.matrix.component.interfaces;

import java.util.List;

import org.molgenis.matrix.component.Filter;


/**
 * Tells the Matrix browser what to render. We leave it up to the user to construct or manipulate RenderableMatrix.
 * 
 * @author erikroos, joerivandervelde
 *
 * @param <R>
 * @param <C>
 * @param <V>
 */
public interface RenderableMatrix<R, C, V> extends BasicMatrix<R, C, V>, SourceMatrix<R, C, V>{
	
	/**
	 * The list of filters that was applied to reach the currently rendered matrix.
	 */
	public List<Filter> getFilters();
	
	/**
	 * The constraint logic that was used to parse the filters in a special way.
	 */
	public String getConstraintLogic();
	
	/**
	 * Show the stepsize on the screen. This is the number of cells, or 'speed' with
	 * which the matrix moves when move (panning) actions are performed.
	 */
	public int getStepSize();
	
	/**
	 * Show the column start index of the visible values
	 */
	public int getColIndex();
	
	/**
	 * Show the row start index of the visible values
	 */
	public int getRowIndex();

}
