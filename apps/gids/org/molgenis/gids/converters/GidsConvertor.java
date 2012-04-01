package org.molgenis.gids.converters;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.gids.GidsSample;
import org.molgenis.gids.converters.phenoModelconverterandloader.Conflicts;
import org.molgenis.gids.converters.phenoModelconverterandloader.PM_Updater;
import org.molgenis.gids.converters.phenoModelconverterandloader.PMconverterandloaderPlugin;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

import app.CsvExport;

public class GidsConvertor
{
	private Logger logger;
	final List<Individual> individualsList = new ArrayList<Individual>();
	final List<Measurement> measurementsList = new ArrayList<Measurement>();
	final List<Measurement> totalMeasurementsList = new ArrayList<Measurement>();
	final List<ObservedValue> valuesList = new ArrayList<ObservedValue>();
	final List<Investigation> investigationList = new ArrayList<Investigation>();
	final List<GidsSample> samplesList = new ArrayList<GidsSample>();
	final List<Measurement> indvList = new ArrayList<Measurement>();
	private String invName;
	private ArrayList<String> alIndividuals = new ArrayList<String>();
	private List<String> listKnownInd = new ArrayList<String>();
	private PM_Updater pm = new PM_Updater();
	private List<String> samplesListString = new ArrayList<String>();
	private HashMap<String, String> blackList = new HashMap<String, String>();
	private List<String> knownConflicts = new ArrayList<String>();
	private List<String> listTargets = new ArrayList<String>();
	private HashMap<String, String> hashSampleIndCombi = new HashMap<String, String>();

	public HashMap<String, String> getHashSampleIndCombi()
	{
		return hashSampleIndCombi;
	}

	public File tmpDir = null;
	List<Conflicts> listUpdate = new ArrayList<Conflicts>();
	Database db;

	public List<Conflicts> converter(File file, String invName, Database db, String individual, String father,
			String mother, String sample, List<String> samplemeaslist, List<String> indvmeaslist,
			HashMap<String, String> hashChangeMeas, PMconverterandloaderPlugin pmp, String checkNewData)
			throws Exception
	{

		this.invName = invName;
		this.db = db;
		makeInvestigation(invName);
		populateIndividualAndSample(file, invName, individual, father, mother, sample, pmp);
		populateMeasurement(file, invName, individual, father, mother, sample, hashChangeMeas);
		populateValue(file, invName, individual, sample, samplemeaslist, hashChangeMeas, invName, checkNewData);

		for (Entry<String, String> entry : blackList.entrySet())
		{
			String[] indmeas = entry.getKey().split("&");
			String[] dbfile = entry.getValue().split("&");
		}

		CsvExport export = new CsvExport();
		tmpDir = new File(System.getProperty("java.io.tmpdir"));

		logger.info("############   " + tmpDir.toString());
		File measurement = new File(tmpDir + File.separator + "measurement.txt");
		File individuals = new File(tmpDir + File.separator + "individual.txt");
		File gidssamples = new File(tmpDir + File.separator + "gidssample.txt");
		File values = new File(tmpDir + File.separator + "observedvalue.txt");
		boolean flag = false;

		deleteFile(flag, measurement);
		deleteFile(flag, individuals);
		deleteFile(flag, gidssamples);
		deleteFile(flag, values);

		try
		{
			export.exportAll(tmpDir, individualsList, samplesList, measurementsList, valuesList);
		}
		catch (Exception e)
		{
			logger.info("CANNOT EXPORT DATA");
		}
		return listUpdate;
	}

