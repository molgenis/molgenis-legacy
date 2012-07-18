package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.db.jdbc.JDBCQueryGernatorUtil;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

/**
 * Wrap one Protocol EAV model in a TupleTable so that you can query this
 * protocol as if it was a real table (which we actually could implement for
 * smaller protocols).
 * 
 * Here each Measurement is converted into Field, and each ProtocolApplication
 * becomes a Tuple with the ObservedValue for filling the elements of the Tuple.
 * In addition, the 'target' is assumed constant for all ObservedValue and is
 * added as first column. Optionally, the ProtocolApplication metadata can be
 * viewed (future todo).
 */
public class FilterableProtocolTable extends AbstractFilterableTupleTable
{
	// protocol to query
	private Protocol protocol;

	// database holding the data
	private Database db;

	// mapping to Field (changes on paging)
	private List<Field> columns;

	// measurements
	List<Measurement> measurements;

	public FilterableProtocolTable(Database db, Protocol protocol)
	{
		this.db = db;
		this.protocol = protocol;
	}

	@Override
	public int getColCount()
	{
		return protocol.getFeatures_Id().size();
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		try
		{
			Query<Measurement> q = db.query(Measurement.class).in(Measurement.ID, protocol.getFeatures_Id());

			if (this.getColLimit() > 0) q.limit(this.getColLimit());
			if (this.getColOffset() > 0) q.offset(this.getColOffset());

			measurements = q.find();

			columns = new ArrayList<Field>();

			// row name is always there
			columns.add(new Field("target"));

			for (Measurement m : measurements)
			{
				Field col = new Field(m.getName());
				col.setDescription(m.getDescription());
				// todo: setType()
				columns.add(col);
			}

			return columns;
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
	}

	@Override
	public List<Tuple> getRows() throws TableException
	{
		List<Tuple> result = new ArrayList<Tuple>();

		List<String> colNames = new ArrayList<String>();
		for (Field f : getColumns())
			colNames.add(f.getName());

		try
		{
			// for each protocol application get all values (doesn't scale, but
			// good for demo)
			for (Integer protocolApplicationId : this.getRowIds())
			{
				Tuple row = new SimpleTuple(colNames);

				for (ObservedValue v : db.query(ObservedValue.class).eq(ObservedValue.PROTOCOLAPPLICATION, protocolApplicationId)
						.find())
				{
					row.set("target", v.getTarget_Name());
					row.set(v.getFeature_Name(), v.getValue());
				}

				result.add(row);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;

	}

	@Override
	public Iterator<Tuple> iterator()
	{
		try
		{
			return getRows().iterator();
		}
		catch (TableException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void close()
	{
		try
		{
			db.close();
		}
		catch (DatabaseException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public int getCount() throws TableException
	{
		try
		{
			return db.query(ProtocolApplication.class).eq(ProtocolApplication.PROTOCOL, protocol.getIdValue()).count();
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
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

	// we only need to know what rows to show :-)
	// can we implement this via a query abstraction tool??
	private List<Integer> getRowIds() throws TableException, DatabaseException
	{
		// load the measurements
		getColumns();

		//get selected
		Set<String> columnsUsed = new HashSet<String>();
		for(QueryRule r: getFilters())
		{
			columnsUsed.add(r.getField());
		}
		
		//get measurements
		List<Measurement> measurementsUsed = new ArrayList<Measurement>();
		for(Measurement m: measurements)
		{
			if(columnsUsed.contains(m.getName())) measurementsUsed.add(m);
		}
		
		
		// one column is defined by ObservedValue.Investigation,
		// ObservedValue.protocolApplication, ObservedValue.Feature (column
		// 'target' will be moved to ProtocolApplication)
		
		String sql = "SELECT * from ProtocolApplication";
		// filtering
		for (Measurement m : measurementsUsed)
		{
			sql += " NATURAL JOIN (SELECT ObservedValue.protocolApplication as id, ObservedValue.value as "
					+ m.getName() + " FROM ObservedValue WHERE ObservedValue.feature = "+m.getId()+") as "+m.getName();
		}
		// sorting [todo]


		// limit
		List<QueryRule> filters = new ArrayList<QueryRule>(getFilters());
		if (getLimit() > 0) filters.add(new QueryRule(Operator.LIMIT, getLimit()));
		if (getOffset() > 0) filters.add(new QueryRule(Operator.OFFSET, getFilters()));

		List<Integer> result = new ArrayList<Integer>();
		for(Tuple t: db.sql(sql, filters.toArray(new QueryRule[filters.size()]))) result.add(t.getInt("id"));
		return result;
	}
}
