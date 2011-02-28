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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.server.QueryRuleUtil;
import org.molgenis.util.Entity;

/**
 * Input for many-to-many cross-references (xref) to choose data entities from
 * the database. Selectable data items will be shown as selection box and are
 * loaded dynamically via an 'ajax' service.
 */
public class MrefInput extends HtmlInput
{
	// private List<ValueLabel> options = new Vector<ValueLabel>();
	// The label of the value to show in the box
	// xrefLabel,values
	private Map<String, List<?>> valueLabels = new TreeMap<String, List<?>>();
	// what is this?
	private String targetfield;

	private String xrefEntity;
	private String xrefField;
	private List<String> xrefLabels = new ArrayList<String>();
	private List<QueryRule> xrefFilters = new ArrayList<QueryRule>();

	public MrefInput(String name, Object value)
	{
		super(name, value);
	}
	
	public MrefInput(String name, String entityName, Database db) throws InstantiationException, IllegalAccessException
	{
		this(name, db.getClassForName(entityName));
	}
	
	public MrefInput(String name, Class<? extends Entity> xrefEntityClass) throws InstantiationException, IllegalAccessException
	{
		super(name,null);
		
		this.setXrefEntity(xrefEntityClass);
		this.setXrefField(xrefEntityClass.newInstance().getIdField());
		this.setXrefLabels(xrefEntityClass.newInstance().getLabelFields());
	}

	@Override
	public String toHtml()
	{
		// BIG FIXME we have to enable nillable checkin on this one
		this.setNillable(true);

		List<?> values = (List<?>) super.getObject();
		if (values == null) values = new ArrayList<Object>();

		// template of an xref dialog
		XrefInput input = new XrefInput(this.getName());
		input.setXrefEntity(this.getXrefEntity());
		input.setXrefField(this.getXrefField());
		input.setXrefLabels(this.getXrefLabels());
		input.setReadonly(this.isReadonly());
		input.setStyle("display: block;");
		input.setHidden(this.isHidden());

		// add currently known values
		StringBuffer html = new StringBuffer();

		for (int i = 0; i < values.size(); i++)
		{
			input.setValue(values.get(i));

			// String result = "";

			for (String labelName : this.getXrefLabels())
			{
				input.setValueLabel(labelName, this.getValueLabels(labelName)
						.get(i));
			}
			input.setId(this.getName() + i);
			html.append(input.toHtml());
		}

		// add an empty one if none is shown
		if (values.size() == 0)
		{
			input.setValue(null);
			// html.append(input.toHtml()+"<br>");
		}

		if (isHidden())
		{
			return "<div>" + html.toString() + "</div>";
		}
		else
		{
			String buttons = String
					.format(
							"<button style=\"\" type=\"button\" onclick=\"mref_addInput('%s','%s','%s','%s','%s',this.parentNode);\">+</button>",
							getName(), getXrefEntity(), getXrefField(),
							getXrefLabels().get(0), getXrefFilterRESTString());
			buttons += "<button type=\"button\" onclick=\"mref_removeInput(this.parentNode);\">-</button>";

			return "<div>" + html.toString() + buttons + "</div>";
		}
	}

	@Override
	/**
	 * Note, this returns the labels of the selected values.
	 */
	public String getValue()
	{
		String result = "";

		int size = 0;
		for (String label : this.getXrefLabels())
		{
			if (this.getValueLabels(label) != null)
			{
				size = Math.max(size, this.getValueLabels(label).size());
			}
		}

		for (int i = 0; i < size; i++)
		{
			String valueLabel = "";
			for (String labelName : this.getXrefLabels())
			{
				String value = this.getValueLabels(labelName) != null
						&& i < this.getValueLabels(labelName).size()
						&& this.getValueLabels(labelName).get(i) != null ? this
						.getValueLabels(labelName).get(i).toString() : "";
				if (valueLabel.toString().equals("")) valueLabel += value;
				else
					valueLabel += ":" + value;
			}
			result += ", " + valueLabel;
		}

		return result.replaceFirst(", ", "");
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

	public <E extends Entity> void setXrefEntity(Class<E> klazz)
	{
		this.setXrefEntity(klazz.getName());
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

	public List<String> getXrefLabels()
	{
		return xrefLabels;
	}

	public void setXrefLabels(List<String> xrefLabels)
	{
		this.xrefLabels = xrefLabels;
	}

	public void setXrefLabel(String xrefLabel)
	{
		this.xrefLabels.clear();
		this.xrefLabels.add(xrefLabel);
	}

	public List<?> getValueLabels(String xrefLabelName)
	{
		return valueLabels.get(xrefLabelName);
	}

	public void setValueLabels(String xrefLabelName, List<?> valueLabels)
	{
		this.valueLabels.put(xrefLabelName, valueLabels);
	}

	// returns filters as filter string
	public String getXrefFilterRESTString()
	{
		return QueryRuleUtil.toRESTstring(xrefFilters);
	}

	public void setXrefFilters(QueryRule... queryRule)
	{
		this.xrefFilters.addAll(Arrays.asList(queryRule));
	}
}
