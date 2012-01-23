package org.molgenis.mutation.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.Publication;
import org.molgenis.core.service.PublicationService;
import org.molgenis.core.vo.PublicationVO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.Patient;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.ObservedValueVO;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.mutation.vo.PhenotypeDetailsVO;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;
import org.molgenis.protocol.Workflow;
import org.molgenis.protocol.WorkflowElement;
import org.molgenis.protocol.service.WorkflowService;
import org.molgenis.submission.Submission;
import org.molgenis.util.Tuple;

public class PatientService implements Serializable
{
	private static final long serialVersionUID       = -5343468595949980106L;
	private Database db                              = null;
	private HashMap<Integer, PatientSummaryVO> cache = new HashMap<Integer, PatientSummaryVO>();

	public void setDatabase(Database db)
	{
		this.db = db;
	}

	public List<PatientSummaryVO> findPatients(PatientSearchCriteriaVO criteria) throws DatabaseException
	{
		if (this.db instanceof JDBCDatabase)
		{
			Query<Patient> query = this.db.query(Patient.class);
	
			if (criteria.getPid() != null)
			{
				query = query.equals(Patient.IDENTIFIER, criteria.getPid());
			}
			if (criteria.getMutationId() != null)
			{
				Mutation mutation = this.db.findById(Mutation.class, criteria.getMutationId());
				query = query.equals(Patient.MUTATIONS, mutation.getId());
			}
			if (criteria.getMid() != null)
			{
				List<Mutation> mutations = this.db.query(Mutation.class).equals(Mutation.IDENTIFIER, criteria.getMid()).find();
				if (mutations.size() == 1)
				{
					Integer mutationId = mutations.get(0).getId();
					query = query.equals(Patient.MUTATIONS, mutationId);
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
	
			if (query.getRules().length > 0)
			{
				return this.toPatientSummaryVOList(query.sortASC(Patient.IDENTIFIER).find());
			}
			else // Return an empty list, if query is "empty". find() would return all objects.
			{
				return new ArrayList<PatientSummaryVO>();
			}
		}
		else if (this.db instanceof JpaDatabase)
		{
			CriteriaBuilder cb              = this.db.getEntityManager().getCriteriaBuilder();
			CriteriaQuery<Patient> query    = cb.createQuery(Patient.class);
//			Metamodel metaModel             = this.db.getEntityManager().getMetamodel();
//			EntityType<Mutation> Mutation_  = metaModel.entity(Mutation.class);

			Root<Patient> patient           = query.from(Patient.class);
			query.select(patient);
			
			List<Predicate> patientCriteria = new ArrayList<Predicate>();

			if (criteria.getPid() != null)
				patientCriteria.add(cb.equal(patient.get("identifier"), criteria.getPid()));
			if (criteria.getMutationId() != null)
				patientCriteria.add(cb.or(cb.equal(patient.get("mutation1"), criteria.getMutationId()), cb.equal(patient.get("mutation2"), criteria.getMutationId())));
			if (criteria.getMid() != null)
			{
				Join<Patient, Mutation> mutation1 = patient.join("mutation1", JoinType.LEFT);
				Join<Patient, Mutation> mutation2 = patient.join("mutation2", JoinType.LEFT);

				patientCriteria.add(cb.or(cb.equal(mutation1.get("identifier"), criteria.getMid()), cb.equal(mutation2.get("identifier"), criteria.getMid())));
			}
			if (criteria.getConsent() != null)
			{
				if (criteria.getConsent())
					patientCriteria.add(patient.get("consent").in(Arrays.asList(new String[] { "publication", "yes" })));
				else
					patientCriteria.add(cb.equal(patient.get("consent"), "no"));
			}
			if (criteria.getSubmissionId() != null)
				patientCriteria.add(cb.equal(patient.get("submission"), criteria.getSubmissionId()));
			if (criteria.getUserId() != null)
			{
				Join<Patient, Submission> submission   = patient.join("submission", JoinType.LEFT);
				Join<Patient, MolgenisUser> submitters = submission.join("submitters", JoinType.LEFT);

				patientCriteria.add(cb.equal(submitters.get("id"), criteria.getUserId()));
			}

			if (patientCriteria.size() > 0)
			{
				query.where(cb.and(patientCriteria.toArray(new Predicate[0])));
				query.orderBy(cb.asc(patient.get("identifier")));
				return this.toPatientSummaryVOList(this.db.getEntityManager().createQuery(query).getResultList());
			}
			else
				return new ArrayList<PatientSummaryVO>();
		}
		else
			throw new DatabaseException("Unsupported database mapper");
	}

	public PhenotypeDetailsVO findPhenotypeDetails(String pid) throws DatabaseException, ParseException
	{
		List<Patient> patients = this.db.query(Patient.class).equals(Patient.IDENTIFIER, pid).in(Patient.CONSENT, Arrays.asList(new String[] { "publication", "yes" })).find();
		
		if (CollectionUtils.isEmpty(patients))
			throw new IllegalArgumentException("Unknown patient identifier '" + pid + "'");
		
		Patient patient        = patients.get(0);
		
		if (this.db instanceof JDBCDatabase)
		{
			PhenotypeDetailsVO phenotypeDetailsVO = new PhenotypeDetailsVO();
			phenotypeDetailsVO.setPatientId(patient.getId());
			phenotypeDetailsVO.setPatientIdentifier(patient.getIdentifier());
			phenotypeDetailsVO.setProtocolNames(new ArrayList<String>());
			phenotypeDetailsVO.setObservedValues(new HashMap<String, List<ObservedValueVO>>());

			List<Workflow> workflows = this.db.query(Workflow.class).find();
			
			if (CollectionUtils.isEmpty(workflows))
				return phenotypeDetailsVO;
			
			// Use only first workflow
			// TODO: What to do in case more than one workflow was found???
			
			WorkflowService workflowService        = new WorkflowService();
			workflowService.setDatabase(db);
			List<WorkflowElement> workflowElements = workflowService.findWorkflowElements(workflows.get(0));

			if (CollectionUtils.isEmpty(workflowElements))
				return phenotypeDetailsVO;
			
			for (WorkflowElement workflowElement : workflowElements)
			{
				Protocol protocol = this.db.findById(Protocol.class, workflowElement.getProtocol_Id());

				// ignore protocols without features
				if (CollectionUtils.isEmpty(protocol.getFeatures_Id()))
					continue;

				phenotypeDetailsVO.getProtocolNames().add(protocol.getName());
				phenotypeDetailsVO.getObservedValues().put(protocol.getName(), new ArrayList<ObservedValueVO>());

				List<ObservedValue> observedValues = this.db.query(ObservedValue.class).equals(ObservedValue.TARGET, patient.getId()).in(ObservedValue.FEATURE, protocol.getFeatures_Id()).find();
				for (ObservedValue observedValue : observedValues)
				{
					ObservedValueVO observedValueVO = new ObservedValueVO();
					observedValueVO.setFeatureName(observedValue.getFeature_Name());
					observedValueVO.setValue(observedValue.getValue());
					phenotypeDetailsVO.getObservedValues().get(protocol.getName()).add(observedValueVO);
				}
			}
			return phenotypeDetailsVO;
		}
		else if (this.db instanceof JpaDatabase)
		{
			//TODO: Use navigatable objects here. Would currently break JDBCMapper build.
			PhenotypeDetailsVO phenotypeDetailsVO = new PhenotypeDetailsVO();
			phenotypeDetailsVO.setPatientId(patient.getId());
			phenotypeDetailsVO.setPatientIdentifier(patient.getIdentifier());
			phenotypeDetailsVO.setProtocolNames(new ArrayList<String>());
			phenotypeDetailsVO.setObservedValues(new HashMap<String, List<ObservedValueVO>>());

			List<Workflow> workflows = this.db.query(Workflow.class).find();
			
			if (CollectionUtils.isEmpty(workflows))
				return phenotypeDetailsVO;
			
			// Use only first workflow
			// TODO: What to do in case more than one workflow was found???
			
			WorkflowService workflowService        = new WorkflowService();
			workflowService.setDatabase(this.db);
			List<WorkflowElement> workflowElements = workflowService.findWorkflowElements(workflows.get(0));

			if (CollectionUtils.isEmpty(workflowElements))
				return phenotypeDetailsVO;
			
			for (WorkflowElement workflowElement : workflowElements)
			{
				Protocol protocol = this.db.findById(Protocol.class, workflowElement.getProtocol_Id());

				javax.persistence.Query query = this.db.getEntityManager().createNativeQuery("SELECT ov.id, f.name, ov.value FROM ObservedValue ov JOIN Protocol_Features pf ON (ov.Feature = pf.Features) JOIN ObservationElement f ON (pf.Features = f.id) WHERE ov.target = " + patient.getId() + " AND pf.Protocol = " + protocol.getId());
				List<?> observedValueList     = query.getResultList();
				
				if (observedValueList.size() == 0)
					continue;

				phenotypeDetailsVO.getProtocolNames().add(protocol.getName());
				phenotypeDetailsVO.getObservedValues().put(protocol.getName(), new ArrayList<ObservedValueVO>());

				for (Object row : observedValueList)
				{
					Object[] columns = (Object[]) row;
					ObservedValueVO observedValueVO = new ObservedValueVO();
					observedValueVO.setFeatureName(columns[1].toString());
					observedValueVO.setValue(columns[2].toString());
					phenotypeDetailsVO.getObservedValues().get(protocol.getName()).add(observedValueVO);
				}
			}
			return phenotypeDetailsVO;
		}
		else
			throw new UnsupportedOperationException("Unsupported database mapper");
	}

	public List<PatientSummaryVO> getAllPatientSummaries() throws DatabaseException
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
	 * Get number of patients by type of mutation pathogenicity
	 * @param pathogenicity
	 * @return number of patients
	 * @throws DatabaseException
	 */
	public int getNumPatientsByPathogenicity(String pathogenicity) throws DatabaseException
	{
		if (this.db instanceof JDBCDatabase)
			return ((JDBCDatabase) this.db).sql("SELECT DISTINCT p.id FROM Patient p LEFT JOIN Mutation m ON (p.mutation1 = m.id) WHERE m.pathogenicity = '" + pathogenicity + "'").size();
		else if (this.db instanceof JpaDatabase)
		{
			javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT COUNT(DISTINCT p.id) FROM Patient p LEFT JOIN Mutation m ON (p.mutation1 = m.id) WHERE m.pathogenicity = '" + pathogenicity + "'");
			return Integer.valueOf(q.getSingleResult().toString());
		}
		else
			throw new DatabaseException("Unsupported database mapper");
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
			return ((JDBCDatabase) this.db).sql("SELECT DISTINCT id FROM Patient WHERE NOT EXISTS (SELECT id FROM Patient_patientreferences WHERE Patient.id = Patient_patientreferences.Patient)").size();
		else if (this.db instanceof JpaDatabase)
		{
			javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT COUNT(DISTINCT p.id) FROM Patient p LEFT OUTER JOIN Patient_patientreferences pp ON (p.id = pp.Patient) WHERE pp.patientreferences IS NULL");
			return Integer.valueOf(q.getSingleResult().toString());
		}
		else
			throw new DatabaseException("Unsupported database mapper");
	}

//	public void insertObservedValues(PatientSummaryVO patientSummaryVO) throws DatabaseException, ParseException, IOException
//	{
//		if (observedValueVO == null)
//			return;
//		
//		List<ObservableFeature> features = this.db.query(ObservableFeature.class).equals(ObservableFeature.NAME, observedValueVO.getFeatureName()).find();
//		
//		if (features.size() != 1)
//			return;
//		
//		List<Patient> patients = this.db.query(Patient.class).equals(Patient.IDENTIFIER, observedValueVO.getTargetName()).find();
//		
//		if (patients.size() != 1)
//			return;
//
//		ObservedValue observedValue = new ObservedValue();
//		observedValue.setFeature(features.get(0));
//		observedValue.setInvestigation(1);
//		observedValue.setTarget(patients.get(0));
//		observedValue.setValue(observedValueVO.getValue());
//		
//		this.db.add(observedValue);
//	}




//	public void setDefaults(PatientSummaryVO patientSummaryVO)
//			throws DatabaseException, ParseException, MalformedURLException,
//			JAXBException, IOException {
//		// set default mutations
//
//		// Mutation nf = new Mutation();
//		// nf.setCdna_notation("NF");
//		// nf = db.findByExample(nf).get(0);
//		//
//		// patientSummaryVO.setMutation1(nf);
//		// patientSummaryVO.setMutation2(nf);
//
//		// set default phenotype
//
//		// MutationPhenotype phenotype =
//		// this.db.query(MutationPhenotype.class).equals(MutationPhenotype.NAME,
//		// "DEB-u").find().get(0);
////		MutationPhenotype phenotype = this.db.query(MutationPhenotype.class)
////				.equals(MutationPhenotype.NAME, "DEB-u").find().get(0);
////
////		patientSummaryVO.setPhenotypeMajor(phenotype.getMajortype());
////		patientSummaryVO.setPhenotypeSub(phenotype.getSubtype());
//
//		// set default Pubmed values based on given PubMed ID (if any)
//
//		if (CollectionUtils.isNotEmpty(patientSummaryVO.getPublicationVOList())) {
//			for (PublicationVO publicationVO : patientSummaryVO.getPublicationVOList()) {
//				PubmedService pubmedService = new PubmedService();
//				List<Integer> pubmedIds = new ArrayList<Integer>();
//				pubmedIds.add(new Integer(publicationVO.getPubmed()));
//				List<PubmedArticle> articles = pubmedService
//						.getPubmedArticlesForIds(pubmedIds);
//				// TODO:Danny: Use or loose
//				/* PubmedArticle article = */articles.get(0);
//			}
//		}
//	}



	private PatientSummaryVO toPatientSummaryVO(Patient patient) throws DatabaseException
	{
		if (this.cache.containsKey(patient.getId()))
		{
			return this.cache.get(patient.getId());
		}

		PatientSummaryVO patientSummaryVO = new PatientSummaryVO();
//		patientSummaryVO.setPatient(patient);

		patientSummaryVO.setPatientIdentifier(patient.getIdentifier());
		patientSummaryVO.setPatientNumber(patient.getNumber());
		patientSummaryVO.setPatientConsent(patient.getConsent());
		patientSummaryVO.setPatientAge(patient.getAge());
		patientSummaryVO.setPatientGender(patient.getGender());
		patientSummaryVO.setPatientEthnicity(patient.getEthnicity());
		patientSummaryVO.setPatientDeceased(patient.getDeceased());
		patientSummaryVO.setPatientDeathCause(patient.getDeath_Cause());
		patientSummaryVO.setPatientMmp1Allele1(patient.getMmp1_Allele1());
		patientSummaryVO.setPatientMmp1Allele2(patient.getMmp1_Allele2());
			
		patientSummaryVO.setVariantSummaryVOList(new ArrayList<MutationSummaryVO>());
		List<Mutation> mutations = this.db.query(Mutation.class).in(Mutation.ID, patient.getMutations_Id()).find();
		for (Mutation mutation : mutations)
		{
			MutationSummaryVO variantSummaryVO = new MutationSummaryVO();
			variantSummaryVO.setIdentifier(mutation.getIdentifier());
			variantSummaryVO.setCdnaNotation(mutation.getCdna_Notation());
			variantSummaryVO.setAaNotation(mutation.getAa_Notation());
			variantSummaryVO.setExonId(mutation.getExon_Id());
			variantSummaryVO.setExonName(mutation.getExon_Name());
			variantSummaryVO.setConsequence(mutation.getConsequence());
			variantSummaryVO.setPathogenicity(mutation.getPathogenicity());
			patientSummaryVO.getVariantSummaryVOList().add(variantSummaryVO);
		}
		if (mutations.size() > 0)
		{
			Mutation mutation1 = mutations.get(0);
			patientSummaryVO.setCdnaPosition(mutation1.getCdna_Position());
			patientSummaryVO.setAaPosition(mutation1.getAa_Position());
			Exon exon = this.db.findById(Exon.class, mutation1.getExon_Id());
			patientSummaryVO.setExonNumber(exon.getNumber());
		}
		patientSummaryVO.setVariantComment(patient.getMutation2remark());

		List<ObservableFeature> features = this.db.query(ObservableFeature.class).equals(ObservableFeature.NAME, "Phenotype").find();
		if (features.size() != 1)
			throw new DatabaseException("Not exactly one ObservableFeature with name 'Phenotype' found.");

		List<ObservedValue> phenotypes = this.db.query(ObservedValue.class).equals(ObservedValue.FEATURE, features.get(0).getId()).equals(ObservedValue.TARGET, patient.getId()).find();
		List<String> phenotypeNames    = new ArrayList<String>();
		for (ObservedValue phenotype : phenotypes)
			phenotypeNames.add(phenotype.getValue());
		patientSummaryVO.setPhenotypeMajor(StringUtils.join(phenotypeNames, ", "));
		patientSummaryVO.setPhenotypeSub("");
			
		patientSummaryVO.setPatientMaterialList(patient.getMaterial_Name());
			
		Submission submission  = this.db.findById(Submission.class, patient.getSubmission_Id());
		MolgenisUser submitter = this.db.findById(MolgenisUser.class, submission.getSubmitters_Id().get(0));
		patientSummaryVO.setSubmitterDepartment(submitter.getDepartment());
		patientSummaryVO.setSubmitterInstitute(submitter.getAffiliation_Name());
		patientSummaryVO.setSubmitterCity(submitter.getCity());
		patientSummaryVO.setSubmitterCountry(submitter.getCountry());
			
		if (CollectionUtils.isNotEmpty(patient.getPatientreferences_Id()))
		{
			patientSummaryVO.setPublicationVOList(new ArrayList<PublicationVO>());
			List<Publication> publications = this.db.query(Publication.class).in(Publication.ID, patient.getPatientreferences_Id()).find();
			for (Publication publication : publications)
			{
				PublicationVO publicationVO = new PublicationVO();
				publicationVO.setName(publication.getName());
				publicationVO.setTitle(publication.getTitle());
				publicationVO.setPubmedId(publication.getPubmedID_Name());
				patientSummaryVO.getPublicationVOList().add(publicationVO);
			}
		}

		patientSummaryVO.setPubmedURL(PublicationService.PUBMED_URL);

//			patientSummaryVO.setMaterial(patient.getMaterial_Name());

		// cache value
		this.cache.put(patient.getId(), patientSummaryVO);

		return patientSummaryVO;
	}

	private List<PatientSummaryVO> toPatientSummaryVOList(List<Patient> patients) throws DatabaseException
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
			List<Tuple> counts = ((JDBCDatabase) this.db).sql("SELECT v.value, COUNT(v.value) FROM ObservedValue v JOIN ObservationElement e ON (e.id = v.Feature) WHERE e.name = 'Phenotype' GROUP BY value");
			HashMap<String, Integer> result = new HashMap<String, Integer>();
		
			for (Tuple entry : counts)
				result.put(entry.getString(0), entry.getInt(1));

			return result;
		}
		else if (this.db instanceof JpaDatabase)
		{
			String sql                      = "SELECT v.value, COUNT(v.value) FROM ObservedValue v JOIN ObservationElement e ON (e.id = v.Feature) WHERE e.name = 'Phenotype' GROUP BY value";
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
