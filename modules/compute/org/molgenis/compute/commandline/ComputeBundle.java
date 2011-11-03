package org.molgenis.compute.commandline;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.compute.ComputeJob;
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
	List<ComputeJob> computeJobs = new ArrayList<ComputeJob>();
	
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
	public List<ComputeJob> getComputeJobs()
	{
		return computeJobs;
	}
	public void setComputeJobs(List<ComputeJob> computeJobs)
	{
		this.computeJobs = computeJobs;
	}
	
	public void prettyPrint()
	{
		System.out.println("ComputeParameter:");
		for(ComputeParameter f:this.getComputeParameters())
		{
			System.out.println(f);
		}
		System.out.println("WorkflowElement:");
		for(WorkflowElement f:this.getWorkflowElements())
		{
			System.out.println(f);
		}
		System.out.println("WorkflowElementParameter");
		for(WorkflowElementParameter f:this.getWorkflowElementParameters())
		{
			System.out.println(f);
		}
		System.out.println("WorkflowElementParameter");
		for(WorkflowElementParameter f:this.getWorkflowElementParameters())
		{
			System.out.println(f);
		}
		System.out.println("Protocol");
		for(ComputeProtocol f:this.getComputeProtocols())
		{
			System.out.println(f);
		}
	}

}
