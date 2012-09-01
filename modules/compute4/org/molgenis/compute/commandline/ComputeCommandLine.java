package org.molgenis.compute.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.molgenis.compute.commandline.options.Options;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.util.Tuple;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

//import nl.vu.psy.rite.exceptions.RiteException;
//import nl.vu.psy.rite.operations.Recipe;
//import nl.vu.psy.rite.operations.Step;
//import nl.vu.psy.rite.operations.implementations.bash.BashOperation;
//import nl.vu.psy.rite.persistence.mongo.MongoRecipeStore;

public class ComputeCommandLine {
	protected ComputeBundle computeBundle;
	protected File parametersfile, workflowfile, worksheetfile, protocoldir, workingdir;
	protected String outputdir, templatedir, backend;
	protected Hashtable<String, Object> userValues = new Hashtable<String, Object>();
	private List<ComputeTask> tasks = new ArrayList<ComputeTask>();
	private Worksheet worksheet;

	private void generateJobs() throws Exception {
		computeBundle = new ComputeBundleFromDirectory(this);

		//
		// Append the commandline params to the list of ComputeParamters, so we
		// can use them in Protocols.
		//
		ComputeParameter McDir = new ComputeParameter();
		McDir.setName("McDir");
		McDir.setDefaultValue(userValues.get("McDir").toString());
		computeBundle.addComputeParameter(McDir);
		ComputeParameter McId = new ComputeParameter();
		McId.setName("McId");
		McId.setDefaultValue(userValues.get("McId").toString());
		computeBundle.addComputeParameter(McId);
		ComputeParameter McParameters = new ComputeParameter();
		McParameters.setName("McParameters");
		McParameters.setDefaultValue(userValues.get("McParameters").toString());
		computeBundle.addComputeParameter(McParameters);
		ComputeParameter McProtocols = new ComputeParameter();
		McProtocols.setName("McProtocols");
		McProtocols.setDefaultValue(userValues.get("McProtocols").toString());
		computeBundle.addComputeParameter(McProtocols);
		ComputeParameter McTemplates = new ComputeParameter();
		McTemplates.setName("McTemplates");
		McTemplates.setDefaultValue(userValues.get("McTemplates").toString());
		computeBundle.addComputeParameter(McTemplates);
		ComputeParameter McWorkflow = new ComputeParameter();
		McWorkflow.setName("McWorkflow");
		McWorkflow.setDefaultValue(userValues.get("McWorkflow").toString());
		computeBundle.addComputeParameter(McWorkflow);
		ComputeParameter McWorksheet = new ComputeParameter();
		McWorksheet.setName("McWorksheet");
		McWorksheet.setDefaultValue(userValues.get("McWorksheet").toString());
		computeBundle.addComputeParameter(McWorksheet);
		ComputeParameter McScripts = new ComputeParameter();
		McScripts.setName("McScripts");
		McScripts.setDefaultValue(userValues.get("McScripts").toString());
		computeBundle.addComputeParameter(McScripts);

		this.worksheet = new Worksheet(computeBundle);

		List<ComputeProtocol> protocollist = computeBundle.getComputeProtocols();

		// create hash of all workflow elements (needed for dependencies)
		Map<String, WorkflowElement> wfeMap = new HashMap<String, WorkflowElement>();
		for (WorkflowElement wfe : computeBundle.getWorkflowElements()) {
			wfeMap.put(wfe.getName(), wfe);
		}

		// process workflow elements
		for (WorkflowElement wfe : computeBundle.getWorkflowElements()) {
			print("Starting generation of workflow element: " + wfe.getName());

			// get protocol and find its targets
			ComputeProtocol protocol = findProtocol(wfe.getProtocol_Name(), protocollist);

			// get template + insert header and footer
			String scripttemplate = addHeaderFooter(protocol.getScriptTemplate(), protocol.getScriptInterpreter());

			// fold and reduce worksheet
			// String[] targets = parseHeaderElement(FOREACH, scripttemplate);
			List<String> targets = protocol.getIterateOver_Name();
			if (0 == targets.size()) {
				targets.add("line_number");
			}

			// add path to loader
			// FileTemplateLoader ftl1 = new
			// FileTemplateLoader(this.workflowdir);

			// Configuration cfg = new Configuration();
			// cfg.setDirectoryForTemplateLoading(this.protocoldir);
			// String documentation = protocol.getDescription();
			// Template template = new Template("a template", new
			// StringReader(documentation), cfg);
			// StringWriter filledtemplate = new StringWriter();
			// template.process(parameters, filledtemplate);

			// System.out.println(">>> " + documentation);
			// System.out.println(">>> " + filledtemplate.toString());

			List<Tuple> folded = Worksheet.foldWorksheet(this.worksheet.worksheet, this.computeBundle.getComputeParameters(), targets);
			// Worksheet.reduceTargets(targets);
			// print("folded worksheet: " + this.worksheet.folded.size() + ":" +
			// this.worksheet.folded.toString());
			// print("reduced worksheet: " + this.worksheet.reduced.size() + ":"
			// + this.worksheet.reduced.toString());

			// each element of reduced worksheet produces one
			// protocolApplication (i.e. a script)
			for (Tuple work : folded) {
				// fill template with work and put in script
				ComputeTask job = new ComputeTask();
				job.setName(this.generateJobName(wfe, work));
				job.setInterpreter(protocol.getScriptInterpreter());

				// if walltime, cores, mem not specified in protocol, then use
				// value from worksheet

				String walltime = (protocol.getWalltime() == null ? worksheet.getdefaultvalue("walltime") : protocol.getWalltime());
				// job.setWalltime(walltime);
				work.set("walltime", walltime);

				// String queue = (protocol.getClusterQueue() == null ?
				// worksheet.getdefaultvalue("clusterQueue")
				// : protocol.getClusterQueue());
				// FIXME: Here I make queue dependent on walltime and memory per
				// node..., which is specifically for Millipede..
				// This you can find out on the cluster
				Integer cores = (protocol.getCores() == null ? Integer.parseInt(worksheet.getdefaultvalue("cores")) : protocol.getCores());
				String mem = (protocol.getMem() == null ? worksheet.getdefaultvalue("mem").toString() : protocol.getMem().toString());
				// int m = Integer.parseInt(mem); // memory in GB?
				// queue = (4 < m && 2 < cores ? "quads" : "nodes");

				// int wt_h = Integer.parseInt(walltime.substring(0, 2));
				// int wt_m = Integer.parseInt(walltime.substring(3, 5));
				// int wt_s = Integer.parseInt(walltime.substring(6, 8));
				// if (lessOrEqualThan(24, wt_h, wt_m, wt_s)) {
				// done
				// } else if (lessOrEqualThan(72, wt_h, wt_m, wt_s)) {
				// queue = queue + "medium";
				// } else if (lessOrEqualThan(240, wt_h, wt_m, wt_s)) {
				// queue = queue + "long";
				// } else
				// throw new Exception("Walltime too large: " + walltime +
				// ". Maximum is 240h.");

				// work.set("clusterQueue", queue);
				work.set("cores", cores);
				work.set("mem", mem + "gb");
				// done with FIXME

				job.setInterpreter(protocol.getScriptInterpreter() == null ? worksheet.getdefaultvalue("interpreter") : protocol.getScriptInterpreter());

				// set jobname. If a job starts/completes, we put this in a
				// logfile
				work.set("jobname", job.getName());

				// record in worksheet job names for each element
				// (in column with same name as wfe)
				// this.worksheet.set(targets, work, wfe.getName(),
				// job.getName());

				// retrieve previousSteps
				for (String previousStep : wfe.getPreviousSteps_Name()) {
					// get the WorkflowElement of previous step
					WorkflowElement previousWfe = wfeMap.get(previousStep);

					ComputeProtocol wfeProtocol = findProtocol(previousWfe.getProtocol_Name(), computeBundle.getComputeProtocols());

					// see how long the list is
					int size = 1;
					for (String target : wfeProtocol.getIterateOver_Name()) {
						if (work.getObject(target) instanceof List) {
							size = work.getList(target).size();
							// BUG? What if user puts lists of different length
							// in worksheet?
							break;
						}
					}

					// we calculate dependencies
					Set<String> dependencies = new HashSet<String>();
					for (int i = 0; i < size; i++) {
						String jobName = previousWfe.getName();
						for (String target : wfeProtocol.getIterateOver_Name()) {
							if (work.getObject(target) instanceof List)
								// replace target by number
								jobName += "_" + work.getList(target).get(i);
							// jobName += "_XXX" + i;
							else
								jobName += "_" + work.getString(target);
							// jobName += "_YYY";
						}
						dependencies.add(stepnr(previousWfe.getName()) + jobName);
					}
					job.getPrevSteps_Name().addAll(dependencies);
				}

				// add the script
				job.setComputeScript(filledtemplate(scripttemplate, work, job.getName()));

				this.tasks.add(job);

				print("Generated " + job.getName() + ", depending on " + job.getPrevSteps_Name());
			}
		}

		// UNCOMMENT THE FOLLOWING CODE IF YOU WANT: as a last step add a job
		// that writes a "pipeline.finished" file
		/*
		 * ComputeTask job = new ComputeTask();
		 * job.setName(getworkflowfilename()); job.setInterpreter("bash");
		 * 
		 * // if walltime, cores, mem not specified in protocol, then use value
		 * from worksheet job.setWalltime("00:00:10"); job.setCores(1);
		 * job.setMem(1);
		 * 
		 * // final job is dependent on all other jobs Set<String> dependencies
		 * = new HashSet<String>(); for (ComputeTask cj : this.jobs) {
		 * dependencies.add(cj.getName()); }
		 * job.getPrevSteps_Name().addAll(dependencies);
		 * 
		 * // add the script job.setComputeScript("touch $PBS_O_WORKDIR" +
		 * File.separator + getworkflowfilename() + ".finished");
		 * 
		 * this.jobs.add(job);
		 * 
		 * // print("compute parameters: " +
		 * computeBundle.getComputeParameters().toString()); //
		 * print("user parameters: " + computeBundle.getUserParameters()); //
		 * print("full worksheet: " + computeBundle.getWorksheet());
		 */
	}

