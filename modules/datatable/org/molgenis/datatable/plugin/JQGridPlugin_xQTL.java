package org.molgenis.datatable.plugin;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.datatable.model.BinaryTupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.ScreenController;

/**
 * View data in a matrix.
 */
public class JQGridPlugin_xQTL extends JQGridPluginEntity
{
	private DataMatrixHandler dmh = null;

	public JQGridPlugin_xQTL(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
		FormModel<Data> parentForm = (FormModel<Data>) ((FormController) parentController).getModel();
		Data data = parentForm.getRecords().get(0);

		if (this.dmh == null)
		{
			dmh = new DataMatrixHandler(db);
		}

		try
		{
			DataMatrixInstance m = dmh.createInstance(data, db);
			BinaryTupleTable btt = new BinaryTupleTable(m.getAsFile());
			tableView = new JQGridView("test", this, btt);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

}