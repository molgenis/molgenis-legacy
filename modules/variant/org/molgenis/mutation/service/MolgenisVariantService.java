package org.molgenis.mutation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.molgenis.auth.MolgenisUser;
import org.molgenis.core.OntologyTerm;
import org.molgenis.core.dto.PublicationDTO;
import org.molgenis.core.service.PublicationService;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.mutation.ServiceLocator;
import org.molgenis.mutation.dto.ExonSearchCriteriaDTO;
import org.molgenis.mutation.dto.ExonDTO;
import org.molgenis.mutation.dto.GeneDTO;
import org.molgenis.mutation.dto.MutationSummaryDTO;
import org.molgenis.mutation.dto.PatientSummaryDTO;
import org.molgenis.mutation.dto.ProteinDomainDTO;
import org.molgenis.mutation.dto.VariantDTO;
import org.molgenis.mutation.util.SequenceUtils;
import org.molgenis.pheno.AlternateId;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.pheno.dto.ProtocolDTO;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.protocol.Protocol;
import org.molgenis.submission.Submission;
import org.molgenis.variant.Exon;
import org.molgenis.variant.Gene;
import org.molgenis.variant.Patient;
import org.molgenis.variant.Protein;
import org.molgenis.variant.ProteinDomain;
import org.molgenis.variant.SequenceCharacteristic;
import org.molgenis.variant.SequenceRelation;
import org.molgenis.variant.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MolgenisVariantService
{
	protected Database db;
	protected EntityManager em;
	protected Map<String, OntologyTerm> ontologyTermCache;

	@Autowired
	public MolgenisVariantService(final Database db)
	{
		this.db = db;
		this.em = db.getEntityManager();
		this.init();
	}

	private void init()
	{
		try
		{
			List<OntologyTerm> ontologyTermList = this.db.query(OntologyTerm.class).find();
			
			this.ontologyTermCache = new HashMap<String, OntologyTerm>();

			for (OntologyTerm ontologyTerm : ontologyTermList)
			{
				this.ontologyTermCache.put(ontologyTerm.getName(), ontologyTerm);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public Map<String, OntologyTerm> getOntologyTermCache()
	{
		return this.ontologyTermCache;
	}

	public ExonDTO findExonByMutationPosition(final String position)
	{
		try
		{
			Pattern reExon   = Pattern.compile("^(\\d+)$");
			Pattern reIntron = Pattern.compile("^(\\d+)([+-])(\\d+)$");
			Matcher mExon    = reExon.matcher(position);
			Matcher mIntron  = reIntron.matcher(position);
			
			if (mExon.matches())
			{
				return this.findExonByCdnaPosition(Integer.parseInt(mExon.group(1)));
			}
			else if (mIntron.matches())
			{
				// search by gDNA position since intron don't have cDNA position
				int cdnaPosition = Integer.parseInt(mIntron.group(1));
				String operation = mIntron.group(2);
				int cdnaAdder    = Integer.parseInt(mIntron.group(3));
	
				int gdnaPosition;
	
				ExonDTO exonDTO  = this.findExonByCdnaPosition(cdnaPosition);
	
				if ("+".equals(operation))
					gdnaPosition = ("F".equals(exonDTO.getOrientation()) ? exonDTO.getGdnaStart() + cdnaAdder : exonDTO.getGdnaStart() - cdnaAdder);
				else
					gdnaPosition = ("F".equals(exonDTO.getOrientation()) ? exonDTO.getGdnaStart() - cdnaAdder : exonDTO.getGdnaStart() + cdnaAdder);
	
				return this.findExonByGdnaPosition(gdnaPosition);
			}
			else
			{
				throw new SearchServiceException("Invalid position notation: " + position);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public ExonDTO findExonByCdnaPosition(final int position)
	{
		try
		{
			List<Exon> exons = this.db.query(Exon.class).equals(Exon.ISINTRON, false).lessOrEqual(Exon.STARTCDNA, position).greaterOrEqual(Exon.ENDCDNA, position).find();

			if (exons.size() != 1)
				throw new SearchServiceException("Not exactly one exon matching for cDNA position " + position);
			
			return this.exonToExonDTO(exons.get(0));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public ExonDTO findExonByGdnaPosition(final int position)
	{
		try
		{
			String sql = "SELECT e FROM Exon e WHERE ((e.startGdna <= :startPos AND :endPos <= e.endGdna AND e.strand = '+1') OR (e.endGdna <= :endPos AND :startPos <= e.startGdna AND e.strand = '-1'))";
			TypedQuery<Exon> query = this.em.createQuery(sql, Exon.class);
			query.setParameter("startPos", position);
			query.setParameter("endPos", position);
			List<Exon> exonList = query.getResultList();

			if (exonList.size() != 1)
				throw new SearchServiceException("Not exactly one exon matching for gDNA position " + position);
			
			return this.exonToExonDTO(exonList.get(0));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/*
	 * Retrieve the one and only gene from the database
	 * @return data transfer object for the gene
	 */
	public GeneDTO findGene()
	{
		try
		{
			List<Gene> geneList = this.db.query(Gene.class).find();
	
			if (geneList.size() == 0)
				throw new SearchServiceException("No gene found in database.");
			
			Gene gene = geneList.get(0);
	
//			return this.sequenceCharacteristicToGeneDTO(gene);
			return this.geneToGeneDTO(gene);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Convert a SequenceCharacteristic into a GeneDTO
	 * @param SequenceCharacteristic gene
	 * @return GeneDTO
	 */
	public GeneDTO geneToGeneDTO(final Gene gene)
	{
		GeneDTO geneDTO = new GeneDTO();

		geneDTO.setLength(gene.getSeqlen());
		geneDTO.setName(gene.getName());
		geneDTO.setNuclSequence(gene.getResidues());
		geneDTO.setSymbol(gene.getName()); // For simplicity: name == symbol (name is not needed)
		geneDTO.setBpEnd(gene.getEndGdna());
		geneDTO.setBpStart(gene.getStartGdna());
		geneDTO.setOrientation("-1".equals(gene.getStrand()) ? "R" : "F"); //TODO: What about "0"???

		// find the relations to chromosome and protein
		List<SequenceRelation> relationList = Arrays.asList(gene.getSequenceFeatureSequenceRelationCollection().toArray(new SequenceRelation[0]));

		for (SequenceRelation relation : relationList)
		{
			SequenceCharacteristic tmp = relation.getSequenceTarget();

			if ("chromosome".equals(tmp.getFeatureType().getName()))
			{
				geneDTO.setChromosome(tmp.getName());
			}
		}

		Protein protein = gene.getProtein();
		geneDTO.setAaSequence(protein.getResidues());

		List<ObservedValue> observedValueList = Arrays.asList(gene.getFeatureObservedValueCollection().toArray(new ObservedValue[0]));
		
		for (ObservedValue observedValue : observedValueList)
		{
			if ("genbank".equals(observedValue.getFeature().getName()))
				geneDTO.setGenbankId(observedValue.getValue());
			else if ("build".equals(observedValue.getFeature().getName()))
				geneDTO.setGenomeBuild(observedValue.getValue());
		}

		return geneDTO;
	}

	/**
	 * Convert a SequenceCharacteristic into a GeneDTO
	 * @param SequenceCharacteristic gene
	 * @return GeneDTO
	 */
	public GeneDTO sequenceCharacteristicToGeneDTO(final SequenceCharacteristic gene)
	{
		GeneDTO geneDTO = new GeneDTO();

		geneDTO.setLength(gene.getSeqlen());
		geneDTO.setName(gene.getName());
		geneDTO.setNuclSequence(gene.getResidues());
		geneDTO.setSymbol(gene.getName()); // For simplicity: name == symbol (name is not needed)

		// find the relations to chromosome and protein
		List<SequenceRelation> relationList = Arrays.asList(gene.getSequenceFeatureSequenceRelationCollection().toArray(new SequenceRelation[0]));

		for (SequenceRelation relation : relationList)
		{
			SequenceCharacteristic tmp = relation.getSequenceTarget();

			if ("chromosome".equals(tmp.getFeatureType().getName()))
			{
				geneDTO.setBpEnd(relation.getFmax());
				geneDTO.setBpStart(relation.getFmin());
				geneDTO.setChromosome(tmp.getName());
				geneDTO.setOrientation("-1".equals(relation.getStrand()) ? "R" : "F"); //TODO: What about "0"???
			}
			else if ("protein".equals(tmp.getFeatureType().getName()))
			{
				geneDTO.setAaSequence(tmp.getResidues());
			}
		}

		List<ObservedValue> observedValueList = Arrays.asList(gene.getFeatureObservedValueCollection().toArray(new ObservedValue[0]));
		
		for (ObservedValue observedValue : observedValueList)
		{
			if ("genbank".equals(observedValue.getFeature().getName()))
				geneDTO.setGenbankId(observedValue.getValue());
			else if ("build".equals(observedValue.getFeature().getName()))
				geneDTO.setGenomeBuild(observedValue.getValue());
		}

		return geneDTO;
	}

	/**
	 * Convert a SequenceCharacteristic into a ExonDTO
	 * @param SequenceCharacteristic exon
	 * @return ExonDTO
	 * @throws DatabaseException 
	 */
	public ExonDTO sequenceCharacteristicToExonDTO(final SequenceCharacteristic exon) throws DatabaseException
	{
		ExonDTO exonDTO = new ExonDTO();
		exonDTO.setId(exon.getId());
		exonDTO.setIsIntron("intron".equals(exon.getFeatureType().getName()) ? true : false);
		exonDTO.setLength(exon.getSeqlen());
		exonDTO.setName(exon.getName());
		if (exon.getSeqlen() != null && exon.getSeqlen() % 3 == 0)
			exonDTO.setMultiple3Nucl(true);
		else
			exonDTO.setMultiple3Nucl(false);

		// find relation to the sequence of the gene
		List<SequenceRelation> sequenceRelationList = this.db.query(SequenceRelation.class).equals(SequenceRelation.SEQUENCEFEATURE, exon.getId()).find();

		for (SequenceRelation relation : sequenceRelationList)
		{
			if (relation.getRelationType() == null)
				continue;

			SequenceCharacteristic tmp = relation.getSequenceTarget();

			if ("part-of".equals(relation.getRelationType().getName()) && "cdna_sequence".equals(tmp.getFeatureType().getName()))
			{
				exonDTO.setCdnaEnd(relation.getFmax());
				exonDTO.setCdnaStart(relation.getFmin());
			}
			else if ("part-of".equals(relation.getRelationType().getName()) && "chromosome".equals(tmp.getFeatureType().getName()))
			{
				exonDTO.setGdnaEnd(relation.getFmax());
				exonDTO.setGdnaStart(relation.getFmin());
			}
			else if ("part-of".equals(relation.getRelationType().getName()) && "protein_domain".equals(tmp.getFeatureType().getName()))
			{
				exonDTO.getDomainId().add(tmp.getId());
				exonDTO.getDomainName().add(tmp.getName());
			}
		}

		// find protein domains
		
		String sql = "SELECT s FROM SequenceRelation r JOIN r.sequenceFeature s WHERE s.featureType.name = 'protein_domain' AND ((r.fmin <= :pos AND :pos <= r.fmax AND r.strand = '+1') OR (r.fmax <= :pos AND :pos <= r.fmin AND r.strand = '-1'))";
		TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
		query.setParameter("pos", exonDTO.getGdnaStart());
		
		for (SequenceCharacteristic proteinDomain : query.getResultList())
		{
			exonDTO.getDomainId().add(proteinDomain.getId());
			exonDTO.getDomainName().add(proteinDomain.getName());
		}

		GeneDTO geneDTO = this.findGene();

		Integer gdnaStart = Math.abs(exonDTO.getGdnaStart() - geneDTO.getBpStart().intValue());
		Integer gdnaEnd   = Math.abs(exonDTO.getGdnaEnd() - geneDTO.getBpStart().intValue());//gdnaStart + exon.getLength();
		exonDTO.setNuclSequence(StringUtils.substring(geneDTO.getNuclSequence(), gdnaStart, gdnaEnd + 1));
		exonDTO.setNuclSequenceFlankLeft(this.getNuclSequenceFlankLeft(exonDTO, geneDTO));
		exonDTO.setNuclSequenceFlankRight(this.getNuclSequenceFlankRight(exonDTO, geneDTO));
		exonDTO.setOrientation(geneDTO.getOrientation());

		if (!exonDTO.getIsIntron())
		{
			exonDTO.setNuclSequence(exonDTO.getNuclSequence().toUpperCase());
			exonDTO.setNuclSequenceFlankLeft(exonDTO.getNuclSequenceFlankLeft().toLowerCase());
			exonDTO.setNuclSequenceFlankRight(exonDTO.getNuclSequenceFlankRight().toLowerCase());
			exonDTO.setAaSequence(StringUtils.substring(geneDTO.getAaSequence(), exonDTO.getCdnaStart(), exonDTO.getCdnaEnd() + 1));
			exonDTO.setNumFullAminoAcids(SequenceUtils.getNumFullAminoAcids(exonDTO.getAaSequence()));
			exonDTO.setNumPartAminoAcids(SequenceUtils.getNumPartAminoAcids(exonDTO.getAaSequence()));
			exonDTO.setNumGlyXYRepeats(SequenceUtils.getNumGlyXYRepeats(exonDTO.getAaSequence()));
		}

		if (exonDTO.getIsIntron())
		{
			exonDTO.setNuclSequence(exonDTO.getNuclSequence().toLowerCase());
			exonDTO.setNuclSequenceFlankLeft(exonDTO.getNuclSequenceFlankLeft().toUpperCase());
			exonDTO.setNuclSequenceFlankRight(exonDTO.getNuclSequenceFlankRight().toUpperCase());
		}

		return exonDTO;
	}

	/**
	 * Convert a SequenceCharacteristic into a ExonDTO
	 * @param SequenceCharacteristic exon
	 * @return ExonDTO
	 * @throws DatabaseException 
	 */
	public ExonDTO exonToExonDTO(final Exon exon) throws DatabaseException
	{
		ExonDTO exonDTO = new ExonDTO();
		exonDTO.setId(exon.getId());
		exonDTO.setIsIntron("intron".equals(exon.getFeatureType().getName()) ? true : false);
		exonDTO.setLength(exon.getSeqlen());
		exonDTO.setName(exon.getName());
		if (exon.getSeqlen() != null && exon.getSeqlen() % 3 == 0)
			exonDTO.setMultiple3Nucl(true);
		else
			exonDTO.setMultiple3Nucl(false);
		exonDTO.setGdnaStart(exon.getStartGdna());
		exonDTO.setGdnaEnd(exon.getEndGdna());
		if (!exonDTO.getIsIntron())
		{
			exonDTO.setCdnaStart(exon.getStartCdna());
			exonDTO.setCdnaEnd(exon.getEndCdna());
		}

		// find protein domains
		
		String sql = "SELECT p FROM ProteinDomain p WHERE ((p.startGdna <= :startPos AND :endPos <= p.endGdna AND p.strand = '+1') OR (p.endGdna <= :endPos AND :startPos <= p.startGdna AND p.strand = '-1'))";
		TypedQuery<ProteinDomain> query = this.em.createQuery(sql, ProteinDomain.class);
		query.setParameter("startPos", exon.getStartGdna());
		query.setParameter("endPos", exon.getEndGdna());

//		String sql = "SELECT s FROM SequenceRelation r JOIN r.sequenceFeature s WHERE s.featureType.name = 'protein_domain' AND ((r.fmin <= :pos AND :pos <= r.fmax AND r.strand = '+1') OR (r.fmax <= :pos AND :pos <= r.fmin AND r.strand = '-1'))";
//		TypedQuery<SequenceCharacteristic> query = this.em.createQuery(sql, SequenceCharacteristic.class);
//		query.setParameter("pos", exonDTO.getGdnaStart());
		
		for (ProteinDomain proteinDomain : query.getResultList())
		{
			exonDTO.getDomainId().add(proteinDomain.getId());
			exonDTO.getDomainName().add(proteinDomain.getName());
		}

		GeneDTO geneDTO = this.findGene();

		Integer gdnaStart = Math.abs(exonDTO.getGdnaStart() - geneDTO.getBpStart().intValue());
		Integer gdnaEnd   = Math.abs(exonDTO.getGdnaEnd() - geneDTO.getBpStart().intValue());//gdnaStart + exon.getLength();
		exonDTO.setNuclSequence(StringUtils.substring(geneDTO.getNuclSequence(), gdnaStart, gdnaEnd + 1));
		exonDTO.setNuclSequenceFlankLeft(this.getNuclSequenceFlankLeft(exonDTO, geneDTO));
		exonDTO.setNuclSequenceFlankRight(this.getNuclSequenceFlankRight(exonDTO, geneDTO));
		exonDTO.setOrientation("-1".equals(exon.getStrand()) ? "R" : "F"); //TODO: What about "0"???

		if (!exonDTO.getIsIntron())
		{
			exonDTO.setNuclSequence(exonDTO.getNuclSequence().toUpperCase());
			exonDTO.setNuclSequenceFlankLeft(exonDTO.getNuclSequenceFlankLeft().toLowerCase());
			exonDTO.setNuclSequenceFlankRight(exonDTO.getNuclSequenceFlankRight().toLowerCase());
			exonDTO.setAaSequence(StringUtils.substring(geneDTO.getAaSequence(), exonDTO.getCdnaStart(), exonDTO.getCdnaEnd() + 1));
			exonDTO.setNumFullAminoAcids(SequenceUtils.getNumFullAminoAcids(exonDTO.getAaSequence()));
			exonDTO.setNumPartAminoAcids(SequenceUtils.getNumPartAminoAcids(exonDTO.getAaSequence()));
			exonDTO.setNumGlyXYRepeats(SequenceUtils.getNumGlyXYRepeats(exonDTO.getAaSequence()));
		}

		if (exonDTO.getIsIntron())
		{
			exonDTO.setNuclSequence(exonDTO.getNuclSequence().toLowerCase());
			exonDTO.setNuclSequenceFlankLeft(exonDTO.getNuclSequenceFlankLeft().toUpperCase());
			exonDTO.setNuclSequenceFlankRight(exonDTO.getNuclSequenceFlankRight().toUpperCase());
		}

		return exonDTO;
	}

	public List<ExonDTO> exonListToExonDTOList(final List<Exon> exons) throws DatabaseException
	{
		List<ExonDTO> result = new ArrayList<ExonDTO>();

		for (Exon exon : exons)
			result.add(this.exonToExonDTO(exon));
		
		return result;
	}

	public List<ExonDTO> sequenceCharacteristicListToExonDTOList(final List<SequenceCharacteristic> exons) throws DatabaseException
	{
		List<ExonDTO> result = new ArrayList<ExonDTO>();

		for (SequenceCharacteristic exon : exons)
			result.add(this.sequenceCharacteristicToExonDTO(exon));
		
		return result;
	}

	public List<VariantDTO> variantListToVariantDTOList(final List<Variant> variantList)
	{
		List<VariantDTO> result = new ArrayList<VariantDTO>();

		for (Variant variant : variantList)
			result.add(this.variantToVariantDTO(variant));
				
		return result;
	}

	public List<VariantDTO> sequenceCharacteristicListToVariantDTOList(final List<SequenceCharacteristic> variantList)
	{
		List<VariantDTO> result = new ArrayList<VariantDTO>();

		for (SequenceCharacteristic variant : variantList)
			result.add(this.sequenceCharacteristicToVariantDTO(variant));
				
		return result;
	}

	public VariantDTO variantToVariantDTO(final Variant variant)
	{
		try
		{
			VariantDTO variantDTO = new VariantDTO();
			variantDTO.setAaNotation(variant.getNameAa());
			variantDTO.setAaStart(variant.getStartAa());
			variantDTO.setCdnaNotation(variant.getNameCdna());
			variantDTO.setCdnaStart(variant.getStartCdna());
			variantDTO.setGdnaStart(variant.getStartGdna());
			variantDTO.setId(variant.getId());
	
			/* Find the external molgenis variant id */
			for (AlternateId alternateId : variant.getAlternateId())
			{
				if ("molgenis_variant_id".equals(alternateId.getDefinition()))
					variantDTO.setIdentifier(alternateId.getName());
			}
			
			/* Find corresponding exon/intron */
			String sql = "SELECT e FROM Exon e WHERE ((e.startGdna <= :position AND :position <= e.endGdna AND e.strand = '+1') OR (e.endGdna <= :position AND :position <= e.startGdna AND e.strand = '-1'))";
			TypedQuery<Exon> exonQuery = this.em.createQuery(sql, Exon.class);
			exonQuery.setParameter("position", variant.getStartGdna());

			for (Exon exon : exonQuery.getResultList())
			{
				variantDTO.setExonId(exon.getId());
				variantDTO.setExonName(exon.getName());
			}

			/* Find prominent value to be displayed in table view */
			String pathoSql = "SELECT ov FROM ObservedValue ov JOIN ov.feature f WHERE ov.target = :target AND (f.name = 'Pathogenicity' OR f.name = 'Consequence' OR f.name = 'Inheritance')";
			TypedQuery<ObservedValue> pathoQuery = this.em.createQuery(pathoSql, ObservedValue.class);
			pathoQuery.setParameter("target", variant);
			List<ObservedValue> observedValueList = pathoQuery.getResultList();

			for (ObservedValue observedValue : observedValueList)
			{
				if (StringUtils.equals(observedValue.getFeature_Name(), "Pathogenicity"))
				{
					variantDTO.setObservedValue(observedValue.getValue());
				}
				else if (StringUtils.equals(observedValue.getFeature_Name(), "Consequence"))
				{
					variantDTO.setConsequence(observedValue.getValue());
				}
				else if (StringUtils.equals(observedValue.getFeature_Name(), "Inheritance"))
				{
					variantDTO.setInheritance(observedValue.getValue());
				}
			}

			return variantDTO;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}
	
	public VariantDTO sequenceCharacteristicToVariantDTO(final SequenceCharacteristic variant)
	{
		try
		{
			VariantDTO variantDTO = new VariantDTO();
			variantDTO.setCdnaNotation(variant.getName());
			variantDTO.setId(variant.getId());
	
			/* Find the amino acid notation */
			List<SequenceRelation> targetRelations = this.db.query(SequenceRelation.class).equals(SequenceRelation.TARGET, variant.getId()).find();
			
			for (SequenceRelation relation : targetRelations)
			{
				if ("result-of".equals(relation.getRelationType().getName()))
				{
					SequenceCharacteristic feature = relation.getSequenceFeature();
					OntologyTerm featureType       = feature.getFeatureType();
					
					if (featureType == null)
						continue;
	
					if ("gdna-variant".equals(feature.getFeatureType().getName()))
					{
						variantDTO.setGdnaStart(relation.getFmin());
					}
					else if ("aa-variant".equals(feature.getFeatureType().getName()))
					{
						variantDTO.setAaNotation(feature.getName());
						variantDTO.setAaStart(relation.getFmin());
					}
				}
			}
	
			/* Find the external molgenis variant id */
			for (AlternateId alternateId : variant.getAlternateId())
			{
				if ("molgenis_variant_id".equals(alternateId.getDefinition()))
					variantDTO.setIdentifier(alternateId.getName());
			}
			
			/* Find corresponding exon/intron */
			String exonSql = "SELECT DISTINCT r FROM SequenceRelation r JOIN r.sequenceTarget t WHERE t.featureType.name IN ('exon', 'intron') AND r.sequenceFeature = :feature";
			TypedQuery<SequenceRelation> exonQuery = this.em.createQuery(exonSql, SequenceRelation.class);
			exonQuery.setParameter("feature", variant);
			
			for (SequenceRelation relation : exonQuery.getResultList())
			{
				variantDTO.setExonId(relation.getSequenceTarget().getId());
				variantDTO.setExonName(relation.getSequenceTarget().getName());
				variantDTO.setCdnaStart(relation.getFmin());
			}

			/* Find prominent value to be displayed in table view */
			String pathoSql = "SELECT ov FROM ObservedValue ov JOIN ov.feature f WHERE ov.target = :target AND (f.name = 'Pathogenicity' OR f.name = 'consequence')";
			TypedQuery<ObservedValue> pathoQuery = this.em.createQuery(pathoSql, ObservedValue.class);
			pathoQuery.setParameter("target", variant);
			List<ObservedValue> observedValueList = pathoQuery.getResultList();

			/* A proper data model would ensure that exactly one is found.
			 * But you get what you deserve.
			 */
			if (observedValueList.size() == 1)
				variantDTO.setObservedValue(observedValueList.get(0).getValue());

			return variantDTO;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public MutationSummaryDTO sequenceCharacteristicToMutationSummaryDTO(final SequenceCharacteristic variant)
	{
		try
		{
			MutationSummaryDTO mutationSummaryDTO = new MutationSummaryDTO();
			mutationSummaryDTO.setId(variant.getId());
			mutationSummaryDTO.setCdnaNotation(variant.getName());
			mutationSummaryDTO.setGdnaNotation("");
			mutationSummaryDTO.setAaNotation("");
	
			/* Find the external molgenis variant id */
			for (AlternateId alternateId : variant.getAlternateId())
			{
				if ("molgenis_variant_id".equals(alternateId.getDefinition()))
					mutationSummaryDTO.setIdentifier(alternateId.getName());
			}
	
			/* Find all relevant SequenceRelations */
			List<SequenceRelation> featureRelations = this.db.query(SequenceRelation.class).equals(SequenceRelation.FEATURE, variant.getId()).find();
			
			for (SequenceRelation relation : featureRelations)
			{
				if ("part-of".equals(relation.getRelationType().getName()))
				{
					SequenceCharacteristic target = (SequenceCharacteristic) relation.getTarget();
					
					if ("exon".equals(target.getFeatureType().getName()) || "intron".equals(target.getFeatureType().getName()))
					{
						mutationSummaryDTO.setCdnaEnd(relation.getFmax());
						mutationSummaryDTO.setCdnaStart(relation.getFmin());
						mutationSummaryDTO.setExonId(target.getId());
						mutationSummaryDTO.setExonName(target.getName());
					}
				}
			}
	
			List<SequenceRelation> targetRelations = this.db.query(SequenceRelation.class).equals(SequenceRelation.TARGET, variant.getId()).find();
			
			for (SequenceRelation relation : targetRelations)
			{
				if ("result-of".equals(relation.getRelationType().getName()))
				{
					SequenceCharacteristic feature = relation.getSequenceFeature();
					OntologyTerm featureType       = feature.getFeatureType();
					
					if (featureType == null)
						continue;
	
					if ("gdna-variant".equals(feature.getFeatureType().getName()))
					{
						mutationSummaryDTO.setGdnaEnd(relation.getFmax());
						mutationSummaryDTO.setGdnaNotation(feature.getName());
						mutationSummaryDTO.setGdnaStart(relation.getFmin());
					}
					else if ("aa-variant".equals(feature.getFeatureType().getName()))
					{
						mutationSummaryDTO.setAaEnd(relation.getFmax());
						mutationSummaryDTO.setAaNotation(feature.getName());
						mutationSummaryDTO.setAaStart(relation.getFmin());
					}
				}
			}
	
			if (StringUtils.isNotEmpty(mutationSummaryDTO.getAaNotation()))
				mutationSummaryDTO.setNiceNotation(mutationSummaryDTO.getCdnaNotation() + " (" + mutationSummaryDTO.getAaNotation() + ")");
			else
				mutationSummaryDTO.setNiceNotation(mutationSummaryDTO.getCdnaNotation());
			
			/* find ObservedValue's, separate 'Inheritance' and 'Consequence' */
			mutationSummaryDTO.setProtocolDTOList(new ArrayList<ProtocolDTO>());
			mutationSummaryDTO.setObservedValueDTOHash(new HashMap<String, List<ObservedValueDTO>>());
	
			PhenoService phenoService   = ServiceLocator.instance().getPhenoService();
	
			List<Protocol> protocolList = this.em.createQuery("SELECT p FROM Protocol p", Protocol.class).getResultList();
	
			for (Protocol protocol : protocolList)
			{
				String sql = "SELECT ov FROM ObservedValue ov WHERE ov.target = :target AND feature IN (:features)";
				TypedQuery<ObservedValue> query = this.em.createQuery(sql, ObservedValue.class);
				query.setParameter("target", variant);
				query.setParameter("features", protocol.getFeatures());
				List<ObservedValue> observedValueList = query.getResultList();
				if (observedValueList.size() > 0)
				{
					mutationSummaryDTO.getProtocolDTOList().add(phenoService.protocolToProtocolDTO(protocol));
					mutationSummaryDTO.getObservedValueDTOHash().put("Protocol" + protocol.getId(), phenoService.observedValueListToObservedValueDTOList(observedValueList));
				}
			}
			
			/* Set "special" values that are displayed prominent:
			 * Consequence, Inheritance, Pathogenicity
			 */
			String sql = "SELECT ov FROM ObservedValue ov WHERE ov.target = :target";
			TypedQuery<ObservedValue> query = this.em.createQuery(sql, ObservedValue.class);
			query.setParameter("target", variant);
			List<ObservedValue> specialObservedValueList = query.getResultList();

			for (ObservedValue observedValue : specialObservedValueList)
			{
				if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "codon change"))
				{
					mutationSummaryDTO.setCodonChange(observedValue.getValue());
				}
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "consequence"))
				{
					mutationSummaryDTO.setConsequence(observedValue.getValue());
					mutationSummaryDTO.setObservedValue(observedValue.getValue());
				}
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "inheritance"))
				{
					mutationSummaryDTO.setInheritance(observedValue.getValue());
				}
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "pathogenicity"))
				{
					mutationSummaryDTO.setPathogenicity(observedValue.getValue());
				}
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "type of mutation"))
				{
					mutationSummaryDTO.setType(observedValue.getValue());
				}
			}

			mutationSummaryDTO.setPatientSummaryDTOList(new ArrayList<PatientSummaryDTO>());
			// helper hash to get distinct phenotypes
			HashMap<String, String> phenotypeNameHash          = new HashMap<String, String>();
			// helper hash to get distinct publications
			HashMap<String, PublicationDTO> publicationDTOHash = new HashMap<String, PublicationDTO>();
			
			List<Patient> patients = this.db.query(Patient.class).equals(Patient.MUTATIONS, variant.getId()).find();
			// Get distinct list, not available in Molgenis query language
			HashMap<Integer, Patient> patientHash = new HashMap<Integer, Patient>();
			for (Patient patient : patients)
				patientHash.put(patient.getId(), patient);
			patients = Arrays.asList(patientHash.values().toArray(new Patient[0]));
			
			List<ObservableFeature> features = this.db.query(ObservableFeature.class).equals(ObservableFeature.NAME, "Phenotype").or().equals(ObservableFeature.DESCRIPTION, "Phenotype").find();
			if (features.size() != 1)
				throw new DatabaseException("Not exactly one ObservableFeature with name 'Phenotype' found.");
	
			for (Patient patient : patients)
			{
				PatientSummaryDTO patientSummaryDTO = new PatientSummaryDTO();
				patientSummaryDTO.setPatientIdentifier(patient.getAlternateId().get(0).getName());
				
				List<ObservedValue> phenotypes = this.db.query(ObservedValue.class).equals(ObservedValue.FEATURE, features.get(0).getId()).equals(ObservedValue.TARGET, patient.getId()).find();
				List<String> phenotypeNames    = new ArrayList<String>();
				for (ObservedValue phenotype : phenotypes)
				{
					phenotypeNames.add(phenotype.getValue());
					phenotypeNameHash.put(phenotype.getValue(), phenotype.getValue());
				}
				patientSummaryDTO.setPhenotypeMajor(StringUtils.join(phenotypeNames, ", "));
				patientSummaryDTO.setPhenotypeSub("");
	
				/* We will also retrieve the mutation that we already have 
				 * Delete the first we find from the otherVariants list
				 * (It can be present more than once.)
				 */
				boolean found = false;
				for (SequenceCharacteristic patientVariant : patient.getMutations())
				{
					if (patientVariant.getId().equals(variant.getId()) && !found)
					{
						found = true;
						continue;
					}
					patientSummaryDTO.getVariantDTOList().add(this.sequenceCharacteristicToVariantDTO(patientVariant));
				}
	
				Submission submission  = patient.getSubmission();
				MolgenisUser submitter = submission.getSubmitters().get(0);
				patientSummaryDTO.setSubmitterDepartment(submitter.getDepartment());
				patientSummaryDTO.setSubmitterInstitute(submitter.getAffiliation_Name());
				patientSummaryDTO.setSubmitterCity(submitter.getCity());
				patientSummaryDTO.setSubmitterCountry(submitter.getCountry());
				patientSummaryDTO.setPublicationDTOList(new ArrayList<PublicationDTO>());
	
				if (CollectionUtils.isNotEmpty(patient.getPatientreferences()))
				{
					PublicationService publicationService = ServiceLocator.instance().getPublicationService();
					List<PublicationDTO> publicationDTOList = publicationService.publicationListToPublicationDTOList(patient.getPatientreferences());
					for (PublicationDTO publicationDTO : publicationDTOList)
					{
						patientSummaryDTO.getPublicationDTOList().add(publicationDTO);
						publicationDTOHash.put(publicationDTO.getName(), publicationDTO);
					}
				}
				mutationSummaryDTO.getPatientSummaryDTOList().add(patientSummaryDTO);
			}
	
			mutationSummaryDTO.setPhenotypeNameList(new ArrayList<String>());
			mutationSummaryDTO.getPhenotypeNameList().addAll(phenotypeNameHash.values());
	
			mutationSummaryDTO.setPublicationDTOList(new ArrayList<PublicationDTO>());
			mutationSummaryDTO.getPublicationDTOList().addAll(publicationDTOHash.values());
	
			mutationSummaryDTO.setPubmedURL(PublicationService.PUBMED_URL);
	
			return mutationSummaryDTO;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public MutationSummaryDTO variantToMutationSummaryDTO(final Variant variant)
	{
		try
		{
			MutationSummaryDTO mutationSummaryDTO = new MutationSummaryDTO();
			mutationSummaryDTO.setId(variant.getId());
			mutationSummaryDTO.setCdnaNotation(variant.getNameCdna());
			mutationSummaryDTO.setCdnaStart(variant.getStartCdna());
			mutationSummaryDTO.setGdnaNotation(variant.getNameGdna());
			mutationSummaryDTO.setGdnaStart(variant.getStartGdna());
			mutationSummaryDTO.setAaNotation(variant.getNameAa());
			mutationSummaryDTO.setAaStart(variant.getStartAa());
			mutationSummaryDTO.setType(variant.getType());
	
			/* Find the external molgenis variant id */
			for (AlternateId alternateId : variant.getAlternateId())
			{
				if ("molgenis_variant_id".equals(alternateId.getDefinition()))
					mutationSummaryDTO.setIdentifier(alternateId.getName());
			}
			/* Find exons */
			String sql = "SELECT e FROM Exon e WHERE ((e.startGdna <= :pos AND :pos <= e.endGdna AND e.strand = '+1') OR (e.endGdna <= :pos AND :pos <= e.startGdna AND e.strand = '-1'))";
			TypedQuery<Exon> query = this.em.createQuery(sql, Exon.class);
			query.setParameter("pos", variant.getStartGdna());
			
			for (Exon exon : query.getResultList())
			{
				mutationSummaryDTO.setExonId(exon.getId());
				mutationSummaryDTO.setExonName(exon.getName());
			}

			/* Find protein domains */
			String sqlpd = "SELECT pd FROM ProteinDomain pd WHERE ((pd.startGdna <= :pos AND :pos <= pd.endGdna AND pd.strand = '+1') OR (pd.endGdna <= :pos AND :pos <= pd.startGdna AND pd.strand = '-1'))";
			TypedQuery<ProteinDomain> querypd = this.em.createQuery(sqlpd, ProteinDomain.class);
			querypd.setParameter("pos", variant.getStartGdna());
			
			mutationSummaryDTO.setProteinDomainNameList(new ArrayList<String>());

			for (ProteinDomain proteinDomain : querypd.getResultList())
			{
				mutationSummaryDTO.getProteinDomainNameList().add(proteinDomain.getName());
			}

			if (StringUtils.isNotEmpty(mutationSummaryDTO.getAaNotation()))
				mutationSummaryDTO.setNiceNotation(mutationSummaryDTO.getCdnaNotation() + " (" + mutationSummaryDTO.getAaNotation() + ")");
			else
				mutationSummaryDTO.setNiceNotation(mutationSummaryDTO.getCdnaNotation());
			
			/* find ObservedValue's, separate 'Inheritance' and 'Consequence' */
			mutationSummaryDTO.setProtocolDTOList(new ArrayList<ProtocolDTO>());
			mutationSummaryDTO.setObservedValueDTOHash(new HashMap<String, List<ObservedValueDTO>>());
	
			PhenoService phenoService   = ServiceLocator.instance().getPhenoService();
	
			List<Protocol> protocolList = this.em.createQuery("SELECT p FROM Protocol p", Protocol.class).getResultList();
	
			for (Protocol protocol : protocolList)
			{
				if (protocol.getFeatures().size() == 0)
					continue;

				String sql2 = "SELECT ov FROM ObservedValue ov WHERE ov.target = :target AND feature IN (:features)";
				TypedQuery<ObservedValue> query2 = this.em.createQuery(sql2, ObservedValue.class);
				query2.setParameter("target", variant);
				query2.setParameter("features", protocol.getFeatures());
				List<ObservedValue> observedValueList = query2.getResultList();
				if (observedValueList.size() > 0)
				{
					mutationSummaryDTO.getProtocolDTOList().add(phenoService.protocolToProtocolDTO(protocol));
					mutationSummaryDTO.getObservedValueDTOHash().put("Protocol" + protocol.getId(), phenoService.observedValueListToObservedValueDTOList(observedValueList));
				}
			}
			
			/* Set "special" values that are displayed prominent:
			 * Consequence, Inheritance, Pathogenicity
			 */
			String sql3 = "SELECT ov FROM ObservedValue ov WHERE ov.target = :target";
			TypedQuery<ObservedValue> query3 = this.em.createQuery(sql3, ObservedValue.class);
			query3.setParameter("target", variant);
			List<ObservedValue> specialObservedValueList = query3.getResultList();

			for (ObservedValue observedValue : specialObservedValueList)
			{
				if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "codon change"))
				{
					mutationSummaryDTO.setCodonChange(observedValue.getValue());
				}
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "consequence"))
				{
					mutationSummaryDTO.setConsequence(observedValue.getValue());
					mutationSummaryDTO.setObservedValue(observedValue.getValue());
				}
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "inheritance"))
				{
					mutationSummaryDTO.setInheritance(observedValue.getValue());
				}
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "pathogenicity"))
				{
					mutationSummaryDTO.setPathogenicity(observedValue.getValue());
				}
//				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "type of mutation"))
//				{
//					mutationSummaryDTO.setType(observedValue.getValue());
//				}
			}

			mutationSummaryDTO.setPatientSummaryDTOList(new ArrayList<PatientSummaryDTO>());
			// helper hash to get distinct phenotypes
			HashMap<String, String> phenotypeNameHash          = new HashMap<String, String>();
			// helper hash to get distinct publications
			HashMap<Integer, PublicationDTO> publicationDTOHash = new HashMap<Integer, PublicationDTO>();
			
			List<Patient> patients = this.db.query(Patient.class).equals(Patient.MUTATIONS, variant.getId()).find();
			// Get distinct list, not available in Molgenis query language
			HashMap<Integer, Patient> patientHash = new HashMap<Integer, Patient>();
			for (Patient patient : patients)
				patientHash.put(patient.getId(), patient);
			patients = Arrays.asList(patientHash.values().toArray(new Patient[0]));
			
			for (Patient patient : patients)
			{
				PatientSummaryDTO patientSummaryDTO = new PatientSummaryDTO();
				patientSummaryDTO.setPatientIdentifier(patient.getAlternateId().get(0).getName());
				patientSummaryDTO.setPhenotypeMajor(patient.getPhenotype());
				
				phenotypeNameHash.put(patient.getPhenotype(), patient.getPhenotype());
	
				/* We will also retrieve the mutation that we already have 
				 * Delete the first we find from the otherVariants list
				 * (It can be present more than once.)
				 */
				boolean found = false;
				for (SequenceCharacteristic patientVariant : patient.getMutations())
				{
					if (patientVariant.getId().equals(variant.getId()) && !found)
					{
						found = true;
						continue;
					}
					patientSummaryDTO.getVariantDTOList().add(this.variantToVariantDTO((Variant) patientVariant));
				}
	
				Submission submission  = patient.getSubmission();
				MolgenisUser submitter = submission.getSubmitters().get(0);
				patientSummaryDTO.setSubmitterDepartment(submitter.getDepartment());
				patientSummaryDTO.setSubmitterInstitute(submitter.getAffiliation_Name());
				patientSummaryDTO.setSubmitterCity(submitter.getCity());
				patientSummaryDTO.setSubmitterCountry(submitter.getCountry());
				patientSummaryDTO.setPublicationDTOList(new ArrayList<PublicationDTO>());
	
				if (CollectionUtils.isNotEmpty(patient.getPatientreferences()))
				{
					PublicationService publicationService = ServiceLocator.instance().getPublicationService();
					List<PublicationDTO> publicationDTOList = publicationService.publicationListToPublicationDTOList(patient.getPatientreferences());
					for (PublicationDTO publicationDTO : publicationDTOList)
					{
						patientSummaryDTO.getPublicationDTOList().add(publicationDTO);
						publicationDTOHash.put(publicationDTO.getId(), publicationDTO);
					}
				}
				mutationSummaryDTO.getPatientSummaryDTOList().add(patientSummaryDTO);
			}
	
			mutationSummaryDTO.setPhenotypeNameList(new ArrayList<String>());
			mutationSummaryDTO.getPhenotypeNameList().addAll(phenotypeNameHash.values());
	
			mutationSummaryDTO.setPublicationDTOList(new ArrayList<PublicationDTO>());
			mutationSummaryDTO.getPublicationDTOList().addAll(publicationDTOHash.values());
	
			mutationSummaryDTO.setPubmedURL(PublicationService.PUBMED_URL);
	
			return mutationSummaryDTO;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public List<MutationSummaryDTO> observationElementListToMutationSummaryDTOList(final List<ObservationElement> mutationList)
	{
		List<Variant> result = new ArrayList<Variant>();
		
		for (ObservationElement e : mutationList)
		{
			if (e instanceof Variant)
			{
				result.add((Variant) e);
			}
			else if (e instanceof Patient)
			{
				String sql = "SELECT s FROM Patient p JOIN p.mutations s WHERE p = :patient";
				TypedQuery<Variant> query = this.em.createQuery(sql, Variant.class);
				query.setParameter("patient", e);
				result.addAll(query.getResultList());
			}
		}
		return this.variantListToMutationSummaryDTOList(result);
	}

	public List<MutationSummaryDTO> variantListToMutationSummaryDTOList(final List<Variant> mutations)
	{
		List<MutationSummaryDTO> result = new ArrayList<MutationSummaryDTO>();

		for (Variant mutation : mutations)
		{
			MutationSummaryDTO mutationSummaryVO = this.variantToMutationSummaryDTO(mutation);
			result.add(mutationSummaryVO);
		}
		
		return result;
	}

	public List<MutationSummaryDTO> sequenceCharacteristicListToMutationSummaryDTOList(final List<SequenceCharacteristic> mutations)
	{
		List<MutationSummaryDTO> result = new ArrayList<MutationSummaryDTO>();

		for (SequenceCharacteristic mutation : mutations)
		{
			MutationSummaryDTO mutationSummaryVO = this.sequenceCharacteristicToMutationSummaryDTO(mutation);
			result.add(mutationSummaryVO);
		}
		
		return result;
	}

	public PatientSummaryDTO patientToPatientSummaryDTO(final Patient patient)
	{
		PatientSummaryDTO patientSummaryDTO = new PatientSummaryDTO();

		patientSummaryDTO.setPatientId(patient.getId());
		patientSummaryDTO.setPhenotypeMajor(patient.getPhenotype());

		// find alternate IDs
		for (AlternateId alternateId : patient.getAlternateId())
		{
			if ("molgenis_patient_id".equals(alternateId.getDefinition()))
				patientSummaryDTO.setPatientIdentifier(alternateId.getName());
			else if ("local_patient_no".equals(alternateId.getDefinition()))
				patientSummaryDTO.setPatientLocalId(alternateId.getName());
		}

		/* Add variants */

		List<VariantDTO> variantDTOList = this.variantListToVariantDTOList(patient.getMutations());
		patientSummaryDTO.setVariantDTOList(variantDTOList);
		if (variantDTOList.size() > 0)
			patientSummaryDTO.setGdnaStart(variantDTOList.get(0).getGdnaStart());

		Submission submission  = patient.getSubmission();
		MolgenisUser submitter = submission.getSubmitters().get(0);
		patientSummaryDTO.setSubmitterDepartment(submitter.getDepartment());
		patientSummaryDTO.setSubmitterInstitute(submitter.getAffiliation_Name());
		patientSummaryDTO.setSubmitterCity(submitter.getCity());
		patientSummaryDTO.setSubmitterCountry(submitter.getCountry());

		/* Add literature references */
		if (CollectionUtils.isNotEmpty(patient.getPatientreferences()))
		{
			PublicationService publicationService = ServiceLocator.instance().getPublicationService();
			patientSummaryDTO.setPublicationDTOList(publicationService.publicationListToPublicationDTOList(patient.getPatientreferences()));
		}

		patientSummaryDTO.setPubmedURL(PublicationService.PUBMED_URL);

		return patientSummaryDTO;
	}

	public List<PatientSummaryDTO> patientListToPatientSummaryDTOList(final List<Patient> patients)
	{
		List<PatientSummaryDTO> result = new ArrayList<PatientSummaryDTO>();

		for (Patient patient : patients)
		{
			result.add(this.patientToPatientSummaryDTO(patient));
		}

		Collections.sort(result);

		return result;
	}

	public ProteinDomainDTO proteinDomainToProteinDomainDTO(final ProteinDomain proteinDomain, final Boolean noIntrons)
	{
		try
		{
			ProteinDomainDTO proteinDomainDTO = new ProteinDomainDTO();
			proteinDomainDTO.setDomainId(proteinDomain.getId());
			proteinDomainDTO.setDomainName(proteinDomain.getName());
			proteinDomainDTO.setGdnaEnd(proteinDomain.getEndGdna());
			proteinDomainDTO.setGdnaStart(proteinDomain.getStartGdna());
			proteinDomainDTO.setOrientation("-1".equals(proteinDomain.getStrand()) ? "R" : "F"); //TODO: What about "0"???

			/* Find exons */
			proteinDomainDTO.setExonDTOList(new ArrayList<ExonDTO>());

			String sql = "SELECT e FROM Exon e WHERE e.isIntron = 0 AND ((:startPos <= e.startGdna AND e.endGdna <= :endPos AND e.strand = '+1') OR (:endPos <= e.endGdna AND e.startGdna <= :startPos AND e.strand = '-1'))";
			TypedQuery<Exon> query = this.em.createQuery(sql, Exon.class);
			query.setParameter("startPos", proteinDomain.getStartGdna());
			query.setParameter("endPos", proteinDomain.getEndGdna());
			
			for (Exon exon : query.getResultList())
			{
				proteinDomainDTO.getExonDTOList().add(this.exonToExonDTO(exon));
			}

			// find exons/introns in the region of this protein domain
/*
			proteinDomainDTO.setExonDTOList(new ArrayList<ExonDTO>());
	
			Query<SequenceRelation> query = this.db.query(SequenceRelation.class);

			if ("-1".equals(proteinDomainDTO.getOrientation()))
			{
				query = query.lessOrEqual(SequenceRelation.FMIN, proteinDomainDTO.getGdnaStart());
				query = query.greaterOrEqual(SequenceRelation.FMAX, proteinDomainDTO.getGdnaEnd());
			}
			else
			{
				query = query.greaterOrEqual(SequenceRelation.FMIN, proteinDomainDTO.getGdnaStart());
				query = query.lessOrEqual(SequenceRelation.FMAX, proteinDomainDTO.getGdnaEnd());
			}

			List<SequenceRelation> exonRelations = query.find();

			for (SequenceRelation relation : exonRelations)
			{
				SequenceCharacteristic feature = relation.getSequenceFeature();

				if ("exon".equals(feature.getFeatureType().getName()))
					proteinDomainDTO.getExonDTOList().add(this.sequenceCharacteristicToExonDTO(feature));
				if (!noIntrons && "intron".equals(feature.getFeatureType().getName()))
					proteinDomainDTO.getExonDTOList().add(this.sequenceCharacteristicToExonDTO(feature));
			}
*/	
			return proteinDomainDTO;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public ProteinDomainDTO sequenceCharacteristicToProteinDomainDTO(final SequenceCharacteristic proteinDomain, final Boolean noIntrons)
	{
		try
		{
			ProteinDomainDTO proteinDomainDTO = new ProteinDomainDTO();
			proteinDomainDTO.setDomainId(proteinDomain.getId());
			proteinDomainDTO.setDomainName(proteinDomain.getName());
			
			// find start and end position
			List<SequenceRelation> relations = Arrays.asList(proteinDomain.getSequenceFeatureSequenceRelationCollection().toArray(new SequenceRelation[0]));
			for (SequenceRelation relation : relations)
			{
				SequenceCharacteristic target = relation.getSequenceTarget();
				
				if ("chromosome".equals(target.getFeatureType().getName()))
				{
					proteinDomainDTO.setGdnaEnd(relation.getFmax());
					proteinDomainDTO.setGdnaStart(relation.getFmin());
					proteinDomainDTO.setOrientation(relation.getStrand());
				}
			}
			
			// find exons/introns in the region of this protein domain
			proteinDomainDTO.setExonDTOList(new ArrayList<ExonDTO>());
	
			Query<SequenceRelation> query = this.db.query(SequenceRelation.class);

			if ("-1".equals(proteinDomainDTO.getOrientation()))
			{
				query = query.lessOrEqual(SequenceRelation.FMIN, proteinDomainDTO.getGdnaStart());
				query = query.greaterOrEqual(SequenceRelation.FMAX, proteinDomainDTO.getGdnaEnd());
			}
			else
			{
				query = query.greaterOrEqual(SequenceRelation.FMIN, proteinDomainDTO.getGdnaStart());
				query = query.lessOrEqual(SequenceRelation.FMAX, proteinDomainDTO.getGdnaEnd());
			}

			List<SequenceRelation> exonRelations = query.find();

			for (SequenceRelation relation : exonRelations)
			{
				SequenceCharacteristic feature = relation.getSequenceFeature();

				if ("exon".equals(feature.getFeatureType().getName()))
					proteinDomainDTO.getExonDTOList().add(this.sequenceCharacteristicToExonDTO(feature));
				if (/*!noIntrons && */"intron".equals(feature.getFeatureType().getName()))
					proteinDomainDTO.getExonDTOList().add(this.sequenceCharacteristicToExonDTO(feature));
			}
	
			return proteinDomainDTO;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public List<ProteinDomainDTO> proteinDomainListToProteinDomainDTOList(final List<ProteinDomain> proteinDomainList)
	{
		List<ProteinDomainDTO> result = new ArrayList<ProteinDomainDTO>();

		for (ProteinDomain proteinDomain : proteinDomainList)
			result.add(this.proteinDomainToProteinDomainDTO(proteinDomain, true));
		
		return result;
	}

	public List<ProteinDomainDTO> sequenceCharacteristicListToProteinDomainDTOList(final List<SequenceCharacteristic> proteinDomainList)
	{
		List<ProteinDomainDTO> result = new ArrayList<ProteinDomainDTO>();

		for (SequenceCharacteristic proteinDomain : proteinDomainList)
			result.add(this.sequenceCharacteristicToProteinDomainDTO(proteinDomain, true));
		
		return result;
	}
	
	/**
	 * get the notation of the codon change for a given mutation
	 * @param mutation
	 * @return codon change
	 * @throws DatabaseException
	 */
//	private String getCodonChange(SequenceCharacteristic mutation) throws DatabaseException
//	{
//		if (mutation.getAa_Position() == null || mutation.getAa_Position() == 0 || StringUtils.isEmpty(mutation.getCodonchange()))
//			return "";
//
//		GeneDTO geneDTO   = this.getGene();
//		String splicedSeq = SequenceUtils.splice(geneDTO.getNuclSequence());
//		return SequenceUtils.getCodon(splicedSeq, mutation.getAa_Position()) + ">" + mutation.getCodonchange();
//	}

	/**
	 * Get the left flanking nucleotide sequence of given exon
	 * @param exonDTO
	 * @return Left flanking sequence 
	 */
	private String getNuclSequenceFlankLeft(final ExonDTO exonDTO, final GeneDTO geneDTO)
	{
		Integer gdnaStart  = Math.abs(exonDTO.getGdnaStart().intValue() - geneDTO.getBpStart().intValue());
		Integer flankEnd   = Math.abs(gdnaStart);
		Integer flankStart = Math.abs(flankEnd - 10);

		return StringUtils.substring(geneDTO.getNuclSequence(), flankStart, flankEnd);
	}

	/**
	 * Get the right flanking nucleotide sequence of given exon
	 * @param exon
	 * @return Right flanking sequence
	 */
	private String getNuclSequenceFlankRight(final ExonDTO exonDTO, final GeneDTO geneDTO)
	{
		Integer gdnaStart  = Math.abs(exonDTO.getGdnaStart().intValue() - geneDTO.getBpStart().intValue());
		Integer gdnaEnd    = gdnaStart + exonDTO.getLength();
		Integer flankStart = gdnaEnd;
		Integer flankEnd   = flankStart + 10;

		return StringUtils.substring(geneDTO.getNuclSequence(), flankStart, flankEnd);
	}
	
	/**
	 * Get all exons sorted by their gDNA position
	 * @return list of ExonDTO's
	 */
	public List<ExonDTO> findAllExons()
	{
		try
		{
//			List<SequenceCharacteristic> exonList = this.db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("exon")).or().equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("intron")).find();
//			List<ExonDTO> exonDTOList             = this.sequenceCharacteristicListToExonDTOList(exonList);
			List<Exon> exonList       = this.db.query(Exon.class).find();
			List<ExonDTO> exonDTOList = this.exonListToExonDTOList(exonList);
			
			Collections.sort(exonDTOList);
//			
//			int cdnaPos = 1;
//			for (ExonDTO exonDTO : exonDTOList)
//			{
//				int cdnaStart = cdnaPos;
//				int lengthm1  = exonDTO.getGdnaStart() - exonDTO.getGdnaEnd();
//				int cdnaEnd   = cdnaStart + lengthm1;
//				System.out.println("INSERT INTO SequenceRelation (SequenceFeature, Feature, SequenceTarget, Target, relationType, strand, fmin, fmax) VALUES (" + exonDTO.getId() + ", " + exonDTO.getId() + ", 166, 166, 6, '1', " + cdnaStart + ", " + cdnaEnd + ");");
//				cdnaPos += lengthm1 + 1;
//			}
			return exonDTOList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	/**
	 * Get all introns sorted by their gDNA position
	 * @return list of ExonDTO's
	 */
	public List<ExonDTO> findAllIntrons()
	{
		try
		{
			List<SequenceCharacteristic> exonList = this.db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("intron")).find();
			
			List<ExonDTO> exonDTOList             = this.sequenceCharacteristicListToExonDTOList(exonList);
			
			Collections.sort(exonDTOList);
			
			return exonDTOList;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new SearchServiceException(e.getMessage());
		}
	}

	public ExonDTO findFirstExon(final ExonSearchCriteriaDTO criteria)
	{
		try
		{
			List<ExonDTO> exonDTOList = this.findAllExons();
			
			for (ExonDTO exonDTO : exonDTOList)
			{
				if ((Boolean.TRUE.equals(criteria.getIsIntron()) && exonDTO.getIsIntron()) || (Boolean.FALSE.equals(criteria.getIsIntron()) && !exonDTO.getIsIntron()))
					return exonDTO;
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

	public ExonDTO findPrevExon(final ExonSearchCriteriaDTO criteria)
	{
		if (criteria.getGdnaPosition() == null)
			return null;

		try
		{
			List<ExonDTO> exonDTOList = this.findAllExons();
			
			for (int i = 0; i < exonDTOList.size(); i++)
			{
				ExonDTO exonDTO = exonDTOList.get(i);
				
				if (exonDTO.getId().equals(criteria.getExonId()))
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

	public ExonDTO findNextExon(final ExonSearchCriteriaDTO criteria)
	{
		if (criteria.getGdnaPosition() == null)
			return null;

		try
		{
			List<ExonDTO> exonDTOList = this.findAllExons();
			
			for (int i = 0; i < exonDTOList.size(); i++)
			{
				ExonDTO exonDTO = exonDTOList.get(i);
				
				if (exonDTO.getId().equals(criteria.getExonId()))
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

	public ExonDTO findLastExon(final ExonSearchCriteriaDTO criteria)
	{
		try
		{
			List<ExonDTO> exonDTOList = this.findAllExons();

			Collections.reverse(exonDTOList);
			
			for (ExonDTO exonDTO : exonDTOList)
			{
				if ((Boolean.TRUE.equals(criteria.getIsIntron()) && exonDTO.getIsIntron()) || (Boolean.FALSE.equals(criteria.getIsIntron()) && !exonDTO.getIsIntron()))
					return exonDTO;
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
}
