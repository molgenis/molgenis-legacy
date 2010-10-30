package org.molgenis.framework.ui.commands;

import java.util.List;

import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public abstract class PluginCommand extends SimpleCommand
{
	/** Constructor */
	public PluginCommand(String name, FormModel parentScreen)
	{
		super(name, parentScreen);
		this.setMenu("Plugin");
		this.setIcon("plugin.png");
	}

	/** Optional function if you need a dialog */
	public abstract List<HtmlInput> getInputs();

	/**
	 * Handle the request that is delegated from the formscreen
	 * 
	 * @param request
	 */
	public abstract void handleRequest(Tuple request);

	public abstract String getViewName();

	public abstract String getViewTemplate();
}
