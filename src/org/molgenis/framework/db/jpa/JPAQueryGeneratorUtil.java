package org.molgenis.framework.db.jpa;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.Metamodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field.Type;

import org.molgenis.util.Entity;

/**
 * @author jorislops
 */
public class JPAQueryGeneratorUtil {

	private transient static Log logger = LogFactory
			.getLog("JPAQueryGeneratorUtil");

	public static <IN extends Entity> TypedQuery<IN> createWhere(
			Class<IN> inputClass, JpaMapper mapper, EntityManager em,
			QueryRule... rules) {
		return createWhere(inputClass, inputClass, mapper, em, rules);
	}

	public static <IN extends Entity> TypedQuery<Long> createCount(
			Class<IN> inputClass, JpaMapper mapper, EntityManager em,
			QueryRule... rules) {
		return createWhere(inputClass, Long.class, mapper, em, rules);
	}

	public static <IN extends Entity, OUT> TypedQuery<OUT> createWhere(
			Class<IN> inputClass, Class<OUT> outputClass, JpaMapper mapper,
			EntityManager em, QueryRule... rules) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<OUT> cq = cb.createQuery(outputClass);
		Metamodel m = em.getMetamodel();
		Root<IN> root = cq.from(inputClass);

		if (inputClass.getSimpleName().equals(outputClass.getSimpleName())) {
			cq.select((Selection<? extends OUT>) root);
		} else {
			cq.select((Selection<? extends OUT>) cb.count(root));
		}

		int[] limitOffset = createWhere(mapper, em, root, cq, cb, rules);

