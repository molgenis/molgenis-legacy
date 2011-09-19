package org.molgenis.framework.db.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.fieldtypes.IntField;
import org.molgenis.fieldtypes.LongField;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;

public class JDBCConnectionHelper
{
	/** The jndi to a data source */
	DataSourceWrapper source = null;

	/** The current JDBC connection of this database (only when in transaction) */
	Connection connection;

	/** Flag to indicate whether the database is in a transaction */
	boolean inTransaction = false;

	int openconnections = 0;

	/** Ticket to indicate a private transaction */
	String privateTransaction = null;

	/** Logger for this database */
	private static final transient Logger logger = Logger.getLogger(JDBCDatabase.class.getSimpleName());

	public JDBCConnectionHelper(DataSourceWrapper source)
	{
		this.source = source;
	}

	public JDBCConnectionHelper()
	{
		// TODO Auto-generated constructor stub
	}

	// @Override
	public void beginTx() throws DatabaseException
	{
		getConnection();
		try
		{
			if (inTransaction)
			{
				logger.error("BeginTx failed: transaction already begun");
				throw new DatabaseException("BeginTx failed: transaction already begun");
			}
			connection.setAutoCommit(false);
			inTransaction = true;
			logger.debug("begin transaction");
		}
		catch (SQLException sqle)
		{
			logger.error("beginTx failed: " + sqle.getMessage());
			throw new DatabaseException(sqle);
		}
	}

	// @Override
	public void commitTx() throws DatabaseException
	{
		try
		{
			if (!inTransaction) throw new DatabaseException("commitTx failed: no active transaction");
			connection.commit();
			connection.setAutoCommit(true);
			inTransaction = false;
			// FIXME in case of hsqldb we need to checkpoint
			// if(this.source.getDriverClassName().contains("hsql"))
			// this.executeQuery("checkpoint");
			logger.info("commited transaction");
		}
		catch (SQLException sqle)
		{
			logger.error("commitTx failed: " + sqle.getMessage());
			throw new DatabaseException(sqle);
		}
		finally
		{
			closeConnection();
		}
	}

	// @Override
	public void rollbackTx() throws DatabaseException
	{
		try
		{
			if (!inTransaction) throw new DatabaseException("rollbackTx failed: no active transaction");
			connection.rollback();
			connection.setAutoCommit(true);
			inTransaction = false;
			logger.info("rolled back transaction on "+this.connection.getMetaData().getURL());
		}
		catch (SQLException sqle)
		{
			logger.error("rollbackTx failed: " + sqle.getMessage());
			throw new DatabaseException(sqle);
		}
		finally
		{
			closeConnection();
		}
	}

	public boolean inTx()
	{
		return inTransaction;
	}

	/** open the connection (if not already) */
	public Connection getConnection() throws DatabaseException
	{
		try
		{
			if (connection == null || connection.isClosed())
			{
				openconnections++;
				connection = source.getConnection();
				logger.debug(this + "opened database connection, connectioncount=" + openconnections +", count in pool: "+this.source.countOpenConnections()+"/"+source.getMaxActive());
				connection.setAutoCommit(true); // restore default
			}
			return connection;
		}
		catch (Exception sqle)
		{
			logger.error("Cannot open connection: " + sqle.getMessage());
			throw new DatabaseException(sqle);
		}
	}
	
	public DataSourceWrapper getSource(){
		return source;
	}

	/**
	 * close the connection (if not in transaction)
	 * 
	 * @throws DatabaseException
	 */
	protected void closeConnection()
	{
		if (inTransaction)
		{
			// logger.debug("Didn't close connection: transaction active");
		}
		else
		{
			if (connection != null)
			{
				try
				{
					connection.setAutoCommit(true); // restore default
					if (!connection.isClosed()) connection.close();
					connection = null;
					openconnections--;
					logger.debug(this + "closed connection back to pool, connectioncount=" + openconnections+", open connections in pool: "+source.countOpenConnections()+"/"+source.getMaxActive());
				}
				catch (Exception sqle)
				{
					// System.err.println(this+"Cannot close connection: " +
					// sqle.getMessage());
					logger.error(this + "Cannot close connection: " + sqle.getMessage());
				}
			}
		}
	}

	/**
	 * Executes a JDBC query and returns the resultset.
	 * 
	 * @param sql
	 * @param rules
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	public ResultSet executeQuery(String sql, QueryRule... rules) throws DatabaseException
	{
		getConnection();
		Statement stmt = null;
		try
		{
			stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			if (source.getDriverClassName().contains("mysql"))
			{
				stmt.setFetchSize(Integer.MIN_VALUE); // trigger streaming of
			}
			String allSql = sql;
			if (rules != null && rules.length > 0) allSql += createWhereSql(null, false, true, rules);
			ResultSet rs = stmt.executeQuery(allSql);
			logger.debug("executeQuery: " + allSql);
			return rs;
		}
		catch (NullPointerException npe)
		{
			logger.error("executeQuery() failed with " + npe + " on sql: " + sql + "\ncause: " + npe.getCause());
			npe.printStackTrace();
			throw new DatabaseException(npe);
		}
		catch (SQLException sqle)
		{
			logger.error("executeQuery(" + sql + ")" + sqle);
			throw new DatabaseException(sqle);
		}
	}

	/**
	 * Closes a statement.
	 * 
	 * @param stmt
	 */
	public static void closeStatement(Statement stmt)
	{
		try
		{
			if (stmt != null) stmt.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.warn("closeStatement(): " + e);
		}
	}

