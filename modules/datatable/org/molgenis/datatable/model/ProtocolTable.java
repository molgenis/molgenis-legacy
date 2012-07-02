package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
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
public class ProtocolTable extends AbstractTupleTable
{
	// protocol to query
	private Protocol protocol;

	// database holding the data
	private Database db;

	// mapping to Field (so we only have to convert once)
	private List<Field> columns;

	public ProtocolTable(Database db, Protocol protocol)
	{
		this.db = db;
		this.protocol = protocol;
	}

	@Override
	public List<Field> getColumns()
	{
		try
		{
			List<Measurement> measurements = db.query(Measurement.class).in(Measurement.ID, protocol.getFeatures_Id())
					.find();

			columns = new ArrayList<Field>();
			
			//target
			columns.add( new Field("target"));

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
			// TODO need exception?
			e.printStackTrace();

			return null;
		}
	}

	@Override
	public List<Tuple> getRows()
	{
		List<Tuple> result = new ArrayList<Tuple>();
		
		List<String> colNames = new ArrayList<String>();
		for(Field f: getColumns()) colNames.add(f.getName());

		try
		{
			// get protocol applications
			Query<ProtocolApplication> q = db.query(ProtocolApplication.class).eq(ProtocolApplication.PROTOCOL,
					protocol.getId());
			if (getLimit() > 0) q.limit(getLimit());
			if (getOffset() > 0) q.offset(getOffset());
			List<ProtocolApplication> apps = q.find();

			// for each protocol application get all values
			for (ProtocolApplication pa : apps)
			{
				Tuple row = new SimpleTuple(colNames);
				
				for(ObservedValue v: db.query(ObservedValue.class).eq(ObservedValue.PROTOCOLAPPLICATION, pa.getId()).find())
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
		// TODO: can we use the streaming interface of the database for this in
		// some way?
		return getRows().iterator();
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
}
