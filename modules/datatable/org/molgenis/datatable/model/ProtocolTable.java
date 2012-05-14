package org.molgenis.datatable.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;

/**
 * Wrap one Protocol EAV model in a TupleTable so that you can query this protocol as if it was a real table (which we actually could implement for smaller protocols). 
 * 
 * Here each Measurement is converted into Field, and each ProtocolApplication
 * becomes a Tuple with the ObservedValue for filling the elements of the Tuple.
 * In addition, the 'target' is assumed constant for all ObservedValue and is
 * added as first column. Optionally, the ProtocolApplication metadata can be
 * viewed (future todo).
 */
public class ProtocolTable implements TupleTable
{
	// protocol to query
	Protocol protocol;
	
	// database holding the data
	Database db;
	
	// mapping to Field (so we only have to convert once)
	List<Field> columns;
	
	public ProtocolTable(Protocol protocol)
	{
		
	}

	@Override
	public List<Field> getColumns()
	{
		//using this query for backward compatibility with non-jpa
		try
		{
			List<Measurement> measurements = db.query(Measurement.class).in(Measurement.ID, protocol.getFeatures_Id()).find();
			
			columns = new ArrayList<Field>();
			
			for(Measurement m: measurements)
			{
				Field col = new Field(m.getName());
				col.setDescription(m.getDescription());
				//todo: setType()
				
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
		// TODO create here the big join query
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		//TODO: can we use the streaming interface of the database for this in some way?
		return getRows().iterator();
	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub
	}

}
