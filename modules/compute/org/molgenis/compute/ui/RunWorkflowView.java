package org.molgenis.compute.ui;

import org.molgenis.MolgenisFieldTypes;
import org.molgenis.compute.ComputeParameter;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.MrefInput;

public class RunWorkflowView implements ScreenView
{
	RunWorkflowModel model;
	
	public RunWorkflowView(RunWorkflowModel model)
	{
		this.model = model;
	}

	@Override
	public String render() throws HtmlInputException
	{
		MolgenisForm f = new MolgenisForm(model);
		
		//future: use matrix + shopping cart command
		MrefInput targets = new MrefInput("targets", model.getWorkflow().getTargetFilter());
		targets.setLabel("Select "+model.getWorkflow().getTargetFilter());
		f.add(targets);
		
		f.add(new ActionInput("runWorkflow"));
		
		//display inputs for all ComputeFeature that are in template.
		for(ComputeParameter feature: model.getFeatures())
		{
			f.add(MolgenisFieldTypes.createInput(feature.getDataType(), feature.getName(), null));
		}
		
		return f.render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
