<h3>Submission wizard: Single patient</h3>
<p>
This wizard will guide you through the submission of a single patient. Please provide a local patient identifier. Submission starts by selecting a patient phenotype. For entering a mutation, please select a mutation from the drop-down list(s). If a mutation is not yet known in the database, please enter a new mutation by using the Submission wizard: New mutation, accessible by clicking "Submit new mutation". Details buttons can be used to submit details on clinical, immunofluorescence, and electron microscopy phenotypes.
</p>
<p>
Data will be inserted after curation by the curator. If your patient does not fit in the predefined fields, please use the comment box.
</p>
<p>
<i>Patient consent</i><br/>
You need to indicate that the patient consented for inclusion in the database, even if the data in the database are anonymised. If no consent is given, only a phenotype, genotype, and results of immunofluorescence and electron microscopy analyses will be shown, without any details.
</p>
<p>
For obtaining consent, you can download the provided <a href="res/mutation/col7a1/consent_form.pdf">consent form</a>. We do currently not require you to upload the consent forms to our system. If you indicate that the patient consented, you confirm that a signed consent form is in your possession.
</p>
<hr/>
<p>* Field is required.</p>
<#assign form = screen.patientForm>
<script language="JavaScript">
function toggleForm(phenotype)
{
	if (phenotype == "3" || phenotype == "6" || phenotype == "2" || phenotype == "5" || phenotype == "16" || phenotype == "4" || phenotype == "17" || phenotype == "1")
	{
		document.getElementById("mutation2_label").style.display = "none";
		document.getElementById("mutation2_input").style.display = "none";
	}
	else
	{
		document.getElementById("mutation2_label").style.display = "block";
		document.getElementById("mutation2_input").style.display = "block";
	}
}
</script>
<table border="0" cellpadding="4" cellspacing="4">
<tr><td>Local patient No *</td><td>${form.number}</td><td></td><td></td></tr>
<tr><td>Phenotype *</td><td colspan="3">${form.phenotype}</td></tr>
<tr><td>Mutation 1 *</td><td>${form.mutation1} <a href="molgenis.do?__target=${screen.name}&__action=newMutation&referer=1">Add</a></td><td><div id="mutation2_label" style="display:none">Mutation 2 *</div></td><td><div id="mutation2_input" style="display:none">${form.mutation2} <a href="molgenis.do?__target=${screen.name}&__action=newMutation&referer=2">Add</a></div></td></tr>
<tr><td>PubMed ID</td><td>${form.pubmed}</td><td>Link to Article</td><td>${form.pdf}</td></tr>
<tr><td>Age</td><td>${form.age}</td><td>Gender</td><td>${form.gender}</td></tr>
<tr><td>Ethnicity</td><td>${form.ethnicity}</td><td></td><td></td></tr>
<tr><td>Deceased?</td><td>${form.deceased}</td><td>Cause of death</td><td>${form.death_cause}</td></tr>
<tr><td>MMP1 allele 1</td><td>${form.mmp1_allele1}</td><td>MMP1 allele 1</td><td>${form.mmp1_allele2}</td></tr>
<tr><td>Consent *</td><td>${form.consent}</td><td></td><td></td></tr>
<tr><td>Comment</td><td colspan="3">${form.comment}</td></tr>
<#--
<tr><td colspan="4"><hr/></td></tr>
<tr><td colspan="3"></td><td align="right"><input type="submit" value="Proceed" onclick="__action.value='insertPatient';return true;"/></td></tr>
-->
</table>

<h4>Phenotypic Details</h4>
<table cellpadding="4">
<tr><td>Blistering</td><td>${form.blistering}</td></tr>
<tr><td>Location</td><td>${form.location}</td></tr>
<tr><td>Hands</td><td>${form.hands}</td></tr>
<tr><td>Feet</td><td>${form.feet}</td></tr>
<tr><td>Arms</td><td>${form.arms}</td></tr>
<tr><td>Legs</td><td>${form.legs}</td></tr>
<tr><td>Proximal body flexures</td><td>${form.proximal_body_flexures}</td></tr>
<tr><td>Trunk</td><td>${form.trunk}</td></tr>
<tr><td>Mucous membranes</td><td>${form.mucous_membranes}</td></tr>
<tr><td>Skin atrophy</td><td>${form.skin_atrophy}</td></tr>
<tr><td>Milia</td><td>${form.milia}</td></tr>
<tr><td>Nail dystrophy</td><td>${form.nail_dystrophy}</td></tr>
<tr><td>Albopapuloid papules</td><td>${form.albopapuloid_papules}</td></tr>
<tr><td>Pruritic papules</td><td>${form.pruritic_papules}</td></tr>
<tr><td>Alopecia</td><td>${form.alopecia}</td></tr>
<tr><td>Squamous cell carcinoma(s)</td><td>${form.squamous_cell_carcinomas}</td></tr>
<tr><td>Revertant skin patch</td><td>${form.revertant_skin_patch}</td></tr>
<tr><td>Mechanism</td><td>${form.mechanism}</td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td>Flexion contractures</td><td>${form.flexion_contractures}</td></tr>
<tr><td>Pseudosyndactyly (hands)</td><td>${form.pseudosyndactyly_hands}</td></tr>
<tr><td>Microstomia</td><td>${form.microstomia}</td></tr>
<tr><td>Ankyloglossia</td><td>${form.ankyloglossia}</td></tr>
<tr><td>Swallowing difficulties/dysphagia/oesophagus strictures</td><td>${form.dysphagia}</td></tr>
<tr><td>Growth retardation</td><td>${form.growth_retardation}</td></tr>
<tr><td>Anemia</td><td>${form.anemia}</td></tr>
<tr><td>Renal failure</td><td>${form.renal_failure}</td></tr>
<tr><td>Dilated cardiomyopathy</td><td>${form.dilated_cardiomyopathy}</td></tr>
<tr><td>Other</td><td>${form.other}</td></tr>
<tr><th colspan="2">Immunofluorescence antigen mapping</th></tr>
<tr><td>Amount of type VII collagen</td><td>${form.if_value}</td></tr>
<tr><td>Retention of type VII Collagen in basal cells</td><td>${form.if_retention}</td></tr>
<tr><td>Comments</td><td>${form.if_description}</td></tr>
<tr><th colspan="2">Electron Microscopy</th></tr>
<tr><td>Anchoring fibrils number</td><td>${form.em_fibrils}</td></tr>
<tr><td>Ultrastructure</td><td>${form.em_appearance}</td></tr>
<tr><td>Retention of type VII Collagen in basal cells</td><td>${form.em_retention}</td></tr>
<tr><td>Comments</td><td>${form.em_description}</td></tr>
<tr><td colspan="2"><hr/></td></tr>
<tr><td colspan="2" align="right"><input type="submit" value="Proceed" onclick="__action.value='insertPatient';return true;"/></td></tr>
</table>


[<a href="molgenis.do?__target=${screen.name}&__action=newBatch">Back to submission wizard: multiple patients</a>]