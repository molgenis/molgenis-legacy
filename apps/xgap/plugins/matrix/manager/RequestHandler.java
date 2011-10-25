package plugins.matrix.manager;

import org.molgenis.framework.db.Database;
import org.molgenis.util.Tuple;

import plugins.rplot.MakeRPlot;



public class RequestHandler {
	
	public static void handle(MatrixManagerModel screenModel, Tuple request, Database db) throws Exception {
		String action = request.getString("__action");
		if (action.equals("refresh")) {
			screenModel.setSelectedData(null);
		}
		else if (action.equals("changeSubmatrixSize")) {
			screenModel.getBrowser().update();
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
			MakeRPlot.plot(screenModel, rowName, colName, action, type, width, height);
		}
		else if (action.equals("moveRight")) {
			screenModel.getBrowser().moveRight();
		}
		else if (action.equals("moveLeft")) {
			screenModel.getBrowser().moveLeft();
		}
		else if (action.equals("moveDown")) {
			screenModel.getBrowser().moveDown();
		}
		else if (action.equals("moveUp")) {
			screenModel.getBrowser().moveUp();
		}
		else if (action.equals("moveFarRight")) {
			screenModel.getBrowser().moveFarRight();
		}
		else if (action.equals("moveFarLeft")) {
			screenModel.getBrowser().moveFarLeft();
		}
		else if (action.equals("moveFarDown")) {
			screenModel.getBrowser().moveFarDown();
		}
		else if (action.equals("moveFarUp")) {
			screenModel.getBrowser().moveFarUp();
		}else{
			throw new Exception("Action '"+action+"' unknown.");
		}
	}

}
