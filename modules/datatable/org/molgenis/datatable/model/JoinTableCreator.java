package org.molgenis.datatable.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.datatable.model.QueryTable.QueryCreator;
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

public class JoinTableCreator implements QueryCreator
{
	/**
	 * Will this not limit join possibilities?
	 * 
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
	private final List<Join> joins;

	private LinkedHashMap<String, SimpleExpression<? extends Object>> select;

	public JoinTableCreator(final Database db, final List<String> tableNames, final List<String> columnNames,
			final List<Join> joins)
	{
		this.db = db;
		this.tableNames = tableNames;
		this.columnNames = columnNames;
		this.joins = joins;
	}

	@Override
	public SQLQueryImpl createQuery(Connection connection, SQLTemplates dialect)
	{
		final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);

		select = new LinkedHashMap<String, SimpleExpression<? extends Object>>();
		final Map<String, List<Field>> tableColumns = loadColumnData(db, tableNames, columnNames, joins);

		for (final String tableName : tableNames)
		{
			final PathBuilder table = new PathBuilder<RelationalPath>(RelationalPath.class, tableName);
			query.from(table);
			for (final Field f : tableColumns.get(tableName.toLowerCase()))
			{
				final SimpleExpression<?> path = createPath(f, table);
				select.put(f.getSqlName(), path);
			}
		}

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
	public LinkedHashMap<String, SimpleExpression<? extends Object>> getSelectMap()
	{

		return select;
	}

	@Override
	public List<Field> getField()
	{
		final List<Field> columns = new ArrayList<Field>();
		final Map<String, List<Field>> columnsByTable = loadColumnData(db, tableNames, columnNames, joins);
		for (final String table : columnsByTable.keySet())
		{
			for (final Field field : columnsByTable.get(table))
			{
				columns.add(field);
			}
		}
		return columns;
	}

	private static Map<String, List<Field>> loadColumnData(final Database db, List<String> tableNames,
			List<String> columnNames, List<Join> joins)
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
					if (CollectionUtils.isNotEmpty(columnNames))
					{
						if (!columnNames.contains(sqlColumnName))
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
