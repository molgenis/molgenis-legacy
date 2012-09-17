
/* File:        col7a1/model/UploadBatch.java
 * Copyright:   GBIC 2000-2010, all rights reserved
 * Date:        August 11, 2010
 * 
 * generator:   org.molgenis.generators.csv.CsvReaderGen 3.3.2-testing
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package org.molgenis.myo5b.csv;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.CsvToDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.security.Login;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;
import org.molgenis.variant.Patient;
import org.molgenis.variant.SequenceCharacteristic;
import org.molgenis.variant.SequenceRelation;
import org.molgenis.variant.Variant;

import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.MutationUploadDTO;
import org.molgenis.mutation.service.UploadService;
import org.molgenis.pheno.AlternateId;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.core.Publication;
import org.molgenis.core.service.PublicationService;
import org.molgenis.submission.Submission;
import org.springframework.stereotype.Component;

/**
 * Reads UploadBatch from a delimited (csv) file, resolving xrefs to ids where needed, that is the tricky bit ;-)
 */
@Component
public class UploadBatchCsvReader extends CsvToDatabase<Entity>
{
	public final transient Logger logger    = Logger.getLogger(UploadBatchCsvReader.class);
	private final String[] patientValueCols = { "ancestry", "consanguinity", "gender", "onset", "diagnosis (LM, EM)", "PAS/CD10/EM data available", "Additional IHC data available", "Tx (SB, C, L)", "age at Tx (years)", "rejection (yes/no)", "Jaundice+pruritis episodes", "result of last liver biopsy", "COD (+age)", "additional diseases", "height at last follow up", "psychomotor development" };
	private final String[] variantValueCols = { "par/mat", "homo-/heterozygous", "consequence", "MYO5B domain", "3D structure (only for motor domain)", "conserved (yes/no)", "function of affected residue", "mRNA expression", "protein expression", "protein IHC", "rab11-binding site affected (predicted)", "rab8-binding site affected (predicted)" };

