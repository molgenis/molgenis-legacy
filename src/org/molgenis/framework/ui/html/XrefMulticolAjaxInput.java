/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li> 2006-03-07, 1.0.0, DI Matthijssen
 * <li> 2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li> 2006-05-14; 1.2.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

// jdk
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.molgenis.util.ValueLabel;

/**
 * Input for cross-reference (xref) data. Data will be shown as a chain of
 * selection boxes. Each select box updates the one right next to it on change.
 * The last one is the selection field.
 */
public class XrefMulticolAjaxInput extends HtmlInput
{
	// private List<ValueLabel> options = new Vector<ValueLabel>();
	// The label of the value to show in the box
	private String valueLabel;
	// what is this?
	private String targetfield;

	// the foreign key entity
	private String xrefEntity;
	// the foreign key field
	private String xrefField;
	// each item of this should be 'entity.field'.
	private List<String> xrefLabelFields;

	public XrefMulticolAjaxInput(String name, Object value)
	{
		super(name, value);
	}

	@Override
	public String toHtml()
	{
		String labelArr = "{";
		for (String label : xrefLabelFields)
		{
			labelArr += "\"label\",";
		}
		labelArr = labelArr.substring(0, labelArr.length() - 1) + "}";

		// basically only the last one matters, the rest is just to populate
		// it
		String readonly = (this.isReadonly()) ? " readonly class=\"readonly\" " : String.format(
				" onfocus=\"showXrefInput(this,'%s','%s','%s'); return false;\" ", xrefEntity, xrefField, labelArr);

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), super.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());
			input.setHidden(true);
			return input.toHtml();
		}

		StringBuffer optionsHtml = new StringBuffer();
		if (super.getObject() != null)
		{
			optionsHtml.append("\t<option selected value=\"" + super.getValue() + "\">" + this.getValueLabel()
					+ "</option>\n");
		}
		if (!this.isReadonly())
		{
			optionsHtml.append("\t<option value=\"\"></option>\n");
			// empty option
		}
		return "<select id=\"" + this.getId() + "\" name=\"" + this.getName() + "\" "
				+ readonly + ">\n" + optionsHtml.toString() + "</select>\n";

	}

	@Override
	/*
	 * * Note, this returns the labels of the selected values.
	 */
	public String getValue()
	{

		if (this.getValueLabel() == null) return "";
		return this.getValueLabel();
	}

	public String getTargetfield()
	{
		return targetfield;
	}

	public void setTargetfield(String targetfield)
	{
		this.targetfield = targetfield;
	}

	public String getXrefEntity()
	{
		return xrefEntity;
	}

	public void setXrefEntity(String xrefEntity)
	{
		this.xrefEntity = xrefEntity;
	}

	public String getXrefField()
	{
		return xrefField;
	}

	public void setXrefField(String xrefField)
	{
		this.xrefField = xrefField;
	}

	public List<String> getXrefLabel()
	{
		return xrefLabelFields;
	}

	public void setXrefLabel(List<String> xrefLabels)
	{
		this.xrefLabelFields = xrefLabels;
	}

	public String getValueLabel()
	{
		return valueLabel;
	}

	public void setValueLabel(String valueLabel)
	{
		this.valueLabel = valueLabel;
	}

}
