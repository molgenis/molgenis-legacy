package org.molgenis.framework.ui.html;

import org.molgenis.util.SimpleTree;

public class JQueryTreeViewElement extends SimpleTree<JQueryTreeViewElement>
{
	private static final long serialVersionUID = 1L;
	
	/** Label of the tree that can be made also linkable  **/ 
	String label;

	public JQueryTreeViewElement(String name, JQueryTreeViewElement parent)
	{
		super(name, parent);
		this.setLabel(name);
	}
	
	public JQueryTreeViewElement(String name, JQueryTreeViewElement parent, String url)
	{
		super(name, parent);
		this.setLabel("<a href=\"" + url + "\" >"+ name +"</a>");

	}

	//whether the element is ticked/selected
	private boolean isSelected = false;
	
	//whether the element is collapsed
	private boolean isCollapsed = false;

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
	
}