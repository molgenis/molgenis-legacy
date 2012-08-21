package plugins.matrix.manager;

import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.ApplicationController;
import org.molgenis.util.Tuple;

public class Browser
{

	/**
	 * Supposed to contain ONLY navigational logic to move around in the matrix
	 * viewer.
	 */
	private BrowserModel model = new BrowserModel();
	
	private ApplicationController ac;

	public BrowserModel getModel()
	{
		return model;
	}

	public Browser(Data selectedData, DataMatrixInstance instance, ApplicationController ac) throws Exception
	{
		this.ac = ac;
		
		// create instance of complete matrix and run checks
		model.setInstance(instance);
		model.setColMax(instance.getNumberOfCols());
		model.setRowMax(instance.getNumberOfRows());


		runChecks(selectedData);

		// set defaults for first view
		model.setStepSize(5);
		model.setHeight(10);
		model.setWidth(5);
		model.setColStart(0);
		model.setRowStart(0);
		model.setColStop(model.getColMax() < model.getWidth() ? model.getColMax() : model.getWidth());
	
		model.setRowStop(model.getRowMax() < model.getHeight() ? model.getRowMax() : model.getHeight());

		// set first submatrix using the defaults
		updateSubmatrix();
	}

	private void runChecks(Data selectedData) throws DatabaseException
	{
		if (model.getColMax() < 1)
		{
			throw new DatabaseException("Datamatrix '" + selectedData.getName() + "' has no columns.");
		}
		if (model.getRowMax() < 1)
		{
			throw new DatabaseException("Datamatrix '" + selectedData.getName() + "' has no rows.");
		}
	}
	
	public void updateSubmatrixKeepRows() throws Exception
	{
		setStartAndStops();
		
		List<String> rowNames = this.getModel().getSubMatrix().getRowNames();
		List<String> colNames = this.getModel().getInstance().getColNames().subList(model.getColStart(), model.getColStop());
		
		// create and set submatrix
		DataMatrixInstance subMatrix = model.getInstance().getSubMatrix(rowNames, colNames);
		model.setSubMatrix(subMatrix);
		
		//store pointer for csv download 'visible'
		ac.sessionVariables.put(MatrixManager.SESSION_MATRIX_DATA, subMatrix);
	}
	
	public void updateSubmatrixKeepCols() throws Exception
	{
		setStartAndStops();
		
		List<String> rowNames = this.getModel().getInstance().getRowNames().subList(model.getRowStart(), model.getRowStop());
		List<String> colNames = this.getModel().getSubMatrix().getColNames();
		
		// create and set submatrix
		DataMatrixInstance subMatrix = model.getInstance().getSubMatrix(rowNames, colNames);
		model.setSubMatrix(subMatrix);
		
		//store pointer for csv download 'visible'
		ac.sessionVariables.put(MatrixManager.SESSION_MATRIX_DATA, subMatrix);
	}

	private void updateSubmatrix() throws Exception
	{
		// helper vars
		int nRows = model.getRowStop() - model.getRowStart();
		int nCols = model.getColStop() - model.getColStart();

		// create and set submatrix
		DataMatrixInstance subMatrix = model.getInstance().getSubMatrixByOffset(model.getRowStart(),
				nRows, model.getColStart(), nCols);
		model.setSubMatrix(subMatrix);
		
		//store pointer for csv download 'visible'
		ac.sessionVariables.put(MatrixManager.SESSION_MATRIX_DATA, subMatrix);

	}
	
	private void setStartAndStops()
	{
		verifyColStart();
		verifyRowStart();
		determineColStop();
		determineRowStop();
	}

	private void moveActionFollowup() throws Exception
	{
		setStartAndStops();
		updateSubmatrix();
	}

	/**
	 * On vertical move actions at the right edge of the matrix combined with a
	 * width setting increase past the edge, the horizontal window would move
	 * out of range. The colStop is verified, but this is based on colStart
	 * which can be wrong at that point. Same goes for horizontal move actions
	 * under inverse conditions. This function compensates.
	 */
	private void verifyColStart()
	{
		if (model.getColStart() + model.getWidth() > model.getColMax())
		{
			if (model.getWidth() > model.getColMax())
			{
				model.setColStart(0);
			}
			else
			{
				model.setColStart(model.getColMax() - model.getWidth());
			}
		}
	}

