package org.molgenis.hemodb.plugins;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.hemodb.HemoProbe;
import org.molgenis.hemodb.plugins.QuestionsModel.QuestionState;
import org.molgenis.util.EmailService;
import org.molgenis.util.RScript;

import app.DatabaseFactory;

public class CalculateMedianR implements Runnable
{

	private String geneExp;
	private List<String> sampleNamesGroup1;
	private List<String> sampleNamesGroup2;
	private double signifCutoff;
	private List<String> allProbes;
	private EmailService es;
	private String emailAddress;
	private QuestionsModel questionsModel;
	private List<String> groups;

	public CalculateMedianR(QuestionsModel questionsModel, String geneExp, List<String> sampleNamesGroup1,
			List<String> sampleNamesGroup2, List<String> groups, double signifCutoff, List<String> allProbes,
			EmailService es, String emailAdress)
	{
		super();
		this.geneExp = geneExp;
		this.sampleNamesGroup1 = sampleNamesGroup1;
		this.sampleNamesGroup2 = sampleNamesGroup2;
		this.signifCutoff = signifCutoff;
		this.allProbes = allProbes;
		this.es = es;
		this.emailAddress = emailAdress;
		this.questionsModel = questionsModel;
		this.groups = groups;

		Thread t = new Thread(this);
		t.start();
	}

	public void calculateMedian() throws Exception
	{

		// db connection OUTSIDE frontcontroller: evil but needed because we are
		// in a thread.... :(
		Database db = DatabaseFactory.create();

		List<String> allSampleNames = new ArrayList<String>();
		allSampleNames.addAll(sampleNamesGroup1);
		allSampleNames.addAll(sampleNamesGroup2);

		// get data set
		Data dataSet = db.query(Data.class).eq(Data.NAME, geneExp).find().get(0);

		// load matrix
		DataMatrixHandler dmh = new DataMatrixHandler(db);
		DataMatrixInstance instance = (BinaryDataMatrixInstance) dmh.createInstance(dataSet, db);

		// slice part out of dataset
		DataMatrixInstance group1Selection = instance.getSubMatrix(allProbes, sampleNamesGroup1);
		DataMatrixInstance group2Selection = instance.getSubMatrix(allProbes, sampleNamesGroup2);

		File group1SelectionFile = new File(System.getProperty("java.io.tmpdir") + File.separator
				+ "group1Selection_data_" + System.nanoTime() + ".txt");
		File group2SelectionFile = new File(System.getProperty("java.io.tmpdir") + File.separator
				+ "group2Selection_data_" + System.nanoTime() + ".txt");

		File proberesults = new File(System.getProperty("java.io.tmpdir") + File.separator + "proberesults_"
				+ System.nanoTime() + ".txt");

		PrintStream p1 = new PrintStream(new BufferedOutputStream(new FileOutputStream(group1SelectionFile)), false,
				"UTF8");
		group1Selection.toPrintStream(p1);

		PrintStream p2 = new PrintStream(new BufferedOutputStream(new FileOutputStream(group2SelectionFile)), false,
				"UTF8");
		group2Selection.toPrintStream(p2);

		// create RScript
		RScript script = new RScript();

		// execute
		script.append("compareExpression <- function(data, col1, col2){compare <- data[,col1]-data[,col2]");
		script.append("return(compare)}");

		script.append("geneExpressionDataSetGroupOne <- read.table(\"" + group1SelectionFile.getAbsolutePath()
				+ "\",sep=\"\\t\",header=T,row.names=1,colClasses=c(\"character\"),check.names=FALSE)");
		script.append("geneExpressionDataSetGroupOne <- as.matrix(geneExpressionDataSetGroupOne)");
		script.append("colnames <- colnames(geneExpressionDataSetGroupOne)");
		script.append("rownames <- rownames(geneExpressionDataSetGroupOne)");
		script.append("geneExpressionDataSetGroupOne <- matrix(as.numeric(as.matrix(geneExpressionDataSetGroupOne)),c(dim(geneExpressionDataSetGroupOne)[1],dim(geneExpressionDataSetGroupOne)[2]))");
		script.append("colnames(geneExpressionDataSetGroupOne) <- colnames");
		script.append("rownames(geneExpressionDataSetGroupOne) <- rownames");

		script.append("geneExpressionDataSetGroupTwo <- read.table(\"" + group2SelectionFile.getAbsolutePath()
				+ "\",sep=\"\\t\",header=T,row.names=1,colClasses=c(\"character\"),check.names=FALSE)");
		script.append("geneExpressionDataSetGroupTwo <- as.matrix(geneExpressionDataSetGroupTwo)");
		script.append("colnames <- colnames(geneExpressionDataSetGroupTwo)");
		script.append("rownames <- rownames(geneExpressionDataSetGroupTwo)");
		script.append("geneExpressionDataSetGroupTwo <- matrix(as.numeric(as.matrix(geneExpressionDataSetGroupTwo)),c(dim(geneExpressionDataSetGroupTwo)[1],dim(geneExpressionDataSetGroupTwo)[2]))");
		script.append("colnames(geneExpressionDataSetGroupTwo) <- colnames");
		script.append("rownames(geneExpressionDataSetGroupTwo) <- rownames");

		script.append("rowMediansGroupOne <- as.matrix(apply(geneExpressionDataSetGroupOne,1,median))");
		script.append("rowMediansGroupTwo <- as.matrix(apply(geneExpressionDataSetGroupTwo,1,median))");

		script.append("medianDataSet <- cbind(rowMediansGroupOne,rowMediansGroupTwo)");
		script.append("rownames(medianDataSet) <- row.names(geneExpressionDataSetGroupOne)");

		script.append("geneExpressionTest <- as.matrix(compareExpression(medianDataSet,1,2))");
		script.append("rownames(geneExpressionTest) <- row.names(geneExpressionDataSetGroupOne)");

		script.append("significance <- geneExpressionTest >=" + signifCutoff + " | geneExpressionTest <= -"
				+ signifCutoff);
		script.append("significantProbes <- as.matrix(geneExpressionTest[significance])");

		script.append("index <- which(significance[,1]==TRUE)");
		script.append("result <- significance[index,]");

		script.append("probeNames <- names(result)");
		script.append("write.table(probeNames, file=\"" + proberesults.getAbsolutePath()
				+ "\", sep=\"\\t\", quote=FALSE, row.names=FALSE, col.names=FALSE)");
		script.execute();
		System.out.println("script executed");

		Scanner scanner = new Scanner(proberesults);
		ArrayList<String> probes = new ArrayList<String>();

		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine();
			probes.add(line);
		}
		List<String> genes = selectGenesWithProbes(db, probes);
		System.out.println("genes are: " + genes);

