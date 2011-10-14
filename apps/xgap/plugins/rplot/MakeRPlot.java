package plugins.rplot;

import java.io.File;

import matrix.DataMatrixInstance;

import org.molgenis.data.Data;

import plugins.matrix.manager.MatrixManagerModel;

public class MakeRPlot
{
	public static void plot(MatrixManagerModel model, String rowName, String colName, String action, String type, int width, int height) throws Exception
	{
		String rowType = model.getSelectedData().getTargetType(); //shorthand
		String colType = model.getSelectedData().getFeatureType(); //shorthand
		Data data = model.getSelectedData(); //shorthand
		
		DataMatrixInstance instance = null;
		if(action.startsWith("r_plot_full"))
		{
			instance = model.getBrowser().getModel().getInstance();
		}else if(action.startsWith("r_plot_visible"))
		{
			instance = model.getBrowser().getModel().getSubMatrix();
		}else
		{
			throw new Exception("unrecognized action: " + action);
		}
		
		Object[] plotThis = null;
		PlotParameters params = new PlotParameters();
		
		if (action.endsWith("row"))
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
		
		else if(action.endsWith("col"))
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
		else if(action.endsWith("heatmap"))
		{
			params.setTitle(instance.getData().getName());
			params.setxLabel("");
			params.setyLabel("");
		}
		else
		{
			throw new Exception("unrecognized action: " + action);
		}
		

			File tmpImg = new File(System.getProperty("java.io.tmpdir") + File.separator + "rplot"
					+ System.nanoTime() + ".png");
			params.setType(type);
			params.setWidth(width);
			params.setHeight(height);

			if(action.endsWith("col") || action.endsWith("row"))
			{
				if (type.equals("boxplot"))
				{
					params.setFunction("boxplot");
				}
				else
				{
					params.setFunction("plot");
				}

				new ScriptInstance(plotThis, tmpImg, params);
			}
			else if(action.endsWith("heatmap"))
			{
				new HeatmapScriptInstance(instance, tmpImg, params);
			}
			
			model.setTmpImgName(tmpImg.getName());
		
	}
}
