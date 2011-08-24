package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.matrix.component.general.Filter;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.matrix.component.general.MatrixRendererHelper;
import org.molgenis.matrix.component.general.Mover;
import org.molgenis.matrix.component.general.RenderableMatrixImpl;
import org.molgenis.matrix.component.general.Validate;
import org.molgenis.matrix.component.interfaces.BasicMatrix;
import org.molgenis.matrix.component.interfaces.RenderableMatrix;
import org.molgenis.matrix.component.interfaces.SliceableMatrix;
import org.molgenis.matrix.component.interfaces.SourceMatrix;
import org.molgenis.util.Tuple;

/**
 * 
 * TODO: Class doc
 * 
 * @param <R>
 * @param <C>
 * @param <V>
 */
public class MatrixRenderer<R, C, V> extends HtmlWidget
{

	private SliceableMatrix<R, C, V> sliceable;
	private RenderableMatrix<R, C, V> renderMe;
	private SourceMatrix<R, C, V> source;
	private String screenName;
	private int stepSize;
	private Mover<R, C, V> mover;

	/**
	 * Simple constructor for MatrixRenderer. Setup matrix rendering with only
	 * two significant arguments: sliceable and source. Wraps the complete
	 * constructor which will use default settings when called by this
	 * constructor with certain null values.
	 * 
	 * @param name
	 *            The HtmlWidget name for this component.
	 * @param sliceable
	 *            The sliceable matrix describing your filtering logic. Needed
	 *            to create a RenderableMatrix.
	 * @param source
	 *            The source matrix describing your data and data set. Needed to
	 *            create a RenderableMatrix.
	 * @param screenName
	 *            The name of the parent/plugin the component is used in. Must
	 *            be set right. Use this.getName() in your plugin.
	 * @throws Exception
	 */
	public MatrixRenderer(String name, SliceableMatrix<R, C, V> sliceable, SourceMatrix<R, C, V> source,
			String screenName) throws Exception
	{
		this(name, name, sliceable, source, null, null, 5, screenName);
	}

