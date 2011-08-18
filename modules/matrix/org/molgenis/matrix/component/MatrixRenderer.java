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
			// when "" is omitted: java.lang.IllegalArgumentException:
			// QueryRule(): Operator.<= cannot be used with one argument
			filters.add(new Filter(Filter.Type.rowIndex, new QueryRule("", Operator.LESS_EQUAL, rowStop)));
			filters.add(new Filter(Filter.Type.colIndex, new QueryRule("", Operator.LESS_EQUAL, colStop)));
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
			switch (f.getFilterType())
			{
				case rowIndex:
					sliceable.sliceByRowIndex(f.getQueryRule());
					break;
				case colIndex:
					sliceable.sliceByColIndex(f.getQueryRule());
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
		parameters.put("rowStartIndex", this.rowStartIndex);
		parameters.put("colStartIndex", this.colStartIndex);
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


		
		if (action.equals("moveRight"))
		{
			this.moveRight();
		}
		
		applyFiltersAndRender(sliceable, renderMe.getFilters());
	}

	private void moveRight() throws Exception
	{
		System.out.println("moveRight()");
		
		int start = -1;
		
		//remove existing colindex filters
		//and find out the start index..
		//TODO: correct behaviour in combination with other filters??
		for(int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++){
			Filter f = renderMe.getFilters().get(filterIndex);
			if(f.getFilterType().equals(Filter.Type.colIndex)){
				if(f.getQueryRule().getOperator().equals(Operator.EQUALS) || f.getQueryRule().getOperator().equals(Operator.GREATER) || f.getQueryRule().getOperator().equals(Operator.GREATER_EQUAL)){
					if(start != -1){
						throw new Exception("Multiple filters altering column start index");
					}
					start = (Integer) f.getQueryRule().getValue();
				}
				renderMe.getFilters().remove(filterIndex);
			}
		}
		
		//add new filters
		//TODO: where?? old position??
		//TODO: logic
		start = start != -1 ? start + renderMe.getStepSize() : renderMe.getStepSize();
		int stop = (renderMe.getVisibleCols().size()+start);

		System.out.println("new start: " + start);
		System.out.println("new stop: " + stop);

		this.colStartIndex = start;
		
		renderMe.getFilters().add(new Filter(Filter.Type.colIndex, new QueryRule("", Operator.LESS_EQUAL, stop)));
		renderMe.getFilters().add(new Filter(Filter.Type.colIndex, new QueryRule("", Operator.GREATER_EQUAL, start)));
		
	}

}
