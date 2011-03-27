package org.molgenis.feedback.ui;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.framework.ui.html.TextLineInput;

public class ContactForm extends Container
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3622893211252371379L;

	public ContactForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", "send"));
		this.add(new TextLineInput("name"));
		this.add(new TextLineInput("email"));
		this.add(new StringInput("comments", "Enter your comments here"));
		((StringInput) this.get("comments")).setHeight(10);
		this.add(new ActionInput("send"));
		((ActionInput) this.get("send")).setValue(1);
		((ActionInput) this.get("send")).setLabel("Submit");
		((ActionInput) this.get("send")).setTooltip("Submit");
	}
}
