package org.molgenis.generators.db;

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

public class DatabaseFactoryGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(DatabaseFactoryGen.class);

	@Override
	public String getDescription()
	{
		return "Generates a database factory.";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( "/"+getClass().getSimpleName()+".java.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);

		File target = new File( this.getSourcePath(options) + "/app/DatabaseFactory.java" );
		target.getParentFile().mkdirs();

		templateArgs.put("package", APP_DIR);
		templateArgs.put("databaseImp", options.mapper_implementation.equals(MolgenisOptions.MapperImplementation.JPA) ? "jpa" : "jdbc");
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}

}
