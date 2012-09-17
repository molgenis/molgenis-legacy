package org.molgenis.mutation.ui.search.form;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.util.ValueLabel;

public class DisplayOptionsForm extends Container
{

	private static final long serialVersionUID = 7252019727191110005L;

	public DisplayOptionsForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("__action", ""));

		this.add(new HiddenInput("domain_id", ""));
		this.add(new HiddenInput("exon_id", ""));
		this.add(new HiddenInput("mid", ""));

		this.add(new SelectInput("snpbool", ""));
		List<ValueLabel> snpOptions           = new ArrayList<ValueLabel>();
		snpOptions.add(new ValueLabel("show", "show"));
		snpOptions.add(new ValueLabel("hide", "hide"));
		((SelectInput) this.get("snpbool")).setOptions(snpOptions);

		this.add(new SelectInput("showIntrons", ""));
		List<ValueLabel> showIntronsOptions   = new ArrayList<ValueLabel>();
		showIntronsOptions.add(new ValueLabel("show", "show"));
		showIntronsOptions.add(new ValueLabel("hide", "hide"));
		((SelectInput) this.get("showIntrons")).setOptions(showIntronsOptions);

		this.add(new SelectInput("showNames", ""));
		List<ValueLabel> showNamesOptions     = new ArrayList<ValueLabel>();
		showNamesOptions.add(new ValueLabel("show", "show"));
		showNamesOptions.add(new ValueLabel("hide", "hide"));
		((SelectInput) this.get("showNames")).setOptions(showNamesOptions);

		this.add(new SelectInput("showNumbering", ""));
		List<ValueLabel> showNumberingOptions = new ArrayList<ValueLabel>();
		showNumberingOptions.add(new ValueLabel("show", "show"));
		showNumberingOptions.add(new ValueLabel("hide", "hide"));
		((SelectInput) this.get("showNumbering")).setOptions(showNumberingOptions);

		this.add(new SelectInput("showMutations", ""));
		List<ValueLabel> showMutationsOptions = new ArrayList<ValueLabel>();
		showMutationsOptions.add(new ValueLabel("show", "show"));
		showMutationsOptions.add(new ValueLabel("hide", "hide"));
		((SelectInput) this.get("showMutations")).setOptions(showMutationsOptions);
	}
}