		TypedQuery<OUT> query = em.createQuery(cq);
		if (limitOffset[0] != -1) {
			query.setMaxResults(limitOffset[0]);
		}
		if (limitOffset[1] != -1) {
			query.setFirstResult(limitOffset[1]);
		}
		return query;
	}

	private static <E> int[] createWhere(JpaMapper mapper, EntityManager em,
			Root<E> root, CriteriaQuery cq, CriteriaBuilder cb,
			QueryRule... rules) {

		int[] limitOffset = new int[] { -1, -1 };

		Predicate whereClause = null;
		List<Order> orders = new ArrayList<Order>();

		for (QueryRule r : rules) {
			QueryRule rule = new QueryRule(r);
			if (mapper != null) {
				rule.setField(mapper.getTableFieldName(rule.getField()));
				String attributeName = rule.getField();

				Operator operator = rule.getOperator();
				Predicate predicate = null;

				// if (rule.getValue() == null) {
				// predicate = root.get(attributeName).isNull();
				// } else {
				switch (operator) {
				case LAST:
					throw new UnsupportedOperationException(
							"Not supported yet.");
				case SORTASC:
					orders.add(cb.asc(root.get((String) rule.getValue())));
					break;
				case SORTDESC:
					orders.add(cb.desc(root.get((String) rule.getValue())));
					break;
				case LIMIT:
					limitOffset[0] = (Integer) rule.getValue();
					break;
				case OFFSET:
					limitOffset[1] = (Integer) rule.getValue();
					break;
				default:
					switch (operator) {
					case EQUALS:
						// work around (maybe that difference must be made
						// between equals and join)
						if (rule.getValue() instanceof Entity) {
							try {
								String methodName = "get"
										+ attributeName.substring(0, 1)
												.toUpperCase()
										+ attributeName.substring(1,
												attributeName.length());

								Method m = rule.getValue().getClass()
										.getMethod(methodName);
								Object value = m.invoke(rule.getValue());
								predicate = cb.equal(root.get(attributeName),
										value);
							} catch (Exception ex) {
								LogFactory.getLog(
										JPAQueryGeneratorUtil.class.getName())
										.error(ex);
							}
						} else {
							try {

								if (attributeName.matches("[^_]+_[^_]+")) {
									String[] split = attributeName.split("_");
									String xrefAttribute = split[0];
									String xrefTargetAttribute = split[1];

									Entity entity = (Entity) root.getJavaType()
											.newInstance();
									String xrefAttribtename = entity
											.getXrefIdFieldName(xrefAttribute);
									// it's a xref
									Join join = root.join(xrefAttribute);
									Expression attribute = join
											.get(xrefTargetAttribute);
									Object value = rule.getValue();
									predicate = cb.equal(attribute, value);
								} else if (root.get(attributeName)
										.getJavaType().newInstance() instanceof Entity) {
									Entity entity = (Entity) root.getJavaType()
											.newInstance();
									String xrefAttribtename = entity
											.getXrefIdFieldName(attributeName);
									// it's a xref
									Join join = root.join(attributeName);
									Expression attribute = join
											.get(xrefAttribtename);
									Object value = rule.getValue();
									predicate = cb.equal(attribute, value);
								} else {
									predicate = cb.equal(
											root.get(attributeName),
											rule.getValue());
								}
							} catch (InstantiationException ex) {
								// dit is een hack omdat newInstance niet
								// aangroepen kan worden op immutable objects
								predicate = cb.equal(root.get(attributeName),
										rule.getValue());
								// LogFactory.getLog(JPAQueryGeneratorUtil.class.getName()).log(Level.SEVERE,
								// null, ex);
							} catch (IllegalAccessException ex) {
								// if the path not exist on root then this
								// exception occurs (in case a xref_label to an
								// xref of the object).
								ex.printStackTrace();
								// throw new DatabaseException(ex);
							}
						}
						break;
					case NOT:
						predicate = cb.notEqual(root.get(attributeName),
								rule.getValue());
						break;
					case LIKE:
						predicate = cb.like(
								root.get(attributeName).as(String.class),
								(String) rule.getValue());
						break;
					case LESS:
						predicate = cb.lessThan(
								root.get(attributeName).as(String.class),
								(Comparable) rule.getValue());
						break;
					case GREATER:
						predicate = cb.greaterThan(
								root.get(attributeName).as(String.class),
								(Comparable) rule.getValue());
						break;
					case LESS_EQUAL:
						predicate = cb.lessThanOrEqualTo(root
								.get(attributeName).as(String.class),
								(Comparable) rule.getValue());
						break;
					case GREATER_EQUAL:
						predicate = cb.greaterThanOrEqualTo(
								root.get(attributeName).as(String.class),
								(Comparable) rule.getValue());
						break;
					case NESTED:
						QueryRule[] nestedrules = rule.getNestedRules();
						createWhere(mapper, em, root, cq, cb, nestedrules);
						break;
					case IN: // not a query but a list for example SELECT * FROM
								// x WHERE x.a1 IN (v1, v2, v3)
						Object[] values = new Object[0];
						if (rule.getValue() instanceof List) {
							values = ((List<Object>) rule.getValue()).toArray();
						} else {
							values = (Object[]) rule.getValue();
						}

						List<String> list = new ArrayList<String>();

						for (int i = 0; i < values.length; i++) {
							if (mapper != null
									&& omitQuotes(mapper.getFieldType(rule
											.getField()))) {
								list.add(escapeSql(values[i]));
							} else {
								list.add(escapeSql(values[i]));
							}
						}
						predicate = root.get(attributeName).in(list);
						break;
					}
					// make a where clause from the predicate
					if (whereClause != null) {
						// assert predicate != null : rule.getOperator();

						if (predicate != null) {
							if (rule.getOperator().equals(Operator.OR)) {
								whereClause = cb.or(whereClause, predicate);
							} else {
								whereClause = cb.and(whereClause, predicate);
							}
						}
					} else {
						whereClause = predicate;
					}
					break; // of default
				}
				// }

			}
		}
		if (orders.size() > 0) {
			cq.orderBy(orders);
		}
		if (whereClause != null) {
			cq.where(whereClause);
		}
		return limitOffset;
	}

	private static String createWhereSql(JpaMapper mapper, boolean isNested,
			boolean withOffset, QueryRule... rules)
			throws DatabaseException {
		StringBuffer where_clause = new StringBuffer("");
		QueryRule previousRule = new QueryRule(Operator.AND);
		for (QueryRule r : rules)
		{
			//logger.debug(r);
			//skip OR and AND operators
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
				if (mapper != null && rule.getField() != null) { 
					rule.setField("o." +mapper.getTableFieldName(rule.getField()));
				}

				if (rule.getOperator() == Operator.LAST || rule.getOperator() == Operator.LIMIT
						|| rule.getOperator() == Operator.OFFSET || rule.getOperator() == Operator.SORTASC
						|| rule.getOperator() == Operator.SORTDESC)
				{

				}
				else if (rule.getOperator() == QueryRule.Operator.NESTED)
				{
					QueryRule[] nestedrules = rule.getNestedRules();
					if (nestedrules.length > 0)
					{
						if (where_clause.length() > 0)
						{
							if (previousRule != null && Operator.OR.equals(previousRule.getOperator()))
							{
								where_clause.append(" OR ");
							}
							else
							{
								where_clause.append(" AND ");
							}
						}
						where_clause.append("(");
						where_clause.append(createWhereSql(mapper, true, false, nestedrules));
						where_clause.append(")");
					}
				}
				else if (rule.getOperator() == QueryRule.Operator.IN)
				{
					// only add if nonempty condition???
					if (rule.getValue() == null
							|| (rule.getValue() instanceof List && ((List) rule.getValue()).size() == 0)
							|| (rule.getValue() instanceof Object[] && ((Object[]) rule.getValue()).length == 0)) throw new DatabaseException(
							"empty 'in' clause for rule " + rule);
					{
						if (where_clause.length() > 0)
						{
							if (previousRule != null && Operator.OR.equals(previousRule.getOperator()))
							{
								where_clause.append(" OR ");
							}
							else
							{
								where_clause.append(" AND ");
							}
						}

						// where_clause.append(tablePrefix + rule.getField() +
						// " IN(");
						where_clause.append(rule.getField() + " IN(");

						Object[] values = new Object[0];
						if (rule.getValue() instanceof List)
						{
							values = ((List<Object>) rule.getValue()).toArray();
						}
						else
						{
							values = (Object[]) rule.getValue();
						}

						for (int i = 0; i < values.length; i++)
						{
							if (i > 0) where_clause.append(",");
							if (mapper != null && omitQuotes(mapper.getFieldType(rule.getField())))
							{
								// where_clause.append(values[i]
								// .toString());
								where_clause.append("" + escapeSql(values[i]) + "");
							}
							else
							{
								where_clause.append("'" + escapeSql(values[i]) + "'");
							}
						}
						where_clause.append(") ");
					}
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
					// if (rule.getField() != "" && operator != "" &&
					// rule.getValue() != "")
					// {
					if (where_clause.length() > 0)
					{
						if (previousRule != null && Operator.OR.equals(previousRule.getOperator()))
						{
							where_clause.append(" OR ");
						}
						else
						{
							where_clause.append(" AND ");
						}
					}
					if (Boolean.TRUE.equals(rule.getValue())) rule.setValue("1");
					if (Boolean.FALSE.equals(rule.getValue())) rule.setValue("0");
					Object value = rule.getValue() == null ? "NULL" : escapeSql(rule.getValue());

					if (!value.equals("NULL") && rule.getOperator() == Operator.LIKE
							&& (mapper == null || !omitQuotes(mapper.getFieldType(rule.getField()))))
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
					if (rule.getOperator().equals(Operator.JOIN)) where_clause.append(rule.getField() + " " + operator
							+ " " + value + "");
					else
					{
						if ("NULL".equals(value) && operator.equals("=")) where_clause.append(rule.getField()
								+ " IS NULL");
						else
							where_clause.append(rule.getField() + " " + operator + " '" + value + "'");
					}
				}
				previousRule = null;
			}
		}

		String result = where_clause.toString();
		if (!isNested && where_clause.length() > 0) result = " WHERE " + result;
		return result;
	}

	/** Helper method for creating a sort clause */
	private static String createSortSql(JpaMapper mapper, QueryRule... rules) {
		return createSortSql(mapper, false, rules);
	}

	private static boolean omitQuotes(Type t) {
		return t == Type.LONG || t == Type.INT || t == Type.DECIMAL;

	}

	/**
	 * Helper method for creating a sort clause
	 * 
	 * @param tableName
	 *            name of the table that is used to prefix column names.
	 * @param reverseSorting
	 *            to reverese sorting order. This is used when trying to find
	 *            the "last records" in a sorted list by instead finding the
	 *            "first records" in the reversly ordered list.
	 * @param rules
	 *            query rules to be translated into sql order by clause.
	 * @return sql with sort clause
	 */
	private static String createSortSql(JpaMapper mapper,
			boolean reverseSorting, QueryRule rules[]) {

		for (QueryRule rule : rules) {
			if (rule.getOperator() == Operator.LAST) {
				reverseSorting = !reverseSorting;
				break;
			}
		}

		String sort_clause = "";
		for (QueryRule r : rules) {
			QueryRule rule = new QueryRule(r); // copy because of sideeffects

			// limit clause
			if ((rule.getOperator() == Operator.SORTASC && !reverseSorting)
					|| (reverseSorting && rule.getOperator() == Operator.SORTDESC)) {
				if (mapper != null) {
					rule.setValue("o." +mapper.getTableFieldName(rule.getValue()
							.toString()));
				}
				sort_clause += rule.getValue().toString() + " ASC,";
			} else if ((rule.getOperator() == QueryRule.Operator.SORTDESC && !reverseSorting)
					|| (reverseSorting && rule.getOperator() == Operator.SORTASC)) {
				if (mapper != null) {
					rule.setValue("o." +mapper.getTableFieldName(rule.getValue()
							.toString()));
				}
				sort_clause += rule.getValue().toString() + " DESC,";
			}
		}
		if (sort_clause.length() > 0) {
			return " ORDER BY "
					+ sort_clause.substring(0, sort_clause.lastIndexOf(","));
		}
		return sort_clause;
	}

	/**
	 * Helper method for creating a limit clause
	 * 
	 * @param withOffset
	 *            Indicate whether offset is to be used. If false the limit
	 *            clause is kept empty.
	 * @param rules
	 *            query rules to be translated into SQL order by clause.
	 * @param limit
	 *            out: limit count of the query (returns null if no limit is
	 *            found in the rules)
	 * @param offset
	 *            out: offset of the query (returns null if no offset is found
	 *            in the rules);
	 * @return limit,offset as output parameters
	 */
	private static void createLimitSql(boolean withOffset, QueryRule[] rules,
			Integer[] limitOffset) {
		boolean limitFound = false;
		boolean offsetFound = false;
		for (QueryRule rule : rules) {
			if (rule.getOperator() == QueryRule.Operator.LIMIT) {
				limitOffset[0] = new Integer((Integer) rule.getValue());
				if(offsetFound) {
					break;
				}
			} else if (rule.getOperator() == QueryRule.Operator.OFFSET) {
				limitOffset[1] = new Integer((Integer) rule.getValue());
				if(limitFound) {
					break;
				}
			}
		}
	}

	private static <E extends Entity> String createQueryString(boolean count, Class<E> entityClass, JpaMapper mapper, boolean isNested,
			boolean withOffset, Integer[] limitOffset, QueryRule... rules) throws DatabaseException {
		String select = "SELECT o FROM %s o ";
		String countSelect = "SELECT count(o) FROM %s o ";
		
		StringBuilder query = new StringBuilder();		
		if(count) {
			query.append(String.format(countSelect, entityClass.getSimpleName()));
		} else {
			query.append(String.format(select, entityClass.getSimpleName() ));
		}
		query.append(createWhereSql(mapper, isNested, withOffset, rules));
		query.append(createSortSql(mapper, rules));
		createLimitSql(withOffset, rules, limitOffset);
		return query.toString();
	}	
	
	public static <E extends Entity> List<E> createQuery(JpaDatabase db, Class<E> entityClass, QueryRule...rules) throws DatabaseException {
        Integer[] limitOffset = new Integer[2];
    	JpaMapper<E> mapper = (JpaMapper<E>)db.getMapper(entityClass.getName());
        String ql = JPAQueryGeneratorUtil.createQueryString(false, entityClass, mapper, false, false, limitOffset, rules);
        
        EntityManager em = db.getEntityManager();
        if(limitOffset[0] == null && limitOffset[1] == null) {
        	return em.createQuery(ql, entityClass)
                    .getResultList();
        } else if(limitOffset[0] != null && limitOffset[1] != null) {
        	return em.createQuery(ql, entityClass)
			.setFirstResult(limitOffset[1])
			.setMaxResults(limitOffset[0])
			.getResultList();        
    	} else if(limitOffset[0] != null && limitOffset[1] == null) {
        	return em.createQuery(ql, entityClass)
        			.setMaxResults(limitOffset[0])
    				.getResultList();        	
    	} else if(limitOffset[0] == null && limitOffset[1] != null) {
    		return em.createQuery(ql, entityClass)
					.setFirstResult(limitOffset[1])
					.getResultList();
    	}
        return null;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Entity> int createCountQuery(JpaDatabase db, Class<E> entityClass, QueryRule...rules) throws DatabaseException {
		Integer[] limitOffset = new Integer[2];
    	JpaMapper<E> mapper = (JpaMapper<E>)db.getMapper(entityClass.getName());
        String ql = JPAQueryGeneratorUtil.createQueryString(true, entityClass, mapper, false, false, limitOffset, rules);
        EntityManager em = db.getEntityManager();
        return em.createQuery(ql, Long.class).getSingleResult().intValue();	        		
	}
	
	/**
	 * Helper method for creating an escaped sql string for a value.
	 * <p>
	 * This can be used by createXXXsql methods to prevend sql-injection in data
	 * values.
	 * 
	 * @param value
	 *            to be escaped
	 */
	private static String escapeSql(Object value) {
		return value.toString();
	}
	
	
	public static String convertToJpaFieldName(String fieldName) {
		if(fieldName.contains(".") || fieldName.contains("_")) {
			fieldName = fieldName.replace("_", ".");			
			StringBuilder sb = new StringBuilder();
			String[] splits = fieldName.split("\\.");
			for(int i = 0; i < splits.length; ++i) {
				String s = splits[i];
				sb.append(s.substring(0,1).toLowerCase());
				sb.append(s.substring(1, s.length()));
				if(i < splits.length - 1) {
					sb.append(".");
				}
			}
			return sb.toString();
		}
		return fieldName;		
	}
}