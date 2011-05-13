package convertors.gids;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import app.CsvExport;
import app.JDBCDatabase;

public class ConvertGidsToPheno
{
	private Logger logger;
	final List<Individual> individualsList  = new ArrayList<Individual>();
	final List<Measurement> measurementsList  = new ArrayList<Measurement>();
	final List<ObservedValue> valuesList  = new ArrayList<ObservedValue>();
	final List<Investigation> investigationList = new ArrayList<Investigation>();
	private String invName;
	
	
	public void converter(String filename,String outputDir, String invName) throws Exception{
		
		//String filename = "C:/Documents and Settings/Administrator/workspace/molgenis_apps/handwritten/java/convertors/gids/export_CeliacSprue_for_PhenoModel.csv";
		
		ConvertGidsToPheno conv = new ConvertGidsToPheno();
		conv.makeInvestigation(invName);
		//conv.populateIndividual(filename,invName);
		conv.populateMeasurement(filename,invName);
		conv.populateValue(filename,invName);
		this.invName = invName;
		CsvExport export = new CsvExport();
		//File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpDir = new File(outputDir);
		
		System.out.println("individualsList size = " + individualsList.size());
		
		export.exportAll(tmpDir, individualsList, measurementsList, valuesList, investigationList);
	}

	public ConvertGidsToPheno() throws Exception
	{
		logger = Logger.getLogger("ConvertGidsToPheno");
	}
	/*
	public void populateInvestigations(){
		
		String [] listOfInvestigations = {"CeliacSprue", "PreventCD", "IBD", "COPD", "GODDAF", "SLE"};
		
		for(String invName: listOfInvestigations){
			investigationList.add(makeInvestigation(invName));
		}
		
	}
	*/
	public Investigation makeInvestigation(String invName){
		Investigation newInvest = new Investigation();
		newInvest.setName(invName);
		logger.info("#########################  makeInvestigation    " + invName );
		return newInvest;
	}
	
	public void populateIndividual(String filename) throws Exception
	{
		final List<String> namesSeen = new ArrayList<String>();
		
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				//logger.info("Parsing line: " + line_number);
				
				String gidsId = tuple.getString("id_individual");
				String mother_Name = tuple.getString("id_mother");
				String father_Name = tuple.getString("id_father");
				
				if (!namesSeen.contains(gidsId)) {
					namesSeen.add(gidsId);
					Individual newIndividual = new Individual();
					newIndividual.setName(gidsId);
					newIndividual.setInvestigation_Name(invName);
					newIndividual.setMother_Name(mother_Name);					
					newIndividual.setFather_Name(father_Name);	
					individualsList.add(newIndividual);
				}
			}
		});
	}
	
	public void populateMeasurement(String filename, String invName) throws Exception {
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		for (String header : reader.colnames()) {
			if (!header.equals("id_individual") && !header.equals("id_mother") && !header.equals("id_father")) {
				Measurement measurement = new Measurement();
				measurement.setName(header);
				measurement.setInvestigation_Name(invName);
				measurementsList.add(measurement);
			}
		}
	}
	
	public String getInvestigation(){
		return invName;
	}
	
	
	public void populateValue(String filename, String invName) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				//logger.info("Parsing line: " + line_number);
				
				String targetName = tuple.getString("id_individual");
				
				// date_of_birth
				String date_of_birth = tuple.getString("date_of_birth");
				//valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(0).getName(), now, 
						//null, customIdMeasurementId, newAnimal.getName(), oldAnimalId, null));
				
				// physician_id 
				Integer physician_id = tuple.getInt("physician_id");
				//valuesToAddList.add(ct.createObservedValue(invid, protocolAppsToAddList.get(0).getName(), now, 
					//	null, customIdMeasurementId, newAnimal.getName(), oldAnimalId, null));
				
				//id_project
				
				//house_nr
				String house_nr = tuple.getString("house_nr");
				
				
				//sampling_date
				String sampling_date = tuple.getString("sampling_date");
				
				
				//work_up_date
				String work_up_date = tuple.getString("work_up_date");
				
				
				//dna_od_ratio
				Double dna_od_ratio = tuple.getDouble("dna_od_ratio");
				
				
				//od_260
				Double od_260 = tuple.getDouble("od_260");
				
				
				//isolate_concentration
				Double isolate_concentration = tuple.getDouble("isolate_concentration");
				
				
				//isolate_volume
				Double isolate_volume = tuple.getDouble("isolate_volume");
				
				
				//isolate_yield
				Double isolate_yield = tuple.getDouble("isolate_yield");
				
				
				//ul_dna
				Double ul_dna = tuple.getDouble("ul_dna");
				
				
				//******Enums!!******
				//informed_consent (yes/no/unknown)
				//twin_confirmed (yes/no/unknown)
				//twin_type (monozygotic/dizygotic/unknown)
				//is_proband(yes/no/unknown
				//deceased(yes/no/unknown)
				//has_medication(yes/no/unknown)
				//status_for_project(affected/carrier/unaffected/susp.carrier/susp.affected/questionable/control)
				//keep_informed(yes/no/unknown
				
				//isolate_type (dna,rna,blood
				
				for (Measurement m : measurementsList) {
					String featureName = m.getName();	
					String value = tuple.getString(featureName);
					
					//if(value.contains("'")){
					//	value = value.replace("'", "");
					//}
					ObservedValue newValue = new ObservedValue();
					newValue.setFeature_Name(featureName);
					newValue.setTarget_Name(targetName);
					newValue.setValue(value);
					newValue.setInvestigation_Name("CeliacSprue");
					
					valuesList.add(newValue);
				}
			}
		});
	}
}
