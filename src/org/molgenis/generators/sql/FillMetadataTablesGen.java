package org.molgenis.generators.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.DataTypeGen;
import org.molgenis.generators.Generator;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class FillMetadataTablesGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(FillMetadataTablesGen.class);

	@Override
	public String getDescription()
	{
		return "Fills the metadata tables.";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( getClass().getSimpleName()+".sql.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);

		String packageName = DataTypeGen.class.getPackage().toString().substring(Generator.class.getPackage().toString().length());
		File target = new File( this.getSqlPath(options) + "/insert_metadata.sql" );
		target.getParentFile().mkdirs();
		
		templateArgs.put( "model", model );
		templateArgs.put("package", model.getName().toLowerCase() + packageName);
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}

}
