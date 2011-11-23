package org.molgenis.framework.ui.html;

public class JQuerySplitter<E> extends HtmlWidget
{
	private String splitterName;
	private JQuerySplitterContents splitterContents = null;

	public JQuerySplitter(String name, JQuerySplitterContents contents) {
		super(name);
		
		this.setSplitterContents(contents);
		this.setSplitterName(name);
	}
	

	
	@Override
	public String toHtml(){
		
		String html = "<script type=\"text/javascript\" src=\"res/jquery-plugins/datatables/js/jquery.js\"></script>\n";
		html += "<script type=\"text/javascript\" src=\"res/jquery-plugins/splitter/splitter.js\"></script>";
		//html += "<style type=\"text/css\" media=\"all\">";
	    html += "<link rel=\"stylesheet\" href=\"res/jquery-plugins/splitter/jquery.spliter.css\" type=\"text/css\" media=\"screen\" />\n" ;
	    html += "<script>";
		html += "$(document).ready(function() { \n";
	    html += "$(\"#splitterContainer\").splitter({minAsize:100,maxAsize:300,splitVertical:true,A:" ;
	    html += "$('#leftPane'),B:" ;
		html += "$('#rightPane'),slave:" ;
		html += "$(\"#rightSplitterContainer\"),closeableto:0})";
		html += "$(\"#rightSplitterContainer\").splitter({splitHorizontal:true,A:"; 
		html += "$('#rightTopPane'),B:";
		html += "$('#rightBottomPane'),closeableto:100});";
		html += "});";
	    html += "</script>\n";
		
	    return html;
	}


	public void setSplitterContents(JQuerySplitterContents contents)
	{
		splitterContents = contents;
	}


	public JQuerySplitterContents getSplitterContents()
	{
		return splitterContents;
	}


	public void setSplitterName(String name)
	{
		splitterName = name;
	}


	public String getSplitterName()
	{
		return splitterName;
	}
}

