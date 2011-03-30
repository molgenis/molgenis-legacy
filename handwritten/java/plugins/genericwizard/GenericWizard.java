/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.genericwizard;

import java.io.File;
import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;



import app.ExcelImport;
import app.ImportWizardExcelPrognosis;

public class GenericWizard extends PluginModel<Entity> {

	private static final long serialVersionUID = -6011550003937663086L;
	private GenericWizardModel model = new GenericWizardModel();

	public GenericWizardModel getModel() {
		return model;
	}

	public GenericWizard(String name, ScreenModel<Entity> parent) {
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders() {
		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";

	}

	@Override
	public String getViewName() {
		return "GenericWizard";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/genericwizard/GenericWizard.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		System.out.println("*** handleRequest WRAPPER __action: "
				+ request.getString("__action"));
		this.handleRequest(db, request, null);
	}

	@Override
	public boolean isVisible() {
		//you can use this to hide this plugin, e.g. based on user rights.
		if (this.getLogin().isAuthenticated()){
			return true;
		} else {
			return false;
		}
	}

	public void handleRequest(Database db, Tuple request, PrintWriter out) {
		if (request.getString("__action") != null) {

			System.out.println("*** handleRequest __action: "
					+ request.getString("__action"));

			try {
				
				//BUTTONS ON SCREEN ONE
				if (request.getString("__action").equals("upload")) {

					//get uploaded file and do checks
					File file = request.getFile("upload");
					if (file == null) {
						throw new Exception("No file selected.");
					} else if (!file.getName().endsWith(".xls")) {
						throw new Exception(
								"File does not end with '.xls', other formats are not supported.");
					}

					//run prognosis
					ImportWizardExcelPrognosis iwep = new ImportWizardExcelPrognosis(
							file);
					
					//if no error, set prognosis, set file, and continue
					this.model.setIwep(iwep);
					this.model.setCurrentFile(file);
					this.model.setWhichScreen("two");
					
				//BUTTONS ON SCREEN TWO
				} else if (request.getString("__action").equals("toScreenOne")) {
					
					//goto screen one
					this.model.setWhichScreen("one");
					
					//reset stuff
					this.model.setCurrentFile(null);
					this.model.setIwep(null);
					this.model.setImportSuccess(false);

				} else if (request.getString("__action").equals("import")) {
					
					//goto screen three
					this.model.setWhichScreen("three");
					
					//set import succes to false (again), to be sure
					this.model.setImportSuccess(false);
					ExcelImport.importAll(this.model.getCurrentFile(), db,
							new SimpleTuple());
					
					//when no error, set success to true
					this.model.setImportSuccess(true);
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

		} catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e
					.getMessage() : "null", false));
		}

	}

}
