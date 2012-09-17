/* Date:        October 28, 2010
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package plugin.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.ScreenMessage;
import org.molgenis.framework.ui.ScreenModel;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import app.JDBCMetaDatabase;

public class SimpleWholeDatabaseSearch extends PluginModel<Entity>
{
	private static final long serialVersionUID = 4004696283997492221L;

	public SimpleWholeDatabaseSearch(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	private SimpleWholeDatabaseSearchModel model = new SimpleWholeDatabaseSearchModel();

	JDBCMetaDatabase metadb;

	public SimpleWholeDatabaseSearchModel getMyModel()
	{
		return model;
	}
	
	public String getCustomHtmlHeaders()
	{
		return "";
	}

	@Override
	public String getViewName()
	{
		return "plugin_search_SimpleWholeDatabaseSearch";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugin/search/SimpleWholeDatabaseSearch.ftl";
	}

	@Override
	public void handleRequest(Database db, Tuple request)
	{
		try
		{
			if (request.getString("__action") != null)
			{
				String action = request.getString("__action");

				if (action.equals("doSearch"))
				{
					String searchThis = request.getString("searchThis");
					
					this.model.searchThis = searchThis;

					long start = System.currentTimeMillis();
					List<org.molgenis.util.Entity> results = search(searchThis, db);
					long stop = System.currentTimeMillis();
					
					double time = (stop-start)/1000.0;
					System.out.println("TIME " + time);
					this.model.setTime(time);

					this.model.setResults(results);
					this.setMessages(new ScreenMessage("Search complete", true));
				}
			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));
		}
	}

	private List<Entity> search(String searchThis, Database db) throws DatabaseException
	{
		List<org.molgenis.util.Entity> res = new ArrayList<org.molgenis.util.Entity>();
	
		for (org.molgenis.model.elements.Entity eClass : db.getMetaData().getEntities(false, false))
		{
				
			String name = eClass.getName();
			Class<? extends Entity> entityClass = db.getClassForName(name);
				
			//user must have at least have read permissions
			if(this.getLogin().canRead(entityClass))
			{
				List<? extends org.molgenis.util.Entity> eInstances = db.find(entityClass);
				for (org.molgenis.util.Entity e : eInstances)
				{
					for (String field : e.getFields())
					{
						// match if:
						// 1. Field value is not NULL
						// 2. Class is equal to Type, hereby removing
						// superclasses from results
						// 3. Lowercased value matches the lowercased search
						// string
						if (e.get(field) != null && e.get(Field.TYPE_FIELD).toString().equals(name)
								&& e.get(field).toString().toLowerCase().contains(searchThis.toLowerCase()))
						{
							res.add(e);
							break;
						}
					}
				}
			}
			
		}
		return res;
	}

	@Override
	public void reload(Database db)
	{

		try
		{
			if (metadb == null)
			{
				metadb = new JDBCMetaDatabase();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			this.setMessages(new ScreenMessage(e.getMessage() != null ? e.getMessage() : "null", false));

		}
	}
}
