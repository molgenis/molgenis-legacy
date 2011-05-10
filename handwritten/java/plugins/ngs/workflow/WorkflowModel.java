package plugins.ngs.workflow;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;
import org.molgenis.ngs.NgsSample;
import org.molgenis.ngs.Project;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.WorkflowElement;

import commonservice.CommonService;

public class WorkflowModel extends SimpleScreenModel {

	private static final long serialVersionUID = -1792154119915644699L;

	public WorkflowModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private List<NgsSample> samples = new ArrayList<NgsSample>();
    private List<Measurement> features = new ArrayList<Measurement>();
    private List<Project> projects = new ArrayList<Project>();
    private List<Protocol> protocols = new ArrayList<Protocol>();
    private List<ObservedValue> valuesBySample = new ArrayList<ObservedValue>();
    private  Set<WorkflowElement> workflowElements = new LinkedHashSet<WorkflowElement>();
    private Protocol currentProtocol;
    private CommonService cq;
    private NgsSample sample = new NgsSample();

    private boolean projectSpecific = false;
    private String projectName = "";
    private String action = "init";
    
    public void setCommonQueries(CommonService cq) {
	this.cq = cq;
    }
    
    public CommonService getCommonQueries() {
	return cq;
    }
    
    public void setWorkflowElements(Set<WorkflowElement> set) {
	this.workflowElements = set;
    }
    
    public Set<WorkflowElement> getWorkflowElements() {
	return workflowElements;
    }

    
    public List<ObservedValue> getValuesBySample() throws DatabaseException, ParseException {
    	return valuesBySample;
    }
    
    public void setValuesBySample(int sampleId, List<Measurement> features) throws DatabaseException, ParseException {
    	valuesBySample = cq.getObservedValueBySampleAndFeatures(sampleId, features);
    }

    public void setAction(String action) {
	this.action = action;
    }

    public String getAction() {
	return action;
    }

    public void setSamples(List<NgsSample> allSamples) {
	this.samples = allSamples;
    }

    public List<NgsSample> getSamples() {
	return samples;
    }

    public void setProjects(List<Project> projects) {
	this.projects = projects;
    }

    public List<Project> getProjects() {
	return projects;
    }

    public void setSample(NgsSample sample) {
	this.sample = sample;
    }

    public NgsSample getSample() {
	return sample;
    }

    public void setFeatures(List<Measurement> features) {
    	this.features = features;
    }

    public List<Measurement> getFeatures() {
    	return features;
    }

    public void setProtocols(List<Protocol> protocols) {
	this.protocols = protocols;
    }

    public List<Protocol> getProtocols() {
	return protocols;
    }

    public void setCurrentProtocol(Protocol currentProtocol) {
	this.currentProtocol = currentProtocol;
    }

    public Protocol getCurrentProtocol() {
	return currentProtocol;
    }

    public void setProjectSpecific(boolean projectSpecific) {
	this.projectSpecific = projectSpecific;
    }

    public boolean isProjectSpecific() {
	return projectSpecific;
    }

    public void setProjectName(String projectName) {
	this.projectName = projectName;
    }

    public String getProjectName() {
	return projectName;
    }

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}


}


