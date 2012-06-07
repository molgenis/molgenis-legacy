package org.molgenis.datatable.view;

import org.molgenis.datatable.controller.JQGridController.View;

public interface ViewFactory {
	public View createView(String viewName);
}
