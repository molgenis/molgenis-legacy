package org.molgenis.datatable.view;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.molgenis.datatable.controller.Renderers;
import org.molgenis.datatable.model.FilterableTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.util.JQueryUtil;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridConfiguration;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.server.MolgenisResponse;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

public class JQGridView extends HtmlWidget {
	public static final String OPERATION = "Operation";
    
	private enum Operation {
		LOAD_CONFIG,
		RENDER_DATA, 
		LOAD_TREE
	}

    public interface TupleTableBuilder {
        public TupleTable create(Database db, Tuple request) throws TableException;
        public String getUrl();
    }
    private final TupleTableBuilder tupleTableBuilder;

    public JQGridView(String name, TupleTableBuilder tupleTableBuilder) {
        super(name);
        this.tupleTableBuilder = tupleTableBuilder;
    }

    /**
     * Handle a particular {@link MolgenisRequest}, and encode any resulting
     * renderings/exports into a {@link MolgenisResponse}. Particulars handled:
     * <ul> <li>Select the appropriate view towards which to export/render.</li>
     * <li>Apply proper sorting and filter rules.</li> <li>Wrap the desired data
     * source in the appropriate instantiation of
     * {@link TupleTable}.</li> <li>Select and render the data.</li> </ul>
     */
    public void handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException {
		try {
			final HttpServletResponse response = ((MolgenisRequest) request).getResponse();
			
			final TupleTable tupleTable = tupleTableBuilder.create(db, request);
			final String opRequest =  request.getString(OPERATION);
			
			final Operation operation = StringUtils.isNotEmpty(opRequest) ? 
					Operation.valueOf(opRequest) :
					Operation.RENDER_DATA;
			if (operation == Operation.LOAD_CONFIG) {
                loadTupleTableConfig(db, (MolgenisRequest)request, tupleTable);
			} else if (operation == Operation.LOAD_TREE) {
                final String treeNodes = JQueryUtil.getDynaTreeNodes(tupleTable.getColumns());
                response.getOutputStream().print(treeNodes);
            } else {
                final List<QueryRule> rules = new ArrayList<QueryRule>();
                final List<QueryRule> filterRules = createQueryRulesFromJQGridRequest(request);

                if (CollectionUtils.isNotEmpty(filterRules)) {  //is this a good idea (instanceof)?
                    if (tupleTable instanceof FilterableTupleTable) {
                        rules.addAll(filterRules);
                        ((FilterableTupleTable)tupleTable).setFilters(rules);
                    }
                }

                final int limit = request.getInt("rows");
                final int rowCount = tupleTable.getCount();
                tupleTable.close(); // Not nice! We should fix this!
                final int totalPages = (int) Math.ceil(rowCount / limit);
                final int page = Math.min(request.getInt("page"), totalPages);
                final int offset = Math.max(limit * page - limit, 0);
                
                tupleTable.setLimit(limit);
                tupleTable.setOffset(offset);
                final String sortOrder = request.getString("sord");
                final String sortField = request.getString("sidx");
                
                
                
                if(StringUtils.isNotEmpty(sortField) && tupleTable instanceof FilterableTupleTable) {
                    final Operator sortOperator = StringUtils.equals(sortOrder, "asc") ? QueryRule.Operator.SORTASC : QueryRule.Operator.SORTDESC;
                    rules.add(new QueryRule(sortOperator, sortField));
                }
                
                if(tupleTable instanceof FilterableTupleTable) {
                   ((FilterableTupleTable)tupleTable).setFilters(rules);
                }
                
                renderData(((MolgenisRequest) request).getRequest(), response, page, totalPages, tupleTable);
            }
			tupleTable.close();
        } catch (Exception e) {
            throw new HandleRequestDelegationException(e);
        }
    }

    /**
     * Render a particular subset of data from a {@link TupleTable} to a
     * particular {@link Renderer}.
     *
     * @param request The request encoding the particulars of the rendering to
     * be done.
     * @param response The response into which the view is rendered.
     * @param page The selected page (only relevant for {@link JQGridRenderer}
     * rendering)
     * @param totalPages The total number of pages (only relevant for
     *            {@link JQGridRenderer} rendering)
     * @param tupleTable The table from which to render the data.
     */
    private void renderData(HttpServletRequest request, HttpServletResponse response, int page, int totalPages,
            final TupleTable tupleTable) throws TableException {
        final ServletContext servletContext = request.getSession().getServletContext();

        String strViewType = (String) request.getParameter("viewType");
        if (StringUtils.isEmpty(strViewType)) { 
            strViewType = "JQ_GRID";
        }
        try {
            final ViewFactory viewFactory = new ViewFactoryImpl(); 
            final Renderers.Renderer view = viewFactory.createView(strViewType);
            view.export(servletContext, request, response, request.getParameter("caption"), tupleTable, totalPages, page);
        } catch (Exception e) {
            throw new TableException(e);
        }
    }

