package org.molgenis.datatable.plugin;

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
import org.molgenis.datatable.model.JoinQueryTable;
import org.molgenis.datatable.model.QueryTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
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

/**
 * View data in a matrix.
 */
public class JQGridPlugin extends EasyPluginController<ScreenModel> {
	private static final long serialVersionUID = 1095633658510456459L;

	public JQGridPlugin(String name, ScreenController<?> parent) {
		super(name, parent);
	}

	JQGridView.TupleTableBuilder tupleTableBuilder = new JQGridView.TupleTableBuilder() {

		private final String backEnd = "JOINTABLE";

		@Override
		public TupleTable create(Database db, Tuple request)
				throws TableException {
			List<String> tableNames = new ArrayList<String>();
			final List<String> columnNames = new ArrayList<String>();
			getTableAndColumnNames(request, tableNames, columnNames, true);
			try {
				if (backEnd.equals("JOINTABLE")) {
					return createJoinTable(db, tableNames, columnNames);
				} else if (backEnd.equals("QUERYTABLE")) {
					return createQueryTable(db, tableNames, columnNames);
				} else if (backEnd.equals("JDBCTABLE")) {
					return createJDBTable(db, tableNames, columnNames);
				} else {
					return null;
				}
			} catch (Exception ex) {
				throw new TableException(ex);
			}
		}

		private TupleTable createJDBTable(Database db, List<String> tableNames,
				final List<String> columnNames) throws DatabaseException {
			try {
				if (CollectionUtils.isEmpty(columnNames)) {
					return new JdbcTable(db, "SELECT * FROM Country");
				} else {
					return new JdbcTable(db, String.format(
							"SELECT %s FROM Country",
							StringUtils.join(columnNames, ",")));
				}
			} catch (Exception ex) {
				throw new DatabaseException(ex);
			}
		}

		private TupleTable createJoinTable(Database db,
				List<String> tableNames, final List<String> columnNames)
				throws DatabaseException {
			final Connection connection = db.getConnection();
			final SQLTemplates dialect = new MySQLTemplates();
			final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);

			if (CollectionUtils.isEmpty(tableNames)) {
				tableNames = Arrays.asList("Country", "City");
			}

			final List<JoinQueryTable.Join> joins = Arrays
					.asList(new JoinQueryTable.Join("Country", "Country.Code",
							"City", "City.CountryCode"));
			return new JoinQueryTable(query, tableNames, columnNames, joins, db);
		}

		private TupleTable createQueryTable(Database db,
				List<String> tableNames, final List<String> columnNames)
				throws DatabaseException {
			final Connection connection = db.getConnection();
			final SQLTemplates dialect = new MySQLTemplates();
			final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);

			PathBuilder<RelationalPath> country = new PathBuilder<RelationalPath>(
					RelationalPath.class, "Country");
			PathBuilder<RelationalPath> city = new PathBuilder<RelationalPath>(
					RelationalPath.class, "City");
			query.from(country, city).where(
					country.get("code").eq(city.get("countrycode")));

			final NumberPath<Integer> countryPopulation = country
					.get(new NumberPath<Integer>(Integer.class, "Population"));
			final NumberPath<Integer> cityPopulation = city
					.get(new NumberPath<Integer>(Integer.class, "Population"));

			final NumberExpression<Double> cityPopulationRatio = cityPopulation
					.divide(countryPopulation);
			query.where(country.get("code").eq(city.get("countrycode")));
			query.limit(10);
			query.orderBy(cityPopulationRatio.desc());

			// create select
			Field countryName = new Field("Country.Name");
			countryName.setType(new StringField());
			Field cityName = new Field("City.Name");
			cityName.setType(new StringField());
			Field ratio = new Field("ratio");
			ratio.setType(new DecimalField());

			LinkedHashMap<String, SimpleExpression<? extends Object>> selectMap = new LinkedHashMap<String, SimpleExpression<? extends Object>>();
			selectMap.put("Country.Name", country.get(new StringPath("name")));
			selectMap.put("City.Name", city.get(new StringPath("name")));
			selectMap.put("ratio", cityPopulationRatio);
			List<Field> columns = Arrays.asList(countryName, cityName, ratio);
			final QueryTable queryTable = new QueryTable(query, selectMap,
					columns);
			return queryTable;
		}

		private void getTableAndColumnNames(Tuple request,
				List<String> inTableNames, List<String> inColumnNames,
				boolean completeColumnNames) {
			if (request != null) {
				final String[] colNamesParamaters = ((MolgenisRequest) request)
						.getRequest().getParameterValues("colNames[]");
				if (colNamesParamaters == null
						|| colNamesParamaters.length == 0) {
					return;
				}

				final List<String> columNames = Arrays
						.asList(colNamesParamaters);
				for (final String column : columNames) {
					if (StringUtils.contains(column, ".")) {
						final String tableName = StringUtils.substringBefore(
								column, ".");
						final String columnName = StringUtils.substringAfter(
								column, ".");
						if (!inTableNames.contains(tableName)) {
							inTableNames.add(tableName);
						}
						if (completeColumnNames) {
							inColumnNames.add(column);
						} else {
							inColumnNames.add(columnName);
						}
					} else {
						inColumnNames.add(column);
					}
				}
			}
		}

		@Override
		public String getUrl() {
			return "molgenis.do?__target=" + getName()
					+ "&__action=download_json";
		}
	};

	@Override
	public void reload(Database db) {
		// all request completly recreate grid
	}

	// handling of the ajax; should be auto-wired via the JQGridTableView
	// contructor (TODO)
	public void download_json(Database db, Tuple request, OutputStream out) {
		final JQGridView jqGridView = new JQGridView(super.getName(),
				tupleTableBuilder);
		try {
			jqGridView.handleRequest(db, request, out);
		} catch (HandleRequestDelegationException ex) {
			Logger.getLogger(JQGridPlugin.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	// what is shown to the user
	@Override
	public ScreenView getView() {
		MolgenisForm view = new MolgenisForm(this);
		view.add(new JQGridView(super.getName(), tupleTableBuilder));
		return view;
	}
}
