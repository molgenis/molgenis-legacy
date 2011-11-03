package org.molgenis.compute.commandline;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.molgenis.util.cmdline.CmdLineException;
import org.molgenis.util.cmdline.CmdLineParser;
import org.molgenis.util.cmdline.Option;

/**
 * Option to parameterize the {@link Compute}
 * 
 *  We build on the 'org.molgenis.util.cmdline framework to make this easier'.
 */
public class ComputeCmdOptions
{
	/**
	 * Initialize with the defaults
	 */
	public ComputeCmdOptions()
	{
		
	}
	
	
	/** relative path to workflowDir */
	@Option(name = "workflowDir", param = Option.Param.DIRPATH, type = Option.Type.REQUIRED_ARGUMENT, usage = "Path to directory with your workflow.txt, parameters.txt, protocol folder, etc.")
	public ArrayList<String> model_database = new ArrayList<String>();

	/** relative path to the ui.xml file */
	@Option(name = "model_userinterface", param = Option.Param.FILEPATH, type = Option.Type.REQUIRED_ARGUMENT, usage = "File with user interface specification (in MOLGENIS DSL). Can be same file as model_database. Default: ''")
	public String model_userinterface = "";



	/**
	 * Get the options as a map, used in the UsedMolgenisOptionsGen.ftl template
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getOptionsAsMap() throws Exception
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		// use reflection to get the Fields
		Field[] fields = this.getClass().getDeclaredFields();

		for (int i = 0; i < fields.length; i++)
		{
			// only include the annotated fields
			if (fields[i].isAnnotationPresent(Option.class))
			{
				Option opt = fields[i].getAnnotation(Option.class);
				if (opt.param() == Option.Param.PASSWORD)
				{
					result.put(opt.name(), "xxxxxx");
				}
				else
				{
					result.put(opt.name(), fields[i].get(this));
				}
			}
		}
		return result;
	}

	/**
	 * Initialize options from properties file
	 * 
	 * @param propertiesFile
	 *            the path string to molgenis.properties file
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CmdLineException
	 */
//	public ComputeCmdOptions(String propertiesFile) throws FileNotFoundException,
//			IOException, CmdLineException
//	{
//		this.molgenis_properties = propertiesFile;
//		Properties props = new Properties();
//		try
//		{
//			// try to load from local files
//			props.load(new FileInputStream(propertiesFile.trim()));
//		}
//		catch (FileNotFoundException e)
//		{
//			try
//			{
//				// try to load from classpath
//				props.load(ClassLoader.getSystemResourceAsStream(propertiesFile
//						.trim()));
//			}
//			catch (Exception e2)
//			{
//				throw new IOException("couldn't find file "
//						+ new File(propertiesFile).getAbsolutePath());
//			}
//
//		}
//
//		CmdLineParser parser = new CmdLineParser(this);
//		parser.parse(props);
//		// System.out.println("Mapper implementation molgenis name: " +
//		// this.mapper_implementation.name());
//
//		// if (new File(propertiesFile).getParentFile() != null)
//		// {
//		// this.path = new
//		// File(propertiesFile).getParentFile().getAbsolutePath() + "/";
//		// }
//		Logger.getLogger(this.getClass().getSimpleName()).debug(
//				"parsed properties file.");
//	}

	/**
	 * Initialize options from properties object
	 * 
	 * @param properties
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws CmdLineException
	 */
	public ComputeCmdOptions(Properties properties)
	{
		CmdLineParser parser;
		try
		{
			parser = new CmdLineParser(this);
			parser.parse(properties);
			Logger.getLogger(this.getClass().getSimpleName()).debug(
					"parsed properties file.");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Cannot find property file: "
					+ e.getMessage());
		}
	}

	public String toString()
	{
		try
		{
			return new CmdLineParser(this).toString(this);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (CmdLineException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
}
