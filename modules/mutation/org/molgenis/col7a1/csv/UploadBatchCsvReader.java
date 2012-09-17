
/* File:        col7a1/model/UploadBatch.java
 * Copyright:   GBIC 2000-2010, all rights reserved
 * Date:        August 11, 2010
 * 
 * generator:   org.molgenis.generators.csv.CsvReaderGen 3.3.2-testing
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package org.molgenis.col7a1.csv;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.molgenis.framework.db.CsvToDatabase;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Database.DatabaseAction;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.UploadService;
import org.molgenis.mutation.vo.MutationSearchCriteriaVO;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.MutationUploadVO;
import org.molgenis.mutation.vo.ObservedValueVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.vo.PublicationVO;
import org.molgenis.submission.Submission;

/**
 * Reads UploadBatch from a delimited (csv) file, resolving xrefs to ids where needed, that is the tricky bit ;-)
 */
public class UploadBatchCsvReader extends CsvToDatabase<Entity>
{
	public static final transient Logger logger = Logger.getLogger(UploadBatchCsvReader.class);
	
			
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
	
		final MutationService mutationService   = new MutationService();
		mutationService.setDatabase(db);
		final UploadService uploadService       = new UploadService();
		uploadService.setDatabase(db);

		final Submission submission             = new Submission();
		DateFormat dateFormat                   = new SimpleDateFormat("yyyy-MM-dd");
		submission.setDate(dateFormat.format(new Date()));
		submission.setReleasedate(dateFormat.format(new Date()));
		submission.setIdentifier("S" + new Date());
		if (db instanceof JDBCDatabase)
		{
			List<Integer> submitters            = new ArrayList<Integer>();
			submitters.add(db.getLogin().getUserId());
			submission.setSubmitters_Id(submitters);
		}
		else if (db instanceof JpaDatabase)
		{
			List<MolgenisUser> submitters       = new ArrayList<MolgenisUser>();
			submitters.add(db.findById(MolgenisUser.class, db.getLogin().getUserId()));
//			submission.setSubmitters(submitters);
		}
		db.add(submission);

		//cache for objects to be imported from file (in batch)
		//TODO: Danny: Use or loose
		/*final List<PatientSummaryVO> patientList = */new ArrayList<PatientSummaryVO>(BATCH_SIZE);
		//wrapper to count
		final IntegerWrapper total = new IntegerWrapper(0);
		reader.setMissingValues(missingValues);

			Integer mutationIdentifier = uploadService.getMaxMutationIdentifier();
			Integer patientIdentifier  = uploadService.getMaxPatientIdentifier();

