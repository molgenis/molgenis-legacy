package plugins.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.cluster.Analysis;
import org.molgenis.cluster.DataName;
import org.molgenis.cluster.DataValue;
import org.molgenis.cluster.Job;
import org.molgenis.cluster.ParameterName;
import org.molgenis.cluster.ParameterValue;
import org.molgenis.cluster.Subjob;
import org.molgenis.data.Data;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.SimpleScreenModel;

import plugins.cluster.helper.LoginSettings;

/**
 * Model for ClusterPlugin
 * @author joerivandervelde
 *
 */
public class ClusterPluginModel
{
	
	int maxSubjobs;
	int nrOfJobs;
	
	String state;
	String selectedComputeResource;
	Analysis selectedAnalysis;
	
	HashMap<String, String> jobParamMap = new HashMap<String, String>();
	HashMap<String, String> jobToOutputLink = new HashMap<String, String>();
	
	Job candidateJob;
	LoginSettings ls;
	
	List<Data> matrices = new ArrayList<Data>();
	List<Job> jobs = new ArrayList<Job>();
	List<Subjob> subjobs = new ArrayList<Subjob>();
	List<Analysis> analysis = new ArrayList<Analysis>();
	List<ParameterName> parameternames = new ArrayList<ParameterName>();
	List<ParameterValue> parametervalues = new ArrayList<ParameterValue>();
	List<DataName> datanames = new ArrayList<DataName>();
	List<DataValue> datavalues = new ArrayList<DataValue>();
	
	String refreshRate = "15";
	
	

	public HashMap<String, String> getJobToOutputLink()
	{
		return jobToOutputLink;
	}
	public void setJobToOutputLink(HashMap<String, String> jobToOutputLink)
	{
		this.jobToOutputLink = jobToOutputLink;
	}
	public Analysis getSelectedAnalysis()
	{
		return selectedAnalysis;
	}
	public void setSelectedAnalysis(Analysis selectedAnalysis)
	{
		this.selectedAnalysis = selectedAnalysis;
	}
	public String getRefreshRate()
	{
		return refreshRate;
	}
	public void setRefreshRate(String refreshRate)
	{
		this.refreshRate = refreshRate;
	}
	public String getSelectedComputeResource()
	{
		return selectedComputeResource;
	}
	public void setSelectedComputeResource(String selectedComputeResource)
	{
		this.selectedComputeResource = selectedComputeResource;
	}
	public int getMaxSubjobs()
	{
		return maxSubjobs;
	}
	public void setMaxSubjobs(int maxSubjobs)
	{
		this.maxSubjobs = maxSubjobs;
	}
	public int getNrOfJobs()
	{
		return nrOfJobs;
	}
	public void setNrOfJobs(int nrOfJobs)
	{
		this.nrOfJobs = nrOfJobs;
	}
	public String getState()
	{
		return state;
	}
	public void setState(String state)
	{
		this.state = state;
	}
	public HashMap<String, String> getJobParamMap()
	{
		return jobParamMap;
	}
	public void setJobParamMap(HashMap<String, String> jobParamMap)
	{
		this.jobParamMap = jobParamMap;
	}
	public Job getCandidateJob()
	{
		return candidateJob;
	}
	public void setCandidateJob(Job candidateJob)
	{
		this.candidateJob = candidateJob;
	}
	public LoginSettings getLs()
	{
		return ls;
	}
	public void setLs(LoginSettings ls)
	{
		this.ls = ls;
	}
	public List<Data> getMatrices()
	{
		return matrices;
	}
	public void setMatrices(List<Data> matrices)
	{
		this.matrices = matrices;
	}
	public List<Job> getJobs()
	{
		return jobs;
	}
	public void setJobs(List<Job> jobs)
	{
		this.jobs = jobs;
	}
	public List<Subjob> getSubjobs()
	{
		return subjobs;
	}
	public void setSubjobs(List<Subjob> subjobs)
	{
		this.subjobs = subjobs;
	}
	public List<Analysis> getAnalysis()
	{
		return analysis;
	}
	public void setAnalysis(List<Analysis> analysis)
	{
		this.analysis = analysis;
	}
	public List<ParameterName> getParameternames()
	{
		return parameternames;
	}
	public void setParameternames(List<ParameterName> parameternames)
	{
		this.parameternames = parameternames;
	}
	public List<ParameterValue> getParametervalues()
	{
		return parametervalues;
	}
	public void setParametervalues(List<ParameterValue> parametervalues)
	{
		this.parametervalues = parametervalues;
	}
	public List<DataName> getDatanames()
	{
		return datanames;
	}
	public void setDatanames(List<DataName> datanames)
	{
		this.datanames = datanames;
	}
	public List<DataValue> getDatavalues()
	{
		return datavalues;
	}
	public void setDatavalues(List<DataValue> datavalues)
	{
		this.datavalues = datavalues;
	}

}
