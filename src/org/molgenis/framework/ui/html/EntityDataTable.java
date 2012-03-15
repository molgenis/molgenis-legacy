package org.molgenis.framework.ui.html;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.fieldtypes.MrefField;
import org.molgenis.fieldtypes.XrefField;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.elements.Field;
import org.molgenis.util.Entity;

/**
 * DataTable for listing entity via an AJAX list view, incl search. 
 *
 * To get this to work you also need to install the related molgenisservice. In your properties say:<br/>
 * org.molgenis.framework.server.services.MolgenisDataTableService@/datatable
 */
public class EntityDataTable extends HtmlWidget
{
	String klazzName;
	org.molgenis.model.elements.Entity entityModel;

	/**
	 * Constructor
	 * 
	 * @param unique
	 *            name
	 * @param class of the entity to be shown
	 * @throws DatabaseException
	 */
	public EntityDataTable(String name, Class<? extends Entity> entityClass, Database db) throws DatabaseException
	{
		super(name);
		entityModel = db.getMetaData().getEntity(entityClass.getSimpleName());
		klazzName = entityClass.getName();
	}

	@Override
	public String toHtml()
	{
		List<String> labels = new ArrayList<String>();

		try
		{
			boolean first = false;
			for (Field field : entityModel.getAllFields())
			{
				// in this case use label
				if (field.getType() instanceof XrefField || field.getType() instanceof MrefField)
				{
					for (String label : field.getXrefLabelNames())
					{
						labels.add(field.getName() + "_" + label);
					}
				}
				else
				{
					labels.add(field.getName());
				}
			}
		}
		catch (Exception e)
		{

		}

		String result = "<table id=\"" + getName() + "\"><thead>";
		for (String label : labels)
			result += "<td>" + label + "</td>";
		result += "</thead></table>";
		result += "\n<script>";
		result += "var oTable = $('#" + getName() + "').dataTable({";
		result += "\n	'bJQueryUI' : true,";
		result += "\n	'bServerSide' : true,";
		result += "\n	'sAjaxSource' : 'datatable',";
		result += "\n	'bPagination' : true,";
		result += "\n	'sScrollX': '100%',";
		result += "\n	'bScrollCollapse': true,";
		result += "\n	'bProcessing' : true,";
		result += "\n	'aoColumns' : [";
		for (String label : labels)
		{
			if(label != labels.get(0)) result +=",";
			result += "{ 'mDataProp' : '" + label + "'}";
		}
		result += "	],";
		result += "	'fnServerParams' : function(aoData) {";
		result += "\n	aoData.push({";
		result += "\n		'name' : 'entity',";
		result += "\n		'value' : '" + klazzName + "'";
		result += "\n	});";
		result += "\n}";
		result += "});";
		result += "\nnew FixedColumns(oTable, {'iLeftWidth' : 200});";
		result += "</script>";
		return result;
	}
}
