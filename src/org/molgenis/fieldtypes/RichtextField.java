package org.molgenis.fieldtypes;

import org.molgenis.framework.ui.html.RichtextInput;
import org.molgenis.framework.ui.html.HtmlInput;
import org.molgenis.framework.ui.html.HtmlInputException;

public class RichtextField extends TextField
{
	@Override
	public HtmlInput createInput(String name, String xrefEntityClassName) throws HtmlInputException
	{
		return new RichtextInput(name);
	}
}
