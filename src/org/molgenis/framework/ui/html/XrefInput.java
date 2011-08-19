/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li>2006-03-07, 1.0.0, DI Matthijssen
 * <li>2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li>2006-05-14; 1.2ƒ.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

// jdk
import java.text.ParseException;
import java.util.List;

import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

/**
 * Input for cross-reference (xref) entities in a MOLGENIS database. Data will
 * be shown as selection box. Use xrefEntity to specifiy what entity provides
 * the values for selection. Use xrefField to define which entity field to use
 * for the values. Use xrefLabels to select which field(s) should be shown as
 * labels to the user (optional).
 */
public class XrefInput extends EntityInput<Entity>
{
	// Parameter to indicate whether this XrefInput should have an 'Add new ...'
	// button attached to it.
	private boolean includeAddButton = false;
	private ActionInput addButton = new ActionInput("add", "", "");

	/** Minimal constructor */
	public <E extends Entity> XrefInput(String name, Class<? extends Entity> xrefEntityClass,
			E value)
	{
		super(name, xrefEntityClass, value);
	}

	/**
	 * Alternative minimal constructor using an entity object instance to
	 * configure all.
	 */
	public <E extends Entity> XrefInput(String name, E object)
	{
		super(name, object.getClass(), object);
	}

	/** Alternative minimal constructor using an entity class to configure all. */

	public XrefInput(String name, Class<? extends Entity> xrefEntityClass)
	{
		super(name, xrefEntityClass, null);
	}

	/**
	 * Alternative minimal constructor using entity name
	 * 
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 * @throws ClassNotFoundException
	 */
	public XrefInput(String name, String entityName) throws HtmlInputException
	{
		super(name, entityName);
	}

	/** Complete constructor */
	public XrefInput(String name, String label, Entity value, Boolean nillable,
			Boolean readonly, String description,
			Class<? extends Entity> xrefEntityClass)
	{
		super(name, label, value, nillable, readonly, description,
				xrefEntityClass);
	}

	/** Alternative complete constructor using String name of entityClass */
	public XrefInput(String name, String label, Entity value, Boolean nillable,
			Boolean readonly, String description, String xrefEntityClass)
			throws HtmlInputException
	{
		super(name, label, value, nillable, readonly, description,
				xrefEntityClass);
	}

	/**
	 * Constructor taking parameters from tuple
	 * 
	 * @throws HtmlInputException
	 */
	public XrefInput(Tuple t) throws HtmlInputException
	{
		super(t);
	}

	protected XrefInput()
	{
	}

	@Override
	public String toHtml()
	{
		if (this.error != null) return "ERROR: " + error;

		if ("".equals(getXrefEntity()) || "".equals(getXrefField())
				|| getXrefLabels() == null || getXrefLabels().size() == 0)
		{
			throw new RuntimeException(
					"XrefInput("
							+ this.getName()
							+ ") is missing xrefEntity, xrefField and/or xrefLabels settings");
		}

		String xrefLabelString = this.toCsv(getXrefLabels());
		String readonly = (this.isReadonly()) ? " readonly class=\"readonly\" "
				: String
						.format(
								" onfocus=\"showXrefInput(this,'%s','%s','%s','%s'); return false;\" ",
								getXrefEntity(), getXrefField(),
								xrefLabelString, getXrefFilters());

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), super
					.getValue());
			
			if(super.getObject() instanceof Entity)
			{
				input = new StringInput(this.getName(), super
						.getObject().getIdValue().toString());
			}

			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());
			input.setHidden(true);
			return input.toHtml();
		}

		StringBuffer optionsHtml = new StringBuffer();
		if (super.getObject() != null)
		{
			optionsHtml.append("\t<option selected value=\""
					+ getObject().getIdValue() + "\">" + this.getValue()
					+ "</option>\n");
		}
		// else if (!this.isReadonly())
		// {
		// optionsHtml.append("\t<option value=\"\"></option>\n");
		// // empty option
		// }
		if (includeAddButton)
		{
			this.addButton.setJavaScriptAction("if( window.name == '' ){ window.name = 'molgenis'+Math.random();}document.getElementById('" + this.getId() + "').form.__target.value=document.getElementById('" + this.getId() + "').form.name.replace(/_form/g, '');document.getElementById('" + this.getId() + "').form.__action.value='" + this.getId() + "';molgenis_window = window.open('','molgenis_edit_new_xref','height=800,width=600,location=no,status=no,menubar=no,directories=no,toolbar=no,resizable=yes,scrollbars=yes');document.getElementById('" + this.getId() + "').form.target='molgenis_edit_new_xref';document.getElementById('" + this.getId() + "').form.__show.value='popup';document.getElementById('" + this.getId() + "').form.submit();molgenis_window.focus();");
		}
		return "<select id=\"" + this.getId() + "\" name=\"" + this.getName()
				+ "\" " + readonly + ">\n" + optionsHtml.toString()
				+ "</select>\n" + (includeAddButton ? this.addButton : "");
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

	@Override
	/**
	 * Returns the label of the selected value.
	 */
	public String getValue()
	{
		if (getObject() != null) return this.getObject().getLabelValue();
		return "";
	}

	public void setIncludeAddButton(boolean includeAddButton)
	{
		this.includeAddButton = includeAddButton;
	}

	public void setAddButton(ActionInput addButton)
	{
		this.addButton = addButton;
	}

	public ActionInput getAddButton()
	{
		return this.addButton;
	}

	@Override
	public String toHtml(Tuple params) throws ParseException,
			HtmlInputException
	{
		return new XrefInput(params).render();
	}

}