	/**
	 * Imports UploadBatch from tab/comma delimited File
	 * @param db database to import into
	 * @param reader csv reader to load data from
	 * @param defaults to set default values for each row
	 * @param dbAction indicating wether to add,update,remove etc
	 * @param missingValues indicating what value in the csv is treated as 'null' (e.g. "" or "NA")
	 * @return number of elements imported
	 */
	public int importCsv(final Database db, CsvReader reader, final Tuple defaults, final DatabaseAction dbAction, final String missingValues) throws DatabaseException, IOException, Exception 
	{
		//cache for entities of which xrefs couldn't be resolved (e.g. if there is a self-refence)
		//these entities can be updated with their xrefs in a second round when all entities are in the database
		//final List<UploadBatch> uploadBatchsMissingRefs = new ArrayList<UploadBatch>();

//		db.beginTx();
//
//		for (String variantCol : variantValueCols)
//		{
//			Measurement measurement = new Measurement();
//			measurement.setName(variantCol);
//			measurement.setDataType("string");
//			measurement.setTemporal(false);
//			db.add(measurement);
//		}
//		for (String patientCol : patientValueCols)
//		{
//			Measurement measurement = new Measurement();
//			measurement.setName(patientCol);
//			measurement.setDataType("string");
//			measurement.setTemporal(false);
//			db.add(measurement);
//		}
//
//		db.commitTx();

		final EntityManager em                      = db.getEntityManager();
		final UploadService uploadService           = ServiceLocator.instance().getUploadService();
		final PublicationService publicationService = ServiceLocator.instance().getPublicationService();
		final Login securityService                 = ServiceLocator.instance().getSecurityService();

		final Submission submission       = new Submission();
		DateFormat dateFormat             = new SimpleDateFormat("yyyy-MM-dd");
		submission.setDate(dateFormat.format(new Date()));
		submission.setReleasedate(dateFormat.format(new Date()));
		submission.setIdentifier("S" + new Date());
		List<Integer> submitters          = new ArrayList<Integer>();
		submitters.add(securityService.getUserId());
		submission.setSubmitters_Id(submitters);
		db.add(submission);

		//cache for objects to be imported from file (in batch)
		final Map<String, AlternateId> alternateIdList    = new HashMap<String, AlternateId>();
		final List<ObservedValue> observedValueList       = new ArrayList<ObservedValue>();
		final Map<String, Patient> patientList            = new HashMap<String, Patient>();
		final Set<String> pubmedStringList                = new HashSet<String>();
		final Map<String, Variant> variantList            = new HashMap<String, Variant>();
		final List<SequenceRelation> sequenceRelationList = new ArrayList<SequenceRelation>();

		reader.setMissingValues(missingValues);
		/*
		 * Set column separator to \t
		 * Override from CsvBufferedReaderMultiline:
		 * public static char[] separators = { ',', '\t', ';', ' ' };
		 */
		reader.setSeparator('\t');

		int mutationIdentifier = uploadService.findMaxMutationIdentifier();
		int patientIdentifier  = uploadService.findMaxPatientIdentifier();

		for (Tuple tuple : reader)
		{
			//parse object, setting defaults and values from file

			Patient patient = new Patient();

			patient.setSubmission(submission);

			// Add stable external identifier

			patientIdentifier = patientIdentifier + 1;
			patient.setName("P" + patientIdentifier);

			AlternateId patientId = new AlternateId();
			patientId.setDefinition("molgenis_patient_id");
			patientId.setName("P" + patientIdentifier);
			alternateIdList.put(patientId.getName(), patientId);

			patient.getAlternateId().add(patientId);
			
			// Add local id

			String localIdString = tuple.getString("patient ID");

			AlternateId localId = new AlternateId();
			localId.setDefinition("local_patient_no");
			localId.setName(localIdString);
			alternateIdList.put(localId.getName(), localId);
			
			patient.getAlternateId().add(localId);

			// Add variants

			if (StringUtils.isNotEmpty(tuple.getString("cDNA change")))
			{
				String[] cdnaNotations   = StringUtils.split(tuple.getString("cDNA change"), ", ");
				String[] aaNotations     = StringUtils.split(tuple.getString("protein change"), ", ");

				for (int i = 0; i < cdnaNotations.length; i++)
				{
					String cdnaNotation  = cdnaNotations[i];
					String aaNotation    = (ArrayUtils.getLength(aaNotations) == ArrayUtils.getLength(cdnaNotations) ? aaNotations[i] : "");

					// Check whether already existing
					List<Variant> results = db.query(Variant.class).equals(Variant.NAME, cdnaNotation).find();

					if (results.size() < 1)
					{
						// Calculate some values from cdna position
						MutationUploadDTO mutationUploadDTO = new MutationUploadDTO();
						mutationUploadDTO.setCdnaNotation(cdnaNotation);
						uploadService.assignValuesFromNotation(mutationUploadDTO);
						if (StringUtils.isNotEmpty(aaNotation))
							mutationUploadDTO.setAaNotation(aaNotation);

						// Add cDNA variant notation
						Variant cdnaVariant = new Variant();
						cdnaVariant.setFeatureType(uploadService.getOntologyTermCache().get("variant"));
						cdnaVariant.setName(cdnaNotation);
						cdnaVariant.setSeqlen(mutationUploadDTO.getLength());

						variantList.put(cdnaVariant.getName(), cdnaVariant);

						cdnaVariant.setStartCdna(mutationUploadDTO.getCdnaStart());
						cdnaVariant.setEndCdna(mutationUploadDTO.getCdnaEnd());

						cdnaVariant.setStartAa(mutationUploadDTO.getAaStart());
						cdnaVariant.setEndAa(mutationUploadDTO.getAaEnd());

						cdnaVariant.setStartGdna(mutationUploadDTO.getGdnaStart());
						cdnaVariant.setEndGdna(mutationUploadDTO.getGdnaEnd());

						cdnaVariant.setType(mutationUploadDTO.getType());

						// Add stable external identifier

						mutationIdentifier = mutationIdentifier + 1;

						AlternateId mutationId = new AlternateId();
						mutationId.setDefinition("molgenis_variant_id");
						mutationId.setName("M" + mutationIdentifier);

						alternateIdList.put(mutationId.getName(), mutationId);

						cdnaVariant.getAlternateId().add(mutationId);

						// Add observed values

						for (String variantValueCol : variantValueCols)
						{
							String value = ObjectUtils.toString(tuple.getString(variantValueCol), "unknown");

							ObservedValue observedValue = new ObservedValue();
							ObservableFeature feature   = new ObservableFeature();
							feature.setName(variantValueCol);
							observedValue.setFeature(feature);
							observedValue.setTarget(cdnaVariant);
							observedValue.setValue(value);
								
							observedValueList.add(observedValue);
						}

						// Add calculated values from variantUploadDTO
						
						ObservedValue codonChangeOV          = new ObservedValue();
						ObservableFeature codonChangeFeature = new ObservableFeature();
						codonChangeFeature.setName("Codon change");
						codonChangeOV.setFeature(codonChangeFeature);
						codonChangeOV.setTarget(cdnaVariant);
						codonChangeOV.setValue(mutationUploadDTO.getCodonChange());
						observedValueList.add(codonChangeOV);

						ObservedValue consequenceOV          = new ObservedValue();
						ObservableFeature consequenceFeature = new ObservableFeature();
						consequenceFeature.setName("Consequence");
						consequenceOV.setFeature(consequenceFeature);
						consequenceOV.setTarget(cdnaVariant);
						consequenceOV.setValue(mutationUploadDTO.getConsequence());
						observedValueList.add(consequenceOV);

						ObservedValue splicingOV          = new ObservedValue();
						ObservableFeature splicingFeature = new ObservableFeature();
						splicingFeature.setName("Effect on splicing");
						splicingOV.setFeature(splicingFeature);
						splicingOV.setTarget(cdnaVariant);
						splicingOV.setValue(mutationUploadDTO.getEffectOnSplicing().toString());
						observedValueList.add(splicingOV);

						ObservedValue eventOV          = new ObservedValue();
						ObservableFeature eventFeature = new ObservableFeature();
						eventFeature.setName("Event");
						eventOV.setFeature(eventFeature);
						eventOV.setTarget(cdnaVariant);
						eventOV.setValue(mutationUploadDTO.getEvent());
						observedValueList.add(eventOV);

						ObservedValue ntchangeOV          = new ObservedValue();
						ObservableFeature ntchangeFeature = new ObservableFeature();
						ntchangeFeature.setName("NT change");
						ntchangeOV.setFeature(ntchangeFeature);
						ntchangeOV.setTarget(cdnaVariant);
						ntchangeOV.setValue(mutationUploadDTO.getNtChange());
						observedValueList.add(ntchangeOV);

						patient.getMutations().add(cdnaVariant);
					}
					else
					{
						patient.getMutations().add(results.get(0));
					}
				}
			}

			// Add publications
			if (StringUtils.isNotEmpty(tuple.getString("Pubmed ID")))
			{
				for (String pubmedString : tuple.getString("PubMed ID").split("[,;]"))
				{
					pubmedString = StringUtils.deleteWhitespace(pubmedString);

					List<Publication> results = db.query(Publication.class).equals(Publication.NAME, pubmedString).find();

					if (results.size() < 1)
					{
						pubmedStringList.add(pubmedString);

						Publication publication = new Publication();
						publication.setName(pubmedString);

						patient.getPatientreferences().add(publication);
					}
					else
					{
						patient.getPatientreferences().add(results.get(0));
					}
				}
			}

			// Add observed values for patients

			for (String patientValueCol : patientValueCols)
			{
				String value = ObjectUtils.toString(tuple.getString(patientValueCol), "unknown");
					
				ObservedValue observedValue = new ObservedValue();
				ObservableFeature feature   = new ObservableFeature();
				feature.setName(patientValueCol);
				observedValue.setFeature(feature);
				observedValue.setTarget(patient);
				observedValue.setValue(value);
					
				observedValueList.add(observedValue);
			}
			
			patientList.put(patient.getName(), patient);
		}

		int counter = 0;

		// Now finally import everything

//		counter += db.add(Arrays.asList(alternateIdList.toArray(new AlternateId[alternateIdList.size()])));
//		counter += db.add(Arrays.asList(alternateIdList.values().toArray(new AlternateId[0])));
		
		// resolve foreign keys for sequenceCharacteristicList
		
		List<Variant> resolvedVariantList = new ArrayList<Variant>();
		
		for (Variant variant : variantList.values())
		{
			if (CollectionUtils.isNotEmpty(variant.getAlternateId()))
			{
				List<AlternateId> resolvedAlternateIdList = new ArrayList<AlternateId>();

				for (AlternateId alternateId : variant.getAlternateId())
				{
					if (!em.contains(alternateId))
					{
						List<AlternateId> tmpList = db.query(AlternateId.class).equals(AlternateId.NAME, alternateId.getName()).find();
						
						if (tmpList.size() > 0)
							alternateId = tmpList.get(0);
					}
					resolvedAlternateIdList.add(alternateId);
				}
				variant.setAlternateId(resolvedAlternateIdList);
			}
			resolvedVariantList.add(variant);
		}

		counter += db.add(resolvedVariantList);

		// resolve foreign keys for sequenceRelationList

		List<SequenceRelation> resolvedSequenceRelationList = new ArrayList<SequenceRelation>();
		
		for (SequenceRelation relation : sequenceRelationList)
		{
			if (relation.getSequenceFeature() != null)
			{
				List<SequenceCharacteristic> sequenceFeatureList = db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.NAME, relation.getSequenceFeature().getName()).find();
				
				if (sequenceFeatureList.size() == 1)
				{
					relation.setFeature(sequenceFeatureList.get(0));
					relation.setSequenceFeature(sequenceFeatureList.get(0));
				}
			}
			if (relation.getSequenceTarget()!= null)
			{
				List<SequenceCharacteristic> sequenceTargetList = db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.NAME, relation.getSequenceTarget().getName()).find();
				
				if (sequenceTargetList.size() == 1)
				{
					relation.setSequenceTarget(sequenceTargetList.get(0));
					relation.setTarget(sequenceTargetList.get(0));
				}
			}
			resolvedSequenceRelationList.add(relation);
		}

		counter += db.add(resolvedSequenceRelationList);

		// resolve foreign keys for pubmedStringList
		
		if (pubmedStringList.size() > 0)
		{
			List<Publication> publicationList = publicationService.pubmedIdListToPublicationList(Arrays.asList(pubmedStringList.toArray(new String[pubmedStringList.size()])));
		
			counter += db.add(publicationList);
		}

		// resolve foreign keys for patientList

		List<Patient> resolvedPatientList = new ArrayList<Patient>();

		for (Patient patient : patientList.values())
		{
			if (CollectionUtils.isNotEmpty(patient.getAlternateId()))
			{
				List<AlternateId> resolvedAlternateIdList = new ArrayList<AlternateId>();

				for (AlternateId alternateId : patient.getAlternateId())
				{
					if (!em.contains(alternateId))
					{
						List<AlternateId> tmpList = db.query(AlternateId.class).equals(AlternateId.NAME, alternateId.getName()).find();
						
						if (tmpList.size() > 0)
							alternateId = tmpList.get(0);
					}
					resolvedAlternateIdList.add(alternateId);
				}
				patient.setAlternateId(resolvedAlternateIdList);
			}
			if (CollectionUtils.isNotEmpty(patient.getMutations()))
			{
				List<Variant> resolvedMutationList = new ArrayList<Variant>();

				for (Variant variant : patient.getMutations())
				{
					if (!em.contains(variant))
					{
						List<Variant> tmpList = db.query(Variant.class).equals(Variant.NAME, variant.getName()).find();
						
						if (tmpList.size() == 1)
							variant = tmpList.get(0);
					}
					resolvedMutationList.add(variant);
				}
				patient.setMutations(resolvedMutationList);
			}
			if (CollectionUtils.isNotEmpty(patient.getPatientreferences()))
			{
				List<Publication> resolvedPublicationList = new ArrayList<Publication>();
				
				for (Publication publication : patient.getPatientreferences())
				{
					if (!em.contains(publication))
					{
						List<Publication> tmpList = db.query(Publication.class).equals(Publication.NAME, publication.getName()).find();

						if (tmpList.size() == 1)
							publication = tmpList.get(0);
					}
					resolvedPublicationList.add(publication);
				}
				patient.setPatientreferences(resolvedPublicationList);
			}
			resolvedPatientList.add(patient);
		}

		counter += db.add(resolvedPatientList);

		// resolve foreign keys for observedValueList
		
		List<ObservedValue> resolvedObservedValueList = new ArrayList<ObservedValue>();
		
		for (ObservedValue observedValue : observedValueList)
		{
			if (observedValue.getFeature() != null)
			{
				List<ObservableFeature> observableFeatureList = db.query(ObservableFeature.class).equals(ObservableFeature.NAME, observedValue.getFeature().getName()).find();
				
				if (observableFeatureList.size() == 1)
					observedValue.setFeature(observableFeatureList.get(0));
			}
			if (observedValue.getTarget() != null)
			{
				List<ObservationTarget> observationTargetList = db.query(ObservationTarget.class).equals(ObservationTarget.NAME, observedValue.getTarget().getName()).find();
				
				if (observationTargetList.size() == 1)
					observedValue.setTarget(observationTargetList.get(0));
			}
			resolvedObservedValueList.add(observedValue);
		}
		
		counter += db.add(resolvedObservedValueList);

		return counter;
	}
	
	/**
	 * Imports UploadBatch from tab/comma delimited File
	 * @param db database to import into
	 * @param reader csv reader to load data from
	 * @param defaults to set default values for each row
	 * @param dbAction indicating wether to add,update,remove etc
	 * @param missingValues indicating what value in the csv is treated as 'null' (e.g. "" or "NA")
	 * @return number of elements imported
	 */
