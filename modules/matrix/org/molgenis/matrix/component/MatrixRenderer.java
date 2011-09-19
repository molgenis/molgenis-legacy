package org.molgenis.matrix.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.matrix.MatrixException;
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
			List<MatrixQueryRule> filters, String constraintLogic, int stepSize, String screenName) throws Exception
	{
		super(name, label);

		// save source and sliceable
		this.source = source;
		this.sliceable = sliceable;
		this.screenName = screenName;
		this.stepSize = stepSize;
		this.mover = new Mover<R, C, V>();
	
		// Why was the statement below inserted?
//		if(true) throw new UnsupportedOperationException("fixme using limit/offset");

//		int nrOfPagingFilters = 0;
//		if (filters != null) {
//			for (MatrixQueryRule f : filters) {
//				if (f.getFilterType().equals(MatrixQueryRule.Type.paging)) {
//					nrOfPagingFilters++;
//				}
//			}
//		}

		// set default index filters if no (index) filters are specified
		if (filters == null /*|| nrOfPagingFilters == 0*/)
		{
			// use -1 to compensate for totals to indices
			int rowStop = MatrixRendererHelper.ROW_STOP_DEFAULT > source.getTotalNumberOfRows() - 1 ? source
					.getTotalNumberOfRows() - 1 : MatrixRendererHelper.ROW_STOP_DEFAULT;
			int colStop = MatrixRendererHelper.COL_STOP_DEFAULT > source.getTotalNumberOfCols() - 1 ? source
					.getTotalNumberOfCols() - 1 : MatrixRendererHelper.COL_STOP_DEFAULT;

			if (filters == null) filters = new ArrayList<MatrixQueryRule>();
			this.sliceable.setRowLimit(rowStop);
			this.sliceable.setColLimit( colStop);
		}

		filterAndRenderRequest(sliceable, filters);
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

		List<MatrixQueryRule> previousFilters = MatrixRendererHelper.copyMatrixQueryRuleList(renderMe.getRules());

		// note: for convenience, these action functions store filter
		// modifications in renderMe.
		if (action.equals("updatePagingSettings")) this.updatePagingRequest(request);
		if (action.equals("moveRight")) mover.moveRight(renderMe);
		if (action.equals("moveLeft")) moveLeft();
		if (action.equals("moveDown")) mover.moveDown(renderMe);
		if (action.equals("moveUp")) mover.moveUp(renderMe);
		if (action.equals("moveFarRight")) mover.moveFarRight(renderMe);
		if (action.equals("moveFarLeft")) mover.moveFarLeft(renderMe);
		if (action.equals("moveFarDown")) mover.moveFarDown(renderMe);
		if (action.equals("moveFarUp")) mover.moveFarUp(renderMe);
		if (action.startsWith("add_filter")) this.addFilterRequest(action, request);
		if (action.startsWith("remove_filter")) this.removeFilterRequest(action, request);
		if (action.startsWith("push_filter")) this.pushFilterRequest(action, request);

		try
		{
			filterAndRenderRequest(sliceable, renderMe.getRules());
		}
		catch (Exception e)
		{
			filterAndRenderRequest(sliceable, previousFilters);
			throw e;
		}

	}

	/**
	 * Add a filter to the stack. Handle the 'Apply' buttons for filters by
	 * getting the values (type, field, operator and value) from the request and
	 * parse them into the corresponding filters. Basic checks on variable types
	 * is be done here but no more. Filter correctness is checked by
	 * validateFilters in the Validate class.
	 * 
	 * @param request
	 * @throws Exception
	 */
	private void addFilterRequest(String action, Tuple request) throws Exception
	{
		String pref = MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX;

		String field = request.getString(pref + action + "FILTER_FIELD");
		Operator operator = Operator.valueOf(request.getString(pref + action + "FILTER_OPERATOR"));
		Object value = request.getObject(pref + action + "FILTER_VALUE");

		new Validate<R, C, V>().validateFilterInputs(field, operator, value);

		MatrixQueryRule newFilter = null;

		if (action.equals("add_filter_by_index"))
		{
			newFilter = new MatrixQueryRule(MatrixQueryRule.Type.colIndex, field, operator, value);
		}
		else if (action.equals("add_filter_by_col_value"))
		{
			newFilter = new MatrixQueryRule(MatrixQueryRule.Type.colValues, field, operator, value);
		}
		else if (action.equals("add_filter_by_row_value"))
		{
			newFilter = new MatrixQueryRule(MatrixQueryRule.Type.rowValues, field, operator, value);
		}
		else if (action.equals("add_filter_by_col_attrb"))
		{
			newFilter = new MatrixQueryRule(MatrixQueryRule.Type.colHeader, field, operator, value);
		}
		else if (action.equals("add_filter_by_row_attrb"))
		{
			newFilter = new MatrixQueryRule(MatrixQueryRule.Type.rowHeader, field, operator, value);
		}
		else
		{
			throw new MatrixException("Filter action '" + action + "' not recognized.");
		}

		addFilter(newFilter);
	}

	/**
	 * Remove a filter from the stack
	 * 
	 * @param action
	 * @param request
	 */
	private void removeFilterRequest(String action, Tuple request)
	{
		int index = Integer.valueOf(action.replace("remove_filter", ""));
		removeFilter(index);
	}

	/**
	 * Push a filter up or down the stack
	 * 
	 * @param action
	 * @param request
	 * @throws MatrixException
	 */
	private void pushFilterRequest(String action, Tuple request) throws MatrixException
	{
		if (action.contains("push_filter_up"))
		{
			int index = Integer.valueOf(action.replace("push_filter_up", ""));
			pushFilter(index, false);
		}
		else
		{
			int index = Integer.valueOf(action.replace("push_filter_down", ""));
			pushFilter(index, true);
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
	private void filterAndRenderRequest(SliceableMatrix<R, C, V> sliceable, List<MatrixQueryRule> filters) throws Exception
	{
		System.out.println("applyFiltersAndRender");

		new Validate<R, C, V>().validateFilters(filters, renderMe);
		filters = orderFilters(filters);
		sliceable.reset();

		System.out.println("sliceable: colsize: " + sliceable.getResult().getColCount());
		System.out.println("sliceable: rowsize: " + sliceable.getResult().getRowCount());

		// apply filters in order
		for (MatrixQueryRule f : filters)
		{
			System.out.println("iterating over: " + f.toString());

			switch (f.getFilterType())
			{
//				case index:
//					sliceable.sliceByIndex(f);
//					break;
//				case paging:
//					sliceable.sliceByPaging(f);
//					break;
//				case rowHeader:
//					sliceable.sliceByRowProperty(property, operator, value);
//					break;
//				case colHeader:
//					sliceable.sliceByColHeader(f);
//					break;
//				case rowValues:
//					sliceable.sliceByRowValues(f);
//					break;
//				case colValues:
//					sliceable.sliceByColValues(f);
//					break;
			}
		}

		BasicMatrix<R, C, V> basic = sliceable.getResult();
		new Validate<R, C, V>().validateResult(basic);

		System.out.println("result 'basic': colsize: " + basic.getColHeaders().size());
		System.out.println("result 'basic': rowsize: " + basic.getRowHeaders().size());

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
	private void updatePagingRequest(Tuple request) throws Exception
	{
		String pref = MatrixRendererHelper.MATRIX_COMPONENT_REQUEST_PREFIX;
		int width = request.getInt(pref + "width");
		int height = request.getInt(pref + "height");
		int newStepSize = request.getInt(pref + "stepSize");
		updatePaging(width, height, newStepSize);
	}

	/**
	 * Move the paging filters to the back of the filter stack. The order will
	 * become: other, offsets, limits. This will preserve proper paging
	 * behaviour. Later on we could allow the user to 'drag around' all the
	 * filters and make some crazy combinations which bypass this function,
	 * maybe with a checkbox option.
	 */
	private List<MatrixQueryRule> orderFilters(List<MatrixQueryRule> filters)
	{
		List<MatrixQueryRule> result = new ArrayList<MatrixQueryRule>();
//		List<MatrixQueryRule> limitFilters = new ArrayList<MatrixQueryRule>();
//		List<MatrixQueryRule> offsetFilters = new ArrayList<MatrixQueryRule>();
		List<MatrixQueryRule> otherFilters = new ArrayList<MatrixQueryRule>();
//		for (int filterIndex = 0; filterIndex < filters.size(); filterIndex++)
//		{
//			MatrixQueryRule f = filters.get(filterIndex);
//			if(true) throw new UnsupportedOperationException("use limit and offset on sliceable");
//			if (f.getFilterType().equals(MatrixQueryRule.Type.paging))
//			{
//				if (f.getOperator().equals(Operator.LIMIT))
//				{
//					limitFilters.add(f);
//				}
//				if (f.getOperator().equals(Operator.OFFSET))
//				{
//					offsetFilters.add(f);
//				}
//			}
//			else
//			{
//				otherFilters.add(f);
//			}
//		}
		result.addAll(otherFilters);
		//result.addAll(offsetFilters);
		//result.addAll(limitFilters);
		return result;
	}
	
	public void addFilter(MatrixQueryRule filter) throws Exception
	{
		new Validate<R, C, V>().validateFilter(filter, renderMe);
		this.renderMe.getRules().add(filter);
	}

	/**
	 * API: Remove a filter.
	 * 
	 * @param index
	 */
	public void removeFilter(int index)
	{
		renderMe.getRules().remove(index);
	}

	/**
	 * API: Push filter up or down the stack.
	 * @param index
	 * @param pushDown
	 * @throws MatrixException
	 */
	public void pushFilter(int index, boolean pushDown) throws MatrixException
	{
		MatrixQueryRule filter = renderMe.getRules().get(index);
//		if (filter.getFilterType() == MatrixQueryRule.Type.paging) throw new MatrixException(
//				"You cannot move paging filters yet!");
		renderMe.getRules().remove(index);
		if (pushDown)
		{
			renderMe.getRules().add(index + 1, filter);
		}
		else
		{
			renderMe.getRules().add(index - 1, filter);
		}
	}

	/**
	 * Overloaded updatePaging
	 * 
	 * @param width
	 * @param height
	 */
	public void updatePaging(int width, int height)
	{
		updatePaging(width, height, -1);
	}
	
	/**
	 * API: update the paging variables
	 * @param width
	 * @param height
	 * @param stepSize
	 */
	public void updatePaging(int width, int height, int stepSize)
	{
		if(stepSize > 0) this.stepSize = stepSize;
		
		List<MatrixQueryRule> newFilters = new ArrayList<MatrixQueryRule>();
		
		// keep all filters, except for paging -> LIMIT
		for (int filterIndex = 0; filterIndex < renderMe.getRules().size(); filterIndex++)
		{
			MatrixQueryRule f = renderMe.getRules().get(filterIndex);
//			if (!(f.getFilterType().equals(MatrixQueryRule.Type.paging) && f.getOperator().equals(Operator.LIMIT)))
//			{
//				newFilters.add(f);
//			}
		}
	
		
//		
//		newFilters.add(new MatrixQueryRule(MatrixQueryRule.Type.paging, "row", Operator.LIMIT, height));
//		newFilters.add(new MatrixQueryRule(MatrixQueryRule.Type.paging, "col", Operator.LIMIT, width));
	
		renderMe.getRules().clear();
		renderMe.getRules().addAll(newFilters);
	}

	/**
	 * API: Apply current filters and render the matrix.
	 * @throws Exception
	 */
	public void filterAndRender() throws Exception
	{
		filterAndRenderRequest(sliceable, renderMe.getRules());
	}

	/**
	 * API: Move left.
	 * 
	 * @throws Exception
	 */
	public void moveLeft() throws Exception
	{
		mover.moveLeft(renderMe);
	}

	/**
	 * API: Move down.
	 * 
	 * @throws Exception
	 */
	public void moveDown() throws Exception
	{
		mover.moveDown(renderMe);
	}

	/**
	 * API: Move right.
	 * 
	 * @throws Exception
	 */
	public void moveRight() throws Exception
	{
		mover.moveRight(renderMe);
	}

	/**
	 * API: Move up.
	 * 
	 * @throws Exception
	 */
	public void moveUp() throws Exception
	{
		mover.moveUp(renderMe);
	}
	
	/**
	 * API: Move far left.
	 * 
	 * @throws Exception
	 */
	public void moveFarLeft() throws Exception
	{
		mover.moveFarLeft(renderMe);
	}

	/**
	 * API: Move far down.
	 * 
	 * @throws Exception
	 */
	public void moveFarDown() throws Exception
	{
		mover.moveFarDown(renderMe);
	}

	/**
	 * API: Move far right.
	 * 
	 * @throws Exception
	 */
	public void moveFarRight() throws Exception
	{
		mover.moveFarRight(renderMe);
	}

	/**
	 * API: Move far up.
	 * 
	 * @throws Exception
	 */
	public void moveFarUp() throws Exception
	{
		mover.moveFarUp(renderMe);
	}

	/**
	 * API: Get the result. Helper function for those who just want the content
	 * of the rendered matrix. This means you can choose to not render the
	 * matrix at all and just use the 'engine', or render it yourself.
	 * 
	 * @return
	 */
	public RenderableMatrix<R, C, V> getRendered()
	{
		return this.renderMe;
	}
	
	public void setRowOffsetLimit(int offset, int limit)
	{
		this.sliceable.setRowLimit(limit);
		this.sliceable.setRowOffset(offset);
	}

	
	public void setColOffsetLimit(int offset, int limit)
	{
		this.sliceable.setColLimit(limit);
		this.sliceable.setColOffset(offset);
	}
}
