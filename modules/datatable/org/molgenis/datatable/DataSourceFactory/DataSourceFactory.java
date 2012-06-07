package org.molgenis.datatable.DataSourceFactory;

import java.util.List;

import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;

import com.google.gson.internal.StringMap;


/**
 * Interface for the creation of datasources, for the use in controllers.
 * A general implementation for the most common types can be found in {@link DataSourceFactoryImpl}.
 * If a particular package implements a {@link TupleTable} datasource wrapper for their own data, that
 * package should implement its own factory. 
 */
public interface DataSourceFactory {
	public TupleTable createDataSource(StringMap<?> dataSource, Database db, List<QueryRule> rules) throws Exception;
}
