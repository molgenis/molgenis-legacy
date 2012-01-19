package org.molgenis.gids.converters.phenoModelconverterandloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.gids.GidsSample;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservedValue;

public class PM_Updater {

	private List<Individual> listIndividuals = new ArrayList<Individual>();
	HashMap<String,String> hashIndFeaVal = new HashMap<String, String>();
	HashMap<String,List<String>> targetWithMeasurement = new HashMap<String,List<String>>();
	List<String> listSampleIds = new ArrayList<String>();
	List<String> listsampleMeas = new ArrayList<String>();
	public void makeTFVlists(Database db, String investigation, String sample) throws DatabaseException{
		List<ObservedValue> observedValueList = db.find(ObservedValue.class, new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.EQUALS, investigation));
		List<GidsSample> sampleGIDS = db.find(GidsSample.class,  new QueryRule(ObservedValue.INVESTIGATION_NAME, Operator.EQUALS, investigation));
		/*if(db.query(Investigation.class).eq(Investigation.NAME, investigation).count() !=0){
			listIndividuals = db.find(Individual.class, new QueryRule(Individual.INVESTIGATION_NAME, Operator.EQUALS, investigation));
		}*/
		
		hashIndFeaVal = new HashMap<String, String>();
		targetWithMeasurement = new HashMap<String,List<String>>();
		//List<String> measurements = new ArrayList<String>();
		listSampleIds = new ArrayList<String>();
		listsampleMeas = new ArrayList<String>();
		for(ObservedValue ov : observedValueList){
			String target = ov.getTarget_Name();
			
			// target can be individual or sample
			String feature = ov.getFeature_Name();
			
			if(feature.equals(sample)){
				listSampleIds.add(ov.getValue());
				
			}
			String value = ov.getValue();	
			if(listSampleIds.contains(target)){
				if(!listsampleMeas.contains(feature)){
					listsampleMeas.add(feature);
					
				}
			}
			hashIndFeaVal.put(target+"-"+feature, value);

			
			if(targetWithMeasurement.containsKey(target)){
				
				List<String> tempHolder = targetWithMeasurement.get(target);
				
				if(!tempHolder.contains(feature)){
					tempHolder.add(feature);
				}
				
				targetWithMeasurement.put(target, tempHolder);
				
			}else{
				List<String> measurements = new ArrayList<String>();
				targetWithMeasurement.put(target, measurements);
			}
//			if(!target.equals(lastTarget)){
//				individualWithMeasurement2.put(lastTarget, measurements);
//				measurements = new ArrayList<String>();
//				
//			}
//			measurements.add(feature);
//			lastTarget = target;
		}
	}
	
	
	public List<String> getListsampleMeas() {
		return listsampleMeas;
	}

	public List<String> getListSampleIds() {
		return listSampleIds;
	}

	public List<Individual> getListIndividuals() {
		return listIndividuals;
	}

	public void setListIndividuals(List<Individual> listIndividuals) {
		this.listIndividuals = listIndividuals;
	}

	public HashMap<String, String> getHashIndFeaVal() {
		return hashIndFeaVal;
	}

	public HashMap<String, List<String>> getTargetWithMeasurement() {
		return targetWithMeasurement;
	}
	
}
