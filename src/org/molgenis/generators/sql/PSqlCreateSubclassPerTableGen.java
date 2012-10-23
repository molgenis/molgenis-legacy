package org.molgenis.generators.sql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.model.MolgenisModel;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class PSqlCreateSubclassPerTableGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(PSqlCreateSubclassPerTableGen.class);

	@Override
	public String getDescription()
	{
		return "Generates create tables and views for each entity.";
	}

	@Override
	public void generate(Model model, MolgenisOptions options) throws Exception
	{
		Template template = createTemplate("/" + this.getClass().getSimpleName() + ".psql.ftl");
		Map<String, Object> templateArgs = createTemplateArguments(options);

		List<Entity> entityList = model.getEntities();
		entityList = MolgenisModel.sortEntitiesByDependency(entityList, model); // side
																				// effect?

		File target = new File(this.getSqlPath(options) + "/create_tables.sql");
		target.getParentFile().mkdirs();

		templateArgs.put("model", model);
		templateArgs.put("entities", entityList);
		OutputStream targetOut = new FileOutputStream(target);
		template.process(templateArgs, new OutputStreamWriter(targetOut, Charset.forName("UTF-8")));
		targetOut.close();

		logger.info("generated " + target);
	}
}