    /**
     * Extract the filter rules from the sent jquery request, and convert them
     * into Molgenis Query rules.
     *
     * @param request A request containing filter rules
     * @return A list of QueryRules that represent the filter rules from the
     * request.
     */
    @SuppressWarnings("rawtypes")
    private static List<QueryRule> createQueryRulesFromJQGridRequest(Tuple request) {
        final String filtersParameter = request.getString("filters");
        final List<QueryRule> rules = new ArrayList<QueryRule>();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(filtersParameter)) {
            final StringMap filters = (StringMap) new Gson().fromJson(filtersParameter, Object.class);
            final String groupOp = (String) filters.get("groupOp");
            @SuppressWarnings("unchecked")
            final ArrayList<StringMap<String>> jsonRules = (ArrayList<StringMap<String>>) filters.get("rules");
            int ruleIdx = 0;
            for (StringMap<String> rule : jsonRules) {
                final String field = rule.get("field");
                final String op = rule.get("op");
                final String value = rule.get("data");

                final QueryRule queryRule = convertOperator(field, op, value);
                rules.add(queryRule);

                final boolean notLast = jsonRules.size() - 1 != ruleIdx++;
                if (groupOp.equals("OR") && notLast) {
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
     * @param field The field to which to apply the operator
     * @param op The operator string (jquery syntax)
     * @param value The value (if any) for the right-hand side of the operator
     * expression.
     * @return A new QueryRule that represents the supplied jquery expression.
     */
    private static QueryRule convertOperator(final String field, final String op, final String value) {
        // ['eq','ne','lt','le','gt','ge','bw','bn','in','ni','ew','en','cn','nc']
        QueryRule rule = new QueryRule(field, QueryRule.Operator.EQUALS, value);
        if (op.equals("eq")) {
            rule.setOperator(QueryRule.Operator.EQUALS);
        } else if (op.equals("ne")) {
            // NOT
            rule.setOperator(QueryRule.Operator.EQUALS);
            rule = toNotRule(rule);
        } else if (op.equals("lt")) {
            rule.setOperator(QueryRule.Operator.LESS);
        } else if (op.equals("le")) {
            rule.setOperator(QueryRule.Operator.LESS_EQUAL);
        } else if (op.equals("gt")) {
            rule.setOperator(QueryRule.Operator.GREATER);
        } else if (op.equals("ge")) {
            rule.setOperator(QueryRule.Operator.GREATER_EQUAL);
        } else if (op.equals("bw")) {
            rule.setValue(value + "%");
            rule.setOperator(QueryRule.Operator.LIKE);
        } else if (op.equals("bn")) {
            // NOT
            rule.setValue(value + "%");
            rule.setOperator(QueryRule.Operator.LIKE);
            rule = toNotRule(rule);
        } else if (op.equals("in")) {
            rule.setOperator(QueryRule.Operator.IN);
        } else if (op.equals("ni")) {
            // NOT
            rule.setOperator(QueryRule.Operator.IN);
            rule = toNotRule(rule);
        } else if (op.equals("ew")) {
            rule.setValue("%" + value);
            rule.setOperator(QueryRule.Operator.LIKE);
        } else if (op.equals("en")) {
            // NOT
            rule.setValue("%" + value);
            rule.setOperator(QueryRule.Operator.LIKE);
            rule = toNotRule(rule);
        } else if (op.equals("cn")) {
            rule.setValue("%" + value + "%");
            rule.setOperator(QueryRule.Operator.LIKE);
        } else if (op.equals("nc")) {
            // NOT
            rule.setValue("%" + value + "%");
            rule.setOperator(QueryRule.Operator.LIKE);
            rule = toNotRule(rule);
        } else {
            throw new IllegalArgumentException(String.format("Unkown Operator: %s", op));
        }
        return rule;
    }

    /**
     * Add a 'NOT' operator to a particular rule.
     *
     * @param rule The rule to negate.
     * @return A new {@link QueryRule} which is the negation of the supplied
     * rule.
     */
    private static QueryRule toNotRule(QueryRule rule) {
        return new QueryRule(QueryRule.Operator.NOT, rule);
    }

    @Override
    public String toHtml() {

        try {
            final Map<String, Object> args = new HashMap<String, Object>();

            args.put("tableId", super.getId());
            args.put("url", tupleTableBuilder.getUrl());

            final Configuration cfg = new Configuration();
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setClassForTemplateLoading(JQGridView.class, "");
            final Template template = cfg.getTemplate(JQGridView.class.getSimpleName() + ".ftl");
            final Writer out = new StringWriter();
            template.process(args, out);
            out.flush();
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTupleTableConfig(Database db, MolgenisRequest request, TupleTable tupleTable) throws TableException, IOException {
        final JQGridConfiguration config = new JQGridConfiguration(getId(), "Name", tupleTableBuilder.getUrl(), "test", tupleTable);
        final String jqJsonConfig = new Gson().toJson(config);
        request.getResponse().getOutputStream().println(jqJsonConfig);
    }

    /**
     * Class wrapping the results of a jqGrid query. To be serialized by Gson,
     * hence no accessors necessary for private datamembers.
     */
    public static class JQGridResult {

        @SuppressWarnings("unused")
        private final int page;
        @SuppressWarnings("unused")
        private final int total;
        @SuppressWarnings("unused")
        private final int records;
        private ArrayList<LinkedHashMap<String, String>> rows = new ArrayList<LinkedHashMap<String, String>>();

        public JQGridResult(int page, int total, int records) {
            this.page = page;
            this.total = total;
            this.records = records;
        }
    }

    /**
     * Function to build a datastructure filled with rows from a
     * {@link TupleTable}, to be serialised by Gson and displayed from there by
     * a jqGrid.
     *
     * @param rowCount The number of rows to select.
     * @param totalPages The total number of pages of data (ie. dependent on
     * size of dataset and nr. of rows per page)
     * @param page The selected page.
     * @param table The Tupletable from which to read the data.
     * @return
     */
    public static JQGridResult buildJQGridResults(final int rowCount, final int totalPages, final int page,
            final TupleTable table) throws TableException {
        final JQGridResult result = new JQGridResult(page, totalPages, rowCount);
        for (final Tuple row : table) {
            final LinkedHashMap<String, String> rowMap = new LinkedHashMap<String, String>();

            final List<String> fieldNames = row.getFieldNames();
            for (final String fieldName : fieldNames) {
                final String rowValue = !row.isNull(fieldName) ? row.getString(fieldName) : "null";
                rowMap.put(fieldName, rowValue); // TODO encode to HTML
            }
            result.rows.add(rowMap);
        }
        return result;
    }
}
