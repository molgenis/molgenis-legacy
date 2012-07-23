package org.molgenis.datatable.view;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.molgenis.datatable.controller.Renderers;
import org.molgenis.datatable.controller.Renderers.JQGridRenderer;
import org.molgenis.datatable.controller.Renderers.Renderer;
import org.molgenis.datatable.model.FilterableTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.util.JQueryUtil;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridConfiguration;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridFilter;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridPostData;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridResult;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridRule;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridRule.JQGridOp;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.google.gson.Gson;

public class JQGridView extends HtmlWidget
{
	public static final String OPERATION = "Operation";

	private enum Operation
	{
		LOAD_CONFIG, RENDER_DATA, LOAD_TREE
	}

	public interface TupleTableBuilder
	{
		public TupleTable create(Database db, Tuple request) throws TableException;

		public String getUrl();
	}

	private final TupleTableBuilder tupleTableBuilder;

	public JQGridView(String name, TupleTableBuilder tupleTableBuilder)
	{
		super(name);
		this.tupleTableBuilder = tupleTableBuilder;
	}

	public JQGridView(final String name, final ScreenController<?> hostController, final TupleTable table)
	{
		this(name, new TupleTableBuilder()
		{
			@Override
			public String getUrl()
			{
				return "molgenis.do?__target=" + hostController.getName() + "&__action=download_json_" + name;
			}

			@Override
			public TupleTable create(Database db, Tuple request) throws TableException
			{
				table.setDb(db);
				return table;
			}
		});
	}

	/**
	 * Handle a particular {@link MolgenisRequest}, and encode any resulting
	 * renderings/exports into a {@link MolgenisResponse}. Particulars handled:
	 * <ul>
	 * <li>Select the appropriate view towards which to export/render.</li>
	 * <li>Apply proper sorting and filter rules.</li>
	 * <li>Wrap the desired data source in the appropriate instantiation of
	 * {@link TupleTable}.</li>
	 * <li>Select and render the data.</li>
	 * </ul>
	 */
	public void handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException
	{
		try
		{
			final TupleTable tupleTable = tupleTableBuilder.create(db, request);
			final Operation operation = StringUtils.isNotEmpty(request.getString(OPERATION)) ? Operation
					.valueOf(request.getString(OPERATION)) : Operation.RENDER_DATA;

			switch (operation)
			{
				case LOAD_CONFIG:
					loadTupleTableConfig(db, (MolgenisRequest) request, tupleTable);
					break;
				case LOAD_TREE:
					// risky: we give it all columns which would fail if there
					// are many
					final String treeNodes = JQueryUtil.getDynaTreeNodes(tupleTable.getAllColumns());
					((MolgenisRequest) request).getResponse().getOutputStream().print(treeNodes);
					break;
				case RENDER_DATA:
					final List<QueryRule> rules = new ArrayList<QueryRule>();

					// parse the request
					JQGridPostData postData = new JQGridPostData(request);

					// convert to query rules
					final List<QueryRule> filterRules = createQueryRulesFromJQGridRequest(postData.filters);

					if (CollectionUtils.isNotEmpty(filterRules))
					{
						if (tupleTable instanceof FilterableTupleTable)
						{
							rules.addAll(filterRules);
							((FilterableTupleTable) tupleTable).setFilters(rules);
						}
					}

					final int rowCount = tupleTable.getCount();
					final int totalPages = (int) Math.ceil(rowCount / postData.rows);

					// update page
					postData.page = Math.min(postData.page, totalPages);
					final int offset = Math.max((postData.page - 1) * postData.rows, 0);

					// data.rows == limit
					tupleTable.setLimit(postData.rows);

					// data.rows * data.page
					tupleTable.setOffset(offset);

					if (StringUtils.isNotEmpty(postData.sidx) && tupleTable instanceof FilterableTupleTable)
					{
						final Operator sortOperator = StringUtils.equals(postData.sord, "asc") ? QueryRule.Operator.SORTASC
								: QueryRule.Operator.SORTDESC;
						rules.add(new QueryRule(sortOperator, postData.sidx));
					}

					if (tupleTable instanceof FilterableTupleTable)
					{
						((FilterableTupleTable) tupleTable).setFilters(rules);
					}

					renderData(((MolgenisRequest) request), postData, totalPages, tupleTable);
			}
		}
		catch (final Exception e)
		{
			throw new HandleRequestDelegationException(e);
		}
	}

