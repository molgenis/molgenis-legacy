package org.molgenis.filemanager.ui;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.filemanager.FileLocation;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.EntityTable;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.util.Tuple;

public class LoadFromLocalPlugin extends EasyPluginController<LoadFromLocalPlugin>
{
	private List<FileLocation> files = new ArrayList<FileLocation>();

	public LoadFromLocalPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.setModel(this); // you can create a seperate class as 'model'.
	}

	public void list(Database db, Tuple request) throws Exception
	{
		files.clear();

		try
		{
			File path = new File(request.getString("path"));
			for (File file : path.listFiles())
			{
				if (file.isFile())
				{
					FileLocation f = new FileLocation();
					f.setFileServer_Name("local");
					f.setFilePath(file.getAbsolutePath());
					f.setTimeChecked(new Date(System.currentTimeMillis()));
					if (file.exists()) f.setFileStatus("exists");

					files.add(f);
				}
			}
		}
		catch (Exception e)
		{
			files.clear();
			throw e;
		}
	}

	public void load(Database db, Tuple request) throws DatabaseException
	{
		db.add(files);
		files.clear();
	}

	// what is shown to the user
	public ScreenView getView()
	{
		// uncomment next line if you want to use template file instead
		// return new FreemarkerView("LoadFromLocalPluginView.ftl", getModel());

		MolgenisForm view = new MolgenisForm(this);
		if (files.size() == 0)
		{
			view.add(new StringInput("path"));
			view.add(new ActionInput("list").setLabel("List contents"));
		}
		else
		{
			view.add(new ActionInput("load").setLabel("Load selected"));
			view.add(new EntityTable("currentFiles", files, false, "fileServer_name", "filePath", "fileStatus",
					"timeChecked"));
		}

		return view;
	}

	private String helloName = "UNKNOWN";

	// matches ActionInput("sayHello")
	public void sayHello(Database db, Tuple request)
	{
		if (!request.isNull("helloName"))
		{
			this.helloName = request.getString("helloName");
		}
	}

	@Override
	public void reload(Database db) throws Exception
	{
		// //example: update model with data from the database
		// Query q = db.query(Person.class);
		// q.like("name", "john");
		// getModel().investigations = q.find();
	}
}