package plugins.LLcatalogueTree;

import java.util.Vector;

import org.molgenis.framework.ui.html.JQueryTreeView;
import org.molgenis.framework.ui.html.JQueryTreeViewElement;
import org.molgenis.util.SimpleTree;

public class JQueryTreeViewMeasurement<E> extends JQueryTreeView<E>{

	private SimpleTree<JQueryTreeViewElementMeasurement> treeData;
	
	public JQueryTreeViewMeasurement(String name, SimpleTree treeData) {
		super(name, treeData);
		this.treeData = treeData;
		// TODO Auto-generated constructor stub
	}
	
	public SimpleTree<JQueryTreeViewElementMeasurement> getTree (){
		return treeData;
	}
	
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
			returnString = "<li><span class=\"point\">" + node.getLabel() + "</span></li>";
		}
		return returnString;
	}
	
	@Override
	public String toHtml(){
		
		String html = "<ul id=\"browser\" class=\"pointtree\">";
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
//		+"$(\"#browser\").treeview({collapsed: true});});\n"
		+"$(\"#browser\").treeview(" 
		+");});\n"
		+"$(\"a\").click(function(event) {" 
		+"$('#test').attr(\"value\", $(this).text());"
		+"	  alert($('#test').val());"
//		+"		alert('Tree element clicked!!');"
		+"		$(\"#bottom\").html('This is the bottom of the screen and has been replaced with this content.');"
		+"		$(\"#rightSide\").html($(this).text());"//.html('"; 
		+"		$(\"#broswer #a\");"
		//html+=	getMeasurementDetails();
		//html+="');"				
		+"	});"
		+"</script>\n";
	    
	    return html;
	}
}
