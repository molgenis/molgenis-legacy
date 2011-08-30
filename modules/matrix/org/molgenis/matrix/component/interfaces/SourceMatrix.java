package org.molgenis.matrix.component.interfaces;

import java.util.List;

public interface SourceMatrix<R, C, V> {

	/**
	 * Describes what is in the rows. Displayed at the row pager. E.g. when
	 * returning "Individual" -> "Individual 1-10 of 430".
	 * 
	 * @throws Exception
	 */
	public String getRowType() throws Exception;

	/**
	 * Describes what is in the columns. Displayed at the column pager. E.g.
	 * when returning "Marker" -> "Marker 1-50 of 3000".
	 */
	public String getColType() throws Exception;

	/**
	 * Get an instance of RenderDescriptor which tells the render how to
	 * visualize matrix headers and values
	 */
	public RenderDescriptor<R, C, V> getRenderDescriptor() throws Exception;

	/**
	 * The maximum number of rows in this matrix you could possible browse or
	 * filter.
	 */
	public int getTotalNumberOfRows();

	/**
	 * The maximum number of columns in this matrix you could possible browse or
	 * filter.
	 */
	public int getTotalNumberOfCols();

	/**
	 * Tells the renderer on which row header attributes the user can filter. If
	 * the row headers are strings, you could implement this with just "value".
	 * If your headers are entities, you might want to fill this list with the
	 * attributes of the entity.
	 */
	public List<String> getRowHeaderFilterAttributes();

	/**
	 * Same as getRowHeaderFilterAttributes() except for columns.
	 */
	public List<String> getColHeaderFilterAttributes();

}
