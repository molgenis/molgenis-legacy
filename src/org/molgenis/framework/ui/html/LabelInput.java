package org.molgenis.framework.ui.html;

/**
 * Input that only has a label but will not show an input (i.e., doesn't have a value).
 */
public class LabelInput extends HtmlInput
{
	public LabelInput(String name)
	{
		super(name,null);
	}

	@Override
	public String toHtml()
	{
		return "&nbsp;";
	}
}