	/**
	 * Render a particular subset of data from a {@link TupleTable} to a
	 * particular {@link Renderer}.
	 * 
	 * @param request
	 *            The request encoding the particulars of the rendering to be
	 *            done.
	 * @param response
	 *            The response into which the view is rendered.
	 * @param postData
	 *            The selected page (only relevant for {@link JQGridRenderer}
	 *            rendering)
	 * @param totalPages
	 *            The total number of pages (only relevant for
	 *            {@link JQGridRenderer} rendering)
	 * @param tupleTable
	 *            The table from which to render the data.
	 */
	private void renderData(MolgenisRequest request, JQGridPostData postData, int totalPages,
			final TupleTable tupleTable) throws TableException
	{
		tupleTable.setDb(request.getDatabase());

		String strViewType = request.getString("viewType");
		if (StringUtils.isEmpty(strViewType))
		{
			strViewType = "JQ_GRID";
		}

		try
		{
			final ViewFactory viewFactory = new ViewFactoryImpl();
			final Renderers.Renderer view = viewFactory.createView(strViewType);
			view.export(request, request.getString("caption"), tupleTable, totalPages, postData.page);
		}
		catch (final Exception e)
		{
			throw new TableException(e);
		}
	}

	/**
	 * Extract the filter rules from the sent jquery request, and convert them
	 * into Molgenis Query rules.
	 * 
	 * @param filters
	 *            A request containing filter rules
	 * @return A list of QueryRules that represent the filter rules from the
	 *         request.
	 */
	@SuppressWarnings("rawtypes")
	private static List<QueryRule> createQueryRulesFromJQGridRequest(JQGridFilter filters)
	{
		final List<QueryRule> rules = new ArrayList<QueryRule>();
		if (filters != null)
		{
			final String groupOp = filters.groupOp;

			int ruleIdx = 0;
			for (final JQGridRule rule : filters.rules)
			{
				final QueryRule queryRule = convertOperator(rule);
				rules.add(queryRule);

				final boolean notLast = filters.rules.size() - 1 != ruleIdx++;
				if (groupOp.equals("OR") && notLast)
				{
					rules.add(new QueryRule(QueryRule.Operator.OR));
				}
			}
		}
		return rules;
	}

