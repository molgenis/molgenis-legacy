package org.molgenis.framework.ui.html;


public class JQueryTreeView extends HtmlWidget
{

	public JQueryTreeView(String name)
	{
		super(name);
	}
	
	@Override
	public String toHtml(){
		
		String html = "<ul id=\"browser\" class=\"pointtree\">"
	 	+ "<li><span class=\"folder\">Folder 1</span>"
	 	+ "<ul>"
	 	+ "<li><span class=\"point\">Item 1.1</span></li>"
	 	+ "</ul>"
	 	+ "</li>"
	 	+ "<li><span class=\"folder\">Folder 2</span>"
	 	+ "<ul>"
	 	+ "<li><span class=\"folder\">Subfolder 2.1</span>"
	 	+ "<ul id=\"folder21\">"
	 	+ "<li><span class=\"point\">point 2.1.1</span></li>"
	 	+ "<li><span class=\"point\">point 2.1.2</span></li>"
	 	+ "</ul>"
	 	+ "</li>"
	 	+ "<li><span class=\"point\">point 2.2</span></li>"
	 	+ "</ul>"
	 	+ "</li>"
	 	+ "<li><span class=\"point\">Folder 3</span></li>"
	    + "</ul>";
	    
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
		+"$(\"#browser\").treeview();\n"
		+" $(\"#add\").click(function() {\n"
		+" 	var branches = $(\"<li><span class='folder'>New Sublist</span><ul>\" + \n"
		+" 		\"<li><span class='file'>Item1</span></li>\" + \n"
		+" 		\"<li><span class='file'>Item2</span></li>\" +\n"
		+" 		\"</ul></li>\").appendTo(\"#browser\");\n"
		+" 	$(\"#browser\").treeview({\n"
		+" 		add: branches\n"
		+" 	});\n"
		+" });\n"
		+"  });\n"
		+"</script>\n";
		
	    return html;
	}
}

