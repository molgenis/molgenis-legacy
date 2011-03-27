/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.diagnostics;

import java.io.File;
import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.ExcelImport;
import app.ImportWizardExcelPrognosis;

public class Diagnostics extends PluginModel {

	private DiagnosticsModel model = new DiagnosticsModel();

	public DiagnosticsModel getModel() {
		return model;
	}

	public Diagnostics(String name, ScreenModel parent) {
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders() {
		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";

	}

	@Override
	public String getViewName() {
		return "Diagnostics";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/system/diagnostics/Diagnostics.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		System.out.println("*** handleRequest WRAPPER __action: "
				+ request.getString("__action"));
		this.handleRequest(db, request, null);
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	public void handleRequest(Database db, Tuple request, PrintWriter out) {
		if (request.getString("__action") != null) {

			System.out.println("*** handleRequest __action: "
					+ request.getString("__action"));

			try {
				
				
				if (request.getString("__action").equals("upload")) {
					
				}

					

				this.setMessages();
			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e
						.getMessage() : "null", false));
			}
		}
	}

	public void clearMessage() {
		this.setMessages();
	}

	@Override
	public void reload(Database db) {

		try {
			
			// file system based overview of the data
			
			// what is contained in the data folder?
			
			// what is part of Data, what is not??
			
			// any Data objects without backend? refer to xgap wizard!
			
			// system directory status
			
			// database status
			
			// load some log files off the system and show them?

		} catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e
					.getMessage() : "null", false));
		}

	}

}
