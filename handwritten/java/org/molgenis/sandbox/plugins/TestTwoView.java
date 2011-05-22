
package org.molgenis.sandbox.plugins;

import org.molgenis.framework.ui.EasyPluginView;
import org.molgenis.framework.ui.html.*;

public class TestTwoView extends EasyPluginView<TestTwoModel>
{

	public TestTwoView(TestTwoModel model)
	{
		super(model);
	}

	@Override
	public HtmlRenderer getInputs(TestTwoModel model)
	{
		MolgenisForm f = new MolgenisForm(model);
		
		//we use d.getValue() to render the dates in a nicer formatting.
		DateInput d = new DateInput("date",model.date);
		
		f.add(new TextParagraph("desc","currently selected date: "+d.getValue()));
		f.add(new LabelInput("label","Change date: "+d.getValue()));
		f.add(new DateInput("date", model.date));
		f.add(new ActionInput("updateDate","Update date"));
		
		return f;
	}
}