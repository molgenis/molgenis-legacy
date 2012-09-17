package matrix.test.implementations.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import loaders.XgapLoadPanelTypes;
import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.memory.MemoryDataMatrixInstance;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.molgenis.core.MolgenisFile;
import org.molgenis.core.OntologyTerm;
import org.molgenis.data.Data;
import org.molgenis.data.DecimalDataElement;
import org.molgenis.data.TextDataElement;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Panel;
import org.molgenis.util.Entity;
import org.molgenis.util.TarGz;
import org.molgenis.xgap.DerivedTrait;
import org.molgenis.xgap.Marker;

import regressiontest.util.Util;
import decorators.NameConvention;

public class Helper
{
	private Database db = null;
	private List<String> uniqueNames = new ArrayList<String>();
	private Logger logger = Logger.getLogger(getClass().getSimpleName());

	private List<Data> dataList;
	private File inputFilesDir;

	public List<Data> getDataList()
	{
		return dataList;
	}

	public File getInputFilesDir()
	{
		return inputFilesDir;
	}

	public Helper(Database db)
	{
		this.db = db;
	}

	private Data getTestTextData(Investigation inv, DerivedTrait feature, Panel target, String rowType, String colType,
			String source)
	{
		Data d = new Data();
		d.setName("testTextData");
		d.setValueType("Text");
		d.setInvestigation(inv);
		d.setInvestigation_Name(inv.getName());
		d.setTargetType(rowType);
		d.setFeatureType(colType);
		d.setStorage(source);
//		d.setFeature(feature);
//		d.setTarget(target);
		return d;
	}

	public void prepareDatabaseAndFiles(String storage, Params params) throws DatabaseException, IOException, InterruptedException
	{
		// new emptyDatabase(db);
		db.remove(db.find(TextDataElement.class));
		db.remove(db.find(DecimalDataElement.class));
		db.remove(db.find(Marker.class));
		db.remove(db.find(Individual.class));
		db.remove(db.find(MolgenisFile.class));
		for(Data d : db.find(Data.class)){
			db.remove(d);
		}
		db.remove(db.find(DerivedTrait.class));
		db.remove(db.find(Panel.class));
		db.remove(db.find(Investigation.class));

		logger.info("Creating new investigation and adding it to database..");
		Investigation inv = getTestInvestigation();
		db.add(inv);

		logger.info("Creating randomized individuals and markers, and adding them to database..");
		List<Individual> indList = getRandomIndividuals(inv, params.matrixDimension1);
		List<Marker> marList = getRandomMarkers(inv, params.matrixDimension2);
		db.add(indList);
		db.add(marList);

		logger.info("Creating feature annotations for matrices");
		DerivedTrait textFeature = new DerivedTrait();
		textFeature.setName("test_text_data_feature");
		db.add(textFeature);
		DerivedTrait decimalFeature = new DerivedTrait();
		decimalFeature.setName("test_decimal_data_feature");
		db.add(decimalFeature);

		logger.info("Loading panel ontologies");
		new XgapLoadPanelTypes(db);
		OntologyTerm panelType = db.find(OntologyTerm.class, new QueryRule("definition", Operator.EQUALS, "other"))
				.get(0);

		logger.info("Creating panel as matrix 'target'");
		Panel p = new Panel();
		p.setName("panel_name");
		p.setPanelType(panelType);
		db.add(p);

		logger.info("Creating 'data'  objects and adding them to database..");
		List<Data> dataList = new ArrayList<Data>();
		dataList.add(getTestTextData(inv, textFeature, p, "Individual", "Marker", storage));
		dataList.add(getTestDecimalData(inv, decimalFeature, p, "Marker", "Individual", storage));
		db.add(dataList);

		this.dataList = dataList;

		logger.info("Creating or refreshing input directory to hold data matrices files..");
		File inputFilesDir = new File(System.getProperty("java.io.tmpdir") + File.separator
				+ NameConvention.escapeFileName(inv.getName()) + "_datamatrices");
		if (!inputFilesDir.exists())
		{
			inputFilesDir.mkdir();
		}
		else
		{
			FileUtils.cleanDirectory(inputFilesDir);
		}

		logger.info("Randomizing data matrix filling and adding data files to input directory..");
		for (Data data : dataList)
		{
			createAndWriteRandomMatrix(inputFilesDir, data, db, params.matrixDimension2, params.matrixDimension1, params.maxTextLength,
					params.sparse, params.fixedTextLength);
		}

		this.inputFilesDir = inputFilesDir;
	}

