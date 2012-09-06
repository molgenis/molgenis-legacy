package org.molgenis.datatable.view;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.molgenis.datatable.model.FilterableTupleTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.util.JQueryUtil;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridConfiguration;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridFilter;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridPostData;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridResult;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridRule;
import org.molgenis.datatable.view.renderers.Renderers;
import org.molgenis.datatable.view.renderers.Renderers.JQGridRenderer;
import org.molgenis.datatable.view.renderers.Renderers.Renderer;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.FreemarkerView;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.model.elements.Field;
import org.molgenis.pheno.Individual;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.ProtocolApplication;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.google.gson.Gson;

/**
 * View class which provides a JQGrid view on a {@link TupleTable}
 */
public class JQGridView extends HtmlWidget
{
	public static final String OPERATION = "Operation";
	private boolean initialize = true;
	HashMap<String, String> hashMeasurementsWithCategories = new HashMap<String, String>();

	/**
	 * Operations that the GridView can handle. LOAD_CONFIG, RENDER_DATA,
	 * LOAD_TREE
	 */
	private enum Operation
	{
		LOAD_CONFIG, RENDER_DATA, LOAD_TREE, EDIT_RECORD, ADD_RECORD, DELETE_RECORD, UPLOAD_MATRIX
	}

	/**
	 * Interface for builder classes that allow easy reconstruction of the
	 * view's inner {@link TupleTable} in the {@link JQGridView#handleRequest}
	 * function
	 */
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

