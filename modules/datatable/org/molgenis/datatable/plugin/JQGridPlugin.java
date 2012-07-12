package org.molgenis.datatable.plugin;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.server.MolgenisRequest;
import org.molgenis.framework.ui.EasyPluginController;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.MolgenisForm;
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
    	
        @Override
        public TupleTable create(Database db, Tuple request) throws TableException {
			final List<String> tableNames = new ArrayList<String>();
			final List<String> columnNames = new ArrayList<String>();
			getTableAndColumnNames(request, tableNames, columnNames, true);
			if(CollectionUtils.isEmpty(columnNames)) {
				return new JdbcTable(db, "SELECT * FROM Country");
			} else {
				return new JdbcTable(db, String.format("SELECT %s FROM Country",StringUtils.join(columnNames, ",")));
			}
        }

        private void getTableAndColumnNames(Tuple request, List<String> inTableNames, List<String> inColumnNames, boolean completeColumnNames) {
            if (request != null) {
				final String[] colNamesParamaters = ((MolgenisRequest)request).getRequest().getParameterValues("colNames[]");
				if(colNamesParamaters == null || colNamesParamaters.length == 0) {
					return;
				}

				final List<String> columNames = Arrays.asList(colNamesParamaters);
                for (final String column : columNames) {
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
        //all request completely recreate grid
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
