package org.molgenis.mutation.ui;

import org.molgenis.framework.ui.html.Container;

/**
 * Wrapper class around ExpertSearchForm to be used in freemarker.
 *
 */
public class HtmlFormWrapper
{
	private Container form;

	public HtmlFormWrapper(Container form)
	{
		this.form = form;
	}

	public Container getForm()
	{
		return form;
	}

	public void setForm(Container form)
	{
		this.form = form;
	}

	public String render()
	{
		return this.form.toHtml();
	}
}
