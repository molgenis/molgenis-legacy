package plugins.matrix.manager;

import java.io.File;

import matrix.DataMatrixInstance;
import matrix.implementations.database.DatabaseDataMatrixInstance;

import org.molgenis.framework.db.Database;
import org.molgenis.util.Tuple;

import plugins.rplot.MakeRPlot;



public class RequestHandler {
	
	public static void handle(MatrixManagerModel screenModel, Tuple request, Database db) throws Exception {
	
		if(screenModel.getBrowser().getModel().getInstance() instanceof DatabaseDataMatrixInstance)
		{
			((DatabaseDataMatrixInstance)screenModel.getBrowser().getModel().getInstance()).setDatabase(db);
		}
		
		String action = request.getString("__action");
		if (action.equals("refresh")) {
			screenModel.setSelectedData(null);
			screenModel.setTmpImgName(null);
		}
		else if (action.equals("changeSubmatrixSize"))
		{
			int stepSize = request.getInt("stepSize") < 1 ? 1 : request.getInt("stepSize");
			int width = request.getInt("width") < 1 ? 1 : request.getInt("width");
			int height = request.getInt("height") < 1 ? 1 : request.getInt("height");
			
			//if only width has changed, do special update of submatrix to preserve rows
			if(width != screenModel.getBrowser().getModel().getWidth() && height == screenModel.getBrowser().getModel().getHeight())
			{
				setStepWidthHeight(screenModel, stepSize, width, height);
				screenModel.getBrowser().updateSubmatrixKeepRows();
				screenModel.setFilter("width updated, row selection was preserved");
			}
			//if only height has changed, do special update of submatrix to preserve columns
			else if(width == screenModel.getBrowser().getModel().getWidth() && height != screenModel.getBrowser().getModel().getHeight())
			{
				setStepWidthHeight(screenModel, stepSize, width, height);
				screenModel.getBrowser().updateSubmatrixKeepCols();
				screenModel.setFilter("height updated, column selection was preserved");
			}
			//nothing has changed, do not update submatrix (just stepsize)
			else if(width == screenModel.getBrowser().getModel().getWidth() && height == screenModel.getBrowser().getModel().getHeight())
			{
				screenModel.getBrowser().getModel().setStepSize(stepSize);
				screenModel.setFilter("stepsize updated");
			}
			//both have changed, do a regular update of submatrix, resetting row/col to limit/offset defaults
			else
			{
				setStepWidthHeight(screenModel, stepSize, width, height);
				screenModel.getBrowser().update();
				screenModel.setFilter("width and height updated, " + moveMsg);
			}
		}
		else if (action.startsWith("filter")) {
			String filter = screenModel.getBrowser().applyFilters(request, db, screenModel);
			screenModel.setFilter(filter);
		}
		else if (action.startsWith("select_")) {
			String filter = screenModel.getBrowser().applySelect(request, db, screenModel);
			screenModel.setFilter(filter);
		}
		else if (action.startsWith("2d_filter")) {
			screenModel.setSelectedFilterDiv("filter7");
			String filter = screenModel.getBrowser().apply2DFilter(request, db);
			screenModel.setFilter(filter);
		}
		else if(action.startsWith("r_plot")){
			
			String rowName = request.getString("r_plot_row_select");
			String colName = request.getString("r_plot_col_select");
			String type = request.getString("r_plot_type");
			if(action.endsWith("heatmap")) type = request.getString("r_heatmap_type");
			int width = Integer.parseInt(request.getString("r_plot_resolution").split("x")[0]);
			int height = Integer.parseInt(request.getString("r_plot_resolution").split("x")[1]);
			screenModel.setSelectedWidth(width);
			screenModel.setSelectedHeight(height);
			screenModel.setSelectedFilterDiv("filter6");
			
			DataMatrixInstance instance;
			if(action.startsWith("r_plot_full"))
			{
				instance = screenModel.getBrowser().getModel().getInstance();
			}else if(action.startsWith("r_plot_visible"))
			{
				instance = screenModel.getBrowser().getModel().getSubMatrix();
			}else
			{
				throw new Exception("unrecognized action: " + action);
			}
			
			File img = MakeRPlot.plot(screenModel.getSelectedData(), instance, rowName, colName, action, type, width, height);
			screenModel.setTmpImgName(img.getName());
		}
		else if (action.equals("moveRight")) {
			screenModel.getBrowser().moveRight();
			screenModel.setFilter("moved right, " + moveMsg);
		}
		else if (action.equals("moveLeft")) {
			screenModel.getBrowser().moveLeft();
			screenModel.setFilter("moved left, " + moveMsg);
		}
		else if (action.equals("moveDown")) {
			screenModel.getBrowser().moveDown();
			screenModel.setFilter("moved down, " + moveMsg);
		}
		else if (action.equals("moveUp")) {
			screenModel.getBrowser().moveUp();
			screenModel.setFilter("moved up, " + moveMsg);
		}
		else if (action.equals("moveFarRight")) {
			screenModel.getBrowser().moveFarRight();
			screenModel.setFilter("moved far right, " + moveMsg);
		}
		else if (action.equals("moveFarLeft")) {
			screenModel.getBrowser().moveFarLeft();
			screenModel.setFilter("moved far left, " + moveMsg);
		}
		else if (action.equals("moveFarDown")) {
			screenModel.getBrowser().moveFarDown();
			screenModel.setFilter("moved far down, " + moveMsg);
		}
		else if (action.equals("moveFarUp")) {
			screenModel.getBrowser().moveFarUp();
			screenModel.setFilter("moved far up, " + moveMsg);
		}else{
			throw new Exception("Action '"+action+"' unknown.");
		}
	}
	
	static String moveMsg = "restored matrix to unfiltered state (paging by index)";
	
	private static void setStepWidthHeight(MatrixManagerModel screenModel, int stepSize, int width, int height)
	{
		screenModel.getBrowser().getModel().setStepSize(stepSize);
		screenModel.getBrowser().getModel().setWidth(width);
		screenModel.getBrowser().getModel().setHeight(height);
	}

}
