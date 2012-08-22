/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.matrix.manager;

import java.util.HashMap;
import java.util.List;

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

	protected DataMatrixHandler dmh = null;
	
	public static String SESSION_MATRIX_DATA = "session_inmemory_matrix_data";
	
	protected MatrixManagerModel model = new MatrixManagerModel();

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

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

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
					RequestHandler.handle(this.model, request, db);
				}

			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
			}
		}
	}

	public void setHeaderAttr(Database db) throws DatabaseException, MolgenisModelException, InstantiationException, IllegalAccessException
	{
		
		ObservationElement target = (ObservationElement) db.getClassForName(this.model.getSelectedData().getTargetType()).newInstance();
		ObservationElement feature = (ObservationElement) db.getClassForName(this.model.getSelectedData().getFeatureType()).newInstance();
		this.model.setRowHeaderAttr(target.getFields());
		this.model.setColHeaderAttr(feature.getFields());
	}

	public void createOverLibText(Database db) throws Exception
	{
		List<String> rowNames = this.model.getBrowser().getModel().getSubMatrix().getRowNames();
		List<String> colNames = this.model.getBrowser().getModel().getSubMatrix().getColNames();
		this.model.setRowObsElem((OverlibText.getObservationElements(db, rowNames, this.model.getSelectedData().getTargetType())));
		this.model.setColObsElem((OverlibText.getObservationElements(db, colNames,  this.model.getSelectedData().getFeatureType())));
	}

	public void createHeaders()
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
	
	public void setAllOperators()
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
	
	public void setValueOperators()
	{
		HashMap<String, String> ops = new HashMap<String, String>();
		//if(this.model.getSelectedData().getValueType().equals("Decimal"))
		//{
			ops.put("GREATER", "greater than");
			ops.put("GREATER_EQUAL", "greater and equal");
			ops.put("LESS", "less than");
			ops.put("LESS_EQUAL", "less and equal");
		//}
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

			Object oldAttr = oldData.get(f);
			Object newAttr = newData.get(f);

			if (oldAttr == null && newAttr == null)
			{
				// equal if both are null
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
			else
			{
			}

		}
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
				this.model.setSelectedFilterDiv("filter2");
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
				//find out if the 'Data' has a proper backend
				boolean hasLinkedStorage = dmh.isDataStoredIn(data, data.getStorage(), db);
				
				//if not, it doesn't mean the source file is not there! e.g. after updating your database
				if(!hasLinkedStorage)
				{
					//attempt to relink
					boolean relinked = dmh.attemptStorageRelink(data, data.getStorage(), db);
					
					if(relinked)
					{
						this.setMessages(new ScreenMessage("INFO: Datamatrix '"+data.getName()+"' relinked to a storage file with the same (file escaped) name. Please make sure this is intented.", true));
					}
					
					//and requery
					this.model.setHasBackend(dmh.isDataStoredIn(data, data.getStorage(), db));
				}
				else
				{
					//already has properly linked storage
					this.model.setHasBackend(true);
				}
				
				if (this.model.isHasBackend())
				{
					logger.info("*** creating browser instance");
					Browser br = new CreateBrowserInstance(db, data, this.getApplicationController()).getBrowser();
					this.model.setBrowser(br);
					
					if(!br.getModel().getInstance().getData().getValueType().equals(data.getValueType()))
					{
						String oldDt = data.getValueType();
						String newDt = br.getModel().getInstance().getData().getValueType();
						
						data.setValueType(newDt);
						db.update(data);
						
						this.setMessages(new ScreenMessage("WARNING: Data valuetype '" + oldDt + "' adjusted to storage specification '" + newDt + "' to prevent problems with e.g. value types", true));
					}
					
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
