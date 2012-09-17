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
		this.add(new TextLineInput("variation"));
		((TextLineInput) this.get("variation")).setClazz("adv_search");
		this.add(new TextLineInput("genbankid"));
		this.add(new TextLineInput("nuclno"));
		((TextLineInput) this.get("nuclno")).setClazz("adv_search");
		this.add(new TextLineInput("aano"));
		((TextLineInput) this.get("aano")).setClazz("adv_search");
		this.add(new TextLineInput("exon"));
		((TextLineInput) this.get("exon")).setClazz("adv_search");
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
