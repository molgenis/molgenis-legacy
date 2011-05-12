package org.molgenis.framework.ui;

import org.molgenis.framework.ui.html.HtmlInput;

public abstract class EasyPluginView extends SimpleScreenView
{
	private static final long serialVersionUID = 1L;
	
	public EasyPluginView()
	{
		super();
	}
	
	public abstract HtmlInput getInputs();

	@Override
	public String render()
	{
		return getInputs().toHtml();
	}
}
