package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

public abstract class AbstractFilterableTupleTable extends AbstractTupleTable implements FilterableTupleTable
{
	private List<QueryRule> filters = new ArrayList<QueryRule>();

	protected AbstractFilterableTupleTable()
	{
	}

	protected AbstractFilterableTupleTable(List<QueryRule> rules) throws TableException
	{
		this.setFilters(rules);
	}

	@Override
	public void setFilters(List<QueryRule> rules) throws TableException
	{
		if (rules == null) throw new NullPointerException("rules cannot be null");

		for (QueryRule r : rules)
		{
			if (Operator.LIMIT.equals(r.getOperator()) || Operator.LIMIT.equals(r.getOperator()))
			{
				throw new TableException(
						"TupleTable doesn't support LIMIT or OFFSET QueryRules; use setLimit and setOffset instead");
			}
		}
		this.filters = rules;
	}

	@Override
	public List<QueryRule> getFilters()
	{
		return filters;
	}

	@Override
	public QueryRule getSortRule()
	{
		final QueryRule sortAsc = getRule(Operator.SORTASC);
		if (sortAsc != null)
		{
			return sortAsc;
		}
		else
		{
			return getRule(Operator.SORTDESC);
		}
	}

	@Override
	public void setLimitOffset(int limit, int offset)
	{
		this.setLimit(limit);
		this.setOffset(offset);
	}

	// UTIL

	// private Object getRuleValue(final Operator operator)
	// {
	// if (CollectionUtils.isEmpty(filters))
	// {
	// return -1;
	// }
	// else
	// {
	// final QueryRule rule = getRule(operator);
	// return rule == null ? -1 : rule.getValue();
	// }
	// }

	private QueryRule getRule(final Operator operator)
	{
		final QueryRule rule = (QueryRule) CollectionUtils.find(filters, new Predicate()
		{
			@Override
			public boolean evaluate(Object arg0)
			{
				return ((QueryRule) arg0).getOperator() == operator;
			}
		});
		return rule;
	}
}
