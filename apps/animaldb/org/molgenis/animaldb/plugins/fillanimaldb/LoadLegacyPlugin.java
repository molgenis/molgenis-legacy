/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.fillanimaldb;

import org.molgenis.animaldb.convertors.oldadb.LoadAnimalDB;
import org.molgenis.animaldb.convertors.prefill.PrefillAnimalDB;
import org.molgenis.animaldb.convertors.rhutdb.ConvertRhutDbToPheno;
import org.molgenis.animaldb.convertors.ulidb.ConvertUliDbToPheno;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class LoadLegacyPlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5634663322794444817L;

	public LoadLegacyPlugin(String name, ScreenController<?> parent)
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
		return "org_molgenis_animaldb_plugins_fillanimaldb_LoadLegacyPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/fillanimaldb/LoadLegacyPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			String action = request.getString("__action");
			
			if (action.equals("load")) {
				String filename = request.getString("zip");
				String legacy = request.getString("source");
				if (legacy.equals("prefill")) {
					PrefillAnimalDB myPrefill = new PrefillAnimalDB(db, this.getLogin());
					myPrefill.prefillFromZip(filename);
					this.setSuccess("Pre-filling AnimalDB successful");
				} else if (legacy.equals("ulidb")) {
					ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
					myLoadUliDb.convertFromFile(filename);
					this.setSuccess("Legacy import from Uli Eisel DB successful");
				} else if (legacy.equals("oldadb")) {
					LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
					myLoadAnimalDB.convertFromZip(filename);
					this.setSuccess("Legacy import from old AnimalDB successful");
				} else if (legacy.equals("rhutdb")) {
					ConvertRhutDbToPheno myLoadRhutDb = new ConvertRhutDbToPheno(db, this.getLogin());
					myLoadRhutDb.convertFromZip(filename);
					this.setSuccess("Legacy import from Roelof Hut DB successful");
				}
			}
				
			
//			if( action.equals("loadUliBackgrounds") )
//			{
//				String filename = request.getString("ulibackgroundtable");
//				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
//				myLoadUliDb.populateBackground(filename);
//			}
//			
//			if( action.equals("loadUliGenes") )
//			{
//				String filename = request.getString("uligenetable");
//				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
//				myLoadUliDb.populateGene(filename);
//			}
//			
//			if( action.equals("loadUliLines") )
//			{
//				String filename = request.getString("ulilinetable");
//				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
//				myLoadUliDb.populateLine(filename);
//			}
//			
//			if( action.equals("loadUliAnimals") )
//			{
//				String filename = request.getString("ulianimaltable");
//				//String filename = "C:/Documents and Settings/Administrator/workspace/molgenis_apps/data/AnimalDB/legacy/20110429_UliEisel/Tierdetails.csv";
//				ConvertUliDbToPheno myLoadUliDb = new ConvertUliDbToPheno(db, this.getLogin());
//				//myLoadUliDb.populateLine("C:/Documents and Settings/Administrator/workspace/molgenis_apps/data/AnimalDB/legacy/20110429_UliEisel/Linie.csv");
//				//myLoadUliDb.populateGene("C:/Documents and Settings/Administrator/workspace/molgenis_apps/data/AnimalDB/legacy/20110429_UliEisel/Gen.csv");
//				//myLoadUliDb.populateBackground("C:/Documents and Settings/Administrator/workspace/molgenis_apps/data/AnimalDB/legacy/20110429_UliEisel/GenetischerHintergrund.csv");
//				myLoadUliDb.populateAnimal(filename);
//				myLoadUliDb.populateProtocolApplication();
//				myLoadUliDb.populateValue(filename);
//				myLoadUliDb.parseParentRelations(filename);
//				myLoadUliDb.writeToDb();
//				
//				CommonService cs = CommonService.getInstance();
//				cs.setDatabase(db);
//				cs.makeObservationTargetNameMap(this.getLogin().getUserName(), true);
//			}
//			
//			if( action.equals("loadAnimals") )
//			{
//				String filename = request.getString("animaltable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populateAnimal(filename);
//			}
//			
//			if( action.equals("loadLocations") )
//			{
//				String filename = request.getString("locationtable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populateLocation(filename);
//			}
//			
//			if( action.equals("loadLitters") )
//			{
//				String filename = request.getString("littertable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populateLitter(filename);
//			}
//			
//			if( action.equals("loadExperiments") )
//			{
//				String filename = request.getString("experimenttable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populateExperiment(filename);
//			}
//			
//			if( action.equals("loadDECApplications") )
//			{
//				String filename = request.getString("decapplicationtable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populateDECApplication(filename);
//			}
//			
//			if( action.equals("loadAnimalsInExperiments") )
//			{
//				String filename = request.getString("experimentanimaltable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populateAnimalsInExperiments(filename);
//			}
//			
//			if( action.equals("loadPresets") )
//			{
//				String filename = request.getString("presettable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populatePreset(filename);
//			}
//			
//			if( action.equals("loadPresetAnimals") )
//			{
//				String filename = request.getString("presetanimaltable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populatePresetAnimals(filename);
//			}
//			
//			if( action.equals("loadEvents") )
//			{
//				String filename = request.getString("eventtable");
//				LoadAnimalDB myLoadAnimalDB = new LoadAnimalDB(db, this.getLogin());
//				myLoadAnimalDB.populateEvents(filename);
//			}
			
		} catch(Exception e) {
			e.printStackTrace();
			this.setError("Something went wrong while loading your legacy database: " + e.getMessage());
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
