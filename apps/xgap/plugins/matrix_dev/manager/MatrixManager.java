/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix_dev.manager;

import java.io.OutputStream;

import matrix.XgapRenderableMatrix;
import matrix.general.DataMatrixHandler;
import matrix.general.Importer;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.component.MatrixRenderer;
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
		return "plugins/matrix_dev/manager/MatrixManager.ftl";
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	public void handleRequest(Database db, Tuple request, OutputStream out)
	{
		if (request.getString("__action") != null)
		{
			try
			{
				if (this.model.isUploadMode())
				{
					Importer.performImport(request, this.model.getSelectedData(), db);
					this.model.setSelectedData(null);
				}
				else
				{
					//RequestHandler.handle(this.model, request, new PrintWriter(out));
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
					System.out.println("**** CREATING XgapRenderableMatrix");
					XgapRenderableMatrix xrm = new XgapRenderableMatrix(db, data, dmh);
					MatrixRenderer m = new MatrixRenderer("piet", xrm);
					model.setMatrix(m);
					System.out.println("**** DONE AND SET XgapRenderableMatrix");
				}
			}

			if (this.model.isHasBackend())
			{
				this.model.setUploadMode(false);
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
		}

	}

	@Override
	public void handleRequest(Database db, Tuple request) {
		handleRequest(db, request, null);
	}

}
