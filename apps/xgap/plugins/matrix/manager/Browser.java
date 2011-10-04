package plugins.matrix.manager;

import matrix.AbstractDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.util.Tuple;

public class Browser
{

	/**
	 * Supposed to contain ONLY navigational logic to move around in the matrix
	 * viewer.
	 */
	private BrowserModel model = new BrowserModel();

	public BrowserModel getModel()
	{
		return model;
	}

	public Browser(Data selectedData, AbstractDataMatrixInstance<Object> instance) throws Exception
	{
		// create instance of complete matrix and run checks
		model.setInstance(instance);
		// System.out.println("*** Browser instance: " +
		// instance.getNumberOfCols() + ", rows: " + instance.getNumberOfRows()
		// + ", first element: " + instance.getElement(0, 0));
		model.setColMax(instance.getNumberOfCols());
		model.setRowMax(instance.getNumberOfRows());

		// System.out.println("*** matrix tostring:");
		// System.out.println(instance.toString());

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

	private void updateSubmatrix() throws Exception
	{
		// helper vars
		int nRows = model.getRowStop() - model.getRowStart();
		int nCols = model.getColStop() - model.getColStart();

		// create and set submatrix
		AbstractDataMatrixInstance<Object> subMatrix = model.getInstance().getSubMatrixByOffset(model.getRowStart(),
				nRows, model.getColStart(), nCols);
		model.setSubMatrix(subMatrix);
		//System.out.println("*** submatrix updated, cols: " + subMatrix.getNumberOfCols() + ", rows: "
		//		+ subMatrix.getNumberOfRows() + ", first element: " + subMatrix.getElement(0, 0));
	}

	private void moveActionFollowup() throws Exception
	{
		verifyColStart();
		verifyRowStart();
		determineColStop();
		determineRowStop();
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
	public String applyFilters(Tuple request, Database db) throws Exception
	{
		String filter = null;
		
		AbstractDataMatrixInstance<Object> filterMatrix = null;
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
		
		if(action.endsWith("by_index"))
		{
			String field = request.getString("add_filter_by_indexFILTER_FIELD");
			String operator = request.getString("add_filter_by_indexFILTER_OPERATOR");
			Object value = request.getObject("add_filter_by_indexFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByIndex(q);
			this.model.setSubMatrix(filterMatrix);
			
			filter = action.replace("_", " ") + ", " + field + " " + operator + " " + value;
			
		}
		else if(action.endsWith("by_col_value"))
		{
			String field = request.getString("add_filter_by_col_valueFILTER_FIELD");
			String operator = request.getString("add_filter_by_col_valueFILTER_OPERATOR");
			Object value = request.getObject("add_filter_by_col_valueFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByColMatrixValues(q);
			this.model.setSubMatrix(filterMatrix);
			
			filter = action.replace("_", " ") + ", " + field + " " + operator + " " + value;

		}
		else if(action.endsWith("by_row_value"))
		{
			String field = request.getString("add_filter_by_row_valueFILTER_FIELD");
			String operator = request.getString("add_filter_by_row_valueFILTER_OPERATOR");
			Object value = request.getObject("add_filter_by_row_valueFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByRowMatrixValues(q);
			this.model.setSubMatrix(filterMatrix);
			
			filter = action.replace("_", " ") + ", " + field + " " + operator + " " + value;
		
		}
		else if(action.endsWith("by_col_attrb"))
		{
			String field = request.getString("add_filter_by_col_attrbFILTER_FIELD");
			String operator = request.getString("add_filter_by_col_attrbFILTER_OPERATOR");
			Object value = request.getObject("add_filter_by_col_attrbFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByColEntityValues(db, q);
			this.model.setSubMatrix(filterMatrix);
			
			filter = action.replace("_", " ") + ", " + field + " " + operator + " " + value;
		}
		else if(action.endsWith("by_row_attrb"))
		{
			String field = request.getString("add_filter_by_row_attrbFILTER_FIELD");
			String operator = request.getString("add_filter_by_row_attrbFILTER_OPERATOR");
			Object value = request.getObject("add_filter_by_row_attrbFILTER_VALUE");
			QueryRule q = new QueryRule(field, Operator.valueOf(operator), value);
			filterMatrix = filterMatrix.getSubMatrixFilterByRowEntityValues(db, q);
			this.model.setSubMatrix(filterMatrix);
			
			filter = action.replace("_", " ") + ", " + field + " " + operator + " " + value;
		}
		
		model.setWidth(this.model.getSubMatrix().getNumberOfCols());
		model.setHeight(this.model.getSubMatrix().getNumberOfRows());
		
		verifyColStart();
		verifyRowStart();
		determineColStop();
		determineRowStop();
		
		return filter;
	}

}
