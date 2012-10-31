package org.molgenis.ui.theme.bootstrap;

import org.molgenis.ui.Button;
import org.molgenis.ui.Form;
import org.molgenis.ui.Icon;
import org.molgenis.ui.StringInput;
import org.molgenis.ui.theme.RenderException;
import org.molgenis.ui.theme.base.BaseTheme;
import org.molgenis.ui.theme.bootstrap.BootstrapTheme;
import org.testng.annotations.Test;

public class TestBootstrapTheme
{
	@Test
	public void test1() throws RenderException
	{
		// compose
		Form f = new Form()
		.add(new StringInput("name"))
		.add(new Button("helloWorld").setLabel("Say hello").setIcon(Icon.SEARCH));

		// render
		BaseTheme r = new BootstrapTheme();
		String result = r.render(f);

		System.out.println(result);
	}
}
