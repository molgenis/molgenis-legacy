/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-07, 1.0.0, DI Matthijssen
 * <li>2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li>2006-05-14; 1.2.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

// jdk
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Input for many-to-many cross-references (xref) to choose data entities from
 * the database. Selectable data items will be shown as selection box and are
 * loaded dynamically via an 'ajax' service.
 */
public class MrefInput extends EntityInput<List<? extends Entity>>
{
	public static final String VALUES = "values";

	// what is this?
	private String targetfield;

	/** Minimal constructor */
	public MrefInput(String name, Class<? extends Entity> xrefEntityClass,
			List<? extends Entity> dummyList)
	{
		super(name, xrefEntityClass, dummyList);
	}

	/**
	 * Alternative minimal constructor using an entity object instance to
	 * configure all.
	 */
	public MrefInput(String name, List<? extends Entity> objects)
	{
		this(name, objects.get(0).getClass(), objects);
	}

	/** Alternative minimal constructor using an entity class to configure all. */

	public MrefInput(String name, Class<? extends Entity> xrefEntityClass)
	{
		super(name, xrefEntityClass, null);
	}

	/**
	 * Alternative minimal constructor using entity name
	 * 
	 * @throws HtmlInputException
	 * 
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 */
	public MrefInput(String name, String entityName) throws HtmlInputException
	{
		super(name, entityName);
	}

	/** Complete constructor */
	public MrefInput(String name, String label, List<Entity> values,
			Boolean nillable, Boolean readonly, String description,
			Class<? extends Entity> xrefEntityClass)
	{
		super(name, label, values, nillable, readonly, description,
				xrefEntityClass);
	}

	/**
	 * Alternative complete constructor using String name of entityClass
	 * 
	 * @throws HtmlInputException
	 */
	public MrefInput(String name, String label, List<Entity> values,
			Boolean nillable, Boolean readonly, String description,
			String xrefEntityClass) throws HtmlInputException
	{
		super(name, label, values, nillable, readonly, description,
				xrefEntityClass);
	}

	/**
	 * Constructor taking parameters from tuple
	 * 
	 * @throws HtmlInputException
	 */
	@SuppressWarnings("unchecked")
	public MrefInput(Tuple t) throws HtmlInputException
	{
		super(t);
		if (!t.isNull(VALUE)) this.setValue((List<Entity>) t.getList(VALUE));
		if (!t.isNull(VALUES)) this.setValue((List<Entity>) t.getList(VALUES));
	}

	protected MrefInput()
	{
		super();
	}

