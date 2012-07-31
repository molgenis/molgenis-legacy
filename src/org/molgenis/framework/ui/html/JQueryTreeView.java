package org.molgenis.framework.ui.html;

import java.util.List;
import java.util.Vector;

import org.molgenis.util.SimpleTree;


public class JQueryTreeView<E> extends HtmlWidget
{
	private SimpleTree<JQueryTreeViewElement> treeData;
			
	public JQueryTreeView(String name, SimpleTree<JQueryTreeViewElement> treeData)
	{
		super(name);
		this.treeData = treeData;
	}
	
	/**
	 * Checks whether the given node contains labels that have been selected/toggled,
	 * entailing the node should be presented expanded (opened).
	 * 
	 * @param node
	 * @param selected
	 * @return
	 */
	private boolean nodeOpen(JQueryTreeViewElement node, List<String> selectedLabels) {
		Vector<JQueryTreeViewElement> children = node.getChildren();
		// iterate through all children of this node
		for (JQueryTreeViewElement child : children) {
			// if this child is in the selected list, node should be open
			if (selectedLabels.contains(child.getLabel())) {
				return true;
			}
			// if this child has children and one of them is in the selected list, node should be open
			if (child.hasChildren()) {
				if (nodeOpen(child, selectedLabels) == true) {
					return true;
				}
			}
		}
		// no (grand)child selected, so node should be closed
		return false;
	}
	
	/**
	 * 
	 * No Nodes collapsed explicitly. Node is manually closed then by giving its LI element a "closed"(/opened) CSS class.
	 * Animation enabled, speed is "normal".
	 * "Cookie" persistence enabled, causing the current tree state to be persisted.
	 *	Dynamically adding a sub tree to the existing tree demonstrated.
	 */
	private String renderTree(JQueryTreeViewElement node, List<String> selectedLabels) {
		String returnString;
		
		if (node.hasChildren()) {
			
			if(!node.getChildren().get(0).hasChildren() && node.getCheckBox() == true){
				
				returnString = "<li id = \"" + node.getName().replaceAll(" ", "_") 
							+  "\" class=\"closed"
							+  "\" style=\"display:none;\"><span class=\"folder\"><input type=\"checkbox\" id=\"" 
							+  node.getEntityID() + "\" name=\"" + node.getEntityID().split("_identifier_")[0] + "\"" 
							+  (selectedLabels.contains(node.getLabel()) ? " checked=\"yes\"" : "") 
							+  " />" + node.getLabel() + "</span>\n"
							+  "<ul>\n";
			}else{
				returnString = "<li id = \"" + node.getName().replaceAll(" ", "_") 
							+  "\" class=\"closed"
							+  "\" style=\"display:none;\"><span class=\"folder\">" + node.getLabel() + "</span>\n"
							+  "<ul>\n";
			}
			
			Vector<JQueryTreeViewElement> children = node.getChildren();
			
			for (JQueryTreeViewElement child : children) {
				returnString += renderTree(child, selectedLabels);
			}
			returnString += "</ul>\n</li>\n";
		} else {
			returnString = "<li id = \"" + node.getName().replaceAll(" ", "_") + "\" style=\"display:none;\"><span class=\"point\"><input type=\"checkbox\" id=\"" 
						  +	node.getEntityID() + "\" name=\"" + node.getEntityID().split("_identifier_")[0] + "\"" 
						  +	(selectedLabels.contains(node.getLabel()) ? " checked=\"yes\"" : "") 
						  +	" />" + node.getLabel() + "</span></li>\n"; 				
		}
		
		return returnString;
	}
	
	public String toHtml(List<String> selected){
		String html ="" 
			+ "<script src=\"res/jquery-plugins/Treeview/jquery.treeview.js\" language=\"javascript\"></script>\n"
			+ "<script src=\"res/scripts/catalogue.js\" language=\"javascript\"></script>\n"
			+ "<link rel=\"stylesheet\" href=\"res/jquery-plugins/Treeview/jquery.treeview.css\" type=\"text/css\" media=\"screen\" />\n" 
			+ "<link rel=\"stylesheet\" href=\"res/css/catalogue.css\" type=\"text/css\" media=\"screen\" />\n" 
			+ "<script src=\"res/jquery-plugins/splitter/splitter.js\" language=\"javascript\"></script>\n"
			+ "<link type=\"text/css\" href=\"jquery/css/smoothness/jquery-ui-1.8.7.custom.css\" rel=\"Stylesheet\"/>"
			+ "<script src=\"jquery/development-bundle/ui/jquery-ui-1.8.7.custom.js\" language=\"javascript\"></script>\n"
			+ "<ul id=\"browser\" class=\"pointtree\">\n"
			+ renderTree(treeData.getRoot(), selected)
			+ "</ul>\n";	
		
	    return html;
	}
	
	@Override
	public String toHtml(){
		return "";
	}
}