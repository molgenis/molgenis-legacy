/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.phenoModelconverterandloaderLifelines;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;

import app.CsvImport;

import convertors.CopyOfGenericConvertor;
import convertors.GenericConvertor;

/**
 * 
 * @author roankanninga
 *
 */
public class PMconverterandloaderPlugin extends PluginModel<Entity>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Investigation> investigations;
	/*
	 * state can be start, downloaded, pipeline and skip
	 * start = startscreen -> convertwindow
	 * pipeline = starts the pipeline, convert + load into database
	 * skip = skips the convertwindow and goes immediately to load files into database window
	 */
	private String state = "start";
	public CopyOfGenericConvertor gc;
	public String wait = "no";
	public String invName = "";
	
	

	public PMconverterandloaderPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public CopyOfGenericConvertor getGC() {
		return this.gc;
	}

	@Override
	public String getViewName()
	{
		return "plugins_phenoModelconverterandloader_PMconverterandloaderPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/phenoModelconverterandloader/PMconverterandloaderPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		String action = request.getString("__action");
		File file = request.getFile("convertData");	
		try {
			if(db.query(Investigation.class).eq(Investigation.NAME, "System").count() == 0){
				Investigation i = new Investigation();
				i.setName("System");
				db.add(i);	
			}
		} catch (DatabaseException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//goes to the load files into database screen
		if(action.equals("skip")){
			state = "skip";
		}
		if (action.equals("clean") ){		
			state = null;
		}
		//goes back to the startscreen
		if(action.equals("reset")){
			state = "start";
		}
		
		if (action.equals("emptyDB") ){
			try {
				if(db.count(Investigation.class)!=0){
					wait = "yes";
					new emptyDatabase(db, true);
					wait ="no";
					
					this.setMessages(new ScreenMessage("empty database succesfully", true));
				}
				else{
					this.setMessages(new ScreenMessage("database is empty already", true));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		
		/* 
		 * Runs pipeline, data will be converted to 3 different files; individual.txt, measurement.txt and observedvalue.txt
		 * if all the measurements already exist, then there will be no measurement.txt made.
		 */
		if(action.equals("pipeline")){	
			checkInvestigation(db, request);

			try {
				//convert csv file into different txt files
				runGenerConver(file,invName,db);		    
				state = "pipeline";
				
				//get the server path
				File dir = gc.getDir();
				
				//import the data into database
				CsvImport.importAll(dir, db, null);
				dir = null;
				this.setMessages(new ScreenMessage("data succesfully added to the database", true));

			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
		
		/*
		 * Create downloadable links
		 */
		if (action.equals("downloads") ){
			checkInvestigation(db, request);
			
			try {
				//convert csv file into different txt files
				runGenerConver(file,invName,db);				
				state = "downloaded";

			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
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
	
	public void checkInvestigation(Database db, Tuple request){
		if (!request.getString("investigation").equals("") && !request.getString("investigation").equals("select investigation")) {
			invName = request.getString("investigation");			
		}
		else{
			invName = request.getString("createNew");
			Investigation inve = new Investigation();
			inve.setName(invName);
			inve.setOwns_Name("admin");
			try {
				db.add(inve);
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	public void runGenerConver(File file, String invName, Database db){
		try {
			gc = new CopyOfGenericConvertor();
			gc.converter(file, invName, db);				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	public void setInvestigations(List<Investigation> investigations) {
		this.investigations = investigations;
	}

	public List<Investigation> getInvestigations() {
		return investigations;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getState() {
		return state;
	}
	
}
