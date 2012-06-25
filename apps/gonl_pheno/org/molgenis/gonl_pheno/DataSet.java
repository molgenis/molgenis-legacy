package org.molgenis.gonl_pheno;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.ExampleData;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;

public class DataSet implements ExampleData
{
	public Map<String, Panel> panels = new LinkedHashMap<String,Panel>();
	public Map<String, Individual> individuals = new LinkedHashMap<String,Individual>();
	public Map<String, Measurement> measurements = new LinkedHashMap<String,Measurement>();
	public List<ObservedValue> values = new ArrayList<ObservedValue>();
	
	public void addPanel(String name)
	{
		if(panels.get(name) == null)
		{
			Panel p = new Panel();
			p.setName(name);
			panels.put(name, p);
		}
	}

	public void addIndividual(String name)
	{
		if(individuals.get(name) == null)
		{
			Individual i = new Individual();
			i.setName(name);
			individuals.put(name, i);
		}
	}

	public void addValue(Individual individual, String measurement, String value)
	{		
		if(measurements.get(measurement) == null)
		{
			Measurement m = new Measurement();
			m.setName(measurement);
			measurements.put(measurement, m);
		}
		
		ObservedValue v = new ObservedValue();
		v.setTarget_Name(individual.getName());
		v.setFeature_Name(measurement);
		v.setValue(value);
		values.add(v);
	}

	@Override
	public void load(Database db) throws DatabaseException
	{
		db.add(new ArrayList(this.measurements.values()));
		db.add(new ArrayList(this.individuals.values()));
		db.add(new ArrayList(this.panels.values()));
		db.add(this.values);
		
	}
}
