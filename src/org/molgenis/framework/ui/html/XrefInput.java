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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.util.Entity;

/**
 * Input for cross-reference (xref) entities in a MOLGENIS database. Data will
 * be shown as selection box. Use xrefEntity to specifiy what entity provides
 * the values for selection. Use xrefField to define which entity field to use
 * for the values. Use xrefLabels to select which field(s) should be shown as
 * labels to the user (optional).
 */
public class XrefInput extends HtmlInput
{
	// private List<ValueLabel> options = new Vector<ValueLabel>();
	// The label of the value to show in the box
	private Map<String, Object> valueLabel = new LinkedHashMap<String, Object>();

	private String xrefEntity;
	private String xrefField;
	private String xrefFilter;
	private List<String> xrefLabels = new ArrayList<String>();;

	public XrefInput(String name)
	{
		super(name,null);
	}
	
	public XrefInput(String name, Object value)
	{
		super(name, value);
	}
	
	public XrefInput(String name, String entityName, Database db) throws InstantiationException, IllegalAccessException
	{
		this(name, db.getClassForName(entityName));
	}
	
	public XrefInput(String name, Class<? extends Entity> xrefEntityClass) throws InstantiationException, IllegalAccessException
	{
		super(name,null);
		
		this.setXrefEntity(xrefEntityClass);
		this.setXrefField(xrefEntityClass.newInstance().getIdField());
		this.setXrefLabels(xrefEntityClass.newInstance().getLabelFields());
	}

	@Override
	public String toHtml()
	{
		if("".equals(xrefEntity) || "".equals(xrefField) || xrefLabels == null || xrefLabels.size() == 0)
		{
			throw new RuntimeException("XrefInput("+this.getName()+") is missing xrefEntity, xrefField and/or xrefLabels settings");
		}
		
		String xrefLabelString = this.toCsv(xrefLabels);
		String readonly = (this.isReadonly()) ? " readonly class=\"readonly\" "
				: String
						.format(
								" onfocus=\"showXrefInput(this,'%s','%s','%s','%s'); return false;\" ",
								xrefEntity, xrefField, xrefLabelString,
								xrefFilter);

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), super
					.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());
			input.setHidden(true);
			return input.toHtml();
		}

		StringBuffer optionsHtml = new StringBuffer();
		if (super.getObject() != null)
		{
			optionsHtml.append("\t<option selected value=\"" + super.getValue()
					+ "\">" + this.getValue() + "</option>\n");
		}
		// else if (!this.isReadonly())
		// {
		// optionsHtml.append("\t<option value=\"\"></option>\n");
		// // empty option
		// }
		return "<select id=\"" + this.getId() + "\" name=\"" + this.getName()
				+ "\" " + readonly + ">\n" + optionsHtml.toString()
				+ "</select>\n";
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
	 * Note, this returns the label of the selected value.
	 */
	public String getValue()
	{
		String result = "";
		for (String label : this.xrefLabels)
		{
			if (result.equals("")) result += this.valueLabel.get(label) != null ? this.valueLabel
					.get(label)
					: "";
			else
				result += ":"
						+ (this.valueLabel.get(label) != null ? this.valueLabel
								.get(label) : "");
		}

		return result;
	}

	public String getXrefEntity()
	{
		return xrefEntity;
	}

	/**
	 * Set the entity where this xref should get its values from
	 * 
	 * @param xrefEntity
	 */
	public <E extends Entity> void setXrefEntity(Class<E> xrefEntity)
	{
		this.setXrefEntity(xrefEntity.getName());
	}

	/**
	 * Set the entity where this xref should get its values from
	 * 
	 * @param xrefEntity
	 */
	public void setXrefEntity(String xrefEntity)
	{
		this.xrefEntity = xrefEntity;
	}

	public String getXrefField()
	{
		return xrefField;
	}

	/**
	 * Set the entity field (i.e. database column) that this xref should get its
	 * values from. For example 'id'.
	 * 
	 * @param xrefField
	 *            field name
	 */
	public void setXrefField(String xrefField)
	{
		this.xrefField = xrefField;
	}

	public List<String> getXrefLabel()
	{
		return xrefLabels;
	}

	/**
	 * Set the entity field (i.e. database column) that provides the values that
	 * should be shown to the user as options in the xref select box. For
	 * example 'name'.
	 * 
	 * @param xrefLabel
	 *            field name
	 */
	public void setXrefLabel(String xrefLabel)
	{
		assert(xrefLabel != null);
		this.xrefLabels.clear();
		this.xrefLabels.add(xrefLabel);
	}

	/**
	 * In case of entities with multiple column keys you can also have multiple
	 * labels concatenated together. For example 'investigation_name, name'.
	 * 
	 * @param xrefLabels
	 *            a list of field names
	 */
	public void setXrefLabels(List<String> xrefLabels)
	{
		assert(xrefLabels != null);
		this.xrefLabels = xrefLabels;
	}

	public Object getValueLabel(String xrefLabelName)
	{
		return valueLabel.get(xrefLabelName);
	}

	/**
	 * Set the default selected value and label
	 */
	public void setValueLabel(String xrefLabelName, Object valueLabel)
	{
		this.valueLabel.put(xrefLabelName, valueLabel);
	}

}
