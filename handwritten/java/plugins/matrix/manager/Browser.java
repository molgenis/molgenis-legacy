package plugins.matrix.manager;

import java.util.List;

import matrix.AbstractDataMatrixInstance;

import org.molgenis.data.Data;
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
		System.out.println("*** submatrix updated, cols: " + subMatrix.getNumberOfCols() + ", rows: "
				+ subMatrix.getNumberOfRows() + ", first element: " + subMatrix.getElement(0, 0));
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
	 * under inverse conditions. These functions fix it.
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

	public void applyFilters(Tuple request) throws Exception
	{
		// get the current submatrix (view)
		AbstractDataMatrixInstance<Object> subMatrix = this.getModel().getSubMatrix();
		
		List<String> colNames = subMatrix.getColNames();
		for (String colName : colNames)
		{
			Integer filterValue = request.getInt("FILTER_VALUE_COL_" + colName);
			if (filterValue != null)
			{
				System.out.println("value for colName " + colName + ": " + filterValue);
				String filterOperator = request.getString("FILTER_OPERATOR_COL_" + colName);
				if (filterOperator.equals("GT"))
				{
					QueryRule q = new QueryRule(colName, Operator.GREATER, filterValue);
					subMatrix = subMatrix.getSubMatrixFilterByColMatrixValues(q);
					this.model.setSubMatrix(subMatrix);
				}
				else if (filterOperator.equals("GE"))
				{
					// etc
					//TODO
				}
			}
		}
		
		List<String> rowNames = subMatrix.getRowNames();
		for (String rowName : rowNames)
		{
			Integer filterValue = request.getInt("FILTER_VALUE_ROW_" + rowName);
			if (filterValue != null)
			{
				System.out.println("value for rowName " + rowName + ": " + filterValue);
				String filterOperator = request.getString("FILTER_OPERATOR_ROW_" + rowName);
				if (filterOperator.equals("GT"))
				{
					QueryRule q = new QueryRule(rowName, Operator.GREATER, filterValue);
					subMatrix = subMatrix.getSubMatrixFilterByRowMatrixValues(q);
					this.model.setSubMatrix(subMatrix);
				}
				else if (filterOperator.equals("GE"))
				{
					// etc
					//TODO
				}
			}
		}
		
		//TODO: Okay??
		model.setWidth(this.model.getSubMatrix().getNumberOfCols());
		model.setHeight(this.model.getSubMatrix().getNumberOfRows());
	}

}
