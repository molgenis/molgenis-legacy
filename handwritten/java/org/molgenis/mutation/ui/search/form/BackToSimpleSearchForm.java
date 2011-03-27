package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;

public class BackToSimpleSearchForm extends Container
{
	public BackToSimpleSearchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", "init"));
		this.add(new ActionInput("expertSearch"));
		((ActionInput) this.get("expertSearch")).setValue(0);
		((ActionInput) this.get("expertSearch")).setLabel("Back to Simple Search");
		((ActionInput) this.get("expertSearch")).setTooltip("Back to Simple Search");
	}
}
