package convertors;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import app.CsvExport;

/**
 * Class that reads in any csv data file with observable features (measurements) in the columns,
 * observation targets (individuals) in the rows, and observed values in the 'cells'.
 * The first column is supposed to contain the targets' identifiers.
 * The data is put into 4 lists: investigations, measurements, individuals and values.
 * These lists are then written to txt files that conform to the Pheno model and can be
 * imported using the ExcelImporter.
 * 
 * @author Roan Kanninga, erikroos
 *
 */
public class GenericConvertor
{
	private Logger logger;
	static final List<Individual> individualsList  = new ArrayList<Individual>();
	static final List<Measurement> measurementsList  = new ArrayList<Measurement>();
	static final List<ObservedValue> valuesList  = new ArrayList<ObservedValue>();
	static final List<Investigation> investigationList = new ArrayList<Investigation>();
	
	// Put the path to the export file of your original data here:
	private static String filename = "/Users/roankanninga/Documents/NewMolgenis/molgenis_apps/handwritten/java/convertors/gids/export_CeliacSprue_for_PhenoModel.csv";
	// Put the names of your investigation(s) here:
	private String [] listOfInvestigationNames = {"CeliacSprue", "PreventCD", "IBD", "COPD", "GODDAF", "SLE"};
	// Put the header of the column with the individuals' identifiers here:
	private String indColumn = "id_individual";
	
	public static void main(String[] args) throws Exception
	{
		GenericConvertor conv = new GenericConvertor();
		conv.populateInvestigations();
		conv.populateIndividual(filename);
		conv.populateMeasurement(filename);
		conv.populateValue(filename);
		
		CsvExport export = new CsvExport();
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		export.exportAll(tmpDir, individualsList, measurementsList, valuesList, investigationList);
	}

	public GenericConvertor() throws Exception
	{
		logger = Logger.getLogger("GenericConvertor");
	}

	public void populateInvestigations(){
		
		for(String invName: listOfInvestigationNames){
			investigationList.add(makeInvestigation(invName));
		}
		
	}
	public Investigation makeInvestigation(String invName){
		Investigation newInvest = new Investigation();
		newInvest.setName(invName);
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
				logger.info("Parsing line: " + line_number);
				
				String gidsId = tuple.getString(indColumn);
				
				if (!namesSeen.contains(gidsId)) {
					namesSeen.add(gidsId);
					Individual newIndividual = new Individual();
					newIndividual.setName(gidsId);
					newIndividual.setInvestigation_Name(listOfInvestigationNames[0]);
					individualsList.add(newIndividual);
				}
			}
		});
	}
	
	public void populateMeasurement(String filename) throws Exception {
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		for (String header : reader.colnames()) {
			if (!header.equals(indColumn)) {
				Measurement measurement = new Measurement();
				measurement.setName(header);
				measurement.setInvestigation_Name(listOfInvestigationNames[0]);
				measurementsList.add(measurement);
			}
		}
	}
	
	public void populateValue(String filename) throws Exception
	{
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws DatabaseException, ParseException, IOException
			{
				logger.info("Parsing line: " + line_number);
				
				String targetName = tuple.getString(indColumn);
				
				for (Measurement m : measurementsList) {
					String featureName = m.getName();	
					String value = tuple.getString(featureName);
					
					ObservedValue newValue = new ObservedValue();
					newValue.setFeature_Name(featureName);
					newValue.setTarget_Name(targetName);
					newValue.setValue(value);
					newValue.setInvestigation_Name(listOfInvestigationNames[0]);
					
					valuesList.add(newValue);
				}
			}
		});
	}
}
