package org.molgenis.framework.db.jdbc;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvWriter;
import org.molgenis.util.Entity;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.jdbc.ColumnInfo.Type;

public class MappingDecorator<E extends Entity> implements JDBCMapper<E>
{
	private JDBCMapper<E> mapper;

	public MappingDecorator(JDBCMapper<E> generatedMapper)
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
	public int add(CsvReader reader, CsvWriter writer) throws DatabaseException
	{
		return mapper.add(reader, writer);
	}

	@Override
	public int count(QueryRule... rules) throws DatabaseException
	{
		return mapper.count(rules);
	}

	@Override
	public List<E> find(QueryRule[] rules) throws DatabaseException
	{
		return mapper.find(rules);
	}

	@Override
	public void find(CsvWriter writer, QueryRule[] rules) throws DatabaseException
	{
		mapper.find(writer, rules);
	}

	@Override
	public JDBCDatabase getDatabase()
	{
		return mapper.getDatabase();
	}

	@Override
	public int remove(CsvReader reader) throws DatabaseException
	{
		return mapper.remove(reader);
	}

	@Override
	public List<E> toList(CsvReader reader, int limit) throws Exception
	{
		return mapper.toList(reader, limit);
	}

	@Override
	public int update(CsvReader reader) throws DatabaseException
	{
		return mapper.update(reader);
	}

	@Override
	public Type getFieldType(String field)
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
	public void find(CsvWriter writer, List<String> fieldsToExport, QueryRule[] rules) throws DatabaseException
	{
		mapper.find(writer, fieldsToExport, rules);
	}
}
