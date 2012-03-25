package org.molgenis.mutation.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
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
import org.molgenis.pheno.ObservedValue;
import org.molgenis.pheno.Patient;
import org.molgenis.pheno.dto.IndividualDTO;
import org.molgenis.pheno.dto.ObservedValueDTO;
import org.molgenis.pheno.dto.ProtocolDTO;
import org.molgenis.pheno.service.PhenoService;
import org.molgenis.protocol.Protocol;
import org.molgenis.submission.Submission;
import org.molgenis.variant.SequenceCharacteristic;
import org.molgenis.variant.SequenceRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MolgenisVariantService
{
	protected Database db;
	protected EntityManager em;
//	protected Map<String, Measurement> measurementCache;
	protected Map<String, OntologyTerm> ontologyTermCache;
//	private HashMap<Integer, ExonDTO> exonCache                   = new HashMap<Integer, ExonDTO>();
//	private HashMap<Integer, PatientSummaryVO> patientCache             = new HashMap<Integer, PatientSummaryVO>();
//	private HashMap<Integer, ProteinDomainSummaryVO> proteinDomainCache = new HashMap<Integer, ProteinDomainSummaryVO>();

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
			RE reExon   = new RE("^(\\d+)$");
			RE reIntron = new RE("^(\\d+)([+-])(\\d+)$");
	
			if (reExon.match(position))
				return this.findExonByCdnaPosition(Integer.parseInt(reExon.getParen(1)));
			else if (reIntron.match(position))
			{
				// search by gDNA position since intron don't have cDNA position
				int cdnaPosition = Integer.parseInt(reIntron.getParen(1));
				String operation = reIntron.getParen(2);
				int cdnaAdder    = Integer.parseInt(reIntron.getParen(3));
	
				int gdnaPosition;
	
				ExonDTO exonDTO  = this.findExonByCdnaPosition(cdnaPosition);
	
				if ("+".equals(operation))
					gdnaPosition = ("F".equals(exonDTO.getOrientation()) ? exonDTO.getGdnaStart() + cdnaAdder : exonDTO.getGdnaStart() - cdnaAdder);
				else
					gdnaPosition = ("F".equals(exonDTO.getOrientation()) ? exonDTO.getGdnaStart() - cdnaAdder : exonDTO.getGdnaStart() + cdnaAdder);
	
				return this.findExonByGdnaPosition(gdnaPosition);
			}
			else
				throw new RESyntaxException("Invalid position notation: " + position);
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
			List<SequenceRelation> relations = this.db.query(SequenceRelation.class).lessOrEqual(SequenceRelation.FMIN, position).greaterOrEqual(SequenceRelation.FMAX, position).find();
			
			if (relations.size() != 1)
				throw new SearchServiceException("Not exactly one SequenceCharacteristic matching for cDNA position " + position);
			
			return this.sequenceCharacteristicToExonDTO(relations.get(0).getSequenceFeature());
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
			List<SequenceRelation> relations = this.db.query(SequenceRelation.class).lessOrEqual(SequenceRelation.FMIN, position).greaterOrEqual(SequenceRelation.FMAX, position).equals(SequenceRelation.STRAND, "+1").find();
			relations.addAll(this.db.query(SequenceRelation.class).lessOrEqual(SequenceRelation.FMAX, position).greaterOrEqual(SequenceRelation.FMIN, position).equals(SequenceRelation.STRAND, "-1").find());

			for (SequenceRelation relation : relations)
			{
				SequenceCharacteristic seqChar = (SequenceCharacteristic) relation.getFeature();
				if ("intron".equals(seqChar.getFeatureType().getName()))
					return this.sequenceCharacteristicToExonDTO(seqChar);
			}

			throw new SearchServiceException("No SequenceCharacteristic matching for gDNA position " + position);
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
			List<SequenceCharacteristic> geneList =
					this.db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("gene")).find();
	
			if (geneList.size() != 1)
				throw new SearchServiceException("Not exactly one gene found in database.");
			
			SequenceCharacteristic gene = geneList.get(0);
	
			return this.sequenceCharacteristicToGeneDTO(gene);
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
//System.out.println(">>> sequenceCharacteristicToExonDTO: name==" + exon.getName() + ", length==" + exon.getSeqlen());
		ExonDTO exonDTO = new ExonDTO();
		exonDTO.setDomainId(new ArrayList<Integer>());
		exonDTO.setDomainName(new ArrayList<String>());
		exonDTO.setId(exon.getId());
		exonDTO.setIsIntron("intron".equals(exon.getFeatureType().getName()) ? true : false);
		exonDTO.setLength(exon.getSeqlen());
		exonDTO.setName(exon.getName());
		if (exon.getSeqlen() != null && exon.getSeqlen() % 3 == 0)
			exonDTO.setMultiple3Nucl(true);
		else
			exonDTO.setMultiple3Nucl(false);

		// find relation to the sequence of the gene
