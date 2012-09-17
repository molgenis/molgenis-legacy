package plugins.data;


import gcc.catalogue.ShoppingCart;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class MySelectionsEditPlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = 8033359218974451651L;
	private List<ShoppingCart> usersSelections = new ArrayList<ShoppingCart>();
	private JQueryTreeView<JQueryTreeViewElement> treeView = null;



		public MySelectionsEditPlugin(String name, ScreenController<?> parent)
		{
			super(name, parent);
		}


		@Override
		public String getViewName()
		{
			return "plugins_data_MySelectionsEditPlugin";
		}
	
		@Override
		public String getViewTemplate()
		{
			return "plugins/data/MySelectionsEditPlugin.ftl";
		}
		
		public String getCustomHtmlHeaders()
	    {
	        return "<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/download_list.css\">" +
	        		"<link rel=\"stylesheet\" style=\"text/css\" href=\"res/css/catalogue.css\">";
	    }
	
		@Override
		public void handleRequest(Database db, Tuple request) throws Exception	{
	
			if ("editSelection".equals(request.getAction())) {
				System.out.println(">>aaaaaaaaaaaaaaa<<<<<<<<<<<<<<<<<<<<<<<<" );
				
				List<String> selected = new ArrayList<String>();
				String htmlTreeView = treeView.toHtml(selected);

			} 
		}
		
		@Override
		public void reload(Database db) {
			this.usersSelections.clear();
			try {
				for (ShoppingCart sc: db.find(ShoppingCart.class)) {
					this.usersSelections.add(sc);
					//System.out.println(">>>>"+sc);
				}
			} catch (DatabaseException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	
		public List<ShoppingCart> getUsersSelections() {
			return usersSelections;
		}
		
		public String getChoiceLabel() {
			return "User Selections Below";
		}
		
		
		
	
		
	
}