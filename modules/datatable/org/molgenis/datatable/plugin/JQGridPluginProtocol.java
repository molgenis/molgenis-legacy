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
public class JQGridPluginProtocol extends
		EasyPluginController<JQGridPluginProtocol> {
	JQGridView tableView;
	boolean editable = false;

	public JQGridPluginProtocol(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	@Override
	public void reload(Database db) {
		// need to (re) load the table
		try {
			// only this line changed ...
			Protocol p = db.query(Protocol.class)
					.eq(Protocol.NAME, "stageCatalogue").find().get(0);

			// create table
			ProtocolTable table = new ProtocolTable(db, p);
			table.setTargetString("Pa_Id");
			// add editable decorator

			// check which table to show
			tableView = new JQGridView("test", this, table);

			tableView
					.setLabel("<b>Table:</b>Testing using the MemoryTupleTable");
		} catch (Exception e) {
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json_test(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException {
		// handle requests for the table named 'test'

		tableView.handleRequest(db, request, out);

	}

	//
	// public void handleRequest(Database db, Tuple request) {
	// if (request.getAction().equals("edit")) {
	// System.out.println("hello world");
	// }
	// }

	// what is shown to the user
	public ScreenView getView() {
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}
}