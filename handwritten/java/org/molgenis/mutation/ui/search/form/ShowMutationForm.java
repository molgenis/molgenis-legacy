package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.SelectInput;

public class ShowMutationForm extends Container
{
	public ShowMutationForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", "showMutation"));
		this.add(new HiddenInput("expertSearch", "1"));
		this.add(new SelectInput("mid", ""));
		((SelectInput) this.get("mid")).setClazz("adv_search");
		((SelectInput) this.get("mid")).setOnchange("submit()");
	}
}
