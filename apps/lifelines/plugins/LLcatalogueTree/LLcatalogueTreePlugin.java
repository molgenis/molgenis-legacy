

package plugins.LLcatalogueTree;


import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.LinkableTree;
import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tuple;

public class LLcatalogueTreePlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -6143910771849972946L;
	
	private JQueryTreeView<JQueryTreeViewElement> treeView;
	
	public LLcatalogueTreePlugin(String name, ScreenController<?> parent)
	{	
		super(name, parent);
//		JQueryTreeViewElement protocolTree = null;
		Database db = this.getController().getDatabase();

		List<ObservableFeature> obsFeatures = new ArrayList<ObservableFeature>();
		
		//obsFeatures.get(0);
		Protocol protocol = new Protocol();
		List<Protocol> protocols = new ArrayList<Protocol>();
		List<Protocol> subprotocols = new ArrayList<Protocol>();
		
		protocol.getValues();
		protocol.getName();

		   
		
		//iterate through all the protocols and set the name of each protocol to tree name
		//make a link for each of them so that the contents show on the splitter!
		
		
		try {
			 for(Protocol p: db.find(Protocol.class))
			    {
			       System.out.println("Protocols name: >>" + p.getName());
			       protocols.add(p);
			       JQueryTreeViewElement protocolTree= new JQueryTreeViewElement(p.getName(), null);
			       
				   if (p.getSubprotocols_Id() != null) {
				       	System.out.println("SubProtocols name: >>" + p.getName());
					   //if protocols.contains(p)
					   //subprotocols.add(p);
				   }
			    }
			
			
		} catch (DatabaseException e) {
			e.printStackTrace();
		}


		

		JQueryTreeViewElement myTree = new JQueryTreeViewElement("myTree", null);
		JQueryTreeViewElement mySubTree1 = new JQueryTreeViewElement("mySubTree1", myTree);
		JQueryTreeViewElement mySubSubTree = new JQueryTreeViewElement("mySubSubTree", mySubTree1);
		JQueryTreeViewElement mySubSubSubtree = new JQueryTreeViewElement("mySubSubSubtree",mySubSubTree );
		
		JQueryTreeViewElement linkableTree = new JQueryTreeViewElement("linkabletree", mySubSubSubtree, "http://www.google.com");
		
		JQueryTreeViewElement anotherTree = new JQueryTreeViewElement("anothertree",linkableTree, "http://www.google.com" );

		//JQueryTreeViewElement mySubTree2 = new JQueryTreeViewElement("mySubTree2", protocolTree);
		
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
