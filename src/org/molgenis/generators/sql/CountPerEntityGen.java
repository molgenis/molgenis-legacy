package org.molgenis.generators.sql;

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

public class CountPerEntityGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(CountPerEntityGen.class);

	@Override
	public String getDescription()
	{
		return "Generates one query to count all entities in the database (without counting subclasses twice).";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( getClass().getSimpleName()+".sql.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);

		File target = new File( this.getSqlPath(options) + "/count_per_entity.sql" );
		target.getParentFile().mkdirs();
		
		templateArgs.put( "model", model );
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}

}
