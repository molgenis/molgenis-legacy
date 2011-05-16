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
		
		
		if (action.equals("deleteDB") ){
			try {
				new emptyDatabase((app.JDBCDatabase) db, true);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		
		
		
		
		if(action.equals("loadind")){
			File bla = request.getFile("readind");
			File moveBla = new File(roanDir + File.separator + bla.getName());
			//move the file
			boolean moveSuccess = bla.renameTo(moveBla);
			if(!moveSuccess){
				logger.error("MOVE readind PHAIL");
				//error oid... gaat vaak fout onder windoos!!!! java bug!!
			}
			
			String originalName = request.getString("readindOriginalFileName");
			boolean renameSuccess = moveBla.renameTo(new File(roanDir + File.separator + originalName));
			
			if(!renameSuccess){
				logger.error("RENAME readind PHAIL");
				//error oid... gaat vaak fout onder windoos!!!! java bug!!
			}
		
			
		}
		if(action.equals("loadmeas")){
			File bla = request.getFile("readmeas");
			File moveBla = new File(roanDir + File.separator + bla.getName());
			//move the file
			boolean moveSuccess = bla.renameTo(moveBla);
			if(!moveSuccess){
				logger.error("MOVE readmeas PHAIL");
				//error oid... gaat vaak fout onder windoos!!!! java bug!!
			}
			
			String originalName = request.getString("readmeasOriginalFileName");
			boolean renameSuccess = moveBla.renameTo(new File(roanDir + File.separator + originalName));
			
			if(!renameSuccess){
				logger.error("RENAME readmeas PHAIL");
				//error oid... gaat vaak fout onder windoos!!!! java bug!!
			}
		
			
		}
		if(action.equals("loadval")){
			File bla = request.getFile("readval");
			File moveBla = new File(roanDir + File.separator + bla.getName());
			//move the file
			boolean moveSuccess = bla.renameTo(moveBla);
			if(!moveSuccess){
				logger.error("MOVE readval PHAIL");
				//error oid... gaat vaak fout onder windoos!!!! java bug!!
			}
			
			String originalName = request.getString("readvalOriginalFileName");
			boolean renameSuccess = moveBla.renameTo(new File(roanDir + File.separator + originalName));
			
			if(!renameSuccess){
				logger.error("RENAME readval PHAIL");
				//error oid... gaat vaak fout onder windoos!!!! java bug!!
			}
			
		}
		if(action.equals("loadAll")){
			
			try {
				CsvImport.importAll(roanDir, db, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