	public void populateIndividualAndSample(File file, String invName, final String individual, final String father,
			final String mother, final String sample, final PMconverterandloaderPlugin pmp) throws Exception
	{
		individualsList.clear();
		samplesList.clear();
		final List<String> namesSeen = new ArrayList<String>();
		this.invName = invName;
		CsvFileReader reader = new CsvFileReader(file);

		for (Tuple tuple : reader)
		{
			// Change id into the targetname/target id column
			String indName = tuple.getString(individual);
			if (db.query(Individual.class).eq(Individual.NAME, indName).count() == 0)
			{
				if (!namesSeen.contains(indName))
				{
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
			}

			// Create new sample and link to individual
			String sampleName = tuple.getString(sample);
			if (sampleName != null)
			{
				if (db.query(GidsSample.class).eq(GidsSample.NAME, sampleName).count() == 0)
				{
					GidsSample newSample = new GidsSample();
					newSample.setInvestigation_Name(getInvestigation());
					newSample.setName(sampleName);
					newSample.setIndividualID_Name(indName);
					samplesList.add(newSample);
				}
			}
			else
			{
				pmp.setError("A sample (with id_individual:" + indName + ") has no id_sample");

			}
		}
	}

	// public String getLatest() throws DatabaseException{
	// String unknown = "301-";
	// List<GidsSample> b = db.query(GidsSample.class).like(GidsSample.NAME,
	// unknown).find();
	// System.out.println("b.size " + b.get(0)+"\t"+b.get(1));
	// String nextSample = "301-0";
	// if(b.size()!=0){
	// String getLastSample = b.get(b.size()-1).getName();
	// System.out.println("getLastSample "+ getLastSample);
	// String []splitLastSample = getLastSample.split(unknown);
	// int nextSampleInt = (Integer.valueOf(splitLastSample[1]).intValue()+1);
	// System.out.println("nextSampleInt " +nextSampleInt);
	// nextSample = unknown+nextSampleInt;
	// System.out.println("nextSample " +nextSample);
	// }
	// return nextSample;
	//
	// }

	public boolean checkIndividualinDB(String indName, PM_Updater pm)
	{
		if (pm.getListIndividuals().contains(indName))
		{
			return true;
		}
		return false;
	}

	public void populateMeasurement(File file, String invName, final String target, final String father,
			final String mother, final String sample, HashMap<String, String> hashChangeMeas) throws Exception
	{

		measurementsList.clear();
		totalMeasurementsList.clear();

		CsvFileReader reader = new CsvFileReader(file);
		for (String header : reader.colnames())
		{
			if (db.query(Measurement.class).eq(Measurement.NAME, header).count() == 0)
			{
				/*
				 * If the measurement already exist in the data, but with a
				 * wrong header (e.g. birthdate in inputfile--> date of birth in
				 * db)
				 */
				if (hashChangeMeas.containsKey(header))
				{
					List<Measurement> measList = db.query(Measurement.class)
							.eq(Measurement.NAME, hashChangeMeas.get(header)).find();
					int invID = db.query(Investigation.class).eq(Investigation.NAME, "Shared").find().get(0).getId();
					Measurement meas = measList.get(0);
					meas.setInvestigation_Id(invID);
					db.update(meas);
					totalMeasurementsList.add(meas);
				}
				else
				{
					Measurement measurement = new Measurement();
					measurement.setName(header);
					measurement.setInvestigation_Name(invName);
					measurementsList.add(measurement);
					totalMeasurementsList.add(measurement);
				}

			}
			else
			{
				List<Measurement> measList = db.query(Measurement.class).eq(Measurement.NAME, header).find();
				int invID = db.query(Investigation.class).eq(Investigation.NAME, "Shared").find().get(0).getId();
				Measurement meas = measList.get(0);
				meas.setInvestigation_Id(invID);
				db.update(meas);
				totalMeasurementsList.add(meas);
			}
		}
	}

	public String getInvestigation()
	{
		return invName;
	}

	public void populateValue(File file, String invName, final String individual, final String sample,
			final List<String> samplemeaslist, final HashMap<String, String> hashChangeMeas,
			final String investigation, final String checkNewData) throws Exception
	{
		valuesList.clear();
		alIndividuals.clear();

		CsvFileReader reader = new CsvFileReader(file);

		int teller = 0;
		for (Tuple tuple : reader)
		{
			if (teller == 0)
			{
				pm = new PM_Updater();
				pm.makeTFVlists(db, investigation, sample);
				teller++;
			}
			// Change targetname into the targetname/target id column

			String indiName = tuple.getString(individual);
			String sampleName = "";

			if (tuple.getString(sample) == null)
			{
				sampleName = hashSampleIndCombi.get(indiName);
			}
			else
			{
				sampleName = tuple.getString(sample);
			}

			listTargets.add(indiName);
			listTargets.add(sampleName);
			HashMap<String, String> hashMeasFlipped = new HashMap<String, String>();
			// Flip the key and value and put them in the new hashmap
			for (Entry<String, String> entry : hashChangeMeas.entrySet())
			{
				hashMeasFlipped.put(entry.getValue(), entry.getKey());
			}
			/*
			 * If the measurement already exist in the data, but with a wrong
			 * header (e.g. birthdate in inputfile--> date of birth in db)
			 */
			for (Measurement m : totalMeasurementsList)
			{

				String featureName = m.getName();
				String value = "";
				if (hashMeasFlipped.containsKey(featureName))
				{
					value = tuple.getString(hashMeasFlipped.get(featureName));
				}
				else
				{
					value = tuple.getString(featureName);
				}

				// Check if the individual already exists in the database
				if (pm.getTargetWithMeasurement().containsKey(indiName))
				{
					if (checkNewData.equals("false"))
					{
						checkForupdates(indiName, featureName, value, sample);
					}
				}
				else if (pm.getTargetWithMeasurement().containsKey(sampleName))
				{
					if (checkNewData.equals("false"))
					{
						checkForupdates(sampleName, featureName, value, sample);
					}

				}

				else
				{

					ObservedValue newValue = new ObservedValue();
					newValue.setFeature_Name(featureName);

					// Check if combination Target+Feature already existed
					// in the !Samples list
					if (samplemeaslist.contains(featureName))
					{
						newValue.setTarget_Name(sampleName);
						newValue.setValue(value);
						newValue.setInvestigation_Name(getInvestigation());
						valuesList.add(newValue);
					}
					else
					{
						// Check if featureName + target already existed in
						// alIndviduals list
						if (!alIndividuals.contains(featureName + indiName))
						{
							newValue.setTarget_Name(indiName);
							alIndividuals.add(featureName + indiName);
						}
						else
						{
							continue;
						}
						newValue.setValue(value);
						newValue.setInvestigation_Name(getInvestigation());
						valuesList.add(newValue);
					}
				}
			}
		}
	}

	public void checkForupdates(String target, String featureName, String value, String sample)
			throws DatabaseException
	{
		// System.out.println("target featureName: "+ target + "\t"+featureName
		// );
		if (pm.getTargetWithMeasurement().get(target).contains(featureName))
		{
			if (pm.getHashIndFeaVal().get(target + "-" + featureName) != null)
			{ // There is something
				if (!pm.getHashIndFeaVal().get(target + "-" + featureName).equals(value))
				{
					Query<ObservedValue> testValue = db.query(ObservedValue.class);
					testValue.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, featureName));
					testValue.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, target));
					Integer idValue = testValue.find().get(0).getId();
					Conflicts con = new Conflicts();
					if (value != null)
					{
						if (!knownConflicts.contains(target + featureName))
						{
							if (!featureName.equals(sample))
							{
								con.setVariables(target, featureName, idValue,
										pm.getHashIndFeaVal().get(target + "-" + featureName), value);
								listUpdate.add(con);
								knownConflicts.add(target + featureName);
							}
						}
					}
				}
			}
			else
			{
				if (value != null)
				{
					// UPDATE DATABASE
					updateDB(db, value, featureName, target);
				}
			}
		}

	}

	public void updateDB(Database db, String value, String featureName, String target) throws DatabaseException
	{
		Query<ObservedValue> testValue = db.query(ObservedValue.class);
		testValue.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.EQUALS, featureName));
		testValue.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, target));
		ObservedValue newValue = testValue.find().get(0);
		newValue.getId();
		newValue.setValue(value);
		db.update(newValue);
		valuesList.add(newValue);
	}

	public Database getDb()
	{
		return db;
	}

	public Integer getListSizeTargets()
	{
		return individualsList.size();
	}

	public Integer getListSizeMeasurements()
	{
		return measurementsList.size();
	}

	public Integer getListSizeValues()
	{
		return valuesList.size();
	}

	public Integer getListSizeSamples()
	{
		return samplesList.size();
	}

	public File getDir()
	{
		return tmpDir;
	}

	public GidsConvertor() throws Exception
	{
		logger = Logger.getLogger("Generic Convertor");
	}

	public Investigation makeInvestigation(String invName)
	{
		Investigation newInvest = new Investigation();
		newInvest.setName(invName);
		return newInvest;
	}

	public void deleteFile(boolean y, File x)
	{
		if (x.exists())
		{
			y = x.delete();
			System.out.println("DELETED: " + x.toString());
		}
		else
		{

			logger.info(x.getName() + ".txt was not in de tmpdirectory");
		}
	}
}
