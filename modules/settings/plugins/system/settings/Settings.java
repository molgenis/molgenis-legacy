/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.system.settings;

import java.io.PrintWriter;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.FileSourceHelper;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.servlet.MolgenisServlet;

public class Settings<E extends Entity> extends PluginModel<E>
{

	private static final long serialVersionUID = 4037475429590054858L;
	private FileSourceHelper model;

	public FileSourceHelper getVO()
	{
		return model;
	}

	public Settings(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "Settings";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/system/settings/Settings.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			try
			{
				if (request.getString("__action").equals("setFileDirPath"))
				{
					db.getFileSourceHelper().setFilesource(request.getString("fileDirPath"));
				}
				else if (request.getString("__action").equals("deleteFileDirPath"))
				{
					db.getFileSourceHelper().deleteFilesource();
				}
				else if (request.getString("__action").equals("validate"))
				{
					db.getFileSourceHelper().validateFileSource();
				}
				this.setMessages();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	public void clearMessage()
	{
		this.setMessages();
	}

	@Override
	public void reload(Database db)
	{
		try
		{
			model = db.getFileSourceHelper();
			model.hasValidFileSource();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

}
