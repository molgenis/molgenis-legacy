package org.molgenis.datatable.view;

import java.util.List;

import org.molgenis.datatable.model.TableException;
import org.molgenis.datatable.model.TupleTable;
import org.molgenis.framework.ui.html.HtmlWidget;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Tuple;

public class HtmlTableView extends HtmlWidget
{
	private TupleTable table;

	public HtmlTableView(String name, TupleTable table)
	{
		super(name, name);
		
		this.table = table;
	}

	@Override
	public String toHtml()
	{
		List<Field> columns;
		try
		{
			columns = table.getColumns();
		}
		catch (TableException e)
		{
			throw new RuntimeException(e);
		}
		
		String result = "\n<table id=\""+getId()+"\">";
		
		//header
		result += "\n\t<thead><tr>";
		for(Field f: columns)
		{
			result += "\n\t\t<td>"+f.getLabel()+"</td>";
		}
		result += "\n\t\t</tr></thead>\n\t<tbody>";
		
		//rows
		for(Tuple row: table)
		{
			result += "\n\t\t<tr>";
			for(Field f: columns)
			{
				//todo: what about row.getObject() instanceof List?
				
				result += "\n\t\t\t<td>"+(row.isNull(f.getName()) ? "&nbsp;" : row.getString(f.getName()))+"</td>";
			}
			result += "\n\t\t</tr>";
		}
		
		result += "\n\t</tbody>\n</table>";
		
		return result;
	}

}
