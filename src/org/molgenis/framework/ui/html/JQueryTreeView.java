package org.molgenis.framework.ui.html;

import java.util.Vector;

import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tree;


public class JQueryTreeView<E> extends HtmlWidget
{
	private SimpleTree<JQueryTreeViewElement> treeData;

	public JQueryTreeView(String name, SimpleTree treeData)
	{
		super(name);
		this.treeData = treeData;
	}
	
	/**
	 * 
	 * No Nodes collapsed explicitly. Node is manually closed then by giving its LI element a "closed"(/opened) CSS class.
	 * Animation enabled, speed is "normal".
	 * "Cookie" persistence enabled, causing the current tree state to be persisted.
	 *	Dynamically adding a sub tree to the existing tree demonstrated.
	 */
	private String renderTree(JQueryTreeViewElement node) {
		String returnString;
		if (node.hasChildren()) {
			returnString = "<li class=\"closed\"><span class=\"folder\">" + node.getLabel() + "</span>";
			returnString += "<ul>";
			Vector<JQueryTreeViewElement> children = node.getChildren();
			for (JQueryTreeViewElement child : children) {
				returnString += renderTree(child);
			}
			returnString += "</ul></li>";
		} else {
			returnString = "<li><span class=\"point\"><input type=\"checkbox\" id=\"" + 
				node.getLabel() + "\" name=\"" + node.getLabel() + "\" />" + node.getLabel() + "</span></li>";
		}
		return returnString;
	}
	
	@Override
	public String toHtml(){
		
		String html = "<div id=\"masstoggler\">	<a title=\"Collapse entire tree\" href=\"#\">Collapse All</a> | ";
		html += "<a title=\"Expand entire tree\" href=\"#\">Expand All</a>" ;
		//html += " | <a title=\"Toggle the tree below\" href=\"#\">Toggle All</a></div> ";
		html += "<ul id=\"browser\" class=\"pointtree\">";
		html += renderTree(treeData.getRoot());
		html += "</ul>";
	    html += "<script src=\"res/jquery-plugins/datatables/js/jquery.js\"></script>\n"
		+ "<link rel=\"stylesheet\" href=\"res/jquery-plugins/Treeview/jquery.treeview.css\" type=\"text/css\" media=\"screen\" />\n" 
		+ "<script src=\"res/jquery-plugins/Treeview/jquery.treeview.js\" language=\"javascript\"></script>"
		+ " <style type=\"text/css\">\n"
		+ "#browser {\n"
		+ "font-family: Verdana, helvetica, arial, sans-serif;\n"
		+ "font-size: 68.75%;\n"
		+"}\n"
		+"</style>\n"
		+"<script>\n"
		+"$(document).ready(function(){\n"
		+"$(\"#browser\").treeview({control: \"#masstoggler\"});" 
		+"});\n"
//		+ "$(document).unload(function() {"
//		+ "alert('Handler for .unload() called.');"
//		+ "});"
		+"</script>\n";
	    
	    return html;
	}
}