	/**
	 * Helper method for creating a where clause from QueryRule...rules.
	 * 
	 * @param mapper
	 *            mapper that is used to extract metadata to create the
	 * @param isNested
	 *            wether this whereclause will be nested inside another clause,
	 *            e.g (A AND B) OR C. If nested then the word "where" will not
	 *            be included in the returned string so this method can be used
	 *            recursively.
	 * @param withOffset
	 *            whether this whereclause should be calculated including offset
	 *            and limit
	 * @param rules
	 *            query rules to be translated into sql where clause.
	 * @return sql where clause. FIXME: remove the 'withOffset' part?
	 * @throws DatabaseException
	 */
	public static String createWhereSql(JDBCMapper<?> mapper, boolean isNested, boolean withOffset, QueryRule... rules)
			throws DatabaseException
	{
		StringBuffer where_clause = new StringBuffer("");
		QueryRule previousRule = new QueryRule(Operator.AND);
		if(rules != null){
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
				if (mapper != null) rule.setField(mapper.getTableFieldName(rule.getField()));

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
				//experimental: subqery
				else if (rule.getOperator() == QueryRule.Operator.IN_SUBQUERY)
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
					where_clause.append(rule.getField() + " IN("+rule.getValue()+")");
				}
				else if (rule.getOperator() == QueryRule.Operator.IN)
				{
					// only add if nonempty condition???
					if (rule.getValue() == null
							|| (rule.getValue() instanceof List<?> && ((List<?>) rule.getValue()).size() == 0)
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
						if (rule.getValue() instanceof List<?>)
						{
							values = ((List<?>) rule.getValue()).toArray();
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
					if (rule.getOperator().equals(Operator.JOIN)) {
						where_clause.append(rule.getField() + " " + operator
							+ " " + value + "");
					}
					else
					{
						if ("NULL".equals(value) && operator.equals("=")) {
							where_clause.append(rule.getField() + " IS NULL");
						} else if ("NULL".equals(value) && operator.equals("!=")) {
							where_clause.append(rule.getField() + " IS NOT NULL");
						} else {
							where_clause.append(rule.getField() + " " + operator + " '" + value + "'");
						}
					}
				}
				previousRule = null;
			}
		}
		}
		String result = where_clause.toString();
		if (!isNested && where_clause.length() > 0) result = " WHERE " + result;
		return result + createSortSql(mapper, rules) + createLimitSql(withOffset, rules);
	}

	/** Helper method for creating a sort clause */
	private static String createSortSql(JDBCMapper<?> mapper, QueryRule... rules)
	{
		return createSortSql(mapper, false, rules);
	}

	private static boolean omitQuotes(FieldType t)
	{
		return t instanceof LongField || t instanceof IntField || t instanceof DecimalField;
		
		//t.equals(Type.LONG) || t.equals(Type.INT) || t.equals(Type.DECIMAL);
//		return t instanceof LongField || t instanceof IntField|| t instanceof DecimalField;

	}

	/**
	 * Helper method for creating a sort clause
	 * 
	 * @param mapper
	 *            mapper that is used to extract metadata to create the
	 * @param reverseSorting
	 *            to reverese sorting order. This is used when trying to find
	 *            the "last records" in a sorted list by instead finding the
	 *            "first records" in the reversly ordered list.
	 * @param rules
	 *            query rules to be translated into sql order by clause.
	 * @return sql with sort clause
	 */
	public static String createSortSql(JDBCMapper<?> mapper, boolean reverseSorting, QueryRule rules[])
	{
		// copy parameter into local temp so we can change it
		String sort_clause = "";
		if(rules != null){
		Boolean revSort = reverseSorting;
		for (QueryRule rule : rules)
		{
			if (rule.getOperator() == Operator.LAST)
			{
				revSort = !revSort;
				break;
			}
		}

		
		for (QueryRule r : rules)
		{
			QueryRule rule = new QueryRule(r); // copy because of sideeffects

			// limit clause
			if ((rule.getOperator() == Operator.SORTASC && !revSort)
					|| (revSort && rule.getOperator() == Operator.SORTDESC))
			{
				if (mapper != null) rule.setValue(mapper.getTableFieldName(rule.getValue().toString()));
				sort_clause += rule.getValue().toString() + " ASC,";
			}
			else if ((rule.getOperator() == QueryRule.Operator.SORTDESC && !revSort)
					|| (revSort && rule.getOperator() == Operator.SORTASC))
			{
				if (mapper != null) rule.setValue(mapper.getTableFieldName(rule.getValue().toString()));
				sort_clause += rule.getValue().toString() + " DESC,";
			}
		}
		}
		if (sort_clause.length() > 0) return " ORDER BY " + sort_clause.substring(0, sort_clause.lastIndexOf(","));
		return sort_clause;
	}

	/**
	 * Helper method for creating a limit clause
	 * 
	 * @param withOffset
	 *            Indicate whether offset is to be used. If false the limit
	 *            clause is kept empty.
	 * @param rules
	 *            query rules to be translated into sql order by clause.
	 * @return sql for limit,offset
	 */
	public static String createLimitSql(boolean withOffset, QueryRule[] rules)
	{
		String limit_clause = "";
		String offset_clause = "";
		if(rules != null){
		for (QueryRule rule : rules)
		{
			// limit clause
			if (rule.getOperator() == QueryRule.Operator.LIMIT)
			{
				limit_clause = " LIMIT " + rule.getValue();
			}
			else if (rule.getOperator() == QueryRule.Operator.OFFSET)
			{
				offset_clause = " OFFSET " + rule.getValue();
			}
		}
		}
		if (withOffset || offset_clause.equals("")) return limit_clause + offset_clause;
		return "";
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
	public static String escapeSql(Object value)
	{
		if (value != null) return StringEscapeUtils.escapeSql(value.toString().replace("'", "''"));
		return null;
		// return sql.toString().replace("'", "''");
	}
}
