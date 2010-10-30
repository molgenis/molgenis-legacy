package org.molgenis.generators.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.generators.db.JpaMapperGen;
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
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( "/"+this.getClass().getSimpleName()+".R.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);
		
		//File targetFile = new File( this.getSourcePath(options) + model.getName().replace(".","/") + "/source.R" );
		File targetFile = new File( this.getSourcePath(options) + "app/servlet/source.R" );
		targetFile.getParentFile().mkdirs();
		
		templateArgs.put( "model", model );
				templateArgs.put("template", template.getName());
		templateArgs.put("file", targetFile.toString());	
		OutputStream targetOut = new FileOutputStream( targetFile );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + targetFile);
	}
}
