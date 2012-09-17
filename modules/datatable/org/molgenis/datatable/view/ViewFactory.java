package org.molgenis.datatable.view;

import org.molgenis.datatable.view.renderers.Renderers.Renderer;

public interface ViewFactory
{
	public Renderer createView(String viewName);
}
