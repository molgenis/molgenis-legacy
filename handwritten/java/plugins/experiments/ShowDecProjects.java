/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.experiments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Panel;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import commonservice.CommonService;

public class ShowDecProjects extends PluginModel<Entity>
{
	private static final long serialVersionUID = 1906962555512398640L;
	private List<DecProject> decappList = new ArrayList<DecProject>();
	private List<ObservationTarget> decApplicantList = new ArrayList<ObservationTarget>();
	private CommonService ct = CommonService.getInstance();
	private String action = "init";
	private int listId = 0;
	
	public ShowDecProjects(String name, ScreenController<?> parent)
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
		return "plugins_experiments_ShowDecProjects";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/experiments/ShowDecProjects.ftl";
	}
	
	public int getListId()
	{
		return listId;
	}

	public void setListId(int listId)
	{
		this.listId = listId;
	}

	public void setDecappList(List<DecProject> decappList) {
		this.decappList = decappList;
	}

	public List<DecProject> getDecappList() {
		return decappList;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setDecApplicantList(List<ObservationTarget> decApplicantList) {
		this.decApplicantList = decApplicantList;
	}

	public List<ObservationTarget> getDecApplicantList() {
		return decApplicantList;
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try {
			this.setAction(request.getAction());
		
			if (action.equals("AddEdit"))
			{
				int id = request.getInt("id");
				listId = id;
			}
			if (action.equals("Show"))
			{
				// No action here
			}
			if (action.equals("addEditDecProject")) {
				SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy, HH:mm:ss", Locale.US);
				
				// Get values from form
				
				// Name
				String name = "";
				if (request.getString("name") != null && !request.getString("name").equals("")) {
					name = request.getString("name");
				} else {
					throw(new Exception("No name given - project not added"));
				}
					
				// DEC number
				String decnumber = "";
				if (request.getString("decnumber") != null && !request.getString("decnumber").equals("")) {
					decnumber = request.getString("decnumber");
				} else {
					throw(new Exception("No DEC number given - project not added"));
				}
				
				// DEC application PDF
				String decapplicationpdf = null;
				if (request.getString("decapplicationpdf") != null && !request.getString("decapplicationpdf").equals("")) {
					decapplicationpdf = request.getString("decapplicationpdf");
				}
				
				// DEC approval PDF
				String decapprovalpdf = null;
				if (request.getString("decapprovalpdf") != null && !request.getString("decapprovalpdf").equals("")) {
					decapprovalpdf = request.getString("decapprovalpdf");
				}
				
				// Start date-time
				String starttimeString = "";
				Date starttime = null;
				if (request.getString("starttime") != null) {
					starttimeString = request.getString("starttime");
					starttime = sdf.parse(starttimeString);
				} else {
					throw(new Exception("No start date given - project not added"));
				}
				
				// End date-time
				Date endtime = null;
				String endtimeString = null;
				if (request.getString("endtime") != null) {
					endtimeString = request.getString("endtime");
					endtime = sdf.parse(endtimeString);
				}
				
				// Some variables we need later on
				int investigationId = ct.getInvestigationId("AnimalDB");
				Calendar myCal = Calendar.getInstance();
				Date now = myCal.getTime();
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Check if edit or add
				int projectId;
				if (listId == 0) {
					// Make new DEC project
					projectId = ct.makePanel(investigationId, name, this.getLogin().getUserId());
					int protocolId = ct.getProtocolId("SetTypeOfGroup");
					int measurementId = ct.getMeasurementId("TypeOfGroup");
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(investigationId, 
							now, null, protocolId, measurementId, projectId, "DecApplication", 0));
				} else {
					// Get existing DEC project
					projectId = ct.getObservationTargetId(getDecProjectByListId().getName());
				}
				
				// Set values
				// Nice feature of pheno model: we don't have to overwrite the old values
				// We just make new ones and the most recent ones count!
				int protocolId = ct.getProtocolId("SetDecProjectSpecs");
				ProtocolApplication app = ct.createProtocolApplication(investigationId, protocolId);
				db.add(app);
				int protocolApplicationId = app.getId();
				int measurementId = ct.getMeasurementId("DecNr");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, decnumber, 0));
				if (decapplicationpdf != null) {
					measurementId = ct.getMeasurementId("DecApplicationPdf");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
							endtime, measurementId, projectId, decapplicationpdf, 0));
				}
				if (decapprovalpdf != null) {
					measurementId = ct.getMeasurementId("DecApprovalPdf");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
							endtime, measurementId, projectId, decapprovalpdf, 0));
				}
				measurementId = ct.getMeasurementId("StartDate");
				valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
						endtime, measurementId, projectId, starttimeString, 0));
				if (endtimeString != null) {
					measurementId = ct.getMeasurementId("EndDate");
					valuesToAddList.add(ct.createObservedValue(investigationId, protocolApplicationId, starttime, 
							endtime, measurementId, projectId, endtimeString, 0));
				}
				// Add everything to DB
				db.add(valuesToAddList);
				
				// Reload, so list is refreshed
				this.getMessages().clear();
				this.getMessages().add(new ScreenMessage("DEC Project successfully added", true));
				this.reload(db);
			}
			
		} catch (Exception e) {
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
			e.printStackTrace();
		}
	}

	@Override
	public void reload(Database db)
	{
		ct.setDatabase(db);
		
		try {
			// Populate DEC projects list
			decappList.clear();
			List<Panel> decList = ct.getAllMarkedPanels("DecApplication");
			int pos = 1;
			for (Panel currentDec : decList) {
				String name = currentDec.getName();
				
				DecProject tmpDec = new DecProject();
				// set id
				tmpDec.setId(currentDec.getId());
				// set dec app list id
				tmpDec.setDecAppListId(pos);
				// set name
				tmpDec.setName(name);
				// set decnr
				int featureId = ct.getMeasurementId("DecNr");
				String decNr = ct.getMostRecentValueAsString(currentDec.getId(), featureId);
				if (decNr != null) tmpDec.setDecNr(decNr);
				// set decapplicant
				featureId = ct.getMeasurementId("DecApplicantId");
				String decApplicantString = ct.getMostRecentValueAsString(currentDec.getId(), featureId);
				if (decApplicantString != null && !decApplicantString.equals("")) {
					ObservationTarget applicant = ct.getObservationTargetById(Integer.parseInt(decApplicantString));
					tmpDec.setDecApplicantName(applicant.getName());
				} else {
					tmpDec.setDecApplicantName("");
				}
				// set pdfdecapplication
				String pdfDecApplication = "";
				featureId = ct.getMeasurementId("DecApplicationPdf");
				pdfDecApplication = ct.getMostRecentValueAsString(currentDec.getId(), featureId);
				if (pdfDecApplication != null) tmpDec.setPdfDecApplication(pdfDecApplication);
				// set pdfdecapproval
				String pdfDecApproval = "";
				featureId = ct.getMeasurementId("DecApprovalPdf");
				pdfDecApproval = ct.getMostRecentValueAsString(currentDec.getId(), featureId);
				if (pdfDecApproval != null) tmpDec.setPdfDecApproval(pdfDecApproval);
				// set start date
				featureId = ct.getMeasurementId("StartDate");
				String startDate = ct.getMostRecentValueAsString(currentDec.getId(), featureId);
				if (startDate != null) tmpDec.setStartDate(startDate);
				// set end date
				featureId = ct.getMeasurementId("EndDate");
				String endDate = ct.getMostRecentValueAsString(currentDec.getId(), featureId);
				if (endDate != null) tmpDec.setEndDate(endDate);
				
				decappList.add(tmpDec);
				
				pos++;
			}
			
			// Populate list of Actors with Article 9 status
			// TODO: find a way to handle the Article 9 status now that we don't have Actors anymore
			/*
			decApplicantList.clear();
			List<Integer> actorIdList = ct.getAllObservationTargetIds("Actor", false);
			List<ObservationTarget> tmpApplicantList = ct.getObservationTargets(actorIdList);
			for (ObservationTarget applicant : tmpApplicantList) {
				int featureId = ct.getMeasurementId("Article");
				try {
					String decApplicantString = ct.getMostRecentValueAsString(applicant.getId(), featureId);
					if (decApplicantString.equals("9")) {
						decApplicantList.add(applicant);
					}
				} catch (Exception e) {
					//
				}
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
			this.getMessages().clear();
			if (e.getMessage() != null) {
				this.getMessages().add(new ScreenMessage(e.getMessage(), false));
			}
		}
	}
	
	public DecProject getDecProjectByListId() {
		if (listId == 0) return null;
		return decappList.get(listId - 1);
	}
	
}
