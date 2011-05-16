/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.convertergids;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import convertors.gids.ConvertGidsToPheno;

public class ConvertDataIntoPhenoPlugin extends PluginModel<Entity>
{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Investigation> investigations;
	private String finished = null;
	

	public ConvertDataIntoPhenoPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

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

		String invName = "";
		String action = request.getString("__action");
		
		if (!request.getString("investigation").equals("") && !request.getString("investigation").equals("select investigation")) {
			invName = request.getString("investigation");
			
		}
		else{
			invName = request.getString("createNew");
			Investigation inve = new Investigation();
			inve.setName(invName);
			try {
				db.add(inve);
			} catch (DatabaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		File file = request.getFile("convertData");

		if (action.equals("convertMe") ){
			//String a = request.getAction("checker");
			try {
				ConvertGidsToPheno cgtp = new ConvertGidsToPheno();
				
				cgtp.converter(file, invName);
				finished = "finish";

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
	public void setFinished(String finished) {
		this.finished = finished;
	}
	public String getFinished() {
		return finished;
	}
	
}
