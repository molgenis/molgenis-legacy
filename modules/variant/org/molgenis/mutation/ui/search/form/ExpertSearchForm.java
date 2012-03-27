package org.molgenis.mutation.ui.search.form;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.TextLineInput;

public class ExpertSearchForm extends Container
{

	private static final long serialVersionUID = -6880726573479157651L;

	public ExpertSearchForm()
	{
		this.add(new HiddenInput("__target", ""));
		this.add(new HiddenInput("select", ""));
		this.add(new HiddenInput("__action", "findMutations"));
		this.add(new HiddenInput("expertSearch", "1"));
		TextLineInput<String> variationInput = new TextLineInput<String>("variation");
		variationInput.setClazz("adv_search");
		this.add(variationInput);
		this.add(new TextLineInput<String>("genbankid"));
		TextLineInput<String> nuclnoInput = new TextLineInput<String>("nuclno");
		nuclnoInput.setClazz("adv_search");
		this.add(nuclnoInput);
		TextLineInput<String> aanoInput = new TextLineInput<String>("aano");
		aanoInput.setClazz("adv_search");
		this.add(aanoInput);
		TextLineInput<String> exonInput = new TextLineInput<String>("exon");
		exonInput.setClazz("adv_search");
		this.add(exonInput);
		this.add(new SelectInput("exon_id", ""));
		((SelectInput) this.get("exon_id")).setClazz("adv_search");
		this.add(new SelectInput("type", ""));
		((SelectInput) this.get("type")).setClazz("adv_search");
		((SelectInput) this.get("type")).setSize(3);
		this.add(new SelectInput("consequence", ""));
		((SelectInput) this.get("consequence")).setClazz("adv_search");
		((SelectInput) this.get("consequence")).setSize(3);
		this.add(new SelectInput("domain_id", ""));
		((SelectInput) this.get("domain_id")).setClazz("adv_search");
		this.add(new SelectInput("phenotype", ""));
		((SelectInput) this.get("phenotype")).setClazz("adv_search");
		((SelectInput) this.get("phenotype")).setSize(3);
		this.add(new SelectInput("inheritance", ""));
		((SelectInput) this.get("inheritance")).setClazz("adv_search");
		this.add(new ActionInput("findMutations"));
		((ActionInput) this.get("findMutations")).setLabel("Search");
		((ActionInput) this.get("findMutations")).setTooltip("Search");
		((ActionInput) this.get("findMutations")).setButtonValue("Search");
	}
}
