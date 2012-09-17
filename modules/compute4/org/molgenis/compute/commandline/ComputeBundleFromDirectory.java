package org.molgenis.compute.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class ComputeBundleFromDirectory extends ComputeBundle
{
	Logger logger = Logger.getLogger(ComputeBundleFromDirectory.class);

	/**
	 * Minimal constructor only requiring path to workflow directory + worksheet
	 * file. This uses defaults:
	 * <ul>
	 * <li>workflowDir/parameters.txt -> loads ComputeParameter
	 * <li>workflowDir/workflow.txt -> loads WorkflowElement
	 * <li>workflowDir/protocols -> reads all ftl in that directory into
	 * protocol
	 * <li>workflowDir/workflowelementparameters.txt -> optional: read
	 * WorkflowElementParameter
	 * <li>(DISCUSSION: can we not merge with ComputeParameter???)
	 * </ul>
	 * 
	 * @param workflowDir
	 * @param worksheetFile
	 * @throws Exception
	 */
	public ComputeBundleFromDirectory(ComputeCommandLine options)
			throws Exception
	{
		// load files
		this.setComputeParameters(options.parametersfile);
		this.setWorkflowElements(options.workflowfile);

		// put names of workflowElement into parameters (so we can refer to them
		// in worksheet for dependencies)
		// for(WorkflowElement wfe: this.getWorkflowElements())
		// {
		// ComputeParameter pm = new ComputeParameter();
		// pm.setName(wfe.getName());
		// this.getComputeParameters().add(pm);
		// }

		// try
		// {
		// this.setWorkflowElementParameters(new
		// File(workflowDir.getAbsolutePath() + File.separator
		// + "workflowparameters.txt"));
		// }
		// catch (Exception e)
		// {
		// // if file does not exists, do nothing
		// }
		// read user parameters
		this.setWorksheet(options.worksheetfile);

		// combine computeParameters (parameters.txt) and userParameters
		// (worksheetFile)
		// into one file, called worksheet
		// this.fillWorksheet();

		// load the protocols
		this.setComputeProtocols(options.protocoldir);

	}

	// private void fillWorksheet()
	// {
	// List<ComputeParameter> parameterlist = getComputeParameters(); //
	// parameters.txt
	// List<Tuple> userworksheet = getUserParameters(); // original user
	// worksheet
	//
	// Map<String, String> parameters = new HashMap<String, String>();
	//
	// // novel worksheet that combines user worksheet with parameters
	// List<Tuple> worksheet = new ArrayList<Tuple>();
	//
	// // fill worksheet and iteratively substitute values that point to
	// parameters
	// for (Tuple usertuple : userworksheet)
	// {
	// // first put all parameters/values in map
	// parameters.clear();
	//
	// // add parameters.txt fields to parameters
	// for (ComputeParameter cp : parameterlist)
	// {
	// String field = cp.getName();
	// String value = cp.getDefaultValue();
	// // only add if it not exists yet
	// if (!parameters.containsKey(field)) {
	// parameters.put(cp.getName(), value == null ? "" : value);
	// } else {
	// // // check whether existing value is empty
	// // // if not: error
	// // if (value != null && value != "") {
	// throw new RuntimeException("Parameter " + field +
	// " occurs > 1 times in your parameter.txt file.");
	// // }
	// }
	// }
	//
	// // add user worksheet values to wt, for this tuple
	// for (String field : usertuple.getFields())
	// {
	// String value = usertuple.getString(field);
	// parameters.put(field, value == null ? "" : value);
	// }
	//
	// // iteratively substitute values that point to parameters
	// String fieldtemplate, value, originalvalue;
	// Template template;
	// Configuration conf = new Configuration();
	// StringWriter filledtemplate;
	//
	// boolean done = false;
	// while (!done)
	// {
	// boolean updated = false;
	// for (String field : parameters.keySet())
	// {
	// try
	// {
	// // do substitution for field
	// originalvalue = parameters.get(field);
	// fieldtemplate = new String(originalvalue == null ? "" : originalvalue);
	// template = new Template(field, new StringReader(fieldtemplate), conf);
	// filledtemplate = new StringWriter();
	// template.process(parameters, filledtemplate);
	//
	// value = filledtemplate.toString();
	//
	// if (!value.equalsIgnoreCase(originalvalue))
	// {
	// // update value of field in parameter list
	// parameters.put(field, value);
	// updated = true;
	// }
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	//
	// if (!updated) done = true; // nothing changed, so we're done
	// }
	//
	// // all values in parameters for this usertupele + parameters.txt are now
	// iteratively filled
	//
	// // put these parameters in worksheet tuple (wt)
	//
	// Tuple wt = new SimpleTuple();
	//
	// for (String field : parameters.keySet())
	// {
	// value = parameters.get(field);
	// wt.set(field, value == null ? "" : value);
	// }
	//
	// // add wt to worksheet
	// worksheet.add(wt);
	// }
	//
	// setWorksheet(worksheet);
	// }

	public void setWorksheet(File worksheetFile) throws Exception
	{
		this.setUserParameters(new WorksheetHelper()
				.readTuplesFromFile(worksheetFile));
		// copy missing parameters from worksheet header to ComputeParameter
		Tuple firstTuple = this.getUserParameters().get(0);
		for (String field : firstTuple.getFields())
		{
			if (this.getComputeParameter(field) == null)
			{
				ComputeParameter cp = new ComputeParameter();
				cp.setName(field);
				this.addComputeParameter(cp);
			}
		}
	}

	public ComputeBundleFromDirectory()
	{
	}

	public ComputeBundleFromDirectory(File workflowDir, File fileWorksheet)
	{
		// TODO Auto-generated constructor stub
	}

	public ComputeBundleFromDirectory(File parametersfile, File workflowfile,
			File worksheetfile, File protocoldir) throws Exception
	{
		this.setComputeParameters(parametersfile);
		this.setWorkflowElements(workflowfile);
		this.setWorksheet(worksheetfile);
		this.setComputeProtocols(protocoldir);
	}

	public void setComputeProtocols(File templateFolder) throws IOException
	{
		// assume each file.ftl in the 'protocols' folder to be a protocol
		List<ComputeProtocol> protocols = new ArrayList<ComputeProtocol>();
		for (File f : templateFolder.listFiles())
		{
			if (f.getName().endsWith(".ftl"))
			{
				ComputeProtocol p = new ComputeProtocol();
				p.setName(f.getName().replace(".ftl", ""));

				// parse out the headers and template
				String scriptTemplate = "";

				BufferedReader reader = new BufferedReader(new FileReader(f));
				String line = null;
				String ls = System.getProperty("line.separator");
				boolean foundHeader = false;

				while ((line = reader.readLine()) != null)
				{
					if (line.trim().startsWith("#MOLGENIS"))
					{
						foundHeader = true;

						String[] keyValues = line.substring(9).split(" ");
						// parse into tuple
						Tuple params = new SimpleTuple();
						for (String keyValue : keyValues)
						{
							if (!keyValue.equals(""))
							{
								String[] data = keyValue.split("=");

								if (data.length != 2) throw new RuntimeException(
										"error parsing file " + f
												+ ", keyValue ='" + keyValue
												+ "': " + line);

								String key = data[0];
								if (!p.getFields().contains(key))
								{
									throw new RuntimeException(
											"error parsing file " + f
													+ ", unknown key '" + key
													+ "' in line: " + line);
								}

								params.set(data[0], data[1]);
							}
						}
						// set the params
						p.setMem(params.getString("mem"));
						// p.setIterateOver_Name(params.getStringList("iterateOver"));
						p.setWalltime(params.getString("walltime"));
						p.setNodes(params.getInt("nodes"));
						p.setCores(params.getInt("cores"));
						//p.setClusterQueue(params.getString("clusterQueue"));
					}
					else if (line.trim().startsWith("#FOREACH"))
					{
						String thisline = line.substring("#FOREACH".length())
								.trim();
						if (thisline.length() > 0)
						{
							String[] targets = thisline.split(",");
							for (String target : targets)
							{
								p.getIterateOver_Name().add(target.trim());
							}
						}
					}
					else if (line.trim().startsWith("#DOCUMENTATION"))
					{
						String thisline = line.substring(
								"#DOCUMENTATION".length()).trim();
						if (0 < thisline.length())
						{
							p.setDescription(thisline.trim());
						}
					}
					else if (line.trim().startsWith("#INTERPRETER"))
					{
						String thisline = line.substring(
								"#INTERPRETER".length()).trim();
						if (0 < thisline.length())
						{
							p.setScriptInterpreter(thisline.trim());
						}
					}
					else if (line.trim().startsWith("#PBS"))
					{
						// ignored?

						// //check against lines without spaces to solve issue
						// of mulitiple spaces
						// if(line.replace(" ",
						// "").startsWith("#PBS-wwalltime="))
						// {
						// p.setWalltime(line.substring(line.indexOf("walltime=")+9));
						// }
						// else if(line.replace(" ",
						// "").startsWith("#PBS-lnodes="))
						// {
						// logger.error("TODO:" + line);
						// }
						// else if(line.replace(" ",
						// "").startsWith("#PBS-lmem="))
						// {
						// //convert to GB
						// String mem =
						// line.substring(line.indexOf("mem=")+4).trim();
						// if(mem.endsWith("Gb"))
						// {
						// Integer memValue =
						// Integer.parseInt(mem.substring(0,mem.length()-2));
						// p.setMemoryReq(memValue);
						// }
						// else
						// {
						// throw new
						// RuntimeException("error parsing file: memory should be in 'Gb': "+line);
						// }
						// }
						// else if(line.replace(" ",
						// "").startsWith("#MOLGENIStarget="))
						// {
						// p.setTargetType(line.substring(line.indexOf("target=")));
						// }
						// else
						// {
						// logger.error("parsing "+f.getName()+", line: "+line);
						// }
					}
					// else
					// {
					// also add the #LINES to the code!
					scriptTemplate += line + ls;
					// }
				}
				reader.close();

				if (!foundHeader) logger
						.warn("protocol "
								+ f
								+ " does not contain #MOLGENIS header. Defaults will be used");

				p.setScriptTemplate(scriptTemplate);

				protocols.add(p);
			}
		}
		this.setComputeProtocols(protocols);
	}

	public void setComputeParameters(File file) throws Exception
	{
		this.setComputeParameters(readEntitiesFromFile(file,
				ComputeParameter.class));
	}

	public void setWorkflowElements(File file) throws Exception
	{
		this.setWorkflowElements(readEntitiesFromFile(file,
				WorkflowElement.class));
	}

	private <E extends Entity> List<E> readEntitiesFromFile(File file,
			final Class<E> klazz) throws Exception
	{
		final List<E> result = new ArrayList<E>();

		// check if file exists
		if (!file.exists())
		{
			logger.warn("file '" + file.getName() + "' is missing");
			return result;
		}

		// read the file
		CsvReader reader = new CsvFileReader(file);
		for (Tuple tuple : reader)
		{
			E entity = klazz.newInstance();
			entity.set(tuple);
			result.add(entity);

		}

		return result;
	}

	private String readFileAsString(File file) throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

	// public static void main(String[] args) throws Exception
	// {
	// // just a test
	// File workflowDir = new File(
	// "/Users/mswertz/Dropbox/NGS quality report/compute/New_Molgenis_Compute_for_GoNL/Example_01");
	// File worksheetFile = new File(workflowDir.getAbsolutePath() +
	// File.separator + "SampleList_A102.csv");
	//
	// ComputeBundleFromDirectory bundle = new ComputeBundleFromDirectory();
	//
	// // print
	// bundle.prettyPrint();
	// }

}
