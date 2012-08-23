package org.molgenis.datatable.plugin;

import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.model.JoinQueryCreator;
import org.molgenis.datatable.model.QueryCreator;
import org.molgenis.datatable.model.QueryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.test.MemoryTableFactory;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridFilter;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridRule;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
import org.molgenis.model.elements.Field;
import org.molgenis.util.HandleRequestDelegationException;
import org.molgenis.util.Tuple;

import com.google.gson.Gson;
import com.mysema.query.sql.MySQLTemplates;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQueryImpl;
import com.mysema.query.sql.SQLTemplates;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.path.NumberPath;
import com.mysema.query.types.path.PathBuilder;
import com.mysema.query.types.path.StringPath;

/**
 * View data in a matrix.
 */
public class JQGridPlugin extends EasyPluginController<ScreenModel>
{
	private static final long serialVersionUID = 1095633658510456459L;

	public JQGridPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	JQGridView.TupleTableBuilder tupleTableBuilder = new JQGridView.TupleTableBuilder()
	{
		private final String backEnd = "JOINTABLE";

		@Override
		public TupleTable create(Database db, Tuple request) throws TableException
		{
			final List<String> tableNames = new ArrayList<String>();
			final List<String> columnNames = new ArrayList<String>();
			final List<String> hiddenFilterColumns = new ArrayList<String>();
			// columns that have a filter but are not displayed to the user
			getTableAndColumnNames(request, tableNames, columnNames, hiddenFilterColumns, true);
			try
			{
				if (backEnd.equals("JOINTABLE"))
				{
					return createJoinTable(db, tableNames, columnNames, hiddenFilterColumns);
				}
				else if (backEnd.equals("QUERYTABLE"))
				{
					return createQueryTable(db, tableNames, columnNames);
				}
				else if (backEnd.equals("JDBCTABLE"))
				{
					return createJDBTable(db, tableNames, columnNames);
				}
				else if (backEnd.equals("LIFELINES_VM_TEST"))
				{
					return createLifelinesTestVMJoinTable(db, tableNames, columnNames);
				}
				else if (backEnd.equals("MEMORY_TABLE"))
				{
					return createMemory(db, tableNames, columnNames);
				}
				else
				{
					return null;
				}
			}
			catch (final Exception ex)
			{
				throw new TableException(ex);
			}
		}

		private TupleTable createMemory(Database db, List<String> tableNames, List<String> columnNames)
		{
			final TupleTable table = MemoryTableFactory.create(51, 10);
			return table;
		}

		private TupleTable createJDBTable(Database db, List<String> tableNames, final List<String> columnNames)
				throws DatabaseException
		{
			try
			{
				if (CollectionUtils.isEmpty(columnNames))
				{
					return new JdbcTable(db, "SELECT * FROM Country");
				}
				else
				{
					return new JdbcTable(db,
							String.format("SELECT %s FROM Country", StringUtils.join(columnNames, ",")));
				}
			}
			catch (final Exception ex)
			{
				throw new DatabaseException(ex);
			}
		}

		private TupleTable createLifelinesTestVMJoinTable(Database db, List<String> tableNames,
				final List<String> columnNames) throws DatabaseException
		{
			// final Connection connection = db.getConnection();
			// final SQLTemplates dialect = new OracleTemplates();
			// final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);
			//
			// // SELECT *
			// // FROM vw_bezoek_pivot visit, vw_bloeddrukavg bp
			// // WHERE visit.pa_id = bp.pa_id
			// if (CollectionUtils.isEmpty(tableNames))
			// {
			// tableNames = Arrays.asList("VW_BEZOEK", "VW_BLOEDDRUKAVG");
			// }
			// final List<JoinQueryTable.Join> joins = new
			// ArrayList<JoinQueryTable.Join>();
			// if (tableNames.contains("VW_BEZOEK") &&
			// tableNames.contains("VW_BLOEDDRUKAVG"))
			// {
			// joins.add(new Join("VW_BEZOEK", "BZ_ID", "VW_BLOEDDRUKAVG",
			// "BZ_ID"));
			// }
			//
			// return new JoinQueryTable(query, tableNames, columnNames, joins,
			// db);
			return null;
		}

		private TupleTable createJoinTable(Database db, List<String> tableNames, final List<String> columnNames,
				final List<String> hiddenFilterColumns) throws DatabaseException
		{
			final SQLTemplates dialect = new MySQLTemplates();

			List<JoinQueryCreator.Join> joins = new ArrayList<JoinQueryCreator.Join>();

			if (CollectionUtils.isEmpty(tableNames))
			{
				tableNames = Arrays.asList("Country", "City", "CountryLanguage");
			}

			if (tableNames.contains("Country") && tableNames.contains("City"))
			{
				final JoinQueryCreator.Join countryCity = new JoinQueryCreator.Join("Country", "Code", "City",
						"CountryCode");
				joins.add(countryCity);
			}
			if (tableNames.contains("Country") && tableNames.contains("CountryLanguage"))
			{
				final JoinQueryCreator.Join countryLanguage = new JoinQueryCreator.Join("Country", "Code",
						"CountryLanguage", "CountryCode");
				joins.add(countryLanguage);
			}

			final JoinQueryCreator tableCreator = new JoinQueryCreator(db, tableNames, columnNames,
					hiddenFilterColumns, joins);
			return new QueryTable(tableCreator, db.getConnection(), dialect);
		}

		private TupleTable createQueryTable(Database db, List<String> tableNames, final List<String> columnNames)
				throws DatabaseException
		{

			class QueryCountryRatioPopulation implements QueryCreator
			{
				final LinkedHashMap<String, SimpleExpression<? extends Object>> selectMap = new LinkedHashMap<String, SimpleExpression<? extends Object>>();

				@Override
				public SQLQueryImpl createQuery(final Connection connection, final SQLTemplates dialect)
				{
					final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);

					final PathBuilder<RelationalPath> country = new PathBuilder<RelationalPath>(RelationalPath.class,
							"Country");
					final PathBuilder<RelationalPath> city = new PathBuilder<RelationalPath>(RelationalPath.class,
							"City");
					query.from(country, city).where(country.get("code").eq(city.get("countrycode")));

					final NumberPath<Integer> countryPopulation = country.get(new NumberPath<Integer>(Integer.class,
							"Population"));
					final NumberPath<Integer> cityPopulation = city.get(new NumberPath<Integer>(Integer.class,
							"Population"));

					final NumberExpression<Double> cityPopulationRatio = cityPopulation.divide(countryPopulation);
					query.where(country.get("code").eq(city.get("countrycode")));
					query.limit(10);

					selectMap.put("Country.Name", country.get(new StringPath("name")));
					selectMap.put("City.Name", city.get(new StringPath("name")));
					selectMap.put("ratio", cityPopulationRatio);

					return query;
				}

				public LinkedHashMap<String, SimpleExpression<? extends Object>> getAttributeExpressions()
				{
					return selectMap;
				}

				@Override
				public List<Field> getFields()
				{
					final Field countryName = new Field("Country.Name");
					countryName.setType(new StringField());
					final Field cityName = new Field("City.Name");
					cityName.setType(new StringField());
					final Field ratio = new Field("ratio");
					ratio.setType(new DecimalField());
					return Arrays.asList(countryName, cityName, ratio);
				}

				@Override
				public List<String> getHiddenFieldNames()
				{
					return null;
				}

			}
			return new QueryTable(new QueryCountryRatioPopulation(), db.getConnection(), new MySQLTemplates());
		}

