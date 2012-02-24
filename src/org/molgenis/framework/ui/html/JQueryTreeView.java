package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.molgenis.util.SimpleTree;

public class JQueryTreeView<E> extends HtmlWidget
{
	private SimpleTree<JQueryTreeViewElement> treeData;
	
	private List<String> listOfMeasurements = new ArrayList<String>();
			
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
			returnString = "<li class=\"" + (nodeOpen(node, selectedLabels) ? "opened" : "closed") 
			 			+  "\"><span class=\"folder\">" + node.getLabel() + "</span>"
			 			+  "<ul>";
			
			Vector<JQueryTreeViewElement> children = node.getChildren();
			for (JQueryTreeViewElement child : children) {
				returnString += renderTree(child, selectedLabels);
			}
			returnString += "</ul></li>";
		} else {
			returnString = "<li id = \"" + node.getName().replaceAll(" ", "_") + "\"><span class=\"point\"><input type=\"checkbox\" id=\"" 
						  +	node.getLabel() + "\" name=\"" + node.getLabel() + "\"" 
						  +	(selectedLabels.contains(node.getLabel()) ? " checked=\"yes\"" : "") 
						  +	" />" + node.getLabel() + "</span></li>" 
						  +	"<script>createHashMap(\"" + node.getName() + "\",\"" + node.getHtmlValue() + "\")</script>";
				
			listOfMeasurements.add(node.getName());
		}
		
		return returnString;
	}
	
	public String toHtml(List<String> selected){
		String html ="<script src=\"res/jquery-plugins/datatables/js/jquery.js\"></script>\n" 
			+ "<script src=\"res/jquery-plugins/Treeview/jquery.treeview.js\" language=\"javascript\"></script>"
			+ "<script src=\"res/scripts/catalogue.js\" language=\"javascript\"></script>"
			+ "<link rel=\"stylesheet\" href=\"res/jquery-plugins/Treeview/jquery.treeview.css\" type=\"text/css\" media=\"screen\" />\n" 
			+ "<link rel=\"stylesheet\" href=\"res/css/catalogue.css\" type=\"text/css\" media=\"screen\" />\n" 
			+ "<script src=\"res/jquery-plugins/splitter/splitter.js\" language=\"javascript\"></script>"
			+ "<script>var map = new HashMap();" 
			+ "</script>" 
			//		"<div id=\"splitter\">" + //this div is used for the splitter
			//		"   <div> Left content goes here </div>"+
			//		"   <div> Right content goes here </div>"+
			//		" </div> "+
//			+ "<div id=\"masstoggler\">" 
//			+ "<a title=\"Collapse entire tree\" href=\"#\"><img src=\"res/img/toggle_collapse_tiny.png\"  style=\"vertical-align: bottom;\"></a> "
//			+ "<a title=\"Expand entire tree\" href=\"#\"><img src=\"res/img/toggle_expand_tiny.png\"  style=\"vertical-align: bottom;\"></a>" 
//			+ "</div><br/>"
			
			+ "<ul id=\"browser\" class=\"pointtree\">"
			+ renderTree(treeData.getRoot(), selected)
			+ "</ul>";	
		
	    
		// This piece of javascript need to be here because some java calls are needed.  
	    String measurementClickEvent = "<script>";
	    
	    for(String eachMeasurement : listOfMeasurements){
	    	measurementClickEvent += "$('#" + eachMeasurement.replaceAll(" ", "_") + "').click(function() {"
						    			+ "getHashMapContent(\"" + eachMeasurement + "\");});"
						    			+ "";
	    }
	    measurementClickEvent += "</script>";
	    
	    html += measurementClickEvent;
	    return html;
	}
	
	@Override
	public String toHtml(){
		return "";
	}
}