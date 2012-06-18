package org.molgenis.datatable.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;

import com.mindbright.jca.security.UnsupportedOperationException;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.BooleanPath;
import com.mysema.query.types.path.DatePath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

public class QueryTables extends QueryTable
{	
	/**
	 *  Will this not limit join possibilities? 
	 *  
	 */
	public static class Join {
		private final String leftColumnExpr;
		private final String rightColumnExpr;
		
		public Join(String leftColumnExpr, String rightColumnExpr)
		{
			this.leftColumnExpr = leftColumnExpr;
			this.rightColumnExpr = rightColumnExpr;
		}
	}
	
	public QueryTables(final SQLQuery query, final List<String> tableNames, final List<Join> joins, final Database db)
	{
		super((SQLQueryImpl) query, createSelectAndJoin(query, tableNames, joins, db), getFields(db, tableNames));
	}

	private static LinkedHashMap<String,SimpleExpression<? extends Object>> createSelectAndJoin(final SQLQuery query, final List<String> tableNames, List<Join> joins, final Database db)
	{
		final LinkedHashMap<String, SimpleExpression<? extends Object>> select = new LinkedHashMap<String, SimpleExpression<? extends Object>>();
		final Map<String, List<Field>> tableColumns = loadColumnData(db, tableNames);
		for(final String tableName : tableNames) {
			final PathBuilder<RelationalPath> table = new PathBuilder<RelationalPath>(RelationalPath.class, tableName);
			query.from(table);
			for(final Field f : tableColumns.get(tableName.toLowerCase())) {
				final SimpleExpression<?> path = createPath(f, table);
				select.put(f.getSqlName(), path);	
			}
		}
		
		for(Join join : joins) {
			SimpleExpression<? extends Object> leftExpr = select.get(join.leftColumnExpr);
			SimpleExpression<? extends Object> rightExpr = select.get(join.rightColumnExpr);
			query.where(leftExpr.eq((Expression) rightExpr));
		}
		
		
		return select;
	}
	
	private static SimpleExpression<?> createPath(Field f, PathBuilder<RelationalPath> table)
	{
		FieldTypeEnum type = f.getType().getEnumType();
		String name = f.getName().toLowerCase();
		switch(type) {
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
			case DATE: case DATE_TIME:
				return table.get(new DatePath<Date>(Date.class, name));
			default:
				throw new UnsupportedOperationException("create path not implemented for " +type.toString());
		}
		
	}
	
	private static List<Field> getFields(Database db, List<String> tableNames) {
		final List<Field> columns = new ArrayList<Field>();
		final Map<String, List<Field>> columnsByTable = loadColumnData(db, tableNames);
		for(String table : columnsByTable.keySet()) {
			for(Field field : columnsByTable.get(table)) {
				columns.add(field);
			}
		}
		return columns;
	}

	private static Map<String, List<Field>> loadColumnData(final Database db, List<String> tableNames) {
		final Map<String, List<Field>> tableColumns = new LinkedHashMap<String, List<Field>>();
		for(String tableName : tableNames) {
			tableColumns.put(tableName.toLowerCase(), new ArrayList<Field>());
		}
		
		try {
			final Connection conn = db.getConnection();
			final Statement statement = conn.createStatement();
			final String sql = "SELECT * FROM %s LIMIT 1";
			final ResultSet rs = statement.executeQuery(String.format(sql, StringUtils.join(tableNames, ",")));
			final ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 1, n = metaData.getColumnCount(); i <= n; ++i) {
				final String columnName = metaData.getColumnName(i);
				final String tableName = metaData.getTableName(i);
				final Field field = new Field(columnName);
				field.setType(MolgenisFieldTypes.getTypeBySqlTypesCode(metaData.getColumnType(i)));
				field.setTableName(tableName);
				tableColumns.get(tableName.toLowerCase()).add(field);
				
			}
			rs.close();
			statement.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return tableColumns;
	}
}