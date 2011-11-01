/*
 * Date: October 30, 2011 Template: EasyPluginModelGen.java.ftl generator:
 * org.molgenis.generators.ui.EasyPluginModelGen 4.0.0-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.compute.ui;

import java.util.List;

import org.molgenis.compute.ComputeParameter;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.framework.ui.EasyPluginModel;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;

public class RunWorkflowModel extends EasyPluginModel
{
	Workflow workflow;
	List<ComputeParameter> features;
	List<WorkflowElement> elements;
	List<ComputeProtocol> protocols;

	public RunWorkflowModel(RunWorkflow controller)
	{
		super(controller);
	}

	protected Workflow getWorkflow()
	{
		return workflow;
	}

	protected void setWorkflow(Workflow workflow)
	{
		this.workflow = workflow;
	}

	protected List<ComputeParameter> getFeatures()
	{
		return features;
	}

	protected void setFeatures(List<ComputeParameter> features)
	{
		this.features = features;
	}

	protected List<WorkflowElement> getElements()
	{
		return elements;
	}

	protected void setElements(List<WorkflowElement> elements)
	{
		this.elements = elements;
	}

	protected List<ComputeProtocol> getProtocols()
	{
		return protocols;
	}

	protected void setProtocols(List<ComputeProtocol> protocols)
	{
		this.protocols = protocols;
	}

}
