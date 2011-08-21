package org.molgenis.framework.ui.html;

public class VerticalLayout extends FlowLayout
{
	public String render()
	{
		String returnString = "";
		for (HtmlElement i : this.getElements())
		{
			if (i instanceof HtmlInput<?>)
			{
				HtmlInput<?> input = (HtmlInput<?>) i;
				returnString += "<label>" + input.getLabel() + "</label><br/>"
						+ input.toHtml() + "<br/>";
				if (!"".equals(input.getDescription()))
				{
					returnString += "<div class=\"molgenis_help\">"
							+ input.getDescription() + "</div><br/>";
				}
			}
			else
			{
				returnString += i.render() + "<br/>";
			}

		}
		return returnString;
	}

}
