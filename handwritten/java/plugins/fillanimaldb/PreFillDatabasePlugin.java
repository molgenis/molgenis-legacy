/* Date:        February 2, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.fillanimaldb;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;
import app.JDBCDatabase;

public class PreFillDatabasePlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -1778222062030349381L;

	public PreFillDatabasePlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }

	@Override
	public String getViewName()
	{
		return "plugins_fillanimaldb_PreFillDatabasePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/fillanimaldb/PreFillDatabasePlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			String action = request.getString("__action");
			if (action.equals("fillDB") )
			{
				// Empty db and run generated sql scripts
				new emptyDatabase((JDBCDatabase)db, true);
				
				// Populate db with targets, features, values etc. needed to make AnimalDB run
				FillAnimalDB myFillAnimalDB = new FillAnimalDB(db);
				myFillAnimalDB.populateDB(this.getLogin());
				
			}
		} catch(Exception e) {
			e.printStackTrace();
			//e.g. show a message in your form
		}
	}

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
	@Override
	public boolean isVisible()
	{
		// Always visible, so user can add items to the db needed to enable logging in
		return true;
	}
}
