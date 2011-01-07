/**
 * File: invengine.screen.form.SelectInput <br>
 * Copyright: Inventory 2000-2006, GBIC 2005, all rights reserved <br>
 * Changelog:
 * <ul>
 * <li> 2006-03-07, 1.0.0, DI Matthijssen
 * <li> 2006-05-14; 1.1.0; MA Swertz integration into Inveninge (and major
 * rewrite)
 * <li> 2006-05-14; 1.2ƒ.0; RA Scheltema major rewrite + cleanup
 * </ul>
 */

package org.molgenis.framework.ui.html;

// jdk
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.util.Entity;

/**
 * Input for cross-reference (xref) data. Data will be shown as selection box.
 * Optionally, multi-column xref_labels can be set.
 */
public class XrefInput extends HtmlInput
{
	//private List<ValueLabel> options = new Vector<ValueLabel>();
	//The label of the value to show in the box
	private Map<String,Object> valueLabel = new LinkedHashMap<String,Object>();
	//what is this?
	private String targetfield;
	

	private String xrefEntity;
	private String xrefField;
	private String xrefFilter;
	private List<String> xrefLabels;
	
	public XrefInput(String name, Object value)
	{
		super(name, value);
	}

	@Override
	public String toHtml()
	{
		String xrefLabelString = this.toCsv(xrefLabels);
		String readonly = (this.isReadonly()) ? " readonly class=\"readonly\" " : String.format(" onfocus=\"showXrefInput(this,'%s','%s','%s','%s'); return false;\" ", xrefEntity, xrefField, xrefLabelString, xrefFilter);

		if (this.isHidden())
		{
			StringInput input = new StringInput(this.getName(), super.getValue());
			input.setLabel(this.getLabel());
			input.setDescription(this.getDescription());
			input.setHidden(true);
			return input.toHtml();
		}

		StringBuffer optionsHtml = new StringBuffer();
		if(super.getObject() != null)
		{
			optionsHtml.append("\t<option selected value=\""+super.getValue()+"\">"+this.getValue()+"</option>\n");
		}
//		else if (!this.isReadonly())
//		{
//			optionsHtml.append("\t<option value=\"\"></option>\n");
//			// empty option
//		}
		return "<select id=\"" + this.getId() + "\" name=\"" + this.getName() + "\" " + readonly + ">\n" + optionsHtml.toString() + "</select>\n";
	}

	private String toCsv(List<String> xrefLabels)
	{
		String result = "";
		for(String label: xrefLabels)
		{
			result+= ","+label;
		}
		
		return result.replaceFirst(",", "");
	}

	@Override
	/**
	 * Note, this returns the labels of the selected values.
	 */
	public String getValue()
	{
		String result = "";
		for(String label: this.xrefLabels)
		{
			if(result.equals(""))
				result += this.valueLabel.get(label) != null ? this.valueLabel.get(label) : "";
			else
				result += ":" + (this.valueLabel.get(label) != null ? this.valueLabel.get(label) : "");
		}
		
		return result;
	}

	public String getTargetfield()
	{
		return targetfield;
	}

	public void setTargetfield( String targetfield )
	{
		this.targetfield = targetfield;
	}

	public String getXrefEntity() {
		return xrefEntity;
	}
	
	public <E extends Entity>void setXrefEntity(Class<E> xrefEntity)
	{
		this.setXrefEntity(xrefEntity.getName());
	}

	public void setXrefEntity(String xrefEntity) {
		this.xrefEntity = xrefEntity;
	}

	public String getXrefField() {
		return xrefField;
	}

	public void setXrefField(String xrefField) {
		this.xrefField = xrefField;
	}

	public List<String> getXrefLabel() {
		return xrefLabels;
	}

	public void setXrefLabel(String xrefLabel) {
		this.xrefLabels.clear();
		this.xrefLabels.add(xrefLabel);
	}
	
	public void setXrefLabels(List<String> xrefLabels) {
		this.xrefLabels = xrefLabels;
	}

	public Object getValueLabel(String xrefLabelName) {
		return valueLabel.get(xrefLabelName);
	}

	public void setValueLabel(String xrefLabelName, Object valueLabel) {
		this.valueLabel.put(xrefLabelName, valueLabel);
	}

}
