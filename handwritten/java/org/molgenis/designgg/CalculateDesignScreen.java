package org.molgenis.designgg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.DetectOS;
import org.molgenis.util.FileLink;
import org.molgenis.util.Tuple;

import plugins.cluster.helper.Command;
import plugins.cluster.implementations.LocalComputationResource;

/**
 * This screen care of showing the progress of the calculation. If done, the
 * results are saved in properties indPerSlide and indPerCondition. These are
 * lists of tuples (i.e. a table)
 */
public class CalculateDesignScreen extends PluginModel
{
	private static final long serialVersionUID = -1423277857685738125L;
	/** INTERNAL store the id of the R process in */
	private String sessionId = null;
	public boolean bCooking = false; // TODO: Refactor the variable name
	// private RProcessor4designGG rprocessor;
	/** IN Parameters for the calculation */
	private DesignParameters designParameters;
	private String imagePath;
	/** OUT parameters for the result */
	private List<Tuple> indPerCondition;
	private List<Tuple> indPerSlide;
	private String imageLink; // the image
	private String indXCondLink; // Individuals per Condition
	private String indXSlideLink; // Individuals per Slide
	private String outputR; // Includes performance data
	private String progressPercentage; // Current progress in percentage, while
	// running
	private String estimatedEndTime; // Estimated end time
	private String waitingTime; // Waiting time
	private boolean bCalculationDone; // Flag to jump to next web page
	private boolean bCalculationFail; // Flag to signal an error coming from the
	// R execution
	LocalComputationResource cmd = null;
	private String rScript; // The R script used

	public CalculateDesignScreen(String name, ScreenController<?> parent)
	{
		super(name, parent);
		setController(this); // using itself as controller.

		// rprocessor = new RProcessor4designGG();

		bCooking = false;
		bCalculationDone = false;
		bCalculationFail = false;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// will refresh automatically, so nothing to do here.

	}

