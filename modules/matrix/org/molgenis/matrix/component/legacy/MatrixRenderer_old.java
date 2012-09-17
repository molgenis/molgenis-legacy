//package org.molgenis.matrix.component.legacy;
///**
// * File: TextInput.java <br>
// * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
// * Changelog:
// * <ul>
// * <li>2006-03-08, 1.0.0, DI Matthijssen; Creation
// * <li>2006-05-14, 1.1.0, MA Swertz; Refectoring into Invengine.
// * </ul>
// * TODO look at the depreciated functions.
// */
//
//package org.molgenis.matrix.component;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.TreeMap;
//
//import org.molgenis.framework.db.DatabaseException;
//import org.molgenis.framework.db.QueryRule;
//import org.molgenis.framework.db.QueryRule.Operator;
//import org.molgenis.framework.ui.FreemarkerView;
//import org.molgenis.framework.ui.html.HtmlWidget;
//import org.molgenis.matrix.component.interfaces.BasicMatrix;
//import org.molgenis.matrix.component.interfaces.RenderableMatrix;
//import org.molgenis.matrix.component.interfaces.SliceableMatrix;
//import org.molgenis.matrix.component.interfaces.SourceMatrix;
//import org.molgenis.util.Tuple;
//
//public class MatrixRenderer_old extends HtmlWidget
//{
//	
//	public static final String MATRIX_COMPONENT_REQUEST_PREFIX = "matrix_component_request_prefix_";
//
//	/**
//	 * Supposed to contain ONLY navigational logic to move around in the rendered matrix.
//	 */
//	private RendererModel model = new RendererModel();
//
//	public RendererModel getModel()
//	{
//		return model;
//	}
//	
//	public MatrixRenderer_old(String name, SliceableMatrix dataSource, SourceMatrix matrix) throws Exception
//	{
//		this(name, null, dataSource, matrix);
//	}
//
//	/**
//	 * Instantiation only
//	 * @param name
//	 * @param value
//	 * @param matrix
//	 * @throws Exception 
//	 */
//	public MatrixRenderer_old(String name, String label, SliceableMatrix dataSource, SourceMatrix matrix) throws Exception
//	{
//		super(name, label);
//		
//		System.out.println("matrix.getTotalNumberOfCols() = " + matrix.getTotalNumberOfCols());
//		System.out.println("matrix.getTotalNumberOfRows() = " + matrix.getTotalNumberOfRows());
//		
//		// create instance of complete matrix and run checks
//	//	model.setInstance(matrix);
//		model.setDataSource(dataSource);
//		model.setColMax(matrix.getTotalNumberOfCols());
//		model.setRowMax(matrix.getTotalNumberOfRows());
//
//		runChecks();
//
//		// set defaults for first view in the 'internal' model for browsing/filtering a matrix
//		model.setStepSize(5);
//		model.setHeight(10);
//		model.setWidth(5);
//		model.setColStart(0);
//		model.setRowStart(0);
//		model.setColStop(model.getColMax() < model.getWidth() ? model.getColMax() : model.getWidth());
//		model.setRowStop(model.getRowMax() < model.getHeight() ? model.getRowMax() : model.getHeight());
//
//		// set first submatrix using the defaults
//		updateSubmatrix();
//		
//	}
//	
//	/**
//	 * Query the backend with the new coordinates and set submatrix to be displayed
//	 * @throws Exception
//	 */
//	private void updateSubmatrix() throws Exception
//	{
//		// helper vars
//		int nRows = model.getRowStop() - model.getRowStart();
//		int nCols = model.getColStop() - model.getColStart();
//
//		// create and set the submatrix
//		BasicMatrix subMatrix = model.getDataSource().getSubMatrixByOffset(model.getRowStart(),nRows, model.getColStart(), nCols);
//		//verifyRenderableMatrix(subMatrix);
//		//model.setSubMatrix(subMatrix);
//	}
//	
//	private void verifyRenderableMatrix(RenderableMatrix rm) throws Exception{
//		if(rm.getConstraintLogic() == null){
//			throw new Exception("Verify renderable matrix: constraint logic is null");
//		}
//		if(rm.getColIndex() > rm.getFilteredNumberOfCols()){
//			throw new Exception("Verify renderable matrix: col index exceeds filtered number of cols");
//		}
//		if(rm.getRowIndex() > rm.getFilteredNumberOfRows()){
//			throw new Exception("Verify renderable matrix: row index exceeds filtered number of rows");
//		}
//		if(rm.getFilteredNumberOfCols() > rm.getTotalNumberOfCols()){
//			throw new Exception("Verify renderable matrix: filtered number of cols exceeds total amount of cols ("+rm.getFilteredNumberOfCols()+" > "+rm.getTotalNumberOfCols()+")");
//		}
//		if(rm.getFilteredNumberOfRows() > rm.getTotalNumberOfRows()){
//			throw new Exception("Verify renderable matrix: filtered number of rows exceeds total amount of rows ("+rm.getFilteredNumberOfRows()+" > "+rm.getTotalNumberOfRows()+")");
//		}
//		if(rm.getVisibleCols().size() == 0){
//			throw new Exception("Verify renderable matrix: no visible columns");
//		}
//		if(rm.getVisibleRows().size() == 0){
//			throw new Exception("Verify renderable matrix: no visible rows");
//		}
//		if(rm.getVisibleValues().length == 0){
//			throw new Exception("Verify renderable matrix: no visible row values");
//		}
//		if(rm.getVisibleValues()[0].length == 0){
//			throw new Exception("Verify renderable matrix: no visible column values");
//		}
//		if(rm.getFilters() == null){
//			throw new Exception("Verify renderable matrix: filter list not instantiated");
//		}
//		if(rm.getColIndex() < 0){
//			throw new Exception("Verify renderable matrix: negative columns index");
//		}
//		if(rm.getRowIndex() < 0){
//			throw new Exception("Verify renderable matrix: negative row index");
//		}
//	}
//	
//	/**
//	 * Helper. Can be extended with more checks if needed.
//	 * @throws DatabaseException
//	 */
//	private void runChecks() throws DatabaseException
//	{
//		if (model.getColMax() < 1)
//		{
//			throw new DatabaseException("Datamatrix has no columns.");
//		}
//		if (model.getRowMax() < 1)
//		{
//			throw new DatabaseException("Datamatrix has no rows.");
//		}
//	}
//
//	public String toHtml()
//	{
//		Map<String, Object> parameters = new TreeMap<String, Object>();
//		parameters.put("name", this.getName());
//		parameters.put("value", this.getObject());
//		parameters.put("matrix", this.getModel().getSubMatrix());
//		parameters.put("operators", this.operators());
//		parameters.put("req_tag", MATRIX_COMPONENT_REQUEST_PREFIX);
//
//		// delegate to freemarker
//		return new FreemarkerView(
//				"org/molgenis/matrix/component/MatrixRenderer.ftl",
//				parameters).render();
//	}
//	
//	private HashMap<String, String> operators(){
//		HashMap<String, String> ops = new HashMap<String, String>();
//		
//		ops.put("GREATER", "&gt;");
//		ops.put("GREATER_EQUAL", "&gt;=");
//		ops.put("LESS", "&lt;");
//		ops.put("LESS_EQUAL", "&lt;=");
//		ops.put("EQUALS", "==");
//		ops.put("SORTASC", "sort asc");
//		ops.put("SORTDESC", "sort desc");
//		
//		
//		return ops;
//	}
//	
//	public void delegateHandleRequest(Tuple request) throws Exception {
//		
//		int stepSize = request.getInt(MATRIX_COMPONENT_REQUEST_PREFIX+"stepSize");
//		int width = request.getInt(MATRIX_COMPONENT_REQUEST_PREFIX+"width");
//		int height = request.getInt(MATRIX_COMPONENT_REQUEST_PREFIX+"height");
//		
//		stepSize = stepSize < 1 ? 1 : stepSize;
//		width = width < 1 ? 1 : width;
//		height = height < 1 ? 1 : height;
//
//		this.model.setStepSize(stepSize);
//		this.model.setWidth(width);
//		this.model.setHeight(height);
//		
//		String action = request.getString("__action");
//		
//		if(!action.startsWith(MATRIX_COMPONENT_REQUEST_PREFIX)){
//			throw new Exception("Action '"+action+"' does not include the matrix renderer prefix '"+MATRIX_COMPONENT_REQUEST_PREFIX+"'for request delegation.");
//		}
//		
//		action = action.substring(MATRIX_COMPONENT_REQUEST_PREFIX.length(), action.length());
//		
//		if (action.equals("moveRight")) {
//			this.moveRight();
//		}
//		else if (action.equals("moveLeft")) {
//			this.moveLeft();
//		}
//		else if (action.equals("moveDown")) {
//			this.moveDown();
//		}
//		else if (action.equals("moveUp")) {
//			this.moveUp();
//		}
//		else if (action.equals("moveFarRight")) {
//			this.moveFarRight();
//		}
//		else if (action.equals("moveFarLeft")) {
//			this.moveFarLeft();
//		}
//		else if (action.equals("moveFarDown")) {
//			this.moveFarDown();
//		}
//		else if (action.equals("moveFarUp")) {
//			this.moveFarUp();
//		}
//		else if (action.equals("changeSubmatrixSize")) {
//			this.update();
//		}
//		else if (action.startsWith("filter")) {
//			this.applyFilters(request);
//		}
//		else{
//			throw new Exception("Action '"+action+"' unknown.");
//		}
//		
//		
//	}
//	
//	
//	private void moveActionFollowup() throws Exception
//	{
//		verifyColStart();
//		verifyRowStart();
//		determineColStop();
//		determineRowStop();
//		updateSubmatrix();
//	}
//
//	/**
//	 * On vertical move actions at the right edge of the matrix combined with a
//	 * width setting increase past the edge, the horizontal window would move
//	 * out of range. The colStop is verified, but this is based on colStart
//	 * which can be wrong at that point. Same goes for horizontal move actions
//	 * under inverse conditions. This function compensates.
//	 */
//	private void verifyColStart()
//	{
//		if (model.getColStart() + model.getWidth() > model.getColMax())
//		{
//			if (model.getWidth() > model.getColMax())
//			{
//				model.setColStart(0);
//			}
//			else
//			{
//				model.setColStart(model.getColMax() - model.getWidth());
//			}
//		}
//	}
//
//	/**
//	 * On horizontal move actions at the bottom edge of the matrix combined with a
//	 * height setting increase past the edge, the vertical window would move
//	 * out of range. The rowStop is verified, but this is based on rowStart
//	 * which can be wrong at that point. Same goes for vertical move actions
//	 * under inverse conditions. This function compensates.
//	 */
//	private void verifyRowStart()
//	{
//		if (model.getRowStart() + model.getHeight() > model.getRowMax())
//		{
//			if (model.getHeight() > model.getRowMax())
//			{
//				model.setRowStart(0);
//			}
//			else
//			{
//				model.setRowStart(model.getRowMax() - model.getHeight());
//			}
//		}
//	}
//
//	/**
//	 * Determine value for column stop. If width is smaller than maximum column
//	 * value, the stop position is the current column position plus the width.
//	 * If the maximum column value is equal ('perfect fit') or smaller ('window
//	 * cut-off') than the width, the stop position is the maximum possible value
//	 * for column instead.
//	 */
//	private void determineColStop()
//	{
//		if (model.getWidth() < model.getColMax())
//		{
//			model.setColStop(model.getColStart() + model.getWidth());
//		}
//		else
//		{
//			model.setColStop(model.getColMax());
//		}
//	}
//
//	/**
//	 * Determine value for row stop. If height is smaller than maximum row
//	 * value, the stop position is the current row position plus the height.
//	 * If the maximum row value is equal ('perfect fit') or smaller ('window
//	 * cut-off') than the height, the stop position is the maximum possible value
//	 * for row instead.
//	 */
//	private void determineRowStop()
//	{
//		if (model.getHeight() < model.getRowMax())
//		{
//			model.setRowStop(model.getRowStart() + model.getHeight());
//		}
//		else
//		{
//			model.setRowStop(model.getRowMax());
//		}
//	}
//
//	/**
//	 * Moves viewed sub matrix to the right. The step size is added to the
//	 * current column position. If this new column position exceeds the maximum
//	 * column value minus the width ('window size'), it is set to this maximum
//	 * 'window size' value instead. If the width is greater than the maximum
//	 * possible column value, the matrix fits inside the 'viewing window' and
//	 * the column position is set to zero.
//	 */
//	public void moveRight() throws Exception
//	{
//		if (model.getWidth() < model.getColMax())
//		{
//			model.setColStart(model.getColStart() + model.getStepSize() > model.getColMax() - model.getWidth() ? model
//					.getColMax()
//					- model.getWidth() : model.getColStart() + model.getStepSize());
//		}
//		else
//		{
//			model.setColStart(0);
//		}
//		moveActionFollowup();
//	}
//
//	/**
//	 * Moves viewed sub matrix to the left. The step size is subtracted from the
//	 * current column position. If this new column position is less than 0, it
//	 * is set to 0 instead.
//	 */
//	public void moveLeft() throws Exception
//	{
//		model
//				.setColStart(model.getColStart() - model.getStepSize() < 0 ? 0 : model.getColStart()
//						- model.getStepSize());
//		moveActionFollowup();
//	}
//
//	public void moveDown() throws Exception
//	{
//		if (model.getHeight() < model.getRowMax())
//		{
//			model.setRowStart(model.getRowStart() + model.getStepSize() > model.getRowMax() - model.getHeight() ? model
//					.getRowMax()
//					- model.getHeight() : model.getRowStart() + model.getStepSize());
//		}
//		else
//		{
//			model.setRowStart(0);
//		}
//		moveActionFollowup();
//	}
//
//	/**
//	 * Moves viewed sub matrix upwards. The step size is subtracted from the
//	 * current row position. If this new row position is less than 0, it
//	 * is set to 0 instead.
//	 */
//	public void moveUp() throws Exception
//	{
//		model
//				.setRowStart(model.getRowStart() - model.getStepSize() < 0 ? 0 : model.getRowStart()
//						- model.getStepSize());
//		moveActionFollowup();
//	}
//
//	public void moveFarRight() throws Exception
//	{
//		int colStart = model.getColMax() - model.getWidth();
//		model.setColStart(colStart < 0 ? 0 : colStart);
//		moveActionFollowup();
//	}
//
//	public void moveFarLeft() throws Exception
//	{
//		model.setColStart(0);
//		moveActionFollowup();
//	}
//
//	public void moveFarDown() throws Exception
//	{
//		int rowStart = model.getRowMax() - model.getHeight();
//		model.setRowStart(rowStart < 0 ? 0 : rowStart);
//		moveActionFollowup();
//	}
//
//	public void moveFarUp() throws Exception
//	{
//		model.setRowStart(0);
//		moveActionFollowup();
//	}
//
//	/**
//	 * Update can be used after setting new width, height or stepsize. It
//	 * behaves like a move action, except there is no movement. This will update
//	 * the submatrix to the new dimensions.
//	 * 
//	 * @throws Exception
//	 */
//	public void update() throws Exception
//	{
//		moveActionFollowup();
//	}
//
//	/**
//	 * Apply filters to values in the matrix to either
//	 * the whole matrix or current visible matrix.
//	 * 
//	 * @param request
//	 * @throws Exception
//	 */
//	public void applyFilters(Tuple request) throws Exception
//	{
//		
//		RenderableMatrix filterMatrix = null;
//		
//		String filterSelectionType = request.getString("FILTER_SELECTION_TYPE");
//		
//		if(filterSelectionType.equals("vis")){
//			// get the current submatrix (view)
//			filterMatrix = this.getModel().getSubMatrix();
//		}
//		else if(filterSelectionType.equals("evr")){
//			// get the original complete matrix
//			filterMatrix = this.getModel().getInstance();
//		}else{
//			throw new UnsupportedOperationException("FILTER NOT POSSIBLE YET");
//			//unrecognized filter?
//		}
//		
//		//get filters that were appliced to 'column values'
//		for(int col = 0; col < filterMatrix.getVisibleCols().size(); col++){
//			Object filterValue = request.getObject("FILTER_VALUE_COL_" + col);
//			if (filterValue != null)
//			{
//				
//				String filterOperator = request.getString("FILTER_OPERATOR_COL_" + col);
//				System.out.println("col value filter: col = " + col + ", op = " + filterOperator + ", val = " + filterValue);
//				QueryRule q = new QueryRule(String.valueOf(col), Operator.valueOf(filterOperator), filterValue);
//				filterMatrix = model.getDataSource().getSubMatrixByColValueFilter(filterMatrix, q);
//				this.model.setSubMatrix(filterMatrix);	
//			}
//		}
//		
//		//get filters that were appliced to 'row values'
//		for(int row = 0; row < filterMatrix.getVisibleRows().size(); row++){
//			Object filterValue = request.getObject("FILTER_VALUE_ROW_" + row);
//			if (filterValue != null)
//			{
//				String filterOperator = request.getString("FILTER_OPERATOR_ROW_" + row);
//				System.out.println("row value filter: row = " + row + ", op = " + filterOperator + ", val = " + filterValue);
//				QueryRule q = new QueryRule(String.valueOf(row), Operator.valueOf(filterOperator), filterValue);
//				filterMatrix = model.getDataSource().getSubMatrixByRowValueFilter(filterMatrix, q);
//				this.model.setSubMatrix(filterMatrix);	
//			}
//		}
//		
//		//get filters that were appliced to col headers
//		Object filterValue = request.getObject("FILTER_VALUE_COL_HEADER");
//		if (filterValue != null)
//		{
//			String filterOperator = request.getString("FILTER_OPERATOR_COL_HEADER");
//			String filterAttribute = request.getString("FILTER_ATTRIBUTE_COL_HEADER");
//			System.out.println("col header filter: attr = " + filterAttribute + ", op = " + filterOperator + ", val = " + filterValue);
//			QueryRule q = new QueryRule(filterAttribute, Operator.valueOf(filterOperator), filterValue);
//			filterMatrix = model.getDataSource().getSubMatrixByColHeaderFilter(filterMatrix, q);
//			this.model.setSubMatrix(filterMatrix);	
//		}
//		
//		//get filters that were appliced to row headers
//		filterValue = request.getObject("FILTER_VALUE_ROW_HEADER");
//		if (filterValue != null)
//		{
//			String filterOperator = request.getString("FILTER_OPERATOR_ROW_HEADER");
//			String filterAttribute = request.getString("FILTER_ATTRIBUTE_ROW_HEADER");
//			System.out.println("row header filter: attr = " + filterAttribute + ", op = " + filterOperator + ", val = " + filterValue);
//			QueryRule q = new QueryRule(filterAttribute, Operator.valueOf(filterOperator), filterValue);
//			filterMatrix = model.getDataSource().getSubMatrixByRowHeaderFilter(filterMatrix, q);
//			this.model.setSubMatrix(filterMatrix);	
//		}
//		
//		//model.setWidth(this.model.getSubMatrix().getFilteredNumberOfCols());
//		//model.setHeight(this.model.getSubMatrix().getFilteredNumberOfRows());
//		
//		verifyRenderableMatrix(this.model.getSubMatrix());
//		
//		verifyColStart();
//		verifyRowStart();
//		determineColStop();
//		determineRowStop();
//	}
//}
