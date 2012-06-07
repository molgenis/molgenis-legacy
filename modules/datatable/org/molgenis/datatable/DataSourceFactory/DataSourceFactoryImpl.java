package org.molgenis.datatable.DataSourceFactory;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.datatable.model.CsvTable;
import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;

import com.google.gson.internal.StringMap;

public class DataSourceFactoryImpl implements DataSourceFactory {
	private final static String SQL_START = "SELECT %1$s FROM %2$s ";
	
	public TupleTable createDataSource(StringMap<?> dataSource, Database db, List<QueryRule> rules) throws Exception {
		final String type = (String) dataSource.get("type");
		if(type.equals("jdbc")) {
			final String fromExpression = (String) dataSource.get("fromExpression");
						
			final List<String> columnNames = getColumnNames(dataSource);				
			final String sqlSelect = String.format(SQL_START, StringUtils.join(columnNames, ","), fromExpression);
			return new JdbcTable(db, sqlSelect, rules);
		} else if(type.equals("csv")) {
			final String csvUri = (String) dataSource.get("uri");
			return new CsvTable(csvUri);
		}
		return null;
	}

	private List<String> getColumnNames(StringMap<?> dataSource)
	{
		final List<String> columnNames = new ArrayList<String>();
		final ArrayList<?> columns = (ArrayList<?>) dataSource.get("columns");
		CollectionUtils.forAllDo(columns, new Closure()
		{					
			@Override
			public void execute(Object arg0)
			{	
				final String name = (String) ((StringMap<?>)arg0).get("name");
				columnNames.add(name);
			}
		});
		return columnNames;
	}
}
