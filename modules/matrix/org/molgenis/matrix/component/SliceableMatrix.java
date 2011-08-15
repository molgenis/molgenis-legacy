package org.molgenis.matrix.component;

import org.molgenis.framework.db.QueryRule;

public interface SliceableMatrix {
	
	/**
	 * Grab a matrix slice from the data backend by specifying coordinates. Row- and column indices start at 0 for the first element.
	 * Retrieving nRows 1, nCols 1 from index 0,0 returns the first (upper left most) element of the matrix. Also pass along the stepsize.
	 * @param matrix
	 * @param rowIndex
	 * @param nRows
	 * @param colIndex
	 * @param nCols
	 * @return
	 * @throws Exception
	 */
	public RenderableMatrix getSubMatrixByOffset(RenderableMatrix matrix, int rowIndex, int nRows, int colIndex, int nCols, int stepSize) throws Exception;
	
	/**
	 * Grab a matrix slice from the data backend by specifying a filter applied to the matrix values in a certain row.
	 * TODO: example queryrule - steal this from XGAP :)
	 * @param matrix
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public RenderableMatrix getSubMatrixByRowValueFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
	
	/**
	 * Grab a matrix slice from the data backend by specifying a filter applied to one of the row headers.
	 * TODO: example queryrule - steal this from XGAP :)
	 * @param matrix
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public RenderableMatrix getSubMatrixByRowHeaderFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
	
	/**
	 * Grab a matrix slice from the data backend by specifying a filter applied to the matrix values in a certain column.
	 * TODO: example queryrule - steal this from XGAP :)
	 * @param matrix
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public RenderableMatrix getSubMatrixByColValueFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
	
	/**
	 * Grab a matrix slice from the data backend by specifying a filter applied to one of the column headers.
	 *  TODO: example queryrule - steal this from XGAP :)
	 * @param matrix
	 * @param q
	 * @return
	 * @throws Exception
	 */
	public RenderableMatrix getSubMatrixByColHeaderFilter(RenderableMatrix matrix, QueryRule q) throws Exception;
}
