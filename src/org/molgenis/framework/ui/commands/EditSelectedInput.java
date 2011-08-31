package org.molgenis.framework.ui.commands;

import org.molgenis.framework.ui.html.HtmlInput;

/**
 * Decorates an input with a checkbox to disable/enable the input
 * @param <E>
 */
public class EditSelectedInput<E> extends HtmlInput<E>
{
	private HtmlInput<E> input;
	
	public EditSelectedInput(HtmlInput<E> input)
	{
		this.input = input;
	}
	
	public String getLabel()
	{
		return input.getLabel();
	}
	
	@Override
	public String toHtml()
	{
		input.setNillable(true);
		return "<input type=\"checkbox\" name=\"use_"+input.getName()+"\" />" + input.toHtml();
	}

}
