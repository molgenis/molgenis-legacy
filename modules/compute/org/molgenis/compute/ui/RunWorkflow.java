package org.molgenis.compute.ui;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.compute.ComputeFeature;
import org.molgenis.compute.ComputeProtocol;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.WorkflowElementParameter;

/**
 * User story. First the user is asked to select 'targets' of analysis.
 * Subsequently parameters have be filled in that should be applied and compute
 * cluster. If generation of jobs is succesful user can link to monitor screen.
 */
public class RunWorkflow extends EasyPluginController<RunWorkflowModel>
		implements ScreenView
{
	public RunWorkflow(String name, ScreenController<?> parent)
	{
		super(name, null, parent);
		this.setModel(new RunWorkflowModel(this)); // the default model
		this.setView(new RunWorkflowView(this.getModel())); // <plugin													// flavor="freemarker"
	}

	@Override
	public void reload(Database db) throws Exception
	{
		RunWorkflowModel model = getModel();
		
		// check if workflow has changed, if so, update and reset view
		Workflow w = this.getParentForm(Workflow.class).getCurrent();
		if (!w.equals(getModel().workflow))
		{
			//reset view
			this.setView(new RunWorkflowView(this.getModel()));
		
			//reload all info:

			// get the workflow elements
			List<WorkflowElement> elements = db.query(WorkflowElement.class)
					.eq(WorkflowElement.WORKFLOW, w.getId()).find();

			// get the protocols
			List<Integer> protocolIds = new ArrayList<Integer>();
			List<Integer> workflowElementIds = new ArrayList<Integer>();
			for (WorkflowElement e : elements)
			{
				protocolIds.add(e.getProtocol_Id());
				workflowElementIds.add(e.getId());
			}
			List<ComputeProtocol> protocols = db.query(ComputeProtocol.class)
					.in(Protocol.ID, protocolIds).find();

			// get features where isUser=true and check if they are in this workflow by looking in templates
			List<ComputeFeature> allFeatures = db.query(ComputeFeature.class).eq(ComputeFeature.ISUSER, true).find();
			List<ComputeFeature> usedFeatures = new ArrayList<ComputeFeature>();
			for (ComputeFeature f : allFeatures)
			{
				for(ComputeProtocol p: protocols)
				{
					//you will miss parameters that you need for derived 'parameters'
					if(p.getScriptTemplate().contains("${"+f.getName()+"}"))
					{
						usedFeatures.add(f);
						break;
					}
				}
			}
			
			//todo: get workflow element parameters and remove those features that will be weaved
			//real weaving happens during generation...
			//List<WorkflowElementParameter> params = db.query(WorkflowElementParameter.class).in(WorkflowElementParameter.WORKFLOWELEMENT, workflowElementIds).find();
			
			//TODO: check for missing features
			
			//update the model
			model.setWorkflow(w);
			model.setElements(elements);
			model.setProtocols(protocols);
			model.setFeatures(usedFeatures);
		}

	}
}