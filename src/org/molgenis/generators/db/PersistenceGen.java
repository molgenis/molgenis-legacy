package org.molgenis.generators.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.molgenis.MolgenisOptions;
import org.molgenis.generators.Generator;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class PersistenceGen extends Generator
{
	public static final transient Log logger = LogFactory.getLog(PersistenceGen.class);

	@Override
	public String getDescription()
	{
		return "Generates persistence.xml";
	}

	@Override
	public void generate(Model model, MolgenisOptions options) throws Exception
	{
		Template template = createTemplate("/" + getClass().getSimpleName() + ".xml.ftl");
		Map<String, Object> templateArgs = createTemplateArguments(options);

		File target = new File(this.getSourcePath(options) + "/META-INF/persistence.xml");
		// File target = new File( this.getHandWrittenPath(options) +
		// "/META-INF/persistence.xml" );
		target.getParentFile().mkdirs();

		templateArgs.put("options", options);
		templateArgs.put("model", model);

		OutputStream targetOut = new FileOutputStream(target);
		template.process(templateArgs, new OutputStreamWriter(targetOut));
		targetOut.close();

		logger.info("generated " + target);
	}
}
