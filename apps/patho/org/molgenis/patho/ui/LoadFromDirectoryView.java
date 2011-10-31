package org.molgenis.patho.ui;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.StringInput;

public class LoadFromDirectoryView implements ScreenView
{
	LoadFromDirectoryModel model;

	public LoadFromDirectoryView(LoadFromDirectoryModel model)
	{
		this.model = model;
	}

	@Override
	public String render() throws HtmlInputException
	{
		MolgenisForm f = new MolgenisForm(model);
		f.add(new StringInput("directory"));
		f.add(new ActionInput("loadDirectory"));
		return f.render();
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
