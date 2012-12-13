package org.molgenis.framework.db;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.molgenis.framework.db.Database.DatabaseAction;

public interface CsvEntityImporter extends Closeable
{
	public int importData(Reader reader, String entityName, Database db, DatabaseAction dbAction) throws IOException,
			DatabaseException;
}
