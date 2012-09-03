package org.molgenis.mutation.service;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import jxl.Workbook;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.core.OntologyTerm;
import org.molgenis.core.Publication;
import org.molgenis.core.dto.PublicationDTO;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.GeneDTO;
import org.molgenis.mutation.dto.MutationUploadDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.mutation.excel.UploadBatchExcelReader;
import org.molgenis.mutation.util.AlternateIdComparator;
import org.molgenis.mutation.util.SequenceUtils;
import org.molgenis.pheno.AlternateId;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.util.SimpleTuple;
import org.molgenis.variant.Patient;
import org.molgenis.variant.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UploadService extends MolgenisVariantService
{
	private final transient Logger logger = Logger.getLogger(UploadService.class.getSimpleName());
	private Database db;
	private EntityManager em;

	@Autowired
	private UploadBatchExcelReader reader;

	@Autowired
	public UploadService(Database db)
	{
		super(db);
		this.db = db;
		this.em = db.getEntityManager();
	}

	public void reindex()
	{
		if (this.db instanceof JpaDatabase)
		{
			((JpaDatabase) this.db).index();
		}
	}

	/**
	 * Insert patient and set primary key
	 * 
	 * @param patient
	 * @return number of patients inserted
	 * @throws Exception
	 */
	public void insert(PatientSummaryDTO patientSummaryVO)
	{
		try
		{
			if (patientSummaryVO == null)
				throw new UploadServiceException("Patient to be inserted must not be null.");

			Patient patient = new Patient();
	
			patient.setName(patientSummaryVO.getPatientName());
	
			// Add stable external identifier
			AlternateId patientId = new AlternateId();
			patientId.setDefinition("molgenis_patient_id");
			patientId.setName(patientSummaryVO.getPatientIdentifier());
			this.em.persist(patientId);
			patient.getAlternateId().add(patientId);
	
			// Add local patient number
			AlternateId localId = new AlternateId();
			localId.setDefinition("local_patient_no");
			localId.setName(patientSummaryVO.getPatientLocalId());
			this.em.persist(localId);
			patient.getAlternateId().add(localId);
	
			//TODO: Add submission???
			patient.setSubmission_Id(1);
	
			// Add mutations
			for (VariantDTO variantDTO : patientSummaryVO.getVariantDTOList())
			{
				List<Variant> mutations = this.db.query(Variant.class).equals(Variant.NAME, variantDTO.getCdnaNotation()).find();
	
				if (mutations.size() != 1)
					throw new UploadServiceException("No mutation found for " + variantDTO.getCdnaNotation());
	
				patient.getMutations().add(mutations.get(0));
			}
	
			// Add publications
			if (CollectionUtils.isNotEmpty(patientSummaryVO.getPublicationDTOList()))
			{
				for (PublicationDTO publicationVO : patientSummaryVO.getPublicationDTOList())
				{
					if (publicationVO.getPubmedId() == null)
						continue;
	
					List<OntologyTerm> pubmedTerms = this.db.query(OntologyTerm.class).equals(OntologyTerm.NAME, publicationVO.getPubmedId()).find();
					
					if (pubmedTerms.size() != 1)
						continue;
	
					List<Publication> publications = this.db.query(Publication.class).equals(Publication.PUBMEDID, pubmedTerms.get(0).getId()).find();
	
					if (publications.size() != 1)
						logger.error("No publication found for Pubmed ID " + publicationVO.getPubmedId());
	
					patient.getPatientreferences().add(publications.get(0));
				}
			}
	
			
			// Insert patient
			this.em.persist(patient);
			
			// Insert ObservedValues
			
			for (ObservedValueDTO observedValueDTO : patientSummaryVO.getObservedValueDTOList())
			{
//				if (observedValueDTO == null)
//					continue;
//
				List<ObservableFeature> featureList = this.db.query(ObservableFeature.class).equals(ObservableFeature.NAME, observedValueDTO.getFeatureDTO().getFeatureName()).find();
	
				if (featureList.size() != 1)
					logger.error("Not exactly one ObservableFeature found for " + observedValueDTO.getFeatureDTO().getFeatureName());
		
				ObservedValue observedValue = new ObservedValue();
				observedValue.setFeature(featureList.get(0));
				observedValue.setTarget(patient);
				observedValue.setValue(observedValueDTO.getValue());
	
				this.em.persist(observedValue);
			}
			
			this.em.flush();
			this.em.clear();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new UploadServiceException(e.getMessage());
		}
	}

	/**
	 * Insert patient and set primary key
	 * 
	 * @param patient
	 * @return number of patients inserted
	 * @throws Exception
	 */
//	public void insert(PatientUploadDTO patientUploadDTO)
//	{
//		try
//		{
//			if (patientUploadDTO == null)
//				throw new UploadServiceException("Patient to be inserted must not be null.");
//
//			//cache for objects to be imported from file (in batch)
//			final List<MutationUploadDTO> mutationUploadDTOList = new ArrayList<MutationUploadDTO>();
//			final List<ObservedValueDTO> observedValueDTOList   = new ArrayList<ObservedValueDTO>();
//			final List<Patient> patientList   = new ArrayList<Patient>();
//			final List<AlternateId> alternateIdList = new ArrayList<AlternateId>();
//			final List<String> pubmedList                       = new ArrayList<String>();
//
//			Patient patient = new Patient();
//	
//			patient.setName(patientUploadDTO.getPatientName());
//
//			// Add stable external identifier
//			AlternateId patientId = new AlternateId();
//			patientId.setDefinition("molgenis_patient_id");
//			patientId.setName(patientUploadDTO.getPatientName());
//			patient.getAlternateId().add(patientId);
//	
//			// Add local patient number
//			AlternateId localId = new AlternateId();
//			localId.setDefinition("local_patient_no");
//			localId.setName(patientUploadDTO.getPatientLocalId());
//			patient.getAlternateId().add(localId);
//	
//			// Add submission (id)
//			patient.setSubmission_Id(patientUploadDTO.getSubmissionId());
//	
//			// Add mutations
//			for (String cdnaNotation : patientUploadDTO.getVariantCdnaNotationList())
//			{
//				SequenceCharacteristic variant = new SequenceCharacteristic();
//				variant.setName(cdnaNotation);
//				patient.getMutations().add(variant);
//			}
//
//			// Add publications
//			if (CollectionUtils.isNotEmpty(patientUploadDTO.getPubmedStringList()))
//			{
//				for (PublicationDTO publicationVO : patientUploadDTO.getPublicationDTOList())
//				{
//					if (publicationVO.getPubmedId() == null)
//						continue;
//	
//					List<OntologyTerm> pubmedTerms = this.db.query(OntologyTerm.class).equals(OntologyTerm.NAME, publicationVO.getPubmedId()).find();
//					
//					if (pubmedTerms.size() != 1)
//						continue;
//	
//					List<Publication> publications = this.db.query(Publication.class).equals(Publication.PUBMEDID, pubmedTerms.get(0).getId()).find();
//	
//					if (publications.size() != 1)
//						logger.error("No publication found for Pubmed ID " + publicationVO.getPubmedId());
//	
//					patient.getPatientreferences().add(publications.get(0));
//				}
//			}
//	
//			
//			// Insert patient
//			this.db.getEntityManager().persist(patient);
//			
//			// Insert ObservedValues
//			
//			for (ObservedValueDTO observedValueDTO : patientUploadDTO.getObservedValueDTOList())
//			{
////				if (observedValueDTO == null)
////					continue;
////
//				List<ObservableFeature> featureList = this.db.query(ObservableFeature.class).equals(ObservableFeature.NAME, observedValueDTO.getFeatureDTO().getFeatureName()).find();
//	
//				if (featureList.size() != 1)
//					logger.error("Not exactly one ObservableFeature found for " + observedValueDTO.getFeatureDTO().getFeatureName());
//		
//				ObservedValue observedValue = new ObservedValue();
//				observedValue.setFeature(featureList.get(0));
//				observedValue.setTarget(patient);
//				observedValue.setValue(observedValueDTO.getValue());
//	
//				this.db.getEntityManager().persist(observedValue);
//			}
//			
//			this.db.getEntityManager().flush();
//			this.db.getEntityManager().clear();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			throw new UploadServiceException(e.getMessage());
//		}
//	}

	/**
	 * Insert batch of patients. To be refactored.
	 * 
	 * @param file
	 * @param uploadBatchCsvReader
	 * @return number of patients inserted
	 */
	public int insert(File file)
	{
		try
		{
			Workbook workbook             = Workbook.getWorkbook(file);
//			UploadBatchExcelReader reader = new UploadBatchExcelReader();
//			UploadBatchExcelReader reader = (UploadBatchExcelReader) org.molgenis.mutation.ServiceLocator.instance().getContext().getBean("uploadBatchExcelReader");

			this.em.getTransaction().begin();
			int count                     = this.reader.importSheet(this.db, workbook.getSheet("Patients"), new SimpleTuple(), DatabaseAction.ADD_IGNORE_EXISTING, "");
			this.em.getTransaction().commit();
			return count;
		}
		catch (Exception e)
		{
			if (this.em.getTransaction().isActive())
				this.em.getTransaction().rollback();

			e.printStackTrace();
			//TODO: Produce nicer error message
			throw new UploadServiceException(e.getMessage());
		}
	}

	/**
	 * Get the biggest identifier without the leading 'P'
	 * 
	 * @return biggest identifier without the leading 'P'
	 */
	public int findMaxPatientIdentifier()
	{
		try
		{
			List<AlternateId> patientIdList = this.db.query(AlternateId.class).equals(AlternateId.DEFINITION, "molgenis_patient_id").find();
	
			if (CollectionUtils.isEmpty(patientIdList))
				return 0;
	
			Collections.sort(patientIdList, new AlternateIdComparator());
	
			return Integer.valueOf(patientIdList.get(patientIdList.size() - 1).getName().substring(1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new UploadServiceException(e.getMessage());
		}
	}

	/**
	 * Insert mutation and set primary key
	 * @param mutation
	 * @return number of mutations inserted
	 */
	public void insert(MutationUploadDTO mutationUploadDTO)
	{
		// Insert stable external identifier
		AlternateId alternateId = new AlternateId();
		alternateId.setDefinition("");
		alternateId.setName(mutationUploadDTO.getIdentifier());
		this.em.persist(alternateId);
		
		Variant variant = new Variant();
		variant.setAlternateId(alternateId);
		variant.setName(mutationUploadDTO.getCdnaNotation());
		variant.setFeatureType(this.ontologyTermCache.get("variant"));

		// Insert mutation
		this.em.persist(variant);
	}

	/**
	 * Get the biggest identifier without the leading 'M'
	 * @return biggest identifier without the leading 'M'
	 */
	public int findMaxMutationIdentifier()
	{
		try
		{
			List<AlternateId> mutationIdList = this.db.query(AlternateId.class).equals(AlternateId.DEFINITION, "molgenis_variant_id").find();
			
			if (CollectionUtils.isEmpty(mutationIdList))
				return 0;
	
			Collections.sort(mutationIdList, new AlternateIdComparator());
	
			return Integer.valueOf(mutationIdList.get(mutationIdList.size() - 1).getName().substring(1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new UploadServiceException(e.getMessage());
		}
	}

	public void assignValuesFromNotation(MutationUploadDTO mutationUploadDTO) throws UploadServiceException
	{
//		logger.debug(">>> assignValuesFromNotation: cdnaNotation==" + mutationUploadVO.getMutation().getCdna_notation());
		if (mutationUploadDTO.getCdnaNotation() != null)
		{
			String cdnaNotation    = mutationUploadDTO.getCdnaNotation();
			Pattern reIndel        = Pattern.compile("^c.([\\d+-_]+)del([ACGTacgt\\d]*)ins([ACGTacgt]*)$");
			Pattern reDeletion     = Pattern.compile("^c.([\\d+-_]+)del([ACGTacgt\\d]*)$");
			Pattern reDuplication  = Pattern.compile("^c.([\\d+-_]+)dup([ACGTacgt\\d]*)$");
			Pattern reInsertion    = Pattern.compile("^c.([\\d+-_]+)ins([ACGTacgt]*)$");
			Pattern reSubstitution = Pattern.compile("^c.([\\d+-]+)([ACGTacgt]+)>([ACGTacgt]+)$");
			Matcher mIndel        = reIndel.matcher(cdnaNotation);
			Matcher mDeletion     = reDeletion.matcher(cdnaNotation);
			Matcher mDuplication  = reDuplication.matcher(cdnaNotation);
			Matcher mInsertion    = reInsertion.matcher(cdnaNotation);
			Matcher mSubstitution = reSubstitution.matcher(cdnaNotation);

			if (mIndel.matches())
			{
				mutationUploadDTO.setEvent("insertion/deletion");
				
				//deletion

				String[] position = mIndel.group(1).split("_");
				String deletion   = mIndel.group(2);
				String insertion  = mIndel.group(3);

//				logger.debug(">>> position==" + position + ", deletion==" + deletion + ", insertion==" + insertion);

				mutationUploadDTO.setMutationPosition(position[0]);
				if (position.length == 2)
					mutationUploadDTO.setLength(Integer.valueOf(position[1]) - Integer.valueOf(position[0]) + 1);
				else if (StringUtils.isNotEmpty(deletion))
					if (StringUtils.isNumeric(deletion))
						mutationUploadDTO.setLength(Integer.valueOf(deletion));
					else
						mutationUploadDTO.setLength(deletion.length());
				else
					mutationUploadDTO.setLength(1);
				
				// insertion

				mutationUploadDTO.setNtChange(insertion);
			}
			else if (mDeletion.matches())
			{
				String[] position = mDeletion.group(1).split("_");
				String deletion   = mDeletion.group(2);
				
				mutationUploadDTO.setEvent("deletion");
				mutationUploadDTO.setMutationPosition(position[0]);
				if (position.length == 2)
					mutationUploadDTO.setLength(Integer.valueOf(position[1]) - Integer.valueOf(position[0]) + 1);
				else if (StringUtils.isNotEmpty(deletion))
					if (StringUtils.isNumeric(deletion))
						mutationUploadDTO.setLength(Integer.valueOf(deletion));
					else
						mutationUploadDTO.setLength(deletion.length());
				else
					mutationUploadDTO.setLength(1);
			}
			else if (mDuplication.matches())
			{
				String[] position  = mDuplication.group(1).split("_");
				String duplication = mDuplication.group(2);
				
				mutationUploadDTO.setEvent("duplication");
				mutationUploadDTO.setMutationPosition(position[0]);
				if (position.length == 2)
					mutationUploadDTO.setLength(Integer.valueOf(position[1]) - Integer.valueOf(position[0]) + 1);
				else if (StringUtils.isNotEmpty(duplication))
					if (StringUtils.isNumeric(duplication))
						mutationUploadDTO.setLength(Integer.valueOf(duplication));
					else
						mutationUploadDTO.setLength(duplication.length());
				else
					mutationUploadDTO.setLength(1);
			}
			else if (mInsertion.matches())
			{
				String[] position = mInsertion.group(1).split("_");
				String insertion  = mInsertion.group(2);
				mutationUploadDTO.setEvent("insertion");
				mutationUploadDTO.setMutationPosition(position[0]);
				mutationUploadDTO.setLength(insertion.length());
				mutationUploadDTO.setNtChange(insertion);
			}
			else if (mSubstitution.matches())
			{
				mutationUploadDTO.setEvent("point mutation");
				mutationUploadDTO.setMutationPosition(mSubstitution.group(1));
				mutationUploadDTO.setLength(mSubstitution.group(2).length());
				mutationUploadDTO.setNtChange(mSubstitution.group(3));
			}
			else
			{
				throw new UploadServiceException("No valid mutation notation: '" + cdnaNotation + "'");
			}
			int mutationPosition = SequenceUtils.getCDNAPosition(mutationUploadDTO.getMutationPosition());
			mutationUploadDTO.setCdnaStart(mutationPosition);
			mutationUploadDTO.setCdnaEnd(mutationUploadDTO.getCdnaStart() + mutationUploadDTO.getLength() - 1);
			mutationUploadDTO.setGdnaStart(0);
			mutationUploadDTO.setGdnaEnd(0);
//			logger.debug(">>> assignValuesFromNotation: cdnaNotation==" + cdnaNotation + ", event==" + mutationUploadVO.getMutation().getEvent() + ", pos==" + mutationUploadVO.getMutation().getPosition() + ", len==" + mutationUploadVO.getMutation().getLength() + ", ntchange==" + mutationUploadVO.getMutation().getNtchange());
		}
		this.assignValuesFromPosition(mutationUploadDTO);
	}

	public void assignValuesFromPosition(MutationUploadDTO mutationUploadDTO)
	{
		if (StringUtils.isEmpty(mutationUploadDTO.getMutationPosition()) || "0".equals(mutationUploadDTO.getMutationPosition()))
			return;

		GeneDTO geneDTO           = this.findGene();
		List<ExonDTO> exonDTOList = this.findAllExons();
		StringBuffer tmpSequence  = new StringBuffer();
		for (ExonDTO exonDTO : exonDTOList)
			tmpSequence.append(exonDTO.getNuclSequence());
		String nuclSequence       = tmpSequence.toString(); //geneDTO.getNuclSequence();
		StringBuffer mutSequence  = new StringBuffer(nuclSequence);

		// find corresponding exon/intron
		
		ExonDTO exonDTO           = this.findExonByMutationPosition(mutationUploadDTO.getMutationPosition());

		mutationUploadDTO.setExonId(exonDTO.getId());
		mutationUploadDTO.setExonIsIntron(exonDTO.getIsIntron());
		
		mutationUploadDTO.setGdnaStart(SequenceUtils.getGDNAPosition(mutationUploadDTO.getMutationPosition(), exonDTO));

		int mutationStart;
//		System.out.println(">>> assignValuesFromPosition: mut.gdna==" + mutationUploadVO.getMutation().getGdna_Position() + ", gene.start==" + mutationUploadVO.getGene().getBpStart());
		if ("F".equals(geneDTO.getOrientation()))
			mutationStart = mutationUploadDTO.getGdnaStart() - geneDTO.getBpStart().intValue();
		else
			mutationStart = geneDTO.getBpStart().intValue() - mutationUploadDTO.getGdnaStart();
		
		if (mutationUploadDTO.getLength() == null)
			mutationUploadDTO.setLength(1); // default value
		
//		mutationUploadDTO.setNt(nuclSequence.substring(mutationStart, mutationStart + mutationUploadDTO.getLength()).toUpperCase());
		this.assignNt(mutationUploadDTO, nuclSequence, mutationStart);

		if (mutationUploadDTO.getEvent().equals("deletion"))
		{
			int mutationEnd      = mutationStart + mutationUploadDTO.getLength();
			this.assignNt(mutationUploadDTO, nuclSequence, mutationStart);
//			logger.debug(">>> nt==" + mutationUploadVO.getNt());
			mutSequence.delete(mutationStart, mutationEnd);
			mutationUploadDTO.setNtChange("");
		}
		else if (mutationUploadDTO.getEvent().equals("duplication"))
		{
			//int mutationEnd      = mutationStart + mutationUploadVO.getMutation().getLength();
			this.assignNt(mutationUploadDTO, nuclSequence, mutationStart);
			mutSequence.insert(mutationStart, mutationUploadDTO.getNt());
			mutationUploadDTO.setNtChange("");
		}
		else if (mutationUploadDTO.getEvent().equals("insertion"))
		{
			mutationStart++; // insert *after* position
			mutSequence.insert(mutationStart, mutationUploadDTO.getNtChange());
			mutationUploadDTO.setLength(mutationUploadDTO.getNtChange().length());
		}
		else if (mutationUploadDTO.getEvent().equals("point mutation"))
		{
			int mutationEnd      = mutationStart + mutationUploadDTO.getNtChange().length();
			//this.mutationVO.setNt(nuclSequence.substring(mutationStart, mutationEnd).toUpperCase());
			if (mutationUploadDTO.getNtChange() != null)
				mutSequence.replace(mutationStart, mutationEnd, mutationUploadDTO.getNtChange());
			mutationUploadDTO.setLength(mutationUploadDTO.getNtChange().length());
		}
		else if (mutationUploadDTO.getEvent().equals("insertion/deletion"))
		{
			// deletion
			
			int mutationEnd      = mutationStart + mutationUploadDTO.getLength();
			//this.mutationVO.setNt(nuclSequence.substring(mutationStart, mutationEnd).toUpperCase());
			this.assignNt(mutationUploadDTO, nuclSequence, mutationStart);
//			logger.debug(">>> nt==" + mutationUploadVO.getNt());
			mutSequence.delete(mutationStart, mutationEnd);
			
			// insertion
			
			mutationStart++; // insert *after* position
			mutSequence.insert(mutationStart, mutationUploadDTO.getNtChange());
		}

		if ("F".equals(exonDTO.getOrientation()))
			mutationUploadDTO.setGdnaEnd(mutationUploadDTO.getGdnaStart() + mutationUploadDTO.getLength() - 1);
		else
			mutationUploadDTO.setGdnaEnd(mutationUploadDTO.getGdnaStart() - mutationUploadDTO.getLength() + 1);

		this.assignCdnaNotation(mutationUploadDTO);
		this.assignGdnaNotation(mutationUploadDTO);

		// Calculations for transcribed part
		
		if (exonDTO.getIsIntron())
		{
//			mutationUploadDTO.setCodon("");
			mutationUploadDTO.setAa("");
			mutationUploadDTO.setAachange("");
			mutationUploadDTO.setCodonChange("");
			mutationUploadDTO.setAaStart(0);
			mutationUploadDTO.setAaEnd(0);
			mutationUploadDTO.setAaNotation("");
		}
		else
		{
			// splice the sequences
			String splNuclSeq  = SequenceUtils.splice(nuclSequence);
			String splMutSeq   = SequenceUtils.splice(mutSequence.toString());
			int mutTripletPos  = SequenceUtils.getFirstTripletChange(splNuclSeq, splMutSeq);
			int changeCodonPos = StringUtils.indexOfDifference(splNuclSeq, splMutSeq) + 1;
			int changeCodonNum = SequenceUtils.getCodonNum(changeCodonPos); //new Double(Math.ceil(changePos / 3.0)).intValue();
			
			// translate the sequences
			String trlNuclSeq  = SequenceUtils.translate(splNuclSeq);
			String trlMutSeq   = SequenceUtils.translate(splMutSeq);
			int trlTripletPos  = SequenceUtils.getFirstTripletChange(trlNuclSeq, trlMutSeq);
			int changeAaPos    = StringUtils.indexOfDifference(trlNuclSeq, trlMutSeq) + 1;
			int changeAaNum    = SequenceUtils.getCodonNum(changeAaPos); //new Double(Math.ceil(changePos / 3.0)).intValue();
			// calculate position of first changed amino acid
			//int changePos      = StringUtils.indexOfDifference(trlNuclSeq, trlMutSeq) + 1;
			// calculate position of first changed codon

//			mutationUploadDTO.setCodon(SequenceUtils.getCodonByPosition(splNuclSeq, mutTripletPos)); //StringUtils.substring(splNuclSeq, mutTripletPos, mutTripletPos + 3));
			mutationUploadDTO.setCodonChange(SequenceUtils.getCodonByPosition(splMutSeq, mutTripletPos)); //StringUtils.substring(splMutSeq, mutTripletPos, mutTripletPos + 3));
			mutationUploadDTO.setAa(SequenceUtils.getCodonByPosition(trlNuclSeq, trlTripletPos)); //StringUtils.substring(trlNuclSeq, trlTripletPos, trlTripletPos + 3));
			mutationUploadDTO.setAachange(SequenceUtils.getCodonByPosition(trlMutSeq, trlTripletPos)); //StringUtils.substring(trlMutSeq, trlTripletPos, trlTripletPos + 3));

			mutationUploadDTO.setAaStart(changeCodonNum);
			
			this.assignAaNotation(mutationUploadDTO, trlMutSeq, changeAaNum);
		}

		this.assignType(mutationUploadDTO);
		this.assignConsequence(mutationUploadDTO);
	}
	
	public void setDefaults(MutationUploadDTO mutationUploadDTO)
	{
		mutationUploadDTO.setEvent("NA");

		// set default gene
		SearchService searchService = ServiceLocator.instance().getSearchService();
		mutationUploadDTO.setGeneDTO(searchService.findGene());
	}



	private void assignNt(MutationUploadDTO mutationUploadDTO, String nuclSequence, int mutationStart)
	{
		Integer length = mutationUploadDTO.getLength();

		if (length == null)
			length = 1;

		mutationUploadDTO.setNt(nuclSequence.substring(mutationStart, mutationStart + length).toUpperCase());
	}
	
	private void assignConsequence(MutationUploadDTO mutationUploadDTO)
	{
		// default: missense, no effect on splicing
		mutationUploadDTO.setConsequence("Missense codon");
		mutationUploadDTO.setEffectOnSplicing(false);

		if (mutationUploadDTO.getExonIsIntron())
		{
			mutationUploadDTO.setConsequence("Altered splicing -> premature termination codon");
			mutationUploadDTO.setEffectOnSplicing(true);
		}
		else if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1 || mutationUploadDTO.getAaNotation().indexOf("Ter") > -1)
			mutationUploadDTO.setConsequence("Premature termination codon");
		else if (mutationUploadDTO.getAaStart() != null && mutationUploadDTO.getAaStart() == 1)
			mutationUploadDTO.setConsequence("No initiation of transcription/translation");
	}
	
	private void assignType(MutationUploadDTO mutationUploadDTO)
	{
		if (mutationUploadDTO.getExonIsIntron())
			mutationUploadDTO.setType("splice-site mutation");
		else if (mutationUploadDTO.getEvent().equals("deletion"))
			if (mutationUploadDTO.getLength() <= 20)
				if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1)
					mutationUploadDTO.setType("small deletion frame-shift");
				else
					mutationUploadDTO.setType("small deletion in-frame");
			else
				if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1)
					mutationUploadDTO.setType("large deletion frame-shift");
				else
					mutationUploadDTO.setType("large deletion in-frame");
		else if (mutationUploadDTO.getEvent().equals("duplication"))
			if (mutationUploadDTO.getLength() <= 20)
				if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1)
					mutationUploadDTO.setType("small duplication frame-shift");
				else
					mutationUploadDTO.setType("small duplication in-frame");
			else
				if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1)
					mutationUploadDTO.setType("large duplication frame-shift");
				else
					mutationUploadDTO.setType("large duplication in-frame");
		else if (mutationUploadDTO.getEvent().equals("insertion"))
			if (mutationUploadDTO.getLength() <= 20)
				if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1)
					mutationUploadDTO.setType("small insertion frame-shift");
				else
					mutationUploadDTO.setType("small insertion in-frame");
			else
				if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1)
					mutationUploadDTO.setType("large insertion frame-shift");
				else
					mutationUploadDTO.setType("large insertion in-frame");
		else if (mutationUploadDTO.getAaNotation().indexOf("fsX") > -1 || mutationUploadDTO.getAaNotation().indexOf("Ter") > -1)
			mutationUploadDTO.setType("nonsense mutation");
		else
			mutationUploadDTO.setType("missense mutation");
	}
	
	private String getNotation(MutationUploadDTO mutationUploadDTO, String position)
	{
		if (mutationUploadDTO.getLength() == null)
			return "";

		String notation = position;

		if (mutationUploadDTO.getEvent().equals("insertion"))
			notation += "_" + SequenceUtils.getAddedPosition(position, 1);
		else if (mutationUploadDTO.getLength() > 1)
			notation += "_" + SequenceUtils.getAddedPosition(position, mutationUploadDTO.getLength() - 1);

		if (mutationUploadDTO.getEvent().equals("deletion"))
		{
			notation += "del";
			if (mutationUploadDTO.getLength() <= 2)
				notation += mutationUploadDTO.getNt();
		}
		else if (mutationUploadDTO.getEvent().equals("duplication"))
		{
			notation += "dup";
			if (mutationUploadDTO.getLength() <= 2)
				notation += mutationUploadDTO.getNt();
		}
		else if (mutationUploadDTO.getEvent().equals("insertion"))
			notation += "ins" + mutationUploadDTO.getNtChange();
		else if (mutationUploadDTO.getEvent().equals("point mutation"))
			notation += mutationUploadDTO.getNt() + ">" + mutationUploadDTO.getNtChange();
		else if (mutationUploadDTO.getEvent().equals("insertion/deletion"))
		{
			// deletion
			notation += "del";
			if (mutationUploadDTO.getLength() <= 2)
				notation += mutationUploadDTO.getNt();
			//insertion
			notation += "ins" + mutationUploadDTO.getNtChange();
		}
		return notation;
	}
	
	private void assignGdnaNotation(MutationUploadDTO mutationUploadDTO)
	{
		if (mutationUploadDTO.getLength() != null)
			mutationUploadDTO.setGdnaNotation("g." + this.getNotation(mutationUploadDTO, mutationUploadDTO.getGdnaStart().toString()));
		else
			mutationUploadDTO.setGdnaNotation("");
	}

	private void assignCdnaNotation(MutationUploadDTO mutationUploadDTO)
	{
		if (mutationUploadDTO.getLength() != null)
			mutationUploadDTO.setCdnaNotation("c." + this.getNotation(mutationUploadDTO, mutationUploadDTO.getMutationPosition()));
		else
			mutationUploadDTO.setCdnaNotation("");
	}

	private void assignAaNotation(MutationUploadDTO mutationUploadDTO, String trlMutSeq, int codonNum)
	{
		if (codonNum == 1)
			mutationUploadDTO.setAaNotation("p.0");
		else if (codonNum > 1)
		{
			mutationUploadDTO.setAaNotation("p." + mutationUploadDTO.getAa() + codonNum + mutationUploadDTO.getAachange());
			if (mutationUploadDTO.getLength() % 3 != 0 && !mutationUploadDTO.getEvent().equals("point mutation"))
			{
				mutationUploadDTO.setAaNotation(mutationUploadDTO.getAaNotation() + "fs");
				int terPos = SequenceUtils.indexOfCodon(trlMutSeq, "Ter", codonNum);

				if (terPos > -1)
					mutationUploadDTO.setAaNotation(mutationUploadDTO.getAaNotation() + "X" + (terPos - codonNum + 1)); // + " DEBUG: " + terPos + "-" + codonNum + ", "+ trlMutSeq); //StringUtils.substring(trlMutSeq, (codonNum - 1) * 3));
			}
		}
		else
			mutationUploadDTO.setAaNotation("");
	}
}
