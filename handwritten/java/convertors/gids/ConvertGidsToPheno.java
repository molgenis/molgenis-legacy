package convertors.gids;

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

public class ConvertGidsToPheno
{
	private Logger logger;
	final List<Individual> individualsList  = new ArrayList<Individual>();
	final List<Measurement> measurementsList  = new ArrayList<Measurement>();
	final List<ObservedValue> valuesList  = new ArrayList<ObservedValue>();
	final List<Investigation> investigationList = new ArrayList<Investigation>();
	private String invName;
	Database db;
	
	
	public void converter(File file, String invName, Database db) throws Exception{
		
		this.invName = invName;
		this.db = db;
		
		makeInvestigation(invName);
		populateIndividual(file,invName);
		populateMeasurement(file,invName);
		populateValue(file,invName);
		
		CsvExport export = new CsvExport();
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File tmpFileDir = new File(tmpDir.getAbsolutePath());
		
		try{
			export.exportAll(tmpFileDir, individualsList, measurementsList, valuesList);
		}
		catch(Exception e){
			logger.info("CANNOT EXPORT DATA");
		}
	}

	
	
	public Database getDb() {
		return db;
	}



	public ConvertGidsToPheno() throws Exception
	{
		logger = Logger.getLogger("ConvertGidsToPheno");
	}

	public Investigation makeInvestigation(String invName){
		Investigation newInvest = new Investigation();
		newInvest.setName(invName);
		logger.info("#########################  makeInvestigation    " + invName );		
		newInvest.setName(invName);
		return newInvest;
	}
	
	public void populateIndividual(File file, String invName) throws Exception
	{
		final List<String> namesSeen = new ArrayList<String>();
		this.invName = invName;
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
			
				String gidsId = tuple.getString("id_individual");
				String mother_Name = tuple.getString("id_mother");
				String father_Name = tuple.getString("id_father");
				
				if (!namesSeen.contains(gidsId)) {
					namesSeen.add(gidsId);
					Individual newIndividual = new Individual();
					newIndividual.setName(gidsId);

					newIndividual.setInvestigation_Name(getInvestigation());
					newIndividual.setMother_Name(mother_Name);					
					newIndividual.setFather_Name(father_Name);	
					individualsList.add(newIndividual);
				}
			}
		});
	}
	
	public void populateMeasurement(File file, String invName) throws Exception {
		CsvFileReader reader = new CsvFileReader(file);
		int teller=1;
		for (String header : reader.colnames()) {
			if (!header.equals("id_individual") && !header.equals("id_mother") && !header.equals("id_father")) {
				if(db.query(Measurement.class).eq(Measurement.NAME, header).count() == 0){
					logger.info("THIS IS A NEW MEASUREMENT: " + header);
					Measurement measurement = new Measurement();
					measurement.setName(header);
					measurement.setInvestigation_Name(invName);
					measurementsList.add(measurement);
				} else {
					logger.info("THIS MEASUREMENT ALREADY EXISTED: " + header);
					List<Measurement> measList = db.query(Measurement.class).eq(Measurement.NAME, header).find();
					Measurement meas = measList.get(0);
					
					meas.setInvestigation_Name("System"); // todo make
					db.update(meas);
					
				}
			}
			teller++;
		}
	}
	
	public String getInvestigation(){
		return invName;
	}
	
	
	public void populateValue(File file, String invName) throws Exception
	{
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				
				String targetName = tuple.getString("id_individual");
				
				for (Measurement m : measurementsList) {
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
