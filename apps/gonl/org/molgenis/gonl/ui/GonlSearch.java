package org.molgenis.gonl.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.SequenceVariant;

/**
 * GonlSearchController takes care of all user requests and application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the user
 * <li>GonlSearchModel holds application state and business logic on top of domain model.
 * Get it via this.getModel()/setModel(..).
 * <li>PathoSearchView holds the template to show the layout. 
 * Get/set it via this.getView()/setView(..).
 */
public class GonlSearch extends EasyPluginController<GonlSearchModel>
{
	public GonlSearch(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new GonlSearchModel(this)); // the default model
		//this.setView(new GonlSearchView(getModel())); //
	}

	@Override
	public void reload(Database db) throws Exception
	{
		SliceableMatrix m = null;

		getModel().setChromosomes(new ArrayList<String>());
		for (Chromosome c : db.query(Chromosome.class).find())
		{
			this.getModel().getChromosomes().add(c.getName());
		}

		// select chromosome, startpos, endpos
		// m.sliceByRow(SequenceVariant.CHR, QueryRule.Operator.EQUALS,
		// getModel().getSelectedChrId());
	}
	
	public void search(Database db, Tuple request) throws DatabaseException
	{
		//reset
		getModel().setAlleleCounts(null);
		getModel().setVariants(null);
		
		// Get the ID of the chromosome.
		Query<Chromosome> qChromosome = db.query(Chromosome.class).eq(Chromosome.NAME, request.getString("chromosome"));
		int ChrId = qChromosome.find().get(0).getId();
		this.logger.debug("Lookedup chromosome ID: " + ChrId + "for Chr " + request.getString("chromosome"));
		
		//set search params
		//getModel().setSelectedChrId(request.getInt("chromosome"));
		getModel().setSelectedChrId(ChrId);
		getModel().setSelectedChrName(request.getString("chromosome"));
		getModel().setSelectedFrom(request.getInt("from"));
		getModel().setSelectedTo(request.getInt("to"));
		
		//count available variants, if too much return error
		//Query<SequenceVariant> q = db.query(SequenceVariant.class).eq(SequenceVariant.CHR_NAME, getModel().getSelectedChrId())
		//		.greaterOrEqual(SequenceVariant.ENDBP, request.getInt("from"))
		//		.lessOrEqual(SequenceVariant.STARTBP, request.getInt("to"));	
		Query<SequenceVariant> q = db.query(SequenceVariant.class).eq(SequenceVariant.CHR, getModel().getSelectedChrId())
				.greaterOrEqual(SequenceVariant.ENDBP, request.getInt("from"))
				.lessOrEqual(SequenceVariant.STARTBP, request.getInt("to"));
		int count = q.count();
		if (count == 0) {
			throw new DatabaseException("No variants found in the search window...");
		} else if (count > 1000) {
			throw new DatabaseException("Your query resulted in too much data; Please reduce the search window...");
		} else if (count == 1) {
			this.getMessages().add(new ScreenMessage("Found 1 variant", true));
		} else {
			this.getMessages().add(new ScreenMessage("Found "+count+" variants", true));
		}
		
		//set count and variants into model
		getModel().setCount(count);
		getModel().setVariants(q.find());
		
		//get selected ids and retrieve matching observations
		List<Integer> ids = new ArrayList<Integer>();
		for(SequenceVariant v: getModel().getVariants()) ids.add(v.getId());
		List<ObservedValue> values = db.query(ObservedValue.class).in(ObservedValue.FEATURE, ids).find();
		
		//put values in a map to used in view
		Map<String,ObservedValue> valueMap = new LinkedHashMap<String,ObservedValue>();
		for(ObservedValue value: values)
		{
			valueMap.put(value.getFeature_Name(), value);
		}
		getModel().setAlleleCounts(valueMap);		
	}

	@Override
	public ScreenView getView()
	{
		return new GonlSearchView(getModel()); // <plugin
	}

}