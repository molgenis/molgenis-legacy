package org.molgenis.matrix.ui.manager;

import java.util.List;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.FilterableMatrix;
import org.molgenis.matrix.Matrix;
import org.molgenis.matrix.MatrixException;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
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

	public Browser(Matrix instance) throws Exception
	{
		// create instance of complete matrix and run checks
		model.setInstance(instance);
		// System.out.println("*** Browser instance: " +
		// instance.getNumberOfCols() + ", rows: " + instance.getNumberOfRows()
		// + ", first element: " + instance.getElement(0, 0));
		model.setColMax(instance.getColCount());
		model.setRowMax(instance.getRowCount());

		// System.out.println("*** matrix tostring:");
		// System.out.println(instance.toString());

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

	private void updateSubmatrix() throws Exception
	{
		// helper vars
		int nRows = model.getRowStop() - model.getRowStart();
		int nCols = model.getColStop() - model.getColStart();

		// create and set submatrix
		Matrix<ObservationTarget, ObservableFeature, ObservedValue> subMatrix = model.getInstance().getSubMatrixByOffset(model.getRowStart(),
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
	public void applyFilters(Tuple request) throws Exception
	{
		if (!(this.getModel().getInstance() instanceof FilterableMatrix)) {
			throw new MatrixException("Error: matrix not filterable");
		}
		
		FilterableMatrix filterMatrix = null;
		
		if(request.getString("__action").equals("filterVisible")){
			// get the current submatrix (view)
			filterMatrix = (FilterableMatrix) this.getModel().getSubMatrix();
		}
		else if(request.getString("__action").equals("filterAll")){
			// get the original complete matrix
			filterMatrix = (FilterableMatrix) this.getModel().getInstance();
		}else{
			//unrecognized filter?
		}
		
		List<ObservableFeature> cols = filterMatrix.getColNames();
		for (ObservableFeature col : cols)
		{
			String colName = col.getName();
			Object filterValue = request.getObject("FILTER_VALUE_COL_" + colName);
			if (filterValue != null)
			{
				System.out.println("value for colName " + colName + ": " + filterValue);
				String filterOperator = request.getString("FILTER_OPERATOR_COL_" + colName);
				QueryRule q = new QueryRule(colName, Operator.valueOf(filterOperator), filterValue);
				
				filterMatrix = filterMatrix.getSubMatrixFilterByColMatrixValues(q);
				this.model.setSubMatrix(filterMatrix);	
			}
		}
		
		List<ObservationTarget> rows = filterMatrix.getRowNames();
		for (ObservationTarget row : rows)
		{
			String rowName = row.getName();
			Object filterValue = request.getObject("FILTER_VALUE_ROW_" + rowName);
			if (filterValue != null)
			{
				System.out.println("value for rowName " + rowName + ": " + filterValue);
				String filterOperator = request.getString("FILTER_OPERATOR_ROW_" + rowName);
				QueryRule q = new QueryRule(rowName, Operator.valueOf(filterOperator), filterValue);
				
				filterMatrix = filterMatrix.getSubMatrixFilterByRowMatrixValues(q);
				this.model.setSubMatrix(filterMatrix);
			}
		}
		
		model.setWidth(this.model.getSubMatrix().getColCount());
		model.setHeight(this.model.getSubMatrix().getRowCount());
		
		verifyColStart();
		verifyRowStart();
		determineColStop();
		determineRowStop();
	}

}
