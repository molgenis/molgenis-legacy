
/* File:        col7a1/model/UploadBatch.java
 * Copyright:   GBIC 2000-2010, all rights reserved
 * Date:        August 11, 2010
 * 
 * generator:   org.molgenis.generators.csv.CsvReaderGen 3.3.2-testing
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */

package org.molgenis.mutation.csv;

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
import org.molgenis.util.CsvReader;
import org.molgenis.util.CsvReaderListener;
import org.molgenis.util.Entity;
import org.molgenis.util.Tuple;

import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.service.MutationService;
import org.molgenis.mutation.service.PatientService;
import org.molgenis.mutation.vo.MutationSearchCriteriaVO;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.MutationUploadVO;
import org.molgenis.mutation.vo.ObservedValueVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.core.vo.PublicationVO;
//import col7a1.UploadBatch;
import org.molgenis.submission.Submission;

/**
 * Reads UploadBatch from a delimited (csv) file, resolving xrefs to ids where needed, that is the tricky bit ;-)
 */
public class Chd7UploadBatchCsvReader extends CsvToDatabase<Entity>
{
	public static final transient Logger logger = Logger.getLogger(Chd7UploadBatchCsvReader.class);
	
			
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
	
		final MutationService mutationService   = MutationService.getInstance(db);
		final PatientService patientService     = PatientService.getInstance(db);

		final Submission submission             = new Submission();
		DateFormat dateFormat                   = new SimpleDateFormat("yyyy-MM-dd");
		submission.setDate(dateFormat.format(new Date()));
		submission.setReleasedate(dateFormat.format(new Date()));
		submission.setIdentifier("S" + new Date());
		List<Integer> submitters                = new ArrayList<Integer>();
		submitters.add(db.getSecurity().getUserId());
		submission.setSubmitters_Id(submitters);
		db.add(submission);

		//cache for objects to be imported from file (in batch)
		//TODO: Danny: Use or loose
		/*final List<PatientSummaryVO> patientList = */new ArrayList<PatientSummaryVO>(BATCH_SIZE);
		//wrapper to count
		final IntegerWrapper total = new IntegerWrapper(0);
		reader.setMissingValues(missingValues);
		reader.parse(new CsvReaderListener()
		{
			Integer mutationIdentifier = mutationService.getMaxIdentifier();
			Integer patientIdentifier  = patientService.getMaxIdentifier();

			public void handleLine(int lineNo, Tuple tuple) throws Exception
			{
				for (String fieldName : tuple.getFields())
					System.out.println(">>> fieldName==" +fieldName);
				//parse object, setting defaults and values from file
//				if (lineNo > 5) return;
				PatientSummaryVO patientSummaryVO = new PatientSummaryVO();

				patientSummaryVO.setSubmissionDate(submission.getDate());

				patientSummaryVO.setPatientNumber(tuple.getString("ID CHARGE database"));
				patientSummaryVO.setPatientGender("unknown");
//				patientSummaryVO.setPatientEthnicity(tuple.getString("Ethnicity"));
				patientSummaryVO.setPatientAge("unknown");
				patientSummaryVO.setPatientDeceased("unknown");

				patientSummaryVO.setVariantSummaryVOList(new ArrayList<MutationSummaryVO>());

				if (StringUtils.isNotEmpty(tuple.getString("CHD7 c")))
				{
					MutationUploadVO mutationUploadVO = new MutationUploadVO();
					mutationUploadVO.setMutation(new Mutation());
					mutationUploadVO.getMutation().setCdna_Notation("c." + tuple.getString("CHD7 c"));
					mutationService.assignValuesFromNotation(mutationUploadVO);
					if (StringUtils.isNotEmpty(tuple.getString("CHD7 p")))
						mutationUploadVO.getMutation().setAa_Notation("p." + tuple.getString("CHD7 p"));
//					if (StringUtils.isNotEmpty(tuple.getString("Consequence_1")))
//						mutationUploadVO.getMutation().setConsequence(ObjectUtils.toString(tuple.getString("Consequence_1"), ""));
					mutationUploadVO.getMutation().setConsequence("Unknown");
					mutationUploadVO.getMutation().setInheritance("dominant");
	
					mutationIdentifier = mutationIdentifier + 1;
					mutationUploadVO.getMutation().setIdentifier("M" + mutationIdentifier);
					mutationUploadVO.getMutation().setName("M" + mutationIdentifier);
					mutationUploadVO.getMutation().setPathogenicity(StringUtils.lowerCase(tuple.getString("Pathogenicity")));
					mutationUploadVO.getMutation().setType(tuple.getString("Mutation type"));

					// Insert mutation if it does not exist already

					MutationSearchCriteriaVO criteria   = new MutationSearchCriteriaVO();
					criteria.setVariation(mutationUploadVO.getMutation().getCdna_Notation());
					List<MutationSummaryVO> results     = mutationService.findMutations(criteria);

					if (results.size() != 1)
					{
						mutationService.insert(mutationUploadVO);
						System.out.println(">>>Inserted mutation: " + mutationUploadVO.getMutation().toString());
					}
					MutationSummaryVO mutationSummaryVO = new MutationSummaryVO();
					mutationSummaryVO.setCdnaNotation(mutationUploadVO.getMutation().getCdna_Notation());

					patientSummaryVO.getVariantSummaryVOList().add(mutationSummaryVO);
				}

				// Second mutation is always null
				// Leave xref == null and add remark in mutation2remark
				patientSummaryVO.setVariantComment("NA");

				patientSummaryVO.setPhenotypeMajor(tuple.getString("Phenotype"));
				patientSummaryVO.setPhenotypeSub("");

				patientSummaryVO.setPatientConsent("no");

				if (tuple.getString("PubMed ID") != null || tuple.getString("Reference") != null)
				{
					List<PublicationVO> publicationVOs = new ArrayList<PublicationVO>();
//					String[] pubmeds                   = tuple.getString("PubMed ID").split(";");
					String[] titles                    = tuple.getString("Reference").split(";");
					for (int i = 0; i < titles.length; i++)
					{
						PublicationVO publicationVO = new PublicationVO();
//						publicationVO.setPubmed("");
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
				
				for (int i = 18; ; i++)
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

				patientService.insert(patientSummaryVO);

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
		});
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

