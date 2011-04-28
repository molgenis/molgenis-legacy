package org.molgenis.framework.db;

import java.util.List;

import org.molgenis.framework.db.jdbc.ColumnInfo.Type;
import org.molgenis.util.Entity;

public interface DatabaseMapper<E extends Entity>
{
	public E create();
	
	public int add(List<E> entities) throws DatabaseException;

	public int update(List<E> entities) throws DatabaseException;
	
	public int remove(List<E> entities) throws DatabaseException;
	
	public String getTableFieldName(String field);

	public Type getFieldType(String field);
}
