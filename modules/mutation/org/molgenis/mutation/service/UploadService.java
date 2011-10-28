package org.molgenis.mutation.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jxl.Workbook;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.core.OntologyTerm;
import org.molgenis.core.Publication;
import org.molgenis.core.vo.PublicationVO;
import org.molgenis.framework.db.CsvToDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.Patient;
import org.molgenis.mutation.excel.UploadBatchExcelReader;
import org.molgenis.mutation.util.MutationComparator;
import org.molgenis.mutation.util.PatientComparator;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.MutationUploadVO;
import org.molgenis.mutation.vo.ObservedValueVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.util.Entity;
import org.molgenis.util.SimpleTuple;

public class UploadService implements Serializable
{
	private static final long serialVersionUID = -5361668039279819684L;
	private Database db;
	
	public void setDatabase(Database db)
	{
		this.db = db;
	}

	/**
	 * Insert patient and set primary key
	 * 
	 * @param patient
	 * @return number of patients inserted
	 * @throws Exception
	 */
	public void insert(PatientSummaryVO patientSummaryVO) throws Exception
	{
		if (patientSummaryVO == null)
			return;

		Patient patient = this.toPatient(patientSummaryVO);

		// submissions are never inserted via patients, just select newest
		// by date
		//TODO: Do we still need that???
//		List<Submission> submissions = this.db.query(Submission.class).equals(Submission.DATE_,	patientSummaryVO.getSubmissionDate()).sortDESC("id").find();
//
//		if (submissions.size() > 0)
//		{
//			patientSummaryVO.getPatient().setSubmission(submissions.get(0));
//		}

		// mutations are never inserted via patients, just select
		int i = 1;
		for (MutationSummaryVO mutationSummaryVO : patientSummaryVO.getVariantSummaryVOList())
		{
			List<Mutation> mutations = this.db.query(Mutation.class).equals(Mutation.CDNA_NOTATION, mutationSummaryVO.getCdnaNotation()).find();

//			System.out.println(">>> Queried: " + mutationSummaryVO.getCdnaNotation() + ", found: " + mutations.size());
			if (mutations.size() != 1)
				throw new Exception("ERROR: No mutation found for " + mutationSummaryVO.getCdnaNotation());

			if (i == 1)
				patient.setMutation1_Id(mutations.get(0).getId());
			else
				patient.setMutation2_Id(mutations.get(0).getId());
			
			i++;
		}

		// phenotypes are never inserted via patients, just select
		if (patientSummaryVO.getPhenotypeId() == null)
		{
			List<MutationPhenotype> phenotypes = this.db.query(MutationPhenotype.class).equals(MutationPhenotype.MAJORTYPE, patientSummaryVO.getPhenotypeMajor()).equals(MutationPhenotype.SUBTYPE, patientSummaryVO.getPhenotypeSub()).find();

			if (phenotypes.size() != 1)
				throw new Exception("No phenotype found for " + patientSummaryVO.getPhenotypeMajor() + ", " + patientSummaryVO.getPhenotypeSub());

			patientSummaryVO.setPhenotypeId(phenotypes.get(0).getId());
		}
		patient.setPhenotype_Id(patientSummaryVO.getPhenotypeId());

		// Publications are never inserted via patients, just select
		if (CollectionUtils.isNotEmpty(patientSummaryVO.getPublicationVOList()))
		{
			List<Integer> publicationIds = new ArrayList<Integer>();

			for (PublicationVO publicationVO : patientSummaryVO.getPublicationVOList())
			{
				if (publicationVO.getPubmedId() == null)
					continue;

				List<OntologyTerm> pubmedTerms = this.db.query(OntologyTerm.class).equals(OntologyTerm.NAME, publicationVO.getPubmedId()).find();
				
				if (pubmedTerms.size() != 1)
					continue;

				List<Publication> publications = this.db.query(Publication.class).equals(Publication.PUBMEDID_NAME,	pubmedTerms.get(0).getId()).find();

				if (publications.size() != 1)
					throw new Exception("No publication found for Pubmed ID " + publicationVO.getPubmedId());
					
				publicationIds.add(publications.get(0).getId());
			}
				
			patient.setPatientreferences_Id(publicationIds);
		}
		patient.setSubmission_Id(1);
		// Insert patient
		this.db.add(patient);
		
		// Insert ObservedValues
		
		for (ObservedValueVO observedValueVO : patientSummaryVO.getObservedValueVOList())
		{
			if (observedValueVO == null)
				return;
			
			List<ObservableFeature> features = this.db.query(ObservableFeature.class).equals(ObservableFeature.NAME, observedValueVO.getFeatureName()).find();
			
			if (features.size() != 1)
				return;
			
			List<Patient> patients = this.db.query(Patient.class).equals(Patient.IDENTIFIER, observedValueVO.getTargetName()).find();
			
			if (patients.size() != 1)
				return;
	
			ObservedValue observedValue = new ObservedValue();
			observedValue.setFeature(features.get(0));
			observedValue.setInvestigation(1);
			observedValue.setTarget(patient.getId());
			observedValue.setValue(observedValueVO.getValue());
			
			this.db.add(observedValue);
		}
	}

