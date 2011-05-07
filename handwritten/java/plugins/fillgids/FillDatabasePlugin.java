/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.fillgids;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;

import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.CsvImport;

import convertors.ulidb.ConvertUliDbToPheno;

public class FillDatabasePlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5634663322794444817L;

	public FillDatabasePlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	/*
	public String getCustomHtmlHeaders()
    {
        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
    }
*/
	@Override
	public String getViewName()
	{
		return "plugins_fillgids_FillDatabasePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/fillgids/FillDatabasePlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		
			
		
		try {
			String folder = request.getString("loadingdirectories");
			File dir = new File(folder);
						
			//logger.info("measurement: "+filename);
			CsvImport.importAll(dir, db, null);
				
			
			/*
			
			if( action.equals("loadUliBackgrounds") )
			{
				String filename = request.getString("ulibackgroundtable");
				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
				myLoadUliDb.populateBackground(filename);
			}
			
			if( action.equals("loadUliGenes") )
			{
				String filename = request.getString("uligenetable");
				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
				myLoadUliDb.populateGene(filename);
			}
			
			if( action.equals("loadUliLines") )
			{
				String filename = request.getString("ulilinetable");
				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
				myLoadUliDb.populateLine(filename);
			}
			
			if( action.equals("loadUliAnimals") )
			{
				String filename = request.getString("ulianimaltable");
				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
				myLoadUliDb.populateAnimal(filename);
				myLoadUliDb.populateProtocolApplication();
				myLoadUliDb.populateValue(filename);
				myLoadUliDb.parseParentRelations(filename);
				myLoadUliDb.writeToDb();
			}
			*/
			
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
	
}
