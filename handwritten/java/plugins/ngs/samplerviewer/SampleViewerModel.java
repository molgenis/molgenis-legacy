/**
 * @author Jessica Lundberg
 * @date 20 Jan, 2011
 */
package plugins.ngs.samplerviewer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;
import org.molgenis.ngs.NgsSample;
import org.molgenis.ngs.Project;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;

import commonservice.CommonService;

public class SampleViewerModel extends SimpleScreenModel {

    public SampleViewerModel(ScreenController controller)
	{
		super(controller);
		// TODO Auto-generated constructor stub
	}

	private List<NgsSample> samples = new ArrayList<NgsSample>();
    private List<ObservableFeature> features = new ArrayList<ObservableFeature>();
    private List<Project> projects = new ArrayList<Project>();
    private List<Protocol> protocols = new ArrayList<Protocol>();

    private String action = "init";
    private String projectName = "";
    private int protocolId = 0;
    private String protocolName = "";
    private CommonService cq;
    
    List<ObservedValue> valuesBySample;
    
    
    public void setAction(String action) {
	this.action = action;
    }

    public String getAction() {
	return action;
    }

    public void setProjects(List<Project> projects) {
	this.projects = projects;
    }

    public List<Project> getProjects() {
	return projects;
    }

    public void setProtocols(List<Protocol> protocols) {
	this.protocols = protocols;
    }

    public List<Protocol> getProtocols() {
	return protocols;
    }

    public void setProtocolName(String protocolName) {
	this.protocolName = protocolName;
    }

    public String getProtocolName() {
	return protocolName;
    }

    public void setProjectName(String projectName) {
	this.projectName = projectName;
    }

    public String getProjectName() {
	return projectName;
    }

    public void setSamples(List<NgsSample> samples) {
	this.samples = samples;
    }

    public List<NgsSample> getSamples() {
	return samples;
    }

    public void setFeatures(List<ObservableFeature> features) {
	this.features = features;
    }

    public List<ObservableFeature> getFeatures() {
	return features;
    }

    public void setProtocolId(int protocolId) {
	this.protocolId = protocolId;
    }

    public int getProtocolId() {
	return protocolId;
    }

    public void setCommonQueries(CommonService cq) {
	this.cq = cq;
    }

    public CommonService getCommonQueries() {
	return cq;
    }
    
    public List<ObservedValue> getValuesBySample() throws DatabaseException, ParseException {
	return valuesBySample;
    }
    
    public List<ObservedValue> getValuesBySample(int sampleId, List<ObservableFeature> features) throws DatabaseException, ParseException {
	valuesBySample = cq.getObservedValueBySampleAndFeatures(sampleId, features);
	return valuesBySample;
    }

	@Override
	public boolean isVisible()
	{
		// TODO Auto-generated method stub
		return false;
	}


}
