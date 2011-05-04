package convertors.gids;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.DatabaseException;
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
	static final List<Individual> individualsList  = new ArrayList<Individual>();
	static final List<Measurement> measurementsList  = new ArrayList<Measurement>();
	static final List<ObservedValue> valuesList  = new ArrayList<ObservedValue>();
	
	public static void main(String[] args) throws Exception
	{
		String filename = "C:/Documents and Settings/Administrator/workspace/molgenis_apps/handwritten/java/convertors/gids/export_CeliacSprue_for_PhenoModel.csv";
		
		ConvertGidsToPheno conv = new ConvertGidsToPheno();
		conv.populateIndividual(filename);
		conv.populateMeasurement(filename);
		conv.populateValue(filename);
		
		CsvExport export = new CsvExport();
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		export.exportAll(tmpDir, individualsList, measurementsList, valuesList);
	}

	public ConvertGidsToPheno() throws Exception
	{
		logger = Logger.getLogger("ConvertGidsToPheno");
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
				
				String gidsId = tuple.getString("id_individual");
				
				if (!namesSeen.contains(gidsId)) {
					namesSeen.add(gidsId);
					Individual newIndividual = new Individual();
					newIndividual.setName(gidsId);
					individualsList.add(newIndividual);
				}
			}
		});
	}
	
	public void populateMeasurement(String filename) throws Exception {
		File file = new File(filename);
		CsvFileReader reader = new CsvFileReader(file);
		for (String header : reader.colnames()) {
			if (!header.equals("id_individual")) {
				Measurement measurement = new Measurement();
				measurement.setName(header);
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
				
				String targetName = tuple.getString("id_individual");
				
				for (Measurement m : measurementsList) {
					String featureName = m.getName();	
					String value = tuple.getString(featureName);
					
					ObservedValue newValue = new ObservedValue();
					newValue.setFeature_Name(featureName);
					newValue.setTarget_Name(targetName);
					newValue.setValue(value);
					
					valuesList.add(newValue);
				}
			}
		});
	}
}
