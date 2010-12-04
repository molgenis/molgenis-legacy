package org.molgenis.generators.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.molgenis.MolgenisOptions;
import org.molgenis.generators.ForEachEntityGenerator;
import org.molgenis.generators.GeneratorHelper;
import org.molgenis.model.elements.Entity;
import org.molgenis.model.elements.Model;

import freemarker.template.Template;

public class MapperDecoratorGen extends ForEachEntityGenerator
{
	@Override
	public String getDescription()
	{
		return "(Optional) Generates a template for mappers if decorator='path' is set and the class is not there.";
	}

	@Override
	public String getType()
	{
		return "";
	}

	@Override
	public void generate(Model model, MolgenisOptions options) throws Exception
	{
		Template template = this.createTemplate(this.getClass().getSimpleName() + getExtension() + ".ftl");
		Map<String, Object> templateArgs = createTemplateArguments(options);

		// apply generator to each entity
		for (Entity entity : model.getEntities())
		{
			try
			{
				if (entity.getDecorator() != null)
				{
					String fullKlazzName = entity.getDecorator();
					
					String packageName = fullKlazzName;
					if(fullKlazzName.contains("."))
						packageName = fullKlazzName.substring(0, fullKlazzName.lastIndexOf("."));
					
					String shortKlazzName = fullKlazzName;
					if(fullKlazzName.contains("."))
						shortKlazzName = fullKlazzName.substring(fullKlazzName.lastIndexOf(".") + 1);

					File targetDir = new File(this.getHandWrittenPath(options) + "/" + packageName.replace(".", "/"));
					targetDir.mkdirs();

					File targetFile = new File(this.getHandWrittenPath(options) + "/" + fullKlazzName.replace(".", "/")
							+ ".java");
					// only generate if the file doesn't exist
					if (!targetFile.exists())
					{
						templateArgs.put("entityClass", entity.getNamespace()+"."+GeneratorHelper.firstToUpper(entity.getName()));
						
						templateArgs.put("clazzName", shortKlazzName);
						templateArgs.put("entity", entity);
						templateArgs.put("model", model);
						// templateArgs.put("db_driver", options.db_driver);
						templateArgs.put("template", template.getName());
						templateArgs.put("file", packageName.replace(".", "/") + "/"
								+ GeneratorHelper.firstToUpper(entity.getName()) + getType() + getExtension());
						templateArgs.put("package", packageName);

						OutputStream targetOut = new FileOutputStream(targetFile);

						template.process(templateArgs, new OutputStreamWriter(targetOut));
						targetOut.close();

						// logger.info("generated " +
						// targetFile.getAbsolutePath());
						logger.info("generated " + targetFile);
					}
				}
			}
			catch (Exception e)
			{
				logger.error("problem generating for " + entity.getName());
				throw e;
			}
		}
	}

}
