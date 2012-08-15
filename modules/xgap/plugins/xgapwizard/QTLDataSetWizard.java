/*
 * Date: February 10, 2011 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.xgapwizard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixWriter;

import org.apache.commons.io.FileUtils;
import org.molgenis.cluster.DataName;
import org.molgenis.cluster.DataSet;
import org.molgenis.cluster.DataValue;
import org.molgenis.core.OntologyTerm;
import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Chromosome;
import org.molgenis.xgap.Marker;

public class QTLDataSetWizard extends PluginModel
{
	private static final long serialVersionUID = -1810993111211947419L;

	// if db rollbacks, delete this matrix file!
	private File dataFileRollback = null;
	
	private QTLDataSetWizardModel model = new QTLDataSetWizardModel();

	public QTLDataSetWizardModel getMyModel()
	{
		return model;
	}

	@Override
	public String getViewName()
	{
		return "plugins_xgapwizard_QTLDataSetWizard";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/xgapwizard/QTLDataSetWizard.ftl";
	}

	public QTLDataSetWizard(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	/**
	 * Handlerequest. Handles 4 inputs: select investigation, upload geno,
	 * upload pheno, and upload map. Calls helper functions accordingly.
	 */
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{

			if (request.getInt("invSelect") != null)
			{
				this.model.setSelectedInv(request.getInt("invSelect"));
			}

			String action = request.getString("__action");

			if (action.equals("uploadGeno"))
			{
				uploadData("Geno", request, db);

			}
			else if (action.equals("uploadPheno"))
			{
				uploadData("Pheno", request, db);

			}
			else if (action.equals("uploadMap"))
			{

				File mapFile = request.getFile("mapFile");

				if (mapFile == null)
				{
					throw new FileNotFoundException("No file selected");
				}

				parseMapAndAddToDb(mapFile, db, request.getInt("invSelect"));

				this.setMessages(new ScreenMessage(
						"Map file parsed and markers/chromosomes added to database",
						true));
			}
			else
			{
				throw new Exception("Unknown request action: " + action);
			}
		}
		catch (Exception e)
		{
			if (db.inTx())
			{
				try
				{
					// should never be null
					if (dataFileRollback != null)
					{
						// should exist
						if (dataFileRollback.exists())
						{
							FileUtils.forceDelete(dataFileRollback);
						}
					}
					db.rollbackTx();
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					this.setMessages(new ScreenMessage(e.getMessage() != null ? e1.getMessage() : "null", false));
				}
			}
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	/**
	 * Reload. Get a fresh list of investigation and print a message if this
	 * fails
	 */
	@Override
	public void reload(Database db)
	{
		try
		{
			List<Investigation> invList = db.find(Investigation.class);
			this.model.setInvestigations(invList);

			// FIXME: hardcoded for now, must be replaced with combination of
			// Metadb and enum in Data.. (pick out the ObservableFeatures)
			// if possible? may not have other required fields than Name..
			List<String> xof = new ArrayList<String>();
			// xof.add("Chromosome"); needs other required
			xof.add("Measurement");
			xof.add("DerivedTrait");
			xof.add("EnvironmentalFactor");
			xof.add("Gene");
			xof.add("Marker");
			xof.add("MassPeak");
			xof.add("Metabolite");
			xof.add("Probe");
			// xof.add("Spot"); needs other required
			this.model.setXqtlObservableFeatureTypes(xof);

			List<OntologyTerm> crosses = db.find(OntologyTerm.class,
					new QueryRule(OntologyTerm.NAME, Operator.LIKE,
							"xgap_rqtl_straintype_"));
			this.model.setCrosses(crosses);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e
					.getMessage() : "null", false));
		}

	}

	/**
	 * Helper funtion to upload either Genotypes or Phenotypes ('type' = "Geno"
	 * or "Pheno" to the database) from a file into a datamatrix
	 * 
	 * @param type
	 * @param request
	 * @param db
	 * @throws Exception
	 */
	private void uploadData(String type, Tuple request, Database db)
			throws Exception
	{
		File file = request.getFile(type + "File");

		if (file == null)
		{
			throw new FileNotFoundException("No file selected");
		}

		// make 'Data' set for this genotype matrix
		String originalFileName = request.getString(
				type + "FileOriginalfilename").substring(0,
				request.getString(type + "FileOriginalfilename").indexOf("."));
		int invSelect = request.getInt("invSelect");

		Data data = null;
		if (type.equals("Geno"))
		{
			data = makeGenoData(db, originalFileName, invSelect);
		}
		if (type.equals("Pheno"))
		{
			data = makePhenoData(db, originalFileName, invSelect);
		}

		db.beginTx();

		// add data definition
		db.add(data);

		// make binary matrix and upload into db
		new BinaryDataMatrixWriter(data, file, db);

		// tag matrix as Rqtl_data -> genotypes
		tagMatrix("Rqtl_data", type.toLowerCase() + "types", data, db);

		// create an instance of this matrix
		DataMatrixHandler handler = new DataMatrixHandler(db);
		DataMatrixInstance instance = handler.createInstance(data, db);
		dataFileRollback = handler.findSourceFile(data, db);

		if (type.equals("Geno"))
		{
			// add missing individuals with this cross type
			addMissingIndividuals(instance, db, request.getString("cross"),
					invSelect);
		}
		if (type.equals("Pheno"))
		{
			// add missing traits of this type
			String traitType = request.getString("trait");
			boolean traitsAdded = addMissingTraits(instance, db, traitType,
					invSelect);
			if (traitsAdded)
			{
				data.setTargetType(traitType);
				db.update(data);
			}
		}

		db.commitTx();
		dataFileRollback = null;

		this.setMessages(new ScreenMessage(type
				+ "types succesfully uploaded as '" + data.getName()
				+ "' and tagged for QTL analysis", true));
	}

	/**
	 * Add missing traits of this type if needed. Naive and straight
	 * implementation, similar to addMissingIndividuals
	 * 
	 * @param instance
	 * @param db
	 * @param type
	 * @param invId
	 * @throws DatabaseException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private boolean addMissingTraits(DataMatrixInstance instance, Database db,
			String type, int invId) throws DatabaseException,
			InstantiationException, IllegalAccessException
	{
		List<String> traitsNeeded = instance.getRowNames();
		Class traitClass = db.getClassForName(type);

		List<ObservableFeature> traitsInDb = db
				.find(ObservableFeature.class, new QueryRule(
						ObservableFeature.NAME, Operator.IN, traitsNeeded));

		for (ObservableFeature traitInDb : traitsInDb)
		{
			traitsNeeded.remove(traitsNeeded.indexOf(traitInDb.getName()));
		}

		List<ObservableFeature> addThese = new ArrayList<ObservableFeature>();

		for (String traitNeeded : traitsNeeded)
		{
			ObservableFeature missingTrait = (ObservableFeature) traitClass
					.newInstance();
			missingTrait.setInvestigation(invId);
			missingTrait.setName(traitNeeded);
			addThese.add(missingTrait);
		}
		db.add(addThese);

		if (addThese.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Add missing individuals of this cross type if needed. Naive: only looks
	 * for Individuals, not other ObservationTargets. Assumes the individuals
	 * are on the columns. Assumes your list of input Individuals has no
	 * duplicates, good names, etc. Assumes the cross exists as a uniquely named
	 * ontologyterm.
	 * 
	 * @param instance
	 * @param db
	 * @param cross
	 * @throws DatabaseException
	 */
	private void addMissingIndividuals(DataMatrixInstance instance,
			Database db, String cross, int invId) throws DatabaseException
	{
		List<String> indvNamesNeeded = instance.getColNames();
		List<Individual> indInDb = db.find(Individual.class, new QueryRule(
				Individual.NAME, Operator.IN, indvNamesNeeded));

		OntologyTerm crossType = db.find(OntologyTerm.class,
				new QueryRule(OntologyTerm.ID, Operator.EQUALS, cross)).get(0);

		for (Individual inDb : indInDb)
		{
			indvNamesNeeded.remove(indvNamesNeeded.indexOf(inDb.getName()));
		}

		List<Individual> addThese = new ArrayList<Individual>();
		for (String indName : indvNamesNeeded)
		{
			Individual addMe = new Individual();
			addMe.setName(indName);
			addMe.setInvestigation(invId);
			addMe.setOntologyReference(crossType);
			addThese.add(addMe);
		}
		db.add(addThese);
	}

	/**
	 * Helper function to parse a 'map' file and add the needed
	 * markers/chromosomes to the database
	 * 
	 * @param mapFile
	 * @param db
	 * @param invId
	 * @throws Exception
	 */
	private void parseMapAndAddToDb(File mapFile, Database db, int invId)
			throws Exception
	{
		// required columns that we expect in the file
		String[] required = new String[]
		{ "name", "chr", "cm" };

		// instantiate CsvFileReader to handle the file
		CsvFileReader csvFile = new CsvFileReader(mapFile);
		List<String> colNames = csvFile.colnames();

		// do checks if all column names are in order
		if (colNames.size() != required.length)
		{
			throw new Exception("Your files has " + colNames.size()
					+ " columns, this needs to be " + required.length);
		}
		for (String req : required)
		{
			if (!colNames.contains(req))
			{
				throw new Exception("Missing column: " + req);
			}
		}

		// get the values from the CSV
		final List<Tuple> values = new ArrayList<Tuple>();
		for (Tuple tuple : csvFile)
		{
			values.add(tuple);
		}

		// get the selected investigation
		Investigation inv = db.find(Investigation.class,
				new QueryRule("id", Operator.EQUALS, invId)).get(0);

		List<String> markersInDb = getMarkerNamesFromDb(db, inv.getId());
		HashMap<String, Chromosome> chromoInDb = getChromosomesFromDb(db,
				inv.getId());

		// iterate over tuples then convert to markers and chromosomes
		// needed for the import don't add markers that already exist!
		// (=cm info) but do map to existing chromosomes (=no further
		// info)
		List<Marker> markers = new ArrayList<Marker>();
		List<String> markerNames = new ArrayList<String>();
		// List<Chromosome> chromo = new ArrayList<Chromosome>();
		// List<String> chromoNames = new ArrayList<String>();

		HashMap<String, Chromosome> chromoToBeAdded = new HashMap<String, Chromosome>();
		for (Tuple t : values)
		{

			// get the values in their correct type
			String name = t.getString("name");
			int chr = t.getInt("chr");
			double cm = t.getDouble("cm");

			// CHROMOSOMES
			// get or make chromosome object
			Chromosome chromosome = null;
			String chromoName = "chr" + chr;
			if (chromoInDb.keySet().contains(chromoName))
			{
				// get existing chromosome, but check if the ordernumber
				// matches!
				chromosome = chromoInDb.get(chromoName);
				if (chromosome.getOrderNr().intValue() != chr)
				{
					throw new Exception(
							"Trying use existing chromosome 'chr"
									+ chr
									+ "' for input '"
									+ chr
									+ "', but the ordernumbers ("
									+ chromosome.getOrderNr().intValue()
									+ " vs. "
									+ chr
									+ ") are different! Change the annotation or upload an updated file.");
				}
			}
			else
			{
				// if not exist, make new one and add to list of names to
				// 'remember' and only add once to list of chromo needed in db
				if (!chromoToBeAdded.keySet().contains(chromoName))
				{
					chromosome = new Chromosome();
					chromosome.setName(chromoName);
					chromosome.setOrderNr(chr);
					chromosome.setIsAutosomal(true);
					chromosome.setInvestigation(inv);
					chromoToBeAdded.put(chromoName, chromosome);
					// add chromo to db right now, so the ID of the object is
					// set!
					db.add(chromosome);
				}
				else
				{
					// if we made one already, refer to this one
					chromosome = chromoToBeAdded.get(chromoName);
				}
			}

			// MARKERS
			// check for duplicates in the list
			if (markerNames.contains(name))
			{
				throw new Exception("Duplicate marker name '" + name
						+ "' in your map");
			}
			else
			{
				markerNames.add(name);
			}

			// check for duplicates in db
			if (markersInDb.contains(name))
			{
				throw new Exception("There is already a marker named '" + name
						+ "' present in this investigation");
			}

			// create new marker and add to list
			Marker m = new Marker();
			m.setInvestigation(inv);
			m.setName(name);
			m.setChromosome(chromosome);
			m.setCM(cm);

			// add to list
			markers.add(m);
		}

		// add markers to database
		db.add(markers);
	}

	/**
	 * Helper funtion to get a map of name-object for all Chromosomes for this
	 * investigation
	 * 
	 * @param db
	 * @param investigationId
	 * @return
	 * @throws DatabaseException
	 */
	private HashMap<String, Chromosome> getChromosomesFromDb(Database db,
			int investigationId) throws DatabaseException
	{
		QueryRule q = new QueryRule("investigation", Operator.EQUALS,
				investigationId);
		List<Chromosome> chromo = db.find(Chromosome.class, q);
		HashMap<String, Chromosome> chromoHash = new HashMap<String, Chromosome>();
		for (Chromosome c : chromo)
		{
			chromoHash.put(c.getName(), c);
		}
		return chromoHash;
	}

	/**
	 * Helper function to get the marker names from the database
	 * 
	 * @param db
	 * @param investigationId
	 * @return
	 * @throws DatabaseException
	 */
	private List<String> getMarkerNamesFromDb(Database db, int investigationId)
			throws DatabaseException
	{
		QueryRule q = new QueryRule("investigation", Operator.EQUALS,
				investigationId);
		List<Marker> markers = db.find(Marker.class, q);
		List<String> names = new ArrayList<String>();
		for (Marker m : markers)
		{
			names.add(m.getName());
		}
		return names;
	}

	/**
	 * Helper function to create a phenotype 'Data' set object
	 * 
	 * @param db
	 * @param originalFileName
	 * @param invSelect
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	private Data makePhenoData(Database db, String originalFileName,
			int invSelect) throws DatabaseException, IOException,
			ParseException
	{
		Data phenoData = new Data();
		OntologyTerm onto = db.find(
				OntologyTerm.class,
				new QueryRule(OntologyTerm.NAME, Operator.EQUALS,
						"phenotype_matrix")).get(0);
		if (onto == null)
		{
			onto = new OntologyTerm();
			onto.setName("phenotype_matrix");
			onto.setDefinition("Phenotypes");
			db.add(onto);
		}
		// phenoData.setFeature(feature);
		// phenoData.setTarget(feature);
		phenoData.setOntologyReference(onto);
		phenoData.setName(originalFileName);
		phenoData.setFeatureType("Individual");
		phenoData.setTargetType("ClassicalPhenotype");
		phenoData.setValueType("Decimal");
		phenoData.setStorage("Binary");
		Investigation inv = db.find(Investigation.class,
				new QueryRule("id", Operator.EQUALS, invSelect)).get(0);
		phenoData.setInvestigation(inv);
		phenoData.setInvestigation_Name(inv.getName());
		return phenoData;
	}

	/**
	 * Helper function to create a genotype 'Data' set object
	 * 
	 * @param db
	 * @param originalFileName
	 * @param invSelect
	 * @return
	 * @throws DatabaseException
	 * @throws IOException
	 * @throws ParseException
	 */
	private Data makeGenoData(Database db, String originalFileName,
			int invSelect) throws DatabaseException, IOException,
			ParseException
	{
		Data genoData = new Data();
		OntologyTerm onto = db.find(
				OntologyTerm.class,
				new QueryRule(OntologyTerm.NAME, Operator.EQUALS,
						"genotype_matrix")).get(0);
		if (onto == null)
		{
			onto = new OntologyTerm();
			onto.setName("genotype_matrix");
			onto.setDefinition("Genotypes");
			db.add(onto);
		}
		// genoData.setFeature(feature);
		// genoData.setTarget(feature);
		genoData.setOntologyReference(onto);
		genoData.setName(originalFileName);
		genoData.setFeatureType("Individual");
		genoData.setTargetType("Marker");
		genoData.setValueType("Text");
		genoData.setStorage("Binary");
		Investigation inv = Investigation.findById(db, invSelect);
		genoData.setInvestigation(inv);
		genoData.setInvestigation_Name(inv.getName());
		return genoData;
	}

	/**
	 * Helper funtion to tag an existing datamatrix with a specific dataValue,
	 * eg Rqtl genotypes
	 * 
	 * @param dataSet
	 * @param dataName
	 * @param dataValue
	 * @param db
	 * @throws DatabaseException
	 * @throws ParseException
	 * @throws IOException
	 */
	private void tagMatrix(String dataSet, String dataName, Data dataValue,
			Database db) throws DatabaseException, ParseException, IOException
	{
		List<DataSet> dsRefList = db.find(DataSet.class, new QueryRule("name",
				Operator.EQUALS, dataSet));
		DataSet dsRef = null;

		if (dsRefList.size() == 0)
		{
			dsRef = new DataSet();
			dsRef.setName(dataSet);
			db.add(dsRef);
		}
		else
		{
			dsRef = dsRefList.get(0);
		}

		Query<DataName> q = db.query(DataName.class);
		q.addRules(new QueryRule("name", Operator.EQUALS, dataName));
		q.addRules(new QueryRule("dataset", Operator.EQUALS, dsRef.getId()));

		List<DataName> dnRefList = q.find();
		DataName dnRef = null;

		if (dnRefList.size() == 0)
		{
			dnRef = new DataName();
			dnRef.setName(dataName);
			dnRef.setDataSet(dsRef);
			db.add(dnRef);
		}
		else
		{
			dnRef = dnRefList.get(0);
		}

		DataValue dv = new DataValue();
		dv.setDataName(dnRef);
		dv.setValue(dataValue);
		dv.setName(dataValue.getInvestigation_Name() + "_"
				+ dataValue.getName());
		db.add(dv);
	}
}
