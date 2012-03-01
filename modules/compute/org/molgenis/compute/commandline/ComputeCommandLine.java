package org.molgenis.compute.commandline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.commandline.options.Options;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Tuple;

import bsh.This;

// This dependency needs to go when we upgrade to Java 7+
//import com.google.common.io.Files;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ComputeCommandLine
{
	protected ComputeBundle computeBundle;
	protected File parametersfile, workflowfile, worksheetfile, protocoldir, workingdir;
	protected String outputdir, templatedir, backend;
	protected Hashtable<String, Object> userValues = new Hashtable<String, Object>();
	private List<ComputeJob> jobs = new ArrayList<ComputeJob>();
	private Worksheet worksheet;

	private void generateJobs() throws Exception
	{
		computeBundle = new ComputeBundleFromDirectory(this);
		this.worksheet = new Worksheet(computeBundle);

		List<ComputeProtocol> protocollist = computeBundle.getComputeProtocols();

		// create hash of all workflow elements (needed for dependencies)
		Map<String, WorkflowElement> wfeMap = new HashMap<String, WorkflowElement>();
		for (WorkflowElement wfe : computeBundle.getWorkflowElements())
		{
			wfeMap.put(wfe.getName(), wfe);
		}

		// process workflow elements
		for (WorkflowElement wfe : computeBundle.getWorkflowElements())
		{
			print("Starting generation of workflow element: " + wfe.getName());

			// get protocol and find its targets
			ComputeProtocol protocol = findProtocol(wfe.getProtocol_Name(), protocollist);

			// get template + insert header and footer
			String scripttemplate = addHeaderFooter(protocol.getScriptTemplate(), protocol.getInterpreter());

			// fold and reduce worksheet
			// String[] targets = parseHeaderElement(FOREACH, scripttemplate);
			List<String> targets = protocol.getIterateOver_Name();
			if (0 == targets.size())
			{
				targets.add("line_number");
			}


			// add path to loader
			// FileTemplateLoader ftl1 = new FileTemplateLoader(this.workflowdir);

//			Configuration cfg = new Configuration();
//			cfg.setDirectoryForTemplateLoading(this.protocoldir);
//			String documentation = protocol.getDescription();
//			Template template = new Template("a template", new StringReader(documentation), cfg);
//			StringWriter filledtemplate = new StringWriter();
//			template.process(parameters, filledtemplate);
			
//			System.out.println(">>> " + documentation);
//			System.out.println(">>> " + filledtemplate.toString());
			
			List<Tuple> folded = Worksheet.foldWorksheet(this.worksheet.worksheet, this.computeBundle.getComputeParameters(),targets);
//			Worksheet.reduceTargets(targets);
//			print("folded worksheet: " + this.worksheet.folded.size() + ":" + this.worksheet.folded.toString());
//			print("reduced worksheet: " + this.worksheet.reduced.size() + ":" + this.worksheet.reduced.toString());

			// each element of reduced worksheet produces one protocolApplication (i.e. a script)
			for (Tuple work : folded)
			{
				// fill template with work and put in script
				ComputeJob job = new ComputeJob();
				job.setName(this.generateJobName(wfe, work));
				job.setInterpreter(protocol.getInterpreter());

				// if walltime, cores, mem not specified in protocol, then use value from worksheet
				String queue = (protocol.getClusterQueue() == null ? worksheet.getdefaultvalue("clusterQueue") : protocol.getClusterQueue());
				work.set("clusterQueue", queue);
//				job.setClusterQueue(queue);
				String walltime = (protocol.getWalltime() == null ? worksheet.getdefaultvalue("walltime") : protocol.getWalltime());
//				job.setWalltime(walltime);
				work.set("walltime", walltime);
				Integer cores = (protocol.getCores() == null ? Integer.parseInt(worksheet.getdefaultvalue("cores")) : protocol.getCores());
//				job.setCores(cores);
				work.set("cores", cores);
				String mem = (protocol.getMem() == null ? worksheet.getdefaultvalue("mem") : protocol.getMem());
//				job.setMem(mem);
				work.set("mem", mem + "gb");

				job.setInterpreter(protocol.getInterpreter() == null ? worksheet.getdefaultvalue("interpreter") : protocol.getInterpreter());
				
				// set jobname. If a job starts/completes, we put this in a logfile
				work.set("jobname", job.getName());

				// record in worksheet job names for each element
				// (in column with same name as wfe)
				// this.worksheet.set(targets, work, wfe.getName(), job.getName());

				// retrieve previousSteps
				for (String previousStep : wfe.getPreviousSteps_Name())
				{
					// get the WorkflowElement of previous step
					WorkflowElement previousWfe = wfeMap.get(previousStep);

					ComputeProtocol wfeProtocol = findProtocol(previousWfe.getProtocol_Name(), computeBundle.getComputeProtocols());

					// see how long the list is
					int size = 1;
					for (String target : wfeProtocol.getIterateOver_Name())
					{
						if (work.getObject(target) instanceof List)
						{
							size = work.getList(target).size();
							break;
						}
					}

					// we calculate dependencies
					Set<String> dependencies = new HashSet<String>();
					for (int i = 0; i < size; i++)
					{
						String jobName = previousWfe.getName();
						for (String target : wfeProtocol.getIterateOver_Name())
						{
							if (work.getObject(target) instanceof List) jobName += "_" + work.getList(target).get(i);
							else
								jobName += "_" + work.getString(target);
						}
						dependencies.add(stepnr(previousWfe.getName()) + jobName);
					}
					job.getPrevSteps_Name().addAll(dependencies);
				}

				// add the script
				job.setComputeScript(filledtemplate(scripttemplate, work, job.getName()));

				this.jobs.add(job);

				print("Generated " + job.getName() + ", depending on " + job.getPrevSteps_Name());
			}
		}

		// UNCOMMENT THE FOLLOWING CODE IF YOU WANT: as a last step add a job that writes a "pipeline.finished" file
/*		
		ComputeJob job = new ComputeJob();
		job.setName(getworkflowfilename());
		job.setInterpreter("bash");

		// if walltime, cores, mem not specified in protocol, then use value from worksheet
		job.setWalltime("00:00:10");
		job.setCores(1);
		job.setMem(1);

		// final job is dependent on all other jobs
		Set<String> dependencies = new HashSet<String>();
		for (ComputeJob cj : this.jobs)
		{
			dependencies.add(cj.getName());
		}
		job.getPrevSteps_Name().addAll(dependencies);

		// add the script
		job.setComputeScript("touch $PBS_O_WORKDIR" + File.separator + getworkflowfilename() + ".finished");

		this.jobs.add(job);

		// print("compute parameters: " + computeBundle.getComputeParameters().toString());
		// print("user parameters: " + computeBundle.getUserParameters());
		// print("full worksheet: " + computeBundle.getWorksheet());
*/
	}

	private String addHeaderFooter(String scripttemplate, String interpreter)
	{
		// THIS SHOULD BE REPLACED WITH TEMPLATES:
		
		String ls = System.getProperty("line.separator");

		scripttemplate = "<#include \"Header.ftl\"/>" 
						+ scripttemplate + ls
						+ "<#include \"Footer.ftl\"/>";
//					   + "<#include \"Macros.ftl\"/>" + ls
//					   + "<@begin/>" + ls
//					   + (interpreter.equalsIgnoreCase("R") ? "<@Rbegin/>" + ls : "")
//					   + scripttemplate
//					   + (interpreter.equalsIgnoreCase("R") ? "<@Rend/>" + ls : "")
//					   + "<@end/>" + ls;
			
		return(scripttemplate);
	}

	private String stepnr(String wfeName)
	{
		// retrieve step number of wfeName in total workflow

		List<WorkflowElement> workflow = computeBundle.getWorkflowElements();
		for (int i = 0; i < workflow.size(); i++)
		{
			if (wfeName.equalsIgnoreCase(workflow.get(i).getName()))
			{
				return ("s" + (i < 10 ? "0" : "") + i + "_");
			}
		}

		return null;
	}

	private String generateJobName(WorkflowElement wfe, Tuple tuple)
	{
		String jobName = wfe.getName();
		ComputeProtocol wfeProtocol = findProtocol(wfe.getProtocol_Name(), computeBundle.getComputeProtocols());

		// in case no targets, we number
		List<String> targets = wfeProtocol.getIterateOver_Name();
		if (0 == targets.size())
		{
			jobName += "_" + tuple.getString("line_number");
		}
		// otherwise use targets
		else
			for (String target : targets)
			{
				jobName += "_" + tuple.getString(target);
			}

		return stepnr(wfe.getName()) + jobName;
	}

	private String filledtemplate(String scripttemplate, Tuple work, String jobname) throws IOException, TemplateException
	{
		// first create map
		Map<String, Object> parameters = new HashMap<String, Object>();

		// add the helper
		parameters.put("freemarkerHelper", new FreemarkerHelper(this.computeBundle));
		parameters.put("parameters",work);
		parameters.put("workflowElements", this.computeBundle.getWorkflowElements());
		
		
		for (String field : work.getFields())
		{
			parameters.put(field, work.getObject(field));
		}

		// System.out.println(">> parameters > " + parameters);
		// System.out.println(">> script template > " + scripttemplate);

		Configuration cfg = new Configuration();

		// add path to loader
		// FileTemplateLoader ftl1 = new FileTemplateLoader(this.workflowdir);
		cfg.setDirectoryForTemplateLoading(this.protocoldir);

		Template template = new Template(jobname, new StringReader(scripttemplate), cfg);
		StringWriter filledtemplate = new StringWriter();
		template.process(parameters, filledtemplate);

		// put debug info in script
//		String script = "\n#####\n";
//		script = script + "## The following ${parameters} are values:\n";
//		script = script + "##   - " + worksheet.foldon + " " + worksheet.getConstants() + "\n";
//		script = script + "## The following parameters are lists, <#list parameters as p>${p}</#list> \n";
//		script = script + "##   - " + worksheet.list + "\n";
//		script = script + "#####\n\n";
//		script = script + filledtemplate.toString();

		return filledtemplate.toString();
	}

	private ComputeProtocol findProtocol(String protocol_name, List<ComputeProtocol> protocollist)
	{
		for (ComputeProtocol c : protocollist)
		{
			if (c.getName().equalsIgnoreCase(protocol_name)) return c;
		}
		return null;
	}

	// public String[] parseHeaderElement(String header, String protocol)
	// {
	// int posInput = protocol.indexOf(header) + header.length();
	// int posNewLine = protocol.indexOf("\n", posInput);
	// String list = protocol.substring(posInput, posNewLine);
	//
	// String[] elements = list.split(",");
	//
	// for (int i = 0; i < elements.length; i++)
	// {
	// elements[i] = elements[i].trim();
	// }
	//
	// return elements;
	// }

	public static void main(String[] args)
	{
		Options opt = new Options(args, Options.Prefix.DASH, Options.Multiplicity.ONCE, 1);

		opt.getSet().addOption("parametersfile", false, Options.Separator.EQUALS);
		opt.getSet().addOption("workflowfile", false, Options.Separator.EQUALS);
		opt.getSet().addOption("worksheet", false, Options.Separator.EQUALS);
		opt.getSet().addOption("protocoldir", false, Options.Separator.EQUALS);
		opt.getSet().addOption("outputscriptsdir", false, Options.Separator.EQUALS);
		opt.getSet().addOption("grid", false, Options.Separator.EQUALS, Options.Multiplicity.ZERO_OR_ONE);
		opt.getSet().addOption("cluster", false, Options.Separator.EQUALS, Options.Multiplicity.ZERO_OR_ONE);
		opt.getSet().addOption("templatesdir", false, Options.Separator.EQUALS);

		boolean isCorrect = opt.check();

		if (!isCorrect)
		{
			System.out.println(opt.getCheckErrors());

			System.out.println("command line format -i(nputlist)=<InputWorksheet> " + "-p(arametersfile)=<parameters.txt>"
					+ "-w(orkflowfile)=<workflow.txt>" + "-prot(ocoldir)=<WorkflowDescriptionDir> " + "-o(utputscriptsdir=<OutputDir> "
					+ "-g(rid)|c(luster)=<LocationOnBackend(Grid or Cluster)> " + "-t(emplatesdir)=<TemplatesDir> " + "<GererationID>");
			System.exit(1);
		}

		ComputeCommandLine ccl = new ComputeCommandLine();

		ccl.parametersfile = new File(opt.getSet().getOption("parametersfile").getResultValue(0));
		ccl.workflowfile = new File(opt.getSet().getOption("workflowfile").getResultValue(0));
		ccl.worksheetfile = new File(opt.getSet().getOption("worksheet").getResultValue(0));
		ccl.outputdir = opt.getSet().getOption("outputscriptsdir").getResultValue(0);
		ccl.protocoldir = new File(opt.getSet().getOption("protocoldir").getResultValue(0));
		ccl.templatedir = opt.getSet().getOption("templatesdir").getResultValue(0);

		if (opt.getSet().isSet(WorkflowGeneratorCommandLine.GRID))
		{
			System.out.println("generation for grid");
			ccl.backend = WorkflowGeneratorCommandLine.GRID;
			ccl.userValues.put("outputdir", opt.getSet().getOption(WorkflowGeneratorCommandLine.GRID).getResultValue(0));
		}
		else if (opt.getSet().isSet(WorkflowGeneratorCommandLine.CLUSTER))
		{
			System.out.println("generation for cluster");
			ccl.backend = WorkflowGeneratorCommandLine.CLUSTER;
			ccl.userValues.put("outputdir", opt.getSet().getOption(WorkflowGeneratorCommandLine.CLUSTER).getResultValue(0));
		}

		String generationID = opt.getSet().getData().get(0);

		ccl.userValues.put("runID", generationID);
		ccl.workingdir = new File(".");

		try
		{
			ccl.generateJobs();
			ccl.copyWorksheetAndWorkflow();
			ccl.generateScripts();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		print("Finished with generation!");
		System.exit(0);
	}

	private void copyWorksheetAndWorkflow()
	{
		try
		{
			for (File f : Arrays.asList(this.workflowfile, this.worksheetfile))
			{
				String[] filenamelist = f.toString().split(File.separator);
				String filename = filenamelist[filenamelist.length - 1];
				//Files.copy(f, new File(this.outputdir + File.separator + filename));
				FileUtils.copyFile(f, new File(this.outputdir + File.separator + filename));
			}
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

	}

	private String getworkflowfilename()
	{
		String[] workflowfilenamelist = this.workflowfile.toString().split(File.separator);
		String f = workflowfilenamelist[workflowfilenamelist.length - 1];

		// replace dots with underscore, because qsub does not allow for dots in job names or so...
		f = f.replace('.', '_');

		return f;
	}

	/** Convert all compute jobs into scripts + submit.sh */
	private void generateScripts()
	{
		new File(outputdir).mkdirs();
		try
		{
			// and produce submit.sh
			PrintWriter submitWriter = new PrintWriter(new File(outputdir + File.separator + "submit.sh"));

			// also produce a submitlocal.sh
			PrintWriter submitWriterLocal = new PrintWriter(new File(outputdir + File.separator + "submitlocal.sh"));
			
			// touch "workflow file name".started in same directory as submit.sh, when starting submit.sh
			String cmd = "DIR=\"$( cd \"$( dirname \"${BASH_SOURCE[0]}\" )\" && pwd )\"";
			submitWriter.println(cmd);
			submitWriterLocal.println(cmd);
			cmd = "touch $DIR" + File.separator + getworkflowfilename() + ".started";
			submitWriter.println(cmd);
			submitWriterLocal.println(cmd);

			for (ComputeJob job : this.jobs)
			{
				// create submit in submit.sh
				String dependency = "";
				if (job.getPrevSteps_Name().size() > 0)
				{
					dependency = "-W depend=afterok";

					for (String previous : job.getPrevSteps_Name())
					{
						dependency += ":$" + previous;
					}
				}
				
				// do stuff for submit.sh
				submitWriter.println("#" + job.getName());
				submitWriter.println(job.getName() + "=$(qsub -N " + job.getName() + " " + dependency + " " + job.getName() + ".sh)");
				submitWriter.println("echo $" + job.getName());
				submitWriter.println("sleep 1");

				// do stuff for submitlocal.sh
				submitWriterLocal.println("echo Starting with " + job.getName() + "...");
				submitWriterLocal.println("sh " + job.getName() + ".sh");
				submitWriterLocal.println("#Dependencies: " + dependency);
				submitWriterLocal.println("");
				
				// produce .sh file in outputdir for each job
				PrintWriter jobWriter = new PrintWriter(new File(outputdir + File.separator + job.getName() + ".sh"));

				// write the script
				jobWriter.println(job.getComputeScript());

				jobWriter.close();
			}

			submitWriter.close();
			submitWriterLocal.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void print(String string)
	{
		System.out.println(">> " + string);
	}
}
