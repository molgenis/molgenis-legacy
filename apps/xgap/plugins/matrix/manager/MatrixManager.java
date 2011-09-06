/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix.manager;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.general.Importer;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Tuple;

public class MatrixManager extends PluginModel
{

	private DataMatrixHandler dmh = null;
	
	private MatrixManagerModel model = new MatrixManagerModel();

	public MatrixManagerModel getMyModel()
	{
		return model;
	}

	public MatrixManager(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";

	}

	@Override
	public String getViewName()
	{
		return "MatrixManager";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/matrix/manager/MatrixManager.ftl";
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			System.out.println("*** handleRequest __action: " + request.getString("__action"));

			try
			{
				if (this.model.isUploadMode())
				{
					// if(request.getString("inputTextArea") != null){
					//DON'T DO THIS: BAD FOR LARGE UPLOADS -> this.getModel().setUploadTextAreaContent(request.getString("inputTextArea"));
					// }
					Importer.performImport(request, this.model.getSelectedData(), db);
					// set to null to force backend check/creation of browser
					// instance
					this.model.setSelectedData(null);
				}
				else
				{
					int stepSize = request.getInt("stepSize") < 1 ? 1 : request.getInt("stepSize");
					int width = request.getInt("width") < 1 ? 1 : request.getInt("width");
					int height = request.getInt("height") < 1 ? 1 : request.getInt("height");

					this.model.getBrowser().getModel().setStepSize(stepSize);
					this.model.getBrowser().getModel().setWidth(width);
					this.model.getBrowser().getModel().setHeight(height);

					RequestHandler.handle(this.model, request);
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

	public static Browser createBrowserInstance(Database db, Data data) throws Exception
	{
		boolean verifiedBackend = false;
		DataMatrixHandler dmh = new DataMatrixHandler(db); //must create new because function is static (reused)
		verifiedBackend = dmh.isDataStoredIn(data, data.getStorage());
		if (verifiedBackend)
		{
			AbstractDataMatrixInstance<Object> m = dmh.createInstance(data);
			Browser br = new Browser(data, m);
			// this.model.setBrowser(br);
			return br;
		}
		else
		{
			throw new Exception("Could not verify existence of data source");
		}
	}

	private void createOverLibText(Database db) throws Exception
	{
		// System.out.println("*** createOverLibText");
		List<String> rowNames = this.model.getBrowser().getModel().getSubMatrix().getRowNames();
		List<String> colNames = this.model.getBrowser().getModel().getSubMatrix().getColNames();
		this.model.setOverlibText(OverlibText.getOverlibText(db, rowNames, colNames));
	}

	public void clearMessage()
	{
		this.setMessages();
	}

	private void createHeaders()
	{
		this.model.setColHeader(this.model.getSelectedData().getFeatureType() + " "
				+ (this.model.getBrowser().getModel().getColStart() + 1) + "-"
				+ this.model.getBrowser().getModel().getColStop() + " of "
				+ this.model.getBrowser().getModel().getColMax());
		this.model.setRowHeader(this.model.getSelectedData().getTargetType() + "<br>"
				+ (this.model.getBrowser().getModel().getRowStart() + 1) + "-"
				+ this.model.getBrowser().getModel().getRowStop() + " of "
				+ this.model.getBrowser().getModel().getRowMax());
	}

	public static boolean dataHasChanged(Data newData, Data oldData)
	{

		for (String f : oldData.getFields())
		{
			// System.out.println("getting + " +f+", old = " + oldData.get(f) +
			// ", new = " + newData.get(f));

			Object oldAttr = oldData.get(f);
			Object newAttr = newData.get(f);

			if (oldAttr == null && newAttr == null)
			{
				// equal if both are null
				// System.out.println("TWO NULLS - EQUAL!");
			}
			else if (oldAttr == null || newAttr == null)
			{
				// unequal if either is null
				// System.out.println("ONE NULL: DOES NOT EQUAL!");
				return true;
			}
			else if (!newAttr.equals(oldAttr))
			{
				// if both not full, perform 'equals'
				// System.out.println("VALUE DIFFERENCE - DOES NOT EQUAL!");
				return true;
			}
			else
			{
				// System.out.println("ALL CONDITIONS MET - EQUAL!");
			}

		}
		// System.out.println("ALL EQUAL");
		return false;
	}

	@Override
	public void reload(Database db)
	{

		// TODO: create refresh button
		// TODO: review this 'core' logic carefully :)

		ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
		FormModel<Data> parentForm = (FormModel<Data>) ((FormController)parentController).getModel();
		Data data = parentForm.getRecords().get(0);

		  
		try
		{
			
			if(this.dmh == null){
				dmh = new DataMatrixHandler(db);
			}

			boolean newOrOtherData;
			// boolean createBrowserSuccess = true; //assume success, can be
			// false if a new instance is created but fails

			if (this.model.getSelectedData() == null)
			{
				newOrOtherData = true;
			}
			else
			{
				if (dataHasChanged(this.model.getSelectedData(), data))
				{
					newOrOtherData = true;
				}
				else
				{
					newOrOtherData = false;
				}
			}

			this.model.setSelectedData(data);

			if (newOrOtherData)
			{
				logger.info("*** newOrOtherData");
				this.model.setHasBackend(dmh.isDataStoredIn(data, data.getStorage()));
				logger.info("hasBackend: " + this.model.isHasBackend());
				if (this.model.isHasBackend())
				{
					logger.info("*** creating browser instance");
					Browser br = createBrowserInstance(db, data);
					this.model.setBrowser(br);

					// moved to Inspector
					// this.model.setWarningsAndErrors(new
					// WarningsAndErrors(data,
					// db, this.model.getBrowser().getModel()
					// .getInstance()));

				}
			}

			if (this.model.isHasBackend())
			{
				this.model.setUploadMode(false);
				createOverLibText(db);
				createHeaders();
			}
			else
			{
				this.model.setUploadMode(true);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			this.model.setBrowser(null);
		}

	}

}
