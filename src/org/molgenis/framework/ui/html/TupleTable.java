package org.molgenis.framework.ui.html;

import java.util.List;

import org.molgenis.util.Tuple;

public class TupleTable extends HtmlWidget
{
	List<Tuple> tuples;

	public TupleTable(String name, List<Tuple> tuples)
	{
		super(name);
		assert (tuples != null);
		this.tuples = tuples;
	}

	@Override
	public String toHtml()
	{
		String result = "<div><table id=\"" + getName() + "\"><thead><tr>";
		// header
		if (tuples.size() > 0) for (String name : tuples.get(0).getFields())
		{
			result += "<th>" + name + "</th>";
		}
		result += "</tr></thead><tbody>";
		// body
		for (Tuple t : tuples)
		{
			result += "<tr>";

			for (String name : t.getFields())
				result += "<td>" + (t.isNull(name) ? "" : t.getString(name)) + "</td>";

			result += "</tr>";
		}
		result += "</tbody></table><script>$('#"
				+ getName()
				+ "').dataTable({'bJQueryUI': true,'sPaginationType': 'full_numbers','sScrollX': '100%','bScrollCollapse': true});</script></div>";

		return result;
	}

}
