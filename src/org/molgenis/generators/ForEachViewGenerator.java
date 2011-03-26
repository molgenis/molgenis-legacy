package org.molgenis.generators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.MolgenisOptions;
import org.molgenis.model.elements.Model;
import org.molgenis.model.elements.View;

import freemarker.template.Template;

/**
 * This generator applies the template to each (database) view. It uses defaults
 * for template name, package name and classname:
 * <li> template name is this.getClass() + ".java.ftl"
 * <li> package is {model.name}.{own package name}. For example,
 * org.molgenis.generate.foo.bar will be generated to {model.name}.foo.bar.
 * <li> class name is own class name without traling "Gen". For example:
 * FooBarGen will generate {EntityName}FooBar.java files.
 * 
 * @author Morris Swertz
 * @since 30-jul-2007
 * 
 */
public abstract class ForEachViewGenerator extends Generator
{
	public static final transient Logger logger = Logger.getLogger(ForEachViewGenerator.class);

	
	public ForEachViewGenerator()
	{
	}

	@Override
	public void generate( Model model, MolgenisOptions options ) throws Exception
	{
		Template template = this.createTemplate(this.getClass().getSimpleName() + getExtension() + ".ftl");
		Map<String, Object> templateArgs = createTemplateArguments(options);

		// calculate package from its own package
		String packageName = this.getClass().getPackage().toString().substring(
				Generator.class.getPackage().toString().length());
		File targetDir = new File(this.getSourcePath(options) + model.getName().replace(".","/") + packageName.replace(".", "/"));


		// apply generator to each entity
		for( View view : model.getViews() )
		{
			targetDir.mkdirs();
			
			File targetFile = new File(targetDir + "/" + GeneratorHelper.firstToUpper(view.getName()) + getType()
					+ getExtension());
			// logger.debug("trying to generated "+targetFile);
			templateArgs.put("view", view);
			templateArgs.put("model", model);
			templateArgs.put("template", template.getName());
			templateArgs.put("file", packageName.replace(".", "/") + "/"
					+ GeneratorHelper.firstToUpper(view.getName()) + getType() + getExtension());
			templateArgs.put("package", model.getName().toLowerCase() + packageName);

			OutputStream targetOut = new FileOutputStream(targetFile);

			template.process(templateArgs, new OutputStreamWriter(targetOut));
			targetOut.close();

			// logger.info("generated " + targetFile.getAbsolutePath());
			logger.info("generated " + targetFile);

		}
	}

	/**
	 * Calculate class name from its own name.
	 * 
	 * 
	 * @return Name(this.getClass()) - "Gen"
	 */
	public String getType()
	{
		String className = this.getClass().getSimpleName();
		return className.substring(0, className.length() - 3);
	}
}
