package org.molgenis.framework.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.html.WidgetFactory;

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
	private freemarker.template.Configuration conf = null;
	private String templatePath;
	private transient Logger logger = Logger.getLogger(FreemarkerView.class);
	private Map<String,Object> arguments = new LinkedHashMap<String,Object>();

	public FreemarkerView(String templatePath, ScreenModel model)
	{
		super(model);
		this.templatePath = templatePath;
		//this.usePublicFields = usePublicFields;
	}
	
	public FreemarkerView(String templatePath, Map<String,Object> templateArgs)
	{
		super(null);
		this.templatePath = templatePath;
		this.arguments = templateArgs;
	}
	
	public FreemarkerView()
	{
		super(null);
	}

	@SuppressWarnings("deprecation")
	public String render(String templatePath, Map<String,Object> templateArgs)//, boolean usePublicFields)
	{
		logger.debug("trying to render " + templatePath);
		try
		{
			// keep configuration in session so we can reuse it
			if (conf == null)
			{
				logger.debug("create freemarker config");
				// create configuration
				conf = new freemarker.template.Configuration();
				conf
						.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

				List<ClassTemplateLoader> loaders =  new ArrayList<ClassTemplateLoader>();
				
				
				// create template loader
				// load templates from MOLGENIS
				loaders.add(new ClassTemplateLoader(
						MolgenisOriginalStyle.class, ""));
				// load templates from plugins, can be anywere
				// (nb this method is deprecated but I can't see why)
				loaders.add(new ClassTemplateLoader());
				
				for(Object key: templateArgs.keySet())
				{
					if("model".equals(key) && templateArgs.get(key) != null) loaders.add(new ClassTemplateLoader(templateArgs.get(key)
						.getClass()));
				}

				// ClassTemplateLoader loader1 = new ClassTemplateLoader(
				// Object.class, "");
				// ClassTemplateLoader loader2 = new ClassTemplateLoader(
				// getClass().getSuperclass(), "");
				MultiTemplateLoader mLoader = new MultiTemplateLoader(loaders.toArray(new ClassTemplateLoader[loaders.size()]));
				conf.setTemplateLoader(mLoader);
				logger.debug("created freemarker config");
			}

			// merge template
			conf.addAutoInclude("ScreenViewHelper.ftl");
			
			WidgetFactory wf = new WidgetFactory();
			wf.configure(conf);
			
			Template template = conf.getTemplate(templatePath);
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
		//get the database for this application
		//Database db = this.getModel().getController().getApplicationController().getDatabase();
		
		// create template parameters
		Map<String, Object> templateArgs = new LinkedHashMap<String,Object>(this.arguments);
		if(model != null)
		{
			templateArgs.put("application", model.getController()
				.getApplicationController().getModel());
			templateArgs.put("show", model.getController()
					.getApplicationController().getModel().getShow());
		}
		templateArgs.put("screen", model);
		templateArgs.put("model", model);
		templateArgs.put("widgetfactory", new WidgetFactory());
		

		
		return this.render(templatePath, templateArgs);
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
	
	public void addParameter(String name, Object value)
	{
		this.arguments.put(name,value);
	}
}
