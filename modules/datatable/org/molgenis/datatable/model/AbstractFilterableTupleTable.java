package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;

public abstract class AbstractFilterableTupleTable extends AbstractTupleTable
implements FilterableTupleTable {

	private List<QueryRule> filters = new ArrayList<QueryRule>();

	protected AbstractFilterableTupleTable() {
	}

	protected AbstractFilterableTupleTable(List<QueryRule> rules) {
		if (rules != null) {
			filters = rules;
		}
	}

	@Override
	public void setFilters(List<QueryRule> rules) throws TableException {
		if (rules == null) {
			throw new NullPointerException("rules cannot be null");
		}

		for (final QueryRule r : rules) {
			if (Operator.LIMIT.equals(r.getOperator()) || Operator.LIMIT.equals(r.getOperator())) {
				throw new TableException("TupleTable doesn't support LIMIT or OFFSET QueryRules; use setLimit and setOffset instead");
			}
		}
		filters = rules;
	}

	@Override
	public List<QueryRule> getFilters() {
		return filters;
	}

	@Override
	public QueryRule getSortRule() {
		final QueryRule sortAsc = getRule(Operator.SORTASC);
		if (sortAsc != null) {
			return sortAsc;
		} else {
			return getRule(Operator.SORTDESC);
		}
	}

	private QueryRule getRule(final Operator operator) {
		final QueryRule rule = (QueryRule) CollectionUtils.find(filters, new Predicate() {
			public boolean evaluate(Object arg0) {
				return ((QueryRule) arg0).getOperator() == operator;
			}
		});
		return rule;
	}

	@Override
	public void setVisibleColumns(List<String> fieldNames) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public List<Field> getVisibleColumns() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
