package org.molgenis.datatable.plugin;

import java.io.OutputStream;

import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.test.MemoryTableFactory;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/** Simple plugin that only shows a data table for testing */
public class JQGridPluginMemory extends EasyPluginController<JQGridPluginMemory>
{
	JQGridView tableView;

	public JQGridPluginMemory(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{

		// only this line changed ...
		final TupleTable table = MemoryTableFactory.create(51, 10);
		tableView = new JQGridView("test", this, table);
		tableView.setLabel("<b>Table:</b>Testing using the MemoryTupleTable");

	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json_test(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException
	{
		// handle requests for the table named 'test'
		tableView.handleRequest(db, request, out);
	}

	// what is shown to the user
	@Override
	public ScreenView getView()
	{
		final MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}
}