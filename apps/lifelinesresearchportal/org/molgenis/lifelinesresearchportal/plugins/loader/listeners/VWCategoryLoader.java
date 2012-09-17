package org.molgenis.lifelinesresearchportal.plugins.loader.listeners;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;
import org.molgenis.util.TupleReader;

//import static org.hamcrest.text.IsEqualIgnoringCase.*;

public class VWCategoryLoader extends ImportTupleLoader
{

	private final Map<String, Measurement> measurements = new LinkedHashMap<String, Measurement>();
	private final List<Category> categories = new ArrayList<Category>();
	private final Investigation investigation;

	public VWCategoryLoader(Map<String, Protocol> protocols, Investigation investigation, String name, Database db)
			throws DatabaseException
	{
		super(name, db);

		// create a hash map of all measurements
		for (Measurement m : db.find(Measurement.class))
		{
			measurements.put(m.getName(), m);
		}
		this.investigation = investigation;
	}

	@Override
	public void load(TupleReader csvIterator) throws Exception
	{

		for (Tuple tuple : csvIterator)
		{
			// get the measurement
			Measurement m = measurements.get(tuple.getString("TABNAAM") + "_" + tuple.getString("VELD"));

			// create the category
			if (m != null && tuple.getString("VALLABELABEL") != null)
			{
				Category category = new Category();
				category.setInvestigation(investigation);
				category.setName(m.getName() + "_" + tuple.getString("VALLABELABEL"));
				category.setCode_String(tuple.getString("VALLABELVAL"));
				category.setLabel(tuple.getString("VALLABELABEL"));
				category.setDescription(tuple.getString("VALLABELABEL"));

				// add measurement, then get id and set it on
				// measurement.categories
				categories.add(category);
				m.getCategories_Name().add(category.getName());
			}
		}
	}

	@Override
	public void commit() throws Exception
	{
		// TODO Auto-generated method stub
		db.update(categories, DatabaseAction.ADD_IGNORE_EXISTING, Category.NAME);
		db.update(new ArrayList(measurements.values()));
	}

}
