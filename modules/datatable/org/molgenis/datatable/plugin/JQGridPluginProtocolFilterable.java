package org.molgenis.datatable.plugin;

import java.io.OutputStream;

import org.molgenis.datatable.model.ProtocolTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

/** Simple plugin that only shows a data table for testing */
public class JQGridPluginProtocolFilterable extends EasyPluginController<JQGridPluginProtocolFilterable>
{
	JQGridView tableView;

	public JQGridPluginProtocolFilterable(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		// need to (re) load the table
		try
		{
			// only this line changed ...
			final Protocol p = db.query(Protocol.class).eq(Protocol.NAME, "TestProtocol").find().get(0);
			tableView = new JQGridView("test", this, new ProtocolTable(db, p));
			tableView.setLabel("<b>Table:</b>Testing using the FilterableProtocolTable");
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json_test(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
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