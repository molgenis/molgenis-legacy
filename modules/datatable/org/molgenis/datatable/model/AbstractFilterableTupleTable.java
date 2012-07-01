package org.molgenis.datatable.model;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

public abstract class AbstractFilterableTupleTable implements FilterableTupleTable
{
	protected List<QueryRule> rules = Collections.emptyList();

	protected AbstractFilterableTupleTable()
	{}
	
	protected AbstractFilterableTupleTable(List<QueryRule> rules)
	{
		if(rules != null) {
			this.rules = rules;
		}
	}

	private Object getRuleValue(final Operator operator)
	{
		if (CollectionUtils.isEmpty(rules))
		{
			return -1;
		}
		else
		{
			final QueryRule rule = getRule(operator);
			return rule == null ? -1 : rule.getValue();
		}
	}

	private QueryRule getRule(final Operator operator)
	{
		final QueryRule rule = (QueryRule) CollectionUtils.find(rules, new Predicate()
		{
			@Override
			public boolean evaluate(Object arg0)
			{
				return ((QueryRule) arg0).getOperator() == operator;
			}
		});
		return rule;
	}

	@Override
	public void setFilters(List<QueryRule> rules)
	{
		this.rules = rules;
	}

	@Override
	public List<QueryRule> getFilters()
	{
		return rules;
	}

	@Override
	public int getLimit()
	{
		return (Integer) getRuleValue(Operator.LIMIT);
	}

	@Override
	public int getOffset()
	{
		return (Integer) getRuleValue(Operator.OFFSET);
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
}