	/**
	 * Create a {@link QueryRule} based on a jquery operator string, from the
	 * filter popup/dropdown in the {@link JQGridRenderer} UI. Example:
	 * Supplying the arguments 'name', 'ne', 'Asia' creates a QueryRule that
	 * filters for rows where the 'name' column does not equal 'Asia'.
	 * 
	 * @param field
	 *            The field to which to apply the operator
	 * @param op
	 *            The operator string (jquery syntax)
	 * @param value
	 *            The value (if any) for the right-hand side of the operator
	 *            expression.
	 * @return A new QueryRule that represents the supplied jquery expression.
	 */
	private static QueryRule convertOperator(JQGridRule jqGridRule)
	{
		// ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']
		QueryRule rule = new QueryRule(jqGridRule.field, QueryRule.Operator.EQUALS, jqGridRule.data);
		switch (jqGridRule.op)
		{
			case eq:
				rule.setOperator(QueryRule.Operator.EQUALS);
				return rule;
			case ne:
				rule.setOperator(QueryRule.Operator.EQUALS);
				return toNotRule(rule);
			case lt:
				rule.setOperator(QueryRule.Operator.LESS);
				return rule;
			case le:
				rule.setOperator(QueryRule.Operator.LESS_EQUAL);
				return rule;
			case gt:
				rule.setOperator(QueryRule.Operator.GREATER);
				return rule;
			case ge:
				rule.setOperator(QueryRule.Operator.GREATER_EQUAL);
			case bw:
				rule.setValue(jqGridRule.data + "%");
				rule.setOperator(QueryRule.Operator.LIKE);
				return rule;
			case bn:
				// NOT
				rule.setValue(jqGridRule.data + "%");
				rule.setOperator(QueryRule.Operator.LIKE);
				rule = toNotRule(rule);
				return rule;
			case in:
				rule.setOperator(QueryRule.Operator.IN);
				return rule;
			case ni:
				// NOT
				rule.setOperator(QueryRule.Operator.IN);
				rule = toNotRule(rule);
				return rule;
			case ew:
				rule.setValue("%" + jqGridRule.data);
				rule.setOperator(QueryRule.Operator.LIKE);
				return rule;
			case en:
				// NOT
				rule.setValue("%" + jqGridRule.data);
				rule.setOperator(QueryRule.Operator.LIKE);
				return toNotRule(rule);
			case cn:
				rule.setValue("%" + jqGridRule.data + "%");
				rule.setOperator(QueryRule.Operator.LIKE);
				return rule;
			case nc:
				// NOT
				rule.setValue("%" + jqGridRule.data + "%");
				rule.setOperator(QueryRule.Operator.LIKE);
				return toNotRule(rule);
			default:
				throw new IllegalArgumentException(String.format("Unkown Operator: %s", jqGridRule.op));
		}
	}

	/**
	 * Add a 'NOT' operator to a particular rule.
	 * 
	 * @param rule
	 *            The rule to negate.
	 * @return A new {@link QueryRule} which is the negation of the supplied
	 *         rule.
	 */
	private static QueryRule toNotRule(QueryRule rule)
	{
		return new QueryRule(QueryRule.Operator.NOT, rule);
	}

	@Override
	public String toHtml()
	{
		final Map<String, Object> args = new HashMap<String, Object>();

		args.put("tableId", super.getId());
		args.put("url", tupleTableBuilder.getUrl());

		return new FreemarkerView(JQGridView.class, args).render();
	}

	public void loadTupleTableConfig(Database db, MolgenisRequest request, TupleTable tupleTable)
			throws TableException, IOException
	{
		tupleTable.setDb(db);
		final JQGridConfiguration config = new JQGridConfiguration(getId(), "Name", tupleTableBuilder.getUrl(), "test",
				tupleTable);

		// test
		// {"groupOp":"AND","rules":[{"field":"Country.Code","op":"eq","data":"AGO"}]}
		config.postData.filters.groupOp = "AND";
		config.postData.filters.rules.add(new JQGridRule("Country.Code", JQGridOp.eq, "AGO"));

		final String jqJsonConfig = new Gson().toJson(config);
		request.getResponse().getOutputStream().println(jqJsonConfig);
	}

	/**
	 * Function to build a datastructure filled with rows from a
	 * {@link TupleTable}, to be serialised by Gson and displayed from there by
	 * a jqGrid.
	 * 
	 * @param rowCount
	 *            The number of rows to select.
	 * @param totalPages
	 *            The total number of pages of data (ie. dependent on size of
	 *            dataset and nr. of rows per page)
	 * @param page
	 *            The selected page.
	 * @param table
	 *            The Tupletable from which to read the data.
	 * @return
	 */
	public static JQGridResult buildJQGridResults(final int rowCount, final int totalPages, final int page,
			final TupleTable table) throws TableException
	{
		final JQGridResult result = new JQGridResult(page, totalPages, rowCount);
		for (final Tuple row : table.getRows())
		{
			final LinkedHashMap<String, String> rowMap = new LinkedHashMap<String, String>();

			final List<String> fieldNames = row.getFieldNames();
			for (final String fieldName : fieldNames)
			{
				final String rowValue = !row.isNull(fieldName) ? row.getString(fieldName) : "null";
				rowMap.put(fieldName, rowValue); // TODO encode to HTML
			}
			result.rows.add(rowMap);
		}
		return result;
	}
}
