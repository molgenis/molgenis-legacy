package org.molgenis.mutation.ui.search.form;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.ui.html.ActionInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.HiddenInput;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.TextLineInput;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;
import org.molgenis.util.ValueLabel;
import org.springframework.stereotype.Component;

@Component
public class ExpertSearchForm extends Container
{

	private static final long serialVersionUID = -6880726573479157651L;

	private String target;
	private String select;
	private String action;
	private String expertSearch;
	
	private String cdnaNotation;
	private String cdnaStart;
	private String aaStart;
	private String exonName;
	private String exonId;
	private String type;
	private String consequence;
	private String domainId;
	private String phenotype;
	private String inheritance;

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
	
	public void setSelect(String select)
	{
		
	}
	public void setTarget(String target)
	{
		this.get("__target").setValue(target);
	}
	public void populate()
	{
//		this.getModel().getExpertSearchForm().get("select").setValue(this.getName());
//
//		if (this.getModel().getMutationSearchCriteriaVO().getVariation() != null)
//			((TextLineInput) this.getModel().getExpertSearchForm().get("variation")).setValue(this.getModel().getMutationSearchCriteriaVO().getVariation());
//
//		if (this.getModel().getMutationSearchCriteriaVO().getCdnaPosition() != null)
//			((IntInput) this.getModel().getExpertSearchForm().get("nuclno")).setValue(this.getModel().getMutationSearchCriteriaVO().getCdnaPosition());
//
//		if (this.getModel().getMutationSearchCriteriaVO().getCodonNumber() != null)
//			((IntInput) this.getModel().getExpertSearchForm().get("aano")).setValue(this.getModel().getMutationSearchCriteriaVO().getCodonNumber());
//
//		List<ValueLabel> exonIdOptions = new ArrayList<ValueLabel>();
//		exonIdOptions.add(new ValueLabel("", "Select exon/intron"));
//		for (ExonDTO exonSummaryVO : searchService.findAllExons())
//			exonIdOptions.add(new ValueLabel(exonSummaryVO.getId(), exonSummaryVO.getName()));
//		((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setOptions(exonIdOptions);
//		if (this.getModel().getMutationSearchCriteriaVO().getExonId() != null)
//			((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setValue(this.getModel().getMutationSearchCriteriaVO().getExonId());
//		else
//			((SelectInput) this.getModel().getExpertSearchForm().get("exon_id")).setValue("Select exon/intron");
//
//		List<ValueLabel> typeOptions = new ArrayList<ValueLabel>();
//		typeOptions.add(0, new ValueLabel("", "Select mutation type"));
//		for (String mutationType : phenoService.findObservedValues("Type of mutation"))
//			typeOptions.add(new ValueLabel(mutationType, mutationType));
//		((SelectInput) this.getModel().getExpertSearchForm().get("type")).setOptions(typeOptions);
//		if (this.getModel().getMutationSearchCriteriaVO().getType() != null)
//			((SelectInput) this.getModel().getExpertSearchForm().get("type")).setValue(this.getModel().getMutationSearchCriteriaVO().getType());
//		else
//			((SelectInput) this.getModel().getExpertSearchForm().get("type")).setValue("Select mutation type");
//
//		List<ValueLabel> consequenceOptions = new ArrayList<ValueLabel>();
//		consequenceOptions.add(0, new ValueLabel("", "Select consequence"));
//		for (String consequence : phenoService.findObservedValues("consequence"))
//			consequenceOptions.add(new ValueLabel(consequence, consequence));
//		((SelectInput) this.getModel().getExpertSearchForm().get("consequence")).setOptions(consequenceOptions);
//		if (this.getModel().getMutationSearchCriteriaVO().getConsequence() != null)
//			((SelectInput) this.getModel().getExpertSearchForm().get("consequence")).setValue(this.getModel().getMutationSearchCriteriaVO().getConsequence());
//		else
//			((SelectInput) this.getModel().getExpertSearchForm().get("consequence")).setValue("Select consequence");
//
//		List<ValueLabel> domainOptions = new ArrayList<ValueLabel>();
//		domainOptions.add(new ValueLabel("", "Select protein domain"));
//		for (ProteinDomainDTO domainVO : searchService.findAllProteinDomains())
//			domainOptions.add(new ValueLabel(domainVO.getDomainId(), domainVO.getDomainName()));
//		((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setOptions(domainOptions);
//		if (this.getModel().getMutationSearchCriteriaVO().getProteinDomainId() != null)
//			((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setValue(this.getModel().getMutationSearchCriteriaVO().getProteinDomainId());
//		else
//			((SelectInput) this.getModel().getExpertSearchForm().get("domain_id")).setValue("Select protein domain");
//		
//		List<ValueLabel> phenotypeOptions = new ArrayList<ValueLabel>();
//		phenotypeOptions.add(new ValueLabel("", "Select phenotype"));
//		for (String phenotypeName : phenoService.findObservedValues("Phenotype"))
//			phenotypeOptions.add(new ValueLabel(phenotypeName, phenotypeName));
//		((SelectInput) this.getModel().getExpertSearchForm().get("phenotype")).setOptions(phenotypeOptions);
//		if (this.getModel().getMutationSearchCriteriaVO().getPhenotypeId() != null)
//			((SelectInput) this.getModel().getExpertSearchForm().get("phenotype")).setValue(this.getModel().getMutationSearchCriteriaVO().getPhenotypeId());
//		else
//			((SelectInput) this.getModel().getExpertSearchForm().get("phenotype")).setValue("Select phenotype");
//
//		List<ValueLabel> inheritanceOptions = new ArrayList<ValueLabel>();
//		inheritanceOptions.add(0, new ValueLabel("", "Select inheritance"));
//		for (String inheritance : phenoService.findObservedValues("Inheritance"))
//			inheritanceOptions.add(new ValueLabel(inheritance, inheritance));
//		((SelectInput) this.getModel().getExpertSearchForm().get("inheritance")).setOptions(inheritanceOptions);
//		if (this.getModel().getMutationSearchCriteriaVO().getInheritance() != null)
//			((SelectInput) this.getModel().getExpertSearchForm().get("inheritance")).setValue(this.getModel().getMutationSearchCriteriaVO().getInheritance());
//		else
//			((SelectInput) this.getModel().getExpertSearchForm().get("inheritance")).setValue("Select inheritance");
	}
}
