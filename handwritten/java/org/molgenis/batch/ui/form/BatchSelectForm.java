package org.molgenis.batch.ui.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.SelectInput;

public class BatchSelectForm extends Container
{
	public BatchSelectForm()
	{
		SelectInput batches      = new SelectInput("batches");
		batches.setLabel("Your batches");
		batches.setNillable(false);
		
		ActionInput selectButton = new ActionInput("Select", "&nbsp;", "Select batch");
		
		ActionInput clearButton = new ActionInput("Clear", "&nbsp;", "Clear");

		DivPanel batchesPanel    = new DivPanel("batchPanel", "");
		batchesPanel.add(batches);
		batchesPanel.add(selectButton);
		batchesPanel.add(clearButton);
		
		this.add(batchesPanel);
	}
}