	/**
	 * Complete constructor for MatrixRenderer. Setup rendering by providing all
	 * information. Adding any filters here (!null List) will cause the default
	 * filters (paging area) to be ignored.
	 * 
	 * @param name
	 *            The HtmlWidget name for this component.
	 * @param label
	 *            The HtmlWidget label for this component.
	 * @param sliceable
	 *            The sliceable matrix describing your filtering logic. Needed
	 *            to create a RenderableMatrix.
	 * @param source
	 *            The source matrix describing your data and data set. Needed to
	 *            create a RenderableMatrix.
	 * @param filters
	 *            The list of filters used for the first render. Though provided
	 *            by yourself, they will be ordered and validated as any other.
	 * @param constraintLogic
	 *            The filtering logic set on first render.
	 * @param stepSize
	 *            The 'paging speed' set on first render.
	 * @param screenName
	 *            The name of the parent/plugin the component is used in. Must
	 *            be set right. Use this.getName() in your plugin.
	 * @throws Exception
	 */
	public MatrixRenderer(String name, String label, SliceableMatrix<R, C, V> sliceable, SourceMatrix<R, C, V> source,
			List<Filter> filters, String constraintLogic, int stepSize, String screenName) throws Exception
	{
		super(name, label);

		// save source and sliceable
		this.source = source;
		this.sliceable = sliceable;
		this.screenName = screenName;
		this.stepSize = stepSize;

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

	/**
	 * Visualize the RenderableMatrix using a Freemarker template. The resulting
	 * HTML can be put anywhere in the screen in another plugin.
	 */
	public String toHtml()
	{
		Map<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("name", this.getName());
		parameters.put("value", this.getObject());
		parameters.put("matrix", this.renderMe);
		parameters.put("operators", MatrixRendererHelper.operators());
		parameters.put("req_tag", MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX);
		return new FreemarkerView("org/molgenis/matrix/component/MatrixRenderer.ftl", parameters).render();
	}

	/**
	 * Helper function for those who want to get out the content of the rendered
	 * matrix. This means you can choose to not render the matrix at all and
	 * just use the 'engine', or render it yourself.
	 * 
	 * @return
	 */
	public RenderableMatrix<R, C, V> getRendered()
	{
		return this.renderMe;
	}

	/**
	 * Take matrix component action from the request and call the function for
	 * this action. After this is done, apply (altered) filters and render a new
	 * matrix.
	 * 
	 * @param request
	 * @throws Exception
	 */
	public void delegateHandleRequest(Tuple request) throws Exception
	{

		String action = request.getString("__action");
		String pref = MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX;

		new Validate<R, C, V>().validateAction(action, pref);
		action = action.substring(pref.length(), action.length());

		List<Filter> previousFilters = MatrixRendererHelper.copyFilterList(renderMe.getFilters());
		if (mover == null) mover = new Mover<R, C, V>();

		// note: for convenience, these action functions store filter
		// modifications in renderMe.
		if (action.equals("updatePagingSettings")) this.updatePagingSettings(request);
		if (action.equals("moveRight")) mover.moveRight(renderMe);
		if (action.equals("moveLeft")) mover.moveLeft(renderMe);
		if (action.equals("moveDown")) mover.moveDown(renderMe);
		if (action.equals("moveUp")) mover.moveUp(renderMe);
		if (action.equals("moveFarRight")) mover.moveFarRight(renderMe);
		if (action.equals("moveFarLeft")) mover.moveFarLeft(renderMe);
		if (action.equals("moveFarDown")) mover.moveFarDown(renderMe);
		if (action.equals("moveFarUp")) mover.moveFarUp(renderMe);
		if (action.startsWith("filter")) this.addFilter(request);

		try
		{
			applyFiltersAndRender(sliceable, renderMe.getFilters());
		}
		catch (Exception e)
		{
			applyFiltersAndRender(sliceable, previousFilters);
			throw e;
		}

	}

	/**
	 * Order the filters, and create a fresh sliceable matrix. Then loop through
	 * all filters and apply them, 'slicing' the result smaller every pass.
	 * Construct a new RenderableMatrix from the result.
	 * 
	 * @param sliceable
	 * @param filters
	 * @throws Exception
	 */
	private void applyFiltersAndRender(SliceableMatrix<R, C, V> sliceable, List<Filter> filters) throws Exception
	{
		System.out.println("applyFiltersAndRender");

		new Validate<R, C, V>().validateFilters(filters, source);
		filters = orderFilters(filters);
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
		new Validate<R, C, V>().validateResult(basic);

		System.out.println("result 'basic': colsize: " + basic.getVisibleCols().size());
		System.out.println("result 'basic': rowsize: " + basic.getVisibleRows().size());

		renderMe = new RenderableMatrixImpl<R, C, V>(source, basic, filters, "", stepSize, screenName);
	}

	/**
	 * Update the matrix with the values width, height and step size provided in
	 * the left box with the 'Update' button. Step size is simply set, width and
	 * height requires the limit filters to be replaced with new ones.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void updatePagingSettings(Tuple request) throws Exception
	{
		String pref = MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX;
		int width = request.getInt(pref + "width");
		int height = request.getInt(pref + "height");
		this.stepSize = request.getInt(pref + "stepSize");

		List<Filter> newFilters = new ArrayList<Filter>();

		// keep all filters, except for paging -> LIMIT
		for (int filterIndex = 0; filterIndex < renderMe.getFilters().size(); filterIndex++)
		{
			Filter f = renderMe.getFilters().get(filterIndex);
			if (!(f.getFilterType().equals(Filter.Type.paging) && f.getQueryRule().getOperator().equals(Operator.LIMIT)))
			{
				newFilters.add(f);
			}
		}

		newFilters.add(new Filter(Filter.Type.paging, new MatrixQueryRule("row", Operator.LIMIT, height)));
		newFilters.add(new Filter(Filter.Type.paging, new MatrixQueryRule("col", Operator.LIMIT, width)));

		renderMe.getFilters().clear();
		renderMe.getFilters().addAll(newFilters);

	}

	/**
	 * Handle the 'Apply' buttons for filters by getting the values (type,
	 * field, operator and value) from the request and parse them into the
	 * corresponding filters. Basic checks on variable types is be done here but
	 * no more. Filter correctness is checked by validateFilters in the Validate
	 * class.
	 * 
	 * TODO: parse and set filters TODO: how to get error messages on the screen
	 * nicely?
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void addFilter(Tuple request) throws Exception
	{

		MatrixQueryRule q = null;

		Object filterValue = request.getObject("FILTER_BY_VALUE_VALUE");
		String filterField = request.getString("FILTER_BY_VALUE_FIELD").substring(4);
		if (filterValue != null && filterField != null)
		{
			String filterOperator = request.getString("FILTER_BY_VALUE_OPERATOR");
			q = new MatrixQueryRule(filterField, Operator.valueOf(filterOperator), filterValue);
		}

		if (q != null)
		{
			Filter newFilter = new Filter(Filter.Type.colValues, q);
			this.renderMe.getFilters().add(newFilter);
		}
	}

	/**
	 * Move the paging filters to the back of the filter queue. The order will
	 * become: other, offsets, limits. This will preserve proper paging
	 * behaviour. Later on we could allow the user to 'drag around' all the
	 * filters and make some crazy combinations which bypass this function,
	 * maybe with a checkbox option.
	 */
	private List<Filter> orderFilters(List<Filter> filters)
	{
		List<Filter> result = new ArrayList<Filter>();
		List<Filter> limitFilters = new ArrayList<Filter>();
		List<Filter> offsetFilters = new ArrayList<Filter>();
		List<Filter> otherFilters = new ArrayList<Filter>();
		for (int filterIndex = 0; filterIndex < filters.size(); filterIndex++)
		{
			Filter f = filters.get(filterIndex);
			if (f.getFilterType().equals(Filter.Type.paging))
			{
				if (f.getQueryRule().getOperator().equals(Operator.LIMIT))
				{
					limitFilters.add(f);
				}
				if (f.getQueryRule().getOperator().equals(Operator.OFFSET))
				{
					offsetFilters.add(f);
				}
			}
			else
			{
				otherFilters.add(f);
			}
		}
		result.addAll(otherFilters);
		result.addAll(offsetFilters);
		result.addAll(limitFilters);
		return result;
	}

}