		private void getTableAndColumnNames(Tuple request, List<String> inTableNames, List<String> inColumnNames,
				final List<String> hiddenColumnsNames, boolean completeColumnNames)
		{
			if (request != null)
			{
				String[] tableNames = ((MolgenisRequest) request).getRequest().getParameterValues("tableNames[]");

				String[] colNamesParamaters = null;

				final String exportColumns = ((MolgenisRequest) request).getString("exportColumnSelection");
				if (StringUtils.isNotEmpty(exportColumns))
				{
					if (exportColumns.equals("SELECTED_COLUMNS"))
					{
						colNamesParamaters = ((MolgenisRequest) request).getRequest().getParameterValues(
								"treeSelectColNames[]");
					}

					else if (exportColumns.equals("ALL_COLUMNS"))
					{
						return;
					}
				}

				if (tableNames != null && tableNames.length > 0)
				{
					inTableNames.addAll(new ArrayList<String>(Arrays.asList(tableNames)));
				}

				if (colNamesParamaters == null)
				{
					colNamesParamaters = ((MolgenisRequest) request).getRequest().getParameterValues("colNames[]");
				}
				if (colNamesParamaters == null || colNamesParamaters.length == 0)
				{
					return;
				}

				final String filtersParam = request.getString("filters");
				if (StringUtils.isNotEmpty(filtersParam))
				{
					final JQGridFilter filters = new Gson().fromJson(filtersParam, JQGridFilter.class);
					for (JQGridRule rule : filters.rules)
					{
						hiddenColumnsNames.add(rule.field);
					}
				}

				final List<String> columnNames = new ArrayList<String>();
				columnNames.addAll(Arrays.asList(colNamesParamaters));
				columnNames.addAll(hiddenColumnsNames);
				for (final String column : columnNames)
				{
					if (StringUtils.contains(column, "."))
					{
						final String tableName = StringUtils.substringBefore(column, ".");
						final String columnName = StringUtils.substringAfter(column, ".");
						if (!inTableNames.contains(tableName))
						{
							inTableNames.add(tableName);
						}
						if (completeColumnNames)
						{
							inColumnNames.add(column);
						}
						else
						{
							inColumnNames.add(columnName);
						}
					}
					else
					{
						inColumnNames.add(column);
					}
				}
			}
		}

		@Override
		public String getUrl()
		{
			return "molgenis.do?__target=" + getName() + "&__action=download_json";
		}
	};

	@Override
	public void reload(Database db)
	{
		// all request completly recreate grid
	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json(Database db, Tuple request, OutputStream out)
	{
		final JQGridView jqGridView = new JQGridView(super.getName(), tupleTableBuilder);
		try
		{
			jqGridView.handleRequest(db, request, out);
		}
		catch (final HandleRequestDelegationException ex)
		{
			Logger.getLogger(JQGridPlugin.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	// what is shown to the user
	@Override
	public ScreenView getView()
	{
		final MolgenisForm view = new MolgenisForm(this);
		view.add(new JQGridView(super.getName(), tupleTableBuilder));
		return view;
	}
}