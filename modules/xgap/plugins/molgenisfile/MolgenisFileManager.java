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
import java.util.List;

import org.molgenis.core.MolgenisFile;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import decorators.MolgenisFileHandler;
import filehandling.generic.PerformUpload;

public class MolgenisFileManager extends PluginModel<Entity>
{

	private static final long serialVersionUID = 7832540415673199206L;

	public MolgenisFileManager(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private MolgenisFileManagerModel model = new MolgenisFileManagerModel();
	private String appLoc;

	public MolgenisFileManagerModel getMyModel()
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
		appLoc = ((MolgenisRequest) request).getAppLocation();
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
				else if (action.equals("showApplet"))
				{
					this.model.setShowApplet(true);
				}
				else if (action.equals("hideApplet"))
				{
					this.model.setShowApplet(false);
				}

				if (file == null)
				{
					throw new FileNotFoundException("No file selected");
				}

				PerformUpload.doUpload(db, this.model.getMolgenisFile(), file, false);
				this.setMessages(new ScreenMessage("File uploaded", true));
			}

		}

		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	@Override
	public void reload(Database db)
	{

		try
		{

			if (this.model.getShowApplet() == null)
			{
				this.model.setShowApplet(false);
			}

			ScreenController<?> parentController = (ScreenController<?>) this.getParent();
			FormModel<MolgenisFile> parentForm = (FormModel<MolgenisFile>) ((FormController) parentController)
					.getModel();

			List<MolgenisFile> molgenisFileList = parentForm.getRecords();
			MolgenisFile molgenisFile = null;

			if (molgenisFileList.size() == 0)
			{
				return;
			}
			else
			{
				molgenisFile = molgenisFileList.get(0);
			}

			this.model.setMolgenisFile(molgenisFile);

			if (mfh == null)
			{
				mfh = new MolgenisFileHandler(db);
			}

			boolean hasFile = false;
			File theFile = null;

			try
			{
				theFile = mfh.getFile(molgenisFile, db);
				hasFile = true;
			}
			catch (FileNotFoundException e)
			{
				// no file found, assume there is none for this MolgenisFile
				// object :)
			}

			this.model.setHasFile(hasFile);

			// set app location
			if (this.model.getDb_path() == null)
			{
				this.model.setDb_path(appLoc);
			}

			if (hasFile)
			{
				this.model.setFileSize(theFile.length());
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));

		}
	}

}
