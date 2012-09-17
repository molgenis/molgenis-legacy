/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.archiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.CsvToDatabase.ImportResult;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.TarGz;
import org.molgenis.util.Tuple;

import app.CsvExport;
import app.CsvImport;

public class Archiver extends PluginModel<Entity>
{

	private static final long serialVersionUID = -6011550023936663086L;
	private ArchiverModel model = new ArchiverModel();

	public ArchiverModel getMyModel()
	{
		return model;
	}

	public Archiver(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "Archiver";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/archiver/Archiver.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			System.out.println("*** handleRequest __action: " + request.getString("__action"));

			try
			{
				String action = request.getString("__action");
				if (action.equals("export"))
				{

					File tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "dbexport_"
							+ System.nanoTime());
					tmpDir.mkdir();

					if (!tmpDir.exists())
					{
						throw new Exception("Could not create tmp folder: " + tmpDir.getAbsolutePath());
					}

					List<Class<? extends Entity>> specialCases = new ArrayList<Class<? extends Entity>>();
					specialCases.add(org.molgenis.auth.MolgenisGroup.class);
					specialCases.add(org.molgenis.auth.MolgenisPermission.class);
					specialCases.add(org.molgenis.auth.MolgenisRoleGroupLink.class);
					specialCases.add(org.molgenis.auth.MolgenisUser.class);
					specialCases.add(org.molgenis.core.MolgenisEntity.class);
					new CsvExport().exportSpecial(tmpDir, db, specialCases, true);
					File tarFile = TarGz.tarDir(tmpDir);
					this.model.setDownload(tarFile.getName());
					this.setMessages(new ScreenMessage("Export successful!", true));
				}
				else if (action.equals("import"))
				{
					File importFile = request.getFile("importFile");
					File extractDir = TarGz.tarExtract(importFile);
					ImportResult i = CsvImport.importAll(extractDir, db, new SimpleTuple(), true);
					if (i.getErrorItem().equals("no error found"))
					{
						this.setMessages(new ScreenMessage("Import successful!", true));
					}
					else
					{
						String msg = i.getMessages().get(i.getErrorItem());
						this.setMessages(new ScreenMessage(
								"Import failed on entity '" + i.getErrorItem() + "': " + msg, false));
					}

				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	@Override
	public void reload(Database db)
	{

		try
		{

		}
		catch (Exception e)
		{

		}

	}

}