		StringBuilder sb = new StringBuilder();
		sb.append("This is the data you selected for the analysis: " + "\n");
		sb.append("The data matrix selected: " + geneExp + "\n");
		sb.append("Group one: " + groups.get(0) + "\n");
		sb.append("Samples in this group: " + sampleNamesGroup1 + "\n");
		sb.append("Group two: " + groups.get(1) + "\n");
		sb.append("Samples in this group: " + sampleNamesGroup2 + "\n");
		sb.append("Significance cutoff: " + signifCutoff + "\n");
		String results = sb.toString();

		StringBuilder geneList = new StringBuilder();
		geneList.append(genes);
		String genesToString = geneList.toString();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
		es.email("Analysis results: " + dateFormat.format(date), results, emailAddress, genesToString, outputStream,
				true, "Human leukemia database mail");
	}

	@Override
	public void run()
	{
		try
		{
			calculateMedian();
			questionsModel.setState(QuestionState.QUESTION2_RESULT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public List<String> selectGenesWithProbes(Database db, ArrayList<String> probes) throws DatabaseException
	{
		/**
		 * selectGenesWithProbes gets a String with (Hemo)probe names, converts
		 * those to (Hemo)genes (by querying the database) and returns the genes
		 */
		try
		{
			List<String> chompedProbes = new ArrayList<String>();
			for (String probe : probes)
			{
				chompedProbes.add(probe.trim());
			}

			List<HemoProbe> genesForProbe = db.find(HemoProbe.class, new QueryRule(HemoProbe.NAME, Operator.IN,
					chompedProbes));
			List<String> genes = new ArrayList<String>();
			for (HemoProbe gfp : genesForProbe)
			{
				if (!genes.contains(gfp.getReportsFor_Name()))
				{
					genes.add(gfp.getReportsFor_Name());
				}
			}

			if (!genes.isEmpty())
			{
				// System.out.println("gene found with this probe: " + genes);
				return genes;
			}
			else if (genes.isEmpty())
			{
				System.out.println("This probe cannot be found in this database.");
			}
		}
		catch (Exception e)
		{
			System.out.println("This gene cannot be found in this database.");
			e.printStackTrace();
		}
		return null;
	}

}
