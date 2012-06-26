//

package org.molgenis.model.elements;


// imports
import java.util.Vector;





public class MethodQuery
{
	// 
	public static class Rule
	{
		public Rule(String field, String operator, String parameter)
		{
			this.field = field;
			this.operator = operator;
			this.parameter = parameter;
		}
		
		
		public String getField()
		{
			return field;
		}
		
		public String getOperator()
		{
			return operator;
		}
		
		public String getParameter()
		{
			return parameter;
		}
		
		
		// data
		protected String field;
		protected String operator;
		protected String parameter;
	}
	
	
	//
	public MethodQuery(String entity)
	{
		this.entity = entity;
	}
	
	
	public String getEntity()
	{
		return entity;
	}
	
	public void addRule(Rule rule)
	{
		rules.add(rule);
	}
	
	public Vector<Rule> getRules()
	{
		return rules;
	}
	
	
	// data
	protected String entity;
	protected Vector<Rule> rules = new Vector<Rule>();
}
