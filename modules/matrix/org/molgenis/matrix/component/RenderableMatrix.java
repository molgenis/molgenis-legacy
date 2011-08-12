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

	/**
	 * Explains the renderer how to visualize your values from the generic type.
	 */
	public String renderValue(V value);
	
	/**
	 * Explains the renderer how to visualize your row headers from the generic type.
	 */
	public String renderRow(R row);
	
	/**
	 * Explains the renderer how to visualize your column headers from the generic type.
	 */
	public String renderCol(C col);
	
	/**
	 * The currently selected rows that are going to be rendered in the view.
	 */
	public List<R> getVisibleRows();
	
	/**
	 * The currently selected columns that are going to be rendered in the view.
	 */
	public List<C> getVisibleCols();
	
	/**
	 * The currently selected matrix values that are going to be rendered in the view.
	 * @throws Exception 
	 */
	public V[][] getVisibleValues() throws Exception;
	
	/**
	 * The maximum number of rows in this matrix you could possible browse or filter.
	 */
	public int getTotalNumberOfRows();
	
	/**
	 * The maximum number of columns in this matrix you could possible browse or filter.
	 */
	public int getTotalNumberOfCols();

	/**
	 * The number of rows in a matrix after it has been potentially filtered.
	 * Can be less than or equal to getTotalNumberOfRows().
	 */
	public int getFilteredNumberOfRows();
	
	/**
	 * The number of columns in a matrix after it has been potentially filtered.
	 * Can be less than or equal to getTotalNumberOfCols().
	 */
	public int getFilteredNumberOfCols();
	
	/**
	 * Tells the renderer the current height offset. For example, if your matrix
	 * has 10 rows, getVisibleRows has 3 entries, and the current offset is 4,
	 * the 'human readable' displayed row indices are 5-8. (offset 0 means 1-4 etc)
	 */
	public int getRowIndex();
	
	/**
	 * Tells the renderer the current height offset.
	 */
	public int getColIndex();
	
	/**
	 * The list of filters that was applied to reach the currently rendered matrix.
	 */
	public List<Filter> getFilters();
	
	/**
	 * The constraint logic that was used to parse the filters in a special way.
	 */
	public String getConstraintLogic();
	
	/**
	 * The name of the screen this component is being rendered it. Needed to know
	 * where to send requests from the various buttons. FIXME: is there a smarter way?
	 */
	public String getScreenName();
	
	
}
