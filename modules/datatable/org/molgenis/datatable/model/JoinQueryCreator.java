package org.molgenis.datatable.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;

import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.BooleanPath;
import com.mysema.query.types.path.DatePath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

/**
 * Implementation of the QueryCreator interface that allows simple joins.
 */
public class JoinQueryCreator implements QueryCreator
{
	/**
	 * Utility class to encapsulate which two tables are joined on which columns
	 */
	public static class Join
	{

		private final String leftColumnName;
		private final String leftTableName;
		private final String rightColumnName;
		private final String rightTableName;

		public Join(String leftTableName, String leftColumnName, String rightTableName, String rightColumnName)
		{
			this.leftColumnName = leftColumnName;
			this.leftTableName = leftTableName;
			this.rightColumnName = rightColumnName;
			this.rightTableName = rightTableName;
		}
	}

	private final Database db;
	private final List<String> tableNames;
	private final List<String> columnNames;
	private final List<String> hiddenFieldNames;
	private final List<Join> joins;

	private LinkedHashMap<String, SimpleExpression<? extends Object>> attributeExpressions;
	private final Map<String, List<Field>> tableColumns;

	/**
	 * Create a {@link JoinQueryCreator}, specifying whence to get the data and
	 * which columns to display.
	 * 
	 * @param db
	 *            The database
	 * @param tableNames
	 *            The names of the tables
	 * @param columnNames
	 *            The names of the columns (excluding hidden ones)
	 * @param hiddenFieldNames
	 *            The names of the columns that should not be displayed. Used
	 *            for the WHERE condition.
	 * @param joins
	 *            The parametrisations of the joins between the tables.
	 */
	public JoinQueryCreator(final Database db, final List<String> tableNames, final List<String> columnNames,
			final List<String> hiddenFieldNames, final List<Join> joins)
	{
		this.db = db;
		this.tableNames = tableNames;
		this.columnNames = columnNames;
		this.hiddenFieldNames = hiddenFieldNames == null ? Collections.<String> emptyList() : hiddenFieldNames;
		this.joins = joins;

		tableColumns = loadColumnData();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SQLQueryImpl createQuery(Connection connection, SQLTemplates dialect)
	{
		final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);
		attributeExpressions = new LinkedHashMap<String, SimpleExpression<? extends Object>>();

		// Fill attribute map with table and column expressions
		for (final String tableName : tableNames)
		{
			final PathBuilder<RelationalPath> table = new PathBuilder<RelationalPath>(RelationalPath.class, tableName);
			query.from(table);
			for (final Field f : tableColumns.get(tableName.toLowerCase()))
			{
				final SimpleExpression<?> path = createPath(f, table);
				attributeExpressions.put(f.getSqlName(), path);
			}
		}

		// Add joins
		for (final Join join : joins)
		{
			final PathBuilder<RelationalPath> leftTable = new PathBuilder<RelationalPath>(RelationalPath.class,
					join.leftTableName);
			final PathBuilder<RelationalPath> rightTable = new PathBuilder<RelationalPath>(RelationalPath.class,
					join.rightTableName);

			query.where(leftTable.get(join.leftColumnName).eq(rightTable.get(join.rightColumnName)));
		}

		return query;
	}

	@Override
	public List<String> getHiddenFieldNames()
	{
		return hiddenFieldNames;
	}

	@Override
	public LinkedHashMap<String, SimpleExpression<? extends Object>> getAttributeExpressions()
	{
		return attributeExpressions;
	}

	@Override
	public List<Field> getFields()
	{
		final List<Field> columns = new ArrayList<Field>();
		final Map<String, List<Field>> columnsByTable = loadColumnData();
		for (final String table : columnsByTable.keySet())
		{
			for (final Field field : columnsByTable.get(table))
			{
				columns.add(field);
			}
		}
		return columns;
	}

	/**
	 * Retrieve column information from the database
	 * 
	 * @return A map with table names as keys and a lists of the fields in that
	 *         table as values.
	 */
	private Map<String, List<Field>> loadColumnData()
	{
		final Map<String, List<Field>> tableColumns = new LinkedHashMap<String, List<Field>>();
		try
		{
			final DatabaseMetaData metaData = db.getConnection().getMetaData();
			for (final String tableName : tableNames)
			{
				tableColumns.put(tableName.toLowerCase(), new ArrayList<Field>());
				final ResultSet columns = metaData.getColumns(null, "%", tableName, "%");
				while (columns.next())
				{
					final String columnName = columns.getString("COLUMN_NAME");
					final int sqlType = columns.getInt("DATA_TYPE");

					final String sqlColumnName = String.format("%s.%s", tableName, columnName);
					if (CollectionUtils.isNotEmpty(columnNames) || CollectionUtils.isNotEmpty(hiddenFieldNames))
					{
						if (!columnNames.contains(sqlColumnName) && !hiddenFieldNames.contains(sqlColumnName))
						{
							continue;
						}
					}

					final Field field = new Field(columnName);
					field.setType(MolgenisFieldTypes.getTypeBySqlTypesCode(sqlType));
					field.setTableName(tableName);
					tableColumns.get(tableName.toLowerCase()).add(field);
				}
			}
		}
		catch (final Exception ex)
		{
			throw new IllegalStateException(ex);
		}
		return tableColumns;
	}

	/**
	 * Create a {@link SimpleExpression} of the path of a field (i.e. column) in
	 * a table.
	 */
	private static SimpleExpression<?> createPath(Field f, PathBuilder<RelationalPath> table)
	{
		final FieldTypeEnum type = f.getType().getEnumType();
		final String name = f.getName().toLowerCase();
		switch (type)
		{
			case STRING:
				return table.get(new StringPath(name));
			case INT:
				return table.get(new NumberPath<Integer>(Integer.class, name));
			case DECIMAL:
				return table.get(new NumberPath<Double>(Double.class, name));
			case LONG:
				return table.get(new NumberPath<Long>(Long.class, name));
			case BOOL:
				return table.get(new BooleanPath(name));
			case DATE:
			case DATE_TIME:
				return table.get(new DatePath<Date>(Date.class, name));
			default:
				throw new UnsupportedOperationException("create path not implemented for " + type.toString());
		}
	}
}
