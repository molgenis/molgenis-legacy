package org.molgenis.gids.converters;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.gids.GidsSample;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;
import app.CsvExport;

public class GidsConvertor
{
	private Logger logger;
	final List<Individual> individualsList  = new ArrayList<Individual>();
	final List<Measurement> measurementsList  = new ArrayList<Measurement>();
	final List<Measurement> totalMeasurementsList  = new ArrayList<Measurement>();
	final List<ObservedValue> valuesList  = new ArrayList<ObservedValue>();
	final List<Investigation> investigationList = new ArrayList<Investigation>();
	final List<GidsSample> samplesList = new ArrayList<GidsSample>();
	final List<Measurement> indvList = new ArrayList<Measurement>();
	private String invName;
	private ArrayList<String> alIndividuals = new ArrayList<String>();
	
	public File tmpDir = null;
	
	Database db;

	public void converter(File file, String invName, Database db, String individual, String father, String mother,String sample, List<String> samplemeaslist, List<String> indvmeaslist, HashMap<String,String> hashChangeMeas) throws Exception{
		
		this.invName = invName;
		this.db = db;
		makeInvestigation(invName);
		populateIndividualAndSample(file,invName,individual, father, mother,sample);
		populateMeasurement(file,invName,individual, father, mother, sample, hashChangeMeas);
		
		populateValue(file,invName, individual, sample, samplemeaslist);
		
		CsvExport export = new CsvExport();
		tmpDir = new File(System.getProperty("java.io.tmpdir"));
		
		logger.info("############   "+tmpDir.toString());
		File a = new File(tmpDir + File.separator +"measurement.txt");
		boolean flag = false;
		if(a.exists()){
			flag=a.delete();
		}
		else{
			logger.info("Measurement.txt was not in de tmpdirectory");
		}
		try{
			System.out.println("samplesList.size(): " + samplesList.size());
			export.exportAll(tmpDir, individualsList, samplesList, measurementsList, valuesList);
		} catch(Exception e){
			logger.info("CANNOT EXPORT DATA");
		}
	}

	public Database getDb() {
		return db;
	}
	
	public Integer getListSizeTargets (){
		return individualsList.size();
	}
	public Integer getListSizeMeasurements (){
		return measurementsList.size();
	}
	public Integer getListSizeValues (){
		return valuesList.size();
	}
	
	public Integer getListSizeSamples (){
		return samplesList.size();
	}
	
	public File getDir(){
		return tmpDir;
	}
	
	public GidsConvertor() throws Exception
	{
		logger = Logger.getLogger("Generic Convertor");
	}

	public Investigation makeInvestigation(String invName){
		Investigation newInvest = new Investigation();
		newInvest.setName(invName);
		newInvest.setName(invName);
		return newInvest;
	}
	
	public void populateIndividualAndSample(File file, String invName,final String individual, final String father, final String mother, final String sample) throws Exception
	{
		individualsList.clear();
		samplesList.clear();
		
		final List<String> namesSeen = new ArrayList<String>();
		this.invName = invName;
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				//Change id into the targetname/target id column
				String indName = tuple.getString(individual);
				
				// If individual not seen yet, create new
				if (!namesSeen.contains(indName)) {
					namesSeen.add(indName);
					Individual newIndividual = new Individual();
					newIndividual.setName(indName);
					newIndividual.setInvestigation_Name(getInvestigation());
					// Optionally
					String mother_Name = tuple.getString(mother);
					String father_Name = tuple.getString(father);
					newIndividual.setMother_Name(mother_Name);					
					newIndividual.setFather_Name(father_Name);	
					
					individualsList.add(newIndividual);
				}
				
				// Create new sample and link to individual
				String sampleName = tuple.getString(sample);

				if(sampleName!=null){
					GidsSample newSample = new GidsSample();
					newSample.setInvestigation_Name(getInvestigation());
					newSample.setName(sampleName);
					newSample.setIndividualID_Name(indName);
					// TODO: set sample values
					
					samplesList.add(newSample);
				}
			}
			
		});
	}
	
	public void populateMeasurement(File file, String invName,final String target, final String father, final String mother, final String sample, HashMap<String,String> hashChangeMeas) throws Exception {
		
		measurementsList.clear();
		totalMeasurementsList.clear();
		
		CsvFileReader reader = new CsvFileReader(file);

		for (String header : reader.colnames()) {

			//if (!header.equals(target) && !header.equals(mother) && !header.equals(father)&& !header.equals(sample)) {
				if(db.query(Measurement.class).eq(Measurement.NAME, header).count() == 0){
					
					/*If the measurement already exist in the data, but with a wrong header (e.g. birthdate in inputfile--> date of birth in db)*/
					if(hashChangeMeas.containsKey(header)){
						
						List<Measurement> measList = db.query(Measurement.class).eq(Measurement.NAME, hashChangeMeas.get(header)).find();
						int invID = db.query(Investigation.class).eq(Investigation.NAME, "Shared" ).find().get(0).getId();
						Measurement meas = measList.get(0);
						meas.setInvestigation_Id(invID);
						db.update(meas);
						totalMeasurementsList.add(meas);
					}
					else{
						Measurement measurement = new Measurement();
						measurement.setName(header);
						measurement.setInvestigation_Name(invName);
						measurementsList.add(measurement);
						totalMeasurementsList.add(measurement);
					}

				} else {			
					List<Measurement> measList = db.query(Measurement.class).eq(Measurement.NAME, header).find();
					int invID = db.query(Investigation.class).eq(Investigation.NAME, "Shared" ).find().get(0).getId();
					Measurement meas = measList.get(0);
					meas.setInvestigation_Id(invID);
					db.update(meas);
					totalMeasurementsList.add(meas);
				}
			//}		
		}
	}
	
	public String getInvestigation(){
		return invName;
	}
	
	public void populateValue(File file, String invName,final String individual, final String sample, final List<String> samplemeaslist) throws Exception
	{
		valuesList.clear();
		
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{			
				//Change targetname into the targetname/target id column
				String targetName = tuple.getString(individual);				
				String sampleName = tuple.getString(sample);
				
				
				if(sampleName!=null){
					for (Measurement m : totalMeasurementsList) {
						String featureName = m.getName();
						
						String value = tuple.getString(featureName);
						ObservedValue newValue = new ObservedValue();
						newValue.setFeature_Name(featureName);
						
						if(samplemeaslist.contains(featureName)){
							newValue.setTarget_Name(sampleName);
							
						}
						else{
							if(!alIndividuals.contains(featureName+targetName)){
								alIndividuals.add(featureName+targetName);
								newValue.setTarget_Name(targetName);
							}
							else{
								break;
							}
							
							
	
						}
						newValue.setValue(value);
						newValue.setInvestigation_Name(getInvestigation());					
						valuesList.add(newValue);	
					}
				}
			}
		});
		
	}
}
