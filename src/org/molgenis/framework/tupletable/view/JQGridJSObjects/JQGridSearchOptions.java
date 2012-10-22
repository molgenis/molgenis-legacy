package org.molgenis.framework.tupletable.view.JQGridJSObjects;

import java.util.Arrays;
import java.util.List;

public class JQGridSearchOptions
{
	public boolean multipleSearch = true;
	public boolean multipleGroup = true;
	public boolean showQuery = true;
	public List<JQGridRule.JQGridOp> sopt = Arrays.asList(JQGridRule.JQGridOp.values());

	public JQGridSearchOptions()
	{
	}

	public JQGridSearchOptions(boolean multipleSearch, boolean multipleGroup, boolean showQuery,
			List<JQGridRule.JQGridOp> sopt)
	{
		this.multipleSearch = multipleSearch;
		this.multipleGroup = multipleGroup;
		this.showQuery = showQuery;
		this.sopt = sopt;
	}

}
