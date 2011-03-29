package org.molgenis.framework.ui;

import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.security.Login;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public abstract class PluginModel<E extends Entity> extends SimpleModel<E> implements ScreenController<E, PluginModel<E>>
{
	private static final long serialVersionUID = -6748634936592503575L;
	public PluginModel(String name, ScreenModel<?> parent)
	{
		super(name, parent);
		// label is the last part of the name
		this.setController(this);
		this.setLabel(this.getName().substring(this.getName().lastIndexOf("_") + 1));
	}

	public Login getLogin()
	{
		return this.getRootScreen().getLogin();
	}

	@Override
	public PluginModel<E> getScreen()
	{
		return this;
	}

	/**
	 * Path to the Template from within the source tree. It is good practice to
	 * give the template the same name. E.g. if the PluginScreen is
	 * myplugins.MyPlugin then the template could be myplugins/MyPlugin.ftl.
	 */
	public abstract String getViewTemplate();

	/**
	 * Name of the main Freemarker macro inside the template
	 * 
	 * @see #getViewTemplate(). It is good practice to give this macro the same
	 *      name as the template file. This macro should have as first parameter
	 *      the screen.
	 * 
	 *      For example: <#macro MyPlugin screen> Hello World </#macro>
	 */
	public abstract String getViewName();

	@Override
	public void handleRequest(Database db, Tuple request, PrintWriter out)
	{
		this.handleRequest(db, request);
	}

	/**
	 * A plugin is actually a model-view-controller structure. The extension of
	 * plugin is the controller. The freemarker template is the view, see
	 * getFreemarker... methods A 'model' of the screen must be provided to be
	 * used by the.
	 */
	
	@Override
	public boolean isVisible()
	{
		return Boolean.TRUE;
	}

	@Override
	public abstract void handleRequest(Database db, Tuple request);

	@Override
	public abstract void reload(Database db);
}
