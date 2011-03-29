package org.molgenis.mutation.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBException;

import jxl.Workbook;

import org.apache.commons.collections.CollectionUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.OntologyTerm;
import org.molgenis.core.Publication;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.mutation.Antibody;
import org.molgenis.mutation.E_M;
import org.molgenis.mutation.I_F;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.Patient;
import org.molgenis.mutation.PhenotypeDetails;
import org.molgenis.mutation.excel.UploadBatchExcelReader;
import org.molgenis.mutation.util.PatientComparator;
import org.molgenis.mutation.util.PatientSummaryVOComparator;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.services.PubmedService;
import org.molgenis.services.pubmed.PubmedArticle;
import org.molgenis.submission.Submission;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.JDBCDatabase;

public class PatientService
{
	private JDBCDatabase db                          = null;
	private static PatientService patientService     = null;
	private HashMap<Integer, PatientSummaryVO> cache = new HashMap<Integer, PatientSummaryVO>();
	//TODO:Danny: Use or loose
	/*	private static final transient Logger logger     = 	Logger.getLogger(JDBCConnectionHelper.class.getSimpleName()); */

	// private constructor, use singleton instance
	private PatientService(Database db)
	{
		this.db = (JDBCDatabase) db;
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
			query = query.equals(Patient.IDENTIFIER, criteria.getPid());
		if (criteria.getMutationId() != null)
			query = query.equals(Patient.MUTATION1, criteria.getMutationId()).or().equals(Patient.MUTATION2, criteria.getMutationId());
		if (criteria.getMid() != null)
		{
			List<Mutation> mutations = this.db.query(Mutation.class).equals(Mutation.IDENTIFIER, criteria.getMid()).find();
			if (mutations.size() == 1)
			{
				Integer mutationId = mutations.get(0).getId();
				query              = query.equals(Patient.MUTATION1, mutationId).or().equals(Patient.MUTATION2, mutationId);
			}
		}
		if (criteria.getConsent() != null)
			if (criteria.getConsent())
				query = query.equals(Patient.CONSENT, "publication").or().equals(Patient.CONSENT, "yes");
			else
				query = query.equals(Patient.CONSENT, "no");
		if (criteria.getSubmissionId() != null)
		{
			query = query.equals(Patient.SUBMISSION, criteria.getSubmissionId());
		}
		if (criteria.getUserId() != null)
		{
			List<Submission> submissions = this.db.query(Submission.class).equals(Submission.SUBMITTERS, criteria.getUserId()).find();
			List<Integer> submissionIds  = new ArrayList<Integer>();
			for (Submission submission : submissions)
				submissionIds.add(submission.getId());
			query = query.in(Patient.SUBMISSION, submissionIds);
		}

		if (query.getRules().length > 0)
			return this.toPatientSummaryVOList(query.find());
		else
			// Return an empty list, if query is "empty". find() would return all objects.
			return new ArrayList<PatientSummaryVO>();
	}

	public PatientSummaryVO findPatientById(Integer id) throws Exception
	{
		return this.toPatientSummaryVO(this.db.findById(Patient.class, id));
	}

//	public PatientSummaryVO findPatientByIdentifier(String identifier) throws Exception
//	{
////		Patient example = new Patient();
////		example.setIdentifier(identifier);
////		example.setConsent(null);
//		return this.toPatientSummaryVO(this.db.query(Patient.class).equals("identifier", identifier).find().get(0)); // safe because identifier is unique
////		return this.toPatientSummaryVO(this.db.findByExample(example).get(0)); // safe because identifier is unique
//	}

//	public List<PatientSummaryVO> findPatientsByMutationId(Integer mutationId) throws DatabaseException, ParseException
//	{
//		return this.toPatientSummaryVOList(this.db.query(Patient.class).equals("mutation1", mutationId).or().equals("mutation2", mutationId).find());
//	}
//
//	public List<Patient> findPatientsByMutationId(Integer mutation1Id, Integer mutation2Id) throws Exception
//	{
//		List<Patient> patients = new ArrayList<Patient>();
//		patients.addAll(this.db.query(Patient.class).equals("mutation1", mutation1Id).equals("mutation2", mutation2Id).find());
//		patients.addAll(this.db.query(Patient.class).equals("mutation2", mutation1Id).equals("mutation1", mutation2Id).find());
//		return patients;
//	}

