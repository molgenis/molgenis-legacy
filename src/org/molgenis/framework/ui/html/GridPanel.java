package org.molgenis.framework.ui.html;

public class GridPanel extends TablePanel
{
	private int columns = 1;

	public GridPanel()
	{
		super();
	}
	
	public GridPanel(int columns)
	{
		this.columns  = columns;
	}

	public void setColumns(int columns)
	{
		this.columns = columns;
	}

	@Override
	public String toHtml()
	{
		String result = "<table>\n";
		int cell      = 0;

		for (HtmlInput i : this.inputs.values())
		{
			if (i.isHidden())
				continue;

			if (cell % columns == 0)
			{
				if (cell != 0)
					result += "</tr>\n";
				result += "<tr>";
			}

			result += ("<td>" + i.getLabel() + "</td><td>" + i.toHtml() + "</td>");
			
			cell++;
		}
		
		if (this.inputs.size() > 0)
			result += "</tr>\n";

		result += "</table>\n";

		return result;
	}
}
