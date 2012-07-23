package org.molgenis.datatable.model;

import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.model.elements.Form.SortOrder;

/**
 * Extension of TupleTable that allows filterings, sortings, etc on the table.
 * 
 * Should replace org.molgenis.framework.db.paging.DatabasePager (but without
 * all the additionall fuss). We should also look if we can reuse parts of the
 * org.molgenis.db.Query for this (i.e. factor out common parts).
 */
public interface FilterableTupleTable extends TupleTable
{
	/**
	 * Set the row filters
	 * @throws TableException 
	 */
	public void setFilters(List<QueryRule> rules) throws TableException;

	/**
	 * Get the column filters
	 */
	public List<QueryRule> getFilters();

	/**
	 * Limit the visible columns by name
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