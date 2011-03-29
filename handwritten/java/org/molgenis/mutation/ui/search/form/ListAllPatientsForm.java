package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;

public class ListAllPatientsForm extends Container
{
	private static final long serialVersionUID = 5684517266605366403L;

	public ListAllPatientsForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", "listAllPatients"));
		this.add(new ActionInput("listAllPatients"));
		((ActionInput) this.get("listAllPatients")).setLabel("Display all patients");
		((ActionInput) this.get("listAllPatients")).setTooltip("Display all patients");
		((ActionInput) this.get("listAllPatients")).setButtonValue("Display all patients");
	}
}
