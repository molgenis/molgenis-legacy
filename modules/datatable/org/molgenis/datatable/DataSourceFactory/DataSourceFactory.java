package org.molgenis.datatable.DataSourceFactory;

import java.util.List;

import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;

import com.google.gson.internal.StringMap;

public interface DataSourceFactory {
	public TupleTable createDataSource(StringMap<?> dataSource, Database db, List<QueryRule> rules) throws Exception;
}
