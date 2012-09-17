package org.molgenis.designgg;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvStringReader;
import org.molgenis.util.DetectOS;
import org.molgenis.util.FileLink;
import org.molgenis.util.TarGz;
import org.molgenis.util.Tuple;

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

	private String imagePath;
	/** OUT parameters for the result */
	private List<Tuple> indPerCondition;
	private List<Tuple> indPerSlide;
	private String imageLink; // the image
	private String indXCondLink; // Individuals per Condition
	private String indXSlideLink; // Individuals per Slide
	private String outputR; // Includes performance data
	private int progressPercentage; // Current progress in percentage, while
	// running
	private String estimatedEndTime; // Estimated end time
	private String waitingTime; // Waiting time
	private boolean bCalculationDone; // Flag to jump to next web page
	private boolean bCalculationFail; // Flag to signal an error coming from the
	// R execution
	LocalComputationResource cmd = null;
	private String rScript; // The R script used

	File workingDir = null; // tmp dir where the stuff is executed

	public CalculateDesignScreen(String name, ScreenController<?> parent)
	{
		super(name, parent);
		// setController(this); // using itself as controller.

		System.out.println("**** constructor CalculateDesignScreen");

		// rprocessor = new RProcessor4designGG();

		bCooking = false;
		bCalculationDone = false;
		bCalculationFail = false;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		// will refresh automatically, so nothing to do here.
		System.out.println("**** handleRequest CalculateDesignScreen");

	}

	byte[] toByteArrayFromalyMap(String filename) throws IOException
	{
		File f = new File(filename);
		FileInputStream fin = null;
		FileChannel ch = null;
		byte[] bytes = null;
		try
		{
			fin = new FileInputStream(f);
			ch = fin.getChannel();
			int size = (int) ch.size();
			MappedByteBuffer buf = ch.map(MapMode.READ_ONLY, 0, size);
			bytes = new byte[size];
			buf.get(bytes);

		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			throw e;
		}
		finally
		{
			try
			{
				if (fin != null)
				{
					fin.close();
				}
				if (ch != null)
				{
					ch.close();
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				throw e;
			}
		}
		return bytes;

	}

	@Override
	public void reload(Database db)
	{
		System.out.println("**** reload CalculateDesignScreen");

		DesignParameters p = ((MainScreen) this.getParent()).getScreen1().getDesignParameters();

		// no job running, start the job
		if (cmd == null)
		{
			cmd = new LocalComputationResource();
			startProcess();

		}
		// if job is running, monitor progress
		else
		{
			File progressFile = new File(this.workingDir.getAbsolutePath() + File.separator + "processing.txt");
			System.out.println("Current DIR" + this.workingDir.getAbsolutePath());
			int progress = -1;

			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(
						progressFile))));
				progress = (int) Math.round(Double.parseDouble(br.readLine()));
				br.close();
			}
			catch (IOException e1)
			{
				// IS A HACK
				setBCalculationDone(true);
				System.err.println("File not found:" + progressFile.getAbsolutePath());
			}

			bCooking = true;
			// is job done yet? that is when processing.txt says '100%'
			// then get the appropriate files

			if (progress == 100 || this.bCalculationDone) // then it is done
			{
				cmd = null;
				if (p.getTwoColorArray())
				{ // Dual Channel

					try
					{
						// 1. Set Individuals per Condition
						try
						{
							setIndPerCondition(readCsv(new String(toByteArrayFromalyMap(workingDir.getAbsolutePath()
									+ File.separator + "myDesignGG_conditionDesign.csv"))));
							Utils.setFile(workingDir + File.separator + "myDesignGG_conditionDesign.csv",
									toByteArrayFromalyMap(workingDir.getAbsolutePath() + File.separator
											+ "myDesignGG_conditionDesign.csv"));
							setIndXCondLink(workingDir.getName() + File.separator + "myDesignGG_conditionDesign.csv");
						}
						catch (Exception exp)
						{
							System.err.println("GOOD ERROR");
						}

						// 2. Set Individuals per Slide
						try
						{
							setIndPerSlide(readCsv(new String(toByteArrayFromalyMap(workingDir.getAbsolutePath()
									+ File.separator + "myDesignGG_arrayDesign.csv"))));
							Utils.setFile(workingDir + File.separator + "myDesignGG_arrayDesign.csv",
									toByteArrayFromalyMap(workingDir.getAbsolutePath() + File.separator
											+ "myDesignGG_arrayDesign.csv"));

							setIndXSlideLink(workingDir.getName() + File.separator + "myDesignGG_arrayDesign.csv");
						}
						catch (Exception exp)
						{
							System.err.println("GOOD ERROR");
						}
						setOutputR(new String(toByteArrayFromalyMap(workingDir.getAbsolutePath() + File.separator
								+ "outputR.log")).replace("\n", "<br>"));
						setBCalculationDone(true);
					}
					catch (Exception e)
					{
						setBCalculationDone(true);
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{ // Single Channel results

					try
					{
						// 1. Set Individuals per Condition
						try
						{
							setIndPerCondition(readCsv(new String(toByteArrayFromalyMap(workingDir.getAbsolutePath()
									+ File.separator + "myDesignGG_conditionDesign.csv"))));
							Utils.setFile(workingDir.getAbsolutePath() + File.separator
									+ "myDesignGG_conditionDesign.csv",
									toByteArrayFromalyMap(workingDir.getAbsolutePath() + File.separator
											+ "myDesignGG_conditionDesign.csv"));

							setIndXCondLink(workingDir.getName() + File.separator + "myDesignGG_conditionDesign.csv");
						}
						catch (Exception exp)
						{
							System.err.println("GOOD ERROR");
						}
						// 2. Set plot image
						// Utils.setFile( this.getImagePath() +
						// File.separator + getSessionId() +
						// File.separator + "myDesignGGSAplot.png",
						// toByteArrayFromalyMap("myDesignGGSAplot.png") );
						// setImageLink("tmpimages/" + getSessionId() +
						// "/myDesignGGSAplot.png");

						// 3. Set outputR
						setOutputR(new String(toByteArrayFromalyMap(workingDir.getAbsolutePath() + File.separator
								+ "outputR.log")).replace("\n", "<br>"));

						setBCalculationDone(true);
					}
					catch (Exception e)
					{ // TODO: show something about exception
						setBCalculationDone(true);
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
		for (Tuple line : reader)
		{
			logger.debug(line.getFields());
			result.add(line);

		}
		logger.debug(">>>> The tuple of discordia:" + result.toString());
		return result;
	}

	/*
	 * Here we retrieve the arguments from the user interface, we build the R
	 * code, and we launch the calculation on the server.
	 */
	private boolean startProcess()
	{
		DesignParameters p = ((MainScreen) this.getParent()).getScreen1().getDesignParameters();

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
				commands.add("weight <- c(" + weights.get(0) + "," + weights.get(1) + "," + weights.get(3) + ","
						+ weights.get(7) + "," + weights.get(2) + "," + weights.get(4) + "," + weights.get(8) + ","
						+ weights.get(5) + "," + weights.get(9) + "," + weights.get(10) + "," + weights.get(6) + ","
						+ weights.get(11) + "," + weights.get(12) + "," + weights.get(13) + "," + weights.get(14) + ")");
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
		commands.add("temp <-designGG( genotype=genotype, nSlides=nSlides, nTuple=nTuple, nEnvFactors=nEnvFactors, nLevels=nLevels,");
		commands.add("Level=Level, bTwoColorArray=bTwoColorArray, optimality=\"A\", method=\"SA\", ");
		commands.add("nIterations=nIterations, n.search=n.search,");
		commands.add("plotScores=F, envFactorNames=envFactorNames, writingProcess=writingProcess, weight=weight, region=region )");

		// PREPARE INPUT FILES
		// 2. We create the file attachments. The genotypes file
		Map<String, byte[]> inputAttachements = new HashMap<String, byte[]>();

		try
		{
			String filePath2 = p.getGenotype();
			Logger logger = Logger.getLogger(this.getClass());
			System.out.println(" !!!!!! -> startProcess. file:" + filePath2);
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

			FileUtils.deleteDirectory(workingFile);
			
			workingFile.delete();
			workingFile.mkdirs();

			this.workingDir = workingFile;

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

			System.out.println("*** " + workingFile.getAbsolutePath() + " ***");
			;
			// run script
			cmd.executeOSDependantCommand(new Command("cd " + workingFile.getAbsolutePath() + " && nohup R CMD BATCH "
					+ scriptFile.getAbsolutePath() + " outputR.log &", true, false, false), DetectOS.getOS());

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

	public int getProgressPercentage()
	{
		return progressPercentage;
	}

	public void setProgressPercentage(int progressPercentage)
	{
		this.progressPercentage = progressPercentage;
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
	 * @return the estimatedEndTime
	 */
	public String getEstimatedEndTime()
	{
		// return estimatedEndTime;
		return ""; // TODO!!
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
		// return waitingTime;
		return ""; // TODO!!
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
