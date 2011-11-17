

package plugins.LLcatalogueTree;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.util.Entity;
import org.molgenis.util.LinkableTree;
import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tuple;

public class LLcatalogueTreePlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -6143910771849972946L;
	
	private JQueryTreeView<?> treeView;

	public LLcatalogueTreePlugin(String name, ScreenController<?> parent)
	{
		super(name, parent);
		
		SimpleTree<JQueryTreeViewElement> myTree = new SimpleTree<JQueryTreeViewElement>("myTree", null);
		SimpleTree<JQueryTreeViewElement> mySubTree1 = new SimpleTree("mySubTree1", myTree);
		SimpleTree<JQueryTreeViewElement> mySubSubTree = new SimpleTree("mySubSubTree", mySubTree1);
		SimpleTree<JQueryTreeViewElement> mySubSubSubtree = new SimpleTree("mySubSubSubtree",mySubSubTree );
		
		LinkableTree<?> linkableTree = new LinkableTree("linkabletree", mySubSubSubtree, "http://www.google.com");
		
		LinkableTree<JQueryTreeViewElement> anotherTree = new LinkableTree("anothertree",linkableTree, "dsfgsdfgdsfg" );

		SimpleTree mySubTree2 = new SimpleTree("mySubTree2", myTree);

		
		treeView = new JQueryTreeView("Example tree viewer", myTree);
	}

	@Override
	public String getViewName()
	{
		return "plugins_LLcatalogueTree_LLcatalogueTreePlugin";
	}

	@Override
	public String getViewTemplate()
	{
		return "plugins/LLcatalogueTree/LLcatalogueTreePlugin.ftl";
	}

	
	@Override
	public void handleRequest(Database db, Tuple request)
	{
		//replace example below with yours
//		try
//		{
//			//start database transaction
//			db.beginTx();
//
//			//get the "__action" parameter from the UI
//			String action = request.getAction();
//		
//			if( action.equals("do_add") )
//			{
//				Experiment e = new Experiment();
//				e.set(request);
//				db.add(e);
//			}
//
//			//commit all database actions above
//			db.commitTx();
//
//		} catch(Exception e)
//		{
//			db.rollbackTx();
//			//e.g. show a message in your form
//		}
	}

	@Override
	public void reload(Database db)
	{
//		try
//		{
//			Database db = this.getDatabase();
//			Query q = db.query(Experiment.class);
//			q.like("name", "test");
//			List<Experiment> recentExperiments = q.find();
//			
//			//do something
//		}
//		catch(Exception e)
//		{
//			//...
//		}
	}
	
	@Override
	public boolean isVisible()
	{
		if (!this.getLogin().isAuthenticated()) {
			return false;
		}
		return true;
	}

	public String getTreeView() {
		return treeView.toHtml();
	}
}
