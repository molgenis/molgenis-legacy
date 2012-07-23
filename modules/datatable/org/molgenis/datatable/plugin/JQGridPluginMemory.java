package org.molgenis.datatable.plugin;

import java.io.OutputStream;

import org.molgenis.datatable.model.MemoryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.test.MemoryTableFactory;
import org.molgenis.datatable.view.JQGridTableView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.pheno.Individual;
import org.molgenis.util.Tuple;

/** Simple plugin that only shows a data table for testing */
public class JQGridPluginMemory extends EasyPluginController<JQGridPluginMemory>
{
	JQGridTableView tableView;

	public JQGridPluginMemory(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		// need to (re) load the table
		try
		{
			//only this line changed ...
			TupleTable table = MemoryTableFactory.create(51,25);
			tableView = new JQGridTableView("test", this, table);
			tableView.setLabel("<b>Table:</b>Testing using the MemoryTupleTable");
		}
		catch (TableException e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}
	
	//handling of the ajax; should be auto-wired via the JQGridTableView contructor (TODO)
	public void download_json_test(Database db, Tuple request, OutputStream out)
	{
		//handle requests for the table named 'test'
		tableView.handleRequest(db, request, out);
	}

	// what is shown to the user
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}
}