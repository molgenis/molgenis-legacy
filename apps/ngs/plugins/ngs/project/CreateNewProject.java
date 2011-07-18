///** 
// * 
// * @author Jessica Lundberg
// * @date 16-10-2010
// * 
// * This class is the model portion for creating a new Project for NGS LIMS. It receives data and calls
// * the controller to insert the data into storage. A new project consists of a Project (project name, etc),
// * a LabWorker (Contact) who is in charge, a Contact and Institution (Customer), and one or more Samples
// * which are going to be processed in the lab.
// */
//
//package plugins.ngs.project;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.List;
//
//import org.apache.log4j.Logger;
//import org.molgenis.framework.db.Database;
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.ui.PluginModel;
//import org.molgenis.framework.ui.ScreenController;
//import org.molgenis.framework.ui.ScreenMessage;
//import org.molgenis.ngs.NgsPerson;
//import org.molgenis.ngs.NgsSample;
//import org.molgenis.ngs.Project;
//import org.molgenis.organization.Institute;
//import org.molgenis.organization.Investigation;
//import org.molgenis.util.Entity;
//import org.molgenis.util.Tuple;
//
//import commonservice.CommonService;
//
//public class CreateNewProject extends PluginModel<Entity>
//{
//
//    private static final long serialVersionUID = 543108839792335414L;
//
//    private static transient Logger logger = Logger.getLogger(CreateNewProject.class);
//
//    private CreateNewProjectModel model;
//    private CommonService ct;
//    private Database db;
//
//
//    public CreateNewProject(String name, ScreenController<?> parent)
//    {
//	super(name, parent);
//	ct = CommonService.getInstance();
//	model = new CreateNewProjectModel(this);
//    }
//
//    public CreateNewProjectModel getModel() {
//	return model;
//    }
//
//
//    @Override
//    public String getViewName()
//    {
//	return "plugins_ngs_project_CreateNewProject";
//    }
//
//    @Override
//    public String getViewTemplate()
//    {
//	return "plugins/ngs/project/CreateNewProject.ftl";
//    }
//
//    @Override
//    public void handleRequest(Database db, Tuple request) {
//	ct.setDatabase(db);
//	this.db = db;
//
//	model.setAction(request.getString("__action"));
//
//	if (model.getAction().equals("Submit")) {
//	    try {
//		db.beginTx();
//
//		setInvestigation(request.getString("projectname"));
//		setInstitution(request.getString("address"), request.getString("orgname"));
//		setCustomer(request);
//		setProject(request);
//		setSamples(request);
//
//		db.commitTx();
//		this.setMessages(new ScreenMessage("Project was successfully added", true));
//		this.reload(db);
//	    } catch (DatabaseException e) {
//		try { db.rollbackTx(); } catch (DatabaseException e1) { logger.error("Could not rollback", e1); }
//
//		logger.error("An exception occured while trying to add a project", e);
//		this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
//
//	    }
//	    catch(Exception e) {
//		e.printStackTrace();
//	    }
//
//	}
//
//    }
//    
//    @Override
//    public void reload(Database db)
//    {
//	ct.setDatabase(db);
//
//	try
//	{
//	    model.getLabworkers().clear();
//	    model.getWorkflows().clear();// not very efficient..
//	    model.getLabworkers().addAll(ct.getAllLabWorkers());
//	    model.getWorkflows().addAll(ct.getAllWorkflows());
//	}
//	catch (Exception e)
//	{
//	    String msg = "An exception occured while trying to retrieve all lab workers and workflows from the database ";
//	    logger.error(msg, e);
//	    this.setMessages(new ScreenMessage(msg != null ? msg : "null", false));
//	}
//
//    }
//
//    /** Create new Sample entities for a project and add them to the database
//     * 
//     * @param request
//     * @throws DatabaseException
//     * @throws ParseException
//     * @throws IOException
//     */
//    private void setSamples(Tuple request) throws DatabaseException, ParseException, IOException {
//	
//	int numSamples = request.getInt("numsamples");
//	
//	for (int i = 1; i <= numSamples; ++i)
//	{
//	    NgsSample sample = new NgsSample();
//	    String sampleName = request.getString("sampname") + i;
//
//
//	    try {
//		sample.setName(sampleName);
//		sample.setLocation(request.getString("location"));
//		sample.set__Type("NgsSample");
//		sample.setProject(model.getProject());
//		sample.setDescription(request.getString("descbox"));
//                sample.setFragmentlength(new
//			Integer(request.getString("fraglength")));
//		sample.setOrigin(request.getString("origin"));
//		sample.setReadlength(new
//			Integer(request.getString("readlength")));
//		sample.setSampletype(request.getString("typesample"));
//		sample.setInvestigation_Name(model.getProject().getName());
//		sample.setInvestigation(model.getInvest());
//		sample.setWorkflowElement(ct.getWorkflowElement("Begin Workflow"));
//		db.add(sample);
//		
//	    } catch(Exception e) {
//		try {
//		    ct.getSampleByName(sampleName);
//		    throw new DatabaseException("A sample with this name: " + sampleName + " already exists, " +
//			"please use a different name");
//		} catch(DatabaseException dbex) {
//		    throw new DatabaseException("Sample could not be added, but does not already exist");
//		    
//		}
//	    }
//	    
//	    
//
//	}
//	
//    }
//
//    /** Create a new Project and add it to the database;
//     * 
//     * @param request
//     * @throws DatabaseException
//     */
//    private void setProject(Tuple request) throws DatabaseException {
//	Project project = new Project();
//	try {
//
//	    project.setInvestigation(model.getInvest());
//	    project.setContractcode(request.getString("contractcode"));
//	    project.setEnddate(request.getString("enddate"));
//	    project.setStartdate(request.getString("startdate"));
//	    project.setName(request.getString("projectname"));
//	    project.setPanelType(ct.getOntologyTerm("Project"));
//	    project.setCustomer(model.getPerson());
//	    project.setWorkflow(ct.getWorkflow(request.getString("workflow")));
//
//	    model.setProjectName(request.getString("projectname"));
//
//	    // figure out which lab worker to assign..
//	    String name = request.getString("labworker");
//	    String[] names = name.split("\\s", 2);
//	    String firstName = "";
//	    String lastName = "";
//	    if (names.length == 2)
//	    {
//		firstName = names[0];
//		lastName = names[1];
//	    }
//
//	    List<NgsPerson> contacts = ct.getLabWorkerByName(firstName, lastName);
//	    project.setLabworker_Id(contacts.get(0));
//	} catch(Exception e) {
//	    throw new DatabaseException("Something went wrong when trying to create a project");
//	}
//
//	
//	try {
//	    db.add(project);
//	    model.setProject(project);
//	} catch(DatabaseException d) {
//	    throw new DatabaseException("Project with contractcode " + project.getContractcode() + " already exists, please pick a different code");
//	   
//	} catch(Exception e) {
//	    logger.warn(e);
//	}
//    }
//
//    
//    /** Create a new customer (NgsPerson) and add it to the database. If Customer already
//     * exists, use existing customer.
//     * 
//     * @param request
//     * @throws DatabaseException
//     */
//    private void setCustomer(Tuple request) throws DatabaseException {
//	NgsPerson person = new NgsPerson();
//	person.setFirstName(request.getString("firstname"));
//	person.setLastName(request.getString("lastname"));
//	person.setEmail(request.getString("email"));
//	person.setPhone(request.getString("telephone"));
//	person.setAffiliation(model.getInstitute());
//	person.set__Type("NgsPerson");
//
//	try {
//	    model.setPerson(ct.getPerson(person.getFirstName(), person.getLastName()));
//	   
//	} catch(DatabaseException d) {
//	   try {
//	       db.add(person);
//	       model.setPerson(person);
//	   } catch(Exception ex) {
//	       throw new DatabaseException("Could not find Person in db, but also could not add");
//	   }
//	} catch(Exception e) {
//	    logger.warn(e);
//	}
//
//    }
//
//    
//    /** Create a new Institution and add it to the database. If Institute already exists
//     * in the database, use existing entry.
//     * 
//     * @param address
//     * @param orgname
//     * @throws DatabaseException
//     */
//    private void setInstitution(String address, String orgname) throws DatabaseException {
//
//	Institute institute = new Institute();
//	institute.setAddress(address);
//	institute.setName(orgname);
//
//	try {
//	    model.setInstitute(ct.getInstitute(orgname));
//	} catch(DatabaseException d) {
//	    try {
//		db.add(institute);
//		model.setInstitute(institute);
//	    } catch(Exception ex) {
//		throw new DatabaseException("Could not find institute but could not add it either");
//	    }
//	} catch(Exception e) {
//	    logger.warn(e);
//	}
//
//    }
//
//    
//    /** Create a new investigation and add it to the database.
//     * 
//     * @param projectName
//     * @throws DatabaseException
//     */
//    private void setInvestigation(String projectName) throws DatabaseException {
//	Investigation invest = new Investigation();
//	invest.setName(projectName);
//
//	try {
//	    db.add(invest);
//	    model.setInvest(invest);
//	    logger.info("Adding a new investigation");
//	} catch(DatabaseException d) {
//	    logger.info("Investigation already exists");
//	    throw new DatabaseException("A project with this name already exists; please choose a different name");
//	} catch(Exception e) {
//	    logger.warn(e);
//	}
//
//    }
//
//    @Override
//    public boolean isVisible()
//    {
//    	return true;
//    }
//
//    public void clearMessage()
//    {
//    	this.setMessages();
//    }
//}
