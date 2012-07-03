package org.molgenis.gonl_pheno;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class TupleSource
{
	Database db;
	Logger logger = Logger.getLogger(TupleSource.class);
	
	public TupleSource(Database db)
	{
		this.db = db;
	}
	
	public List<Tuple> getRows() throws DatabaseException
	{
		logger.info("load rows");
		List<Tuple> result = new ArrayList<Tuple>();
		
		//evil because not all measurements may be needed
		List<String> fields = new ArrayList<String>();
		for(Measurement m: db.find(Measurement.class))
		{
			fields.add(m.getName());
		}

		//evil because no scale (placeholder for the TupleTable framework)
		List<ObservedValue> values = db.query(ObservedValue.class).sortASC(ObservedValue.TARGET_NAME).find();
	
		//t
		String lastName = null;
		Tuple row = null;
		for(ObservedValue v: values)
		{
			//sorted by target; new target == new row
			if(!v.getTarget_Name().equals(lastName))
			{
				//create new row
				if(row != null) result.add(row);
				row = new SimpleTuple(fields);
				lastName = v.getTarget_Name();
			}
			
			row.set(v.getFeature_Name(), v.getValue());
		}
		logger.info("load rows complete");
		
		return result;
	}
}
