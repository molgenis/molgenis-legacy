package org.molgenis.framework.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
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

/**
 * FreemarkerView uses a Freemarker template to render the user interface. The
 * Freemarker template will get as parameters:
 * <ol>
 * <li>application - result of
 * model.getController().getApplicationController().getModel()
 * <li>model - result of getModel()
 * <li>screen - deprecated, synonym of model
 * <li>viewhelper - all kinds of helper methods
 * <li>show - parameter influencing whole app or only one screen rendering
 * </ol>
 * 
 * @see http://www.freemarker.org
 */
public class FreemarkerView extends SimpleScreenView<ScreenModel>
{

	// wrapper of this template
	private freemarker.template.Configuration cfg = null;
	private String templatePath;
	private transient Logger logger = Logger.getLogger(this.getClass());
	private boolean usePublicFields = true;//false;
	private Map<String,Object> arguments = new LinkedHashMap<String,Object>();

	public FreemarkerView(String templatePath, ScreenModel model)
	{
		this(templatePath, model, true);
	}

	public FreemarkerView(String templatePath, ScreenModel model,
			boolean usePublicFields)
	{
		super(model);
		this.templatePath = templatePath;
		this.usePublicFields = usePublicFields;
	}
	
	@SuppressWarnings("deprecation")
	public String render(String templatePath, Map<String,Object> templateArgs, boolean usePublicFields)
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

				if (this.usePublicFields)
				{
					BeansWrapper wrapper = new BeansWrapper();
					// ouch, don't do this
					wrapper.setExposeFields(true);
					wrapper.setExposureLevel(BeansWrapper.EXPOSE_SAFE);
					cfg.setObjectWrapper(wrapper);
				}

				cfg
						.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

				// create template loader
				// load templates from MOLGENIS
				ClassTemplateLoader molgenistl = new ClassTemplateLoader(
						MolgenisOriginalStyle.class, "");
				// load templates from plugins, can be anywere
				// (nb this method is deprecated but I can't see why)
				ClassTemplateLoader root = new ClassTemplateLoader();
				ClassTemplateLoader plugins = new ClassTemplateLoader(model
						.getClass());

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

			// merge template
			cfg.addAutoInclude("ScreenViewHelper.ftl");
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

	@Override
	public String render()
	{
		// create template parameters
		Map<String, Object> templateArgs = new LinkedHashMap<String,Object>(this.arguments);
		templateArgs.put("application", model.getController()
				.getApplicationController().getModel());
		templateArgs.put("screen", model);
		templateArgs.put("model", model);
		templateArgs.put("widgetfactory", new ScreenViewHelper());
		templateArgs.put("show", model.getController()
				.getApplicationController().getModel().getShow());
		
		return this.render(templatePath, templateArgs, usePublicFields);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return model.getController().getCustomHtmlHeaders();
	}

	public String getTemplatePath()
	{
		return templatePath;
	}

	public void setTemplatePath(String templatePath)
	{
		this.templatePath = templatePath;
	}

	public boolean isUsePublicFields()
	{
		return usePublicFields;
	}

	public void setUsePublicFields(boolean usePublicFields)
	{
		this.usePublicFields = usePublicFields;
	}
	
	public void addParameter(String name, Object value)
	{
		this.arguments.put(name,value);
	}
}
