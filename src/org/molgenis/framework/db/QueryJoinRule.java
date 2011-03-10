package org.molgenis.framework.db;


public class QueryJoinRule extends QueryRule {
	String e1;
	String e2;
	String f1;
	String f2;

	public QueryJoinRule(String entity1, String field1, String entity2, String field2) {
		super(entity1 + "." + field1, Operator.JOIN, entity2 + "." + field2);
		e1 = entity1;
		e2 = entity2;
		f1 = field1;
		f2 = field2;
	}

	public String getEntity1() {
		return e1;
	}

	public String getEntity2() {
		return e2;
	}
	
	public String getField1(){
		return f1;
	}
	
	public String getField2()
	{
		return f2;
	}

	public void setEntity1(String name)
	{
		e1 = name;
		this.setField(e1+"."+f1);
		
	}

	public void setEntity2(String name)
	{
		e2 = name;
		this.setValue(e2+"."+f2);
	}
}
