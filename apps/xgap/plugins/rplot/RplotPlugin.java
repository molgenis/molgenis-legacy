/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.rplot;

import java.io.File;

import matrix.AbstractDataMatrixInstance;
import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormController;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class RplotPlugin<E extends Entity> extends PluginModel<E>
{

	private static final long serialVersionUID = 2598093872153856022L;
	private DataMatrixHandler dmh = null;
	private RplotPluginModel model = new RplotPluginModel();

	public RplotPluginModel getMyModel()
	{
		return model;
	}

	public RplotPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

// moved overlib to molgenis core
//	@Override
//	public String getCustomHtmlHeaders()
//	{
//		return "<script src=\"res/scripts/overlib.js\" language=\"javascript\"></script>";
//	}

	@Override
	public String getViewName()
	{
		return "RplotPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/rplot/RplotPlugin.ftl";
	}

	public void handleRequest(Database db, Tuple request)
	{
		if (request.getString("__action") != null)
		{

			try
			{

				this.model.setSelectedRow(request.getString("rowSelect"));
				this.model.setSelectedCol(request.getString("colSelect"));
				this.model.setSelectedWidth(Integer.parseInt(request.getString("resolution").split("x")[0]));
				this.model.setSelectedHeight(Integer.parseInt(request.getString("resolution").split("x")[1]));
				this.model.setSelectedPlotType(request.getString("typeSelect"));

				String rowType = this.model.getSelectedData().getTargetType(); //shorthand
				String colType = this.model.getSelectedData().getFeatureType(); //shorthand
				String rowName = this.model.getSelectedRow(); //shorthand
				String colName = this.model.getSelectedCol(); //shorthand
				DataMatrixInstance instance = this.model.getMatrixInstance(); //shorthand
				Data data = this.model.getSelectedData(); //shorthand
				String action = request.getString("__action"); //shorthand
				
				Object[] plotThis = null;
				PlotParameters params = new PlotParameters();
				
				if (action.equals("plotRow"))
				{
					if (data.getValueType().equals("Text"))
					{
						params.setTitle(rowType + " " + rowName);
						params.setxLabel("Type of " + colType);
						params.setyLabel("# of " + colType);
					}else if (data.getValueType().equals("Decimal"))
					{
						params.setTitle(rowType + " " + rowName);
						params.setxLabel(colType);
						params.setyLabel(rowType + " value");
					}
					plotThis = instance.getRow(rowName);
				}
				
				else if(action.equals("plotCol"))
				{
					if (data.getValueType().equals("Text"))
					{
						params.setTitle(colType + " " + colName);
						params.setxLabel("Type of " + rowType);
						params.setyLabel("# of " + rowType);

					}
					else if (data.getValueType().equals("Decimal"))
					{
						params.setTitle(colType + " " + colName);
						params.setxLabel(rowType);
						params.setyLabel(colType + " value");
					}
					plotThis = instance.getCol(colName);
				}
				
				if (action.equals("plotRow") || action.equals("plotCol"))
				{
					File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot"
							+ System.nanoTime() + ".png");
					params.setType(request.getString("typeSelect"));
					params.setWidth(this.model.getSelectedWidth());
					params.setHeight(this.model.getSelectedHeight());

					if (this.model.getSelectedPlotType().equals("boxplot"))
					{
						params.setFunction("boxplot");
					}
					else
					{
						params.setFunction("plot");
					}

					new ScriptInstance(plotThis, tmpImg, params);
					this.model.setTmpImgName(tmpImg.getName());

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
			//FormModel<Data> theParent = (FormModel<Data>) this.getParent().getParent();
			//Data newSelectedData = ((Data) theParent.getRecords().get(0));
			
			ScreenController<?> parentController = (ScreenController<?>) this.getParent().getParent();
			FormModel<Data> parentForm = (FormModel<Data>) ((FormController)parentController).getModel();
			Data newSelectedData = parentForm.getRecords().get(0);

			
			if(dmh == null){
				dmh = new DataMatrixHandler(db);
			}

			// first time load, or new matrix selected -> refresh rows/cols
			if (this.model.getSelectedData() == null
					|| !(this.model.getSelectedData().getId().intValue() == newSelectedData.getId().intValue()))
			{
				this.model.setSelectedData(newSelectedData);

				
				DataMatrixInstance m = dmh.createInstance(newSelectedData, db);

				this.model.setMatrixCols(m.getColNames());
				this.model.setMatrixRows(m.getRowNames());
				this.model.setMatrixInstance(m);

			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

}
