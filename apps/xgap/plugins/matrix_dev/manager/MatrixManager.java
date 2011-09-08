/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix_dev.manager;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.general.Importer;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.matrix.MatrixException;
import org.molgenis.matrix.component.MatrixRenderer;
import org.molgenis.matrix.component.general.MatrixRendererHelper;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.Tuple;

public class MatrixManager extends PluginModel
{

	private static final long serialVersionUID = -7727254896269295472L;
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

// moved overlib to molgenis core
//	@Override
//	public String getCustomHtmlHeaders()
//	{
//		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";
//
//	}

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

	public void handleRequest(Database db, Tuple request, OutputStream out)
	{
		if (request.getString("__action") != null)
		{
			try
			{
				if (this.model.isUploadMode())
				{
					Importer.performImport(request, this.model.getSelectedData(), db);
					this.model.setSelectedData(null); // force refresh in reload
				}
				else
				{
					String action = request.getString("__action");
					if (action.startsWith(MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX))
					{
						model.getMatrix().delegateHandleRequest(request);
					}

					if (action.equals("resetMatrixRenderer"))
					{
						setupRenderer(this.model.getSelectedData(), db);
					}
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

	public boolean dataHasChanged(Data newData, Data oldData)
	{

		for (String f : oldData.getFields())
		{
			Object oldAttr = oldData.get(f);
			Object newAttr = newData.get(f);

			if (oldAttr == null && newAttr == null)
			{
				// equal if both are null, return false
			}
			else if (oldAttr == null || newAttr == null)
			{
				// unequal if either is null
				return true;
			}
			else if (!newAttr.equals(oldAttr))
			{
				// if both not full, perform 'equals'
				return true;
			}
		}
		return false;
	}

	@Override
	public void reload(Database db)
	{

		ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
		FormModel<Data> parentForm = (FormModel<Data>) ((FormController) parentController).getModel();
		Data data = parentForm.getRecords().get(0);

		try
		{

			if (this.dmh == null)
			{
				dmh = new DataMatrixHandler(db);
			}

			boolean newOrOtherData;

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
				this.model.setHasBackend(dmh.isDataStoredIn(data, data.getStorage()));
				
				if (this.model.isHasBackend())
				{
					setupRenderer(data, db);
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

		}catch(MatrixException me)
		{
			//backend present, but not properly annotated
			this.model.setUploadMode(false); //disable uploading
			model.setMatrix(null); //disable renderer
			this.model.setSelectedData(null); // force refresh in next reload
			this.setMessages(new ScreenMessage(me.getMessage() != null ? me.getMessage() : "null", false));
		}
		catch (Exception e)
		{
			//other exceptions
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}

	}

	private void setupRenderer(Data data, Database db) throws Exception
	{
		System.out.println("setupRenderer");
		// setup the first matrix with bogus values that should be overwritten
		AbstractDataMatrixInstance<Object> matrix = dmh.createInstance(data);

		List<String> colNames = matrix.getColNames();
		List<String> rowNames = matrix.getRowNames();

		List<ObservationElement> annotatedCols = db.find(ObservationElement.class, new QueryRule(
				ObservationElement.NAME, Operator.IN, colNames));
		List<ObservationElement> annotatedRows = db.find(ObservationElement.class, new QueryRule(
				ObservationElement.NAME, Operator.IN, rowNames));

		List<String> annotatedColNames = new ArrayList<String>();
		List<String> annotatedRowNames = new ArrayList<String>();

		for (ObservationElement o : annotatedCols)
		{
			annotatedColNames.add(o.getName());
		}
		for (ObservationElement o : annotatedRows)
		{
			annotatedRowNames.add(o.getName());
		}

		if (colNames.size() == annotatedColNames.size() && rowNames.size() == annotatedRowNames.size())
		{
			matrix.setup(db);

			// create and set the renderer
			MatrixRenderer<ObservationElement, ObservationElement, Object> renderer = new MatrixRenderer<ObservationElement, ObservationElement, Object>(
					"xgap_matrix", matrix, matrix, this.getName());
			model.setMatrix(renderer);
		}
		else
		{
			String missingColNames = "";
			String missingRowNames = "";

			for (String c : colNames)
			{
				if (!annotatedColNames.contains(c))
				{
					missingColNames += c + " ";
				}
			}
			for (String r : rowNames)
			{
				if (!annotatedRowNames.contains(r))
				{
					missingRowNames += r + " ";
				}
			}

			String exc = "";
			if (missingColNames != "")
			{
				exc += "Col names: " + missingColNames;
			}
			if (missingRowNames != "")
			{
				exc += "Row names: " + missingRowNames;
			}
			if (exc.length() > 1000)
			{
				exc = exc.substring(0, 1000) + " and more";
			}
			throw new MatrixException("You are missing the following annotations - " + exc);
		}

	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		handleRequest(db, request, null);
	}

}
