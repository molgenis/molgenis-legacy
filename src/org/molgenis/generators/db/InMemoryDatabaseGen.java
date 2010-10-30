package org.molgenis.generators.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.generators.csv.CsvImportGen;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class InMemoryDatabaseGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(InMemoryDatabaseGen.class);

	@Override
	public String getDescription()
	{
		return "Generates one InMemoryDatabase to use as blackboard. ";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( "/"+getClass().getSimpleName()+".java.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);
		
		List<Entity> entityList = model.getEntities();
		//this.sortEntitiesByXref(entityList,model); //side effect?


		//File target = new File( this.getSourcePath(options) + model.getName().replace(".","/") + "/data/InMemoryDatabase.java" );
		File target = new File( this.getSourcePath(options) + "app/InMemoryDatabase.java" );

		target.getParentFile().mkdirs();
		
		templateArgs.put("model", model );
		templateArgs.put("entities",entityList);
		String packageName = "app";
		templateArgs.put("package", packageName);
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}

}
