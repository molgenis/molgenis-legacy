package org.molgenis.compute.ui;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.FlowLayout;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.MrefInput;
import org.molgenis.framework.ui.html.Paragraph;
import org.molgenis.framework.ui.html.VerticalLayout;
import org.molgenis.framework.ui.html.XrefInput;
import org.molgenis.ngs.NgsSample;
import org.molgenis.protocol.Protocol;

public class ApplyProtocolStartView implements ScreenView
{
	ApplyProtocol controller;
	
	
	public ApplyProtocolStartView(ApplyProtocol controller)
	{
		this.controller = controller;
	}

	@Override
	public String render() throws HtmlInputException
	{
		MolgenisForm f = new MolgenisForm(this.controller);
		
		VerticalLayout table = new VerticalLayout();
		
		table.add(new Paragraph("<b>1. Choose protocol and samples:</b>"));
		
		XrefInput protocol = new XrefInput("Protocol", Protocol.class);
		protocol.setIncludeAddButton(false);
		table.add( protocol );
		
		MrefInput samples = new MrefInput("Samples", NgsSample.class);
		samples.setIncludeAddButton(false);
		table.add(samples);
		
		FlowLayout buttonLine = new FlowLayout();
		buttonLine.add(new ActionInput("createProtocolApplication", "Create new protocol application"));
		buttonLine.add(new ActionInput("findProtocolApplication", "Find existing protocol application"));
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
