package org.molgenis.framework.ui.commands;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextParagraph;
import org.molgenis.util.Tuple;

public class GalaxyCommand extends SimpleCommand {


	public GalaxyCommand(String name, ScreenController<?> parentController) {
		super(name, parentController);
		this.setLabel("Sent to Galaxy");
		this.setIcon("generated-res/img/download.png");
		this.setMenu("File");
		this.setDialog(true);
	}

	private static final long serialVersionUID = 1L;
	
	@Override
	public List<HtmlInput<?>> getInputs() throws DatabaseException {
		List<HtmlInput<?>> result = new ArrayList<HtmlInput<?>>();
		
		String galaxy_url = this.getController().getApplicationController().getGalaxyUrl();
		

		//BIG TODO the url of current MOLGENIS must be easily accessible to use here.
		String molgenis_download_all = "http://localhost:8082/molgenis_apps/molgenis.do?__target="+this.getController().getName()+"&__action=download_all&__show=download";
		
		result.add(new TextParagraph("t","<script>document.molgenis_popup.action = '"+galaxy_url+"'; </script>"));
		
		result.add(new StringInput("URL",molgenis_download_all));
		
		result.add(new ActionInput("sendall", "Send all to galaxy"));
		
		return result;
	}

	@Override
	public List<HtmlInput> getActions() {
		// TODO Auto-generated method stub
		return new ArrayList<HtmlInput>();
	}
	
	@Override
	public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter downloadStream)
	{
		logger.debug("galaxy button clicked: "+this.getController().getApplicationController().getGalaxyUrl());
		
		
		return ScreenModel.Show.SHOW_MAIN;
	}
	
	@Override
	public boolean isVisible()
	{
		if(this.getController().getApplicationController().getGalaxyUrl() != null)
		{
			return true;
		}
		return false;
	}

}
