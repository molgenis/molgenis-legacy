package org.molgenis.fieldtypes;

import org.molgenis.framework.ui.html.FreemarkerInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;

public class FreemarkerField extends TextField
{
	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName) throws HtmlInputException
	{
		return new FreemarkerInput(name);
	}

}
