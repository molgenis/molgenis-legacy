package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.TextLineInput;

public class SimpleSearchForm extends Container
{
	public SimpleSearchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", "findMutationsByTerm"));
		this.add(new TextLineInput("term"));
		((TextLineInput) this.get("term")).setClazz("simple_search");
		((TextLineInput) this.get("term")).setSize(50);
		this.add(new ActionInput("findMutationsByTerm"));
		((ActionInput) this.get("findMutationsByTerm")).setLabel("Search");
		((ActionInput) this.get("findMutationsByTerm")).setTooltip("Search");
		((ActionInput) this.get("findMutationsByTerm")).setButtonValue("Search");
	}
}
