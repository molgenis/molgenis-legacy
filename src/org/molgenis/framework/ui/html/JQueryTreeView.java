package org.molgenis.framework.ui.html;

import java.util.Vector;

import org.molgenis.util.SimpleTree;
import org.molgenis.util.Tree;


public class JQueryTreeView<E> extends HtmlWidget
{
	private SimpleTree<JQueryTreeViewElement> treeData;
	private String MeasurementDetails;

	public JQueryTreeView(String name, SimpleTree treeData)
	{
		super(name);
		System.out.println(">>>>The splkas been created"); 

		this.treeData = treeData;
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
		+"$(\"#browser\").treeview();});\n"
		+ "$(\"a\").click(function(event) {"
		+"	  event.preventDefault();"
		+"	  alert($(this).text());"
//		+"		alert('Tree element clicked!!');"
		+"		$(\"#bottom\").html('This is the bottom of the screen and has been replaced with this content.');"
		+"		$(\"#rightSide\").html($(this).text());"//.html('"; 
		//html+=	getMeasurementDetails();
		//html+="');"				
		+"	});"
		+"</script>\n";
	    
	    return html;
	}

	private String getMeasurementDetails()
	{
		return MeasurementDetails;
	}

	public void setMeasurementDetails(String measurementDetails)
	{
		MeasurementDetails = measurementDetails;
	}
	
	
	
}

