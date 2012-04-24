package org.molgenis.pheno.ui.form;

import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DateInput;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.TextLineInput;

public class ApplyProtocolForm extends Container
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 4998773712604616798L;

	public ApplyProtocolForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", ""));
		this.add(new TextLineInput<String>("paName", ""));
		this.add(new DateInput("paTime", ""));
	}
}