	private Data getTestDecimalData(Investigation inv, DerivedTrait feature, Panel target, String rowType,
			String colType, String source)
	{
		Data d = new Data();
		d.setName("testDecimalData");
		d.setValueType("Decimal");
		d.setInvestigation(inv);
		d.setInvestigation_Name(inv.getName());
		d.setTargetType(rowType);
		d.setFeatureType(colType);
		d.setStorage(source);
//		d.setFeature(feature);
//		d.setTarget(target);
		return d;
	}

	private Investigation getTestInvestigation()
	{
		Investigation inv = new Investigation();
		inv.setName("testStudy");
		inv.setStartDate(new Date());
		return inv;
	}

	public String printBinaryMatrixInfo(BinaryDataMatrixInstance bm)
	{
		String out = "";
		Data data = bm.getData();

		out += "matrix name = " + data.getName() + "\n";
		out += "investigation label = " + data.getInvestigation_Name() + "\n";
		out += "coltype = " + data.getFeatureType() + "\n";
		out += "rowtype = " + data.getTargetType() + "\n";
		out += "valuetype = " + data.getValueType() + "\n";

		out += "first three colnames:" + "\n";
		String colNames = "";
		for (int i = 0; i < bm.getColNames().size(); i++)
		{
			if (i < 3)
			{
				colNames += bm.getColNames().get(i) + " ";
			}
			else
			{
				break;
			}
		}
		out += colNames + "\n";

		out += "first three rownames:" + "\n";
		String rowNames = "";
		for (int i = 0; i < bm.getRowNames().size(); i++)
		{
			if (i < 3)
			{
				rowNames += bm.getRowNames().get(i) + " ";
			}
			else
			{
				break;
			}
		}
		out += rowNames;
		return out;
	}

