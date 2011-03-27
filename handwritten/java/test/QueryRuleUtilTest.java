package test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.QueryRuleUtil;

public class QueryRuleUtilTest
{ 
	@Test
	public void testRESTconversion()
	{
		QueryRule[] testSet = new QueryRule[]
		{ new QueryRule("field", Operator.GREATER_EQUAL, "val>=ue"),
			new QueryRule("field2", Operator.LESS_EQUAL, "val!=ue"),
			new QueryRule("fin",Operator.IN,"a,b"),
			new QueryRule(Operator.SORTASC,"field")};

		for(QueryRule r: testSet)
		{
			String rest = QueryRuleUtil.toRESTstring(r);
			System.out.println("original: "+r);
			System.out.println("REST: "+rest);

			List<QueryRule> rules = QueryRuleUtil.fromRESTstring(rest);

			for (QueryRule r2 : rules)
			{
				System.out.println("fromREST: " + r2);
			}

			assertEquals(r,rules.get(0));
		}
	}
}
