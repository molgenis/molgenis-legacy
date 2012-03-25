package org.molgenis.mutation.ui.search.form;

import java.util.Vector;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.AutocompleteInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.RadioInput;
import org.molgenis.util.ValueLabel;

public class SimpleSearchForm extends Container
{

	private static final long serialVersionUID = -8848955017610414423L;

	public SimpleSearchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", "findMutationsByTerm"));
		this.add(new AutocompleteInput("term", "", "org.molgenis.variant.SequenceCharacteristic", "name", ""));
		((AutocompleteInput) this.get("term")).setClazz("simple_search");
		((AutocompleteInput) this.get("term")).setSize(50);
		Vector<ValueLabel> options = new Vector<ValueLabel>();
		options.add(new ValueLabel("mutations", "Show mutations"));
		options.add(new ValueLabel("patients", "Show patients"));
		this.add(new RadioInput("result", "", "", options, "mutations"));
		this.add(new ActionInput("findMutationsByTerm"));
		((ActionInput) this.get("findMutationsByTerm")).setLabel("Search");
		((ActionInput) this.get("findMutationsByTerm")).setTooltip("Search");
		((ActionInput) this.get("findMutationsByTerm")).setButtonValue("Search");
	}
}
