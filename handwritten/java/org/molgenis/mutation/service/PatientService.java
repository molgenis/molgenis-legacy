package org.molgenis.mutation.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import jxl.Workbook;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.OntologyTerm;
import org.molgenis.core.Publication;
import org.molgenis.core.service.PublicationService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.mutation.Antibody;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.Patient;
import org.molgenis.mutation.PhenotypeDetails;
import org.molgenis.mutation.excel.UploadBatchExcelReader;
import org.molgenis.mutation.util.PatientComparator;
import org.molgenis.mutation.vo.ObservedValueVO;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.mutation.vo.PhenotypeDetailsVO;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.services.PubmedService;
import org.molgenis.services.pubmed.PubmedArticle;
import org.molgenis.submission.Submission;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class PatientService implements Serializable
{
	private static final long serialVersionUID       = -5343468595949980106L;
	private Database db                              = null;
	private static PatientService patientService     = null;
	private HashMap<Integer, PatientSummaryVO> cache = new HashMap<Integer, PatientSummaryVO>();

	// private constructor, use singleton instance
	private PatientService(Database db)
	{
		this.db = db;
	}

	public static PatientService getInstance(Database db)
	{
		if (patientService == null)
			patientService = new PatientService(db);

		return patientService;
	}

	public List<PatientSummaryVO> findPatients(PatientSearchCriteriaVO criteria) throws DatabaseException, java.text.ParseException
	{
		Query<Patient> query = this.db.query(Patient.class);

		if (criteria.getPid() != null)
		{
			query = query.equals(Patient.IDENTIFIER, criteria.getPid());
		}
		if (criteria.getMutationId() != null)
		{
			Mutation mutation = this.db.findById(Mutation.class, criteria.getMutationId());
			query = query.equals(Patient.MUTATION1, mutation.getId()).or().equals(Patient.MUTATION2, mutation.getId());
		}
		if (criteria.getMid() != null)
		{
			List<Mutation> mutations = this.db.query(Mutation.class).equals(Mutation.IDENTIFIER, criteria.getMid()).find();
			if (mutations.size() == 1)
			{
				Integer mutationId = mutations.get(0).getId();
				query = query.equals(Patient.MUTATION1, mutationId).or().equals(Patient.MUTATION2, mutationId);
			}
		}
		if (criteria.getConsent() != null)
		{
			if (criteria.getConsent())
			{
				query = query.in(Patient.CONSENT, Arrays.asList(new String[] { "publication", "yes" }));
			}
			else
			{
				query = query.equals(Patient.CONSENT, "no");
			}
		}
		if (criteria.getSubmissionId() != null)
		{
			query = query.equals(Patient.SUBMISSION, criteria.getSubmissionId());
		}
		if (criteria.getUserId() != null)
		{
			List<Submission> submissions = this.db.query(Submission.class).equals(Submission.SUBMITTERS, criteria.getUserId()).find();
			List<Integer> submissionIds  = new ArrayList<Integer>();
			for (Submission submission : submissions)
			{
				submissionIds.add(submission.getId());
			}
			query = query.in(Patient.SUBMISSION, submissionIds);
		}
		query = query.sortASC(Patient.IDENTIFIER);

		if (query.getRules().length > 0)
		{
			return this.toPatientSummaryVOList(query.find());
		}
		else // Return an empty list, if query is "empty". find() would return all objects.
		{
			return new ArrayList<PatientSummaryVO>();
		}
	}

	public PatientSummaryVO findPatientById(Integer id) throws Exception
	{
		return this.toPatientSummaryVO(this.db.findById(Patient.class, id));
	}

	public PhenotypeDetailsVO findPhenotypeDetails(String pid) throws DatabaseException, ParseException
	{
		List<Patient> patients = this.db.query(Patient.class).equals(Patient.IDENTIFIER, pid).in(Patient.CONSENT, Arrays.asList(new String[] { "publication", "yes" })).find();
		
		if (CollectionUtils.isEmpty(patients))
			throw new IllegalArgumentException("Unknown patient identifier '" + pid + "'");
		
		if (this.db instanceof JDBCDatabase)
		{
			PhenotypeDetailsVO phenotypeDetailsVO = new PhenotypeDetailsVO();
			phenotypeDetailsVO.setPatientId(patients.get(0).getId());
			phenotypeDetailsVO.setPatientIdentifier(patients.get(0).getIdentifier());
			phenotypeDetailsVO.setObservedValues(new HashMap<String, List<ObservedValueVO>>());

			List<Protocol> protocols = this.db.query(Protocol.class).find();
			
			if (CollectionUtils.isEmpty(protocols))
				return phenotypeDetailsVO;
			
			for (Protocol protocol : protocols)
			{
				// ignore protocols without features
				if (protocol.getFeatures_Id().size() == 0)
					continue;

				phenotypeDetailsVO.getObservedValues().put(" " + protocol.getName(), new ArrayList<ObservedValueVO>());

				List<ObservedValue> observedValues = this.db.query(ObservedValue.class).equals(ObservedValue.TARGET, patients.get(0).getId()).in(ObservedValue.FEATURE, protocol.getFeatures_Id()).find();
				for (ObservedValue observedValue : observedValues)
				{
					ObservedValueVO observedValueVO = new ObservedValueVO();
					observedValueVO.setFeatureName(observedValue.getFeature_Name());
					observedValueVO.setValue(observedValue.getValue());
					phenotypeDetailsVO.getObservedValues().get(" " + protocol.getName()).add(observedValueVO);
				}
			}
			return phenotypeDetailsVO;
		}
		else if (this.db instanceof JpaDatabase)
		{
			throw new UnsupportedOperationException("To be implemented");
		}
		else
			throw new UnsupportedOperationException("Unsupported database mapper");
	}

	public List<PatientSummaryVO> getAllPatientSummaries() throws DatabaseException, ParseException
	{
		List<Patient> patients = this.db.query(Patient.class).sortASC(Patient.IDENTIFIER).find();
		return this.toPatientSummaryVOList(patients);
	}

	/**
	 * Get number of patients in the database
	 * 
	 * @return number of patients
	 * @throws DatabaseException
	 */
	public int getNumPatients() throws DatabaseException {
		return this.db.count(Patient.class);
	}

	/**
	 * Get number of unpublished patients.
	 * 
	 * @return number of unpublished patients
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getNumUnpublishedPatients() throws DatabaseException, ParseException
	{
		if (this.db instanceof JDBCDatabase)
			return ((JDBCDatabase) this.db).sql("SELECT DISTINCT id FROM Patient WHERE NOT EXISTS (SELECT id FROM Patient_publications WHERE Patient.id = Patient_publications.Patient)").size();
		else if (this.db instanceof JpaDatabase)
		{
			javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT COUNT(DISTINCT p.id) FROM Patient p LEFT OUTER JOIN Patient_publications pp ON (p.id = pp.Patient) WHERE pp.publications IS NULL");
			return Integer.valueOf(q.getSingleResult().toString());
		}
		else
			throw new DatabaseException("Unsupported database mapper");
	}

	/**
	 * Get the biggest identifier without the leading 'P'
	 * 
	 * @return biggest identifier without the leading 'P'
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Integer getMaxIdentifier() throws DatabaseException, ParseException {
		List<Patient> patients = this.db.query(Patient.class).find();

		if (CollectionUtils.isEmpty(patients)) {
			return 0;
		}

		Collections.sort(patients, new PatientComparator());
		return Integer.valueOf(patients.get(patients.size() - 1).getIdentifier().substring(1));
	}

	/**
	 * Insert batch of patients. To be refactored.
	 * 
	 * @param file
	 * @return number of patients inserted
	 * @throws Exception
	 */
	public int insertBatch(File file) throws Exception {
		try {
			db.beginTx();
			Workbook workbook = Workbook.getWorkbook(file);
			int count = new UploadBatchExcelReader().importSheet(db,
					workbook.getSheet("Patients"), new SimpleTuple(),
					DatabaseAction.ADD_IGNORE_EXISTING, "");
			db.commitTx();
			return count;
		} catch (Exception e) {
			db.rollbackTx();
			throw e;
		}
	}

	/**
	 * Insert patient and set primary key
	 * 
	 * @param patient
	 * @return number of patients inserted
	 * @throws Exception
	 */
	public int insert(PatientSummaryVO patientSummaryVO) throws Exception {
		if (patientSummaryVO == null) {
			return 0;
		}

		try {

			// db.beginTx();

			// submissions are never inserted via patients, just select newest
			// by date
			List<Submission> submissions = this.db
					.query(Submission.class)
					.equals(Submission.DATE_,
							patientSummaryVO.getSubmission().getDate())
					.sortDESC("id").find();

			if (submissions.size() > 0) {
				patientSummaryVO.getPatient().setSubmission(submissions.get(0));
			}

			// mutations are never inserted via patients, just select
			List<Mutation> mutations1 = this.db.findByExample(patientSummaryVO
					.getMutation1());

			if (mutations1.size() == 1) {
				patientSummaryVO.getPatient().setMutation1(mutations1.get(0));
			}

			List<Mutation> mutations2 = this.db.findByExample(patientSummaryVO
					.getMutation2());

			if (mutations2.size() == 1) {
				patientSummaryVO.getPatient().setMutation2(mutations2.get(0));
			}

			// phenotypes are never inserted via patients, just select
			// List<Phenotype> phenotypes =
			// this.db.query(Phenotype.class).equals("name",
			// patientSummaryVO.getPhenotype().getName()).find();
			List<MutationPhenotype> phenotypes = this.db
					.findByExample(patientSummaryVO.getPhenotype());

			if (phenotypes.size() == 1) {
				patientSummaryVO.getPatient().setPhenotype(phenotypes.get(0));
			}

			// Insert PhenotypeDetails
			this.db.add(patientSummaryVO.getPhenotypeDetails());
			List<PhenotypeDetails> phenotypeDetails = this.db
					.findByExample(patientSummaryVO.getPhenotypeDetails());
			if (phenotypeDetails.size() > 0) {
				patientSummaryVO.getPatient().setPhenotype_Details(
						phenotypeDetails.get(0));
			}

			// Insert Publications
			if (CollectionUtils.isNotEmpty(patientSummaryVO.getPublications())) {
				List<Integer> publicationIds = new ArrayList<Integer>();

				for (Publication publication : patientSummaryVO
						.getPublications()) {
					if (publication.getPubmedID_Name() == null) {
						continue;
					}

					List<Publication> publications = this.db
							.query(Publication.class)
							.equals(Publication.PUBMEDID_NAME,
									publication.getPubmedID_Name()).find();

					if (publications.size() > 0) {
						publicationIds.add(publications.get(0).getId());
					} else {
						OntologyTerm ontologyTerm = new OntologyTerm();
						ontologyTerm.setName(publication.getPubmedID_Name());
						this.db.add(ontologyTerm);
						ontologyTerm = this.db
								.query(OntologyTerm.class)
								.equals(OntologyTerm.NAME,
										publication.getPubmedID_Name()).find()
								.get(0);
						publication.setPubmedID(ontologyTerm);
						this.db.add(publication);
						publicationIds.add(this.db
								.query(Publication.class)
								.equals(Publication.PUBMEDID_NAME,
										publication.getPubmedID_Name()).find()
								.get(0).getId());
					}
				}
				patientSummaryVO.getPatient()
						.setPublications_Id(publicationIds);
			}

			// Insert patient and set primary key
			if (patientSummaryVO.getPatient().getPhenotype() != null) {
				// TODO:Danny: Use or loose
				/* int count = */this.db.add(patientSummaryVO.getPatient());

				List<Patient> patients = this.db.findByExample(patientSummaryVO
						.getPatient());
				if (patients.size() == 1) {
					patientSummaryVO.setPatient(patients.get(0));
				}

				// Add IF
				if (patientSummaryVO.getIf_() != null) {
					patientSummaryVO.getIf_().setAntibody(
							this.db.query(Antibody.class)
									.equals(Antibody.NAME,
											patientSummaryVO.getIf_()
													.getAntibody_Name()).find()
									.get(0));
					patientSummaryVO.getIf_().setPatient(
							patientSummaryVO.getPatient());
					this.db.add(patientSummaryVO.getIf_());
				}

				// Add EM
				if (patientSummaryVO.getEm_() != null) {
					patientSummaryVO.getEm_().setPatient(
							patientSummaryVO.getPatient());
					this.db.add(patientSummaryVO.getEm_());
				}

				// // Add publications
				// if (patientSummaryVO.getPublications() != null)
				// for (Publication publication :
				// patientSummaryVO.getPublications())
				// {
				// if (publication.getPubmed() == null)
				// continue;
				//
				// Patient_publication patient_publication = new
				// Patient_publication();
				// patient_publication.setPatient(patientSummaryVO.getPatient());
				//
				// List<Publication> publications =
				// this.db.findByExample(publication);
				//
				// if (publications.size() > 0)
				// patient_publication.setPublication(publications.get(0));
				// else
				// {
				// this.db.add(publication);
				// patient_publication.setPublication(this.db.findByExample(publication).get(0));
				// }
				//
				// this.db.add(patient_publication);
				// }
			}
			// db.commitTx();

			return 1; // count

		} catch (Exception e) {
			// db.rollbackTx();
			throw e;
		}
	}

	public void setDefaults(PatientSummaryVO patientSummaryVO)
			throws DatabaseException, ParseException, MalformedURLException,
			JAXBException, IOException {
		// set default mutations

		// Mutation nf = new Mutation();
		// nf.setCdna_notation("NF");
		// nf = db.findByExample(nf).get(0);
		//
		// patientSummaryVO.setMutation1(nf);
		// patientSummaryVO.setMutation2(nf);

		// set default phenotype

		// MutationPhenotype phenotype =
		// this.db.query(MutationPhenotype.class).equals(MutationPhenotype.NAME,
		// "DEB-u").find().get(0);
		MutationPhenotype phenotype = this.db.query(MutationPhenotype.class)
				.equals(MutationPhenotype.NAME, "DEB-u").find().get(0);

		patientSummaryVO.setPhenotype(phenotype);

		// set default Pubmed values based on given PubMed ID (if any)

		if (CollectionUtils.isNotEmpty(patientSummaryVO.getPublications())) {
			for (Publication publication : patientSummaryVO.getPublications()) {
				PubmedService pubmedService = new PubmedService();
				List<Integer> pubmedIds = new ArrayList<Integer>();
				pubmedIds.add(publication.getPubmedID_Id());
				List<PubmedArticle> articles = pubmedService
						.getPubmedArticlesForIds(pubmedIds);
				// TODO:Danny: Use or loose
				/* PubmedArticle article = */articles.get(0);
			}
		}
	}

	private PatientSummaryVO toPatientSummaryVO(Patient patient) throws DatabaseException, ParseException
	{
		if (this.cache.containsKey(patient.getId()))
		{
			return this.cache.get(patient.getId());
		}

		PatientSummaryVO patientSummaryVO = new PatientSummaryVO();
		patientSummaryVO.setPatient(patient);

		if (this.db instanceof JDBCDatabase)
		{
			patientSummaryVO.setMutation1(this.db.findById(Mutation.class, patient.getMutation1_Id()));
			patientSummaryVO.setMutation2(this.db.findById(Mutation.class, patient.getMutation2_Id()));
			patientSummaryVO.setPhenotype(this.db.findById(MutationPhenotype.class, patient.getPhenotype_Id()));

			patientSummaryVO.setPubmedURL(PublicationService.PUBMED_URL);

			if (CollectionUtils.isNotEmpty(patient.getPublications_Id()))
			{
				List<Publication> publications = this.db.query(Publication.class).in(Publication.ID, patient.getPublications_Id()).find();
				patientSummaryVO.setPublications(publications);
			}

			Submission submission = this.db.findById(Submission.class, patient.getSubmission_Id());
			patientSummaryVO.setSubmission(submission);
			MolgenisUser submitter = this.db.findById(MolgenisUser.class, submission.getSubmitters_Id().get(0));
			patientSummaryVO.setSubmitter(submitter);
			
			patientSummaryVO.setMaterial(patient.getMaterial_Name());
		}
		else if (this.db instanceof JpaDatabase)
		{
//			patientSummaryVO.setMutation1(patient.getMutation1());
//			patientSummaryVO.setMutation2(patient.getMutation2());
//			patientSummaryVO.setPhenotype(patient.getPhenotype());
//
//			patientSummaryVO.setPubmedURL(PublicationService.PUBMED_URL);
//
//			patientSummaryVO.setPublications(patient.getPublications());
//
//			patientSummaryVO.setSubmission(patient.getSubmission());
//			patientSummaryVO.setSubmitter(patient.getSubmission().getSubmitters().get(0));
//			
//			patientSummaryVO.setMaterial(patient.getMaterial_Name());
		}
		else
			throw new DatabaseException("Unsupported database mapper");

		// cache value
		this.cache.put(patient.getId(), patientSummaryVO);

		return patientSummaryVO;
	}

	private List<PatientSummaryVO> toPatientSummaryVOList(List<Patient> patients) throws DatabaseException, ParseException
	{
		List<PatientSummaryVO> result = new ArrayList<PatientSummaryVO>();

		for (Patient patient : patients)
			result.add(this.toPatientSummaryVO(patient));

		return result;
	}

	public HashMap<String, Integer> getPhenotypeCounts() throws DatabaseException
	{
		if (this.db instanceof JDBCDatabase)
		{
			List<Tuple> counts = ((JDBCDatabase) this.db).sql("SELECT p.name, COUNT(i.id) FROM MutationPhenotype p LEFT OUTER JOIN Patient i ON (p.id = i.phenotype) GROUP BY p.name");
			HashMap<String, Integer> result = new HashMap<String, Integer>();
		
			for (Tuple entry : counts)
				result.put(entry.getString(0), entry.getInt(1));

			return result;
		}
		else if (this.db instanceof JpaDatabase)
		{
			String sql                      = "SELECT p.name, COUNT(i.id) FROM MutationPhenotype p LEFT OUTER JOIN Patient i ON (p.id = i.phenotype) GROUP BY p.name";
			List<Object[]> counts           = this.db.getEntityManager().createNativeQuery(sql).getResultList();

			HashMap<String, Integer> result = new HashMap<String, Integer>();

			for (Object[] entry : counts)
				result.put((String) entry[0], Integer.valueOf(entry[1].toString()));

			return result;
		}
		else
			throw new DatabaseException("Unsupported database mapper");
	}
}
