package org.molgenis.generators.csv;

import java.io.File;
import java.util.Map;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.testng.annotations.Test;

public class CsvExportTest
{

	@Test
	public void test2()
	{
		runTarget(new File("ant.xml"), "test", null);
	}

	// // @BeforeClass
	// // public static void setUpBeforeClass() throws Exception
	// // @BeforeMethod
	// @Test
	// public void setUpBeforeClass() throws Exception
	// {
	// Model model = new Model("model");
	// MolgenisOptions options = new MolgenisOptions();
	//
	// File generatedJavaFile = new File(
	// "/git/molgenis/generated/test/java/org/molgenis/generators/csv/CsvExport.java");
	//
	// boolean created = generatedJavaFile.getParentFile().mkdirs();
	// if (!created && !generatedJavaFile.getParentFile().exists())
	// {
	// throw new IOException("could not create " +
	// generatedJavaFile.getParentFile());
	// }
	//
	// OutputStream javaOs = new FileOutputStream(generatedJavaFile);
	// try
	// {
	// new CsvExportGen().generate(model, options, javaOs);
	// }
	// finally
	// {
	// javaOs.close();
	// }
	//
	// runTarget(new File("ant.xml"), "test", null);
	//
	// File buildFile = new File("ant.xml");
	// Project p = new Project();
	// p.setUserProperty("ant.file", buildFile.getAbsolutePath());
	// p.setProperty("")
	// p.init();
	// ProjectHelper helper = ProjectHelper.getProjectHelper();
	// p.addReference("ant.projectHelper", helper);
	// helper.parse(p, buildFile);
	// p.executeTarget(p.getDefaultTarget());
	//
	// JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
	// InputStream javaIs = new FileInputStream(generatedJavaFile);
	//
	// File generatedClassFile = new File(
	// "/git/molgenis/build/generated/test/org/molgenis/generators/csv/CsvExport.class");
	// created = generatedClassFile.getParentFile().mkdirs();
	// if (!created && !generatedClassFile.getParentFile().exists())
	// {
	// throw new IOException("could not create " +
	// generatedClassFile.getParentFile());
	// }
	//
	// OutputStream classOs = new FileOutputStream(generatedClassFile);
	// try
	// {
	// javaCompiler.run(null, null, null, "-cp",
	// System.getProperty("java.class.path")
	// + ";src;D:/git/molgenis/build/generated/test/java",
	// "org/molgenis/generators/csv/CsvExport.java");
	//
	// System.out.println(System.getProperty("java.class.path"));
	// // int returnCode = javaCompiler.run(null, null, null, "-cp", "lib",
	// // "-sourcepath",
	// // "D:/git/molgenis/generated/test/java/", "-d",
	// // "/git/molgenis/build/generated/test/org/molgenis/generators/csv/",
	// // "org.molgenis.generators.csv.CsvExport.java");
	// Collection<File> jarFiles = FileUtils.listFiles(new
	// File("D:/git/molgenis/lib"), new SuffixFileFilter(
	// ".jar"), null);
	// List<String> jarFileNames = new ArrayList<String>(jarFiles.size());
	// for (File jarFile : jarFiles)
	// jarFileNames.add(jarFile.getPath());
	// String classPath = StringUtils.join(jarFileNames, ';');
	// int returnCode = javaCompiler.run(null, null, null, "-cp", classPath
	// + ";D:/git/molgenis/src;D:/git/molgenis/generated/test/java/", "-Xlint",
	// "CsvExport.java");
	// // "D:/git/molgenis/generated/test/java/", "-d",
	// // "/git/molgenis/build/generated/test/org/molgenis/generators/csv/",
	// // "org.molgenis.generators.csv.CsvExport.java");
	// if (returnCode != 0) throw new IOException("compilation failed");
	// }
	// finally
	// {
	// classOs.close();
	// }
	// }

	/**
	 * LGPL
	 */
	public void runTarget(File buildFile, String targetName, Map<String, String> properties)
	{
		ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
		Project project = new Project();

		project.setUserProperty("ant.file", buildFile.getAbsolutePath());
		if (properties != null) for (String key : properties.keySet())
		{
			project.setProperty(key, properties.get(key));
		}
		project.init();

		project.addReference("ant.projectHelper", projectHelper);
		projectHelper.parse(project, buildFile);
		try
		{
			project.executeTarget(targetName);
		}
		catch (BuildException e)
		{
			throw new RuntimeException(String.format("Run %s [%s] failed: %s", buildFile, targetName, e.getMessage()),
					e);
		}
	}
	// @Test
	// public void generate() throws Exception
	// {
	// CsvExport csvExport = new CsvExport();
	// csvExport.exportAll(new File("blaat"), Mockito.mock(Database.class));
	// }
}
