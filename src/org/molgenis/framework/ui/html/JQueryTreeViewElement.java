package org.molgenis.framework.ui.html;

import org.molgenis.util.SimpleTree;

public class JQueryTreeViewElement extends SimpleTree<JQueryTreeViewElement>
{
	private static final long serialVersionUID = 1L;

	/** Label of the tree that can be made also linkable **/
	String label;

	String nodeName;

	String htmlValue;

	private String category;

	private boolean checked = false;

	private String entityID;

	public JQueryTreeViewElement(String name, String entityID, JQueryTreeViewElement parent)
	{
		super(name, parent);
		this.setLabel(name);
		this.setEntityID(entityID);
	}

	public JQueryTreeViewElement(String name, String label, String entityID, JQueryTreeViewElement parent)
	{
		super(name, parent);
		this.setLabel(label);
		this.setEntityID(entityID);

	}

	public JQueryTreeViewElement(String name, JQueryTreeViewElement parent, String htmlValue)
	{
		super(name, parent);
		this.setLabel(name);
		this.htmlValue = htmlValue;
	}

	public JQueryTreeViewElement(String name, String label, JQueryTreeViewElement parent, String htmlValue)
	{
		super(name, parent);
		this.setLabel(label);
		this.htmlValue = htmlValue;
	}

	public void setCheckBox(boolean checked)
	{
		this.checked = checked;
	}

	public boolean getCheckBox()
	{
		return checked;
	}

	public String getNodeName()
	{
		return nodeName;
	}

	// whether the element is ticked/selected
	private boolean isSelected = false;

	// whether the element is collapsed
	private boolean isCollapsed = true;

	public boolean isSelected()
	{
		return isSelected;
	}

	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	public boolean isCollapsed()
	{
		return isCollapsed;
	}

	public void setCollapsed(boolean isCollapsed)
	{
		this.isCollapsed = isCollapsed;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public String getLabel()
	{
		return label;
	}

	private void setEntityID(String entityID)
	{
		this.entityID = entityID;

	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getCategory()
	{
		return category;
	}

	public void setHtmlValue(String htmlValue)
	{
		this.htmlValue = htmlValue;
	}

	public String getHtmlValue()
	{
		return htmlValue;
	}

	public String getEntityID()
	{
		return entityID;
	}
}