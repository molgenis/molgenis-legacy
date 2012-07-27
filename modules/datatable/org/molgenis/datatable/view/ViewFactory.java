package org.molgenis.datatable.view;

import org.molgenis.datatable.view.Renderers.Renderer;

public interface ViewFactory {
	public Renderer createView(String viewName);
}
