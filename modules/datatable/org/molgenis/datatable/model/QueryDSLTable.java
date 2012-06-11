package org.molgenis.datatable.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.MolgenisFieldTypes;
import org.molgenis.model.elements.Field;
import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import com.mysema.query.sql.SQLQuery;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Predicate;

public class QueryDSLTable implements TupleTable
{
	private boolean loaded = false;
	private final SQLQuery query;
	
	private ResultSetTuple rs;
	private List<Field> columns;
	private final List<Expression> select;
	
	
	public QueryDSLTable(SQLQuery query, List<Expression> select, List<Expression> from, List<Predicate> where) throws TableException {
		this.query = query;
		this.select = select;
		
		query.from((Expression<?>[]) from.toArray());
		query.where((Predicate[]) where.toArray());	
		
		load();
	}	
	
	private void load() throws TableException
	{
		if(!loaded) {
			loaded = true;
			try
			{				
				ResultSet r = query.getResults((Expression<?>[]) select.toArray());
				rs = new ResultSetTuple(r);
				columns = loadColumns();
			}
			catch (Exception e)
			{
				throw new TableException(e);
			}
		}
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
	public List<Field> getColumns() throws TableException
	{
		load();
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
	public int getRowCount() throws TableException
	{
		return new Long(query.count()).intValue();
	}

	@Override
	public void close() throws TableException
	{
		// TODO Auto-generated method stub
		
	}

}
