package org.molgenis.generators.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Method;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class SoapApiGen extends Generator
{
	public static final transient Logger logger = Logger.getLogger(SoapApiGen.class);

	@Override
	public String getDescription()
	{
		return "Generates soap service interfaces for each entity.";
	}

	@Override
	public void generate(Model model, MolgenisOptions options) throws Exception
	{
		Template template = createTemplate("/" + this.getClass().getSimpleName() + ".java.ftl");
		Map<String, Object> templateArgs = createTemplateArguments(options);

		List<Entity> entityList = model.getEntities();
		List<Method> methodList = model.getMethods();

		File target = new File(this.getSourcePath(options) + APP_DIR + "/servlet/SoapApi.java");
		target.getParentFile().mkdirs();

		templateArgs.put("model", model);
		templateArgs.put("methods", methodList);
		templateArgs.put("entities", entityList);
		templateArgs.put("helper", new GeneratorHelper(null));
		templateArgs.put("package", APP_DIR + ".servlet");
		OutputStream targetOut = new FileOutputStream(target);
		template.process(templateArgs, new OutputStreamWriter(targetOut));
		targetOut.close();

		logger.info("generated " + target);
	}
}
