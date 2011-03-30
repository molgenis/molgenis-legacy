/* Date:        October 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.molgenisfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Entity;
import org.molgenis.util.HtmlTools;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;
import app.servlet.MolgenisServlet;
import filehandling.generic.MolgenisFileHandler;
import filehandling.generic.PerformUpload;

public class MolgenisFileManager extends PluginModel<Entity>
{

	private static final long serialVersionUID = 7832540415673199206L;

	public MolgenisFileManager(String name, ScreenModel<Entity> parent)
	{
		super(name, parent);
	}

	private MolgenisFileManagerModel model = new MolgenisFileManagerModel();

	public MolgenisFileManagerModel getModel()
	{
		return model;
	}

	private MolgenisFileHandler mfh = null;

	@Override
	public String getViewName()
	{
		return "plugins_molgenisfile_MolgenisFileManager";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/molgenisfile/MolgenisFileManager.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			if (request.getString("__action") != null)
			{
				String action = request.getString("__action");

				File file = null;

				if (request.getString("__action").equals("uploadTextArea"))
				{
					String content = request.getString("inputTextArea");
					File inputTextAreaContent = new File(System.getProperty("java.io.tmpdir") + File.separator
							+ "tmpTextAreaInput" + System.nanoTime() + ".txt");
					BufferedWriter out = new BufferedWriter(new FileWriter(inputTextAreaContent));
					out.write(content);
					out.close();
					file = inputTextAreaContent;
				}
				else if (action.equals("upload"))
				{
					file = request.getFile("upload");
				}
				
				if (file == null)
				{
					throw new FileNotFoundException("No file selected");
				}
				
				PerformUpload.doUpload((JDBCDatabase) db, this.model.getMolgenisFile(), file);
				this.setMessages(new ScreenMessage("File uploaded", true));
			}

			this.setMessages();
		}

		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
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
			FormModel<Entity> theParent = (FormModel<Entity>) this.getParent();
			MolgenisFile molgenisFile = (MolgenisFile) theParent.getRecords().get(0);

			this.model.setMolgenisFile(molgenisFile);

			if (mfh == null)
			{
				mfh = new MolgenisFileHandler(db);
			}

			boolean hasFile = false;

			try
			{
				mfh.getFile(molgenisFile);
				hasFile = true;
			}
			catch (FileNotFoundException e)
			{
				// no file found, assume there is none for this MolgenisFile
				// object :)
			}

			this.model.setHasFile(hasFile);

			// doesnt work??
			String db_path = "http://" + HtmlTools.getExposedIPAddress() + ":8080/"
					+ MolgenisServlet.getMolgenisVariantID();

			// db_path = "http://" + "localhost" + ":8080/" +
			// MolgenisServlet.getMolgenisVariantID();

			this.model.setDb_path(db_path);

			String ip = HtmlTools.getExposedIPAddress();
			String app = MolgenisServlet.getMolgenisVariantID();
			String url = "http://" + ip + ":8080/" + app + "/" + "molgenis.do";

			this.model.setIpURl(url);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));

		}
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}
}
