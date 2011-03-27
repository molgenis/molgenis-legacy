package miscellaneous;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;

public class FindUnmodifiedTemplatePlugins
{

	private int ftlLines = 50;
	private int javaLines = 100;
	
	private ArrayList<String> report = new ArrayList<String>();

	public FindUnmodifiedTemplatePlugins() throws IOException
	{
		File handwritten = new File("handwritten");
		ArrayList<String> report = recurse(handwritten);



		for (String s : report)
		{
			if(s.startsWith("unmodified")){
				System.out.println(s);
			}
		}
	}

	public ArrayList<String> recurse(File f) throws IOException
	{
		if (f.isDirectory())
		{

			if (f.getName().equals(".svn"))
			{
				// ignore .snv dirs
			}
			else
			{

				// see if dir has 3 files: 1 ftl, 1 java and 1 .svn
				if (f.list().length == 3)
				{
					ArrayList<String> fileNames = new ArrayList<String>();
					boolean ftlFile = false;
					boolean javaFile = false;
					for (String fname : f.list())
					{
						if (fname.endsWith(".ftl"))
						{
							ftlFile = true;
						}
						if (fname.endsWith(".java"))
						{
							javaFile = true;
						}
					}
					if (ftlFile && javaFile)
					{

						int ftlLines = -1;
						int javaLines = -1;
						for (File file : f.listFiles())
						{
							if (file.getName().endsWith(".ftl"))
							{
								ftlLines = lineNumberReader(file);
							}
							if (file.getName().endsWith(".java"))
							{
								javaLines = lineNumberReader(file);
							}
						}

//						System.out.println("dir with 1 ftl and 1 java file at " + f.getAbsolutePath() + " (ftl lines = " + ftlLines + ", java lines = " + javaLines + ")");

						if (ftlLines < this.ftlLines && javaLines < this.javaLines)
						{
							report.add(0, "unmodified template plugin at:" + f.getAbsolutePath());
						}else{

						if (ftlLines < this.ftlLines || javaLines < this.javaLines)
						{
							report.add("possible unmodified template plugin at:" + f.getAbsolutePath());
						}
						}

					}

				}

				for (File ff : f.listFiles())
				{
					recurse(ff);
				}

			}

		}
		else
		{
			// System.out.println("file at.. " + f.getAbsolutePath());
		}

		// System.out.println("looking at.. " + f.getAbsolutePath());

		return report;
	}

	private int lineNumberReader(File f) throws IOException
	{
		LineNumberReader lineCounter = new LineNumberReader(new InputStreamReader(new FileInputStream(f)));

		String nextLine = null;
		while ((nextLine = lineCounter.readLine()) != null)
		{
			if (nextLine == null) break;
		}

		return lineCounter.getLineNumber();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException
	{
		new FindUnmodifiedTemplatePlugins();

	}

}
