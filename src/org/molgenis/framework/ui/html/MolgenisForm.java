package org.molgenis.framework.ui.html;

import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenModel;

public class MolgenisForm extends Container implements HtmlRenderer
{
	private static final long serialVersionUID = 1L;
	private ScreenModel model = null;
	
	public MolgenisForm(ScreenModel model)
	{
		this.model = model;
	}
	
	@Override
	public String render()
	{	
		//use freemarker macros to render form header and footer
		FreemarkerView view = new FreemarkerView(MolgenisForm.class.getPackage().getName().replace(".", "/")+"/MolgenisForm.ftl", getModel(), false);
		view.addParameter("inputs", this.getInputs());
		String result = view.render();
		return result;
	}

	public ScreenModel getModel()
	{
		return model;
	}

	public void setModel(ScreenModel model)
	{
		this.model = model;
	}
}
