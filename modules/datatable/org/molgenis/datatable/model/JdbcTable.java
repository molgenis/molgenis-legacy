package org.molgenis.datatable.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.MolgenisFieldTypes;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.model.elements.Field;
import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class JdbcTable extends AbstractFilterableTupleTable
{
	private ResultSetTuple rs;
	private List<Field> columns;
	private final String query;
	private Database db;
	private final String countQuery;	
	private boolean loaded = false;
	


	public JdbcTable(Database db, String query, List<QueryRule> rules) throws TableException
	{
		super();
		this.db = db;
		this.query = query;
		this.setFilters(rules);		


		String fromExpression = StringUtils.substringBetween(query, "SELECT", "FROM");
		this.countQuery = StringUtils.replace(query, fromExpression, " COUNT(*) ");
	}


	public JdbcTable(Database db, String query) throws TableException
	{
		this(db, query, new ArrayList<QueryRule>());
	}

	
	private void load() throws TableException
	{
		if(!loaded) {
			loaded = true;
			try
			{
				rs = new ResultSetTuple(db.executeQuery(query, getFilters().toArray(new QueryRule[0])));
				columns = loadColumns();
			}
			catch (Exception e)
			{
				throw new TableException(e);
			}
		}
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		load();
		close();
		return columns;
	}

	private List<Field> loadColumns() throws TableException
	{
		load();
		final List<Field> columns = new ArrayList<Field>();
		final List<String> fields = rs.getFieldNames();
		int colIdx = 1;
		for (String fieldName : fields)
		{
			final Field field = new Field(fieldName);
			try
			{
				field.setType(MolgenisFieldTypes.getTypeBySqlTypesCode(rs.getSqlType(colIdx)));
			}
			catch (SQLException e)
			{
				throw new TableException(e);
			}
			columns.add(field);
			++colIdx;
		}
		return columns;
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		load();
		try
		{
			List<Tuple> result = new ArrayList<Tuple>();

			while (rs.next())
			{
				result.add(new SimpleTuple(rs));
			}
			return result;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Don't forget to call close after done with Iterator
	 */	
	@Override
	public Iterator<Tuple> iterator()
	{
		try {
			load();
			return new RSIterator(rs);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close() throws TableException
	{
		try
		{
			if(rs != null) {
				rs.close();
			}
			loaded = false;
		}
		catch (SQLException e)
		{
			throw new TableException(e);
		}
	}

	@Override
	public int getCount() throws TableException
	{
		try {
			final ResultSet countSet = db.executeQuery(countQuery, getFilters().toArray(new QueryRule[0]));
			int rowCount = 0;
			if (countSet.next())
			{
				final Number count = (Number) countSet.getObject(1);
				rowCount = count.intValue();
			}
			countSet.close();
			return rowCount;
		} catch (Exception ex) {
			throw new TableException(ex);
		}
	}

	public void setDatabase(Database db)
	{
		this.db = db;
	}


	@Override
	public void setVisibleColumns(List<String> fieldNames)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<Field> getVisibleColumns()
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setDb(Database db)
	{
		this.db = db;
	}
}
