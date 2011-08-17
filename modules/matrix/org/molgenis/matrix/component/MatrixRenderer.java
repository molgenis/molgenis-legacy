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

public class MatrixRenderer<R, C, V> extends HtmlWidget
{

	private SourceMatrix<R, C, V> source;
	private RenderableMatrix<R, C, V> renderMe;

	public MatrixRenderer(String name, SliceableMatrix<R, C, V> sliceable, SourceMatrix<R, C, V> source) throws Exception
	{
		this(name, name, sliceable, source, null, null, -1);
	}

	/**
	 * Instantiation only
	 */
	public MatrixRenderer(String name, String label, SliceableMatrix<R, C, V> sliceable, SourceMatrix<R, C, V> source,
			List<Filter> filters, String constraintLogic, int stepSize) throws Exception
	{
		super(name, label);

		// save the source matrix
		this.source = source;

		// set default index filters if no filters are specified
		if (filters == null)
		{

			//use -1 to compensate for totals to indices
			int rowStop = MatrixRendererHelper.ROW_STOP_DEFAULT > source.getTotalNumberOfRows() - 1 ? source
					.getTotalNumberOfRows() - 1 : MatrixRendererHelper.ROW_STOP_DEFAULT;
			int colStop = MatrixRendererHelper.COL_STOP_DEFAULT > source.getTotalNumberOfCols() - 1 ? source
					.getTotalNumberOfCols() - 1 : MatrixRendererHelper.COL_STOP_DEFAULT;

			filters = new ArrayList<Filter>();
			//when "" is omitted: java.lang.IllegalArgumentException: QueryRule(): Operator.<= cannot be used with one argument
			filters.add(new Filter(Filter.Type.rowIndex, new QueryRule("", Operator.LESS_EQUAL, rowStop)));
			filters.add(new Filter(Filter.Type.colIndex, new QueryRule("", Operator.LESS_EQUAL, colStop)));
		}

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
		renderMe = new RenderableMatrixImpl<R, C, V>(source, basic, filters, "", 5);

		// constraintLogic, stepSize);

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

}
