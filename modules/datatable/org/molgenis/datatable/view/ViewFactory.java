package org.molgenis.datatable.view;

import org.molgenis.datatable.controller.Renderers.Renderer;

public interface ViewFactory {
	public Renderer createView(String viewName);
}
