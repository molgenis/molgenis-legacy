package org.molgenis.lifelinesresearchportal.plugins.loader.listeners;

import org.molgenis.framework.db.Database;
import org.molgenis.util.TupleReader;

/**
 * This listener will read tuples and write them to a target, e.g. a database or other csv file.
 *
 */
public abstract class ImportTupleLoader {
	protected final Database db;
	protected final String name;
	
	public ImportTupleLoader(String name, Database db) {
		this.name = name;
		this.db = db;
	}

	/** Write all (remaining) values to target*/
	public abstract void commit() throws Exception;

	/** load 
	 * @throws Exception */
	public abstract void load(TupleReader tupleIterator) throws Exception;
	
	//process all tupes
//	public void processTuples(List<SimpleTuple> tuples) throws Exception {
//		for(SimpleTuple tuple : tuples) {
//			this.handleLine(-1, tuple);
//		}
//		commit();
//	}
}
