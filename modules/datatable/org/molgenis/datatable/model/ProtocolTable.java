package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Category;
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
	private List<Field> columns = new ArrayList<Field>();
	private String targetString = "Pa_Id";

	public String getTargetString()
	{
		return targetString;
	}

	public void setTargetString(String targetString)
	{
		this.targetString = targetString;
	}

	// measurements
	Map<Measurement, Protocol> measurements = new LinkedHashMap<Measurement, Protocol>();

	public ProtocolTable(Database db, Protocol protocol) throws TableException
	{
		this.setDb(db);

		if (protocol == null) throw new TableException("protocol cannot be null");

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
		if (columns.size() == 0)
		{
			try
			{
				// get all features of protocol AND subprotocols
				measurements = getMeasurementsRecursive(protocol);

				Field target = new Field(targetString);

				columns.add(target);

				// convert into field
				for (Measurement m : measurements.keySet())
				{
					Field col = new Field(m.getName());

					col.setDescription(m.getDescription());
					// todo: setType()
					columns.add(col);
				}

			}
			catch (Exception e)
			{
				throw new TableException(e);
			}
		}
		return columns;
	}

	private Map<Measurement, Protocol> getMeasurementsRecursive(Protocol protocol) throws DatabaseException
	{
		List<Integer> featureIds = protocol.getFeatures_Id();

		Map<Measurement, Protocol> result = new LinkedHashMap<Measurement, Protocol>();

		if (featureIds.size() > 0)
		{
			List<Measurement> mList = getDb().query(Measurement.class).in(Measurement.ID, featureIds).find();
			for (Measurement m : mList)
			{
				result.put(m, protocol);
			}
		}

		// go recursive on all subprotocols
		if (protocol.getSubprotocols_Id().size() > 0)
		{
			List<Protocol> subProtocols = getDb().query(Protocol.class).in(Protocol.ID, protocol.getSubprotocols_Id())
					.find();
			for (Protocol subProtocol : subProtocols)
			{
				result.putAll(getMeasurementsRecursive(subProtocol));
			}
		}

		// return all the featureId
		return result;

	}

	public List<Tuple> getRows() throws TableException
	{
		try
		{
			// List<String> colNames = new ArrayList<String>();
			// for (Field f : getColumns())
			// {
			// colNames.add(protocol.getName() + "." + f.getName());
			// }

			List<Tuple> result = new ArrayList<Tuple>();
			for (Integer rowId : getRowIds(false))
			{
				boolean target = false;
				Tuple row = new SimpleTuple();

				Database db = getDb();

				for (ObservedValue v : db.query(ObservedValue.class).eq(ObservedValue.PROTOCOLAPPLICATION, rowId)
						.find())
				{
					if (!target)
					{
						row.set(targetString, v.getTarget_Name());
						target = true;
					}

					// get measurements (evil expensive)
					Protocol p = null;

					Measurement currentMeasurement = null;
					for (Measurement m : measurements.keySet())
					{
						if (m.getName().equals(v.getFeature_Name()))
						{
							p = measurements.get(m);
							currentMeasurement = m;
							break;
						}
					}

					if ("categorical".equals(currentMeasurement.getDataType()))
					{

						for (Category c : db.find(Category.class, new QueryRule(Category.NAME, Operator.IN,
								currentMeasurement.getCategories_Name())))
						{
							if (v.getValue().equals(c.getCode_String()))
							{
								row.set(v.getFeature_Name(), v.getValue() + "." + c.getDescription());
								break;
							}
						}
					}
					else
					{
						if (!v.getValue().isEmpty()) row.set(v.getFeature_Name(), v.getValue());
					}
				}
				result.add(row);
			}

			// Query for the measurement that is asked to sort, Scolsom01, with
			// filter rule
			//

			if (this.getFilters().size() > 0)
			{

			}

			return result;
		}
		catch (Exception e)
		{
			throw new TableException(e);
		}
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

	// FILTERING
	// we only need to know what rows to show :-)
	private List<Integer> getRowIds(boolean count) throws TableException, DatabaseException
	{
		// get columns that are used in filtering or sorting
		Set<String> columnsUsed = new HashSet<String>();

		for (QueryRule r : getFilters())
		{

			// IF SEARCH BUTTON IS CLICKED
			if (getFilters().get(0).getField() != null)
			{
				columnsUsed.add(r.getField());
			}
			else
			{
				// IF WE WANT TO ORDER A COLUMN
				columnsUsed.add(r.getValue().toString());
			}
		}

		// get measurements
		List<Measurement> measurementsUsed = new ArrayList<Measurement>();

		if (columnsUsed.size() > 0)
		{
			measurementsUsed = getDb().query(Measurement.class)
					.in(Measurement.NAME, new ArrayList<String>(columnsUsed)).find();

		}

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
		if (columnsUsed.contains(targetString))
		{
			sql += " NATURAL JOIN (SELECT id as targetId, name as " + this.targetString
					+ " from ObservationElement) as " + this.targetString;
		}

		List<QueryRule> filters = new ArrayList<QueryRule>(getFilters());

		// limit and offset
		if (!count && getLimit() > 0)
		{
			filters.add(new QueryRule(Operator.LIMIT, getLimit()));
		}
		if (!count && getOffset() > 0)
		{
			filters.add(new QueryRule(Operator.OFFSET, getOffset()));
		}

		List<Integer> result = new ArrayList<Integer>();
		// sql = SELECT count (*) as id from ProtocolApplication
		// filters = Scl90som3 = '1'
		// filters.size() = 1

		for (Tuple t : this.getDb().sql(sql, filters.toArray(new QueryRule[filters.size()])))
			result.add(t.getInt("id"));

		return result;
	}
}
