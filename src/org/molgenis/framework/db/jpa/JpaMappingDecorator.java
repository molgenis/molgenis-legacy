package org.molgenis.framework.db.jpa;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.molgenis.fieldtypes.FieldType;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Mapper;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.SpreadsheetWriter;

public class JpaMappingDecorator<E extends Entity> implements JpaMapper<E>
{
	private Mapper<E> mapper;

	public JpaMappingDecorator(Mapper<E> generatedMapper)
	{
		this.mapper = generatedMapper;
	}

	@Override
	public int add(List<E> entities) throws DatabaseException
	{
		return mapper.add(entities);
	}

	@Override
	public int update(List<E> entities) throws DatabaseException
	{
		return mapper.update(entities);
	}

	@Override
	public int remove(List<E> entities) throws DatabaseException
	{
		return mapper.remove(entities);
	}

	@Override
	public int add(CsvReader reader, SpreadsheetWriter writer) throws DatabaseException
	{
		return mapper.add(reader, writer);
	}

	@Override
	public int count(QueryRule... rules) throws DatabaseException
	{
		return mapper.count(rules);
	}

	@Override
	public List<E> find(QueryRule ...rules) throws DatabaseException
	{
		return mapper.find(rules);
	}

	@Override
	public void find(SpreadsheetWriter writer, QueryRule ...rules) throws DatabaseException
	{
		mapper.find(writer, rules);
	}

	@Override
	public Database getDatabase()
	{
		return mapper.getDatabase();
	}

	@Override
	public int remove(CsvReader reader) throws DatabaseException
	{
		return mapper.remove(reader);
	}

	@Override
	public List<E> toList(CsvReader reader, int limit) throws DatabaseException
	{
		return mapper.toList(reader, limit);
	}

	@Override
	public int update(CsvReader reader) throws DatabaseException
	{
		return mapper.update(reader);
	}

	@Override
	public FieldType getFieldType(String field)
	{
		// TODO Auto-generated method stub
		return mapper.getFieldType(field);
	}

	@Override
	public String getTableFieldName(String field)
	{
		// TODO Auto-generated method stub
		return mapper.getTableFieldName(field);
	}

	@Override
	public E create()
	{
		return mapper.create();
	}

	@Override
	public void find(SpreadsheetWriter writer, List<String> fieldsToExport, QueryRule ...rules) throws DatabaseException
	{
		mapper.find(writer, fieldsToExport, rules);
	}

	@Override
	public void resolveForeignKeys(List<E> enteties) throws ParseException,
			DatabaseException
	{
		mapper.resolveForeignKeys(enteties);
	}

	@Override
	public List<E> findAll()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public List<E> find(String jpaQlWhereClause, Integer limit, Integer offset)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public int count(String jpaQlWhereClause)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void prepareFileAttachements(List<E> entities, File dir)
			throws IOException
	{
		throw new UnsupportedOperationException();		
	}

	@Override
	public boolean saveFileAttachements(List<E> entities, File dir)
			throws IOException
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public String createFindSql(QueryRule[] rules) throws DatabaseException
	{
		return mapper.createFindSql(rules);
	}
}
