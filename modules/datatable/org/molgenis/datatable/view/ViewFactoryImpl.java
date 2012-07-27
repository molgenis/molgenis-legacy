package org.molgenis.datatable.view;

import org.molgenis.datatable.view.Renderers.CSVRenderer;
import org.molgenis.datatable.view.Renderers.ExcelRenderer;
import org.molgenis.datatable.view.Renderers.JQGridRenderer;
import org.molgenis.datatable.view.Renderers.Renderer;
import org.molgenis.datatable.view.Renderers.SPSSRenderer;

public class ViewFactoryImpl implements ViewFactory {
	public Renderer createView(String viewName) {
		if(viewName.equals("JQ_GRID")) {
			return new JQGridRenderer();
		} else if(viewName.equals("EXCEL")) {
			return new ExcelRenderer();
		} else if(viewName.equals("CSV")) {
			return new CSVRenderer();
		} else if(viewName.equals("SPSS")) {
			return new SPSSRenderer();
		}
		throw new IllegalArgumentException(String.format("view: %s not found", viewName));
	}
}
