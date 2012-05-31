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

public class JdbcTable implements TupleTable
{
	public class RSIterator implements Iterator<Tuple>
	{
		private ResultSetTuple entities;
		private boolean didNext = false;
		private boolean hasNext = false;

		public RSIterator(ResultSetTuple rs)
		{
			this.entities = rs;
		}

		public Tuple next()
		{
			try
			{
				if (!didNext)
				{
					entities.next();
				}
				didNext = false;
				return new SimpleTuple(entities);
			}
			catch (SQLException e)
			{
				throw new RuntimeException(e);
			}
		}

		public boolean hasNext()
		{
			try
			{
				if (!didNext)
				{
					hasNext = entities.next();
					didNext = true;
				}
				return hasNext;
			}
			catch (SQLException e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove()
		{
			// TODO Auto-generated method stub

		}
	}

	private ResultSetTuple rs;
	private List<Field> columns;
	private final String query;
	private final List<QueryRule> rules;
	private final Database db;
	private final String countQuery;	
	private boolean loaded = false;
	

	public JdbcTable(Database db, String query, List<QueryRule> rules) throws TableException
	{
		super();
		this.db = db;
		this.query = query;
		this.rules = rules;		

		String fromExpression = StringUtils.substringBetween(query, "SELECT", "FROM");
		this.countQuery = StringUtils.replace(query, fromExpression, " COUNT(*) ");
	}

	public JdbcTable(Database db, String query) throws TableException
	{
		this(db, query, Collections.<QueryRule> emptyList());
	}

	private void load() throws TableException
	{
		if(!loaded) {
			loaded = true;
			try
			{
				rs = new ResultSetTuple(db.executeQuery(query, rules.toArray(new QueryRule[0])));
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
			close();

			return result;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

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
			rs.close();
		}
		catch (SQLException e)
		{
			throw new TableException(e);
		}
	}

	@Override
	public int getRowCount() throws TableException
	{
		try {
			final ResultSet countSet = db.executeQuery(countQuery, rules.toArray(new QueryRule[0]));
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

}
