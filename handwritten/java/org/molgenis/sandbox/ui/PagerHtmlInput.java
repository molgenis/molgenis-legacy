package org.molgenis.sandbox.ui;

import org.molgenis.framework.db.paging.DatabasePager;
import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.TextParagraph;

public class PagerHtmlInput extends HtmlInput
{
	private boolean horizontal = true;

	public PagerHtmlInput(String name, DatabasePager<?> pager)
	{
		super(name, pager);
	}

	@Override
	public String toHtml()
	{
		ActionInput first = new ActionInput(this.getName() + "_first");
		ActionInput prev = new ActionInput(this.getName() + "_prev");
		ActionInput next = new ActionInput(this.getName() + "_next");
		ActionInput last = new ActionInput(this.getName() + "_last");

		String state = (getObject().getOffset()+1) + "-"
				+ Math.min(getObject().getLimit(), getObject().getCount()) + " of " + getObject().getCount();

		if (isHorizontal())
		{
			first.setIcon("res/img/first.png");
			prev.setIcon("res/img/prev.png");
			next.setIcon("res/img/next.png");
			last.setIcon("res/img/last.png");
			return "<div style=\"vertical-align: middle\" class="+this.getClazz()+">"+first.toHtml() + prev.toHtml() + "<label>"+state+"</label>"
					+ next.toHtml() + last.toHtml()+"</di>";
		}
		else
		{
			first.setIcon("res/img/rowStart.png");
			prev.setIcon("res/img/up.png");
			next.setIcon("res/img/down.png");
			last.setIcon("res/img/rowStop.png");
			return first.toHtml() + "<br/>" + prev.toHtml() + "<br/>"+"<label>"+state+"</label>"
					+ next.toHtml() + "<br/>" + last.toHtml();
		}
	}

	public DatabasePager<?> getObject()
	{
		return (DatabasePager<?>) super.getObject();
	}

	/** Will render a horizontal pager if true (default), otherwise vertical. */
	public boolean isHorizontal()
	{
		return horizontal;
	}

	/** Set to false if you want to render a vertical pager */
	public void setHorizontal(boolean horizontal)
	{
		this.horizontal = horizontal;
	}

}
