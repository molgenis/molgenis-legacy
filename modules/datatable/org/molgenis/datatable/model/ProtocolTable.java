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
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
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
public class ProtocolTable extends AbstractFilterableTupleTable
{
	// protocol to query
	private Protocol protocol;

	// mapping to Field (changes on paging)
	private List<Field> columns;

	// measurements
	List<Measurement> measurements;

	public ProtocolTable(Database db, Protocol protocol)
	{
		this.setDb(db);
		this.protocol = protocol;
	}

	@Override
	public int getColCount()
	{
		// +1 for the target column
		return protocol.getFeatures_Id().size() + 1;
	}

	public List<Field> getAllColumns() throws TableException
	{
		return this.getColumns(false);
	}

	@Override
	public List<Field> getColumns() throws TableException
	{
		return this.getColumns(true);
	}

	private List<Field> getColumns(boolean visibleColumnsOnly) throws TableException
	{
		// first column is 'target'
		try
		{
			columns = new ArrayList<Field>();

			// get meta data
			Query<Measurement> q = this.getDb().query(Measurement.class).in(Measurement.ID, protocol.getFeatures_Id());
			if (visibleColumnsOnly && this.getColLimit() > 0)
			{
				// first column is target
				if (this.getColOffset() == 0)
				{
					q.limit(this.getColLimit() - 1);
					columns.add(new Field("target"));
				}
				else
				{
					q.limit(this.getColLimit());
				}
			}

			// always substract 1 for the 'target' column
			if (visibleColumnsOnly && this.getColOffset() > 0) q.offset(this.getColOffset() - 1);

			measurements = q.find();

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

	/**
	 * Iteratively retrieve the rows; we may want some caching mechanism to
	 * retrieve multiple rows per call
	 */
	private static class ProtocolTupleIterator implements Iterator<Tuple>
	{
		// wrapper state
		ProtocolTable table;

		// rowIterator
		Iterator<Integer> rowIndexIterator;

		List<String> colNames;

		ProtocolTupleIterator(ProtocolTable table)
		{
			try
			{
				this.table = table;
				rowIndexIterator = table.getRowIds(false).iterator();
				colNames = new ArrayList<String>();

				for (Field f : table.getColumns())
				{
					colNames.add(f.getName());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		@Override
		public boolean hasNext()
		{
			return rowIndexIterator.hasNext();
		}

		@Override
		public Tuple next()
		{
			try
			{
				Integer rowId = rowIndexIterator.next();
				Tuple row = new SimpleTuple(colNames);

				for (ObservedValue v : table.getDb().query(ObservedValue.class)
						.eq(ObservedValue.PROTOCOLAPPLICATION, rowId).find())
				{
					row.set("target", v.getTarget_Name());
					row.set(v.getFeature_Name(), v.getValue());
				}
				return row;
			}
			catch (DatabaseException e)
			{
				throw new RuntimeException(e);
			}
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		return new ProtocolTupleIterator(this);
	}

	@Override
	public void close()
	{
		try
		{
			this.getDb().close();
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
			return this.getRowIds(true).get(0);
		}
		catch (DatabaseException e)
		{
			throw new TableException(e);
		}
	}

	// we only need to know what rows to show :-)
	private List<Integer> getRowIds(boolean count) throws TableException, DatabaseException
	{
		// load the measurements
		getColumns();

		// get columns that are used in filtering or sorting
		Set<String> columnsUsed = new HashSet<String>();
		for (QueryRule r : getFilters())
		{
			columnsUsed.add(r.getField());
		}

		// get measurements
		List<Measurement> measurementsUsed = getDb().query(Measurement.class)
				.in(Measurement.NAME, new ArrayList<String>(columnsUsed)).find();

		// one column is defined by ObservedValue.Investigation,
		// ObservedValue.protocolApplication, ObservedValue.Feature (column
		// 'target' will be moved to ProtocolApplication)

		String sql = "SELECT id from ProtocolApplication ";
		if (count) sql = "SELECT count(*) as id from ProtocolApplication";

		for (Measurement m : measurementsUsed)
		{
			sql += " NATURAL JOIN (SELECT ObservedValue.protocolApplication as id, ObservedValue.target as targetId, ObservedValue.value as "
					+ m.getName()
					+ " FROM ObservedValue WHERE ObservedValue.feature = "
					+ m.getId()
					+ ") as "
					+ m.getName();
		}
		// filtering [todo: data model change!]
		if (columnsUsed.contains("target"))
		{
			sql += " NATURAL JOIN (SELECT id as targetId, name as target from ObservationElement) as target";
		}

		List<QueryRule> filters = new ArrayList<QueryRule>(getFilters());

		// limit and offset
		if (!count && getLimit() > 0) filters.add(new QueryRule(Operator.LIMIT, getLimit()));
		if (!count && getOffset() > 0) filters.add(new QueryRule(Operator.OFFSET, getOffset()));

		List<Integer> result = new ArrayList<Integer>();
		for (Tuple t : this.getDb().sql(sql, filters.toArray(new QueryRule[filters.size()])))
			result.add(t.getInt("id"));
		return result;
	}
}
