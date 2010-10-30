package org.molgenis.framework.ui.html;

import java.io.StringWriter;
import java.util.Vector;

/**
 * Input for varchar data.
 */
public class EmbeddedInput extends HtmlInput
{
	public EmbeddedInput(String name, Vector<Vector<HtmlInput>> value)
	{
		super( name, value );
	}

	@Override
	public String toHtml()
	{
		Vector<Vector<HtmlInput>> records = (Vector<Vector<HtmlInput>>)this.getObject();
		StringWriter html = new StringWriter();
		int count = 1;

		if (records.size() > 0)
		{
			html.append("<table class=\"embedtable\">");
		
			html.append("<tr>");
			for (HtmlInput input : records.get(0))
			{
				html.append("<td><b>"+input.getLabel()+"</b></td>");
			}
			html.append("</tr>");
		
			for (Vector<HtmlInput> row : records)
			{
				html.append("<tr class=\"form_listrow"+(count%2)+"\">");
				for (HtmlInput input : row)
				{
					html.append("<td>"+input.getValue()+"</td>");
				}
				html.append("</tr>");
				count++;
			}
			html.append("</table>");
		}
			
		return html.toString();
	}

	@Override
	public String getHtmlValue()
	{
		return this.toHtml();
	}

}