//	public int importCsvOld(final Database db, CsvReader reader, final Tuple defaults, final DatabaseAction dbAction, final String missingValues) throws DatabaseException, IOException, Exception 
//	{
//		//cache for entities of which xrefs couldn't be resolved (e.g. if there is a self-refence)
//		//these entities can be updated with their xrefs in a second round when all entities are in the database
//		//final List<UploadBatch> uploadBatchsMissingRefs = new ArrayList<UploadBatch>();
//
//		db.beginTx();
//
//		final UploadService uploadService = ServiceLocator.instance().getUploadService();
//
//		final Submission submission       = new Submission();
//		DateFormat dateFormat             = new SimpleDateFormat("yyyy-MM-dd");
//		submission.setDate(dateFormat.format(new Date()));
//		submission.setReleasedate(dateFormat.format(new Date()));
//		submission.setIdentifier("S" + new Date());
//		List<Integer> submitters          = new ArrayList<Integer>();
//		submitters.add(db.getSecurity().getUserId());
//		submission.setSubmitters_Id(submitters);
//		db.getEntityManager().persist(submission);
//
//		//cache for objects to be imported from file (in batch)
//		final List<MutationUploadDTO> mutationUploadDTOList = new ArrayList<MutationUploadDTO>();
//		final List<ObservedValueDTO> observedValueDTOList   = new ArrayList<ObservedValueDTO>();
//		final List<PatientUploadDTO> patientUploadDTOList   = new ArrayList<PatientUploadDTO>();
//		final List<String> pubmedList                       = new ArrayList<String>();
//
//		reader.setMissingValues(missingValues);
//		reader.parse(new CsvReaderListener()
//		{
//			Integer mutationIdentifier = uploadService.getMaxMutationIdentifier();
//			Integer patientIdentifier  = uploadService.getMaxPatientIdentifier();
//
//			public void handleLine(int lineNo, Tuple tuple) throws Exception
//			{
//				//parse object, setting defaults and values from file
//
//				PatientUploadDTO patientUploadDTO = new PatientUploadDTO();
//
//				patientIdentifier = patientIdentifier + 1;
//				patientUploadDTO.setPatientName("P" + patientIdentifier);
//				patientUploadDTO.setPatientLocalId(tuple.getString("ID CHARGE database"));
//				patientUploadDTO.setSubmissionId(submission.getId());
//
//				// Add variants
//				patientUploadDTO.setVariantCdnaNotationList(new ArrayList<String>());
//
//				if (StringUtils.isNotEmpty(tuple.getString("CHD7 c")))
//				{
//					String[] cdnaNotations   = StringUtils.split(tuple.getString("CHD7 c"), ", ");
//					String[] aaNotations     = StringUtils.split(tuple.getString("CHD7 p"), ", ");
//					String[] pathogenicities = StringUtils.split(tuple.getString("Pathogenicity"), ", ");
//					String[] mutationTypes   = StringUtils.split(tuple.getString("Mutation type"), ", ");
//
//					for (int i = 0; i < cdnaNotations.length; i++)
//					{
//						String cdnaNotation  = cdnaNotations[i];
//						String aaNotation    = (ArrayUtils.getLength(aaNotations) == ArrayUtils.getLength(cdnaNotations) ? aaNotations[i] : "");
//						String pathogenicity = (ArrayUtils.getLength(pathogenicities) == ArrayUtils.getLength(cdnaNotations) ? pathogenicities[i] : "");
//						String mutationType  = (ArrayUtils.getLength(mutationTypes) == ArrayUtils.getLength(cdnaNotations) ? mutationTypes[i] : "");
//
//						MutationUploadDTO mutationUploadDTO = new MutationUploadDTO();
//						mutationUploadDTO.setCdnaNotation("c." + cdnaNotation);
//						uploadService.assignValuesFromNotation(mutationUploadDTO);
//						if (StringUtils.isNotEmpty(aaNotation))
//							mutationUploadDTO.setAaNotation("p." + aaNotation);
//						mutationUploadDTO.setConsequence("Unknown");
//						mutationUploadDTO.setInheritance(tuple.getString("Segregation"));
//		
//						mutationIdentifier = mutationIdentifier + 1;
//						mutationUploadDTO.setIdentifier("M" + mutationIdentifier);
//						mutationUploadDTO.setPathogenicity(StringUtils.lowerCase(pathogenicity));
//						mutationUploadDTO.setType(mutationType);
//
//						patientUploadDTO.getVariantCdnaNotationList().add(mutationUploadDTO.getCdnaNotation());
//
//						// Add to mutationUploadDTOList if it does not exist already
//	
//						List<SequenceCharacteristic> results = db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.NAME, mutationUploadDTO.getCdnaNotation()).find();
//
//						if (results.size() == 0)
//							mutationUploadDTOList.add(mutationUploadDTO);
//					}
//				}
//
//				// Add publications
//				if (tuple.getString("Pubmed ID") != null)
//				{
//					String[] pubmedStringList = tuple.getString("PubMed ID").split("[,;]");
//					
//					for (String pubmedString : pubmedStringList)
//					{
//						pubmedString = StringUtils.deleteWhitespace(pubmedString);
//
//						patientUploadDTO.getPubmedStringList().add(pubmedString);
//						
//						// Add to publicationDTOList if it does not exists already
//						List<Publication> publicationList = db.query(Publication.class).equals(Publication.NAME, pubmedString).find();
//						
//						if (publicationList.size() == 0)
//							pubmedList.add(pubmedString);
//					}
//				}
//
//				// Add phenotypic values
//				patientUploadDTO.setObservedValueDTOList(new ArrayList<ObservedValueDTO>());
//
//				for (int i = 18; ; i++)
//				{
//					String colName = tuple.getColName(i);
//					
//					if (colName == null)
//						break;
//					
//					ObservedValueDTO observedValueDTO = new ObservedValueDTO();
//					FeatureDTO featureDTO             = new FeatureDTO();
//					featureDTO.setFeatureName(colName);
//					observedValueDTO.setFeatureDTO(featureDTO);
//					observedValueDTO.setTargetName(patientUploadDTO.getPatientName());
//					observedValueDTO.setValue(ObjectUtils.toString(tuple.getString(colName), "unknown"));
//
//					patientUploadDTO.getObservedValueDTOList().add(observedValueDTO);
//					
//					// Add to observedValueDTOList for insert
//					
//					observedValueDTOList.add(observedValueDTO);
//				}
//				
//				// Add to patientUploadDTOList
//				patientUploadDTOList.add(patientUploadDTO);
//			}
//		});
//		
//		PhenoService phenoService             = ServiceLocator.instance().getPhenoService();
//		PublicationService publicationService = ServiceLocator.instance().getPublicationService();
//
//		int counter = 0;
//		counter += uploadService.insert(mutationUploadDTOList.toArray(new MutationUploadDTO[0]));
//		counter += publicationService.insert(publicationService.pubmedIdListToPublicationDTOList(pubmedList));
//		counter += uploadService.insert(patientUploadDTOList.toArray(new PatientUploadDTO[0]));
//		counter += phenoService.insert(observedValueDTOList);
//
//		db.rollbackTx();
//
//		return counter;
//	}
}

