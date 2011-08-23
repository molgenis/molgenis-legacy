/* Date:        November 19, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.welcome;

import java.text.ParseException;

import org.molgenis.bbmri.ChangeLog;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class BbmriWelcomeScreenPlugin<E extends Entity> extends PluginModel<E>
{
	
	ChangeLog mostRecentChangeLogEntry; 

	private static final long serialVersionUID = -2848815736940818733L;

	public BbmriWelcomeScreenPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_welcome_BbmriWelcomeScreenPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/welcome/BbmriWelcomeScreenPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//		Database db = this.getDatabase();
//		String action = request.getString("__action");
//		
//		if( action.equals("do_add") )
//		{
//			Experiment e = new Experiment();
//			e.set(request);
//			db.add(e);
//		}
//		} catch(Exception e)
//		{
//			//e.g. show a message in your form
//		}
	}

	@Override
	public void reload(Database db)
	{
		try {
			mostRecentChangeLogEntry = db.query(ChangeLog.class).sortDESC(ChangeLog.CHANGEDATE).find().get(0);
		} catch (Exception e) {
			// no entries (yet), so mostRecentChangeLogEntry will remain null
		}
		
		
	}
	
	public ChangeLog getMostRecentChangeLogEntry() {
		return mostRecentChangeLogEntry;
	}

	public void setMostRecentChangeLogEntry(ChangeLog mostRecentChangeLogEntry) {
		this.mostRecentChangeLogEntry = mostRecentChangeLogEntry;
	}

	@Override
	public boolean isVisible()
	{
		//you can use this to hide this plugin, e.g. based on user rights.
		//e.g.
		//if(!this.getLogin().hasEditPermission(myEntity)) return false;
		return true;
	}
	
	public int getUserId() {
		if (this.getLogin().isAuthenticated() == true) {
			return this.getLogin().getUserId();
		} else {
			return 0;
		}
	}
}
