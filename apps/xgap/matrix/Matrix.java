package matrix;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.ObservedValue;

import app.DatabaseFactory;

public class Matrix<T> {
	private T[][] data;
	private static Database db;
	
	static {
		try {
			db = DatabaseFactory.create("molgenis.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Class<T> klass;
	
	private HashMap<Integer, Integer> targetMap;
	private HashMap<Integer, Integer> targetMapInv;
	
	private HashMap<Integer, Integer> observeMap;
	
	public Matrix(Class<T> klass) throws FileNotFoundException, IOException, DatabaseException {
		this.klass = klass;
	}

	public void LoadDataFromDatabase(int[] observationTargetIds, 
		     int[] observableFeatureIds) throws DatabaseException, ParseException, SQLException {
		List<Integer> targets = new ArrayList<Integer>(observationTargetIds.length);
		for(Integer i : observationTargetIds) {
			targets.add(i);
		}
		List<Integer> observations = new ArrayList<Integer>(observableFeatureIds.length);
		for(Integer i : observableFeatureIds) {
			observations.add(i);
		}
		LoadDataFromDatabase(targets, observations);
	}

	
	public static List<Integer> createWhereSql(boolean isNested, boolean withOffset, QueryRule... rules) throws NumberFormatException, DatabaseException, SQLException 
	{
		List<Integer> targetIds = null;
		QueryRule previousRule = new QueryRule(Operator.AND);
		for (QueryRule r : rules) {
			if( r.getOperator().equals(Operator.OR)  || r.getOperator().equals(Operator.AND))
			{
				previousRule = r;
			}
			else
			{
				QueryRule rule = new QueryRule(r); // copy because of side
				// effects
				// logger.debug(rule);

				// String tablePrefix = "";
				//if (mapper != null) rule.setField(mapper.getTableFieldName(rule.getField()));

				if (rule.getOperator() == Operator.LAST || rule.getOperator() == Operator.LIMIT
						|| rule.getOperator() == Operator.OFFSET || rule.getOperator() == Operator.SORTASC
						|| rule.getOperator() == Operator.SORTDESC)
				{
					
				}
				else if (rule.getOperator() == QueryRule.Operator.NESTED)
				{
					QueryRule[] nestedrules = rule.getNestedRules();
					List<Integer> targets = createWhereSql(true, false, nestedrules);
					if(targetIds == null) {
						if (previousRule != null && Operator.OR.equals(previousRule.getOperator())) {
							
						} else if (previousRule != null && Operator.AND.equals(previousRule.getOperator())) {
							targetIds = andIds(targetIds, targets);
						}							
					} else {
						targetIds = targets;
					}
				}
				else if (rule.getOperator() == QueryRule.Operator.IN)
				{
//					// only add if nonempty condition???
//					if (rule.getValue() == null
//							|| (rule.getValue() instanceof List && ((List) rule.getValue()).size() == 0)
//							|| (rule.getValue() instanceof Object[] && ((Object[]) rule.getValue()).length == 0)) throw new DatabaseException(
//							"empty 'in' clause for rule " + rule);
//					{
//						if (where_clause.length() > 0)
//						{
//							if (previousRule != null && Operator.OR.equals(previousRule.getOperator()))
//							{
//								where_clause.append(" OR ");
//							}
//							else
//							{
//								where_clause.append(" AND ");
//							}
//						}
//
//						// where_clause.append(tablePrefix + rule.getField() +
//						// " IN(");
//						where_clause.append(rule.getField() + " IN(");
//
//						Object[] values = new Object[0];
//						if (rule.getValue() instanceof List)
//						{
//							values = ((List<Object>) rule.getValue()).toArray();
//						}
//						else
//						{
//							values = (Object[]) rule.getValue();
//						}
//
//						for (int i = 0; i < values.length; i++)
//						{
//							if (i > 0) where_clause.append(",");
//							if (mapper != null && omitQuotes(mapper.getFieldType(rule.getField())))
//							{
//								// where_clause.append(values[i]
//								// .toString());
//								where_clause.append("" + escapeSql(values[i]) + "");
//							}
//							else
//							{
//								where_clause.append("'" + escapeSql(values[i]) + "'");
//							}
//						}
//						where_clause.append(") ");
//					}
				}
				else
				// where clause
				{
					// check validity of the rule
					// if(rule.getField() == null ||
					// columnInfoMap.get(rule.getField()) == null )
					// {
					// throw new DatabaseException("Invalid rule: field '"+
					// rule.getField() + "' not known.");
					// }
					
					
					String operator = "";
					switch (rule.getOperator())
					{
						case EQUALS:				
							operator = "=";
							break;
						case JOIN:
							operator = "=";
							break;
						case NOT:
							operator = "!=";
							break;
						case LIKE:
							operator = "LIKE";
							break;
						case LESS:
							operator = "<";
							break;
						case GREATER:
							operator = ">";
							break;
						case LESS_EQUAL:
							operator = "<=";
							break;
						case GREATER_EQUAL:
							operator = ">=";
							break;
					}
					List<Integer> targets = executeCondition(Integer.parseInt(rule.getField()), operator, rule.getValue(), targetIds);
					
					
					// if (rule.getField() != "" && operator != "" &&
					// rule.getValue() != "")
					// {
//					if (targetIds != null)
//					{
					
					
					if (previousRule != null && Operator.OR.equals(previousRule.getOperator()))
					{
						
						//where_clause.append(" OR ");
					}
					else
					{
						targetIds = andIds(targetIds, targets);
						//where_clause.append(" AND ");
					}
//					}
					if (Boolean.TRUE.equals(rule.getValue())) rule.setValue("1");
					if (Boolean.FALSE.equals(rule.getValue())) rule.setValue("0");
					Object value = rule.getValue() == null ? "NULL" : escapeSql(rule.getValue());

					if (!value.equals("NULL") && rule.getOperator() == Operator.LIKE)
							//&& (mapper == null || !omitQuotes(mapper.getFieldType(rule.getField()))))
					{
						if (!value.toString().trim().startsWith("%") && !value.toString().trim().endsWith("%"))
						{
							value = "%" + value + "%";
						}
					}

					// if
					// (omitQuotes(columnInfoMap.get(rule.getField()).getType()))
					// where_clause.append(tablePrefix + rule.getField() + " " +
					// operator + " " + value + "");
					// else
					// where_clause.append(tablePrefix + rule.getField() + " " +
					// operator + " '" + value + "'");
//					if (rule.getOperator().equals(Operator.JOIN)) 
//						where_clause.append(rule.getField() + " " + operator + " " + value + "");
//					else
//					{
//						if ("NULL".equals(value) && operator.equals("=")) 
//							where_clause.append(rule.getField() + " IS NULL");
//						else
//							where_clause.append(rule.getField() + " " + operator + " '" + value + "'");
//					}
				}
				previousRule = null;
			}
		}
		return targetIds;
	}
	
	public static String escapeSql(Object value)
	{
		if (value != null) return StringEscapeUtils.escapeSql(value.toString().replace("'", "''"));
		return null;
		// return sql.toString().replace("'", "''");
	}	

	public static List<Integer> executeCondition(int featureId, String operator, Object value, List<Integer> potentialTargets) throws DatabaseException, SQLException {
		List<Integer> targetIds = new ArrayList<Integer>();
		String sql = "SELECT observationTarget FROM ObservedValue WHERE observableFeature = %s AND value %s %s %s";
		if(potentialTargets != null && potentialTargets.size() > 0) {
			sql = String.format(sql, featureId, operator, value, " AND observationTarget IN (" +toSqlArray(potentialTargets) + ")");
		} else {
			sql = String.format(sql, featureId, operator, value, "");
		}
		
		ResultSet rs = db.executeQuery(sql);
		while(rs.next()) {
			targetIds.add(rs.getInt(1));
		}
		return targetIds;
	}
	
	public static List<Integer> andIds(List<Integer> l0, List<Integer> l1) {
		if(l0 == null) {
			return l1;
		}		
		if(l1.size() > l0.size()) {
			return andIds(l1, l0);
		}
		
		List<Integer> result = new ArrayList<Integer>();
		for(Integer i : l0) {
			if(l1.contains(i)) {
				result.add(i);
			}
		}
		return result;
	}
	
	public void LoadDataFromDatabase(List<Integer> observationTargetIds, 
								     List<Integer> observableFeatureIds) throws DatabaseException, ParseException, SQLException {
		data = (T[][])Array.newInstance(klass, observationTargetIds.size(), observableFeatureIds.size());
		
		targetMap = new HashMap<Integer, Integer>();
		targetMapInv = new HashMap<Integer, Integer>();
		
		observeMap = new HashMap<Integer, Integer>();
		
		for(int i = 0; i < observationTargetIds.size(); ++i) {
			targetMap.put(observationTargetIds.get(i), i);
			targetMapInv.put(i, observationTargetIds.get(i));
		}
		for(int i = 0; i < observableFeatureIds.size(); ++i) {
			observeMap.put(observableFeatureIds.get(i), i);
		}
		
		List<ObservedValue> values = 
			db.query(ObservedValue.class)
				.in("observableFeature", observableFeatureIds)
				.in("observationTarget", observationTargetIds).find();
		
		for(ObservedValue ov : values) {
			int targetId = ov.getTarget_Id();
			int featureId = ov.getFeature_Id();
			data[targetMap.get(targetId)][observeMap.get(featureId)] = (T)ov.getValue();
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < data.length; ++i) {
			sb.append(targetMapInv.get(i) + "|");			
			for(int j = 0; j < data[i].length; ++j) {
				sb.append(data[i][j]);
				sb.append(" | ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	public List<ArrayList<String>> toList() {
		List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		for(int i = 0; i < data.length; ++i) {
			result.add(new ArrayList<String>());
			result.get(0).add("" + targetMapInv.get(i));
		
			for(int j = 0; j < data[i].length; ++j) {
				result.get(0).add("" + data[i][j]);
			}
		}
		return result;
	}
	

	public static void main(String[] args) throws FileNotFoundException, IOException, DatabaseException, ParseException, SQLException {
		Matrix<String> m = new Matrix<String>(String.class);
		
		QueryRule queryRule = new QueryRule("435", Operator.EQUALS, "1");
		QueryRule queryRule2 = new QueryRule("436", Operator.GREATER, "1969-01-01");
		
		List<Integer> newTargets = createWhereSql(false,false, queryRule, queryRule2);
		
		//List<Integer> targets = Matrix.executeEqualCondition(435, "1", potentialTargets);
		m.LoadDataFromDatabase(toArray(newTargets), new int[]{436,435});
		
		System.err.println(m.toString());
	}
	
	public static int[] toArray(List<Integer> list) {
		int[] result = new int[list.size()];
		for(int i = 0; i < result.length; ++i) {
			result[i] = list.get(i);
		}
		return result;
	}
	
	public static String toSqlArray(List<Integer> list) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < list.size(); ++i) {
			sb.append(list.get(i));
			if(i + 1 < list.size()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
}