//		List<SequenceRelation> sequenceRelationList = Arrays.asList(exon.getSequenceFeatureSequenceRelationCollection().toArray(new SequenceRelation[0]));
		List<SequenceRelation> sequenceRelationList = this.db.query(SequenceRelation.class).equals(SequenceRelation.SEQUENCEFEATURE, exon.getId()).find();

		for (SequenceRelation relation : sequenceRelationList)
		{
			if (relation.getRelationType() == null)
				continue;

			SequenceCharacteristic tmp = relation.getSequenceTarget();
//System.out.println(">>> relationType==" + relation.getRelationType().getName() + ", feature==" + tmp.getFeatureType().getName());
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

		GeneDTO geneDTO = this.findGene();

//		System.out.println(">>> sequenceCharacteristicToExonDTO: gene.seq==" + geneDTO.getNuclSequence().length() + ", gdanStart==" + exonDTO.getGdnaStart() + ", gdnaEnd==" + exonDTO.getGdnaEnd());
		Integer gdnaStart = Math.abs(exonDTO.getGdnaStart() - geneDTO.getBpStart().intValue());
		Integer gdnaEnd   = Math.abs(exonDTO.getGdnaEnd() - geneDTO.getBpStart().intValue() + 1);//gdnaStart + exon.getLength();
		exonDTO.setNuclSequence(StringUtils.substring(geneDTO.getNuclSequence(), gdnaStart, gdnaEnd));
		exonDTO.setNuclSequenceFlankLeft(this.getNuclSequenceFlankLeft(exonDTO, geneDTO));
		exonDTO.setNuclSequenceFlankRight(this.getNuclSequenceFlankRight(exonDTO, geneDTO));
		exonDTO.setOrientation(geneDTO.getOrientation());

		if (!exonDTO.getIsIntron())
		{
//			System.out.println(">>> sequenceCharacteristicToExonDTO: cdnaStart==" + exonDTO.getCdnaStart() + ", cdnaEnd==" + exonDTO.getCdnaEnd() + ", aaseq==" + geneDTO.getAaSequence());
			exonDTO.setAaSequence(StringUtils.substring(geneDTO.getAaSequence(), exonDTO.getCdnaStart(), exonDTO.getCdnaEnd()));
			exonDTO.setNumFullAminoAcids(SequenceUtils.getNumFullAminoAcids(exonDTO.getAaSequence()));
			exonDTO.setNumPartAminoAcids(SequenceUtils.getNumPartAminoAcids(exonDTO.getAaSequence()));
			exonDTO.setNumGlyXYRepeats(SequenceUtils.getNumGlyXYRepeats(exonDTO.getAaSequence()));
		}

		if (exonDTO.getIsIntron())
		{
			exonDTO.setNuclSequence(exonDTO.getNuclSequence().toLowerCase());
		}

		return exonDTO;
	}

	public List<ExonDTO> sequenceCharacteristicListToExonDTOList(final List<SequenceCharacteristic> exons) throws DatabaseException
	{
		List<ExonDTO> result = new ArrayList<ExonDTO>();

		for (SequenceCharacteristic exon : exons)
			result.add(this.sequenceCharacteristicToExonDTO(exon));
		
		return result;
	}

	public List<VariantDTO> sequenceCharacteristicListToVariantDTOList(final List<SequenceCharacteristic> variantList)
	{
		List<VariantDTO> result = new ArrayList<VariantDTO>();

		for (SequenceCharacteristic variant : variantList)
			result.add(this.sequenceCharacteristicToVariantDTO(variant));
				
		return result;
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
					variantDTO.setAaNotation(relation.getSequenceFeature().getName());
			}
	
			/* Find the external molgenis variant id */
			for (AlternateId alternateId : variant.getAlternateId())
			{
				if ("molgenis_variant_id".equals(alternateId.getDefinition()))
					variantDTO.setIdentifier(alternateId.getName());
			}
			
			/* Find corresponding exon/intron */
			List<SequenceRelation> featureRelations = this.db.query(SequenceRelation.class).equals(SequenceRelation.FEATURE, variant.getId()).find();
			
			for (SequenceRelation relation : featureRelations)
			{
				if ("part-of".equals(relation.getRelationType().getName()))
				{
					SequenceCharacteristic target = (SequenceCharacteristic) relation.getTarget();
					
					if ("exon".equals(target.getFeatureType().getName()) || "intron".equals(target.getFeatureType().getName()))
					{
						variantDTO.setExonId(target.getId());
						variantDTO.setExonName(target.getName());
					}
				}
			}

			/* Find pathogenicity */
			String sql = "SELECT ov FROM ObservedValue ov JOIN ov.feature f WHERE ov.target = :target AND f.name = :feature";
			TypedQuery<ObservedValue> query = this.em.createQuery(sql, ObservedValue.class);
			query.setParameter("target", variant);
			query.setParameter("feature", "Pathogenicity");
			List<ObservedValue> observedValueList = query.getResultList();

			/* A proper data model would ensure that exactly one is found.
			 * But you get what you deserve.
			 */
			if (observedValueList.size() == 1)
				variantDTO.setPathogenicity(observedValueList.get(0).getValue());

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
			List<ObservedValue> specialObservedValueList = this.db.query(ObservedValue.class).find();

			for (ObservedValue observedValue : specialObservedValueList)
			{
				if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "codon change"))
					mutationSummaryDTO.setCodonChange(observedValue.getValue());
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "consequence"))
					mutationSummaryDTO.setConsequence(observedValue.getValue());
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "inheritance"))
					mutationSummaryDTO.setInheritance(observedValue.getValue());
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "pathogenicity"))
					mutationSummaryDTO.setPathogenicity(observedValue.getValue());
				else if (StringUtils.equalsIgnoreCase(observedValue.getFeature().getName(), "type of mutation"))
					mutationSummaryDTO.setType(observedValue.getValue());
			}

	//		List<ProteinDomain> proteinDomains                = this.db.query(ProteinDomain.class).in(ProteinDomain.ID, exon.getProteinDomain_Id()).find();
	//
	//		// helper hash to get distinct protein domain names
	//		HashMap<Integer, String> domainNameHash           = new HashMap<Integer, String>();
	//		for (ProteinDomain domain : proteinDomains)
	//			domainNameHash.put(domain.getId(), domain.getName());
	//		mutationSummaryVO.setProteinDomainNameList(new ArrayList<String>());
	//		mutationSummaryVO.getProteinDomainNameList().addAll(domainNameHash.values());
	
			mutationSummaryDTO.setPatientSummaryDTOList(new ArrayList<PatientSummaryDTO>());
			// helper hash to get distinct phenotypes
			HashMap<String, String> phenotypeNameHash         = new HashMap<String, String>();
			// helper hash to get distinct publications
			HashMap<Integer, PublicationDTO> publicationDTOHash = new HashMap<Integer, PublicationDTO>();
			
			List<Patient> patients = this.db.query(Patient.class).equals(Patient.MUTATIONS, variant.getId()).find();
			// Get distinct list, not available in Molgenis query language
			HashMap<Integer, Patient> patientHash = new HashMap<Integer, Patient>();
			for (Patient patient : patients)
				patientHash.put(patient.getId(), patient);
			patients = Arrays.asList(patientHash.values().toArray(new Patient[0]));
			
			List<ObservableFeature> features = this.db.query(ObservableFeature.class).equals(ObservableFeature.NAME, "Phenotype").find();
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
	//			String phenotypeName = phenotype.getMajortype() + (StringUtils.isNotEmpty(phenotype.getSubtype()) ? ", " + phenotype.getSubtype() : "");
	//			phenotypeNameHash.put(phenotype.getId(), phenotypeName);
	
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
					patientSummaryDTO.setPublicationDTOList(publicationService.publicationListToPublicationDTOList(patient.getPatientreferences()));
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

		// find alternate IDs
		for (AlternateId alternateId : patient.getAlternateId())
		{
			if ("molgenis_patient_id".equals(alternateId.getDefinition()))
				patientSummaryDTO.setPatientIdentifier(alternateId.getName());
			else if ("local_patient_no".equals(alternateId.getDefinition()))
				patientSummaryDTO.setPatientLocalId(alternateId.getName());
		}

		/* Add variants */

		List<SequenceCharacteristic> variantList = patient.getMutations();
		patientSummaryDTO.setVariantDTOList(this.sequenceCharacteristicListToVariantDTOList(variantList));

		/* Add phenotypic values */
		PhenoService phenoService   = ServiceLocator.instance().getPhenoService();

		IndividualDTO individualDTO = phenoService.findPhenotypeDetails(patient.getId());
		
		HashMap<String, List<ObservedValueDTO>> features = individualDTO.getObservedValues();

		List<ObservedValueDTO> phenotypes = features.get("Phenotype");

		if (CollectionUtils.isNotEmpty(phenotypes))
		{
			List<String> phenotypeNames = new ArrayList<String>();
			for (ObservedValueDTO phenotype : phenotypes)
				phenotypeNames.add(phenotype.getValue());
			patientSummaryDTO.setPhenotypeMajor(StringUtils.join(phenotypeNames, ", "));
			patientSummaryDTO.setPhenotypeSub("");
		}
			
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
			result.add(this.patientToPatientSummaryDTO(patient));

		return result;
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
//			System.out.println(">>> sequenceCharacteristicToProteinDomainDTO: orientation==" + proteinDomainDTO.getOrientation());
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
//System.out.println(">>> sequenceCharacteristicToProteinDomainDTO: query==" + query);
			List<SequenceRelation> exonRelations = query.find();
//System.out.println(">>> sequenceCharacteristicToProteinDomainDTO: found:" + exonRelations.size());
			for (SequenceRelation relation : exonRelations)
			{
				SequenceCharacteristic feature = relation.getSequenceFeature();
//System.out.println(">>> sequenceCharacteristicToProteinDomainDTO: feature==" + feature.getName());
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
//System.out.println(">>> exonDTP==" + exonDTO.getGdnaStart() + ", geneDTO==" + geneDTO.getBpStart());
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
			List<SequenceCharacteristic> exonList = this.db.query(SequenceCharacteristic.class).equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("exon")).or().equals(SequenceCharacteristic.FEATURETYPE, this.ontologyTermCache.get("intron")).find();
			
			List<ExonDTO> exonDTOList             = this.sequenceCharacteristicListToExonDTOList(exonList);
			
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
