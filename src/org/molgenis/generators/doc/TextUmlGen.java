package org.molgenis.generators.doc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.generators.db.JpaMapperGen;
import org.molgenis.model.MolgenisModel;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;
import org.molgenis.model.elements.Module;

import freemarker.template.Template;

public class TextUmlGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(TextUmlGen.class);

	@Override
	public String getDescription()
	{
		return "Generates textUML compatible file.";
	}
	
	@Override
	public void generate(Model model, MolgenisOptions options)
			throws Exception
	{
		Template template = createTemplate( "/"+getClass().getSimpleName()+".java.ftl" );
		Map<String, Object> templateArgs = createTemplateArguments(options);

		File target = new File(this.getDocumentationPath( options ) +"/textUML.txt");		
		target.getParentFile().mkdirs();
		
		List<Entity> entityList = model.getEntities();
		List<Module> moduleList = model.getModules();
		MolgenisModel.sortEntitiesByDependency(entityList,model); //side effect?
		
		templateArgs.put("model", model );
		templateArgs.put("entities",entityList);
		templateArgs.put("modules",moduleList);
		OutputStream targetOut = new FileOutputStream( target );
		template.process( templateArgs, new OutputStreamWriter( targetOut ) );
		targetOut.close();
		
		logger.info("generated " + target);
	}

}
