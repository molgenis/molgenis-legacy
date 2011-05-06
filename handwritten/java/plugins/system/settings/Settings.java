/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.settings;

import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.FileSourceHelper;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class Settings<E extends Entity> extends PluginModel<E> {

	private static final long serialVersionUID = 4037475429590054858L;
	private FileSourceHelper model;

	public FileSourceHelper getModel2() {
		if(true)
			throw new RuntimeException("CHANGED METHOD NAME BECAUSE OF INCOMPATILITY WITH super.getModel()");

		return model;
	}

	public Settings(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public String getViewName() {
		return "Settings";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/system/settings/Settings.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		this.handleRequest(db, request, null);
	}
	
	public void handleRequest(Database db, Tuple request, PrintWriter out) {
		if (request.getString("__action") != null) {

			System.out.println("*** handleRequest __action: " + request.getString("__action"));
			try {
				if (request.getString("__action").equals("setFileDirPath")) {
					db.getFileSourceHelper().setFilesource(request.getString("fileDirPath"));
				} else if (request.getString("__action").equals("deleteFileDirPath")) {
					db.getFileSourceHelper().deleteFilesource();
					
				} else if (request.getString("__action").equals("testDirRwValid")) {
					db.getFileSourceHelper().validateFileSource();
			}
			this.setMessages();
		} catch (Exception e) {
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}
}

	public void clearMessage() {
		this.setMessages();
	}

	@Override
	public void reload(Database db) {
		try{
			db.getFileSourceHelper().validateFileSource();
			model = db.getFileSourceHelper();
		}catch(Exception e){
			e.printStackTrace();
			setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

}
