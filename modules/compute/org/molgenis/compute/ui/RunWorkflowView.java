package org.molgenis.compute.ui;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.*;
import org.molgenis.ngs.NgsSample;

public class RunWorkflowView implements ScreenView
{
	RunWorkflowModel model;

    private ActionInput buttonRun = new ActionInput("buttonRun", "Run");

    private int numberOfSamples = -1;

	public RunWorkflowView(RunWorkflowModel model)
	{
		this.model = model;

	}

	@Override
	public String render() throws HtmlInputException
	{
		MolgenisForm f = new MolgenisForm(model);

		
		//future: use matrix + shopping cart command
//		MrefInput targets = new MrefInput("targets", model.getWorkflow().getTargetFilter());
//		targets.setLabel("Select "+model.getWorkflow().getTargetFilter());
//		f.add(targets);
//
//		f.add(new ActionInput("runWorkflow"));
//
//		//display inputs for all ComputeFeature that are in template.
//		for(ComputeParameter feature: model.getFeatures())
//		{
//			f.add(MolgenisFieldTypes.createInput(feature.getDataType(), feature.getName(), null));
//		}

        Table sampleTable = new Table("sampleTable", "Samples");

        sampleTable.addColumn("#");
        sampleTable.addColumn("Sample Name");

        int i = 0;
        for (NgsSample sample : this.model.getSamples())
        {
            sampleTable.addRow("" + i);
            //check box is demcomplex!!!
            //sampleTable.setCell(0, i, new CheckboxInput("","","",null,null));
            sampleTable.setCell(1, i, sample.getName());
            i++;
        }
        f.add(sampleTable);
        f.add(buttonRun);

		return f.render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
