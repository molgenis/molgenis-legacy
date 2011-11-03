package org.molgenis.compute.commandline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.WorkflowElementParameter;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.testng.log4testng.Logger;

public class ComputeBundleFromDirectory extends ComputeBundle
{
	Logger logger = Logger.getLogger(ComputeBundleFromDirectory.class);

	/**
	 * Minimal constructor only requiring path to workflow directory + worksheet
	 * file. This uses defaults:
	 * <ul>
	 * <li>workflowDir/parameters.txt -> loads ComputeParameter
	 * <li>workflowDir/workflow.txt -> loads WorkflowElement
	 * <li>workflowDir/protocol -> reads all ftl in that directory into protocol
	 * <li>workflowDir/workflowelementparameters.txt -> optional: read
	 * WorkflowElementParameter
	 * <li>(DISCUSSION: can we not merge with ComputeParameter???)
	 * </ul>
	 * 
	 * @param workflowDir
	 * @param worksheet
	 * @throws Exception
	 */
	public ComputeBundleFromDirectory(File workflowDir, File worksheetFile)
			throws Exception
	{
		// load files
		this.setComputeParameters(new File(workflowDir.getAbsolutePath()
				+ File.separator + "parameters.txt"));
		this.setWorkflowElements(new File(workflowDir.getAbsolutePath()
				+ File.separator + "workflow.txt"));
		this.setWorkflowElementParameters(new File(workflowDir.getAbsolutePath()
				+ File.separator + "workflowparameters.txt"));

		// read user parameters
		this.setUserParameters(new WorksheetHelper()
				.readTuplesFromFile(worksheetFile));

		// load the templates
		this.setComputeProtocols(new File(workflowDir.getAbsolutePath()
				+ File.separator + "protocols"));
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
						for (String keyValue : keyValues)
							if (!keyValue.equals(""))
							{
								String[] data = keyValue.split("=");

								if (data.length != 2) throw new RuntimeException(
										"error parsing file " + f
												+ ", keyValue ='" + keyValue
												+ "': " + line);

								if (data[0].equals("mem"))
								{
									p.setMem(Integer.parseInt(data[1]));
								}
								else if (data[0].equals("target"))
								{
									p.setTargetType(data[1]);
								}
								else if (data[0].equals("walltime"))
								{
									p.setWalltime(data[1]);
								}
								else if (data[0].equals("nodes"))
								{
									p.setCores(Integer.parseInt(data[1]));
								}
								else if (data[0].equals("cores"))
								{
									p.setCores(Integer.parseInt(data[1]));
								}
								else
								{
									throw new RuntimeException(
											"error parsing file " + f
													+ ", unknown key '"
													+ data[0] + "' in line: "
													+ line);
								}

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
					else
					{
						scriptTemplate += line + ls;
					}
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

	public void setWorkflowElementParameters(File file) throws Exception
	{
		this.setWorkflowElementParameters(readEntitiesFromFile(file,
				WorkflowElementParameter.class));
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
		reader.parse(new CsvReaderListener()
		{

			@Override
			public void handleLine(int line_number, Tuple tuple)
					throws Exception
			{
				E entity = klazz.newInstance();
				entity.set(tuple);
				result.add(entity);

			}
		});

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

	public static void main(String[] args) throws Exception
	{
		// just a test
		File workflowDir = new File(
				"/Users/mswertz/Dropbox/NGS quality report/compute/New_Molgenis_Compute_for_GoNL/Example_01");
		File worksheetFile = new File(workflowDir.getAbsolutePath() + File.separator + "SampleList_A102.csv");
		
		ComputeBundleFromDirectory bundle = new ComputeBundleFromDirectory(workflowDir, worksheetFile);

		// print
		bundle.prettyPrint();
	}

}
