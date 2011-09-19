package org.molgenis.phenoflow.ui;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;

public class EbiStyleLoader extends EasyPluginController {

	public EbiStyleLoader(String name, 
			ScreenController parent) {
		super(name, null, parent);
		this.setModel(new EbiStyleLoaderModel(this));
		this.setView(new FreemarkerView("EbiStyleLoader.ftl", this.getModel()));

		
		//change the view of the main application
		this.getApplicationController().setView(new FreemarkerView("org/molgenis/phenoflow/ui/UserInterface-EBI.ftl", this.getApplicationController()
						.getModel()));

	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/phenoflow/colors.css\">";
	}

	@Override
	public void reload(Database db) throws Exception {
		// TODO Auto-generated method stub
	}
	



}
