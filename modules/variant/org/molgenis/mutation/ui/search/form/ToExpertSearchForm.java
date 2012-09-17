package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;

public class ToExpertSearchForm extends Container
{

	private static final long serialVersionUID = 7232792460068760022L;

	public ToExpertSearchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", "init"));
		this.add(new HiddenInput("expertSearch", "1"));
		this.add(new ActionInput("submit"));
		((ActionInput) this.get("submit")).setLabel("Advanced search");
		((ActionInput) this.get("submit")).setTooltip("Advanced search");
		
	}
}
