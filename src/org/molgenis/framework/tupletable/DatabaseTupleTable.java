package org.molgenis.framework.tupletable;

import org.molgenis.framework.db.Database;

public interface DatabaseTupleTable
{
//	private Database db;
//
//	public DatabaseTupleTable(Database db) {
//		if(db == null) throw new IllegalArgumentException();
//		this.db = db;	
//	}
//
//	public Database getDb();
//	{
//		return db;
//	}
//	
//	public void setDb(Database db)
//	{
//		this.db = db;
//	}
	
	public Database getDb();
	
	public void setDb(Database db);
	
}
