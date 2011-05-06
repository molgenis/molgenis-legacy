package org.molgenis.framework.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerView implements ScreenView
{
	
	// wrapper of this template
	private freemarker.template.Configuration cfg = null;
	private String templatePath; 
	private ScreenModel model;
	private transient Logger logger = Logger.getLogger(this.getClass());
	
	public FreemarkerView(String templatePath, ScreenModel model)
	{
		this.templatePath = templatePath;
		this.model = model;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String render()
	{
		logger.debug("trying to render " + templatePath);
		try
		{
			// keep configuration in session so we can reuse it
			if (cfg == null)
			{
				logger.debug("create freemarker config");
				// create configuration
				cfg = new freemarker.template.Configuration();

				// set the template loading paths
				cfg.setObjectWrapper(new DefaultObjectWrapper());

				BeansWrapper wrapper = new BeansWrapper();
				wrapper.setExposeFields(true);
				cfg.setObjectWrapper(wrapper);

				cfg
						.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

				// create template loader
				// load templates from MOLGENIS
				ClassTemplateLoader molgenistl = new ClassTemplateLoader(
						MolgenisOriginalStyle.class, "");
				// load templates from plugins, can be anywere
				// (nb this method is deprecated but I can't see why)
				ClassTemplateLoader root = new ClassTemplateLoader();
				ClassTemplateLoader plugins = new ClassTemplateLoader(model.getClass());

				// ClassTemplateLoader loader1 = new ClassTemplateLoader(
				// Object.class, "");
				// ClassTemplateLoader loader2 = new ClassTemplateLoader(
				// getClass().getSuperclass(), "");
				TemplateLoader[] loaders = new TemplateLoader[]
				{ molgenistl, root, plugins };
				MultiTemplateLoader mLoader = new MultiTemplateLoader(loaders);
				cfg.setTemplateLoader(mLoader);
				logger.debug("created freemarker config");
			}

			// create template parameters
			Map<String, Object> templateArgs = new TreeMap<String, Object>();
			templateArgs.put("application", model.getController().getApplicationController().getModel());
			templateArgs.put("screen", model);
			templateArgs.put("model", model);
			templateArgs.put("widgetfactory", new MolgenisWidgetFactory());
			templateArgs.put("show", model.getController().getApplicationController().getModel().getShow());

			// merge template
			cfg.addAutoInclude("MolgenisWidgets.ftl");
			Template template = cfg.getTemplate(templatePath);
			StringWriter writer = new StringWriter();
			template.process(templateArgs, writer);
			writer.close();

			return writer.toString();

		}
		catch (Exception e)
		{
			logger.error("rendering of template " + templatePath + " failed:");
			e.printStackTrace();

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw, true);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();

			return sw.toString().replace("\n", "<br/>");

		}
	}
}
