/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.phenoModelconverterandloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.organization.Investigation;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;

import plugins.emptydb.emptyDatabase;

import app.CsvImport;

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
	public String status = "bla";
	public GenericConvertor gc;
	public String wait = "no";
	public String invName = "";
	public String [] arrayMeasurements;
	private File fileData;
	private String target;
	private String father;
	private String mother;
	private String TAB = "\t";
	private String COMMA = ",";
	private String SEMICOLON = ";";
	private String delimeter;
	private String [] arrayDelimeter = {",","tab",";"};

	public PMconverterandloaderPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public GenericConvertor getGC() {
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
		logger.info("#######################");
		String action = request.getString("__action");


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
		if(action.equals("step3")){
			state = "skip";
			status = "bla";
		}
		
		if(action.equals("skip")){
			state = "skip";
			status = "bla";
		}
		if (action.equals("clean") ){		
			//state = start;
			status = "bla";
		}
		//goes back to the startscreen
		if(action.equals("reset")){
			state = "start";
			status = "bla";
		}
		
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
		
		/* 
		 * Runs pipeline, data will be converted to 3 different files; individual.txt, measurement.txt and observedvalue.txt
		 * if all the measurements already exist, then there will be no measurement.txt made.
		 */
		if(action.equals("update")){		
			fileData = request.getFile("convertData");	
			String fileName = fileData.toString();
			try {
				BufferedReader buffy = new BufferedReader(new FileReader (fileName));
				delimeter = request.getString("delimeter");
				logger.info("$$$$$#########" + delimeter);
				if(delimeter.equals("tab")){
					delimeter = TAB;
				}
				if(delimeter.equals(",")){
					delimeter = COMMA;
				}
				if(delimeter.equals(";")){
					delimeter = SEMICOLON;
				}
				else if(delimeter.equals("choose the delimeter")){
					this.setMessages(new ScreenMessage("No delimeter is chosen, delimeter is set to semicolon", true));
					delimeter = SEMICOLON;
				}
				for(int x =0; x<1; x++){
					try {
						String line = buffy.readLine();
						arrayMeasurements = line.split(delimeter);
						checkInvestigation(db, request);
						state="updated";
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}
		if(action.equals("pipeline")){	
			try {
				//convert csv file into different txt files
				target = request.getString("target");	
				father = request.getString("father");
				mother = request.getString("mother");
				try{
					runGenerConver(fileData,invName,db,target,father,mother);		    
					state = "pipeline";
					
					//get the server path
					File dir = gc.getDir();
					
					//import the data into database
					try{
						CsvImport.importAll(dir, db, null);
						dir = null;
						//this.setMessages(new ScreenMessage("data succesfully added to the database", true));
						redirectToInvestigationPage(request);
					}catch(Exception e){
						if(e.getMessage().startsWith("Tried to add existing Individual elements as new insert:")){
							this.setMessages(new ScreenMessage("The individuals already exist in the database", false));
						}
					}
				}
				catch(Exception e){
					this.setMessages(new ScreenMessage(e.getMessage(), false));
				}

			} catch (Exception e) {
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
		
		/*
		 * Create downloadable links
		 */
		if (action.equals("downloads") ){
			try {
				target = request.getString("target");
				father = request.getString("father");
				mother = request.getString("mother");
				//convert csv file into different txt files
				
				runGenerConver(fileData,invName,db,target,father,mother);				
				status = "downloaded";
				
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
			invName = request.getString("createNewInvest");
			Investigation inve = new Investigation();
			inve.setName(invName);
//			inve.setOwns_Name("admin"); default pheno.xml does not have authorizable for Investigation anymore
			try {
				
				db.add(inve);
				
			} catch (DatabaseException e) {
				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	public void runGenerConver(File file, String invName, Database db,String target, String father, String mother){
		try {
			gc = new GenericConvertor();
			gc.converter(file, invName, db, target,father,mother);				
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}
	
	public void redirectToInvestigationPage(Tuple request){

		HttpServletRequestTuple rt       = (HttpServletRequestTuple) request;
		HttpServletRequest httpRequest   = rt.getRequest();

		// get the http response that is used in this handleRequest
		HttpServletResponse httpResponse = rt.getResponse();
		
		String returnURL = httpRequest.getRequestURL() + "?__target=" + this.getName() + "&__action=mainmenu&select=investigation";
		try {
			httpResponse.sendRedirect(returnURL);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public GenericConvertor getGc() {
		return gc;
	}


	public String getInvName() {
		return invName;
	}


	public String[] getArrayMeasurements() {
		return arrayMeasurements;
	}


	public void setStatus(String status) {
		this.status = status;
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
	public String getStatus() {
		return status;
	}

	public String[] getArrayDelimeter() {
		return arrayDelimeter;
	}
	
 
	
}
