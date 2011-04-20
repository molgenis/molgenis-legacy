package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;

public class ListAllMutationsForm extends Container
{

	private static final long serialVersionUID = 187266107832160314L;

	public ListAllMutationsForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", "listAllMutations"));
		this.add(new ActionInput("listAllMutations"));
		((ActionInput) this.get("listAllMutations")).setLabel("Display all mutations");
		((ActionInput) this.get("listAllMutations")).setTooltip("Display all mutations");
		((ActionInput) this.get("listAllMutations")).setButtonValue("Display all mutations");
	}
}
