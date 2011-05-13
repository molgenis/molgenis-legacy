/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.convertergids;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.Molgenis;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.gids.GidsUpdateDatabase;

import org.molgenis.organization.Investigation;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;
import plugins.fillanimaldb.FillAnimalDB;

import app.CsvImport;
import app.JDBCDatabase;

import convertors.gids.ConvertGidsToPheno;
import convertors.ulidb.ConvertUliDbToPheno;

public class ConvertDataIntoPhenoPlugin extends PluginModel<Entity>
{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private File roanDir;
	private List<Investigation> investigations;
	

	public ConvertDataIntoPhenoPlugin(String name, ScreenController<?> parent)
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
		return "plugins_convertergids_ConvertDataIntoPhenoPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/convertergids/ConvertDataIntoPhenoPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		Investigation chosenInv;
		String invName = "";
		String action = request.getString("__action");
		
		if (!request.getString("investigation").equals("") && !request.getString("investigation").equals("select investigation")) {
			String invId = request.getString("investigation");
			invName = request.getString("investigation");
			logger.info("******** invId" + invId);
		}
		else{
			logger.info("*****###*** invId");
			invName = request.getString("createNew");
			logger.info("*****###*** CREATE NEW:  " + invName);
		}
		
		logger.info("inVNAMENEEAMSD " + invName);
		StringBuilder stbu = new StringBuilder();
		File filename = request.getFile("convertData");
		String file = filename.toString();
		String []array = file.split("/");
		for(int i=0; i <array.length-1; i++){
			stbu.append(array[i]+"/");
		}
		String folder = stbu.toString();
		logger.info("**********  folder  "+folder);
		if (action.equals("convertMe") ){
			//String a = request.getAction("checker");
			try {
				
				ConvertGidsToPheno cgtp = new ConvertGidsToPheno();
				cgtp.converter(file,folder,invName);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		
		
		
	}

	@Override
	public void reload(Database db)
	{
		setInvestigations(new ArrayList<Investigation>());
		try {
			setInvestigations(db.query(Investigation.class).find());
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
	}
	
	public void setInvestigations(List<Investigation> investigations) {
		this.investigations = investigations;
	}

	public List<Investigation> getInvestigations() {
		return investigations;
	}
	
}
