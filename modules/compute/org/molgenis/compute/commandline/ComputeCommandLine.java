package org.molgenis.compute.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.molgenis.compute.ComputeJob;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.compute.commandline.options.Options;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.util.Tuple;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ComputeCommandLine
{
	protected ComputeBundle computeBundle;
	protected File parametersfile, workflowfile, worksheetfile, protocoldir, workingdir;
	protected String outputdir, templatedir, backend;
	protected Hashtable<String, Object> userValues = new Hashtable<String, Object>();
	private static final String FOREACH = "#FOREACH";
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
			print("Workflow element: " + wfe.getName());

			// get protocol and find its targets
			ComputeProtocol protocol = findProtocol(wfe.getProtocol_Name(), protocollist);
			String scripttemplate = protocol.getScriptTemplate();

			// fold and reduce worksheet
			// String[] targets = parseHeaderElement(FOREACH, scripttemplate);
			List<String> targets = protocol.getIterateOver_Name();
			if (0 == targets.size())
			{
				targets.add("line_number");
			}

			this.worksheet.foldWorksheet(targets);
			this.worksheet.reduceTargets(targets);
			print("folded worksheet: " + this.worksheet.folded.size() + ":" + this.worksheet.folded.toString());
			print("reduced worksheet: " + this.worksheet.reduced.size() + ":" + this.worksheet.reduced.toString());

			// each element of reduced worksheet produces one protocolApplication (i.e. a script)
			for (Tuple work : this.worksheet.reduced)
			{
				// fill template with work and put in script
				ComputeJob job = new ComputeJob();
				job.setName(this.generateJobName(wfe, work));
				job.setInterpreter(protocol.getInterpreter());
				job.setWalltime(protocol.getWalltime());
				job.setCores(protocol.getCores());

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
						dependencies.add(jobName);
					}
					job.getPrevSteps_Name().addAll(dependencies);
				}

				// add the script
				job.setComputeScript(filledtemplate(scripttemplate, work));

				this.jobs.add(job);

				System.out.println("============================ new job: ");
				System.out.println(job.getName() + " depends on " + job.getPrevSteps_Name());
			}
		}

		// print("compute parameters: " + computeBundle.getComputeParameters().toString());
		// print("user parameters: " + computeBundle.getUserParameters());
		// print("full worksheet: " + computeBundle.getWorksheet());

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
		return jobName;
	}

	private List<Tuple> getUnfolded(Tuple work)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private String filledtemplate(String scripttemplate, Tuple work) throws IOException, TemplateException
	{
		// first create map
		Map<String, Object> parameters = new HashMap<String, Object>();

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

		Template template = new Template("a template", new StringReader(scripttemplate), cfg);
		StringWriter filledtemplate = new StringWriter();
		template.process(parameters, filledtemplate);

		// put debug info in script
		String script 	= "\n#####\n";
		script = script + "## The following ${parameters} are values:\n";
		script = script + "##   - " + worksheet.foldon + "\n";
		script = script + "##   - " + worksheet.getConstants() + "\n";
		script = script + "## The following parameters are lists, <#list parameters as p>${p}</#list> \n";
		script = script + "##   - " + worksheet.list + "\n";
		script = script + "#####\n\n";
		script = script + filledtemplate.toString();

		return script;
	}

	private ComputeProtocol findProtocol(String protocol_name, List<ComputeProtocol> protocollist)
	{
		for (ComputeProtocol c : protocollist)
		{
			if (c.getName().equalsIgnoreCase(protocol_name)) return c;
		}
		return null;
	}

	public String[] parseHeaderElement(String header, String protocol)
	{
		int posInput = protocol.indexOf(header) + header.length();
		int posNewLine = protocol.indexOf("\n", posInput);
		String list = protocol.substring(posInput, posNewLine);

		String[] elements = list.split(",");

		for (int i = 0; i < elements.length; i++)
		{
			elements[i] = elements[i].trim();
		}

		return elements;
	}

	public static void main(String[] args)
	{
		Options opt = new Options(args, Options.Prefix.DASH, Options.Multiplicity.ONCE, 1);

		opt.getSet().addOption("parametersfile", false, Options.Separator.EQUALS);
		opt.getSet().addOption("workflowfile", false, Options.Separator.EQUALS);
		opt.getSet().addOption("inputlist", false, Options.Separator.EQUALS);
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
		ccl.worksheetfile = new File(opt.getSet().getOption("inputlist").getResultValue(0));
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

			ccl.generateScripts();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		print("Finished with generation!");
		System.exit(0);
	}

	/** Convert all compute jobs into scripts + submit.sh */
	private void generateScripts()
	{
		try
		{
			// and produce submit.sh
			PrintWriter submitWriter = new PrintWriter(new File(outputdir + File.separator + "submit.sh"));

			for (ComputeJob job : this.jobs)
			{
				// create submit in submit.sh
				String dependency = "";
				if(job.getPrevSteps_Name().size() > 0)
				{
					dependency = "-W depend=afterok";
				
					for(String previous: job.getPrevSteps_Name())
					{
						dependency += ":$"+previous;
					}
				}
				submitWriter.println("#"+job.getName());
				submitWriter.println(job.getName()+"=$(qsub -N "+job.getName()+" "+dependency+" "+job.getName()+".sh)");
				submitWriter.println("echo $"+job.getName());
				submitWriter.println("sleep 1");
				
				// produce .sh file in outputdir for each job
				PrintWriter jobWriter = new PrintWriter(new File(outputdir + File.separator + job.getName() + ".sh"));

				// write headers (depends on backend)
				jobWriter.println("#!/bin/bash");
				jobWriter.println("#PBS -q gcc");
				jobWriter.println("#PBS -l nodes=1:ppn=" + job.getCores());
				jobWriter.println("#PBS -l walltime=" + job.getWalltime());
				jobWriter.println("#PBS -l mem=" + job.getMem() + "gb");
				jobWriter.println("#PBS -e "+job.getName()+".err");
				jobWriter.println("#PBS -o "+job.getName()+".out");

				// write the script
				jobWriter.println(job.getComputeScript());

				// write footers

				jobWriter.close();
			}
			submitWriter.close();
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
