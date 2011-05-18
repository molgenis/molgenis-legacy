package org.molgenis.framework.ui.html;

/**
 * Input that only has a label but will not show an input (i.e., doesn't have a value).
 */
public class LabelInput extends HtmlInput
{
	//very confusing, would like to set label without id here
	@Deprecated
	public LabelInput(String name)
	{
		super(name,null);
	}
	
	public LabelInput(String name, String value)
	{
		super(name,value);
	}

	@Override
	public String toHtml()
	{
		return "&nbsp;";
	}
}