	@Override
	public String toHtml()
	{
		if (this.error != null) return "ERROR: " + error;

		// BIG FIXME we have to enable nillable checkin on this one
		this.setNillable(true);

		List<? extends Entity> values = getObject();
		if (values == null) values = new ArrayList<Entity>();

		// template of an xref dialog
		EntityInput<Entity> input = null;
		try
		{
			input = new XrefInput(this.getName(), this.getXrefEntity());
		}
		catch (HtmlInputException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		input.setReadonly(this.isReadonly());
		input.setStyle("display: block;");
		input.setHidden(this.isHidden());
		input.setIncludeAddButton(false);

		// add currently known values
		StringBuffer html = new StringBuffer();

		for (int i = 0; i < values.size(); i++)
		{
			input.setValue(values.get(i));
			input.setId(this.getName() + i);
			html.append(input.toHtml());
		}

		// add an empty one if none is shown
		if (values.size() == 0)
		{
			input.setValue(null);
			// html.append(input.toHtml()+"<br>");
		}

		if (uiToolkit == UiToolkit.ORIGINAL)
		{
			if (isHidden() || isReadonly())
			{
				return "<div id=\"" + getName() + "\">" + html.toString()
						+ "</div>";
			}
			else
			{
				//String xrefLabelString = this.toCsv(getXrefLabels());

				String buttons = String
						.format(
								"<button style=\"\" type=\"button\" onclick=\"mref_addInput('%s','%s','%s','%s','%s',this.parentNode);\">+</button>",
								getName(), getXrefEntity(), getXrefField(),
								getXrefLabels().get(0),
								getXrefFilterRESTString());
				buttons += "<button type=\"button\" onclick=\"mref_removeInput(this.parentNode);\">-</button>";

				return "<div id=\"" + getName() + "\">" + html.toString()
						+ buttons 
						+ "</div>"+ (includeAddButton && !this.isReadonly() ? this.createAddButton() : "");
			}
		}
		else if (uiToolkit == UiToolkit.JQUERY)
		{
			return this.toJquery();
		}
		else
		{
			return uiToolkit + " NOT IMPLEMENTED IN MREFINPUT";
		}
	}

	@SuppressWarnings("unchecked")
	private String toJquery()
	{
		String options = "";
		String xrefLabelString = this.toCsv(getXrefLabels());
		String description = getName().equals(getDescription()) ? "" : " title=\""+getDescription()+"\"";


		if(this.getObject()!= null) for (Entity e : (List<Entity>) this.getObject())
		{
			options += "<option selected=\"selected\" value=\""
					+ e.getIdValue() + "\">" + e.getLabelValue()
					+ "</option>";
		}

		String name;
		try
		{
			name = getEntityClass(getXrefEntity())
					.getSimpleName();

			String readonly = this.isReadonly() ? "readonly " : "";
			
			return "<select multiple=\"multiple\" "+readonly+" data-placeholder=\"Choose some "
					+ name
					+ "\" class=\""+readonly+"ui-widget-content ui-corner-all\" id=\""
					+ this.getId()
					+ "\" name=\""
					+ this.getName()
					+ "\" "
					+ " style=\"width:350px;\" "+ description+">\n"
					+ options 
					+ "</select>"
					+ "\n<script>$(\"#"
					+ this.getId()
					+ "\").ajaxChosen("
					+ "\n{ "
					+ "\n	method: 'GET', "
					+ "\n	url: 'xref/find',"
					+ "\n	xref_entity: '"
					+ this.getXrefEntity()
					+ "',"
					+ "\n	xref_field: '"
					+ this.getXrefField()
					+ "',"
					+ "\n	xref_label: '"
					+ xrefLabelString
					+ "',"
					+ "\n	dataType: 'json', "
					+ "\n},"
					+ "\nfunction (data) {"
					+ "\n	var terms = {}; "
					+ "\n	$.each(data, function (i, val) {terms[i] = val;});"
					+ "\n	return terms;" + "\n});" + "\n</script>\n"
					+ (includeAddButton && !this.isReadonly() ? this.createAddButton() : "");

		}
		catch (HtmlInputException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "ERROR";
		}
	}

	@Override
	/**
	 * Note, this returns the labels of the selected values.
	 */
	public String getValue()
	{
		String result = "";
		for (Entity value : getObject())
		{
			if (result.toString().equals("")) result += value.getLabelValue();
			else
				result += ", " + value.getLabelValue();
		}
		return result;

		// int size = 0;
		// for (String label : this.getXrefLabels())
		// {
		// if (this.getValueLabels(label) != null)
		// {
		// size = Math.max(size, this.getValueLabels(label).size());
		// }
		// }
		//
		// for (int i = 0; i < size; i++)
		// {
		// String valueLabel = "";
		// for (String labelName : this.getXrefLabels())
		// {
		// String value = this.getValueLabels(labelName) != null
		// && i < this.getValueLabels(labelName).size()
		// && this.getValueLabels(labelName).get(i) != null ? this
		// .getValueLabels(labelName).get(i).toString() : "";
		// if (valueLabel.toString().equals("")) valueLabel += value;
		// else
		// valueLabel += ":" + value;
		// }
		// result += ", " + valueLabel;
		// }
		//
		// return result.replaceFirst(", ", "");
	}

	public String getTargetfield()
	{
		return targetfield;
	}

	public void setTargetfield(String targetfield)
	{
		this.targetfield = targetfield;
	}

	@Override
	public String toHtml(Tuple params) throws HtmlInputException
	{
		return new MrefInput(params).render();
	}

	private String toCsv(List<String> xrefLabels)
	{
		String result = "";
		for (String label : xrefLabels)
		{
			result += "," + label;
		}

		return result.replaceFirst(",", "");
	}
}
