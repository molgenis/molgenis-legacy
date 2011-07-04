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

	// Parameter to indicate whether this MrefInput should have an 'Add new ...'
	// button attached to it.
	private boolean includeAddButton = false;
	private ActionInput addButton = new ActionInput("add", "", "");

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
		XrefInput input = null;
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

		if (isHidden() || isReadonly())
		{
			return "<div id=\"" + getName() + "\">" + html.toString()
					+ "</div>";
		}
		else
		{

			String buttons = String
					.format(
							"<button style=\"\" type=\"button\" onclick=\"mref_addInput('%s','%s','%s','%s','%s',this.parentNode);\">+</button>",
							getName(), getXrefEntity(), getXrefField(),
							getXrefLabels().get(0), getXrefFilterRESTString());
			buttons += "<button type=\"button\" onclick=\"mref_removeInput(this.parentNode);\">-</button>";

			return "<div id=\"" + getName() + "\">" + html.toString() + buttons
					+ (includeAddButton ? this.addButton : "") + "</div>";
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

	public void setIncludeAddButton(boolean includeAddButton)
	{
		this.includeAddButton = includeAddButton;
	}

	public void setAddButton(ActionInput addButton)
	{
		this.addButton = addButton;
	}

	@Override
	public String toHtml(Tuple params) throws HtmlInputException
	{
		return new MrefInput(params).render();
	}
}
