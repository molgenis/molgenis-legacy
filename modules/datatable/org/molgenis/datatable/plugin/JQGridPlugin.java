package org.molgenis.datatable.plugin;



import java.util.Collections;

import org.molgenis.datatable.controller.JQGridController;
import org.molgenis.datatable.model.JdbcTable;
import org.molgenis.datatable.view.JQGridView;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.ui.GenericPlugin;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.DivPanel;


/**
 * View data in a matrix.
 */
public class JQGridPlugin extends GenericPlugin
{
	private static final long serialVersionUID = 8804579908239186037L;
	private Container container = null;
	private DivPanel div = null;
	private JQGridView gridView;
	
	public JQGridPlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public void reload(Database db)
	{
		
		try
		{
			//strange way to retrieve columns!
			final JdbcTable jdbcTable = new JdbcTable(db, "SELECT Name, Continent, SurfaceArea, Population FROM Country LIMIT 0", Collections.<QueryRule>emptyList());
			gridView = new JQGridView("jqGridId", new JQGridController.JDBCDataSourceDescription("Country", jdbcTable.getColumns()));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		container = new Container();
		div = new DivPanel();
		container.add(div);
		div.add(gridView);
    }
	
	public ScreenView getView()
    {
    	return container;
    }

	public String render() {
		return container.render();
	}
}
