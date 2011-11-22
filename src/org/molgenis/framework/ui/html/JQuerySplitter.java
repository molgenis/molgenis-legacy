package org.molgenis.framework.ui.html;

import java.util.Vector;

import org.molgenis.util.Tree;

public class JQuerySplitter<E> extends HtmlWidget
{
	private String splitterName;
	private JQuerySplitterContents splitterContents;

	public JQuerySplitter(String name, JQuerySplitterContents contents)
	{
		super(name);
		
		this.setSplitterContents(new JQuerySplitterContents());
		this.setSplitterContents(contents);
		this.setSplitterName(name);
	}
	

	private String renderSplitter(JQuerySplitterContents contents) {
		String returnString = "";

			returnString = "<p>" + this.getSplitterName() + "</p>";
			returnString += "test>>>>>>>>>>" ; 
			returnString += contents; 

		return returnString;
	}
	
	
	
	@Override
	public String toHtml(){
		
		String html = "<ul id=\"browser\" class=\"pointtree\">";
		html += "</ul>";
	    html += "<script src=\"res/jquery-plugins/datatables/js/jquery.js\"></script>\n"
		+ "<link rel=\"stylesheet\" href=\"res/jquery-plugins/splitter/jquery.spliter.css\" type=\"text/css\" media=\"screen\" />\n" 
		+ "<script src=\"res/jquery-plugins/splitter/splitter.js\" language=\"javascript\"></script>"
		+ " <style type=\"text/css\">\n"
		+ "$(document).ready(function() {"
		+ "$(\"#splitterContainer\").splitter({minAsize:100,maxAsize:300,splitVertical:true,A:" 
		+ "$('#leftPane'),B:" 
		+ "$('#rightPane'),slave:" 
		+ "$(\"#rightSplitterContainer\"),closeableto:0});"
		+ "$(\"#rightSplitterContainer\").splitter({splitHorizontal:true,A:" 
		+ "$('#rightTopPane'),B:" 
		+ "$('#rightBottomPane'),closeableto:100});"
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


	public void setSplitterContents(JQuerySplitterContents splitterContents)
	{
		this.splitterContents = splitterContents;
	}


	public JQuerySplitterContents getSplitterContents()
	{
		return splitterContents;
	}


	public void setSplitterName(String splitterName)
	{
		this.splitterName = splitterName;
	}


	public String getSplitterName()
	{
		return splitterName;
	}
}

