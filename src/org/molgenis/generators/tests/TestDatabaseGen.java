package org.molgenis.generators.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.model.MolgenisModel;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class TestDatabaseGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(TestDatabaseGen.class);

	@Override
	public String getDescription()
	{
		return "Generates simple junit test that creates a database and updates/queries data.";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( "/"+this.getClass().getSimpleName()+".java.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);
		
		List<Entity> entityList = model.getEntities();
		MolgenisModel.sortEntitiesByDependency(entityList,model); //side effect?

		File target = new File( this.getSourcePath(options) + "/test/TestDatabase.java" );
		target.getParentFile().mkdirs();
		
		String packageName = "test";
		
		templateArgs.put("databaseImp", options.mapper_implementation.equals(MolgenisOptions.MapperImplementation.JPA) ? "jpa" : "jdbc");		
		templateArgs.put("model", model );
		templateArgs.put("entities",entityList);
		templateArgs.put("package", packageName);
		
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}
}
