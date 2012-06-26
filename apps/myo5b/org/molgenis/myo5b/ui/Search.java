/* Date:        April 4, 2011
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.3
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.myo5b.ui;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.ui.ScreenController;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.IntInput;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.TextLineInput;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.service.SearchService;
import org.molgenis.mutation.ui.HtmlFormWrapper;
import org.molgenis.mutation.ui.search.SearchPlugin;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.util.ValueLabel;

public class Search extends SearchPlugin
{
	/* The serial version UID of this class. Needed for serialization. */
	private static final long serialVersionUID = 4159412082076885902L;

	public Search(String name, ScreenController<?> parent)
	{
		super(name, parent);
		this.getModel().setPatientPager("generated-res/mvid/patientPager.jsp");
		this.getModel().setMutationPager("generated-res/mvid/mutationPager.jsp");
		this.getModel().setPatientViewer("/org/molgenis/mutation/ui/search/patient.ftl");
		this.getModel().getMbrowse().setShowNames(false);
		this.getModel().setExpertSearchFormWrapper(new HtmlFormWrapper(new ExpertSearchForm()));
	}

	@Override
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
		for (String mutationType : phenoService.findObservedValues("Type of mutation"))
			typeOptions.add(new ValueLabel(mutationType, mutationType));
		((SelectInput) expertSearchForm.get("type")).setOptions(typeOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getType() != null)
			((SelectInput) expertSearchForm.get("type")).setValue(this.getModel().getMutationSearchCriteriaVO().getType());
		else
			((SelectInput) expertSearchForm.get("type")).setValue("Select");

		List<ValueLabel> phenotypeOptions = new ArrayList<ValueLabel>();
		phenotypeOptions.add(new ValueLabel("", "Select"));
		for (String phenotypeName : phenoService.findObservedValues("Onset"))
			phenotypeOptions.add(new ValueLabel(phenotypeName, phenotypeName));
		((SelectInput) expertSearchForm.get("phenotype")).setOptions(phenotypeOptions);
		if (this.getModel().getMutationSearchCriteriaVO().getPhenotypeId() != null)
			((SelectInput) expertSearchForm.get("phenotype")).setValue(this.getModel().getMutationSearchCriteriaVO().getPhenotypeId());
		else
			((SelectInput) expertSearchForm.get("phenotype")).setValue("Select");
	}

	@Override
	public void reload(Database db)
	{
		super.reload(db);
	}
}