	@Override
	public void reload(Database db)
	{
		DesignParameters p = this.designParameters;
		String workingDir = "";

		// no job running, start the job
		if (cmd == null)
		{
			new LocalComputationResource();
			startProcess();
		}
		// if job is running, monitor progress
		else
		{
			String progress = "0"; // get this from processing.txt file

			// is job done yet? that is when processing.txt says '100%'
			// then get the appropriate files

			if (progress.equals("100")) // then it is done
			{
				if (p.getTwoColorArray())
				{ // Dual Channel

					try
					{
						// // 1. Set Individuals per Condition
						// setIndPerCondition(readCsv(workingDir+"/myDesignGG_conditionDesign.csv"));
						//							
						// //put csv as downloadable
						// FileLink download = this.getTempFile();
						// download.setLocalpath(new
						// File(workingDir+"/myDesignGG_conditionDesign.csv"));
						//							
						// Utils.setFile(this.getImagePath() + File.separator +
						// getSessionId() + File.separator
						// + "myDesignGG_conditionDesign.csv", mapResults
						// .get("myDesignGG_conditionDesign.csv"));
						//							
						// setIndXCondLink(download.getLink());
						//							
						// // 2. Set Individuals per Slide
						// setIndPerSlide(readCsv(workingDir+"/myDesignGG_arrayDesign.csv"));
						//							
						// Utils.setFile(this.getImagePath() + File.separator +
						// getSessionId() + File.separator
						// + "myDesignGG_arrayDesign.csv",
						// mapResults.get("myDesignGG_arrayDesign.csv"));
						//							
						// setIndXSlideLink("tmpimages/" + getSessionId() +
						// "/myDesignGG_arrayDesign.csv");
						//
						// setOutputR(new
						// String(mapResults.get("outputR.txt")));
						// setBCalculationDone(true);
					}
					catch (Exception e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{ // Single Channel results

					try
					{
						// // 1. Set Individuals per Condition
						// setIndPerCondition(readCsv(new
						// String(mapResults.get("myDesignGG_conditionDesign.csv"))));
						// Utils.setFile(this.getImagePath() + File.separator +
						// getSessionId() + File.separator
						// + "myDesignGG_conditionDesign.csv", mapResults
						// .get("myDesignGG_conditionDesign.csv"));
						// setIndXCondLink("tmpimages/" + getSessionId() +
						// "/myDesignGG_conditionDesign.csv");
						// // 2. Set plot image
						// // Utils.setFile( this.getImagePath() +
						// // File.separator + getSessionId() +
						// // File.separator + "myDesignGGSAplot.png",
						// // mapResults.get("myDesignGGSAplot.png") );
						// // setImageLink("tmpimages/" + getSessionId() +
						// // "/myDesignGGSAplot.png");
						// // 3. Set outputR
						// setOutputR(new
						// String(mapResults.get("outputR.txt")));
						//
						// setBCalculationDone(true);
					}
					catch (Exception e)
					{ // TODO: show something about exception
						e.printStackTrace();
					}

				}
			}

			// is job failed? monitor output.R for this?
			else if (false) // this should be function on output.R
			{

			}

			// is job running, then report progress, just use the String
			// progress for this
			else
			{
				this.setProgressPercentage(progress);
			}
		}

	}

	private List<Tuple> readCsv(String string) throws Exception
	{
		final List<Tuple> result = new ArrayList<Tuple>();
		CsvReader reader = new CsvStringReader(string);
		reader.setSeparator(',');
		reader.setMissingValues("na");
		reader.parse(new CsvReaderListener()
		{
			@Override
			public void handleLine(int line_number, Tuple line) throws Exception
			{
				logger.debug(line.getFields());
				result.add(line);

			}
		});
		logger.debug(">>>> The tuple of discordia:" + result.toString());
		return result;
	}

	/*
	 * Here we retrieve the arguments from the user interface, we build the R
	 * code, and we launch the calculation on the server.
	 */
	private boolean startProcess()
	{
		DesignParameters p = this.getDesignParameters();

		// CREATE R CODE
		// 1. We create the sequence of R commands (an R script)
		List<String> commands = new ArrayList<String>();

		// 1.1 Define analysis platform
		commands.add("bTwoColorArray <- " + (p.getTwoColorArray() ? "T" : "F"));

		// 1.2 Define individual genotypes
		commands.add("genotype <- read.table(\"genotypes.txt\")");

		// 1.3 Define experimental factors
		String levels = "Level <- list( ";

		String envFactorNames = "envFactorNames <- c(";
		boolean comma = false;

		if (p.getFactor1active() != null && p.getFactor1Level() != null)
		{
			// need F1, QF1
			envFactorNames = envFactorNames + " \"" + p.getFactor1Label() + "\"";
			levels += getStrLevels(p.getFactor1Level());
			comma = true;
		}
		if (p.getFactor2active() != null && p.getFactor2Level() != null)
		{
			envFactorNames = envFactorNames + (comma ? ", " : "") + " \"" + p.getFactor2Label() + "\"";
			levels = levels + (comma ? ", " : "") + getStrLevels(p.getFactor2Level());
			comma = true;
		}
		if (p.getFactor3active() != null && p.getFactor3Level() != null)
		{
			envFactorNames = envFactorNames + (comma ? ", " : "") + " \"" + p.getFactor3Label() + "\"";
			levels = levels + (comma ? ", " : "") + getStrLevels(p.getFactor3Level());
		}

		levels += ")";
		envFactorNames += ")";
		commands.add(envFactorNames);
		commands.add(levels);
		commands.add("nEnvFactors <- length( Level )");
		commands.add("nLevels <- as.numeric(lapply( Level, length ))");

		// 1.4 Set constrains
		if (p.getNoSlides() != null)
		{
			commands.add("nSlides <- " + String.valueOf(p.getNoSlides()));
			commands.add("nTuple <- NULL");
		}
		else
		{
			commands.add("nTuple <- " + String.valueOf(p.getNoRilsPerLevel()));
			commands.add("nSlides <- NULL");
		}

		// 1.5 set marker ranges
		if (p.getRangeStart() != null && p.getRangeEnd() != null)
		{
			String ranges = "c(";
			for (int i = 0; i < p.getRangeStart().size(); i++)
			{
				if (i > 0) ranges += ",";
				ranges += "seq(" + p.getRangeStart().get(i) + "," + p.getRangeEnd().get(i) + ",by=1)";
			}
			ranges += ")";
			commands.add("region <- " + ranges);
		}
		else
		{
			commands.add("region <- NULL");
		}

		// 1.6 set weights
		if (p.getWeight() != null && p.getWeight().size() > 1)
		{
			List<?> weights = p.getWeight();

			// source order from web
			// Q
			// F1 QF1
			// F2 QF2 F1F2 QF1F2
			// F3 QF3 F1F3 F2F3 QF1F3 QF2F3 F1F2F3 QF1F2F3
			if (p.getFactor3active() != null)
			{
				// Q F1 F2 F3 QF1 QF2 QF3 F1F2 F1F3 F2F3 QF1F2 QF1F3 F1F2F3
				// QF1F2F3
				// weight <- c(1,2,4,8,3,5,9,6,10,11,7,12,13,14,15)
				commands
						.add("weight <- c(" + weights.get(0) + "," + weights.get(1) + "," + weights.get(3) + ","
								+ weights.get(7) + "," + weights.get(2) + "," + weights.get(4) + "," + weights.get(8)
								+ "," + weights.get(5) + "," + weights.get(9) + "," + weights.get(10) + ","
								+ weights.get(6) + "," + weights.get(11) + "," + weights.get(12) + ","
								+ weights.get(13) + "," + weights.get(14) + ")");
			}
			else if (p.getFactor2active() != null)
			{
				// Q F1 F2 QF1 QF2 F1F2 QF1F2
				// weight <- c(0,1,3,2,4,5,6)
				commands.add("weight <- c(" + weights.get(0) + "," + weights.get(1) + "," + weights.get(3) + ","
						+ weights.get(2) + "," + weights.get(4) + "," + weights.get(5) + "," + weights.get(6) + ")");
			}
			else if (p.getFactor1active() != null)
			{
				// Q F1,QF1
				// weight <- c(0,1,2)
				commands.add("weight <- c(" + weights.get(0) + "," + weights.get(1) + "," + weights.get(2) + ")");
			}
		}
		else
		{
			commands.add("weight <- 1");
		}

		// 1.5 Commons arguments
		commands.add("startTemp <- 1");
		commands.add("endTemp <- 1e-10");
		commands.add("nIterations <- " + p.getNoIterations());
		commands.add("maxTempStep <- 0.9");
		commands.add("n.search <- 2");
		commands.add("writingProcess <- T");

		// commands.add("temp <-designGG( genotype=genotype, nSlides=nSlides, nTuple=nTuple, nEnvFactors=nEnvFactors, nLevels=nLevels, ");
		// commands.add("Level=Level, bTwoColorArray=bTwoColorArray, initial=NULL, weight=1, region=NULL, optimality=\"A\", method=\"SA\", ");
		// commands.add("nIterations=nIterations, n.search=n.search,  endTemp=endTemp, startTemp=startTemp, maxTempStep=maxTempStep,");
		// commands.add("plotScores=F, envFactorNames=envFactorNames, writingProcess=writingProcess )");

		commands.add("library(designGG)");
		commands
				.add("temp <-designGG( genotype=genotype, nSlides=nSlides, nTuple=nTuple, nEnvFactors=nEnvFactors, nLevels=nLevels,");
		commands.add("Level=Level, bTwoColorArray=bTwoColorArray, optimality=\"A\", method=\"SA\", ");
		commands.add("nIterations=nIterations, n.search=n.search,");
		commands
				.add("plotScores=F, envFactorNames=envFactorNames, writingProcess=writingProcess, weight=weight, region=region )");

		// PREPARE INPUT FILES
		// 2. We create the file attachments. The genotypes file
		Map<String, byte[]> inputAttachements = new HashMap<String, byte[]>();

		try
		{
			String filePath2 = p.getGenotype();
			Logger logger = Logger.getLogger(this.getClass());
			logger.info("startProcess. file:" + filePath2);
			String script = "";
			for (String command : commands)
				script += command.toString() + "\n";

			// for echo on screen
			String htmlScript = "";
			for (String command : commands)
				htmlScript += command.toString() + "<br/>";
			this.setRScript(htmlScript);

			logger.info("Trying to run script: \n" + script);
			inputAttachements.put("genotypes.txt", Utils.getFile(filePath2));
		}
		catch (Exception e)
		{
			Logger.getLogger(this.getClass()).debug("Error trying to recover files");
			e.printStackTrace();
			return false;
		}

		executeR(commands, inputAttachements);

		return true;
		// return rprocessor.startProcess(sessionId, commands,
		// inputAttachements);
	}

	public void executeR(List<String> commands, Map<String, byte[]> inputAttachements)
	{
		try
		{

			// create a working dir
			FileLink workingDir = this.getTempFile();
			File workingFile = workingDir.getLocalpath();
			workingFile.delete();
			workingFile.mkdirs();

			// copy input files
			for (String fileName : inputAttachements.keySet())
			{
				File dataFile = new File(workingFile.getAbsoluteFile() + "/" + fileName);
				FileOutputStream fos = new FileOutputStream(dataFile);
				FileChannel fc = fos.getChannel();
				ByteBuffer bb = ByteBuffer.wrap(inputAttachements.get(fileName));
				fc.write(bb);
				fos.close();
			}

			// create the script
			File scriptFile = new File(workingFile.getAbsoluteFile() + "/script.R");
			FileWriter fw = new FileWriter(scriptFile);
			for (String line : commands)
			{
				fw.write(line + "\n");
			}
			fw.close();

			// run script
			cmd.executeOSDependantCommand(new Command("cd " + workingFile.getAbsolutePath() + " && R CMD BATCH "
					+ scriptFile.getAbsolutePath() + " outputR.log", true, false, false), DetectOS.getOS());

		}
		catch (Exception e)
		{
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public List<Tuple> getIndPerCondition()
	{
		return indPerCondition;
	}

	public void setIndPerCondition(List<Tuple> indPerCondition)
	{
		this.indPerCondition = indPerCondition;
	}

	public List<Tuple> getIndPerSlide()
	{
		return indPerSlide;
	}

	public void setIndPerSlide(List<Tuple> indPerSlide)
	{
		this.indPerSlide = indPerSlide;
	}

	public DesignParameters getDesignParameters()
	{
		return designParameters;
	}

	public void setDesignParameters(DesignParameters designParameters)
	{
		this.designParameters = designParameters;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public boolean getBCooking()
	{
		return bCooking;
	}

	public void setBCooking(boolean cooking)
	{
		bCooking = cooking;
	}

	public String getImagePath()
	{
		return imagePath;
	}

	public void setImagePath(String imagePath)
	{
		this.imagePath = imagePath;
	}

	public String getImageLink()
	{
		return imageLink;
	}

	public void setImageLink(String imageLink)
	{
		this.imageLink = imageLink;
	}

	// Returns a string with the form: "c( 1, 56, 11, 23 )
	String getStrLevels(List<?> lLevels)
	{
		String strLevels = "c( ";

		for (Object level : lLevels)
		{
			// TODO: This is really ugly!
			// Do we read a number or something different?
			try
			{
				double test = Double.parseDouble(level.toString());
				// Number
				strLevels = strLevels + level.toString() + ", ";
			}
			catch (NumberFormatException e)
			{
				// Not number
				strLevels = strLevels + "\"" + level.toString() + "\"" + ", ";
			}
		}
		// strip trailing ','
		strLevels = strLevels.substring(0, strLevels.length() - 2);
		strLevels += " )";

		return strLevels;
	}

	/**
	 * @return the outputR
	 */
	public String getOutputR()
	{
		return outputR;
	}

	/**
	 * @param outputR
	 *            the outputR to set
	 */
	public void setOutputR(String outputR)
	{
		this.outputR = outputR;
	}

	public boolean calculationDone()
	{

		return bCalculationDone;
	}

	/**
	 * @return the bCalculationDone
	 */
	public boolean isBCalculationDone()
	{
		return bCalculationDone;
	}

	/**
	 * @param calculationDone
	 *            the bCalculationDone to set
	 */
	public void setBCalculationDone(boolean calculationDone)
	{
		bCalculationDone = calculationDone;
	}

	/**
	 * @return the indXCondLink
	 */
	public String getIndXCondLink()
	{
		return indXCondLink;
	}

	/**
	 * @param indXCondLink
	 *            the indXCondLink to set
	 */
	public void setIndXCondLink(String indXCondLink)
	{
		this.indXCondLink = indXCondLink;
	}

	/**
	 * @return the indXSlideLink
	 */
	public String getIndXSlideLink()
	{
		return indXSlideLink;
	}

	/**
	 * @param indXSlideLink
	 *            the indXSlideLink to set
	 */
	public void setIndXSlideLink(String indXSlideLink)
	{
		this.indXSlideLink = indXSlideLink;
	}

	/**
	 * @return the bCalculationFail
	 */
	public boolean isBCalculationFail()
	{
		return bCalculationFail;
	}

	/**
	 * @param calculationFail
	 *            the bCalculationFail to set
	 */
	public void setBCalculationFail(boolean calculationFail)
	{
		bCalculationFail = calculationFail;
	}

	/**
	 * @return the progressPercentage
	 */
	public String getProgressPercentage()
	{
		return progressPercentage;
	}

	/**
	 * @param progressPercentage
	 *            the progressPercentage to set
	 */
	public void setProgressPercentage(String progressPercentage)
	{
		this.progressPercentage = progressPercentage;
	}

	/**
	 * @return the estimatedEndTime
	 */
	public String getEstimatedEndTime()
	{
		return estimatedEndTime;
	}

	/**
	 * @param estimatedEndTime
	 *            the estimatedEndTime to set
	 */
	public void setEstimatedEndTime(String estimatedEndTime)
	{
		this.estimatedEndTime = estimatedEndTime;
	}

	/**
	 * @return the waitingTime
	 */
	public String getWaitingTime()
	{
		return waitingTime;
	}

	/**
	 * @param waitingTime
	 *            the waitingTime to set
	 */
	public void setWaitingTime(String waitingTime)
	{
		this.waitingTime = waitingTime;
	}

	public void setRScript(String rScript)
	{
		this.rScript = rScript;
	}

	public String getRScript()
	{
		return rScript;
	}

	@Override
	public String getViewName()
	{
		// TODO Auto-generated method stub
		return "screens_CalculateDesignScreen";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/designgg/CalculateDesignScreen.ftl";
	}

}
