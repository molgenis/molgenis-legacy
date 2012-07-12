package plugins.developingAlgorithm;


import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class developingAlgorithm extends PluginModel<Entity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 244998330024877396L;
	public developingAlgorithm(String name, ScreenController<?> parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}

	public String getCustomHtmlHeaders() {
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">";
	}

	@Override
	public String getViewName() {
		return "plugins_developingAlgorithm_developingAlgorithm";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/developingAlgorithm/developingAlgorithm.ftl";
	}

	public void handleRequest(Database db, Tuple request) {


	}


	@Override
	public void reload(Database db) {
 
	}	
	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		if (!this.getLogin().isAuthenticated()) {
			return false;
		}
		return true;
	}


}
