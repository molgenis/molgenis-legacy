package org.molgenis.compute;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.WorkflowElementParameter;
import org.molgenis.util.Tuple;


public class ComputeBundle
{
	List<ComputeParameter> computeParameters = new ArrayList<ComputeParameter>();
	List<ComputeProtocol> computeProtocols = new ArrayList<ComputeProtocol>();
	List<WorkflowElement> workflowElements = new ArrayList<WorkflowElement>();
	List<WorkflowElementParameter> workflowElementParameters = new ArrayList<WorkflowElementParameter>();
	List<Tuple> userParameters = new ArrayList<Tuple>();
	List<ComputeApplication> computeJobs = new ArrayList<ComputeApplication>();
	
	public List<ComputeParameter> getComputeParameters()
	{
		return computeParameters;
	}
	public void setComputeParameters(List<ComputeParameter> computeParameters)
	{
		this.computeParameters = computeParameters;
	}
	public List<ComputeProtocol> getComputeProtocols()
	{
		return computeProtocols;
	}
	public void setComputeProtocols(List<ComputeProtocol> computeProtocols)
	{
		this.computeProtocols = computeProtocols;
	}
	public List<WorkflowElement> getWorkflowElements()
	{
		return workflowElements;
	}
	public void setWorkflowElements(List<WorkflowElement> workflowElements)
	{
		this.workflowElements = workflowElements;
	}
	public List<WorkflowElementParameter> getWorkflowElementParameters()
	{
		return workflowElementParameters;
	}
	public void setWorkflowElementParameters(
			List<WorkflowElementParameter> workflowElementParameters)
	{
		this.workflowElementParameters = workflowElementParameters;
	}
	public List<Tuple> getUserParameters()
	{
		return userParameters;
	}
	public void setUserParameters(List<Tuple> userParameters)
	{
		this.userParameters = userParameters;
	}
	public List<ComputeApplication> getComputeJobs()
	{
		return computeJobs;
	}
	public void setComputeJobs(List<ComputeApplication> computeJobs)
	{
		this.computeJobs = computeJobs;
	}

}
