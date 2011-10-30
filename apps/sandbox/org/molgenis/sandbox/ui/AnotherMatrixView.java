package org.molgenis.sandbox.ui;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.CustomHtml;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.JQueryDataTableBeta;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.TableBeta;

public class AnotherMatrixView implements ScreenView
{
	AnotherMatrixModel model;

	public AnotherMatrixView(AnotherMatrixModel model)
	{
		this.model = model;
	}

	@Override
	public String render() throws HtmlInputException
	{
		// return "hello";

		MolgenisForm f = new MolgenisForm(this.model);

		
		TableBeta t = new JQueryDataTableBeta("test123");
		t.setClazz("molgenis_matrix");

		t.setHeader(0,0,"protocolapp1",1,2);
		
		t.setHeader(1,0,"feature1");
		
		t.setHeader(1,1,"feature2");
		
		t.set(0, 0, new CustomHtml("value0.0"));
		t.set(0, 1, new CustomHtml("value0.1"), 1, 1);
		t.set(1, 0, new CustomHtml("value1.0"), 1, 1);
		t.set(1, 1, "value1.1", 1, 1);

		f.add(t);

		return f.render();

	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