	/**
	 * On horizontal move actions at the bottom edge of the matrix combined with a
	 * height setting increase past the edge, the vertical window would move
	 * out of range. The rowStop is verified, but this is based on rowStart
	 * which can be wrong at that point. Same goes for vertical move actions
	 * under inverse conditions. This function compensates.
	 */
	private void verifyRowStart()
	{
		if (model.getRowStart() + model.getHeight() > model.getRowMax())
		{
			if (model.getHeight() > model.getRowMax())
			{
				model.setRowStart(0);
			}
			else
			{
				model.setRowStart(model.getRowMax() - model.getHeight());
			}
		}
	}

	/**
	 * Determine value for column stop. If width is smaller than maximum column
	 * value, the stop position is the current column position plus the width.
	 * If the maximum column value is equal ('perfect fit') or smaller ('window
	 * cut-off') than the width, the stop position is the maximum possible value
	 * for column instead.
	 */
	private void determineColStop()
	{
		if (model.getWidth() < model.getColMax())
		{
			model.setColStop(model.getColStart() + model.getWidth());
		}
		else
		{
			model.setColStop(model.getColMax());
		}
	}

	/**
	 * Determine value for row stop. If height is smaller than maximum row
	 * value, the stop position is the current row position plus the height.
	 * If the maximum row value is equal ('perfect fit') or smaller ('window
	 * cut-off') than the height, the stop position is the maximum possible value
	 * for row instead.
	 */
	private void determineRowStop()
	{
		if (model.getHeight() < model.getRowMax())
		{
			model.setRowStop(model.getRowStart() + model.getHeight());
		}
		else
		{
			model.setRowStop(model.getRowMax());
		}
	}

	/**
	 * Moves viewed sub matrix to the right. The step size is added to the
	 * current column position. If this new column position exceeds the maximum
	 * column value minus the width ('window size'), it is set to this maximum
	 * 'window size' value instead. If the width is greater than the maximum
	 * possible column value, the matrix fits inside the 'viewing window' and
	 * the column position is set to zero.
	 */
	public void moveRight() throws Exception
	{
		if (model.getWidth() < model.getColMax())
		{
			model.setColStart(model.getColStart() + model.getStepSize() > model.getColMax() - model.getWidth() ? model
					.getColMax()
					- model.getWidth() : model.getColStart() + model.getStepSize());
		}
		else
		{
			model.setColStart(0);
		}
		moveActionFollowup();
	}

	/**
	 * Moves viewed sub matrix to the left. The step size is subtracted from the
	 * current column position. If this new column position is less than 0, it
	 * is set to 0 instead.
	 */
	public void moveLeft() throws Exception
	{
		model
				.setColStart(model.getColStart() - model.getStepSize() < 0 ? 0 : model.getColStart()
						- model.getStepSize());
		moveActionFollowup();
	}

	public void moveDown() throws Exception
	{
		if (model.getHeight() < model.getRowMax())
		{
			model.setRowStart(model.getRowStart() + model.getStepSize() > model.getRowMax() - model.getHeight() ? model
					.getRowMax()
					- model.getHeight() : model.getRowStart() + model.getStepSize());
		}
		else
		{
			model.setRowStart(0);
		}
		moveActionFollowup();
	}

	/**
	 * Moves viewed sub matrix upwards. The step size is subtracted from the
	 * current row position. If this new row position is less than 0, it
	 * is set to 0 instead.
	 */
	public void moveUp() throws Exception
	{
		model
				.setRowStart(model.getRowStart() - model.getStepSize() < 0 ? 0 : model.getRowStart()
						- model.getStepSize());
		moveActionFollowup();
	}

	public void moveFarRight() throws Exception
	{
		int colStart = model.getColMax() - model.getWidth();
		model.setColStart(colStart < 0 ? 0 : colStart);
		moveActionFollowup();
	}

	public void moveFarLeft() throws Exception
	{
		model.setColStart(0);
		moveActionFollowup();
	}

