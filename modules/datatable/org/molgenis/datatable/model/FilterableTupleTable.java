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
 * 
 * TODO: make implement the Query interface for easy manipulation
 */
public interface FilterableTupleTable extends TupleTable
{
	/** Get and manipulate the query rules for this TupleTable (if supported) */
	public List<QueryRule> getFilters();
	
	/** Set filters, includes validation at set time. */
	public void setFilters(List<QueryRule> filters);
}