/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.fillanimaldb;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;

import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class FillDatabasePlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5634663322794444817L;

	public FillDatabasePlugin(String name, ScreenModel<Entity> parent)
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
		return "plugins_fillanimaldb_FillDatabasePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/fillanimaldb/FillDatabasePlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			String action = request.getString("__action");
			
			if( action.equals("loadAnimals") )
			{
				String filename = request.getString("animaltable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populateAnimal(filename);
			}
			
			if( action.equals("loadLocations") )
			{
				String filename = request.getString("locationtable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populateLocation(filename);
			}
			
			if( action.equals("loadLitters") )
			{
				String filename = request.getString("littertable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populateLitter(filename);
			}
			
			if( action.equals("loadExperiments") )
			{
				String filename = request.getString("experimenttable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populateExperiment(filename);
			}
			
			if( action.equals("loadDECApplications") )
			{
				String filename = request.getString("decapplicationtable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populateDECApplication(filename);
			}
			
			if( action.equals("loadAnimalsInExperiments") )
			{
				String filename = request.getString("experimentanimaltable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populateAnimalsInExperiments(filename);
			}
			
			if( action.equals("loadPresets") )
			{
				String filename = request.getString("presettable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populatePreset(filename);
			}
			
			if( action.equals("loadPresetAnimals") )
			{
				String filename = request.getString("presetanimaltable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populatePresetAnimals(filename);
			}
			
			if( action.equals("loadEvents") )
			{
				String filename = request.getString("eventtable");
				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db);
				myLoadAnimalDB.populateEvents(filename);
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
		//you can use this to hide this plugin, e.g. based on user rights.
		try 
		{
			if (this.getLogin().isAuthenticated()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			// Authenticated but no user name, so probably debug user
			return true;
		}
	}
}
