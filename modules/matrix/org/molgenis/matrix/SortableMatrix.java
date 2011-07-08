package org.molgenis.matrix;

import java.util.List;

/**
 * Adds sorting capability to a Matrix
 */
public interface SortableMatrix<E> extends Matrix<E>
{

	/** 
	 * Sort on these rows (indicated by rowname) 
	 */
	public abstract void sortRowsByName(List<String> rowNames);

	/** 
	 * Sort on these rows (indicated by rowindex) 
	 */
	public abstract void sortRowsByIndex(List<Integer> rowIndexes);

	/** 
	 * Sort on these cols (indicated by colname) 
	 */
	public abstract void sortColumnsByName(List<String> colNames);

	/** 
	 * Sort on these cols (indicated by colindexes) 
	 */
	public abstract void sortColumnsByIndex(List<Integer> colIndexes);

}