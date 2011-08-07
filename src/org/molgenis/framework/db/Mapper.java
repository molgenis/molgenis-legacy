package org.molgenis.framework.db;

import java.text.ParseException;
import java.util.List;

import org.molgenis.fieldtypes.FieldType;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.SpreadsheetWriter;

public interface Mapper<E extends Entity> {
	public Database getDatabase();
	
	public int add(List<E> entities) throws DatabaseException;

	// FIXME: can we merge the two add functions by wrapping list/reader into an
	// iterator of some kind?
	public E create();
	
	public int add(CsvReader reader, SpreadsheetWriter writer) throws DatabaseException;
	
	public int update(List<E> entities) throws DatabaseException;
	
	public int update(CsvReader reader) throws DatabaseException;
	
	public int remove(List<E> entities) throws DatabaseException;
	
	public int count(QueryRule ...rules) throws DatabaseException;

	public List<E> find(QueryRule ...rules) throws DatabaseException;

	public void find(SpreadsheetWriter writer, QueryRule[] rules) throws DatabaseException;
	
	public void find(SpreadsheetWriter writer, List<String> fieldsToExport, QueryRule[] rules) throws DatabaseException;

	public int remove(CsvReader reader) throws DatabaseException;

	public List<E> toList(CsvReader reader, int limit) throws DatabaseException;

	public String getTableFieldName(String field);

	public FieldType getFieldType(String field);
	
	public void resolveForeignKeys(List<E> enteties) throws ParseException, DatabaseException;
}