	private boolean lessOrEqualThan(int min, int wt_h, int wt_m, int wt_s) {
		return (wt_h < min || (wt_h == min && wt_m == 0 && wt_s == 0));
	}

	private String addHeaderFooter(String scripttemplate, String interpreter) {
		// THIS SHOULD BE REPLACED WITH TEMPLATES:

		String ls = System.getProperty("line.separator");

		scripttemplate = "<#include \"Header.ftl\"/>" + scripttemplate + ls + "<#include \"Footer.ftl\"/>";
		// + "<#include \"Macros.ftl\"/>" + ls
		// + "<@begin/>" + ls
		// + (interpreter.equalsIgnoreCase("R") ? "<@Rbegin/>" + ls : "")
		// + scripttemplate
		// + (interpreter.equalsIgnoreCase("R") ? "<@Rend/>" + ls : "")
		// + "<@end/>" + ls;

		return (scripttemplate);
	}

	private String stepnr(String wfeName) {
		// retrieve step number of wfeName in total workflow

		List<WorkflowElement> workflow = computeBundle.getWorkflowElements();
		for (int i = 0; i < workflow.size(); i++) {
			if (wfeName.equalsIgnoreCase(workflow.get(i).getName())) {
				return ("s" + (i < 10 ? "0" : "") + i + "_");
			}
		}

		return null;
	}

