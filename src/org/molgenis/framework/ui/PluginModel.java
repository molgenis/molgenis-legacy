package org.molgenis.framework.ui;

import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
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
	 * Show plugin or not, depending on whether the user is authenticated.
	 * Note: at the moment you can still override this method in your plugin to bypass security (evil).
	 */
	@Override
	public boolean isVisible()
	{
		if (this.getLogin().isAuthenticated()){
			try {
				if (this.getLogin().canRead(this)) {
					return true;
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public abstract void handleRequest(Database db, Tuple request);

	@Override
	public abstract void reload(Database db);
}
