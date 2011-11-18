package org.molgenis.framework.ui.html;

public class JQuerySplitter<E> extends HtmlWidget
{
	private String contents;

	public JQuerySplitter(String name)
	{
		super(name);
	}
	
	private String renderSplitter(String contents) {
		String returnString = "";
		
		return returnString;
	}
	
	@Override
	public String toHtml(){
		
		String html = "<ul id=\"browser\" class=\"pointtree\">";
		html += renderSplitter(contents);
		html += "</ul>";
	    html += "<script src=\"res/jquery-plugins/datatables/js/jquery.js\"></script>\n"
		+ "<link rel=\"stylesheet\" href=\"res/jquery-plugins/splitter/jquery.spliter.css\" type=\"text/css\" media=\"screen\" />\n" 
		+ "<script src=\"res/jquery-plugins/splitter/splitter.js\" language=\"javascript\"></script>"
		+ " <style type=\"text/css\">\n"
		+ "$(document).ready(function() {"
		+ "$(\"#splitterContainer\").splitter({minAsize:100,maxAsize:300,splitVertical:true,A:$('#leftPane'),B:$('#rightPane'),slave:$(\"#rightSplitterContainer\"),closeableto:0});"
		+ "$(\"#rightSplitterContainer\").splitter({splitHorizontal:true,A:$('#rightTopPane'),B:$('#rightBottomPane'),closeableto:100});"
		+ "});"
		+ "<div id=\"splitterContainer\">"
		+ "	<div id=\"leftPane\">"
		+  		JQuerySplitterContents.getLeftPane()
		+ "	</div>"
		+ "<!-- #leftPane -->"
		+ "<div id=\"rightPane\">"
		+ "	<div style=\"height:5%;background:#bac8dc\">Toolbar?</div>"
		+ "		<div id=\"rightSplitterContainer\" style=\"height:95%\">"
	    + "			<div id=\"rightTopPane\">"
		+  				JQuerySplitterContents.getRightTopPane()
		+ "			</div>"
		+ "			<div id=\"rightBottomPane\">"
		+ "				<div>"
		+  					JQuerySplitterContents.getRightBottomPane()
		+ "				</div>"
		+ "			</div>"
		+ "		</div>"
		+ "	</div>"
		+ "</div>";

		
	    return html;
	}
}

