package org.molgenis.framework.ui.html;


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

	@Override
	public String toHtml(){
		
//<<<<<<< .mine
//		String html = "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.js\"></script>\n";
//		html += "<script type=\"text/javascript\" src=\"res/jquery-plugins/splitter/splitter.js\"></script>";
//		//html += "<style type=\"text/css\" media=\"all\">";
//	    html += "<link rel=\"stylesheet\" href=\"res/jquery-plugins/splitter/jquery.spliter.css\" type=\"text/css\" media=\"screen\" />\n" ;
//	    html += "<script>";
//		
//	    html += "$(document).ready(function() { \n";
//	    html += "$(\"#splitterContainer\").splitter({minAsize:100,maxAsize:300,splitVertical:true,A:" ;
//	    html += "$('#leftPane'),B:" ;
//		html += "$('#rightPane'),slave:" ;
//		html += "$(\"#rightSplitterContainer\"),closeableto:0})";
//		html += "$(\"#rightSplitterContainer\").splitter({splitHorizontal:true,A:"; 
//		html += "$('#rightTopPane'),B:";
//		html += "$('#rightBottomPane'),closeableto:100});";
//		html += "});";
//
////	    html += "$(document).ready(function() { \n";
////	    html += "$(\"#splitterContainer\").splitter();"; 
//	    html += "</script>\n";
//=======
		
		String html = 
		  "<div id=\"splitterContainer\">"
		+ "	<div id=\"leftPane\">"
		+ "  <p>huyabsvfhvds</p>"
		//+  		splitterContents.getLeftPane()
		+ "	</div>"
		+ " <!-- #leftPane -->"
		+ " <div id=\"rightPane\">"
		+ "  <p>huyabsvfhvds</p>"
//		+ "	<div style=\"height:5%;background:#bac8dc\">Toolbar?</div>"
//		+ "		<div id=\"rightSplitterContainer\" style=\"height:95%\">"
//	    + "			<div id=\"rightTopPane\">"
//		+  				splitterContents.getRightTopPane()
//		+ "			</div>"
//		+ "			<div id=\"rightBottomPane\">"
//		+ "				<div>"
//		+  					splitterContents.getRightBottomPane()
//		+ "				</div>"
//		+ "			</div>"
//		+ "		</div>"
//		+ "	</div>"
		+ " </div>"
		+ "</div>"
		+ "<script src=\"res/jquery-plugins/datatables/js/jquery.js\"></script>\n"
		+ "<link rel=\"stylesheet\" href=\"res/css/spliter.css\" type=\"text/css\" />\n" 
		+ "<script src=\"res/jquery-plugins/splitter/splitter.js\" language=\"javascript\"></script>\n"
		+ "<script>\n"
		+ "$(document).ready(function() {"
		+ "$(\"#splitterContainer\").splitter({minAsize:100,maxAsize:300,splitVertical:true,A:$(\"#leftPane\"),B:$(\"#rightPane\"),closeableto:0});"
		//+ "$(\"#rightSplitterContainer\").splitter({splitHorizontal:true,A:$('#rightTopPane'),B:$('#rightBottomPane'),closeableto:100});"
		+ "});"
		+ "</script>\n";

	    
	    
		
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

