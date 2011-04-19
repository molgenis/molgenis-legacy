package org.molgenis.framework.db;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;

import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jdbc.ColumnInfo.Type;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;

public interface DatabaseMapper<E extends Entity>
{
	public int add(List<E> entities) throws DatabaseException;

	// FIXME: can we merge the two add functions by wrapping list/reader into an
	// iterator of some kind?
	public E create();
	

	
	public int update(List<E> entities) throws DatabaseException;


	
	public int remove(List<E> entities) throws DatabaseException;
	
	public String getTableFieldName(String field);

	public Type getFieldType(String field);
}
