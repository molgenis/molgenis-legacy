

package plugins.LLcatalogueTree;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.ui.PluginModel;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

public class LLcatalogueTreePlugin extends PluginModel<Entity>
{

	private static final long serialVersionUID = -6143910771849972946L;
	
	private JQueryTreeView<JQueryTreeViewElement> treeView;
	
	private HashMap<String, Protocol> nameToProtocol;

	private HashMap<String, JQueryTreeViewElement> labelToTree;
	
	public LLcatalogueTreePlugin(String name, ScreenController<?> parent)
	{	
		super(name, parent);
//		JQueryTreeViewElement protocolTree = null;
	//	Database db = this.getController().getDatabase(); 
		
		//Database db = this.getDatabase();

	}
	
	
	public void recursiveAddingTree(List<String> parentNode, JQueryTreeViewElement parentTree){
		
		for(String protocolName : parentNode){
			
			Protocol protocol = nameToProtocol.get(protocolName);
			
			if(protocol != null){
				
				JQueryTreeViewElement childTree;
				
				if(labelToTree.containsKey(protocolName)){
					
					childTree = labelToTree.get(protocolName);
				
				}else{
					
					childTree = new JQueryTreeViewElement(protocolName, parentTree);
					childTree.setCollapsed(true);
					labelToTree.put(protocolName, childTree);
				}

				if(protocol.getSubprotocols_Name() != null){
					recursiveAddingTree(protocol.getSubprotocols_Name(), childTree);
				}
				if(protocol.getFeatures_Name() != null){
					
					addingMeasurementTotree(protocol.getFeatures_Name(), childTree);
				}
			}
		}
	} 
	
//	@Override
//	public String getCustomHtmlBodyOnLoad()
//	{
//		
//		
////		JQuerySplitterContents c = new JQuerySplitterContents();
////		JQuerySplitter2<JQuerySplitterContents> jqs2 = new JQuerySplitter2<JQuerySplitterContents>("aaa", c);
//		
//		
//		return treeView.toHtml();;
//		
//	}
	
	public void addingMeasurementTotree(List<String> parentNode, JQueryTreeViewElement parentTree){
		
		for(String measurementName : parentNode){
			
			JQueryTreeViewElement childTree;

			if(labelToTree.containsKey(measurementName)){

				childTree = labelToTree.get(measurementName);

			}else{

				childTree = new JQueryTreeViewElement(measurementName, parentTree);
				labelToTree.put(measurementName, childTree);
			}
		}
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
		List<String> topProtocols = new ArrayList<String>();
		
		List<String> bottomProtocols = new ArrayList<String>();
		
		List<String> middleProtocols = new ArrayList<String>();
		
		labelToTree = new HashMap<String, JQueryTreeViewElement>();
		
		nameToProtocol = new HashMap<String, Protocol>();
		
		try {
			for(Protocol p : db.find(Protocol.class)){
				
				//System.out.println(p.getName());
				
				List<String> subNames = p.getSubprotocols_Name();
				
				if(!nameToProtocol.containsKey(p.getName())){
					nameToProtocol.put(p.getName(), p);
				}
				
				if(!subNames.isEmpty()){
					
					if(!topProtocols.contains(p.getName()))
						topProtocols.add(p.getName());
					
					for(String subProtocol : subNames){
						
						if(!middleProtocols.contains(subProtocol))
							middleProtocols.add(subProtocol);
					}
					
				}else{
					
					if(!bottomProtocols.contains(p.getName())){
						bottomProtocols.add(p.getName());
						
					}
				}
			}
			
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		
		middleProtocols.removeAll(bottomProtocols);
		
		topProtocols.removeAll(middleProtocols);
		
		JQueryTreeViewElement protocolsTree = new JQueryTreeViewElement("Protocols", null);
		
		recursiveAddingTree(topProtocols, protocolsTree);
		
		treeView = new JQueryTreeView<JQueryTreeViewElement>("Protocols", protocolsTree);
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
