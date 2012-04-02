package org.molgenis.mutation.ui.upload.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.FileInput;
import org.molgenis.framework.ui.html.HiddenInput;

public class BatchForm extends Container
{

	private static final long serialVersionUID = -3532250380061297220L;

	public BatchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", "insertBatch"));
		this.add(new FileInput("upload"));
		this.add(new ActionInput("insertBatch"));
		((ActionInput) this.get("insertBatch")).setLabel("Upload");
		((ActionInput) this.get("insertBatch")).setTooltip("Upload");
		((ActionInput) this.get("insertBatch")).setButtonValue("Upload");
	}
}
