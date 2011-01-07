/*
 * Date: December 24, 2010 Template: PluginScreenJavaTemplateGen.java.ftl
 * generator: org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.framework.ui;

import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Tuple;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;

public class GenericPlugin extends PluginModel
{
	//serialization id
	private static final long serialVersionUID = 1L;
	// wrapper of this template
	private freemarker.template.Configuration cfg = null;

	public GenericPlugin(String name, ScreenModel parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_newmodel_GenericPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/framework/ui/GenericPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getAction();
		try
		{
			logger.debug("trying to use reflection to call "
					+ this.getClass().getName() + "." + action);
			Method m = this.getClass().getMethod(action, Database.class,
					Tuple.class);
			m.invoke(this, db, request);
			logger.debug("call of " + this.getClass().getName() + "(name="
					+ this.getName() + ")." + action + " completed");
		}
		catch (Exception e)
		{
			logger.error("call of " + this.getClass().getName() + "(name="
					+ this.getName() + ")." + action + " failed: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		// try
		// {
		// Database db = this.getDatabase();
		// Query q = db.query(Experiment.class);
		// q.like("name", "test");
		// List<Experiment> recentExperiments = q.find();
		//			
		// //do somethings
		// }
		// catch(Exception e)
		// {
		// //...
		// }
	}

	@Override
	public boolean isVisible()
	{
		// you can use this to hide this plugin, e.g. based on user rights.
		// e.g.
		// if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}

	public String render(String templatePath)
	{
		logger.debug("trying to render " + templatePath);
		try
		{
			//keep configuration in session so we can reuse it
			if (cfg == null)
			{
				logger.debug("create freemarker config");
				// create configuration
				cfg = new freemarker.template.Configuration();
				BeansWrapper wrapper = new BeansWrapper();
				wrapper.setExposeFields(true);
				cfg.setObjectWrapper(wrapper);

				// create template loader
				ClassTemplateLoader loader1 = new ClassTemplateLoader(
						getClass(), "");
				ClassTemplateLoader loader2 = new ClassTemplateLoader(
						getClass().getSuperclass(), "");
				TemplateLoader[] loaders = new TemplateLoader[]
				{ loader1, loader2 };
				MultiTemplateLoader mLoader = new MultiTemplateLoader(loaders);
				cfg.setTemplateLoader(mLoader);
				logger.debug("created freemarker config");
			}
			
			// create template parameters
			Map<String, Object> templateArgs = new TreeMap<String, Object>();
			templateArgs.put("screen", this);

			// merge template
			Template template = cfg.getTemplate(templatePath);
			StringWriter writer = new StringWriter();
			template.process(templateArgs, writer);
			writer.close();

			return writer.toString();
		}
		catch (Exception e)
		{
			logger.error("rendering of template " + templatePath + " failed:"
					+ e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public String render()
	{
		// ouch: because we use superclass during generation we solve it like
		// this. Ouch!
		return render(this.getClass().getSuperclass().getSimpleName() + ".ftl");
	}
}
