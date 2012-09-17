package org.molgenis.matrix;

import java.util.List;

/**
 * Adds sorting capability to a Matrix
 * At some point we want to merge this with Matrix so that all our matrices become editable.
 */
public interface SortableMatrix<E,A,V> extends Matrix<E,A,V>
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