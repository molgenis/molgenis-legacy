package convertors;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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

public class GenericConvertor
{
	private Logger logger;
	final List<Individual> individualsList  = new ArrayList<Individual>();
	final List<Measurement> measurementsList  = new ArrayList<Measurement>();
	final List<Measurement> totalMeasurementsList  = new ArrayList<Measurement>();
	final List<ObservedValue> valuesList  = new ArrayList<ObservedValue>();
	final List<Investigation> investigationList = new ArrayList<Investigation>();
	private String invName;
	
	public File tmpDir = null;
	
	Database db;

	public void converter(File file, String invName, Database db) throws Exception{
		
		this.invName = invName;
		this.db = db;
		makeInvestigation(invName);
		populateIndividual(file,invName);
		populateMeasurement(file,invName);
		populateValue(file,invName);
		
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
			export.exportAll(tmpDir, individualsList, measurementsList, valuesList);
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
	
	public File getDir(){
		return tmpDir;
	}
	
	public GenericConvertor() throws Exception
	{
		logger = Logger.getLogger("Generic Convertor");
	}

	public Investigation makeInvestigation(String invName){
		Investigation newInvest = new Investigation();
		newInvest.setName(invName);
		newInvest.setName(invName);
		return newInvest;
	}
	
	public void populateIndividual(File file, String invName) throws Exception
	{
		individualsList.clear();
		
		final List<String> namesSeen = new ArrayList<String>();
		this.invName = invName;
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				//Change id into the targetname/target id column
				String id = tuple.getString("id_individual");
				
				// Optionally
				String mother_Name = tuple.getString("id_mother");
				String father_Name = tuple.getString("id_father");
								
				if (!namesSeen.contains(id)) {
					namesSeen.add(id);
					Individual newIndividual = new Individual();
					newIndividual.setName(id);
					newIndividual.setInvestigation_Name(getInvestigation());
					// Optionally
					newIndividual.setMother_Name(mother_Name);					
					newIndividual.setFather_Name(father_Name);	
					
					individualsList.add(newIndividual);
				}
			}
		});
	}
	
	public void populateMeasurement(File file, String invName) throws Exception {
		
		measurementsList.clear();
		totalMeasurementsList.clear();
		
		CsvFileReader reader = new CsvFileReader(file);

		for (String header : reader.colnames()) {
			//optionally
			//if (!header.equals("id_individual")) {
			if (!header.equals("id_individual") && !header.equals("id_mother") && !header.equals("id_father")) {
				if(db.query(Measurement.class).eq(Measurement.NAME, header).count() == 0){
					Measurement measurement = new Measurement();
					measurement.setName(header);
					measurement.setInvestigation_Name(invName);
					measurementsList.add(measurement);
					totalMeasurementsList.add(measurement);

				} else {			
					List<Measurement> measList = db.query(Measurement.class).eq(Measurement.NAME, header).find();
					int invID = db.query(Investigation.class).eq(Investigation.NAME, "System" ).find().get(0).getId();
					Measurement meas = measList.get(0);
					meas.setInvestigation_Id(invID);
					db.update(meas);
					totalMeasurementsList.add(meas);

				}
			}		
			
		}
	}
	
	public String getInvestigation(){
		return invName;
	}
	
	
	public void populateValue(File file, String invName) throws Exception
	{
		valuesList.clear();
		
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{			
				//Change targetname into the targetname/target id column
				String targetName = tuple.getString("id_individual");				
				for (Measurement m : totalMeasurementsList) {
					String featureName = m.getName();	
					String value = tuple.getString(featureName);
					ObservedValue newValue = new ObservedValue();
					newValue.setFeature_Name(featureName);
					newValue.setTarget_Name(targetName);
					newValue.setValue(value);
					newValue.setInvestigation_Name(getInvestigation());					
					valuesList.add(newValue);					
				}
			}
		});
		
	}
}
