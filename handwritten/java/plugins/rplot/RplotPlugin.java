/* Date:        February 2, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugins.rplot;

import java.io.File;
import java.io.PrintWriter;

import matrix.AbstractDataMatrixInstance;
import matrix.general.DataMatrixHandler;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.FormModel;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.util.Tuple;

public class RplotPlugin extends PluginModel
{

	private DataMatrixHandler dmh = null;
	
	private RplotPluginModel model = new RplotPluginModel();

	public RplotPluginModel getModel()
	{
		return model;
	}

	public void clearMessage()
	{
		this.setMessages();
	}

	public RplotPlugin(String name, ScreenModel parent)
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
		return "RplotPlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/rplot/RplotPlugin.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		System.out.println("*** handleRequest WRAPPER __action: " + request.getString("__action"));
		this.handleRequest(db, request, null);
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	public void handleRequest(Database db, Tuple request, PrintWriter out)
	{
		if (request.getString("__action") != null)
		{
			System.out.println("*** handleRequest __action: " + request.getString("__action"));

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
				AbstractDataMatrixInstance<Object> instance = this.model.getMatrixInstance(); //shorthand
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
			FormModel<Data> theParent = (FormModel) this.getParent().getParent();
			Data newSelectedData = ((Data) theParent.getRecords().get(0));
			
			if(dmh == null){
				dmh = new DataMatrixHandler(db);
			}

			// first time load, or new matrix selected -> refresh rows/cols
			if (this.model.getSelectedData() == null
					|| !(this.model.getSelectedData().getId().intValue() == newSelectedData.getId().intValue()))
			{
				this.model.setSelectedData(newSelectedData);

				
				AbstractDataMatrixInstance<Object> m = dmh.createInstance(newSelectedData);

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
