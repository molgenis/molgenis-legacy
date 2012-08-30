/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.col7a1.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.text.StrBuilder;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.TextLineInput;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.ui.HtmlFormWrapper;
import org.molgenis.mutation.ui.search.SearchPlugin;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.util.ValueLabel;

public class Search extends SearchPlugin
{
	private static final long serialVersionUID = 1162846311691838788L;

	public Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.getModel().setPatientPager("generated-res/col7a1/patientPager.jsp");
		this.getModel().setMutationPager("generated-res/col7a1/mutationPager.jsp");
		this.getModel().setPatientViewer("/org/molgenis/col7a1/ui/patient.ftl");
		this.getModel().setMutationViewer("/org/molgenis/col7a1/ui/mutation.ftl");
		this.getModel().setExpertSearchFormWrapper(new HtmlFormWrapper(new ExpertSearchForm()));
	}

	@Override
	public void reload(Database db)
	{
		super.reload(db);

		try
		{
//			List<FrontEndElement> fee;
	
			StrBuilder text = new StrBuilder();
			text.appendln("<h3>");
			text.appendln("Welcome to the <b>international registry of dystrophic epidermolysis bullosa (DEB) patients and associated COL7A1 mutations</b>.");
			text.appendln("</h3>");
			text.appendln("<p>");
			text.appendln("The International Dystrophic Epidermolysis Bullosa Patient Registry contains anonymised data on both published and unpublished DEB patients, as well as their associated COL7A1 mutations and genotypes, and clinical and molecular phenotypes.");
			text.appendln("</p>");
//			text.appendln("<p>");
//			text.appendln("The database currently contains " + this.getModel().getNumPatients() + " DEB patients, of which " + this.getModel().getNumUnpublished() + " unpublished, and " + this.getModel().getNumMutations() + " COL7A1 mutations. Search or browse below.");
//			text.appendln("</p>");
//			text.appendln("<br/>");
			
			this.getModel().setTextWelcome(text.toString());
	
//			fee = db.query(FrontEndElement.class).equals(FrontEndElement.TYPE_, "search").find();
//			
//			if (fee.size() == 1)
//			{
//				this.getModel().setTextSearch(fee.get(0).getUsertext());
//			}
//			else
//			{
				text = new StrBuilder();
				text.appendln("<h3>Search registry</h3>");
				text.appendln("<p>");
				text.appendln("Search by typing any search term in the search field, like cDNA (e.g. \"3G>T\") or protein (e.g. \"Arg525Ter\") notations of mutations, mode of inheritance (e.g. \"dominant\") or specific phenotypes (e.g. \"severe generalized\"). Search results are shown at bottom of page.");
				text.appendln("</p>");

				this.getModel().setTextSearch(text.toString());
//			}
//
//			fee = db.query(FrontEndElement.class).equals(FrontEndElement.TYPE_, "remarks").find();
//			
//			if (fee.size() == 1)
//			{
//				this.getModel().setTextRemarks(fee.get(0).getUsertext());
//			}
//			else
//			{
				text = new StrBuilder();
				text.appendln("<h4>General remarks</h4>");
				text.appendln("<ol>");
				text.appendln("<li>Mutations are numbered according to the current reference sequence (<a href=\"http://www.ncbi.nlm.nih.gov/nuccore/157389010\" target=\"_new\">GenBank Accession no. NM_000094.3</a>)</li>");
				text.appendln("<li>Mutation nomenclature is according to the <a href=\"http://www.hgvs.org/mutnomen/\" target=\"_new\">HGVS recommendations</a></li>");
				text.appendln("</ol>");
				
				this.getModel().setTextRemarks(text.toString());
//			}
//
//			fee = db.query(FrontEndElement.class).equals(FrontEndElement.TYPE_, "support").find();
//			
//			if (fee.size() == 1)
//			{
//				this.getModel().setTextCollaborations(fee.get(0).getUsertext());
//			}
//			else
//			{
				text = new StrBuilder();
				text.appendln("<h4>Collaborators and supporters</h4>");
				text.appendln("<table width=\"100%\">");
				text.appendln("<tr>");
				text.appendln("<td><a href=\"http://www.umcg.nl/NL/UMCG/overhetumcg/organisatie/Specialismen/dermatologie/Pages/default.aspx\" target=\"_new\"><img src=\"res/img/col7a1/umcg.jpg\" width=\"200\"/></a></td>");
				text.appendln("<td><a href=\"http://www.idi.it/web/idi/home\" target=\"_new\"><img src=\"res/img/col7a1/idi.jpg\" height=\"75\"/></a></td>");
				text.appendln("<td><a href=\"http://www.eb-haus.eu/index.php?id=21&L=1\" target=\"_new\"><img src=\"res/img/col7a1/ebhaus.png\" height=\"75\"/></a></td>");
				text.appendln("<td><a href=\"http://www.guysandstthomas.nhs.uk/services/dash/dermatology/dermatology.aspx\" target=\"_new\"><img src=\"res/img/col7a1/stjohns.jpg\" height=\"75\"/></a></td>");
				text.appendln("<td><a href=\"http://www.uniklinik-freiburg.de/ims/live/hospital/dermatology_en.html\" target=\"_new\"><img src=\"res/img/col7a1/ukl-logo.jpg\" width=\"200\"/></a></td>");
				text.appendln("<td><a href=\"http://www.debra-international.org/\" target=\"_new\"><img src=\"res/img/col7a1/debra_international.png\" height=\"75\"/></a></td>");
				text.appendln("</tr>");
				text.appendln("</table>");
				
				this.getModel().setTextCollaborations(text.toString());
//			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void populateExpertSearchForm()
	{
		PhenoService phenoService   = ServiceLocator.instance().getPhenoService();
		SearchService searchService = ServiceLocator.instance().getSearchService();

		Container expertSearchForm  = this.getModel().getExpertSearchFormWrapper().getForm();

		expertSearchForm.get("__target").setValue(this.getName());
		expertSearchForm.get("select").setValue(this.getName());

		if (this.getModel().getMutationSearchCriteriaVO().getVariation() != null)
			((TextLineInput) expertSearchForm.get("variation")).setValue(this.getModel().getMutationSearchCriteriaVO().getVariation());

		if (this.getModel().getMutationSearchCriteriaVO().getCdnaPosition() != null)
			((IntInput) expertSearchForm.get("nuclno")).setValue(this.getModel().getMutationSearchCriteriaVO().getCdnaPosition());

		if (this.getModel().getMutationSearchCriteriaVO().getCodonNumber() != null)
			((IntInput) expertSearchForm.get("aano")).setValue(this.getModel().getMutationSearchCriteriaVO().getCodonNumber());

		List<ValueLabel> exonIdOptions = new ArrayList<ValueLabel>();
		exonIdOptions.add(new ValueLabel("", "Select"));
		for (ExonDTO exonSummaryVO : searchService.findAllExons())
			exonIdOptions.add(new ValueLabel(exonSummaryVO.getId(), exonSummaryVO.getName()));
		((SelectInput) expertSearchForm.get("exon_id")).setOptions(exonIdOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getExonId() != null)
			((SelectInput) expertSearchForm.get("exon_id")).setValue(this.getModel().getMutationSearchCriteriaVO().getExonId());
		else
			((SelectInput) expertSearchForm.get("exon_id")).setValue("Select");

		List<ValueLabel> typeOptions = new ArrayList<ValueLabel>();
		typeOptions.add(0, new ValueLabel("", "Select"));
		for (String mutationType : searchService.getAllVariantTypes())
			typeOptions.add(new ValueLabel(mutationType, mutationType));
		((SelectInput) expertSearchForm.get("type")).setOptions(typeOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getType() != null)
			((SelectInput) expertSearchForm.get("type")).setValue(this.getModel().getMutationSearchCriteriaVO().getType());
		else
			((SelectInput) expertSearchForm.get("type")).setValue("Select");

		List<ValueLabel> consequenceOptions = new ArrayList<ValueLabel>();
		consequenceOptions.add(0, new ValueLabel("", "Select"));
		for (String consequence : phenoService.findObservedValues("consequence"))
			consequenceOptions.add(new ValueLabel(consequence, consequence));
		((SelectInput) expertSearchForm.get("consequence")).setOptions(consequenceOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getConsequence() != null)
			((SelectInput) expertSearchForm.get("consequence")).setValue(this.getModel().getMutationSearchCriteriaVO().getConsequence());
		else
			((SelectInput) expertSearchForm.get("consequence")).setValue("Select");

		List<ValueLabel> domainOptions = new ArrayList<ValueLabel>();
		domainOptions.add(new ValueLabel("", "Select"));
		for (ProteinDomainDTO domainVO : searchService.findAllProteinDomains())
			domainOptions.add(new ValueLabel(domainVO.getDomainId(), domainVO.getDomainName()));
		((SelectInput) expertSearchForm.get("domain_id")).setOptions(domainOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getProteinDomainId() != null)
			((SelectInput) expertSearchForm.get("domain_id")).setValue(this.getModel().getMutationSearchCriteriaVO().getProteinDomainId());
		else
			((SelectInput) expertSearchForm.get("domain_id")).setValue("Select");
		
		List<ValueLabel> phenotypeOptions = new ArrayList<ValueLabel>();
		phenotypeOptions.add(new ValueLabel("", "Select"));
		for (String phenotypeName : phenoService.findObservedValues("Phenotype"))
			phenotypeOptions.add(new ValueLabel(phenotypeName, phenotypeName));
		((SelectInput) expertSearchForm.get("phenotype")).setOptions(phenotypeOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getPhenotypeId() != null)
			((SelectInput) expertSearchForm.get("phenotype")).setValue(this.getModel().getMutationSearchCriteriaVO().getPhenotypeId());
		else
			((SelectInput) expertSearchForm.get("phenotype")).setValue("Select");

		List<ValueLabel> inheritanceOptions = new ArrayList<ValueLabel>();
		inheritanceOptions.add(0, new ValueLabel("", "Select"));
		for (String inheritance : phenoService.findObservedValues("Inheritance"))
			inheritanceOptions.add(new ValueLabel(inheritance, inheritance));
		((SelectInput) expertSearchForm.get("inheritance")).setOptions(inheritanceOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getInheritance() != null)
			((SelectInput) expertSearchForm.get("inheritance")).setValue(this.getModel().getMutationSearchCriteriaVO().getInheritance());
		else
			((SelectInput) expertSearchForm.get("inheritance")).setValue("Select");
	}
}
