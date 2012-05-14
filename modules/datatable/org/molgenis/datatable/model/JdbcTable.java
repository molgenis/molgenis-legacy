package org.molgenis.datatable.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.MolgenisFieldTypes;
import org.molgenis.model.elements.Field;
import org.molgenis.util.ResultSetTuple;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class JdbcTable implements TupleTable
{
	public class RSIterator implements Iterator<Tuple> {
	    private ResultSetTuple entities;
	    private boolean didNext = false;
	    private boolean hasNext = false;
	    
	    public RSIterator(ResultSetTuple rs) {
			this.entities = rs;	    	
	    }
	    
	    public Tuple next(){
	    	try {
		        if (!didNext) {
		            entities.next();
		        }
		        didNext = false;
		        return new SimpleTuple(entities);
	    	} catch (SQLException e) {
	    		throw new RuntimeException(e);
	    	}
    	}

	    public boolean hasNext(){
	    	try {
		        if (!didNext) {
		            hasNext = entities.next();
		            didNext = true;
		        }
		        return hasNext;
	    	} catch (SQLException e) {
	    		throw new RuntimeException(e);
	    	}
	    }

		@Override
		public void remove()
		{
			// TODO Auto-generated method stub
			
		}
	}
	
	private final Statement statement;
	private final ResultSetTuple rs;
	
	public JdbcTable(String query, Connection connection) throws SQLException
	{
		super();
		statement = connection.createStatement();
		rs = new ResultSetTuple(statement.executeQuery(query));	
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		final List<Field> columns = new ArrayList<Field>();
		final List<String> fields = rs.getFieldNames();
		int colIdx = 1;
		for(String fieldName : fields) {
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
		try
		{
			List<Tuple> result = new ArrayList<Tuple>();
			
			while(rs.next()) {
				result.add(new SimpleTuple(rs));
			}
			close();
			
			return result;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		} finally {
			if(statement != null) {
				try
				{
					statement.close();
				}
				catch (SQLException e)
				{
					throw new RuntimeException(e);
				}
			}			
		}
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return new RSIterator(rs);
	}

	@Override
	public void close() throws TableException
	{
		try
		{
			rs.close();
			statement.close();
		}
		catch (SQLException e)
		{
			throw new TableException(e);
		}
	}
}
