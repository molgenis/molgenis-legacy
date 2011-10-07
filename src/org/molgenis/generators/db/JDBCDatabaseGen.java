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
		
		templateArgs.put("db_filepath", options.db_filepath);
		templateArgs.put("loginclass", options.auth_loginclass);
		templateArgs.put("auth_redirect", options.auth_redirect);
		templateArgs.put("databaseImp", options.mapper_implementation.equals(MolgenisOptions.MapperImplementation.JPA) ? "jpa" : "jdbc");
		templateArgs.put("db_mode", options.db_mode);
		templateArgs.put("generate_BOT", options.generate_BOT);
		templateArgs.put("db_driver", options.db_driver);
		templateArgs.put("db_uri", options.db_uri);
		templateArgs.put("db_user", options.db_user);
		templateArgs.put("db_password", options.db_password);
		
		templateArgs.put("model", model );
		templateArgs.put("entities",entityList);
		templateArgs.put("package", APP_DIR);
		templateArgs.put("auth_loginclass", options.auth_loginclass);
		templateArgs.put("decorator_overriders", options.decorator_overriders);
		templateArgs.put("disable_decorators", options.disable_decorators);
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}

}
