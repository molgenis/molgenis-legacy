/*
 * @Date 06-10-2010
 * @Author Jessica Lundberg
 */
package plugins.welcome;

import jxl.common.Logger;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

import plugins.fillngsdb.FillNgsDatabase;

public class NGSWelcomeScreenPlugin<E extends Entity> extends PluginModel<E>{

	private static final long serialVersionUID = 1918194427035294724L;
	private FillNgsDatabase fill;
	private static final transient Logger logger = Logger.getLogger(NGSWelcomeScreenPlugin.class);

	
	public NGSWelcomeScreenPlugin(String name, ScreenModel<E> parent) {
		super(name, parent);
		
		
	}

	@Override
	public String getViewName() {
		return "plugins_welcome_NGSWelcomeScreenPlugin";
	}

	@Override
	public String getViewTemplate() {
		return "plugins/welcome/NGSWelcomeScreenPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) {
	    
	}

	@Override
	public void reload(Database db) {
	    boolean isFilled = CommonService.getInstance().isNgsDatabaseFilled();
	    
	    if(!isFilled) {
		fill = new FillNgsDatabase(db);
		isFilled = true;
		try {
		fill.populateDatabase();
		} catch(Exception e) {
			logger.warn("Something went wrong with loading the database with default data", e);
		}
	    }
	}

	@Override
	public boolean isVisible() {
		return true;
	}
}