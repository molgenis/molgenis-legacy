package org.molgenis.datatable.view.JQGridJSObjects;

import java.util.ArrayList;
import java.util.List;

//{"groupOp":"AND","rules":[{"field":"Country.Code","op":"eq","data":"AGO"}]}
public class JQGridFilter
{
	public String groupOp = "AND";
	public List<JQGridRule> rules = new ArrayList<JQGridRule>();
}
