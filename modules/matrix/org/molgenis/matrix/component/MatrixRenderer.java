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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.matrix.component.general.Filter;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.general.MatrixRendererHelper;
import org.molgenis.matrix.component.general.RenderableMatrixImpl;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;
import org.molgenis.util.Tuple;

public class MatrixRenderer<R, C, V> extends HtmlWidget
{

	private SliceableMatrix<R, C, V> sliceable;
	private RenderableMatrix<R, C, V> renderMe;
	private SourceMatrix<R, C, V> source;
	private int rowStartIndex;
	private int colStartIndex;
	private String screenName;
	
	public MatrixRenderer(String name, SliceableMatrix<R, C, V> sliceable, SourceMatrix<R, C, V> source, String screenName)
			throws Exception
	{
		this(name, name, sliceable, source, null, null, -1, screenName);
	}

	public RenderableMatrix<R, C, V> getRendered() {
		return this.renderMe;
	}
	
	/**
	 * Instantiation only
	 */
	public MatrixRenderer(String name, String label, SliceableMatrix<R, C, V> sliceable, SourceMatrix<R, C, V> source,
			List<Filter> filters, String constraintLogic, int stepSize, String screenName) throws Exception
	{
		super(name, label);

		// save source and sliceable
		this.source = source;
		this.sliceable = sliceable;
		this.screenName = screenName;

		// set default index filters if no filters are specified
		if (filters == null)
		{
			// use -1 to compensate for totals to indices
			int rowStop = MatrixRendererHelper.ROW_STOP_DEFAULT > source.getTotalNumberOfRows() - 1 ? source
					.getTotalNumberOfRows() - 1 : MatrixRendererHelper.ROW_STOP_DEFAULT;
			int colStop = MatrixRendererHelper.COL_STOP_DEFAULT > source.getTotalNumberOfCols() - 1 ? source
					.getTotalNumberOfCols() - 1 : MatrixRendererHelper.COL_STOP_DEFAULT;

			filters = new ArrayList<Filter>();
			filters.add(new Filter(Filter.Type.paging, new MatrixQueryRule("row", Operator.LIMIT, rowStop)));
			filters.add(new Filter(Filter.Type.paging, new MatrixQueryRule("col", Operator.LIMIT, colStop)));
		}

		applyFiltersAndRender(sliceable, filters);


	}

	private void applyFiltersAndRender(SliceableMatrix<R, C, V> sliceable, List<Filter> filters) throws Exception
	{
		System.out.println("applyFiltersAndRender");
		
		//refresh sliceable
		sliceable.createFresh();
		
		System.out.println("sliceable: colsize: " + sliceable.getResult().getVisibleCols().size());
		System.out.println("sliceable: rowsize: " + sliceable.getResult().getVisibleRows().size());
		
		// apply filters in order
		for (Filter f : filters)
		{
			System.out.println("iterating over: " + f.toString());
			
			switch (f.getFilterType())
			{
				case index:
					sliceable.sliceByIndex(f.getQueryRule());
					break;
				case paging:
					sliceable.sliceByPaging(f.getQueryRule());
					break;
				case rowHeader:
					sliceable.sliceByRowHeader(f.getQueryRule());
					break;
				case colHeader:
					sliceable.sliceByColHeader(f.getQueryRule());
					break;
				case rowValues:
					sliceable.sliceByRowValues(f.getQueryRule());
					break;
				case colValues:
					sliceable.sliceByColValues(f.getQueryRule());
					break;
			}
		}
		
		BasicMatrix<R, C, V> basic = sliceable.getResult();
		
		System.out.println("result 'basic': colsize: " + basic.getVisibleCols().size());
		System.out.println("result 'basic': rowsize: " + basic.getVisibleRows().size());
		
		renderMe = new RenderableMatrixImpl<R, C, V>(source, basic, filters, "", 5, screenName);
	}

	public String toHtml()
	{
		Map<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("name", this.getName());
		parameters.put("value", this.getObject());
		parameters.put("matrix", this.renderMe);
		parameters.put("operators", MatrixRendererHelper.operators());
		parameters.put("req_tag", MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX);

		// delegate to freemarker
		return new FreemarkerView("org/molgenis/matrix/component/MatrixRenderer.ftl", parameters).render();
	}

