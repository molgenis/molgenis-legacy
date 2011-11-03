package org.molgenis.compute.commandline;

import org.molgenis.util.Tuple;

import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA. User: georgebyelas Date: 02/11/2011 Time: 14:36 To
 * change this template use File | Settings | File Templates.
 */
public class StartCommandLineGeneration
{
	private File workingDir = null;
	private File workflowDir = null;
	private File fileWorksheet = null;
	private String applicationName = null;
	private ComputeBundle computeBundle = null;

	private void run() throws Exception
	{
		computeBundle = new ComputeBundleFromDirectory(workflowDir,
				fileWorksheet);
		WorkflowGeneratorCommandLine generator = new WorkflowGeneratorCommandLine();

		// set where to write scripts
		generator.setToWriteLocally(true);
		String path = workingDir.getAbsolutePath();
		(new File(path + System.getProperty("file.separator") + applicationName))
				.mkdir();
		generator.setLocalLocation(path + System.getProperty("file.separator")
				+ applicationName + System.getProperty("file.separator"));
		generator.setRemoteLocation("remote");

		// here the loop over samples/lanes
		for (Tuple target : computeBundle.getUserParameters())
		{
			Hashtable<String, String> userValues = new Hashtable<String, String>();
			// ugly copy from tuple to hashtable
			for (String field : target.getFields())
			{
				if(!target.isNull(field))
				{
					userValues.put(field, target.getString(field));
				}
			}

			generator.processSingleWorksheet(computeBundle, userValues, "",
					applicationName);
			// add generated applications to the bundle
			computeBundle.setComputeJobs(generator.getComputeApplications());

			// every sample can be processed and monitored in the separated
			// pipeline
			// MCF mcf = new MCFServerSsh();
			// mcf.setPipeline(generator.getPipeline());
		}

	}

	/*
	 * arg0 - file with worksheet elements arg1 - name of application (run) e.g.
	 * testrun
	 */
	public static void main(String[] args)
	{
		args = new String[]
		{
				"/Users/mswertz/Dropbox/NGS quality report/compute/New_Molgenis_Compute_for_GoNL/Example_01/SampleList_A102.csv",
				"test1",
				"/Users/mswertz/Dropbox/NGS quality report/compute/New_Molgenis_Compute_for_GoNL/Example_01/"

		};
		if (args.length < 2)
		{
			System.out.println("\n" + "    /*\n"
					+ "    * arg0 - file with worksheet elements\n"
					+ "    * arg1 - name of application (run) e.g. testrun\n"
					+ "    * */");
			System.exit(1);
		}
		else
		{
			StartCommandLineGeneration generation = new StartCommandLineGeneration();

			generation.setFileWorksheet(new File(args[0]));
			generation.setApplicationName(args[1]);
			if (args.length > 2)
			{
				generation.setWorkflowDir(new File(args[2]));
			}
			else
			{
				//default workflowDir == current dir
				generation.setWorkflowDir(new File("."));
			}
			generation.setWorkingDir(new File("."));

			try
			{
				generation.run();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void setFileWorksheet(File fileWorksheet)
	{
		this.fileWorksheet = fileWorksheet;
	}

	public void setApplicationName(String applicationName)
	{
		this.applicationName = applicationName;
	}

	public void setWorkingDir(File workingDir)
	{
		this.workingDir = workingDir;
	}

	public void setWorkflowDir(File workflowDir)
	{
		this.workflowDir = workflowDir;
	}

}
