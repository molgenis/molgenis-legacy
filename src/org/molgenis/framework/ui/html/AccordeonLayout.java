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
		return "";
	}

	@Override
	public String render()
	{
		if (this.style == UiToolkit.JQUERY)
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
