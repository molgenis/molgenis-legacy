package org.molgenis.lifelinesresearchportal.plugins.loader.listeners;

import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.SimpleTuple;

/**
 * This listener will read tuples and write them to a target, e.g. a database or other csv file.
 *
 */
public abstract class ImportTupleListener implements CsvReaderListener{
	protected final Database db;
	protected final String name;
	
	public ImportTupleListener(String name, Database db) {
		this.name = name;
		this.db = db;
	}

	/** Write all (remaining) values to target*/
	public abstract void commit() throws Exception;

	//process all tupes
	public void processTuples(List<SimpleTuple> tuples) throws Exception {
		for(SimpleTuple tuple : tuples) {
			this.handleLine(-1, tuple);
		}
		commit();
	}
}
