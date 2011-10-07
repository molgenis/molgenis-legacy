/**
 * @author Jessica Lundberg
 * @date 20 Jan, 2011
 * 
 * A model class for CreateNewProject
 * 
 */
package plugins.ngs.project;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;
import org.molgenis.organization.DeprecatedPerson;
import org.molgenis.organization.Institute;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.Workflow;
import org.testng.collections.Lists;

public class CreateNewProjectModel extends SimpleScreenModel {
    public CreateNewProjectModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private Investigation project;
    private DeprecatedPerson person;
    private Institute institute;
    private List<DeprecatedPerson> labworkers = Lists.newArrayList();
    private String action = "init";
    private List<Investigation> investigation = new ArrayList<Investigation>();
    private Investigation invest;
    private String projectName = "";
    private List<Workflow> workflows = new ArrayList<Workflow>();



    public void setLabworkers(List<DeprecatedPerson> labworkers)
    {
	this.labworkers = labworkers;
    }

    public List<DeprecatedPerson> getLabworkers()
    {
	return labworkers;
    }

    public void setAction(String action)
    {
	this.action = action;
    }

    public String getAction()
    {
	return this.action;
    }

    public void setInvestigation(List<Investigation> investigation)
    {
	this.investigation = investigation;
    }

    public List<Investigation> getInvestigation()
    {
	return investigation;
    }

    public void setProjectName(String projectname)
    {
	this.projectName = projectname;
    }

    public String getProjectName()
    {
	return projectName;
    }

    public void setWorkflows(List<Workflow> workflows)
    {
	this.workflows = workflows;
    }

    public List<Workflow> getWorkflows()
    {
	return workflows;
    }

    public void setProject(Investigation project) {
	this.project = project;
    }

    public Investigation getProject() {
	return project;
    }

    public void setPerson(DeprecatedPerson person) {
	this.person = person;
    }

    public DeprecatedPerson getPerson() {
	return person;
    }

    public void setInstitute(Institute institute) {
	this.institute = institute;
    }

    public Institute getInstitute() {
	return institute;
    }

    public void setInvest(Investigation invest) {
	this.invest = invest;
    }

    public Investigation getInvest() {
	return invest;
    }

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}
}