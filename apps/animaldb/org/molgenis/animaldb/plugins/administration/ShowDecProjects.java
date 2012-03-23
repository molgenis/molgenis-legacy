/* Date:        September 14, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.animaldb.plugins.administration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.molgenis.animaldb.commonservice.CommonService;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;


public class ShowDecProjects extends PluginModel<Entity>
{
	private static final long serialVersionUID = 1906962555512398640L;
	private List<DecProject> decappList = new ArrayList<DecProject>();
	private CommonService ct = CommonService.getInstance();
	private String action = "init";
	private int listId = 0;
	
	public ShowDecProjects(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}
	
	public String getCustomHtmlHeaders() {
		return "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.dataTables.js\"></script>\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/jquery-plugins/datatables/css/demo_table_jui.css\">\n" +
				"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/animaldb.css\">";
	}
	
	@Override
	public String getViewName()
	{
		return "org_molgenis_animaldb_plugins_administration_ShowDecProjects";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/animaldb/plugins/administration/ShowDecProjects.ftl";
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

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		ct.setDatabase(db);
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
				//SimpleDateFormat oldDateOnlyFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
				SimpleDateFormat newDateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
				SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
				// Get values from form
				
				// Name
				String name = "DEC ";
				if (request.getString("name") != null && !request.getString("name").equals("")) {
					name = request.getString("name");
				}
					
				// DEC number
				String decnumber = "";
				if (request.getString("decnumber") != null && !request.getString("decnumber").equals("")) {
					decnumber = request.getString("decnumber");
				} else {
					throw(new Exception("No DEC number given - project not added"));
				}
				
				// DEC title
				String dectitle = "";
				if (request.getString("dectitle") != null && !request.getString("dectitle").equals("")) {
					dectitle = request.getString("dectitle");
				} else {
					throw(new Exception("No DEC title given - project not added"));
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
				
				// Start date
				Date startdate = null;
				if (request.getString("startdate") != null) {
					//String startdateString = request.getString("startdate");
					startdate = newDateOnlyFormat.parse(request.getString("startdate"));
					
				} else {
					throw(new Exception("No start date given - project not added"));
				}
				
				// End date
				Date enddate = null;
				if (request.getString("enddate") != null) {
					//String enddateString = request.getString("enddate");
					enddate = newDateOnlyFormat.parse(request.getString("enddate"));
				}
				
				// Some variables we need later on
				Integer decapplicantId = this.getLogin().getUserId();
				String investigationName = ct.getOwnUserInvestigationName(this.getLogin().getUserName());
				Date now = new Date();
				
				// Init lists that we can later add to the DB at once
				List<ObservedValue> valuesToAddList = new ArrayList<ObservedValue>();
				
				// Check if edit or add
				String projectName;
				if (listId == 0) {
					// autogenerate name to be the DEC id prepended with "DEC "
					projectName = name + decnumber;
					// Make new DEC project
					ct.makePanel(investigationName, projectName, this.getLogin().getUserName());
					valuesToAddList.add(ct.createObservedValueWithProtocolApplication(investigationName, 
							now, null, "SetTypeOfGroup", "TypeOfGroup", projectName, "DecApplication", null));
				} else {
					// Get existing DEC project
					projectName = getDecProjectByListId().getName();
				}
				
				// Set values
				// Nice feature of pheno model: we don't have to overwrite the old values
				// We just make new ones and the most recent ones count!
				// TODO: this is not entirely true anymore, now that value dates
				// are date-only, without time info, so values from the same day
				// cannot be distinguished anymore!
				ProtocolApplication app = ct.createProtocolApplication(investigationName, "SetDecProjectSpecs");
				db.add(app);
				String protocolApplicationName = app.getName();
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "DecNr", projectName, decnumber, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "DecTitle", projectName, dectitle, null));
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "DecApplicantId", projectName, decapplicantId.toString(), null));
				if (decapplicationpdf != null) {
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
							enddate, "DecApplicationPdf", projectName, decapplicationpdf, null));
				}
				if (decapprovalpdf != null) {
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
							enddate, "DecApprovalPdf", projectName, decapprovalpdf, null));
				}
				valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
						enddate, "StartDate", projectName, dbFormat.format(startdate), null));
				if (enddate != null) {
					valuesToAddList.add(ct.createObservedValue(investigationName, protocolApplicationName, startdate, 
							enddate, "EndDate", projectName,  dbFormat.format(enddate), null));
				}
				// Add everything to DB
				db.add(valuesToAddList);
				
				if (listId == 0) {
					this.setSuccess("DEC project successfully added");
				} else {
					this.setSuccess("DEC project successfully updated");
				}
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
			List<String> investigationNames = ct.getWritableUserInvestigationNames(this.getLogin().getUserName());
			// Populate DEC projects list
			decappList.clear();
			List<ObservationTarget> decList = ct.getAllMarkedPanels("DecApplication", investigationNames);
			int pos = 1;
			for (ObservationTarget currentDec : decList) {
				String name = currentDec.getName();
				DecProject tmpDec = new DecProject();
				tmpDec.setId(currentDec.getId());
				tmpDec.setDecAppListId(pos);
				tmpDec.setName(name);
				String decNr = ct.getMostRecentValueAsString(name, "DecNr");
				if (decNr != null) tmpDec.setDecNr(decNr);
				String decTitle = ct.getMostRecentValueAsString(name, "DecTitle");
				if (decTitle != null) tmpDec.setDecTitle(decTitle);
				String decApplicantString = ct.getMostRecentValueAsString(name, "DecApplicantId");
				if (decApplicantString != null && !decApplicantString.equals("")) {
					MolgenisUser applicant = db.findById(MolgenisUser.class, Integer.parseInt(decApplicantString));
					tmpDec.setDecApplicantName(applicant.getName());
				} else {
					tmpDec.setDecApplicantName("");
				}
				String pdfDecApplication = "";
				pdfDecApplication = ct.getMostRecentValueAsString(name, "DecApplicationPdf");
				if (pdfDecApplication != null) tmpDec.setPdfDecApplication(pdfDecApplication);
				String pdfDecApproval = "";
				pdfDecApproval = ct.getMostRecentValueAsString(name, "DecApprovalPdf");
				if (pdfDecApproval != null) tmpDec.setPdfDecApproval(pdfDecApproval);
				String startDate = ct.getMostRecentValueAsString(name, "StartDate");
				if (startDate != null) tmpDec.setStartDate(startDate);
				String endDate = ct.getMostRecentValueAsString(name, "EndDate");
				if (endDate != null) tmpDec.setEndDate(endDate);
				
				decappList.add(tmpDec);
				
				pos++;
			}
			
			// Populate list of Actors with Article 9 status
			// TODO: find a way to handle the Article 9 status now that we don't have Actors anymore
			// For now we solve it by setting the ID of the current user as the Applicant ID
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
