package org.molgenis.lifelines.listeners;

import org.molgenis.framework.db.Database;
import org.molgenis.util.CsvReaderListener;

/**
 * This listener will read tuples and write them to a target, e.g. a database or other csv file.
 * @author jorislops
 *
 */
public abstract class ImportTupleListener implements CsvReaderListener {
	protected final Database db;
	protected final String name;
	
	public ImportTupleListener(String name, Database db) {
		this.name = name;
		this.db = db;
	}

	/** Write all (remaining) values to target*/
	public abstract void commit() throws Exception;

	
}
