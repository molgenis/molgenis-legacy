package org.molgenis.matrix.component.interfaces;

import java.util.List;

// TODO merge this with the rendere as it is MatrixMetadata
public interface SourceMatrix<R, C, V>
{

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
	 * visualize matrix headers and values. This is only a default, individual
	 * renders can decide to render otherwise.
	 */
	@Deprecated
	//In my mind this should be part of renderable matrix?
	//In particular because you can derive this from ColType, RowType
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

	// rename to getColHeaderPropertyNames
	@Deprecated
	public List<String> getRowHeaderFilterAttributes();
	
	/**
	 * Tells the renderer on which row header attributes the user can filter. If
	 * the row headers are strings, you could implement this with just "value".
	 * If your headers are entities, you might want to fill this list with the
	 * attributes of the entity.
	 */
	public List<String> getRowPropertyNames();
	
	// rename to getColHeaderPropertyNames
	@Deprecated
	public List<String> getColHeaderFilterAttributes();
	
	/**
	 * Same as getRowHeaderFilterAttributes() except for columns.
	 */
	public List<String> getColPropertyNames();

}
