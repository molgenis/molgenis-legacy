package org.molgenis.framework.ui;

import org.molgenis.framework.ui.html.HtmlRenderer;

public abstract class EasyPluginView<M extends EasyPluginModel> extends SimpleScreenView<M>
{
	private static final long serialVersionUID = 1L;
	
	public EasyPluginView(M model)
	{
		super(model);
	}
	
	public abstract HtmlRenderer getInputs(M model);

	@Override
	public String render()
	{
		return getInputs(getModel()).render();
	}
}
