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
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class JDBCDatabaseGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(JDBCDatabaseGen.class);

	@Override
	public String getDescription()
	{
		return "Generates one JDBCDatabase to talk to the data. Encapsulates Database Mappers to do this.";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( "/"+getClass().getSimpleName()+".java.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);
		
		List<Entity> entityList = model.getEntities();
		//this.sortEntitiesByXref(entityList,model); //side effect?
		File target = new File( this.getSourcePath(options) + APP_DIR +"/JDBCDatabase.java" );
		target.getParentFile().mkdirs();
		
		templateArgs.put("model", model );
		templateArgs.put("entities",entityList);
		templateArgs.put("package", APP_DIR);
		templateArgs.put("auth_loginclass", options.auth_loginclass);
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}

}
