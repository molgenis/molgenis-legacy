package org.molgenis.framework.db;

import org.apache.commons.lang.StringUtils;
import org.molgenis.util.Entity;

public class SubQueryRule extends QueryRule
{
	private final Class subQueryResultClass;
	private final Class subQueryFromClass;
	private final Operator subQueryOperator;
	private final String subQueryField;
	
	public SubQueryRule(String fieldName, Operator subQueryOperator, String subQueryField, Class subQueryResultClass, Class<? extends Entity> subQueryFromClass, QueryRule... subQueryRules) {
		super(fieldName, Operator.SUBQUERY, subQueryRules);
		this.subQueryResultClass = subQueryResultClass;
		this.subQueryFromClass = subQueryFromClass;
		this.subQueryOperator = subQueryOperator;
		this.subQueryField = subQueryField;
	}

	public Class getSubQueryResultClass()
	{
		return subQueryResultClass;
	}

	public Class getSubQueryFromClass()
	{
		return subQueryFromClass;
	}

	public Operator getSubQueryOperator()
	{
		return subQueryOperator;
	}
	
	public String getSubQueryField() {
		return subQueryField;
	}	
	
	public String getSubQueryAttributeJpa() {
	    if(!StringUtils.isEmpty(subQueryField)) {
	        return subQueryField.substring(0,1).toLowerCase() + subQueryField.substring(1);
	    }
	    return subQueryField;
	}
}