	/**
	 * Returns random MemoryMatrix. NOTE: also creates a file, at location:
	 * inputMatrixDir.getAbsolutePath() + File.separator +
	 * NameConvention.escapeFileName(data.getName()) + ".txt"
	 * 
	 * @param data
	 * @param db
	 * @param totalRows
	 * @param totalCols
	 * @param maxStringLength
	 * @param sparse
	 * @param fixedTextLength
	 * @return
	 * @throws Exception 
	 */
	public MemoryDataMatrixInstance<Object> createAndWriteRandomMemoryMatrix(File inputMatrixDir, Data data,
			Database db, int totalRows, int totalCols, int maxStringLength, boolean sparse, boolean fixedTextLength)
			throws Exception
	{
		File res = new File(inputMatrixDir.getAbsolutePath() + File.separator
				+ NameConvention.escapeFileName(data.getName()) + ".txt");

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(res)));

		List<String> colNames = new ArrayList<String>();
		List<String> rowNames = new ArrayList<String>();

		Object[][] elements = null;

		List<? extends Entity> colList = db.find(db.getClassForName(data.getFeatureType()));
		List<? extends Entity> rowList = db.find(db.getClassForName(data.getTargetType()));

		for (Entity e : colList)
		{
			out.write("\t" + e.get("name").toString());
			colNames.add(e.get("name").toString());
		}

		out.write("\n");
		out.flush();

		for (Entity e : rowList)
		{
			rowNames.add(e.get("name").toString());
		}

		if (data.getValueType().equals("Decimal"))
		{
			//decimal data
			elements = new Object[totalRows][totalCols];
			for (int i = 0; i < totalRows; i++)
			{
				out.write(rowList.get(i).get("name").toString());
				for (int j = 0; j < totalCols; j++)
				{
					if (sparse)
					{
						if (Util.getRandomBoolean() == true)
						{
							double rand = Util.getRandomDouble();
							elements[i][j] = rand;
							out.write("\t" + rand);
						}
						else
						{
							elements[i][j] = null;
							out.write("\t");
						}
					}
					else
					{
						double rand = Util.getRandomDouble();
						elements[i][j] = rand;
						out.write("\t" + rand);
					}
				}
				out.write("\n");
				out.flush();
			}
		}
		else
		{
			// for text data, swap row with col dimension size
			elements = new Object[totalCols][totalRows];
			for (int i = 0; i < totalCols; i++)
			{
				out.write(rowList.get(i).get("name").toString());
				for (int j = 0; j < totalRows; j++)
				{
					if (sparse)
					{
						if (Util.getRandomBoolean() == true)
						{
							String rand = Util.getRandomString(maxStringLength, fixedTextLength);
							elements[i][j] = rand;
							out.write("\t" + rand);
						}
						else
						{
							elements[i][j] = null;
							out.write("\t");
						}
					}
					else
					{
						String rand = Util.getRandomString(maxStringLength, fixedTextLength);
						elements[i][j] = rand;
						out.write("\t" + rand);
					}
				}
				out.write("\n");
				out.flush();
			}
		}

		out.close();

		MemoryDataMatrixInstance<Object> mm = new MemoryDataMatrixInstance<Object>(rowNames, colNames, elements, data);

		return mm;
	}

	private File createAndWriteRandomMatrix(File inputMatrixDir, Data data, Database db, int totalRows, int totalCols,
			int maxStringLength, boolean sparse, boolean fixedTextLength) throws IOException, DatabaseException
	{
		File res = new File(inputMatrixDir.getAbsolutePath() + File.separator
				+ NameConvention.escapeFileName(data.getName()) + ".txt");

		List<? extends Entity> colList = db.find(db.getClassForName(data.getFeatureType()));
		List<? extends Entity> rowList = db.find(db.getClassForName(data.getTargetType()));

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(res)));

		for (Entity e : colList)
		{
			out.write("\t" + e.get("name").toString());
		}
		out.write("\n");
		out.flush();

		if (data.getValueType().equals("Decimal"))
		{
			//decimal data
			for (int i = 0; i < totalRows; i++)
			{
				out.write(rowList.get(i).get("name").toString());
				for (int j = 0; j < totalCols; j++)
				{
					if (sparse)
					{
						if (Util.getRandomBoolean() == true)
						{
							out.write("\t" + Util.getRandomDouble());
						}
						else
						{
							out.write("\t");
						}
					}
					else
					{
						out.write("\t" + Util.getRandomDouble());
					}
				}
				out.write("\n");
				out.flush();
			}
		}
		else
		{
			// for text data, swap row with col dimension size
			for (int i = 0; i < totalCols; i++)
			{
				out.write(rowList.get(i).get("name").toString());
				for (int j = 0; j < totalRows; j++)
				{
					if (sparse)
					{
						if (Util.getRandomBoolean() == true)
						{
							out.write("\t" + Util.getRandomString(maxStringLength, fixedTextLength));
						}
						else
						{
							out.write("\t");
						}
					}
					else
					{
						out.write("\t" + Util.getRandomString(maxStringLength, fixedTextLength));
					}
				}
				out.write("\n");
				out.flush();
			}
		}

		out.close();

		return res;
	}

	public void printSettings(String source, Params params)
	{
		System.out.println("##################################################");
		System.out.println("## Test" + source + "Matrix" + "\tstarting with settings: ##");
		System.out.println("##################################################");
		System.out.println("matrixDimension1 <- " + params.matrixDimension1);
		System.out.println("matrixDimension2 <- " + params.matrixDimension2);
		System.out.println("maxTextLength <- " + params.maxTextLength);
		System.out.println("fixedTextLength <- " + Boolean.toString(params.fixedTextLength).toUpperCase());
		System.out.println("sparse <- " + Boolean.toString(params.sparse).toUpperCase());
		System.out.println("skipPerElement <- " + Boolean.toString(params.skipPerElement).toUpperCase());
	}

	private List<Individual> getRandomIndividuals(Investigation inv, int amount)
	{
		List<Individual> indList = new ArrayList<Individual>();
		for (int i = 0; i < amount; i++)
		{
			String name = Util.getRandomString(10, false);
			if (uniqueNames.contains(name))
			{
				amount++;
			}
			else
			{
				uniqueNames.add(name);
				Individual ind = new Individual();
				ind.setName(name);
				ind.setInvestigation(inv);
				indList.add(ind);
			}
		}
		return indList;
	}

	private List<Marker> getRandomMarkers(Investigation inv, int amount)
	{
		List<Marker> marList = new ArrayList<Marker>();
		for (int i = 0; i < amount; i++)
		{
			String name = Util.getRandomString(10, false);
			if (uniqueNames.contains(name))
			{
				amount++;
			}
			else
			{
				uniqueNames.add(name);
				Marker mar = new Marker();
				mar.setName(name);
				mar.setInvestigation(inv);
				marList.add(mar);
			}
		}
		return marList;
	}

	public static String readFileToString(File file) throws FileNotFoundException, Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		String res = "";
		String line;
		while ((line = br.readLine()) != null){
			res += line+"\n";
		}
		return res;
	}
}
