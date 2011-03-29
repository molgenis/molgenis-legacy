package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;

public class ToSimpleSearchForm extends Container
{

	private static final long serialVersionUID = -5150327029807239526L;

	public ToSimpleSearchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", "init"));
		this.add(new HiddenInput("expertSearch", "0"));
		this.add(new ActionInput("submit"));
		((ActionInput) this.get("submit")).setLabel("Back to simple search");
		((ActionInput) this.get("submit")).setTooltip("Back to simple search");
	}
}
