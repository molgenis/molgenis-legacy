package org.molgenis.generators.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class RApiGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(RApiGen.class);

	public String getDescription()
	{
		return "Generates a R file that sources all R files.";
	}

	@Override
	public void generate(Model model, MolgenisOptions options) throws Exception
	{
		String findAPIlocation = null;
		for (String s : options.services)
		{
			String service = s.split("@")[0];
			if (service.equals("org.molgenis.framework.server.services.MolgenisDownloadService"))
			{
				findAPIlocation = s.split("@")[1];
				break;
			}
		}
		if (findAPIlocation == null)
		{
			throw new Exception("You cannot use the R API without MolgenisDownloadService mapped as a service!");
		}

		String addAPIlocation = null;
		for (String s : options.services)
		{
			String service = s.split("@")[0];
			if (service.equals("org.molgenis.framework.server.services.MolgenisUploadService"))
			{
				addAPIlocation = s.split("@")[1];
				break;
			}
		}
		if (addAPIlocation == null)
		{
			throw new Exception("You cannot use the R API without MolgenisUploadService mapped as a service!");
		}

		Template template = createTemplate("/" + this.getClass().getSimpleName() + ".R.ftl");
		Map<String, Object> templateArgs = createTemplateArguments(options);

		// File targetFile = new File( this.getSourcePath(options) +
		// model.getName().replace(".","/") + "/source.R" );
		File targetFile = new File(this.getSourcePath(options) + "app/servlet/source.R");
		targetFile.getParentFile().mkdirs();

		templateArgs.put("model", model);
		templateArgs.put("template", template.getName());
		templateArgs.put("file", targetFile.toString());
		templateArgs.put("findAPIlocation", findAPIlocation);
		templateArgs.put("addAPIlocation", addAPIlocation);
		OutputStream targetOut = new FileOutputStream(targetFile);
		template.process(templateArgs, new OutputStreamWriter(targetOut));
		targetOut.close();

		logger.info("generated " + targetFile);
	}
}
