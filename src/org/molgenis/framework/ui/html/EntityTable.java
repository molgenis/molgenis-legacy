package org.molgenis.framework.ui.html;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.molgenis.util.Entity;

public class EntityTable extends HtmlWidget
{
	List<? extends Entity> entities = null;
	List<String> columns = null;

	public EntityTable(List<? extends Entity> entities, boolean editButton, String... columns)
	{
		super(UUID.randomUUID().toString());

		this.entities = entities;
		
		if(columns != null && columns.length > 0)
		{
			this.columns = Arrays.asList(columns);
		}
		else
		{
			for(Entity e: entities)
			{
				this.columns = e.getFields();
				break;
			}
		}
	}

	@Override
	public String toHtml()
	{
		String htmlTable = "<table class=\"listtable\">";

		//create header
		htmlTable += "<thead><tr>";
		for(String column: this.columns)
		{
			htmlTable += "<td>"+column+"</td>";
		}
		htmlTable +="</tr></thead>";
		
		//render records
		int color = 0;
		for(Entity e: entities)
		{
			htmlTable +="<tr class=\"form_listrow"+color+"\">";
			if(color == 0) color = 1; else color = 0;
			for(String column: this.columns)
			{
				htmlTable +="<td>"+e.get(column)+"</td>";
			}
			htmlTable +="</tr>";
		}
		htmlTable += "</table>";
		
		return htmlTable;

	}

}
