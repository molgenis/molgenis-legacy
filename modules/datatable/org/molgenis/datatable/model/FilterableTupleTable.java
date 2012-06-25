package org.molgenis.datatable.model;

import java.util.List;

import org.molgenis.framework.db.QueryRule;

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
	 */
	public void setFilters(List<QueryRule> rules);

	/**
	 * Get the column filters
	 */
	public List<QueryRule> getFilters();

	/**
	 * This we can inherit from Query interface?
	 */
	public int getLimit();


	/**
	 * This we can inherit from Query interface?
	 */
	public int getOffset();


	/**
	 * This we can inherit from Query interface?
	 * 
	 * nb: multiple columns are not allowed for now.
	 */
	public QueryRule getSortRule();

}