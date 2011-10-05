/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.general.Importer;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.pheno.ObservationElement;
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

					RequestHandler.handle(this.model, request, db);
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
	
	private void setHeaderAttr(Database db) throws DatabaseException, MolgenisModelException, InstantiationException, IllegalAccessException
	{
		
		ObservationElement target = (ObservationElement) db.getClassForName(this.model.getSelectedData().getTargetType()).newInstance();
		ObservationElement feature = (ObservationElement) db.getClassForName(this.model.getSelectedData().getFeatureType()).newInstance();
		this.model.setRowHeaderAttr(target.getFields());
		this.model.setColHeaderAttr(feature.getFields());
	}

	private void createOverLibText(Database db) throws Exception
	{
		// System.out.println("*** createOverLibText");
		List<String> rowNames = this.model.getBrowser().getModel().getSubMatrix().getRowNames();
		List<String> colNames = this.model.getBrowser().getModel().getSubMatrix().getColNames();
		this.model.setRowObsElem((OverlibText.getObservationElements(db, rowNames, this.model.getSelectedData().getTargetType())));
		this.model.setColObsElem((OverlibText.getObservationElements(db, colNames,  this.model.getSelectedData().getFeatureType())));
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
	
	private void setAllOperators()
	{
		HashMap<String, String> ops = new HashMap<String, String>();
		ops.put("GREATER", "greater than");
		ops.put("GREATER_EQUAL", "greater and equal");
		ops.put("LESS", "less than");
		ops.put("LESS_EQUAL", "less and equal");
		ops.put("EQUALS", "equals exactly");
//		ops.put("SORTASC", "sort asc");
//		ops.put("SORTDESC", "sort desc");
//		ops.put("NOT", "is not");
		this.model.setAllOperators(ops);
	}
	
	private void setValueOperators()
	{
		HashMap<String, String> ops = new HashMap<String, String>();
		if(this.model.getSelectedData().getValueType().equals("Decimal"))
		{
			ops.put("GREATER", "greater than");
			ops.put("GREATER_EQUAL", "greater and equal");
			ops.put("LESS", "less than");
			ops.put("LESS_EQUAL", "less and equal");
		}
		ops.put("EQUALS", "equals exactly");
//		ops.put("SORTASC", "sort asc");
//		ops.put("SORTDESC", "sort desc");
//		ops.put("NOT", "is not");
		this.model.setValueOperators(ops);
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
			
			if(this.model.getSelectedFilterDiv() == null)
			{
				this.model.setSelectedFilterDiv("filter1");
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
					
					//refresh attributes, operators, filter
					this.model.setRowHeaderAttr(null);
					this.model.setColHeaderAttr(null);
					this.model.setAllOperators(null);
					this.model.setValueOperators(null);
					this.model.setFilter(null);
				}
			}

			if (this.model.isHasBackend())
			{
				this.model.setUploadMode(false);
				createOverLibText(db);
				createHeaders();
				
				if(model.getRowHeaderAttr() == null || model.getColHeaderAttr() == null)
				{
					setHeaderAttr(db);
				}
				
				if(model.getAllOperators() == null)
				{
					setAllOperators();
				}
				
				if(model.getValueOperators() == null)
				{
					setValueOperators();
				}
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