	/**
	 * Default construction with an anonymous inner
	 * {@link JQGridView.TupleTableBuilder}
	 */
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
	 * Handle a particular {@link MolgenisRequest}, and render into an
	 * {@link OutputStream}. Particulars handled:
	 * <ul>
	 * <li>Wrap the desired data source in the appropriate instantiation of
	 * {@link TupleTable}.</li>
	 * <li>Determine which {@link Operation} the request is asking to handle.
	 * <li>Apply proper sorting and filter rules.</li>
	 * <li>Select the appropriate view towards which to export/render.</li>
	 * <li>Select and render the data.</li>
	 * </ul>
	 * 
	 * @param db
	 *            The database to connect to
	 * @param request
	 *            The {@link MolgenisRequest} tuple that encodes the request to
	 *            handle
	 * @param out
	 *            The {@link OutputStream} to render to.
	 */
	public void handleRequest(Database db, Tuple request, OutputStream out) throws HandleRequestDelegationException
	{
		try
		{
			final TupleTable tupleTable = tupleTableBuilder.create(db, request);
			final Operation operation = StringUtils.isNotEmpty(request.getString(OPERATION)) ? Operation
					.valueOf(request.getString(OPERATION)) : Operation.RENDER_DATA;

			if (initialize)
			{
				List<Measurement> listOM = db.find(Measurement.class);
				for (Measurement m : listOM)
				{
					if (m.getCategories_Name().size() > 0)
					{
						hashMeasurementsWithCategories.put(m.getName(), m.getDataType());

					}

				}
				initialize = false;
			}

			switch (operation)
			{
				case LOAD_CONFIG:
					loadTupleTableConfig(db, (MolgenisRequest) request, tupleTable);
					break;
				case LOAD_TREE:
					// risky: we give it all columns which would fail if
					// there
					// are many
					final String treeNodes = JQueryUtil.getDynaTreeNodes(tupleTable.getAllColumns());
					((MolgenisRequest) request).getResponse().getOutputStream().print(treeNodes);
					break;
				case RENDER_DATA:
					final List<QueryRule> rules = new ArrayList<QueryRule>();

					// parse the request into post data
					final JQGridPostData postData = new JQGridPostData(request);

					// convert any filters to query rules
					final List<QueryRule> filterRules = createQueryRulesFromJQGridRequest(postData.filters);

					if (CollectionUtils.isNotEmpty(filterRules))
					{
						if (tupleTable instanceof FilterableTupleTable)
						{
							rules.addAll(filterRules);
							((FilterableTupleTable) tupleTable).setFilters(rules);
						}
					}
					int residue = 0;
					final int rowCount = tupleTable.getCount();
					if (rowCount % postData.rows != 0)
					{
						residue = 1;
					}
					final int totalPages = (int) Math.ceil(rowCount / postData.rows) + residue;

					// update page
					postData.page = Math.min(postData.page, totalPages);
					final int offset = Math.max((postData.page - 1) * postData.rows, 0);

					final String exportSelection = request.getString("exportSelection");
					if (!StringUtils.equalsIgnoreCase(exportSelection, "ALL"))
					{
						// data.rows == limit
						tupleTable.setLimit(postData.rows);
						// data.rows * data.page
						tupleTable.setOffset(offset);
					}

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
					break;

				case EDIT_RECORD:

					String targetString = "Pa_Id";

					String targetID = request.getString(targetString);

					if (targetID != null)
					{
						// List<ObservedValue> listObservedValues = new
						// ArrayList<ObservedValue>();

						List<String> listFields = request.getFieldNames();

						List<QueryRule> listQuery = new ArrayList<QueryRule>();
						listQuery.add(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, targetID));
						listQuery.add(new QueryRule(ObservedValue.FEATURE_NAME, Operator.IN, listFields));

						Integer protAppID = db.find(ObservedValue.class, new QueryRule(listQuery)).get(0)
								.getProtocolApplication_Id();

						for (Field eachField : tupleTable.getAllColumns())
						{

							if (!eachField.getName().equals(targetString))
							{
								MolgenisUpdateDatabase mu = new MolgenisUpdateDatabase();
								mu.UpdateDatabase(db, targetID, request.getString(eachField.getName()),
										eachField.getName(), protAppID, hashMeasurementsWithCategories);
							}
						}

					}
					else
					{
						break;
					}
					break;

				case ADD_RECORD:

					// respond to the ajax calling of adding new records/.
					String patientID = request.getString("targetID");

					Individual ot = null;

					String investigationName = "";

					String message = "";

					boolean success = false;

					// Check if the individual already exists in the database,
					// if
					// so, it only gives back the message. If it doesn`t, the
					// individual is added to the database
					if (db.find(Individual.class, new QueryRule(Individual.NAME, Operator.EQUALS, patientID)).size() > 0)
					{
						message = "The patient has already existed and adding failed. Please edit this patient";
						success = false;
					}
					else
					{
						ot = new Individual();
						ot.setName(patientID);
					}
					// If the individual is new, the following code will be
					// executed.
					if (ot != null)
					{

						if (request.getString("data") != null)
						{
							// Get the data and transform it to json object. And
							// this json object contains all the new added
							// values
							JSONObject json = new JSONObject(request.getString("data"));

							// Create a new ProtocolApplication for the new
							// patient.
							ProtocolApplication pa = new ProtocolApplication();

							// Set the protocol to it. At the moment, the
							// reference
							// protocol is hard-coded in the importing. All
							// protocolApplications refer to the same protocol
							// in
							// the mainImporter.
							pa.setProtocol_Name("TestProtocol");

							// Set the name to protocolApplication. The pa name
							// schema should be more flexible later on.
							pa.setName("pa_" + ot.getName());

							List<ObservedValue> listOfNewValues = new ArrayList<ObservedValue>();

							// create an iterator for the json object.
							Iterator<?> iterator = json.keys();

							int count = 0;

							while (iterator.hasNext())
							{

								String feature = iterator.next().toString();

								// We do not know which investigation it is in
								// JQGridView.java class. Therefore we take the
								// investigationName from measurement
								if (count == 0)
								{
									investigationName = db
											.find(Measurement.class,
													new QueryRule(Measurement.NAME, Operator.EQUALS, feature)).get(0)
											.getInvestigation_Name();
									count++;
								}

								String value = json.get(feature).toString();
								if (!value.equals(""))
								{
									ObservedValue ov = new ObservedValue();
									ov.setTarget_Name(patientID);
									ov.setFeature_Name(feature);
									if (hashMeasurementsWithCategories.containsKey(feature))
									{
										String[] splitValue = value.split("\\.");
										ov.setValue(splitValue[0]);
									}
									else
									{
										ov.setValue(value);
									}
									ov.setProtocolApplication_Name(pa.getName());
									ov.setInvestigation_Name(investigationName);
									listOfNewValues.add(ov);
								}
							}

							ot.setInvestigation_Name(investigationName);

							pa.setInvestigation_Name(investigationName);

							db.add(ot);

							db.add(pa);

							db.add(listOfNewValues);

							// If everything goes well, the success message is
							// set.
							message = "the new records have been added to the database!";

							success = true;
						}
					}

					// create a json object to take the message and success
					// variables.
					JSONObject map = new JSONObject();

					map.put("message", message);
					map.put("success", success);

					// Send this json string back the html.
					((MolgenisRequest) request).getResponse().getOutputStream().println(map.toString());
					break;

				case DELETE_RECORD:

					int deleteRowIndex = request.getInt("SelectedRow");

					final String rowValue = tupleTable.getRows().get(deleteRowIndex - 1).getString("Pa_Id");
					Query<ObservedValue> query = db.query(ObservedValue.class);
					List<String> listOfColumns = new ArrayList<String>();
					for (Field f : tupleTable.getAllColumns())
					{
						if (!f.getName().equals("Pa_Id"))
						{
							listOfColumns.add(f.getName());
						}
					}
					query.addRules(new QueryRule(ObservedValue.TARGET_NAME, Operator.EQUALS, rowValue));
					query.addRules(new QueryRule(ObservedValue.FEATURE_NAME, Operator.IN, listOfColumns));
					List<ObservedValue> listOfRemoveValues = query.find();

					if (listOfRemoveValues.size() > 0)
					{

						Integer ProtocolApplicationID = null;

						if (listOfRemoveValues.get(0).getProtocolApplication_Id() != null)
						{
							ProtocolApplicationID = listOfRemoveValues.get(0).getProtocolApplication_Id();
						}
						db.remove(listOfRemoveValues);
						if (ProtocolApplicationID != null)
						{
							ProtocolApplication pa = db.find(ProtocolApplication.class,
									new QueryRule(ProtocolApplication.ID, Operator.EQUALS, ProtocolApplicationID)).get(
									0);
							db.remove(pa);
						}

						Individual ind = db.find(Individual.class,
								new QueryRule(Individual.NAME, Operator.EQUALS, rowValue)).get(0);
						db.remove(ind);
					}
					break;
				case UPLOAD_MATRIX:

					File tmpDir = new File(System.getProperty("java.io.tmpdir"));

					String filePath = tmpDir.getAbsolutePath() + "/" + request.getString("fileName");

					break;
				default:
					break;
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

	/**
	 * Create the HTML that is sent to the browser. Based on a Freemarker
	 * template file.
	 */
	@Override
	public String toHtml()
	{
		final Map<String, Object> args = new HashMap<String, Object>();

		args.put("tableId", super.getId());
		args.put("url", tupleTableBuilder.getUrl());

		return new FreemarkerView(JQGridView.class, args).render();
	}

	public HashMap<String, String> getHashMeasurements()
	{
		return hashMeasurementsWithCategories;
	}

	/**
	 * Create a properly-configured grid with default settings, on first load.
	 */
	public void loadTupleTableConfig(Database db, MolgenisRequest request, TupleTable tupleTable)
			throws TableException, IOException
	{
		tupleTable.setDb(db);
		final JQGridConfiguration config = new JQGridConfiguration(getId(), "Name", tupleTableBuilder.getUrl(),
				getLabel(), tupleTable);

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
			System.out.println("check: " + row);
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
