package org.molgenis.framework.ui.html;


/**
 * Accordeon layout is a MultiPanel layout that shows multiple panels underneath
 * each other. A button can be used to show one of the panels.
 */
public class AccordeonLayout extends MultipanelLayout implements Layout
{
	public AccordeonLayout(String id)
	{
		super(id);
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		if (this.style == UiToolkit.DOJO)
		{
			return "<script type=\"text/javascript\">"
					+ "	dojo.require(\"dijit.layout.AccordionContainer\");"
					+ "</script>";
		}
		return "";
	}

	@Override
	public String render()
	{
		if (this.style == UiToolkit.DOJO)
		{
			String returnString = "<div class=\"claro\" dojoType=\"dijit.layout.AccordionContainer\" style=\"width: 300px; height: 300px\">";
			for (String title : elements.keySet())
			{
				returnString += "<div dojoType=\"dijit.layout.AccordionPane\" title=\""
						+ title + "\">";
				returnString += elements.get(title).render();
				returnString += "</div>";
			}
			returnString += "</div>";
			return returnString;
		}
		else if (this.style == UiToolkit.JQUERY)
		{
			String returnString = "<div class=\"jquery_accordion\">";

			for (String title : elements.keySet())
			{
				returnString += "<h3><a href=\"#\">" + title + "</a></h3><div>";
				returnString += elements.get(title).render();
				returnString += "</div>";
			}

			returnString += "</div><script>$(\".jquery_accordion\").accordion();</script>";
			return returnString;
		}
		else
		{
			return "ERROR";
		}
	}
}