	private String generateJobName(WorkflowElement wfe, Tuple tuple) {
		String jobName = wfe.getName();
		ComputeProtocol wfeProtocol = findProtocol(wfe.getProtocol_Name(), computeBundle.getComputeProtocols());

		// in case no targets, we number
		List<String> targets = wfeProtocol.getIterateOver_Name();
		if (0 == targets.size()) {
			jobName += "_" + tuple.getString("line_number");
		}
		// otherwise use targets
		else
			for (String target : targets) {
				jobName += "_" + tuple.getString(target);
			}

		return stepnr(wfe.getName()) + jobName;
	}

	private String filledtemplate(String scripttemplate, Tuple work, String jobname) throws IOException, TemplateException {
		// first create map
		Map<String, Object> parameters = new HashMap<String, Object>();

		// add the helper
		parameters.put("freemarkerHelper", new FreemarkerHelper(this.computeBundle));
		parameters.put("parameters", work);
		parameters.put("workflowElements", this.computeBundle.getWorkflowElements());

		for (String field : work.getFields()) {
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
		// String script = "\n#####\n";
		// script = script + "## The following ${parameters} are values:\n";
		// script = script + "##   - " + worksheet.foldon + " " +
		// worksheet.getConstants() + "\n";
		// script = script +
		// "## The following parameters are lists, <#list parameters as p>${p}</#list> \n";
		// script = script + "##   - " + worksheet.list + "\n";
		// script = script + "#####\n\n";
		// script = script + filledtemplate.toString();

		return filledtemplate.toString();
	}

	private ComputeProtocol findProtocol(String protocol_name, List<ComputeProtocol> protocollist) {
		for (ComputeProtocol c : protocollist) {
			if (c.getName().equalsIgnoreCase(protocol_name))
				return c;
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

	public static void main(String[] args) {
		Options opt = new Options(args, Options.Prefix.DASH, Options.Multiplicity.ONCE, 0);

		opt.getSet().addOption("parameters", false, Options.Separator.EQUALS);
		opt.getSet().addOption("workflow", false, Options.Separator.EQUALS);
		opt.getSet().addOption("worksheet", false, Options.Separator.EQUALS);
		opt.getSet().addOption("protocols", false, Options.Separator.EQUALS);
		opt.getSet().addOption("templates", false, Options.Separator.EQUALS);
		opt.getSet().addOption("scripts", false, Options.Separator.EQUALS);
		opt.getSet().addOption("id", false, Options.Separator.EQUALS);
		// Directory where MOLGENIS/compute the commandline version was
		// installed.
		// This param is not specified by the user on the commandline but
		// automagically determined by molgenis_compute.sh,
		// which prepends it to the params specified by the user.
		opt.getSet().addOption("mcdir", false, Options.Separator.EQUALS);
		// Disabled until multiple backend support and use of the templates dir
		// is re-enabled.
		// opt.getSet().addOption("grid", false, Options.Separator.EQUALS,
		// Options.Multiplicity.ZERO_OR_ONE);
		// opt.getSet().addOption("cluster", false, Options.Separator.EQUALS,
		// Options.Multiplicity.ZERO_OR_ONE);

		// boolean isCorrect = opt.check();
		boolean isCorrect = opt.check(opt.getSet().getSetName(), false, false);

		if (!isCorrect) {
			System.out.println(opt.getCheckErrors());

			// Location of scripts on backend currently not used on cluster
			// If this changes re-enable:
			// "-grid|cluster=<LocationOfScriptsOnBackend(Grid or Cluster)>\n" +
			System.out.println("command line format:\n" + "-worksheet=<InputWorksheet.csv>\n" + "-parameters=<InputParameters.csv>\n" + "-workflow=<InputWorkflow.csv>\n"
					+ "-protocols=<InputProtocolsDir>\n" + "-templates=<InputTemplatesDir>\n" + "-scripts=<OutputScriptsDir>\n" + "-id=<ScriptGenerationID>\n");
			System.exit(1);
		}

		ComputeCommandLine ccl = new ComputeCommandLine();

		ccl.parametersfile = new File(opt.getSet().getOption("parameters").getResultValue(0));
		ccl.workflowfile = new File(opt.getSet().getOption("workflow").getResultValue(0));
		ccl.worksheetfile = new File(opt.getSet().getOption("worksheet").getResultValue(0));
		ccl.protocoldir = new File(opt.getSet().getOption("protocols").getResultValue(0));
		ccl.templatedir = opt.getSet().getOption("templates").getResultValue(0);
		ccl.outputdir = opt.getSet().getOption("scripts").getResultValue(0);

		// Disabled until multiple backend support and use of the templates dir
		// is re-enabled.
		// if (opt.getSet().isSet(WorkflowGeneratorCommandLine.GRID))
		// {
		// System.out.println("generation for grid");
		// ccl.backend = WorkflowGeneratorCommandLine.GRID;
		// ccl.userValues.put("outputdir",
		// opt.getSet().getOption(WorkflowGeneratorCommandLine.GRID).getResultValue(0));
		// }
		// else if (opt.getSet().isSet(WorkflowGeneratorCommandLine.CLUSTER))
		// {
		// System.out.println("generation for cluster");
		// ccl.backend = WorkflowGeneratorCommandLine.CLUSTER;
		// ccl.userValues.put("outputdir",
		// opt.getSet().getOption(WorkflowGeneratorCommandLine.CLUSTER).getResultValue(0));
		// }

		System.out.println("Script generation for PBS clusters.");
		// ccl.backend = WorkflowGeneratorCommandLine.CLUSTER;
		ccl.backend = "cluster";

		ccl.userValues.put("McDir", opt.getSet().getOption("mcdir").getResultValue(0));
		ccl.userValues.put("McId", opt.getSet().getOption("id").getResultValue(0));
		ccl.userValues.put("McParameters", opt.getSet().getOption("parameters").getResultValue(0));
		ccl.userValues.put("McProtocols", opt.getSet().getOption("protocols").getResultValue(0));
		ccl.userValues.put("McTemplates", opt.getSet().getOption("templates").getResultValue(0));
		ccl.userValues.put("McWorkflow", opt.getSet().getOption("workflow").getResultValue(0));
		ccl.userValues.put("McWorksheet", opt.getSet().getOption("worksheet").getResultValue(0));
		ccl.userValues.put("McScripts", opt.getSet().getOption("scripts").getResultValue(0));

		ccl.workingdir = new File(".");

		try {
			ccl.generateJobs();
			ccl.copyWorksheetAndWorkflow();
			ccl.generateScripts();
			// ccl.generateRite();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		print("Finished with generation!");
		System.exit(0);
	}

	private void copyWorksheetAndWorkflow() {
		try {
			for (File f : Arrays.asList(this.workflowfile, this.worksheetfile, this.parametersfile)) {
				String sourcepath = f.toString();
				String[] filenamelist = sourcepath.split(File.separator);
				String filename = filenamelist[filenamelist.length - 1];
				// Files.copy(f, new File(this.outputdir + File.separator +
				// filename));
				String destinationpath = new String(this.outputdir + File.separator + filename);
				destinationpath = destinationpath.replaceAll(File.separator + "+", File.separator);
				if (!destinationpath.equals(sourcepath)) {
					FileUtils.copyFile(f, new File(this.outputdir + File.separator + filename));
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private String getworkflowfilename() {
		String[] workflowfilenamelist = this.workflowfile.toString().split(File.separator);
		String f = workflowfilenamelist[workflowfilenamelist.length - 1];

		// replace dots with underscore, because qsub does not allow for dots in
		// job names or so...
		f = f.replace('.', '_');

		return f;
	}

	/**
	 * generate for rite
	 * 
	 * @throws RiteException
	 **/
	/*
	 * private void generateRite() throws RiteException { MongoRecipeStore msr =
	 * new MongoRecipeStore("localhost", 27017, "testsh", "recipes");
	 * 
	 * for (ComputeTask job : this.jobs) { Recipe r = new Recipe(job.getName());
	 * Step s = new Step(""); BashOperation bsho = new BashOperation();
	 * bsho.setScript(job.getComputeScript()); s.add(bsho); r.add(s);
	 * 
	 * if (0 < job.getPrevSteps_Name().size()) { for (String previous :
	 * job.getPrevSteps_Name()) { r.addDependency(previous); } }
	 * 
	 * msr.putRecipe(r); }
	 * 
	 * /* r = new Recipe("hello world2"); r.addDependency("hello world"); s =
	 * new Step("bla"); bsho = new BashOperation();
	 * bsho.setScript("echo Hello to you!"); s.add(bsho); r.add(s);
	 * 
	 * msr.putRecipe(r);
	 */
	/* } */

	/** Convert all compute jobs into scripts + submit.sh */
	private void generateScripts() {
		new File(outputdir).mkdirs();

		// extra: custom
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("jobs", tasks);
		params.put("workflowfilename", this.getworkflowfilename());

		String result = new FreemarkerView(this.protocoldir + File.separator + "CustomSubmit.sh.ftl", params).render();

		try {
			FileUtils.write(new File(outputdir + File.separator + "submitCustom.sh"), result);

			// and produce submit.sh
			PrintWriter submitWriter = new PrintWriter(new File(outputdir + File.separator + "submit.sh"));

			// also produce a runlocal.sh
			PrintWriter submitWriterLocal = new PrintWriter(new File(outputdir + File.separator + "runlocal.sh"));

			// touch "workflow file name".started in same directory as
			// submit.sh, when starting submit.sh
			String cmd = "DIR=\"$( cd \"$( dirname \"${BASH_SOURCE[0]}\" )\" && pwd )\"";
			submitWriter.println(cmd);
			submitWriterLocal.println(cmd);
			cmd = "touch $DIR" + File.separator + getworkflowfilename() + ".started";
			submitWriter.println(cmd);
			submitWriterLocal.println(cmd);

			//
			// Temporary hack for executing scripts with runlocal hence directly
			// without using a scheduler like PBS.
			// To prevent lots of errors due to scripts trying to write various
			// *.log, *.out, *.err, etc. files in the $PBS_O_WORKDIR,
			// we set $PBS_O_WORKDIR to the same directory as where runlocal.sh
			// resides.
			//
			cmd = "export PBS_O_WORKDIR=${DIR}";
			submitWriterLocal.println(cmd);

			for (ComputeTask job : this.tasks) {
				// create submit in submit.sh
				String dependency = "";
				if (job.getPrevSteps_Name().size() > 0) {
					dependency = "-W depend=afterok";

					for (String previous : job.getPrevSteps_Name()) {
						dependency += ":$" + previous;
					}
				}

				// do stuff for submit.sh
				submitWriter.println("#" + job.getName());
				submitWriter.println(job.getName() + "=$(qsub -N " + job.getName() + " " + dependency + " " + job.getName() + ".sh)");
				submitWriter.println("echo $" + job.getName());
				submitWriter.println("sleep 8");

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

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void print(String string) {
		System.out.println(">> " + string);
	}
}