	/**
	 * Insert batch of patients. To be refactored.
	 * 
	 * @param file
	 * @return number of patients inserted
	 * @throws Exception
	 */
	public int insertBatch(File file, CsvToDatabase<Entity> uploadBatchCsvReader) throws Exception
	{
		Workbook workbook             = Workbook.getWorkbook(file);
		UploadBatchExcelReader reader = new UploadBatchExcelReader();
		reader.setUploadBatchCsvReader(uploadBatchCsvReader);
		int count                     = reader.importSheet(this.db, workbook.getSheet("Patients"), new SimpleTuple(), DatabaseAction.ADD_IGNORE_EXISTING, "");
		return count;
	}

	/**
	 * Get the biggest identifier without the leading 'P'
	 * 
	 * @return biggest identifier without the leading 'P'
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Integer getMaxPatientIdentifier() throws DatabaseException, ParseException {
		List<Patient> patients = this.db.query(Patient.class).find();

		if (CollectionUtils.isEmpty(patients)) {
			return 0;
		}

		Collections.sort(patients, new PatientComparator());
		return Integer.valueOf(patients.get(patients.size() - 1).getIdentifier().substring(1));
	}

	/**
	 * Insert mutation and set primary key
	 * @param mutation
	 * @return number of mutations inserted
	 * @throws IOException 
	 * @throws Exception 
	 */
	public int insert(MutationUploadVO mutationUploadVO) throws Exception
	{
		int count = 0;

		// Exons are never inserted via mutations, just select
		mutationUploadVO.getMutation().setExon(this.db.findById(Exon.class, mutationUploadVO.getExonId()));

		// Genes are never inserted via mutations, just select
		mutationUploadVO.getMutation().setGene(this.db.query(MutationGene.class).equals(MutationGene.SYMBOL, mutationUploadVO.getGeneSymbol()).find().get(0));

		// Insert mutation and set primary key
		count = this.db.add(mutationUploadVO.getMutation());

		List<Mutation> mutations = this.db.findByExample(mutationUploadVO.getMutation());
		if (mutations.size() == 1)
		{
			mutationUploadVO.getMutation().setId(mutations.get(0).getId());
		}

		return count;
	}

	/**
	 * Get the biggest identifier without the leading 'P'
	 * @return biggest identifier without the leading 'P'
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Integer getMaxMutationIdentifier() throws DatabaseException, ParseException
	{
		List<Mutation> mutations = this.db.query(Mutation.class).find();
		
		if (CollectionUtils.isEmpty(mutations))
			return 0;

		Collections.sort(mutations, new MutationComparator());
		return Integer.valueOf(mutations.get(mutations.size() - 1).getIdentifier().substring(1));
	}

	private Patient toPatient(PatientSummaryVO patientSummaryVO)
	{
		Patient patient = new Patient();

		patient.setIdentifier(patientSummaryVO.getPatientIdentifier());
		patient.setName(patientSummaryVO.getPatientName());
		patient.setNumber(patientSummaryVO.getPatientNumber());
		patient.setConsent(patientSummaryVO.getPatientConsent());
		patient.setAge(patientSummaryVO.getPatientAge());
		patient.setGender(patientSummaryVO.getPatientGender());
		patient.setEthnicity(patientSummaryVO.getPatientEthnicity());
		patient.setDeceased(patientSummaryVO.getPatientDeceased());
		patient.setDeath_Cause(patientSummaryVO.getPatientDeathCause());
		patient.setMmp1_Allele1(patientSummaryVO.getPatientMmp1Allele1());
		patient.setMmp1_Allele2(patientSummaryVO.getPatientMmp1Allele2());
		if (StringUtils.isNotEmpty(patientSummaryVO.getVariantComment()))
			patient.setMutation2remark(patientSummaryVO.getVariantComment());

//		patientSummaryVO.setPatientMaterialList(patient.getMaterial_Name());
//			
//		Submission submission  = this.db.findById(Submission.class, patient.getSubmission_Id());
//		MolgenisUser submitter = this.db.findById(MolgenisUser.class, submission.getSubmitters_Id().get(0));
//		patientSummaryVO.setSubmitterDepartment(submitter.getDepartment());
//		patientSummaryVO.setSubmitterInstitute(submitter.getInstitute());
//		patientSummaryVO.setSubmitterCity(submitter.getCity());
//		patientSummaryVO.setSubmitterCountry(submitter.getCountry());

		return patient;
	}
}
