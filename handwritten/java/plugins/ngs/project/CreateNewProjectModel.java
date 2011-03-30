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

import org.molgenis.ngs.NgsPerson;
import org.molgenis.ngs.Project;
import org.molgenis.organization.Institute;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.Workflow;
import org.testng.collections.Lists;

public class CreateNewProjectModel {
    private Project project;
    private NgsPerson person;
    private Institute institute;
    private List<NgsPerson> labworkers = Lists.newArrayList();
    private String action = "init";
    private List<Investigation> investigation = new ArrayList<Investigation>();
    private Investigation invest;
    private String projectName = "";
    private List<Workflow> workflows = new ArrayList<Workflow>();



    public void setLabworkers(List<NgsPerson> labworkers)
    {
	this.labworkers = labworkers;
    }

    public List<NgsPerson> getLabworkers()
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

    public void setProject(Project project) {
	this.project = project;
    }

    public Project getProject() {
	return project;
    }

    public void setPerson(NgsPerson person) {
	this.person = person;
    }

    public NgsPerson getPerson() {
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
}