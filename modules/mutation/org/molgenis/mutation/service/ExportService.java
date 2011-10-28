package org.molgenis.mutation.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.molgenis.core.Publication;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.Patient;

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
		row.add("Protein change_1");
		row.add("Exon/Intron_1");
		row.add("Consequence_1");
		row.add("Inheritance_1");
		row.add("cDNA change_2");
		row.add("Protein change_2");
		row.add("Exon/Intron_2");
		row.add("Consequence_2");
		row.add("Inheritance_2");
		row.add("IF LH7:2");
		row.add("IF Retention COLVII");
		row.add("EM AF_no");
		row.add("EM AF_structure");
		row.add("EM_Retention COLVII");
		row.add("MMP1 Allele1(rs1799750)");
		row.add("MMP1 Allele2 (rs1799750)");
		row.add("Reference");
		row.add("PubMed ID");
		row.add("Gender");
		row.add("Age");
		row.add("Ethnicity");
		row.add("Deceased");
		row.add("Cause of death");
		row.add("Blistering");
		row.add("Location");
		row.add("Hands");
		row.add("Feet");
		row.add("Arms");
		row.add("Legs");
		row.add("Proximal body flexures");
		row.add("Trunk");
		row.add("Mucosa");
		row.add("Skin atrophy");
		row.add("Milia");
		row.add("Nail dystrophy");
		row.add("Albopapuloid papules");
		row.add("Pruritic papules");
		row.add("Alopecia");
		row.add("Squamous cell carcinoma(s)");
		row.add("Revertant skin patch(es)");
		row.add("Mechanism");
		row.add("Flexion contractures");
		row.add("Pseudosyndactyly (hands)");
		row.add("Microstomia");
		row.add("Ankyloglossia");
		row.add("Swallowing difficulties/ dysphagia/ oesophagus strictures");
		row.add("Growth retardation");
		row.add("Anaemia");
		row.add("Renal failure");
		row.add("Dilated cardiomyopathy");
		row.add("Other");
		
		return row;
	}

	private List<String> exportRow(Patient patient) throws DatabaseException
	{
		List<String> row = new ArrayList<String>();
		row.add(patient.getIdentifier());
		row.add(patient.getNumber());
		row.add(patient.getConsent());
		MutationPhenotype phenotype = this.db.findById(MutationPhenotype.class, patient.getPhenotype_Id());
		row.add(phenotype.getMajortype());
		row.add(phenotype.getSubtype());
		Mutation mutation1          = this.db.findById(Mutation.class, patient.getMutation1_Id());
		row.add(mutation1.getCdna_Notation());
		row.add(mutation1.getAa_Notation());
		row.add(mutation1.getExon_Name());
		row.add(mutation1.getConsequence());
		row.add(mutation1.getInheritance());
		Mutation mutation2          = this.db.findById(Mutation.class, patient.getMutation2_Id());
		if (mutation2 != null)
		{
			row.add(mutation2.getCdna_Notation());
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
		}
//		List<I_F> ifs               = this.db.query(I_F.class).equals(I_F.PATIENT, patient.getId()).find();
//		row.add(ifs.get(0).getValue());
//		row.add(ifs.get(0).getRetention());
//
//		List<E_M> ems               = this.db.query(E_M.class).equals(E_M.PATIENT, patient.getId()).find();
//		row.add(ems.get(0).getNumber());
//		row.add(ems.get(0).getAppearance());
//		row.add(ems.get(0).getRetention());

		row.add(patient.getMmp1_Allele1());
		row.add(patient.getMmp1_Allele2());

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

		row.add(patient.getGender());
		row.add(patient.getAge());
		row.add(patient.getEthnicity());
		row.add(patient.getDeceased());
		row.add(patient.getDeath_Cause());

//		PhenotypeDetails phenotypeDetails = this.db.findById(PhenotypeDetails.class, patient.getPhenotype_Details_Id());
//		row.add(phenotypeDetails.getBlistering());
//		row.add(phenotypeDetails.getLocation());
//		row.add(phenotypeDetails.getHands());
//		row.add(phenotypeDetails.getFeet());
//		row.add(phenotypeDetails.getArms());
//		row.add(phenotypeDetails.getLegs());
//		row.add(phenotypeDetails.getProximal_Body_Flexures());
//		row.add(phenotypeDetails.getTrunk());
//		row.add(phenotypeDetails.getMucous_Membranes());
//		row.add(phenotypeDetails.getSkin_Atrophy());
//		row.add(phenotypeDetails.getMilia());
//		row.add(phenotypeDetails.getNail_Dystrophy());
//		row.add(phenotypeDetails.getAlbopapuloid_Papules());
//		row.add(phenotypeDetails.getPruritic_Papules());
//		row.add(phenotypeDetails.getAlopecia());
//		row.add(phenotypeDetails.getSquamous_Cell_Carcinomas());
//		row.add(phenotypeDetails.getRevertant_Skin_Patch());
//		row.add(phenotypeDetails.getMechanism());
//		row.add(phenotypeDetails.getFlexion_Contractures());
//		row.add(phenotypeDetails.getPseudosyndactyly_Hands());
//		row.add(phenotypeDetails.getMicrostomia());
//		row.add(phenotypeDetails.getAnkyloglossia());
//		row.add(phenotypeDetails.getDysphagia());
//		row.add(phenotypeDetails.getGrowth_Retardation());
//		row.add(phenotypeDetails.getAnemia());
//		row.add(phenotypeDetails.getRenal_Failure());
//		row.add(phenotypeDetails.getDilated_Cardiomyopathy());
//		row.add(phenotypeDetails.getOther());

		return row;
	}

}