	/**
	 * Get all patients.
	 * @return all patients
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<Patient> getAllPatients() throws DatabaseException, ParseException
	{
		return this.db.query(Patient.class).find();
	}

	public List<PatientSummaryVO> getAllPatientSummaries() throws DatabaseException, ParseException
	{
		return this.toPatientSummaryVOList(this.db.query(Patient.class).find());
	}

	/**
	 * Get number of unpublished patients.
	 * @return number of unpublished patients
	 * @throws DatabaseException
	 * @throws ParseException 
	 */
	public int getNumUnpublishedPatients() throws DatabaseException, ParseException
	{
//		return this.db.query(Patient.class).equals("publications", null).count();
		//TODO: Outer join is faster, but is syntax standardized?
		return this.db.sql("SELECT DISTINCT id FROM Patient WHERE NOT EXISTS (SELECT id FROM Patient_publications WHERE Patient.id = Patient_publications.Patient)").size();
	}

	/**
	 * Get the biggest identifier without the leading 'P'
	 * @return biggest identifier without the leading 'P'
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Integer getMaxIdentifier() throws DatabaseException, ParseException
	{
		List<Patient> patients = this.db.query(Patient.class).find();
		Collections.sort(patients, new PatientComparator());
		return Integer.valueOf(patients.get(patients.size() - 1).getIdentifier().substring(1));
	}

	public List<PatientSummaryVO> find(MolgenisUser user) throws DatabaseException, ParseException
	{
		return null;
//		Query<Patient> query         = this.db.query(Patient.class);
//		SubmissionAuthorization auth = new SubmissionAuthorization();
//		List<Patient> patients       = (List<Patient>) auth.doAs(user, new Patient(), query);
//		return this.toPatientSummaryVOList(patients);
	}

	/**
	 * Find the highest patient number
	 * @return highest patient number
	 * @throws DatabaseException
	 * @throws ParseException
	 */
//	public Integer findHighestNumber() throws DatabaseException, ParseException
//	{
//		List<Patient> patients = this.db.query(Patient.class).sortDESC("number_").limit(1).find();
//		
//		if (patients.size() == 1)
//			return patients.get(0).getNumber_();
//		else
//			return 0;
//	}

	/**
	 * Insert batch of patients. To be refactored.
	 * @param file
	 * @return number of patients inserted
	 * @throws Exception
	 */
	public int insertBatch(File file) throws Exception
	{
		try
		{
			db.beginTx();
			Workbook workbook = Workbook.getWorkbook(file);
			int count = new UploadBatchExcelReader().importSheet(db, workbook.getSheet("Patients"), new SimpleTuple(), DatabaseAction.ADD_IGNORE_EXISTING, "");
			db.commitTx();
			return count;
		}
		catch (Exception e)
		{
			db.rollbackTx();
			throw e;
		}
	}

