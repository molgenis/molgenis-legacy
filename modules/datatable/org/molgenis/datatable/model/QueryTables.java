package org.molgenis.datatable.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.MolgenisFieldTypes.FieldTypeEnum;
import org.molgenis.fieldtypes.FieldType;
import org.molgenis.framework.db.Database;
import org.molgenis.model.elements.Field;

import com.hp.hpl.jena.sparql.function.library.date;
import com.mindbright.jca.security.UnsupportedOperationException;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.types.Expression;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.BooleanPath;
import com.mysema.query.types.path.DatePath;
import com.mysema.query.types.path.EnumPath;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

public class QueryTables extends QueryTable
{	
	public QueryTables(final SQLQuery query, final List<String> tableNames, final Database db)
	{
		
		super(query, createSelect(), );
		//super(query, 
		
	}

	private static LinkedHashMap<String,SimpleExpression<? extends Object>> createSelect(final List<String> tableNames, final Database db)
	{
		final LinkedHashMap<String, SimpleExpression<? extends Object>> select = new LinkedHashMap<String, SimpleExpression<? extends Object>>();
		Map<String, List<Field>> tableColumns = loadColumnData(db, tableNames);
		for(final String tableName : tableNames) {
			final PathBuilder<RelationalPath> table = new PathBuilder<RelationalPath>(RelationalPath.class, tableName);
			for(final Field f : tableColumns.get(tableName)) {
				final SimpleExpression<?> path = createPath(f);
				select.put(f.getName(), path);	
			}
		}
		return select;
	}
	
	private static SimpleExpression<?> createPath(Field f)
	{
		FieldTypeEnum type = f.getType().getEnumType();
		switch(type) {
			case STRING:
				return new StringPath(f.getName());
			case INT:
				return new NumberPath<Integer>(Integer.class, f.getName());
			case DECIMAL:
				return new NumberPath<Double>(Double.class, f.getName());
			case LONG:
				return new NumberPath<Long>(Long.class, f.getName());
			case BOOL:
				return new BooleanPath(f.getName()); 
			case DATE: case DATE_TIME:
				return new DatePath<Date>(Date.class, f.getName());
			default:
				throw new UnsupportedOperationException("create path not implemented for " +type.toString());
		}
		
	}

	public static Map<String, List<Field>> loadColumnData(final Database db, List<String> tableNames) {
		final Map<String, List<Field>> tableColumns = new LinkedHashMap<String, List<Field>>();
		for(String tableName : tableNames) {
			tableColumns.put(tableName, new ArrayList<Field>());
		}
		
		try {
			final Connection conn = db.getConnection();
			final Statement statement = conn.createStatement();
			final String sql = "SELECT * FROM %s LIMIT 1";
			final ResultSet rs = statement.executeQuery(String.format(sql, StringUtils.join(tableNames, ".")));
			final ResultSetMetaData metaData = rs.getMetaData();
			for(int i = 1, n = metaData.getColumnCount(); i <= n; ++i) {
				final String columnName = metaData.getColumnName(i);
				final String tableName = metaData.getTableName(i);
				final Field field = new Field(columnName);
				field.setType(MolgenisFieldTypes.getTypeBySqlTypesCode(metaData.getColumnType(i)));
				
				tableColumns.get(tableName).add(field);
				
			}
			rs.close();
			statement.close();
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return tableColumns;
	}
}