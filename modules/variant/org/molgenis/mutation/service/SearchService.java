package org.molgenis.mutation.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;

import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.MutationSearchCriteriaDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.Patient;
import org.molgenis.variant.SequenceCharacteristic;
import org.molgenis.variant.SequenceRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService extends MolgenisVariantService
{
	@Autowired
	public SearchService(Database db)
	{
		super(db);
	}


	/**
	 * Find exon by its primary key
	 * @param id
	 * @return ExonDTO
	 */
	public ExonDTO findExonById(final Integer id)
	{
		try
		{
			SequenceCharacteristic exon = this.em.find(SequenceCharacteristic.class, id);
			return this.sequenceCharacteristicToExonDTO(exon);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Get all mutations sorted by their position
	 * @return list of VariantDTO's
	 */
	public List<VariantDTO> getAllVariants()
	{
		try
		{
			String sql = "SELECT f FROM SequenceRelation r JOIN r.sequenceFeature f JOIN f.featureType ft JOIN r.sequenceTarget t JOIN t.featureType tt WHERE ft.name = 'cdna-variant' AND (tt.name = 'exon' OR tt.name = 'intron') ORDER BY r.fmin";
			TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
			List<SequenceCharacteristic> variantList = query.getResultList();

			return this.sequenceCharacteristicListToVariantDTOList(variantList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Get all exons sorted by their position
	 * @return list of ExonDTOs
	 */
	public List<ExonDTO> getAllExons()
	{
		try
		{
			String sql = "SELECT s FROM SequenceCharacteristic s JOIN s.featureType t WHERE t.name = 'exon' OR t.name = 'intron'";
			TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
			List<SequenceCharacteristic> exonList = query.getResultList();

			List<ExonDTO> exonDTOList = this.sequenceCharacteristicListToExonDTOList(exonList);
			Collections.sort(exonDTOList);

			return exonDTOList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Get all mutations sorted by their position
	 * @return list of MutationSummaryVOs
	 */
	public List<MutationSummaryDTO> findAllMutationSummaries()
	{
		try
		{
			List<MutationSummaryDTO> mutationSummaryDTOList = this.sequenceCharacteristicListToMutationSummaryDTOList(this.db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("cdna-variant")).find());
			Collections.sort(mutationSummaryDTOList);
			return mutationSummaryDTOList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public List<PatientSummaryDTO> findAllPatientSummaries()
	{
		try
		{
			List<Patient> patients = this.db.query(Patient.class).find();
			return this.patientListToPatientSummaryDTOList(patients);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Get all protein domains
	 * @param orientation 
	 * @return protein domains
	 * @throws ParseException 
	 * @throws DatabaseException 
	 */
	public List<ProteinDomainDTO> findAllProteinDomains()
	{
		try
		{
			return this.sequenceCharacteristicListToProteinDomainDTOList(this.db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("protein_domain")).sortASC(SequenceCharacteristic.ID).find());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find the first exon in order
	 * @return ExonDTO
	 */
	public ExonDTO findFirstExon()
	{
		List<ExonDTO> exonDTOList = this.getAllExons();
		return exonDTOList.get(0);
	}

	/**
	 * Find the first mutation in order
	 * @return MutationSummaryDTO
	 */
	public MutationSummaryDTO findFirstMutation()
	{
		List<MutationSummaryDTO> mutationSummaryVOList = this.findAllMutationSummaries();
		return mutationSummaryVOList.get(0);
	}

	/**
	 * Find previous exon in order
	 * @param id of exon for which previous exon is requested
	 * @return ExonDTO
	 */
	public ExonDTO findPrevExon(final Integer id)
	{
		if (id == null)
			return null;

		try
		{
			List<ExonDTO> exonDTOList = this.getAllExons();
			
			for (int i = 0; i < exonDTOList.size(); i++)
			{
				ExonDTO exonDTO = exonDTOList.get(i);
				
				if (exonDTO.getId().equals(id))
				{
					if (i == 0)
						return exonDTOList.get(i);
					else
						return exonDTOList.get(i - 1);
				}
			}
	
			// If we are here we did not find anything :-(
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find previous mutation in order
	 * @param id of mutation for which previous mutation is requested
	 * @return MutationSummaryDTO
	 */
	public MutationSummaryDTO findPrevMutation(final String identifier)
	{
		if (StringUtils.isEmpty(identifier))
			return null;

		try
		{
			List<MutationSummaryDTO> mutationSummaryVOList = this.findAllMutationSummaries();
			
			for (int i = 0; i < mutationSummaryVOList.size(); i++)
			{
				MutationSummaryDTO mutationSummaryVO = mutationSummaryVOList.get(i);
				
				if (mutationSummaryVO.getIdentifier().equals(identifier))
				{
					if (i == 0)
						return mutationSummaryVOList.get(i);
					else
						return mutationSummaryVOList.get(i - 1);
				}
			}
	
			// If we are here we did not find anything :-(
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find next exon in order
	 * @param id of exon for which next exon is requested
	 * @return ExonDTO
	 */
	public ExonDTO findNextExon(final Integer id)
	{
		if (id == null)
			return null;

		try
		{
			List<ExonDTO> exonDTOList = this.getAllExons();
			
			for (int i = 0; i < exonDTOList.size(); i++)
			{
				ExonDTO exonDTO = exonDTOList.get(i);
				
				if (exonDTO.getId().equals(id))
				{
					if (i == exonDTOList.size() - 1)
						return exonDTOList.get(i);
					else
						return exonDTOList.get(i + 1);
				}
			}
	
			// If we are here we did not find anything :-(
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find next mutation in order
	 * @param id of mutation for which next mutation is requested
	 * @return MutationSummaryDTO
	 */
	public MutationSummaryDTO findNextMutation(final String identifier)
	{
		if (StringUtils.isEmpty(identifier))
			return null;

		try
		{
			List<MutationSummaryDTO> mutationSummaryVOList = this.findAllMutationSummaries();
			
			for (int i = 0; i < mutationSummaryVOList.size(); i++)
			{
				MutationSummaryDTO mutationSummaryVO = mutationSummaryVOList.get(i);
				
				if (mutationSummaryVO.getIdentifier().equals(identifier))
				{
					if (i == mutationSummaryVOList.size() - 1)
						return mutationSummaryVOList.get(i);
					else
						return mutationSummaryVOList.get(i + 1);
				}
			}
	
			// If we are here we did not find anything :-(
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find the last exon in order
	 * @return ExonDTO
	 */
	public ExonDTO findLastExon()
	{
		List<ExonDTO> exonDTOList = this.getAllExons();
		return exonDTOList.get(exonDTOList.size() - 1);
	}

	/**
	 * Find the last mutation in order
	 * @return MutationSummaryDTO
	 */
	public MutationSummaryDTO findLastMutation()
	{
		List<MutationSummaryDTO> mutationSummaryVOList = this.findAllMutationSummaries();
		return mutationSummaryVOList.get(mutationSummaryVOList.size() - 1);
	}

	/**
	 * Find variants in a specific exon/intron
	 * @param exonId
	 * @return list of VariantSummaryDTO's
	 */
	public List<MutationSummaryDTO> findMutationsByExonId(final Integer exonId)
	{
		try
		{
			String sql = "SELECT f FROM SequenceRelation r JOIN r.relationType rt JOIN r.sequenceFeature f JOIN f.featureType ft JOIN r.sequenceTarget t WHERE rt.name = :relationType AND ft.name = :featureType AND t.id = :targetId";
			TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
			query.setParameter("relationType", "part-of");
			query.setParameter("featureType", "cdna-variant");
			query.setParameter("targetId", exonId);
			List<SequenceCharacteristic> variantList = query.getResultList();

			return this.sequenceCharacteristicListToMutationSummaryDTOList(variantList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}
	
	/**
	 * Find a variant by its identifier
	 * @param mutationIdentifier
	 * @return VariantSummaryDTO
	 */
	public MutationSummaryDTO findMutationByIdentifier(final String mutationIdentifier)
	{
		try
		{
			String sql = "SELECT s FROM SequenceCharacteristic s JOIN s.alternateId a WHERE a.definition = 'molgenis_variant_id' AND a.name = :name";
			TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
			query.setParameter("name", mutationIdentifier);
			List<SequenceCharacteristic> variantList = query.getResultList();

			if (variantList.size() > 1)
				throw new SearchServiceException("Not exactly one variant matching " + mutationIdentifier);

			if (variantList.size() == 1)
				return this.sequenceCharacteristicToMutationSummaryDTO(variantList.get(0));
			else
				return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public List<MutationSummaryDTO> findMutations(final MutationSearchCriteriaDTO criteria)
	{
		try
		{
			Set<MutationSummaryDTO> mutations = new HashSet<MutationSummaryDTO>();

			if (StringUtils.isNotEmpty(criteria.getVariation()))
				mutations.addAll(this.findMutationsByCdnaNotation(criteria.getVariation()));
			if (StringUtils.isNotEmpty(criteria.getConsequence()))
				mutations.addAll(this.findMutationsByObservedValue("consequence", criteria.getConsequence()));
			if (StringUtils.isNotEmpty(criteria.getMid()))
			{
				MutationSummaryDTO tmp = this.findMutationByIdentifier(criteria.getMid());
				if (tmp != null)
					mutations.add(tmp);
			}
			if (criteria.getCdnaPosition() != null)
				mutations.addAll(this.findMutationsByCdnaPosition(criteria.getCdnaPosition()));
			if (criteria.getCodonChangeNumber() != null)
				mutations.addAll(this.findMutationsByCodonChangeNumber(criteria.getCodonChangeNumber()));
			if (criteria.getExonId() != null)
				mutations.addAll(this.findMutationsByExonId(criteria.getExonId()));
			if (StringUtils.isNotEmpty(criteria.getType()))
				mutations.addAll(this.findMutationsByObservedValue("Type of mutation", criteria.getType()));
			if (criteria.getProteinDomainId() != null)
				mutations.addAll(this.findMutationsByDomainId(criteria.getProteinDomainId()));
			if (StringUtils.isNotEmpty(criteria.getPhenotypeName()))
				mutations.addAll(this.findMutationsByObservedValue("Phenotype", criteria.getPhenotypeName()));
			if (StringUtils.isNotEmpty(criteria.getInheritance()))
				mutations.addAll(this.findMutationsByObservedValue("inheritance", criteria.getInheritance()));
//			if (criteria.getReportedAsSNP() != null)
				//TODO: implement

			return Arrays.asList(mutations.toArray(new MutationSummaryDTO[0]));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find patients by related mutation identifiers
	 * @param mutationIdentifier
	 * @return list of PatientSummaryDTO's
	 */
	public List<PatientSummaryDTO> findPatientsByMutationIdentifier(final String mutationIdentifier)
	{
		try
		{
			String sql = "SELECT p FROM Patient p JOIN p.mutations m JOIN m.alternateId a WHERE a.name = :name";
			TypedQuery<Patient> query = this.em.createQuery(sql, Patient.class);
			query.setParameter("name", mutationIdentifier);
			List<Patient> patientList = query.getResultList();

			return this.patientListToPatientSummaryDTOList(patientList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find patient by its identifier
	 * @param patientIdentifier
	 * @return PatientSummaryDTO
	 */
	public PatientSummaryDTO findPatientByPatientIdentifier(final String patientIdentifier)
	{
		try
		{
			String sql = "SELECT p FROM Patient p JOIN p.alternateId a WHERE a.name = :name";
			TypedQuery<Patient> query = this.em.createQuery(sql, Patient.class);
			query.setParameter("name", patientIdentifier);
			List<Patient> patientList = query.getResultList();

			if (patientList.size() > 1)
				throw new SearchServiceException("Not exactly one patient matches " + patientIdentifier);
			
			if (patientList.size() == 1)
				return this.patientToPatientSummaryDTO(patientList.get(0));
			else
				return null;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public HashMap<String, List<MutationSummaryDTO>> findMutationsByTerm(final String term)
	{
		HashMap<String, List<MutationSummaryDTO>> result = new HashMap<String, List<MutationSummaryDTO>>();

		result.put("variation", this.findMutationsByCdnaNotation(term));

		result.put("PID", this.findMutationsByPatientIdentifier(term));

		MutationSummaryDTO tmp = this.findMutationByIdentifier(term);
		if (tmp != null)
		{
			result.put("MID", new ArrayList<MutationSummaryDTO>());
			result.get("MID").add(tmp);
		}
	
		result.put("publication", this.findMutationsByPublication(term));

		result.put("measurement", this.findMutationsByMeasurement(term));
		result.put("observed value", this.findMutationsByObservedValue(term));

		if (NumberUtils.isNumber(term))
		{
			result.put("exon number", this.findMutationsByExonNumber(Integer.parseInt(term)));

			result.put("nucleotide position", this.findMutationsByCdnaPosition(Integer.parseInt(term)));

			result.put("protein position", this.findMutationsByCodonChangeNumber(Integer.parseInt(term)));
		}
		
		return result;
	}

	public List<MutationSummaryDTO> findMutationsByCodonChangeNumber(final int aaPosition)
	{
		String sql = "SELECT DISTINCT s FROM SequenceRelation r JOIN r.sequenceFeature s WHERE s.featureType.name = 'aa-variant' AND r.fmin = :fmin";
		TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
		query.setParameter("fmin", aaPosition);

		return this.sequenceCharacteristicListToMutationSummaryDTOList(query.getResultList());
	}

	public List<MutationSummaryDTO> findMutationsByCdnaPosition(final int cdnaPosition)
	{
		String sql = "SELECT DISTINCT s FROM SequenceRelation r JOIN r.sequenceFeature s WHERE s.featureType.name = 'cdna-variant' AND r.fmin = :fmin";
		TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
		query.setParameter("fmin", cdnaPosition);

		return this.sequenceCharacteristicListToMutationSummaryDTOList(query.getResultList());
	}

	public List<MutationSummaryDTO> findMutationsByExonNumber(final int exonNumber)
	{
		// TODO Auto-generated method stub
		return new ArrayList<MutationSummaryDTO>();
	}

	public List<MutationSummaryDTO> findMutationsByObservedValue(final String value)
	{
		String sql = "SELECT DISTINCT s FROM ObservedValue ov JOIN ov.target s WHERE s.__Type = 'SequenceCharacteristic' AND ov.value = :value";
		Query query = this.em.createQuery(sql);
		query.setParameter("value", value);

		return this.sequenceCharacteristicListToMutationSummaryDTOList(query.getResultList());
	}

	public List<MutationSummaryDTO> findMutationsByObservedValue(final String featureName, final String value)
	{
		String sql = "SELECT DISTINCT s FROM ObservedValue ov JOIN ov.feature f JOIN ov.target s WHERE s.__Type = 'SequenceCharacteristic' AND f.name = :featureName AND ov.value = :value";
		Query query = this.em.createQuery(sql);
		query.setParameter("featureName", featureName);
		query.setParameter("value", value);

		return this.sequenceCharacteristicListToMutationSummaryDTOList(query.getResultList());
	}

	public List<MutationSummaryDTO> findMutationsByMeasurement(final String feature)
	{
		String sql = "SELECT DISTINCT s FROM ObservedValue ov JOIN ov.feature f JOIN ov.target s WHERE s.__Type = 'SequenceCharacteristic' AND f.name = :name AND ov.value IN ('yes', 'true')";
		Query query = this.em.createQuery(sql);
		query.setParameter("name", feature);

		return this.sequenceCharacteristicListToMutationSummaryDTOList(query.getResultList());
	}

	/**
	 * Find mutations given a publication title
	 * @param title term
	 * @return list of variants
	 */
	public List<MutationSummaryDTO> findMutationsByPublication(final String term)
	{
		try
		{
			String sql = "SELECT s FROM Patient p JOIN p.mutations s JOIN p.patientreferences r WHERE r.title LIKE :term";
			TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
			query.setParameter("term", "%" + term + "%");
			List<SequenceCharacteristic> variantList = query.getResultList();

			return this.sequenceCharacteristicListToMutationSummaryDTOList(variantList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public List<MutationSummaryDTO> findMutationsByPatientIdentifier(final String term)
	{
		String sql = "SELECT DISTINCT s FROM Patient p JOIN p.mutations s JOIN p.alternateId a WHERE a.name = :name";
		TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
		query.setParameter("name", term);

		return this.sequenceCharacteristicListToMutationSummaryDTOList(query.getResultList());
	}

	public List<MutationSummaryDTO> findMutationsByCdnaNotation(final String cdnaNotation)
	{
		String sql = "SELECT s FROM SequenceCharacteristic s JOIN s.featureType t WHERE t.name = 'cdna-variant' AND s.name = :name";
		TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
		query.setParameter("name", cdnaNotation);

		return this.sequenceCharacteristicListToMutationSummaryDTOList(query.getResultList());
	}

	public HashMap<String, List<PatientSummaryDTO>> findPatientsByTerm(final String term)
	{
		HashMap<String, List<PatientSummaryDTO>> result = new HashMap<String, List<PatientSummaryDTO>>();

		result.put("variation", this.findPatientsByCdnaNotation(term));

		result.put("MID", this.findPatientsByMutationIdentifier(term));

		PatientSummaryDTO tmp = this.findPatientByPatientIdentifier(term);
		if (tmp != null)
		{
			result.put("PID", new ArrayList<PatientSummaryDTO>());
			result.get("PID").add(tmp);
		}
	
		result.put("publication", this.findPatientsByPublication(term));

		result.put("measurement", this.findPatientsByMeasurement(term));
		result.put("observed value", this.findPatientsByObservedValue(term));

		if (NumberUtils.isNumber(term))
		{
			result.put("exon number", this.findPatientsByExonNumber(Integer.parseInt(term)));

			result.put("nucleotide position", this.findPatientsByCdnaPosition(Integer.parseInt(term)));

			result.put("protein position", this.findPatientsByCodonChangeNumber(Integer.parseInt(term)));
		}
		
		return result;
	}

	public List<PatientSummaryDTO> findPatientsByCodonChangeNumber(final int parseInt)
	{
		// TODO Auto-generated method stub
		return new ArrayList<PatientSummaryDTO>();
	}


	public List<PatientSummaryDTO> findPatientsByCdnaPosition(final int parseInt)
	{
		// TODO Auto-generated method stub
		return new ArrayList<PatientSummaryDTO>();
	}


	public List<PatientSummaryDTO> findPatientsByExonNumber(final int exonNumber)
	{
		// TODO Auto-generated method stub
		return new ArrayList<PatientSummaryDTO>();
	}


	public List<PatientSummaryDTO> findPatientsByObservedValue(final String value)
	{
		String sql = "SELECT DISTINCT p FROM ObservedValue ov JOIN ov.target p WHERE p.__Type = 'Patient' AND ov.value = :value";
		Query query = this.em.createQuery(sql);
		query.setParameter("value", value);

		return this.patientListToPatientSummaryDTOList(query.getResultList());
	}


	public List<PatientSummaryDTO> findPatientsByMeasurement(final String featureName)
	{
		String sql = "SELECT DISTINCT p FROM ObservedValue ov JOIN ov.feature f JOIN ov.target p WHERE p.__Type = 'Patient' AND f.name = :name AND ov.value IN ('yes', 'true')";
		Query query = this.em.createQuery(sql);
		query.setParameter("name", featureName);

		return this.patientListToPatientSummaryDTOList(query.getResultList());
	}


	public List<PatientSummaryDTO> findPatientsByPublication(final String term)
	{
		try
		{
			String sql = "SELECT p FROM Patient p JOIN p.patientreferences r WHERE r.title LIKE :term";
			TypedQuery<Patient> query = this.em.createQuery(sql, Patient.class);
			query.setParameter("term", "%" + term + "%");
			List<Patient> patientList = query.getResultList();

			return this.patientListToPatientSummaryDTOList(patientList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}


	public List<PatientSummaryDTO> findPatientsByCdnaNotation(final String cdnaNotation)
	{
		String sql = "SELECT DISTINCT p FROM Patient p JOIN p.mutations m WHERE m.name = :cdnaNotation";
		TypedQuery<Patient> query = this.em.createQuery(sql, Patient.class);
		query.setParameter("cdnaNotation", cdnaNotation);

		return this.patientListToPatientSummaryDTOList(query.getResultList());
	}

	/**
	 * Find mutations on the same position as the given one
	 * @param mutationSummaryVO
	 * @return list of VariantDTO
	 * @throws DatabaseException
	 */
	public List<VariantDTO> findPositionMutations(final MutationSummaryDTO mutationSummaryVO)
	{
		try
		{
			List<SequenceRelation> sequenceRelationList = this.db.query(SequenceRelation.class).equals(SequenceRelation.FMIN, mutationSummaryVO.getCdnaStart()).find();
			
			List<VariantDTO> result = new ArrayList<VariantDTO>();
			
			for (SequenceRelation relation : sequenceRelationList)
			{
				SequenceCharacteristic sequenceCharacteristic = relation.getSequenceFeature();
	
				if ("cdna-variant".equals(sequenceCharacteristic.getFeatureType().getName()) && !mutationSummaryVO.getId().equals(sequenceCharacteristic.getId()))
					result.add(this.sequenceCharacteristicToVariantDTO(sequenceCharacteristic));
			}
			
			return result;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Find mutations in the same codon as the given one
	 * @param mutationSummaryVO
	 * @return list of VariantDTO
	 */
	public List<VariantDTO> findCodonMutations(final MutationSummaryDTO mutationSummaryVO)
	{
//		List<MutationSummaryVO> result = new ArrayList<MutationSummaryVO>();
//		if (mutationSummaryVO.getAaPosition() != null)
//		{
//			List<Mutation> codonMutations = this.db.query(Mutation.class).equals(Mutation.AA_POSITION, mutationSummaryVO.getAaPosition()).find();
//			for (Mutation codonMutation : codonMutations)
//			{
//				if (!codonMutation.getId().equals(mutationSummaryVO.getId()))
//				{
//					MutationSummaryVO tmp = new MutationSummaryVO();
//					tmp.setIdentifier(codonMutation.getIdentifier());
//					tmp.setCdnaNotation(codonMutation.getCdna_Notation());
//					result.add(tmp);
//				}
//			}
//		}
//		return result;
		return new ArrayList<VariantDTO>();
	}
	
	/**
	 * Find a protein domain by its id
	 * @param id
	 * @param noIntrons
	 * @return protein domain
	 */
	public ProteinDomainDTO findProteinDomain(final Integer id, final Boolean noIntrons)
	{
		try
		{
			return this.sequenceCharacteristicToProteinDomainDTO(this.em.find(SequenceCharacteristic.class, id), noIntrons);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}


	public List<PatientSummaryDTO> findPatientsByUserId(Integer userId)
	{
		String sql = "SELECT DISTINCT p FROM Patient p JOIN p.submission s JOIN s.submitters ps WHERE ps.id = :userId";
		TypedQuery<Patient> query = this.em.createQuery(sql, Patient.class);
		query.setParameter("userId", userId);

		return this.patientListToPatientSummaryDTOList(query.getResultList());
	}


	public List<MutationSummaryDTO> findMutationsByDomainId(Integer domainId)
	{
		// TODO Auto-generated method stub
		return new ArrayList<MutationSummaryDTO>();
	}
}
