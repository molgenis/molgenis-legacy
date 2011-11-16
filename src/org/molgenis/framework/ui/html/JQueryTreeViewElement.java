package org.molgenis.framework.ui.html;

import org.molgenis.util.SimpleTree;

public class JQueryTreeViewElement extends SimpleTree<JQueryTreeViewElement>
{
	private static final long serialVersionUID = 1L;

	public JQueryTreeViewElement(String name, JQueryTreeViewElement parent)
	{
		super(name, parent);
		// TODO Auto-generated constructor stub
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
	
}