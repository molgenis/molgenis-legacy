package org.molgenis.matrix;

import java.util.List;

import org.molgenis.framework.db.QueryRule;

/**
 * Interface that adds filtering capabilities to a matrix
 * At some point we want to merge this with Matrix so that all our matrices become editable.
 */
public interface FilterableMatrix<E,A,V> extends Matrix<E,A,V>
{

	/** Set the filter on rows. The name in the QueryRule denotes the row name.	 */
	public void setRowFilters(List<QueryRule> filters);

	/** Set the filter on columns. The name in the QueryRule denotes the column name. */
	public void setColFilters(List<QueryRule> filters);

}