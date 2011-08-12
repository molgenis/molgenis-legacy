/**
 * File: TextInput.java <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-08, 1.0.0, DI Matthijssen; Creation
 * <li>2006-05-14, 1.1.0, MA Swertz; Refectoring into Invengine.
 * </ul>
 * TODO look at the depreciated functions.
 */

package org.molgenis.matrix.component;

import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.util.Tuple;

public class MatrixRenderer extends HtmlWidget
{
	
	/**
	 * Supposed to contain ONLY navigational logic to move around in the rendered matrix.
	 */
	private RendererModel model = new RendererModel();

	public RendererModel getModel()
	{
		return model;
	}
	
	public MatrixRenderer(String name, RenderableMatrix matrix, SliceableMatrix dataSource) throws Exception
	{
		this(name, null, matrix, dataSource);
	}

	/**
	 * Instantiation only
	 * @param name
	 * @param value
	 * @param matrix
	 * @throws Exception 
	 */
	public MatrixRenderer(String name, String label, RenderableMatrix matrix, SliceableMatrix dataSource) throws Exception
	{
		super(name, label);
		
		// create instance of complete matrix and run checks
		model.setInstance(matrix);
		model.setDataSource(dataSource);
		model.setColMax(matrix.getTotalNumberOfCols());
		model.setRowMax(matrix.getTotalNumberOfRows());

		runChecks();

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
	
	/**
	 * Query the backend with the new coordinates and set submatrix to be displayed
	 * @throws Exception
	 */
	private void updateSubmatrix() throws Exception
	{
		// helper vars
		int nRows = model.getRowStop() - model.getRowStart();
		int nCols = model.getColStop() - model.getColStart();

		// create and set the submatrix
		RenderableMatrix subMatrix = model.getDataSource().getSubMatrixByOffset(model.getInstance(), model.getRowStart(),
				nRows, model.getColStart(), nCols);
		verifyRenderableMatrix(subMatrix);
		model.setSubMatrix(subMatrix);
	}
	
	private void verifyRenderableMatrix(RenderableMatrix rm) throws Exception{
		if(rm.getConstraintLogic() == null){
			throw new Exception("Verify renderable matrix: constraint logic is null");
		}
		if(rm.getColIndex() > rm.getFilteredNumberOfCols()){
			throw new Exception("Verify renderable matrix: col index exceeds filtered number of cols");
		}
		if(rm.getRowIndex() > rm.getFilteredNumberOfRows()){
			throw new Exception("Verify renderable matrix: row index exceeds filtered number of rows");
		}
		if(rm.getFilteredNumberOfCols() > rm.getTotalNumberOfCols()){
			throw new Exception("Verify renderable matrix: filtered number of cols exceeds total amount of cols");
		}
		if(rm.getFilteredNumberOfRows() > rm.getTotalNumberOfRows()){
			throw new Exception("Verify renderable matrix: filtered number of rows exceeds total amount of rows");
		}
//		if(rm.getScreenName() == null){
//			throw new Exception("Verify renderable matrix: screen name is null");
//		}
		if(rm.getVisibleCols().size() == 0){
			throw new Exception("Verify renderable matrix: no visible columns");
		}
		if(rm.getVisibleRows().size() == 0){
			throw new Exception("Verify renderable matrix: no visible rows");
		}
		if(rm.getVisibleValues().length == 0){
			throw new Exception("Verify renderable matrix: no visible row values");
		}
		if(rm.getVisibleValues()[0].length == 0){
			throw new Exception("Verify renderable matrix: no visible column values");
		}
		if(rm.getFilters() == null){
			throw new Exception("Verify renderable matrix: filter list not instantiated");
		}
	}
	
	/**
	 * Helper. Can be extended with more checks if needed.
	 * @throws DatabaseException
	 */
	private void runChecks() throws DatabaseException
	{
		if (model.getColMax() < 1)
		{
			throw new DatabaseException("Datamatrix has no columns.");
		}
		if (model.getRowMax() < 1)
		{
			throw new DatabaseException("Datamatrix has no rows.");
		}
	}

	public String toHtml()
	{
		Map<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("name", this.getName());
		parameters.put("value", this.getObject());
		parameters.put("matrix", this.getModel().getSubMatrix());

		// delegate to freemarker
		return new FreemarkerView(
				"org/molgenis/matrix/component/MatrixRenderer.ftl",
				parameters).render();
	}
	
	public void delegateHandleRequest(Tuple request, RenderableMatrix matrix) {
		// TODO
	}
}
