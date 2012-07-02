package org.molgenis.ui.theme.bootstrap;

import org.molgenis.framework.ui.ScreenView;
import org.molgenis.framework.ui.html.HtmlInputException;
import org.molgenis.ui.Button;
import org.molgenis.ui.Form;
import org.molgenis.ui.Label;
import org.molgenis.ui.MolgenisComponent;
import org.molgenis.ui.StringInput;
import org.molgenis.ui.theme.RenderException;
import org.molgenis.ui.theme.base.BaseTheme;
import org.molgenis.ui.theme.base.LabelView;

/**
 * Theme that renders the ui using bootstrap. If no suitable renderer is
 * available, the element will be rendered using the BaseTheme
 * 
 * NB: temporarily this implements ScreenView (to bridge old and new).
 */
public class BootstrapTheme extends BaseTheme implements ScreenView
{
	private MolgenisComponent component;

	public BootstrapTheme(MolgenisComponent c)
	{
		this();
		this.component = c;
	}

	public BootstrapTheme()
	{
		super();

		renderers.put(Button.class.getName(), new ButtonView());
		renderers.put(Form.class.getName(), new FormView());
		renderers.put(StringInput.class.getName(), new StringInputView());
	}

	@Override
	public String render() throws HtmlInputException
	{
		try
		{
			return this.render(component);
		}
		catch (RenderException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return e.getMessage();
		}
	}

	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link href=\"bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">" +
				"<link href=\"bootstrap/css/bootstrap-responsive.css\" rel=\"stylesheet\">" +
				"<script type=\"text/javascript\" src=\"bootstrap/js/bootstrap.min.js\"></script>";
	}
}
