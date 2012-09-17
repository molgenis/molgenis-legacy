package org.molgenis.patho.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Tuple;
import org.molgenis.variant.Chromosome;
import org.molgenis.variant.SequenceVariant;

/**
 * PathoSearchController takes care of all user requests and application logic.
 * 
 * <li>Each user request is handled by its own method based action=methodName.
 * <li>MOLGENIS takes care of db.commits and catches exceptions to show to the
 * user <li>PathoSearchModel holds application state and business logic on top
 * of domain model. Get it via this.getModel()/setModel(..) <li>PathoSearchView
 * holds the template to show the layout. Get/set it via
 * this.getView()/setView(..).
 */
public class PathoSearch extends EasyPluginController<PathoSearchModel>
{
	public PathoSearch(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(new PathoSearchModel(this)); // the default model
	}
	
	public ScreenView getView()
	{
		return new PathoSearchView(getModel());
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
		
		//set search params
		getModel().setSelectedChrId(request.getInt("chromosome"));
		getModel().setSelectedFrom(request.getInt("from"));
		getModel().setSelectedTo(request.getInt("to"));
		
		//count available variants, if too much return error
		Query<SequenceVariant> q = db.query(SequenceVariant.class).eq(SequenceVariant.CHR_NAME, getModel().getSelectedChrId()).greaterOrEqual(SequenceVariant.ENDBP, request.getInt("from")).lessOrEqual(SequenceVariant.STARTBP, request.getInt("to"));	
		int count = q.count();
		if(count > 100) throw new DatabaseException("Your query resulted in too many data. Please reduce search window");
		
		//set count and variants into model
		this.setSuccess("Found "+count+" variants");
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

}