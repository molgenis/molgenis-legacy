package org.molgenis.col7a1.ui;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.HtmlInput;
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
		variationInput.setLabel("Variation");
		this.add(variationInput);
		TextLineInput<String> nuclnoInput = new TextLineInput<String>("nuclno");
		nuclnoInput.setClazz("adv_search");
		nuclnoInput.setLabel("Nucleotide No");
		this.add(nuclnoInput);
		TextLineInput<String> aanoInput = new TextLineInput<String>("aano");
		aanoInput.setClazz("adv_search");
		aanoInput.setLabel("Amino Acid No");
		this.add(aanoInput);
		this.add(new SelectInput("exon_id", ""));
		((SelectInput) this.get("exon_id")).setClazz("adv_search");
		((SelectInput) this.get("exon_id")).setLabel("Exon/Intron");
		this.add(new SelectInput("type", ""));
		((SelectInput) this.get("type")).setClazz("adv_search");
		((SelectInput) this.get("type")).setLabel("Mutation type");
		this.add(new SelectInput("consequence", ""));
		((SelectInput) this.get("consequence")).setClazz("adv_search");
		((SelectInput) this.get("consequence")).setLabel("Consequence");
		this.add(new SelectInput("domain_id", ""));
		((SelectInput) this.get("domain_id")).setClazz("adv_search");
		((SelectInput) this.get("domain_id")).setLabel("Protein domain");
		this.add(new SelectInput("phenotype", ""));
		((SelectInput) this.get("phenotype")).setClazz("adv_search");
		((SelectInput) this.get("phenotype")).setLabel("Phenotype");
		this.add(new SelectInput("inheritance", ""));
		((SelectInput) this.get("inheritance")).setClazz("adv_search");
		((SelectInput) this.get("inheritance")).setLabel("Inheritance");
	}

	@Override
	public String toHtml()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("<tr>");

		for (int i = 0; i < this.getInputs().size(); ++i)
		{
			HtmlInput<?> input = this.getInputs().get(i);

			buf.append("<td>");
			if (!input.isHidden())
			{
				buf.append(input.getLabel());
			}
			buf.append("</td><td>");
			buf.append(input.toHtml());
			buf.append("</td>");
			
			if (i % 2 == 1)
			{
				buf.append("</tr>\n");
				buf.append("<tr>");
			}
		}
		
		if (this.getInputs().size() % 2 == 1)
		{
			buf.append("</tr>\n");
		}
		
		return buf.toString();
	}
}
