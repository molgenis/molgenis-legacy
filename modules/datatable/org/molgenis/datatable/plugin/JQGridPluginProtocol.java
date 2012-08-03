package org.molgenis.datatable.plugin;

import java.io.OutputStream;

import org.molgenis.datatable.model.EditableTableDecorator;
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
			EditableTableDecorator editableTable = new EditableTableDecorator(
					table);
			tableView = new JQGridView("test", this, editableTable);

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

	// what is shown to the user
	public ScreenView getView() {
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}
}