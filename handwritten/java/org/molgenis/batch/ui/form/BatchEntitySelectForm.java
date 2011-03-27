package org.molgenis.batch.ui.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;
import org.molgenis.framework.ui.html.Table;
import org.molgenis.framework.ui.html.TablePanel;

public class BatchEntitySelectForm extends Container
{
	public BatchEntitySelectForm()
	{
		Table entitiesDbTable;
		entitiesDbTable          = new Table("entitiesDbTable", "Entities in Database");
		entitiesDbTable.addColumn("Select");
		entitiesDbTable.addColumn("Entities");

		ActionInput addButton    = new ActionInput("Add", "&nbsp;", "Add selected");

		Table entitiesBatchTable;
		entitiesBatchTable       = new Table("entitiesBatchTable", "Entities in Batch");
		entitiesBatchTable.addColumn("Select");
		entitiesBatchTable.addColumn("Entities");
		
		ActionInput removeButton = new ActionInput("Remove", "&nbsp;", "Remove selected");
		
		ActionInput clearButton = new ActionInput("Clear", "&nbsp;", "Clear");

		DivPanel panel           = new DivPanel("panel", "");
		panel.add(entitiesDbTable);
		panel.add(addButton);
		panel.add(entitiesBatchTable);
		panel.add(removeButton);
		panel.add(clearButton);
		
		this.add(panel);
	}
}