	/**
	 * Insert patient and set primary key
	 * @param patient
	 * @return number of patients inserted
	 * @throws Exception
	 */
	public int insert(PatientSummaryVO patientSummaryVO) throws Exception
	{
		if (patientSummaryVO == null)
			return 0;

		try
		{

//			db.beginTx();

			// submissions are never inserted via patients, just select newest by date
			List<Submission> submissions = this.db.query(Submission.class).equals(Submission.DATE_, patientSummaryVO.getSubmission().getDate()).sortDESC("id").find();
			
			if (submissions.size() > 0)
				patientSummaryVO.getPatient().setSubmission(submissions.get(0));

			// mutations are never inserted via patients, just select
			List<Mutation> mutations1 = this.db.findByExample(patientSummaryVO.getMutation1());
			
			if (mutations1.size() == 1)
				patientSummaryVO.getPatient().setMutation1(mutations1.get(0));
			
			List<Mutation> mutations2 = this.db.findByExample(patientSummaryVO.getMutation2());
			
			if (mutations2.size() == 1)
				patientSummaryVO.getPatient().setMutation2(mutations2.get(0));

			// phenotypes are never inserted via patients, just select
//			List<Phenotype> phenotypes = this.db.query(Phenotype.class).equals("name", patientSummaryVO.getPhenotype().getName()).find();
			List<MutationPhenotype> phenotypes = this.db.findByExample(patientSummaryVO.getPhenotype());

			if (phenotypes.size() == 1)
				patientSummaryVO.getPatient().setPhenotype(phenotypes.get(0));

			// Insert PhenotypeDetails
			this.db.add(patientSummaryVO.getPhenotypeDetails());
			List<PhenotypeDetails> phenotypeDetails = this.db.findByExample(patientSummaryVO.getPhenotypeDetails());
			if (phenotypeDetails.size() > 0)
				patientSummaryVO.getPatient().setPhenotype_Details(phenotypeDetails.get(0));

			// Insert Publications
			if (CollectionUtils.isNotEmpty(patientSummaryVO.getPublications()))
			{
				List<Integer> publicationIds = new ArrayList<Integer>();

				for (Publication publication : patientSummaryVO.getPublications())
				{
					if (publication.getPubmedID_Name() == null)
						continue;

					List<Publication> publications = this.db.query(Publication.class).equals(Publication.PUBMEDID_NAME, publication.getPubmedID_Name()).find();

					if (publications.size() > 0)
						publicationIds.add(publications.get(0).getId());
					else
					{
						OntologyTerm ontologyTerm = new OntologyTerm();
						ontologyTerm.setName(publication.getPubmedID_Name());
						this.db.add(ontologyTerm);
						ontologyTerm = this.db.query(OntologyTerm.class).equals(OntologyTerm.NAME, publication.getPubmedID_Name()).find().get(0);
						publication.setPubmedID(ontologyTerm);
						this.db.add(publication);
						publicationIds.add(this.db.query(Publication.class).equals(Publication.PUBMEDID_NAME, publication.getPubmedID_Name()).find().get(0).getId());
					}
				}
				patientSummaryVO.getPatient().setPublications(publicationIds);
			}

			// Insert patient and set primary key
			if (patientSummaryVO.getPatient().getPhenotype() != null)
			{
				//TODO:Danny: Use or loose
				/*int count = */this.db.add(patientSummaryVO.getPatient());

				List<Patient> patients = this.db.findByExample(patientSummaryVO.getPatient());
				if (patients.size() == 1)
					patientSummaryVO.setPatient(patients.get(0));

				// Add IF
				if (patientSummaryVO.getIf_() != null)
				{
					patientSummaryVO.getIf_().setAntibody(this.db.query(Antibody.class).equals(Antibody.NAME, patientSummaryVO.getIf_().getAntibody_Name()).find().get(0));
					patientSummaryVO.getIf_().setPatient(patientSummaryVO.getPatient());
					this.db.add(patientSummaryVO.getIf_());
				}

				// Add EM
				if (patientSummaryVO.getEm_() != null)
				{
					patientSummaryVO.getEm_().setPatient(patientSummaryVO.getPatient());
					this.db.add(patientSummaryVO.getEm_());
				}
				
//				// Add publications
//				if (patientSummaryVO.getPublications() != null)
//					for (Publication publication : patientSummaryVO.getPublications())
//					{
//						if (publication.getPubmed() == null)
//							continue;
//
//						Patient_publication patient_publication = new Patient_publication();
//						patient_publication.setPatient(patientSummaryVO.getPatient());
//
//						List<Publication> publications          = this.db.findByExample(publication);
//
//						if (publications.size() > 0)
//							patient_publication.setPublication(publications.get(0));
//						else
//						{
//							this.db.add(publication);
//							patient_publication.setPublication(this.db.findByExample(publication).get(0));
//						}
//						
//						this.db.add(patient_publication);
//					}
			}
//			db.commitTx();

			return 1; //count
		
		}
		catch (Exception e)
		{
//			db.rollbackTx();
			throw e;
		}
	}

