package org.molgenis.framework.ui.html;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.molgenis.util.Entity;

public class EntityTable extends HtmlWidget
{
	List<? extends Entity> entities = null;
	List<String> columns = null;
	private boolean editButton = false;

	public EntityTable(List<? extends Entity> entities, boolean editButton, String... columns)
	{
		super(UUID.randomUUID().toString());

		this.entities = entities;
		this.editButton  = editButton;
		
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
	
	/**
	 * A easy way to create a Molgenis-style table in your plugin. 
	 * 
	 * 1.Get a list of entities that you want to show in the table
	 * List<Measurement> listOfMeasurements = db.find(Measurement.class, 
	 *      new QueryRule(Measurement.INVESTIGATION_NAME, Operator.EQUAL, "inv"));
	 * 2.Create a new instance of EntityTable and pass the list of measurements as first parameters. 
	 * The second parameter indicates whether to show the editButton in the table.
	 * The rest of parameters are the column that you want to show in the table.
	 * String htmlTable = new EntityTable(listOfMeasurements, true, Measurement.NAME, 
	 *      Measurement.DESCRIPTION, Measurement.DATATYPE, Measurement.TEMPORARY).toHtml();
	 * 3.Optional, if u have selected the editButton to be true, you`ll need to get the entity 
	 * in your plugin and follow the code below:
	 * 
	 * htmlTable = new MeasurementForm(measurement).getHtml();
	 */
	
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
			htmlTable +="<tr class=\"form_listrow" + color + "\" id=\"" + e.getIdValue() + "\">";
			if(color == 0) color = 1; else color = 0;
			for(String column: this.columns)
			{
				htmlTable +="<td>"+e.get(column)+"</td>";
			}
			if(editButton == true){
				htmlTable += "<td><img class=\"edit_button\" src=\"generated-res/img/editview.gif\" title=\"edit record\"" +
					     "onclick=\"submitFormMethod('" + e.getIdValue() + "');\"" +
					     "></td>";
			}
			htmlTable += "</tr>";
		}
		htmlTable += "</table>";
		
		return htmlTable;

	}

}
