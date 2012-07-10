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
import org.molgenis.datatable.model.*;
import org.molgenis.datatable.view.JQGridJSObjects.JQGridConfiguration;
import org.molgenis.datatable.view.JQGridTableView;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.fieldtypes.DecimalField;
import org.molgenis.fieldtypes.StringField;
import org.molgenis.framework.db.Database;
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

//    JQGridTableView tableView;

    public JQGridPlugin(String name, ScreenController<?> parent) {
        super(name, parent);
    }
    
    JQGridView.TupleTableBuilder tupleTableBuilder = new JQGridView.TupleTableBuilder() {
        public static final String WIDGET_ID = "xxx";

        @Override
        public TupleTable create(Database db, Tuple request) throws TableException {
            try {
                final Connection connection = db.getConnection();
                final SQLTemplates dialect = new MySQLTemplates();
                final SQLQueryImpl query = new SQLQueryImpl(connection, dialect);

                boolean joinTable = true;
                if (joinTable) {
                    List<String> tableNames = new ArrayList<String>();
                    final List<String> columnNames = new ArrayList<String>();
                    getTableAndColumnNames(request, tableNames, columnNames, true);

                    if (CollectionUtils.isEmpty(tableNames)) {
                        tableNames = Arrays.asList("Country", "City");
                    }

                    final List<JoinQueryTable.Join> joins = Arrays.asList(
                            new JoinQueryTable.Join("Country", "Country.Code",
                            "City", "City.CountryCode"));
                    return new JoinQueryTable(query, tableNames, columnNames, joins, db);
                }

                PathBuilder<RelationalPath> country = new PathBuilder<RelationalPath>(RelationalPath.class,
                        "Country");
                PathBuilder<RelationalPath> city = new PathBuilder<RelationalPath>(RelationalPath.class, "City");
                query.from(country, city).where(country.get("code").eq(city.get("countrycode")));

                final NumberPath<Integer> countryPopulation = country.get(new NumberPath<Integer>(Integer.class,
                        "Population"));
                final NumberPath<Integer> cityPopulation = city.get(new NumberPath<Integer>(Integer.class,
                        "Population"));

                final NumberExpression<Double> cityPopulationRatio = cityPopulation.divide(countryPopulation);
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
                final QueryTable queryTable = new QueryTable(query, selectMap, columns);
                return queryTable;
            } catch (Exception ex) {
                throw new TableException(ex);
            }
        }

        private void getTableAndColumnNames(Tuple request, List<String> inTableNames, List<String> inColumnNames, boolean completeColumnNames) {
            if (request != null) {
                @SuppressWarnings("unchecked")
                final List<String> columns = (List<String>) new Gson().fromJson((String) request.getObject("colNames"), Object.class);
                
                if(CollectionUtils.isEmpty(columns)) {
                    return;
                }
                
                for (final String column : columns) {
                    if (StringUtils.contains(column, ".")) {
                        final String tableName = StringUtils.substringBefore(column, ".");
                        final String columnName = StringUtils.substringAfter(column, ".");
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
            return "molgenis.do?__target=" + getName() + "&__action=download_json";
        }
    };

    @Override
    public void reload(Database db) {
        //all request completly recreate grid
    }

    //handling of the ajax; should be auto-wired via the JQGridTableView contructor (TODO)
    public void download_json(Database db, Tuple request, OutputStream out) {
        final JQGridView jqGridView = new JQGridView(super.getName(), tupleTableBuilder);
        try {
            jqGridView.handleRequest(db, request, out);
        } catch (HandleRequestDelegationException ex) {
            Logger.getLogger(JQGridPlugin.class.getName()).log(Level.SEVERE, null, ex);
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
