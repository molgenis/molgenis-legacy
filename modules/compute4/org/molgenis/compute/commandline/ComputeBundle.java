package org.molgenis.compute.commandline;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.compute.design.ComputeParameter;
import org.molgenis.compute.design.ComputeProtocol;
import org.molgenis.compute.design.WorkflowElement;
import org.molgenis.compute.runtime.ComputeTask;
import org.molgenis.util.Tuple;

public class ComputeBundle {
	// all parameters needed to describe a workflow
	private Map<String, ComputeParameter> computeParameters = new LinkedHashMap<String, ComputeParameter>();
	private List<ComputeProtocol> computeProtocols = new ArrayList<ComputeProtocol>();
	private List<WorkflowElement> workflowElements = new ArrayList<WorkflowElement>();

	// parameters specific to this analysis run
	private List<Tuple> userParameters = new ArrayList<Tuple>();

	// worksheet for this analysis
	// This is a combination of userParameters (from worksheet.txt)
	// and computeParameters (parameters.txt)
	private List<Tuple> worksheet;

	// generated result of jobs
	private List<ComputeTask> computeTasks = new ArrayList<ComputeTask>();

	public List<ComputeParameter> getComputeParameters() {
		return new ArrayList<ComputeParameter>(computeParameters.values());
	}

	public void setComputeParameters(List<ComputeParameter> computeParameters) {
		this.computeParameters.clear();
		for (ComputeParameter cp : computeParameters) {
			this.computeParameters.put(cp.getName(), cp);
		}
	}

	public ComputeParameter getComputeParameter(String name) {
		return this.computeParameters.get(name);
	}

	public List<ComputeProtocol> getComputeProtocols() {
		return computeProtocols;
	}

	public void addComputeParameter(ComputeParameter cp) {
		this.computeParameters.put(cp.getName(), cp);
	}

	public void setComputeProtocols(List<ComputeProtocol> computeProtocols) {
		this.computeProtocols = computeProtocols;
	}

	public List<WorkflowElement> getWorkflowElements() {
		return workflowElements;
	}

	public void setWorkflowElements(List<WorkflowElement> workflowElements) {
		this.workflowElements = workflowElements;
	}

	public List<Tuple> getUserParameters() {
		return userParameters;
	}

	public void setUserParameters(List<Tuple> userParameters) {
		this.userParameters = userParameters;
	}

	public List<ComputeTask> getComputeTasks() {
		return computeTasks;
	}

	public void setComputeTasks(List<ComputeTask> ComputeTasks) {
		this.computeTasks = ComputeTasks;
	}

	public void setWorksheet(List<Tuple> worksheet) {
		this.worksheet = worksheet;
	}

	public List<Tuple> getWorksheet() {
		return worksheet;
	}

	public void prettyPrint() {
		System.out.println("ComputeParameter:");
		for (ComputeParameter f : this.getComputeParameters()) {
			System.out.println(f);
		}
		System.out.println("UserParameter:");
		for (Tuple f : this.getUserParameters()) {
			System.out.println(f);
		}
		System.out.println("WorkflowElement:");
		for (WorkflowElement f : this.getWorkflowElements()) {
			System.out.println(f);
		}

		System.out.println("Protocol");
		for (ComputeProtocol f : this.getComputeProtocols()) {
			System.out.println(f);
		}
	}

}
