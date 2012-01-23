package org.molgenis.mutation.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.core.Publication;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.Patient;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.submission.Submission;

public class ExportService implements Serializable
{
	private static final long serialVersionUID = 941152681943662622L;
	private Database db;
	
	public ExportService()
	{
	}
	
	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public String exportCsv() throws DatabaseException
	{
		List<Patient> patients = this.db.query(Patient.class).equals(Patient.CONSENT, "publication").sortASC(Patient.IDENTIFIER).find();
		
		String result          = "";

		List<String> header = this.exportHeader();
		result += StringUtils.join(header, ",") + "\n";

		for (Patient patient : patients)
		{
//			System.out.println(">>> patient.submission==" + patient.getSubmission_Id());
			List<String> columns = this.exportRow(patient);
			result += StringUtils.join(columns, ",") + "\n";
		}
		
		return result;
	}
	
	public String exportCsv(Date submissionDate) throws DatabaseException
	{
		List<Patient> patients = this.db.query(Patient.class).equals(Patient.CONSENT, "publication").sortASC(Patient.IDENTIFIER).find();
		
		String result          = "";

		List<String> header = this.exportHeader();
		result += StringUtils.join(header, ",") + "\n";

		for (Patient patient : patients)
		{
			Submission submission = this.db.findById(Submission.class, patient.getSubmission_Id());
			if (submission.getDate().before(submissionDate))
				continue;
//			System.out.println(">>> patient.submission==" + patient.getSubmission_Id());
			List<String> columns = this.exportRow(patient);
			result += StringUtils.join(columns, ",") + "\n";
		}
		
		return result;
	}

	private List<String> exportHeader()
	{
		List<String> row = new ArrayList<String>();
		row.add("Identifier");
		row.add("Local patient number");
		row.add("Patient Consent");
		row.add("Phenotype major type");
		row.add("Phenotype Subtype");
		row.add("cDNA change_1");
		row.add("RNA change_1");
		row.add("Protein change_1");
		row.add("Exon/Intron_1");
		row.add("Consequence_1");
		row.add("Inheritance_1");
		row.add("cDNA change_2");
		row.add("RNA change_2");
		row.add("Protein change_2");
		row.add("Exon/Intron_2");
		row.add("Consequence_2");
		row.add("Inheritance_2");
		row.add("Reference");
//		row.add("Blistering");
//		row.add("Location");
//		row.add("Hands");
//		row.add("Feet");
//		row.add("Arms");
//		row.add("Legs");
//		row.add("Proximal body flexures");
//		row.add("Trunk");
//		row.add("Mucosa");
//		row.add("Skin atrophy");
//		row.add("Milia");
//		row.add("Nail dystrophy");
//		row.add("Albopapuloid papules");
//		row.add("Pruritic papules");
//		row.add("Alopecia");
//		row.add("Squamous cell carcinoma(s)");
//		row.add("Revertant skin patch(es)");
//		row.add("Mechanism");
//		row.add("Flexion contractures");
//		row.add("Pseudosyndactyly (hands)");
//		row.add("Microstomia");
//		row.add("Ankyloglossia");
//		row.add("Swallowing difficulties/ dysphagia/ oesophagus strictures");
//		row.add("Growth retardation");
//		row.add("Anaemia");
//		row.add("Renal failure");
//		row.add("Dilated cardiomyopathy");
//		row.add("Other");
		
		return row;
	}

	private List<String> exportRow(Patient patient) throws DatabaseException
	{
		List<String> row = new ArrayList<String>();
		row.add(patient.getIdentifier());
		row.add(patient.getNumber());
		row.add(patient.getConsent());

		List<ObservedValue> phenotypes = this.db.query(ObservedValue.class).equals(ObservedValue.TARGET, patient.getId()).find();
		List<String> phenotypeNames    = new ArrayList<String>();
		for (ObservedValue phenotype : phenotypes)
		{
			phenotypeNames.add(phenotype.getValue());
		}
		row.add(StringUtils.join(phenotypeNames, ", "));
		
		List<Mutation> mutations       = this.db.query(Mutation.class).in(Mutation.ID, patient.getMutations_Id()).find();
		if (mutations.size() > 0)
		{
			Mutation mutation1         = mutations.get(0);
			row.add(mutation1.getCdna_Notation());
			row.add("r.?");
			row.add(mutation1.getAa_Notation());
			row.add(mutation1.getExon_Name());
			row.add(mutation1.getConsequence());
			row.add(mutation1.getInheritance());
		}
		else
		{
			row.add("NA");
			row.add("");
			row.add("");
			row.add("");
			row.add("");
			row.add("");
		}
		if (mutations.size() > 1)
		{
			Mutation mutation2 = mutations.get(1);
			row.add(mutation2.getCdna_Notation());
			row.add("r.?");
			row.add(mutation2.getAa_Notation());
			row.add(mutation2.getExon_Name());
			row.add(mutation2.getConsequence());
			row.add(mutation2.getInheritance());
		}
		else
		{
			row.add(patient.getMutation2remark());
			row.add("");
			row.add("");
			row.add("");
			row.add("");
			row.add("");
		}

		List<Publication> publications = this.db.query(Publication.class).in(Publication.ID, patient.getPatientreferences_Id()).find();
		List<String> publicationNames  = new ArrayList<String>();
		List<String> publicationPudmed = new ArrayList<String>();
		for (Publication publication : publications)
		{
			publicationNames.add(publication.getName());
			publicationPudmed.add(publication.getPubmedID_Name());
		}
		row.add(StringUtils.join(publicationNames, ";"));
		row.add(StringUtils.join(publicationPudmed, ";"));

//		row.add(patient.getGender());
//		row.add(patient.getAge());
//		row.add(patient.getEthnicity());
//		row.add(patient.getDeceased());
//		row.add(patient.getDeath_Cause());

		return row;
	}

}
