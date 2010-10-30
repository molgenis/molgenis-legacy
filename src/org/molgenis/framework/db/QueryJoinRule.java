package org.molgenis.framework.db;


public class QueryJoinRule extends QueryRule {
	String a;
	String b;

	public QueryJoinRule(String entity1, String field1, String entity2, String field2) {
		super(entity1 + "." + field1, Operator.JOIN, entity2 + "." + field2);
		a = entity1;
		b = entity2;
	}

	public String getA() {
		return a;
	}

	public String getB() {
		return b;
	}
}