	public void setDefaults(PatientSummaryVO patientSummaryVO) throws DatabaseException, ParseException, MalformedURLException, JAXBException, IOException
	{
		// set default mutations

//		Mutation nf = new Mutation();
//		nf.setCdna_notation("NF");
//		nf = db.findByExample(nf).get(0);
//
//		patientSummaryVO.setMutation1(nf);
//		patientSummaryVO.setMutation2(nf);

		// set default phenotype

		MutationPhenotype phenotype = this.db.query(MutationPhenotype.class).equals(MutationPhenotype.NAME, "DEB-u").find().get(0);

		patientSummaryVO.setPhenotype(phenotype);

		// set default Pubmed values based on given PubMed ID (if any)

		if (CollectionUtils.isNotEmpty(patientSummaryVO.getPublications()))
		{
			for (Publication publication : patientSummaryVO.getPublications())
			{
				PubmedService pubmedService          = new PubmedService();
				List<Integer> pubmedIds              = new ArrayList<Integer>();
				pubmedIds.add(publication.getPubmedID_Id());
				List<PubmedArticle> articles         = pubmedService.getPubmedArticlesForIds(pubmedIds);
				//TODO:Danny: Use or loose
				/*PubmedArticle article                = */articles.get(0);
			}
		}
	}

	private PatientSummaryVO toPatientSummaryVO(Patient patient) throws DatabaseException, ParseException
	{
		if (this.cache.containsKey(patient.getId()))
			return this.cache.get(patient.getId());

		PatientSummaryVO patientSummaryVO = new PatientSummaryVO();
		patientSummaryVO.setPatient(patient);
		patientSummaryVO.setMutation1(this.db.findById(Mutation.class, patient.getMutation1()));
		patientSummaryVO.setMutation2(this.db.findById(Mutation.class, patient.getMutation2()));
		patientSummaryVO.setPhenotype(this.db.findById(MutationPhenotype.class, patient.getPhenotype()));
		if (!"no".equals(patient.getConsent()))
			patientSummaryVO.setPhenotypeDetails(this.db.findById(PhenotypeDetails.class, patient.getPhenotype_Details()));
		
		if (CollectionUtils.isNotEmpty(patient.getPublications()))
		{
			List<Publication> publications = this.db.query(Publication.class).in(Publication.ID, patient.getPublications()).find();
			for (Publication publication : publications)
				publication.setPubmedID_Name("http://www.ncbi.nlm.nih.gov/pubmed/" + publication.getPubmedID_Name());
			patientSummaryVO.setPublications(publications);
		}
		
		Submission submission  = this.db.findById(Submission.class, patient.getSubmission());
		patientSummaryVO.setSubmission(submission);
		MolgenisUser submitter = this.db.findById(MolgenisUser.class, submission.getSubmitters().get(0));
		patientSummaryVO.setSubmitter(submitter);
		
		List<I_F> if_s = this.db.query(I_F.class).equals(I_F.PATIENT, patient.getId()).find();
//		I_F if_ = new I_F();
//		if_.setPatient(patient);
//		List<I_F> if_s = this.db.findByExample(if_);
		if (if_s.size() > 0)
			patientSummaryVO.setIf_(if_s.get(0));
		
		List<E_M> em_s = this.db.query(E_M.class).equals(E_M.PATIENT, patient.getId()).find();
//		E_M em_ = new E_M();
//		em_.setPatient(patient);
//		List<E_M> em_s = this.db.findByExample(em_);
		if (em_s.size() > 0)
			patientSummaryVO.setEm_(em_s.get(0));
		
//		logger.debug(">>> toPatientSummaryVO: mut1==" + patientSummaryVO.getMutation1() + ", mut2==" + patientSummaryVO.getMutation2());
		
		// cache value
		this.cache.put(patient.getId(), patientSummaryVO);

		return patientSummaryVO;
	}
	
	private List<PatientSummaryVO> toPatientSummaryVOList(List<Patient> patients) throws DatabaseException, ParseException
	{
		List<PatientSummaryVO> result = new ArrayList<PatientSummaryVO>();

		for (Patient patient : patients)
			result.add(this.toPatientSummaryVO(patient));
		Collections.sort(result, new PatientSummaryVOComparator());
		return result;
	}

	public HashMap<String, Integer> getPhenotypeCounts() throws DatabaseException
	{
		List<Tuple> counts              = this.db.sql("SELECT p.name, COUNT(i.id) FROM MutationPhenotype p LEFT OUTER JOIN Patient i ON (p.id = i.phenotype) GROUP BY p.name");
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		
		for (Tuple entry : counts)
		{
			result.put(entry.getString(0), entry.getInt(1));
		}
		return result;
	}
}
