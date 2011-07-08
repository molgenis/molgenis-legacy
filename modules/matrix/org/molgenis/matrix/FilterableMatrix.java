package org.molgenis.matrix;

import java.util.List;

import org.molgenis.framework.db.QueryRule;

/**
 * Interface that adds filtering capabilities to a matrix
 */
public interface FilterableMatrix<E> extends Matrix<E>
{

	/** Set the filter on rows. The name in the QueryRule denotes the row name.	 */
	public abstract void setRowFilters(List<QueryRule> filters);

	/** Set the filter on columns. The name in the QueryRule denotes the column name. */
	public abstract void setColFilters(List<QueryRule> filters);

}