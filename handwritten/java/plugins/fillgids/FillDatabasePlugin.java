/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.fillgids;

import java.io.File;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;

import app.CsvImport;

public class FillDatabasePlugin extends PluginModel<Entity>
{
	private static final long serialVersionUID = -5634663322794444817L;
	
	private File roanDir;
	

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

		String action = request.getString("__action");
		logger.info("##############   " + roanDir.toString());
		
		if (action.equals("emptyDB") ){
			try {
				if(db.count(Investigation.class)!=0){
					new emptyDatabase(db, true);
					this.setMessages(new ScreenMessage("empty database succesfully", true));
				}
				else{
					this.setMessages(new ScreenMessage("database is empty already", true));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		if(action.equals("loadAll")){
			
			try {
				File fileInd = request.getFile("readind");
				if(fileInd != null){
					File moveInd = new File(roanDir + File.separator + fileInd.getName());
					boolean moveSuccessInd = fileInd.renameTo(moveInd);
					String originalNameInd = request.getString("readindOriginalFileName");
					boolean renameSuccessInd = moveInd.renameTo(new File(roanDir + File.separator + originalNameInd));
				}
				
				
				File fileMeas = request.getFile("readmeas");
				if(fileMeas != null){
					File moveMeas = new File(roanDir + File.separator + fileMeas.getName());
					boolean moveSuccessMeas = fileMeas.renameTo(moveMeas);
					String originalNameMeas = request.getString("readmeasOriginalFileName");
					boolean renameSuccessMeas = moveMeas.renameTo(new File(roanDir + File.separator + originalNameMeas));
				}
				
				
				File fileVal = request.getFile("readval");		
				if(fileVal != null){
					File moveVal = new File(roanDir + File.separator + fileVal.getName());						
					boolean moveSuccessVal = fileVal.renameTo(moveVal);								
					String originalNameVal = request.getString("readvalOriginalFileName");							
					boolean renameSuccessVal = moveVal.renameTo(new File(roanDir + File.separator + originalNameVal));
				}
				
				
				CsvImport.importAll(roanDir, db, null);
				this.setMessages(new ScreenMessage("data succesfully added to the database", true));
				roanDir = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
		
	}

	@Override
	public void reload(Database db)
	{
		if(roanDir == null){
		 roanDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "roan_"+System.nanoTime());
		 boolean success = roanDir.mkdir();
		 if(!success){
			 //message of error of zoiets
		 }
		}

	}
	
}
