package org.molgenis.datatable.DataSourceFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.datatable.model.CsvTable;
import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.model.QueryDSLTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;

import com.google.gson.internal.StringMap;
import com.mysema.query.Query;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.path.PathBuilder;

/**
 * General implementation of {@link DataSourceFactory} for several types of data source.
 * Wraps a particular type of datasource in the appropriate implementation of {@link TupleTable}.
 */
public class DataSourceFactoryImpl implements DataSourceFactory {
	private final static String SQL_SELECT_START = "SELECT %1$s FROM %2$s ";
	
	public TupleTable createDataSource(StringMap<?> dataSource, Database db, List<QueryRule> rules) throws Exception {
		final String type = (String) dataSource.get("type");
		if(type.equals("jdbc")) {
			final String fromExpression = (String) dataSource.get("fromExpression");
						
			final List<String> columnNames = getColumnNames(dataSource);				
			final String sqlSelect = String.format(SQL_SELECT_START, StringUtils.join(columnNames, ","), fromExpression);
			return new JdbcTable(db, sqlSelect, rules);
		} else if(type.equals("csv")) {
			final String csvUri = (String) dataSource.get("uri");
			return new CsvTable(csvUri);
		} else if(type.equals("QueryDSL")) {
			System.out.println("xxxxx");
			return null;
//			final SQLQuery query = (SQLQuery)dataSource.get("query");
//			List<PathBuilder<?>> projection = (List<PathBuilder<?>>) dataSource.get("projection");
//			return new QueryDSLTable(query, projection );
		}
		return null;
	}

	/**
	 * Gets the column names from a particular datasource, represented by a Gson StringMap.
	 * @param dataSource	The data source.
	 * @return	A list of strings, containing the column names.
	 */
	private List<String> getColumnNames(StringMap<?> dataSource)
	{
		final List<String> columnNames = new ArrayList<String>();
		final ArrayList<?> columns = (ArrayList<?>) dataSource.get("columns");
		CollectionUtils.forAllDo(columns, new Closure()
		{					
			public void execute(Object arg0)
			{	
				final String name = (String) ((StringMap<?>)arg0).get("name");
				columnNames.add(name);
			}
		});
		return columnNames;
	}
}