	public void delegateHandleRequest(Tuple request) throws Exception
	{

		String action = request.getString("__action");

		if (!action.startsWith(MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX))
		{
			throw new Exception("Action '" + action + "' does not include the matrix renderer prefix '"
					+ MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX + "'for request delegation.");
		}

		action = action.substring(MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX.length(), action.length());

		if (action.equals("moveRight")) this.moveRight();
		if (action.equals("moveLeft")) this.moveLeft();
		if (action.equals("moveDown")) this.moveDown();
		if (action.equals("moveUp")) this.moveUp();
		if (action.startsWith("filter")) this.addFilter(request);
		
		orderFilters();
		applyFiltersAndRender(sliceable, renderMe.getFilters());
	}
	
	/**
	 * PROOF OF PRINCIPLE! Needs rework.
	 * 
	 * @param request
	 * @throws Exception 
	 */
	private void addFilter(Tuple request) throws Exception {
		
		MatrixQueryRule q = null;
		
		for (int col = 0; col < renderMe.getVisibleCols().size(); col++){
			Object filterValue = request.getObject("FILTER_VALUE_COL_" + col);
			if (filterValue != null) {
				String filterOperator = request.getString("FILTER_OPERATOR_COL_" + col);
				q = new MatrixQueryRule(String.valueOf(col), Operator.valueOf(filterOperator), filterValue);
			}
		}
		
		if (q != null) {
			Filter newFilter = new Filter(Filter.Type.colValues, q);
			this.renderMe.getFilters().add(newFilter);
		}
	}

	/**
	 * Move all LIMIT filters to the back of the filter queue!
	 * Lame??
	 */
	private void orderFilters(){
		List<Filter> limitFilters = new ArrayList<Filter>();
		List<Filter> otherFilters = new ArrayList<Filter>();
		for(int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++){
			Filter f = renderMe.getFilters().get(filterIndex);
			if(f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getOperator().equals(Operator.LIMIT))
			{
				limitFilters.add(f);
			}else{
				otherFilters.add(f);
			}
		}
		renderMe.getFilters().clear();
		renderMe.getFilters().addAll(otherFilters);
		renderMe.getFilters().addAll(limitFilters);
	}

	private void moveRight() throws Exception
	{
		System.out.println("moveRight()");
		//TODO: needs more logic
		boolean offsetFilterFound = false;
		for(int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++){
			Filter f = renderMe.getFilters().get(filterIndex);
			if(f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("col") && f.getQueryRule().getOperator().equals(Operator.OFFSET)){
				f.getQueryRule().setValue((Integer)f.getQueryRule().getValue() + renderMe.getStepSize());
				offsetFilterFound = true;
			}
		}
		if(!offsetFilterFound){
			Filter offset = new Filter(Filter.Type.paging, new MatrixQueryRule("col", Operator.OFFSET, renderMe.getStepSize()));
			renderMe.getFilters().add(offset);
		}
		
		this.colStartIndex = colStartIndex + renderMe.getStepSize();
		
	}
	
	private void moveLeft() throws Exception
	{
		System.out.println("moveLeft()");
		//TODO: needs more logic!!!
		for(int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++){
			Filter f = renderMe.getFilters().get(filterIndex);
			if(f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("col") && f.getQueryRule().getOperator().equals(Operator.OFFSET)){
				f.getQueryRule().setValue((Integer)f.getQueryRule().getValue() - renderMe.getStepSize());
			}
		}
		this.colStartIndex = colStartIndex - renderMe.getStepSize();
	}
	
	private void moveDown() throws Exception
	{
		System.out.println("moveDown()");
		//TODO: needs more logic
		boolean offsetFilterFound = false;
		for(int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++){
			Filter f = renderMe.getFilters().get(filterIndex);
			if(f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("row") && f.getQueryRule().getOperator().equals(Operator.OFFSET)){
				f.getQueryRule().setValue((Integer)f.getQueryRule().getValue() + renderMe.getStepSize());
				offsetFilterFound = true;
			}
		}
		if(!offsetFilterFound){
			Filter offset = new Filter(Filter.Type.paging, new MatrixQueryRule("row", Operator.OFFSET, renderMe.getStepSize()));
			renderMe.getFilters().add(offset);
		}
		
		this.rowStartIndex = rowStartIndex + renderMe.getStepSize();
		
	}
	
	private void moveUp() throws Exception
	{
		System.out.println("moveUp()");
		//TODO: needs more logic!!!
		for(int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++){
			Filter f = renderMe.getFilters().get(filterIndex);
			if(f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getField().equals("row") && f.getQueryRule().getOperator().equals(Operator.OFFSET)){
				f.getQueryRule().setValue((Integer)f.getQueryRule().getValue() - renderMe.getStepSize());
			}
		}
		this.rowStartIndex = rowStartIndex - renderMe.getStepSize();
	}

}
