package org.molgenis.framework.ui.html;

import org.molgenis.util.SimpleTree;

public class JQueryTreeViewElement extends SimpleTree<JQueryTreeViewElement>
{
	private static final long serialVersionUID = 1L;

	/** Label of the tree that can be made also linkable **/
	String label = null;

	String nodeName = null;

	String htmlValue;

	private boolean isbottom = false;

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

	public boolean isIsbottom()
	{
		return isbottom;
	}

	public void setIsbottom(boolean isbottom)
	{
		this.isbottom = isbottom;
	}

	public void toggleNode()
	{
		if (isCollapsed == true)
		{
			isCollapsed = false;
		}
		else
		{
			isCollapsed = true;
		}
	}

	public String toHtml()
	{

		String node = null;

		if (!this.isIsbottom())
		{
			String childrenNode = "";

			if (!this.isCollapsed() && this.hasChildren())
			{

				for (JQueryTreeViewElement childNode : getChildren())
				{
					childrenNode += childNode.toHtml();
				}

			}

			node = "<li id = \"" + getName().replaceAll(" ", "_") + "\" class=\"" + (isCollapsed ? "closed" : "open")
					+ "\"><span class=\"folder\">" + (getLabel() == null ? getName() : getLabel())
					+ "</span><ul style=\"display:" + (isCollapsed ? "none" : "block") + "\">" + childrenNode
					+ "</ul></li>";
		}
		else
		{
			node = "<li id = \"" + getName().replaceAll(" ", "_") + "\"><span class=\"point\">"
					+ (getLabel() == null ? getName() : getLabel()) + "</span></li>";
		}

		return node;

	}

	public String toHtml(String childNode)
	{
		String node = null;

		if (!this.isIsbottom())
		{
			node = "<li id = \"" + getName().replaceAll(" ", "_") + "\" class=\"" + (isCollapsed ? "closed" : "open")
					+ "\"><span class=\"folder\">" + (getLabel() == null ? getName() : getLabel())
					+ "</span><ul style=\"display:" + (isCollapsed ? "none" : "block") + "\">"
					+ (childNode == null ? "" : childNode) + "</ul></li>";
		}
		else
		{
			node = "<li id = \"" + getName().replaceAll(" ", "_") + "\"><span class=\"point\">"
					+ (getLabel() == null ? getName() : getLabel()) + "</span></li>";
		}

		return node;
	}
}