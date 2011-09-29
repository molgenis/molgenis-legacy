package org.molgenis.framework.ui.commands;

//import java.io.IOException;
//import java.io.PrintWriter;
import java.io.OutputStream;
//import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.QueryRule;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.framework.ui.FormController;
//import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.Paragraph;
//import org.molgenis.util.CsvWriter;
import org.molgenis.util.Tuple;

/**
 * This command returns JavaScript code to download all records in CSV format.
 *
 * @param <E>
 */


public class GalaxyCommand extends SimpleCommand {

	public GalaxyCommand(String name, ScreenController<?> parentController) {
		super(name, parentController);
		if (this.getName() == "send_all_to_galaxy") {
			this.setLabel("Send All to Galaxy");
		} else {
			this.setLabel("Send Selected to Galaxy");
		}
		//this.setLabel("Send to Galaxy");
		this.setIcon("generated-res/img/upload2galaxy.png");
		this.setMenu("File");
		//this.setDialog(false);
		
	}

	// TODO: Get proper UID.
	private static final long serialVersionUID = 1L;

	@Override
	public String getJavaScriptAction() {

		StringBuffer jScript = new StringBuffer();
		jScript.append("");
		
		if(this.getController().getApplicationController().getGalaxyUrl() != null) {
		
			String galaxy_url = this.getController().getApplicationController().getGalaxyUrl();
			String molgenis_site = this.getController().getApplicationController().getApplicationUrl();
			String molgenis_download_all = molgenis_site+"/molgenis.do?__target="+this.getController().getName()+"&__action=download_txt_all&__show=download";
			String molgenis_download_selected = molgenis_site+"/molgenis.do?__target="+this.getController().getName()+"&__action=download_txt_selected&__show=download";
			
			jScript.append("var form = document.createElement('form');");
			jScript.append("form.setAttribute('method', 'post');");
			jScript.append("form.setAttribute('action', '" + galaxy_url + "');");

			jScript.append("var hiddenField = document.createElement('input');");
			jScript.append("hiddenField.setAttribute('type', 'hidden');");
			jScript.append("hiddenField.setAttribute('name', 'URL');");
			if (this.getName() == "send_all_to_galaxy") {
				jScript.append("hiddenField.setAttribute('value', '" + molgenis_download_all + "');");
			} else {
				jScript.append("hiddenField.setAttribute('value', '" + molgenis_download_selected + "');");
			}
			jScript.append("hiddenField.setAttribute('value', '" + molgenis_download_all + "');");
			jScript.append("form.appendChild(hiddenField);");

			jScript.append("document.body.appendChild(form);");
			jScript.append("form.submit();");
		
		}

		return jScript.toString();
		
	}
	
	@Override
	public List<HtmlInput<?>> getInputs() throws DatabaseException {
		return null;
	}
	
	@Override
	public List<ActionInput> getActions() {
		return new ArrayList<ActionInput>();
	}
	
	@Override
	//public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter downloadStream) {
	public ScreenModel.Show handleRequest(Database db, Tuple request, OutputStream downloadStream) {
	logger.debug("galaxy button clicked: "+this.getController().getApplicationController().getGalaxyUrl());
		return ScreenModel.Show.SHOW_MAIN;
	}

	//@SuppressWarnings("unchecked")
	//@Override
	//
	//public ScreenModel.Show handleRequest(Database db, Tuple request, PrintWriter csvDownload)
	//		throws ParseException, DatabaseException, IOException {
	//
	//	public ScreenModel.Show handleRequest(Database db, Tuple request, OutputStream downloadStream)
	//	{
	//		logger.debug("galaxy button clicked: "+this.getController().getApplicationController().getGalaxyUrl());
	//		logger.error(this.getName());
	//
	//		FormModel<?> view = this.getFormScreen();
	//
	//		Object ids = request.getList(FormModel.INPUT_SELECTED);
	//		List<Object> records = new ArrayList<Object>();
	//
	//		if (ids != null)
	//		{
	//			if (ids instanceof List)
	//			{
	//				records = (List<Object>) ids;
	//			}
	//			else
	//				records.add(ids);
	//		}
	//
	//		if (records.size() == 0)
	//		{
	//			csvDownload.println("No records selected.");
	//			return ScreenModel.Show.SHOW_MAIN;
	//		}
	//
	//		List<String> fieldsToExport = ((FormController<?>)this.getController()).getVisibleColumnNames();
	//
	//		// watch out, the "IN" operator expects an Object[]
	//		db.find(view.getController().getEntityClass(), new CsvWriter(csvDownload), fieldsToExport,
	//				new QueryRule("id", Operator.IN, records));
	//		return ScreenModel.Show.SHOW_MAIN;
	//	}
	
	@Override
	public boolean isVisible() {
		// Show this menu item only if the user navigated to Molgenis from a Galaxy server.
		if(this.getController().getApplicationController().getGalaxyUrl() != null) {
			return true;
		}
		return false;
	}

}