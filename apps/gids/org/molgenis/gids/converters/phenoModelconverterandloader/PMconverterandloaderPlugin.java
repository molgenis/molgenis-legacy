/* Date:        June 25, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.gids.converters.phenoModelconverterandloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.binding.corba.wsdl.Array;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.gids.converters.GidsConvertor;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.HttpServletRequestTuple;
import org.molgenis.util.Tuple;


import plugins.emptydb.emptyDatabase;

import app.CsvImport;
import app.FillMetadata;

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
	public GidsConvertor gc;
	public String wait = "no";
	public String invName = "";
	public String [] arrayMeasurements;
	List<String> listNewMeas = new ArrayList<String>();
	private File fileData;
	private String individualName;
	private String father;
	private String mother;
	private String sampleName;
	private String TAB = "\t";
	private String COMMA = ",";
	private String SEMICOLON = ";";
	private String delimeter;
	private String [] arrayDelimeter = {",","tab",";"};
	private String [] arrayChooseTable = {"Individuals", "Samples"};
	private HashMap<String,String> hashStep2 = new HashMap<String, String>();
	private HashMap<String,String> hashTargets = new HashMap<String, String>();
	private List<String> sampleMeasList = new ArrayList<String>();
	private List<String> indvMeasList = new ArrayList<String>();
	List<String> measInDb = new ArrayList<String>();
	private HashMap<String,String> hashChangeMeas = new HashMap<String, String>();
	private String existinv;
	private String newinv;
	
	public String[] getArrayChooseTable() {
		return arrayChooseTable;
	}

	public void setArrayChooseTable(String[] arrayChooseTable) {
		this.arrayChooseTable = arrayChooseTable;
	}

	public PMconverterandloaderPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public GidsConvertor getGC() {
		return this.gc;
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_gids_converters_phenoModelconverterandloader_PMconverterandloaderPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/gids/converters/phenoModelconverterandloader/PMconverterandloaderPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request) throws DatabaseException
	{
		logger.info("#######################");
		String action = request.getString("__action");
		
		PM_Updater pm = new PM_Updater();
		
		
		try {
			if(db.query(Investigation.class).eq(Investigation.NAME, "Shared").count() ==0){
				Investigation i = new Investigation();
				i.setName("Shared");
				db.add(i);	
			}
			
			//goes to the load files into database screen
			if(action.equals("step4")){
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
			
			/* 
			 * Runs pipeline, data will be converted to 3 different files; individual.txt, measurement.txt and observedvalue.txt
			 * if all the measurements already exist, then there will be no measurement.txt made.
			 */
			if(action.equals("goToStep2")){		
				fileData = request.getFile("convertData");	
				String fileName = fileData.toString();

				BufferedReader buffy = new BufferedReader(new FileReader (fileName));
				delimeter = request.getString("delimeter");
				
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
					String line = buffy.readLine();
					arrayMeasurements = line.split(delimeter);
					
					setNewinv(request.getString("createNewInvest"));
					setExistinv(request.getString(request.getString("investigation")));
					checkInvestigation(db,request);
					
					state="inStep2";

				}			
			}
		} catch (Exception e) {
			throw new DatabaseException(e);
		}

		if(action.equals("goToStep3")){	
			listNewMeas = new ArrayList<String>();
			hashStep2.put("individual",request.getString("individual"));
			hashStep2.put("sample",request.getString("sample"));
			hashStep2.put("father",request.getString("father"));
			hashStep2.put("mother",request.getString("mother"));
			for (String e : arrayMeasurements){
				if(db.query(Measurement.class).eq(Measurement.NAME, e).count() == 0){
					if(!e.equals(hashStep2.get("individual"))&& !e.equals(hashStep2.get("sample"))&&
							!e.equals(hashStep2.get("father"))&& !e.equals(hashStep2.get("mother"))){
						
						listNewMeas.add(e);
					}
				}
			}
			
			List<Measurement> mList = db.find(Measurement.class);
			int teller=0;
			for(Measurement s:mList){
				if(!measInDb.contains(s)){
					measInDb.add(mList.get(teller).getName());
				}
				teller++;
			}
			
			
			state= "inStep3";
		}
		int teller=0;

		if(action.equals("run pipeline")){	
			try {				
				//convert csv file into different txt files
				individualName = hashStep2.get("individual");
				sampleName = hashStep2.get("sample");
				father = hashStep2.get("father");
				mother = hashStep2.get("mother");
				sampleMeasList.add(individualName);
				String knownMeas = "";
				try{
					if(listNewMeas.size()!=0){
						for (String e : listNewMeas){
							
							Boolean c = request.getBoolean("checker"+teller);
							if(c!=null){
								knownMeas = request.getString("dropbox"+teller);
								hashChangeMeas.put(e, knownMeas);
							}else{
								String bla = request.getString(e);
								if(bla.equals("Samples")){
									sampleMeasList.add(e);
								}
								else{
									indvMeasList.add(e);
								}
							teller++;
							}
						}
					}
					
					runGenerConver(fileData,invName,db,individualName, father, mother,sampleName,sampleMeasList,indvMeasList,hashChangeMeas);		    
					//state = "pipeline";
					state = "start";
					//get the server path
					File dir = gc.getDir();
					System.out.println("ABSOLUTE PATH: "  +dir.getAbsolutePath());
					//import the data into database
					try{
						CsvImport.importAll(dir, db, null);
						dir = null;
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
				db.rollbackTx();
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
		
		if (action.equals("emptyDB") ){
			try {
				if(db.count(Investigation.class)!=0){

					new emptyDatabase(db, false);
					FillMetadata.fillMetadata(db, false);
					
					this.setMessages(new ScreenMessage("empty database succesfully", true));
				}
				else{
					this.setMessages(new ScreenMessage("database is empty already", true));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
						
	}
	
	public String getExistinv() {
		return existinv;
	}

	public void setExistinv(String existinv) {
		this.existinv = existinv;
	}

	public String getNewinv() {
		return newinv;
	}

	public void setNewinv(String newinv) {
		this.newinv = newinv;
	}

	@Override
	public void reload(Database db)
	{
		
		setInvestigations(new ArrayList<Investigation>());
		try {
			setInvestigations(db.query(Investigation.class).find());
		} catch (DatabaseException e) {

			e.printStackTrace();
		} 
		
//		
	}
	

	public void checkInvestigation(Database db, Tuple request) throws DatabaseException{

		if (!request.getString("investigation").equals("") && !request.getString("investigation").equals("select investigation")) {
			invName = request.getString("investigation");	

		}
		else{
			invName = request.getString("createNewInvest");
			Investigation inve = new Investigation();
			inve.setName(newinv);
			db.add(inve);
			Protocol prot = new Protocol();
			prot.setName(invName);
			
			prot.setInvestigation(inve);
	
			db.add(prot);
//			inve.setOwns_Name("admin"); default pheno.xml does not have authorizable for Investigation anymore

			

		}
	}

	public void runGenerConver(File file, String invName, Database db,String target, String father, String mother, String sample, List<String> samplemeaslist,List<String> indvmeaslist,HashMap<String,String> hashChangeMeas){
		try {
			gc = new GidsConvertor();
			gc.converter(file, invName, db, target,father,mother, sample, samplemeaslist,indvmeaslist,hashChangeMeas);				
			
		} catch (Exception e) {
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
	
	public GidsConvertor getGc() {
		return gc;
	}


	public String getInvName() {
		return invName;
	}


	public String[] getArrayMeasurements() {
		return arrayMeasurements;
	}


	public List<String> getListNewMeas() {
		return listNewMeas;
	}

	public void setListNewMeas(List<String> listNewMeas) {
		this.listNewMeas = listNewMeas;
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

	public List<String> getMeasInDb() {
		return measInDb;
	}

	public void setMeasInDb(List<String> measInDb) {
		this.measInDb = measInDb;
	}
	
	
 
	
}
