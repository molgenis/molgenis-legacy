package org.molgenis.matrix.browser;


/**
 * Matrix browser takes a matrix and allows the user to view subsets.
 * Optionally, data can be sorted on one or more rows, or one or more columns.
 * Also filterings can be applied on rows or columns. Finally, limits and
 * offsets can be applied.
 * 
 * @author mswertz
 * 
 */
public interface SortableMatrix
{
	/** Sort on these rows (indicated by rowname) */
	public void sortRows(String[] rowNames);
	
	/** Sort on these rows (indicated by rowindex) */
	public void sortRows(Integer[] rowIndexes);
	
	/** Sort on these cols (indicated by colname) */
	public void sortCols(String[] colNames);
	
	/** Sort on these cols (indicated by colindexes) */
	public void sortCols(Integer[] colIndexes);
}
