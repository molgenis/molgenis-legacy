package org.molgenis.datatable.model;

import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;

/**
 * Extension of TupleTable that allows filterings, sortings, etc on the table.
 */
public interface FilterableTupleTable extends TupleTable
{
	/**
	 * Set the row filters, based on column names. Not allowed are LIMIT, OFFSET
	 * and SORT filters (@see setLimit, setOffset), which will throw
	 * TableException.
	 * 
	 * @throws TableException
	 */
	public void setFilters(List<QueryRule> rules) throws TableException;

	/**
	 * Get the current set of filters
	 */
	public List<QueryRule> getFilters();

	/**
	 * Limit the columns available in this table
	 */
	public void setVisibleColumns(List<String> fieldNames);

	/**
	 * Get visible columns
	 */
	public List<Field> getVisibleColumns();

	/**
	 * This we can inherit from Query interface?
	 * 
	 * nb: multiple columns are not allowed for now.
	 */
	public QueryRule getSortRule();
}