			for(Tuple tuple: reader)
			{

				//parse object, setting defaults and values from file
//				if (lineNo > 5) return;
				PatientSummaryVO patientSummaryVO = new PatientSummaryVO();

				patientSummaryVO.setSubmissionDate(submission.getDate());

				patientSummaryVO.setPatientNumber(tuple.getString("Local patient number"));
				patientSummaryVO.setPatientGender(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Gender")), "unknown"));
				patientSummaryVO.setPatientEthnicity(tuple.getString("Ethnicity"));
				patientSummaryVO.setPatientAge(ObjectUtils.toString(tuple.getString("Age"), "unknown"));
				patientSummaryVO.setPatientDeceased(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Deceased")), "unknown"));
				patientSummaryVO.setPatientConsent(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Signed consent")), "no"));

				patientSummaryVO.setVariantSummaryVOList(new ArrayList<MutationSummaryVO>());

				if (StringUtils.isNotEmpty(tuple.getString("cDNA change_1")))
				{
					MutationUploadVO mutationUploadVO = new MutationUploadVO();
					mutationUploadVO.setGeneSymbol("COL7A1");
					mutationUploadVO.setMutation(new Mutation());
					mutationUploadVO.getMutation().setCdna_Notation("c." + tuple.getString("cDNA change_1"));
					mutationService.assignValuesFromNotation(mutationUploadVO);
					if (StringUtils.isNotEmpty(tuple.getString("Protein change_1")))
						mutationUploadVO.getMutation().setAa_Notation("p." + tuple.getString("Protein change_1"));
					if (StringUtils.isNotEmpty(tuple.getString("Consequence_1")))
						mutationUploadVO.getMutation().setConsequence(ObjectUtils.toString(tuple.getString("Consequence_1"), ""));
					mutationUploadVO.getMutation().setInheritance(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Inheritance_1")), ""));
	
					mutationIdentifier = mutationIdentifier + 1;
					mutationUploadVO.getMutation().setIdentifier("M" + mutationIdentifier);
					mutationUploadVO.getMutation().setName("M" + mutationIdentifier);

					// Insert mutation if it does not exist already

					MutationSearchCriteriaVO criteria   = new MutationSearchCriteriaVO();
					criteria.setVariation(mutationUploadVO.getMutation().getCdna_Notation());
					List<MutationSummaryVO> results     = mutationService.findMutations(criteria);

					if (results.size() != 1)
					{
						uploadService.insert(mutationUploadVO);
						System.out.println(">>>Inserted mutation: " + mutationUploadVO.getMutation().toString());
					}
					MutationSummaryVO mutationSummaryVO = new MutationSummaryVO();
					mutationSummaryVO.setCdnaNotation(mutationUploadVO.getMutation().getCdna_Notation());

					patientSummaryVO.getVariantSummaryVOList().add(mutationSummaryVO);
				}

				// Second mutation can be 'NA' or 'unknown':
				// Leave xref == null and add remark in mutation2remark
				if (StringUtils.isNotEmpty(tuple.getString("cDNA change_2")))
				{
					if (tuple.getString("cDNA change_2").equalsIgnoreCase("na"))
						patientSummaryVO.setVariantComment(tuple.getString("cDNA change_2").toUpperCase());
					else if (tuple.getString("cDNA change_2").equalsIgnoreCase("unknown"))
						patientSummaryVO.setVariantComment(tuple.getString("cDNA change_2").toUpperCase());
					else
					{
						MutationUploadVO mutationUploadVO = new MutationUploadVO();
						mutationUploadVO.setGeneSymbol("COL7A1");
						mutationUploadVO.setMutation(new Mutation());
						mutationUploadVO.getMutation().setCdna_Notation("c." + tuple.getString("cDNA change_2"));
						mutationService.assignValuesFromNotation(mutationUploadVO);
						if (StringUtils.isNotEmpty(tuple.getString("Protein change_2")))
							mutationUploadVO.getMutation().setAa_Notation("p." + tuple.getString("Protein change_2"));
						if (StringUtils.isNotEmpty(tuple.getString("Consequence_2")))
							mutationUploadVO.getMutation().setConsequence(ObjectUtils.toString(tuple.getString("Consequence_2"), ""));
						mutationUploadVO.getMutation().setInheritance(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Inheritance_2")), ""));
		
						mutationIdentifier = mutationIdentifier + 1;
						mutationUploadVO.getMutation().setIdentifier("M" + mutationIdentifier);
						mutationUploadVO.getMutation().setName("M" + mutationIdentifier);

						// Insert mutation if it does not exist already

						MutationSearchCriteriaVO criteria   = new MutationSearchCriteriaVO();
						criteria.setVariation(mutationUploadVO.getMutation().getCdna_Notation());
						List<MutationSummaryVO> results     = mutationService.findMutations(criteria);

						if (results.size() != 1)
						{
							uploadService.insert(mutationUploadVO);
							System.out.println(">>>Inserted mutation: " + mutationUploadVO.getMutation().toString());
						}
						MutationSummaryVO mutationSummaryVO = new MutationSummaryVO();
						mutationSummaryVO.setCdnaNotation(mutationUploadVO.getMutation().getCdna_Notation());

						patientSummaryVO.getVariantSummaryVOList().add(mutationSummaryVO);
					}
				}

				patientSummaryVO.setPhenotypeMajor(StringUtils.upperCase(tuple.getString("Phenotype major type")));
				patientSummaryVO.setPhenotypeSub(StringUtils.lowerCase(tuple.getString("Phenotype Subtype")));

//				PhenotypeDetailsVO phenotypeDetailsVO = new PhenotypeDetailsVO();

//				patientSummaryVO.getPhenotypeDetails().setLocation(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Location")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setBlistering(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Blistering")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setHands(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Hands")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setFeet(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Feet")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setArms(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Arms")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setLegs(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Legs")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setProximal_Body_Flexures(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Proximal body flexures")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setTrunk(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Trunk")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setMucous_Membranes(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Mucosa")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setSkin_Atrophy(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Skin atrophy")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setMilia(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Milia")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setNail_Dystrophy(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Nail dystrophy")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setAlbopapuloid_Papules(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Albopapuloid papules")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setPruritic_Papules(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Pruritic papules")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setAlopecia(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Alopecia")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setSquamous_Cell_Carcinomas(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Squamous cell carcinoma(s)")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setRevertant_Skin_Patch(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Revertant skin patch(es)")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setMechanism(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Mechanism")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setFlexion_Contractures(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Flexion contractures")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setPseudosyndactyly_Hands(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Pseudosyndactyly (hands)")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setMicrostomia(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Microstomia")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setAnkyloglossia(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Ankyloglossia")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setDysphagia(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Swallowing difficulties/ dysphagia/ oesophagus strictures")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setGrowth_Retardation(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Growth retardation")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setAnemia(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Anaemia")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setRenal_Failure(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Renal failure")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setDilated_Cardiomyopathy(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Dilated cardiomyopathy")), "unknown"));
//
//				patientSummaryVO.getPhenotypeDetails().setColoboma(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Coloboma")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setCongenital_Heart_Defect(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Congenital heart defect")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setClp(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("C(L)P")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setChoanal_Anomaly(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Choanal anomaly")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setMental_Retardation(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Mental retardation")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setGrowth_Retardation(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Growth retardation")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setGenital_Hypoplasia(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Genital hypoplasia")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setExternal_Ear_Anomaly(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("External ear anomaly")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setSemicircular_Canal_Anomaly(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Semicircular canal anomaly")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setHearing_Loss(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("Hearing loss")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setTe_Anomaly(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("TE anomaly")), "unknown"));
//				patientSummaryVO.getPhenotypeDetails().setCn_Dysfunction(ObjectUtils.toString(StringUtils.lowerCase(tuple.getString("CN dysfunction")), "unknown"));

//				for (String field : patientSummaryVO.getPhenotypeDetails().getFields(true))
//				{
//					if ("".equals(patientSummaryVO.getPhenotypeDetails().get(field)))
//						patientSummaryVO.getPhenotypeDetails().set(field, "unknown");
//				}
//				patientSummaryVO.getPhenotypeDetails().set(tuple.getString(""));

				if (tuple.getString("PubMed ID") != null && tuple.getString("Reference") != null)
				{
					List<PublicationVO> publicationVOs = new ArrayList<PublicationVO>();
					String[] pubmeds                   = tuple.getString("PubMed ID").split(";");
					String[] titles                    = tuple.getString("Reference").split(";");
					for (int i = 0; i < pubmeds.length; i++)
					{
						PublicationVO publicationVO = new PublicationVO();
						publicationVO.setPubmedId(pubmeds[i]);
						publicationVO.setName(titles[i]);
						publicationVO.setTitle(titles[i]);
						publicationVOs.add(publicationVO);
					}
					patientSummaryVO.setPublicationVOList(publicationVOs);
					patientSummaryVO.setPatientConsent("publication");
				}

				patientIdentifier = patientIdentifier + 1;
				patientSummaryVO.setPatientIdentifier("P" + patientIdentifier);
				patientSummaryVO.setPatientName("P" + patientIdentifier);

				patientSummaryVO.setObservedValueVOList(new ArrayList<ObservedValueVO>());

				ObservedValueVO observedValueVO;
				
				observedValueVO = new ObservedValueVO();
				observedValueVO.setFeatureName("LH7:2 Amount of type VII collagen");
				observedValueVO.setTargetName(patientSummaryVO.getPatientIdentifier());
				observedValueVO.setValue(ObjectUtils.toString(tuple.getString("IF LH7:2"), "unknown"));
				patientSummaryVO.getObservedValueVOList().add(observedValueVO);

				observedValueVO = new ObservedValueVO();
				observedValueVO.setFeatureName("IF Retention of type VII Collagen in basal cells");
				observedValueVO.setTargetName(patientSummaryVO.getPatientIdentifier());
				observedValueVO.setValue(ObjectUtils.toString(tuple.getString("IF Retention COLVII"), "unknown"));
				patientSummaryVO.getObservedValueVOList().add(observedValueVO);

				observedValueVO = new ObservedValueVO();
				observedValueVO.setFeatureName("Anchoring fibrils Number");
				observedValueVO.setTargetName(patientSummaryVO.getPatientIdentifier());
				observedValueVO.setValue(ObjectUtils.toString(tuple.getString("EM AF_no"), "unknown"));
				patientSummaryVO.getObservedValueVOList().add(observedValueVO);
				
				observedValueVO = new ObservedValueVO();
				observedValueVO.setFeatureName("Anchoring fibrils Ultrastructure");
				observedValueVO.setTargetName(patientSummaryVO.getPatientIdentifier());
				observedValueVO.setValue(ObjectUtils.toString(tuple.getString("EM AF_structure"), "unknown"));
				patientSummaryVO.getObservedValueVOList().add(observedValueVO);
				
				observedValueVO = new ObservedValueVO();
				observedValueVO.setFeatureName("EM Retention of type VII Collagen in basal cells");
				observedValueVO.setTargetName(patientSummaryVO.getPatientIdentifier());
				observedValueVO.setValue(ObjectUtils.toString(tuple.getString("EM_Retention COLVII"), "unknown"));
				patientSummaryVO.getObservedValueVOList().add(observedValueVO);

				for (int i = 34; ; i++)
				{
					System.out.println(">>>i==" + i);
					String colName = tuple.getColName(i);
					System.out.println(">>>colName==" + colName);
					
					if (colName == null)
						break;
					
					observedValueVO = new ObservedValueVO();
					observedValueVO.setFeatureName(colName);
					observedValueVO.setTargetName(patientSummaryVO.getPatientIdentifier());
					observedValueVO.setValue(ObjectUtils.toString(tuple.getString(colName), "unknown"));
					patientSummaryVO.getObservedValueVOList().add(observedValueVO);
				}

				uploadService.insert(patientSummaryVO);

				total.set(total.get() + 1);
//				patientList.add(patientSummaryVO);		
//				
//				//add to db when batch size is reached
//				if(patientList.size() == BATCH_SIZE)
//				{
//					//resolve foreign keys and copy those entities that could not be resolved to the missingRefs list
//					uploadBatchsMissingRefs.addAll(resolveForeignKeys(db, uploadBatchList));
//					
//					//update objects in the database using xref_label defined secondary key(s) 'id' defined in xref_label
//					db.update(uploadBatchList,dbAction, "id");
//					
//					//clear for next batch						
//					uploadBatchList.clear();		
//					
//					//keep count
//					total.set(total.get() + BATCH_SIZE);				
//				}
			}

		//add remaining elements to the database
//		if(!uploadBatchList.isEmpty())
//		{
//			//resolve foreign keys, again keeping track of those entities that could not be solved
//			uploadBatchsMissingRefs.addAll(resolveForeignKeys(db, uploadBatchList));
//			//update objects in the database using xref_label defined secondary key(s) 'id' defined in xref_label
//			db.update(uploadBatchList,dbAction, "id");
//		}
//		
//		//second import round, try to resolve FK's for entities again as they might have referred to entities in the imported list
//		List<UploadBatch> uploadBatchsStillMissingRefs = resolveForeignKeys(db, uploadBatchsMissingRefs);
//		
//		//if there are still missing references, throw error and rollback
//		if(uploadBatchsStillMissingRefs.size() > 0){
//			throw new Exception("Import of 'UploadBatch' objects failed: attempting to resolve in-list references, but there are still UploadBatchs referring to UploadBatchs that are neither in the database nor in the list of to-be imported UploadBatchs. (the first one being: "+uploadBatchsStillMissingRefs.get(0)+")");
//		}
//		//else update the entities in the database with the found references and return total
//		else
//		{				
//			db.update(uploadBatchsMissingRefs,DatabaseAction.UPDATE, "id");
//		
//			//output count
//			total.set(total.get() + uploadBatchList.size());
//			logger.info("imported "+total.get()+" uploadBatch from CSV"); 
//		
//			return total.get();
//		}
		
		return total.get();
	}
}

