package org.molgenis.framework.ui.html;

import java.util.Map;

public class TabbedLayout extends MultipanelLayout
{
	public enum Position
	{
		TOPLEFT, TOPRIGHT, LEFTTOP
	};

	public TabbedLayout(String id)
	{
		super(id);
	}

	public void addHyperlink(String name, String hyperlink)
	{
		super.add(new HyperlinkInput(name, hyperlink));
	}

	public String render()
	{
		Map<String, HtmlElement> elements = this.getElements();

		String result = "<div id=\"" + this.getId() + "\">";

		// create tabs
		result += "<ul>";
		int i = 0;
		for (String label : elements.keySet())
		{
			if (elements.get(label) instanceof HyperlinkInput) result += "<li><a href=\""
					+ ((HyperlinkInput) elements.get(label)).getValue() + "\">" + label + "</a></li>";
			else
				result += "<li><a href=\"#" + getId() + "-" + (i++) + "\">" + label + "</a></li>";
		}
		result += "</ul>";

		// create bodies
		i = 0;
		for (String label : elements.keySet())
		{
			result += "<div id=\"" + getId() + "-" + (i++) + "\">" + elements.get(label).render() + "</div>";
		}

		result += "</div>";

		result += "</div><script>$(\"#" + getId() + "\").tabs();</script>";

		return result;
	}

}
