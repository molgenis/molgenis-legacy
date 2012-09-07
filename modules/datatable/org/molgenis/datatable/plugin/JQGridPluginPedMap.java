package org.molgenis.datatable.plugin;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.molgenis.datatable.model.PedMapTupleTable;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.InvestigationFile;

import decorators.MolgenisFileHandler;

/** Simple plugin that only shows a data table for testing */
public class JQGridPluginPedMap extends EasyPluginController<JQGridPluginPedMap>
{
	private static final long serialVersionUID = 1049195164197133420L;
	private JQGridView tableView;
	private List<InvestigationFile> pedFiles;
	private List<InvestigationFile> mapFiles;
	private InvestigationFile selectedPedFile;
	private InvestigationFile selectedMapFile;

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
			pedFiles = getInvestigationFiles(db, "ped");
			mapFiles = getInvestigationFiles(db, "map");

			if (pedFiles.isEmpty())
			{
				throw new Exception("No ped files");
			}

			if (mapFiles.isEmpty())
			{
				throw new Exception("No map files");
			}

			if (selectedMapFile == null)
			{
				selectedMapFile = mapFiles.get(0);
			}

			if (selectedPedFile == null)
			{
				selectedPedFile = pedFiles.get(0);
			}

			MolgenisFileHandler mfh = new MolgenisFileHandler(db);

			File pedFile = mfh.getFile(selectedPedFile, db);
			File mapFile = mfh.getFile(selectedMapFile, db);

			System.out.println("got ped/map files from db..");

			// create table
			TupleTable table = new PedMapTupleTable(pedFile, mapFile);

			System.out.println("PedMapTupleTable created..");

			if (table.getColumns().size() > 10)
			{
				table.setColLimit(10);
				System.out.println("columns limited to 10..");
			}

			// check which table to show
			tableView = new JQGridView("pedmaptable", this, table, false);

			tableView.setLabel("Genotypes");

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
		// handle requests for the table named 'genotypes'
		tableView.handleRequest(db, request, out);

	}

	public void reloadTable(Database db, Tuple request)
	{
		String pedFileName = request.getString("pedfile");
		String mapFileName = request.getString("mapfile");

		for (InvestigationFile file : pedFiles)
		{
			if (file.getName().equals(pedFileName))
			{
				selectedPedFile = file;
			}
		}

		for (InvestigationFile file : mapFiles)
		{
			if (file.getName().equals(mapFileName))
			{
				selectedMapFile = file;
			}
		}

	}

	// what is shown to the user
	public ScreenView getView()
	{
		MolgenisForm view = new MolgenisForm(this);

		SelectInput selectPed = new SelectInput("pedfile");
		for (InvestigationFile file : pedFiles)
		{
			selectPed.addOption(file.getName(), file.getName());
		}
		selectPed.setValue(selectedPedFile.getName());
		view.add(selectPed);

		SelectInput selectMap = new SelectInput("mapfile");
		for (InvestigationFile file : mapFiles)
		{
			selectMap.addOption(file.getName(), file.getName());
		}
		selectMap.setValue(selectedMapFile.getName());
		view.add(selectMap);

		view.add(new ActionInput("reloadTable", "Reload table"));

		view.add(tableView);

		return view;
	}

	private List<InvestigationFile> getInvestigationFiles(Database db, String extension) throws DatabaseException
	{
		return db.find(InvestigationFile.class, new QueryRule(InvestigationFile.EXTENSION, Operator.EQUALS, extension));
	}
}