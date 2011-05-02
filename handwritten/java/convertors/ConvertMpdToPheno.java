package convertors;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Tuple;

import app.CsvExport;

/**
 * Convertor to convert standard MPD (mouse phenotype database) downloads to
 * pheno model. Each file type has its own loader, all in memory. After that you
 * can choose to write to file or write to database.
 */
public class ConvertMpdToPheno
{
	// required
	static String importDir = "../molgenis4phenotype/data/MPD/orig";

	// if you want to write to files
	static String outputDir = "../molgenis4phenotype/data/MPD/output";

	public static void main(String[] args) throws Exception
	{
		ConvertMpdToPheno conv = new ConvertMpdToPheno();
		
		conv.loadProjects();
		conv.loadStrains();
		conv.loadAssayStats();
		conv.loadMeasurements();
		conv.loadAnimalDataPoints();
		
		CsvExport export = new CsvExport();
		export.exportAll(new File(outputDir), projects, strains, animals, measurements, values);
		
		//CsvImport importer = new CsvImport();
		//Database db = null;
		//importer.importAll(db,projects, strains);
	}

	// containers for the mpd data
	static List<Investigation> projects = new ArrayList<Investigation>();
	static List<Panel> strains = new ArrayList<Panel>();
	static List<Individual> animals = new ArrayList<Individual>();
	static List<Measurement> measurements = new ArrayList<Measurement>();
	static List<ObservedValue> values = new ArrayList<ObservedValue>();

	// mpd data provides measnum to link values to the feature.
	Map<String, String> measNumToName = new LinkedHashMap<String, String>();

	/**
	 * Load projects.txt This is a flat listing of investigation.name and
	 * investigation.description.
	 * 
	 * @throws Exception
	 */
	public void loadProjects() throws Exception
	{
		CsvReader reader = new CsvFileReader(new File(importDir
				+ "/projects.txt"));
		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				Investigation project = new Investigation();
				project.setName(tuple.getString("name"));
				project.setDescription(tuple.getString("description"));
				projects.add(project);
			}
		});
	}

	/**
	 * Load measurements.txt
	 * 
	 * This file lists all measurements.name, unit and a serie of custom fields"measnum	projsym	displayorder	varname	desc	units	inseries	protolink	p1	cat1	cat2	cat3	p2	hints	intervention	intparm	appmeth	panelsym	p3	datatype	origin	sextested	nstrainstested	ageweeks"
	 * @throws Exception 
	 */
	public void loadMeasurements() throws Exception
	{
		CsvReader reader = new CsvFileReader(new File(importDir
				+ "/measurements.txt"));
		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				Measurement m = new Measurement();
				m.setInvestigation_Name("projsym");
				m.setName(tuple.getString("name"));
				m.setUnit_Name("units");
				m.setDescription(tuple.getString("desc"));
				measurements.add(m);
			}
		});
	}

	/**
	 * Load animaldatapoints.txt. These are observedvalue per individual plus
	 * sex
	 */
	public void loadAnimalDataPoints() throws Exception
	{
		CsvReader reader = new CsvFileReader(new File(importDir
				+ "/animaldatapoints.txt"));
		addObservableFeatures("sex");

		// sex is repeated for each measurement
		final List<String> hasSexValue = new ArrayList<String>();

		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				// sex, should be only once???
				if (!hasSexValue.contains(tuple.getString("animal_id")))
				{
					ObservedValue v = new ObservedValue();
					v.setTarget_Name(tuple.getString("animal_id"));
					// map measnum to meas.name
					v.setFeature_Name("sex");
					v.setValue(tuple.getString("sex"));

					values.add(v);
					hasSexValue.add(tuple.getString("animal_id"));
				}

				// value
				ObservedValue v = new ObservedValue();
				v.setTarget_Name(tuple.getString("animal_id"));
				// map measnum to meas.name
				v
						.setFeature_Name(measNumToName.get(tuple
								.getString("measnum")));
				v.setValue(tuple.getString("value"));
			}
		});
	}

	/**
	 * Load strains.txt. This file has per line panel.name plus some
	 * observedvalue per panel ("longname", "mpd_id", "vendor", "stocknum",
	 * "prigroup", "typecode", "genaltcode", "ndatasets")
	 * 
	 * @throws Exception
	 */
	public void loadStrains() throws Exception
	{
		// includes a few ObservableFeatures;
		final String[] varNames = new String[]
		{ "longname", "mpd_id", "vendor", "stocknum", "prigroup", "typecode",
				"genaltcode", "ndatasets" };

		addObservableFeatures(varNames);

		CsvReader reader = new CsvFileReader(new File(importDir
				+ "/strains.txt"));
		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				Panel strain = new Panel();
				strain.setName(tuple.getString("strainname"));
				strains.add(strain);
				addObservedValuesForTarget(strain, varNames, tuple);
			}
		});
	}

	/**
	 * Loads assaystats.txt.
	 * 
	 * Grouped by (sex=m, sex=f) it lists a matrix of feature * feature. For
	 * example '{ahtracis in m} X {nstrains,mean,median,sd,se,cv,min,max}'.
	 * 
	 * Alternatively, we could make this a ObservedInference.
	 * 
	 * @throws Exception
	 */
	public void loadAssayStats() throws Exception
	{
		CsvReader reader = new CsvFileReader(new File(importDir
				+ "/assaystats.txt"));

		// get the features
		List<String> temp = reader.colnames();
		temp.remove("measnum");
		temp.remove("varname");
		final String[] features = temp.toArray(new String[temp.size()]);

		addObservableFeatures(features);

		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int lineNumber, Tuple tuple)
					throws Exception
			{
				Panel strain = new Panel();
				strain.setName(tuple.getString("strainname"));
				strains.add(strain);
				addObservedValuesForTarget(strain, features, tuple);
			}
		});
	}

	/*
	 * Helper method for loading observed values per target from a matrix like
	 * tuple
	 */
	private void addObservedValuesForTarget(Panel strain, String[] varNames,
			Tuple tuple)
	{
		for (String name : varNames)
		{
			ObservedValue v = new ObservedValue();
			v.setTarget(strain);
			v.setFeature_Name(name);
			v.setValue(tuple.getString(name));
			values.add(v);
		}
		
		
	}

	/*
	 * Helper method for loading a set of features.
	 */
	
	
	private void addObservableFeatures(String... featureNames)
	{
		// NB if Java had named parameters this code would not be needed.
		// Idea: make possible to say Observeable f =
		// ObserveableFeature.make().name("x");

		for (String name : featureNames)
		{
			Measurement f = new Measurement();
			f.setName(name);
			measurements.add(f);
		}
	}

}
