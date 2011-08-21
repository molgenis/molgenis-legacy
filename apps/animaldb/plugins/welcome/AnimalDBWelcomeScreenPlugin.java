/* Date:        October 28, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.welcome;

import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;
import plugins.fillanimaldb.FillAnimalDB;

import commonservice.CommonService;

public class AnimalDBWelcomeScreenPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5861419875983400033L;

	public AnimalDBWelcomeScreenPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "plugins_welcome_AnimalDBWelcomeScreenPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/welcome/AnimalDBWelcomeScreenPlugin.ftl";
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
			int nrOfUsersInDb = db.count(MolgenisUser.class);
			if (nrOfUsersInDb > 0) { // Check if DB is filled by counting the nr. of users (should always be >= 2)
				// Entry point when logging in, so good place to (re)set the ObservationTarget label map
				CommonService cs = CommonService.getInstance();
				cs.setDatabase(db);
				cs.makeObservationTargetNameMap(this.getLogin().getUserId(), true);
			} else {
				prefillDb(db);
			}
		} catch (Exception e) {
			prefillDb(db);
		}
	}
	
	private void prefillDb(Database db) {
		try {
			// Empty DB and run generated sql scripts
			new emptyDatabase(db, true);
			
			// Populate db with targets, features, values etc. needed to make AnimalDB run
			FillAnimalDB myFillAnimalDB = new FillAnimalDB(db);
			myFillAnimalDB.populateDB(this.getLogin());
			
			this.getMessages().add(new ScreenMessage("Your database was empty, so it was prefilled with entities needed to make AnimalDB run", true));
		} catch (Exception e) {
			String message = "Something went wrong while trying to prefill your database";
			if (e.getMessage() != null) {
				message += (": " + e.getMessage());
			}
			this.getMessages().add(new ScreenMessage(message, false));
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isVisible()
	{
		// Always show the welcome screen
		return true;
	}
}
