package org.molgenis.datatable.plugin;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.molgenis.datatable.model.PedMapTupleTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.InvestigationFile;

import decorators.MolgenisFileHandler;

/** Simple plugin that only shows a data table for testing */
public class JQGridPluginPedMap extends EasyPluginController<JQGridPluginPedMap>
{
	JQGridView tableView;
	boolean editable = false;

	public JQGridPluginPedMap(String name, ScreenController<?> parent)
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
			List<InvestigationFile> pedFiles = db.find(InvestigationFile.class, new QueryRule(
					InvestigationFile.EXTENSION, Operator.EQUALS, "ped"));

			if (pedFiles.size() != 1)
			{
				throw new Exception("Expecting 1 PED file");
			}

			List<InvestigationFile> mapFiles = db.find(InvestigationFile.class, new QueryRule(
					InvestigationFile.EXTENSION, Operator.EQUALS, "map"));

			if (mapFiles.size() != 1)
			{
				throw new Exception("Expecting 1 MAP file");
			}

			System.out.println("1 ped, 1 map file..");

			MolgenisFileHandler mfh = new MolgenisFileHandler(db);

			File pedFile = mfh.getFile(pedFiles.get(0), db);
			File mapFile = mfh.getFile(mapFiles.get(0), db);

			System.out.println("got ped/map files from db..");

			// create table
			TupleTable table = new PedMapTupleTable(pedFile, mapFile);

			System.out.println("PedMapTupleTable created..");

			table.setColLimit(10);

			System.out.println("columns limited to 10..");

			// check which table to show
			tableView = new JQGridView("pedmaptable", this, table);

			tableView.setLabel("<b>Table:</b>Testing using the MemoryTupleTable");

			System.out.println("tableView created..");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setError(e.getMessage());
		}
	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json_pedmaptable(Database db, Tuple request, OutputStream out)
			throws HandleRequestDelegationException
	{
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
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);

		view.add(tableView);

		return view;
	}
}