	public void moveFarDown() throws Exception
	{
		int rowStart = model.getRowMax() - model.getHeight();
		model.setRowStart(rowStart < 0 ? 0 : rowStart);
		moveActionFollowup();
	}

	public void moveFarUp() throws Exception
	{
		model.setRowStart(0);
		moveActionFollowup();
	}

	/**
	 * Update can be used after setting new width, height or stepsize. It
	 * behaves like a move action, except there is no movement. This will update
	 * the submatrix to the new dimensions.
	 * 
	 * @throws Exception
	 */
	public void update() throws Exception
	{
		moveActionFollowup();
	}

	/**
	 * Apply filters to values in the matrix to either
	 * the whole matrix or current visible matrix.
	 * 
	 * @param request
	 * @throws Exception
	 */
	public String applyFilters(Tuple request, Database db, MatrixManagerModel screenModel) throws Exception
	{
		String filter = null;
		
		DataMatrixInstance filterMatrix = null;
		String action = request.getString("__action");
		if(action.startsWith("filter_visible_")){
			// get the current submatrix (view)
			filterMatrix = this.getModel().getSubMatrix();
		}
		else if(action.startsWith("filter_all_")){
			// get the original complete matrix
			filterMatrix = this.getModel().getInstance();
		}else{
			throw new Exception("filter not prepended with filter_all_ or filter_visible_");
		}
		
		String field = null;
		String operator = null;
		Object value = null;
		
		if(action.endsWith("by_index"))
		{
			screenModel.setSelectedFilterDiv("filter1");
			field = request.getString("add_filter_by_indexFILTER_FIELD");
			operator = request.getString("add_filter_by_indexFILTER_OPERATOR");
			value = request.getObject("add_filter_by_indexFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByIndex(q);
		}
		else if(action.endsWith("by_col_value"))
		{
			screenModel.setSelectedFilterDiv("filter2");
			field = request.getString("add_filter_by_col_valueFILTER_FIELD");
			operator = request.getString("add_filter_by_col_valueFILTER_OPERATOR");
			value = request.getObject("add_filter_by_col_valueFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByColMatrixValues(q);
		}
		else if(action.endsWith("by_row_value"))
		{
			screenModel.setSelectedFilterDiv("filter3");
			field = request.getString("add_filter_by_row_valueFILTER_FIELD");
			operator = request.getString("add_filter_by_row_valueFILTER_OPERATOR");
			value = request.getObject("add_filter_by_row_valueFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByRowMatrixValues(q);
		}
		else if(action.endsWith("by_col_attrb"))
		{
			screenModel.setSelectedFilterDiv("filter4");
			field = request.getString("add_filter_by_col_attrbFILTER_FIELD");
			operator = request.getString("add_filter_by_col_attrbFILTER_OPERATOR");
			value = request.getObject("add_filter_by_col_attrbFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByColEntityValues(db, q);
		}
		else if(action.endsWith("by_row_attrb"))
		{
			screenModel.setSelectedFilterDiv("filter5");
			field = request.getString("add_filter_by_row_attrbFILTER_FIELD");
			operator = request.getString("add_filter_by_row_attrbFILTER_OPERATOR");
			value = request.getObject("add_filter_by_row_attrbFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByRowEntityValues(db, q);
		}
		
		this.model.setSubMatrix(filterMatrix);
		
		filter = action.replace("_", " ") + ", " + field + " " + operator.toLowerCase() + " " + value;
		
		//store pointer for csv download 'visible'
		ac.sessionVariables.put(MatrixManager.SESSION_MATRIX_DATA, this.model.getSubMatrix());
		
		model.setWidth(this.model.getSubMatrix().getNumberOfCols());
		model.setHeight(this.model.getSubMatrix().getNumberOfRows());
		
		setStartAndStops();
		
		return filter;
	}

	// kept seperate from regular filters for now - highly experimental stuff :)
	public String apply2DFilter(Tuple request, Database db) throws Exception
	{
		String filter = null;
		
		DataMatrixInstance filterMatrix = null;
		String action = request.getString("__action");
		if(action.startsWith("2d_filter_visible_")){
			// get the current submatrix (view)
			filterMatrix = this.getModel().getSubMatrix();
		}
		else if(action.startsWith("2d_filter_all_")){
			// get the original complete matrix
			filterMatrix = this.getModel().getInstance();
		}else{
			throw new Exception("filter not prepended with 2d_filter_all_ or 2d_filter_visible_");
		}
		
		String amount = null;
		String operator = null;
		Object value = null;
		
		if(action.endsWith("row"))
		{
			amount = request.getString("2d_filter_by_row_AMOUNT");
			operator = request.getString("2d_filter_by_row_FILTER_OPERATOR");
			value = request.getObject("2d_filter_by_row_FILTER_VALUE");
			QueryRule q = new QueryRule(amount, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrix2DFilterByRow(q);
		}
		else if(action.endsWith("col"))
		{
			amount = request.getString("2d_filter_by_col_AMOUNT");
			operator = request.getString("2d_filter_by_col_FILTER_OPERATOR");
			value = request.getObject("2d_filter_by_col_FILTER_VALUE");
			QueryRule q = new QueryRule(amount, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrix2DFilterByCol(q);
		}
		
		this.model.setSubMatrix(filterMatrix);
		
		filter = action.replace("_", " ") + ", " + amount + " " + operator.toLowerCase() + " " + value;
		
		//store pointer for csv download 'visible'
		ac.sessionVariables.put(MatrixManager.SESSION_MATRIX_DATA, this.model.getSubMatrix());
		
		model.setWidth(this.model.getSubMatrix().getNumberOfCols());
		model.setHeight(this.model.getSubMatrix().getNumberOfRows());
		
		setStartAndStops();
		
		return filter;
	}

	public String applySelect(Tuple request, Database db, MatrixManagerModel screenModel) throws Exception
	{
		String action = request.getString("__action");
		
		if(action.endsWith("cols"))
		{
			screenModel.setSelectedFilterDiv("filter8");
			
			List<String> colNames = new ArrayList<String>();
			for(String colName : this.getModel().getInstance().getColNames())
			{
				if(request.getString("colselect_"+colName) != null){
					colNames.add(colName);
				}
			}
			if(colNames.size() == 0)
			{
				throw new Exception("No column names were selected!");
			}
			
			List<String> rowNames = null;
			if(action.contains("preserverows"))
			{
				rowNames = this.getModel().getSubMatrix().getRowNames();
			}
			else
			{
				rowNames = this.getModel().getInstance().getRowNames();
			}
						
			this.model.setSubMatrix(this.model.getInstance().getSubMatrix(rowNames, colNames));
			
			//store pointer for csv download 'visible'
			ac.sessionVariables.put(MatrixManager.SESSION_MATRIX_DATA, this.model.getSubMatrix());
			
			model.setWidth(this.model.getSubMatrix().getNumberOfCols());
			model.setHeight(this.model.getSubMatrix().getNumberOfRows());
			
			setStartAndStops();
			
			return "custom column selection";
			
		}
		else if(action.endsWith("rows"))
		{
			screenModel.setSelectedFilterDiv("filter9");
			
			List<String> rowNames = new ArrayList<String>();
			for(String rowName : this.getModel().getInstance().getRowNames())
			{
				if(request.getString("rowselect_"+rowName) != null){
					rowNames.add(rowName);
				}
			}
			if(rowNames.size() == 0)
			{
				throw new Exception("No row names were selected!");
			}
			
			List<String> colNames = null;
			if(action.contains("preservecols"))
			{
				colNames = this.getModel().getSubMatrix().getColNames();
			}
			else
			{
				colNames = this.getModel().getInstance().getColNames();
			}
						
			this.model.setSubMatrix(this.model.getInstance().getSubMatrix(rowNames, colNames));
			
			//store pointer for csv download 'visible'
			ac.sessionVariables.put(MatrixManager.SESSION_MATRIX_DATA, this.model.getSubMatrix());
			
			model.setWidth(this.model.getSubMatrix().getNumberOfCols());
			model.setHeight(this.model.getSubMatrix().getNumberOfRows());
			
			setStartAndStops();
			
			return "custom row selection";
		}

		
		
		
		return null;
	}

}
