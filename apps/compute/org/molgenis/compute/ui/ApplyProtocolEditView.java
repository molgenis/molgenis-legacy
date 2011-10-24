package org.molgenis.compute.ui;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.EntityForm;
import org.molgenis.framework.ui.html.FlowLayout;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.VerticalLayout;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.protocol.ui.ProtocolApplicationForm;

/**
 * This view shows first the main fields of protocolApplication and then a matrix with the selection of values (rows = samples, cols = measurements).
 * User can choose to 'save' (data will be saved), 'restart' (go to first screen) or 'cancel' (also go to first screen).
 */
public class ApplyProtocolEditView implements ScreenView
{
	ApplyProtocol controller;
	
	public ApplyProtocolEditView(ApplyProtocol controller)
	{
		this.controller = controller;
	}

	@Override
	public String render() throws HtmlInputException
	{
		MolgenisForm f = new MolgenisForm(this.controller);
		
		VerticalLayout table = new VerticalLayout();
		
		table.add(new Paragraph("<b>Edit protocol application:</b>"));
		
		//add inputs of protocolApplication
		EntityForm<ProtocolApplication> paForm = new ProtocolApplicationForm(this.controller.application);
		for(HtmlInput input: paForm.getInputs(ProtocolApplication.ID, ProtocolApplication.NAME, ProtocolApplication.DESCRIPTION, ProtocolApplication.PROTOCOL, ProtocolApplication.TIME, ProtocolApplication.PERFORMER))
		{
			if(input.getName().equals(ProtocolApplication.ID))
			{
				input.setHidden(true);
				table.add(input);
			}
			else
			{
				table.add(input);
			}
		}
		
		//add value matrix
		EditableValueMatrixView matrix = new EditableValueMatrixView("valueMatrix", this.controller.measurements, this.controller.samples, this.controller.values);
		matrix.setLabel("Protocol parameter values");
		table.add(matrix);
		
		// add the buttons in a horizontal flow
		FlowLayout buttonLine = new FlowLayout();
		buttonLine.add(new ActionInput("saveProtocolApplication", "Save"));
		buttonLine.add(new ActionInput("restart", "Restart"));
		buttonLine.add(new ActionInput("cancel", "Cancel"));
		table.add(buttonLine);
		
		f.add(table);
		
		return f.render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
