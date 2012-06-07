package org.molgenis.datatable.view;

import org.molgenis.datatable.controller.JQGridController;
import org.molgenis.datatable.controller.JQGridController.View;

public class ViewFactoryImpl implements ViewFactory {
	public View createView(String viewName) {
		if(viewName.equals("JQ_GRID")) {
			return new JQGridController.JQGridView();
		} else if(viewName.equals("EXCEL")) {
			return new JQGridController.ExcelView();
		} else if(viewName.equals("CSV")) {
			return new JQGridController.CSVView();
		} else if(viewName.equals("SPSS")) {
			return new JQGridController.SPSSView();
		}
		throw new IllegalArgumentException(String.format("view: %s not found", viewName));
	}
}
