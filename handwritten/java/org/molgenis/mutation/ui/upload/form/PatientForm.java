package org.molgenis.mutation.ui.upload.form;

import java.util.Vector;

import org.molgenis.framework.ui.html.CheckboxInput;
import org.molgenis.framework.ui.html.Container;
import org.molgenis.framework.ui.html.SelectInput;
import org.molgenis.framework.ui.html.StringInput;
import org.molgenis.util.ValueLabel;

public class PatientForm extends Container
{
	public PatientForm()
	{
		this.add(new StringInput("age"));
		this.add(new SelectInput("gender", "unknown"));
		this.add(new StringInput("ethnicity"));
		Vector<ValueLabel> deceasedOptions = new Vector<ValueLabel>();
		deceasedOptions.add(new ValueLabel("deceased", ""));
		this.add(new CheckboxInput("deceased", "", "Is patient deceased?", deceasedOptions, new Vector<String>()));
		this.add(new StringInput("death_cause"));
		this.add(new StringInput("mmp1_allele1"));
		this.add(new StringInput("mmp1_allele2"));
		this.add(new SelectInput("consent", "no"));
		this.add(new StringInput("identifier"));
		this.add(new SelectInput("mutation1", ""));
		this.add(new SelectInput("mutation2", ""));
		this.add(new StringInput("number", ""));
		this.add(new StringInput("pdf", ""));
		this.add(new SelectInput("phenotype", ""));
		this.add(new StringInput("pubmed", ""));
		this.add(new StringInput("comment", ""));
		
		// Phenotype details
		this.add(new SelectInput("blistering", "unknown"));
		this.add(new SelectInput("location", "unknown"));
		this.add(new SelectInput("hands", "unknown"));
		this.add(new SelectInput("feet", "unknown"));
		this.add(new SelectInput("arms", "unknown"));
		this.add(new SelectInput("legs", "unknown"));
		this.add(new SelectInput("proximal_body_flexures", "unknown"));
		this.add(new SelectInput("trunk", "unknown"));
		this.add(new SelectInput("mucous_membranes", "unknown"));
		this.add(new SelectInput("skin_atrophy", "unknown"));
		this.add(new SelectInput("milia", "unknown"));
		this.add(new SelectInput("nail_dystrophy", "unknown"));
		this.add(new SelectInput("albopapuloid_papules", "unknown"));
		this.add(new SelectInput("pruritic_papules", "unknown"));
		this.add(new SelectInput("alopecia", "unknown"));
		this.add(new SelectInput("squamous_cell_carcinomas", "unknown"));
		this.add(new SelectInput("revertant_skin_patch", "unknown"));
		this.add(new StringInput("mechanism", ""));
		this.add(new SelectInput("flexion_contractures", "unknown"));
		this.add(new SelectInput("pseudosyndactyly_hands", "unknown"));
		this.add(new SelectInput("microstomia", "unknown"));
		this.add(new SelectInput("ankyloglossia", "unknown"));
		this.add(new SelectInput("dysphagia", "unknown"));
		this.add(new SelectInput("growth_retardation", "unknown"));
		this.add(new SelectInput("anemia", "unknown"));
		this.add(new SelectInput("renal_failure", "unknown"));
		this.add(new SelectInput("dilated_cardiomyopathy", "unknown"));
		this.add(new StringInput("other"));
		this.add(new SelectInput("if_value", "unknown"));
		this.add(new SelectInput("if_retention", "unknown"));
		this.add(new StringInput("if_description"));
		this.add(new SelectInput("em_fibrils", "unknown"));
		this.add(new SelectInput("em_appearance", "unknown"));
		this.add(new SelectInput("em_retention", "unknown"));
		this.add(new StringInput("em_description"));
	}
}
