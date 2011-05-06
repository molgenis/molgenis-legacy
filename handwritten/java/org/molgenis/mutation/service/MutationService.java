package org.molgenis.mutation.service;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.jdbc.JDBCConnectionHelper;

import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Exon_ProteinDomain;
import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.Patient;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.ProteinDomain;
import org.molgenis.mutation.util.MutationComparator;
import org.molgenis.mutation.util.SequenceUtils;
import org.molgenis.mutation.vo.ExonSearchCriteriaVO;
import org.molgenis.mutation.vo.ExonSummaryVO;
import org.molgenis.mutation.vo.MutationSearchCriteriaVO;
import org.molgenis.mutation.vo.MutationSummaryVO;
import org.molgenis.mutation.vo.MutationUploadVO;
import org.molgenis.mutation.vo.PatientSearchCriteriaVO;
import org.molgenis.mutation.vo.PatientSummaryVO;
import org.molgenis.core.Publication;
import org.molgenis.core.service.PublicationService;

public class MutationService implements Serializable
{
	private static final long serialVersionUID        = -5234460093223923754L;
	private Database db                               = null;
	private static MutationService mutationService    = null;
	private HashMap<Integer, MutationSummaryVO> cache = new HashMap<Integer, MutationSummaryVO>();
	private static final transient Logger logger      = Logger.getLogger(JDBCConnectionHelper.class.getSimpleName());

	// private constructor, use singleton instance
	private MutationService(Database db)
	{
		this.db = db;
	}
	
	public static MutationService getInstance(Database db)
	{
		if (mutationService == null)
			mutationService = new MutationService(db);
		
		return mutationService;
	}

	public List<MutationSummaryVO> findMutations(MutationSearchCriteriaVO criteria) throws DatabaseException, java.text.ParseException
	{
		Query<Mutation> query = this.db.query(Mutation.class);

		if (criteria.getCdnaPosition() != null)
			query = query.equals(Mutation.CDNA_POSITION, criteria.getCdnaPosition());
		if (criteria.getCodonChangeNumber() != null)
			query = query.equals(Mutation.AA_POSITION, criteria.getCodonChangeNumber());
		if (StringUtils.length(criteria.getConsequence()) > 2)
			query = query.like(Mutation.CONSEQUENCE, criteria.getConsequence() + "%");
		if (criteria.getExonId() != null)
			query = query.equals(Mutation.EXON, criteria.getExonId());
		if (criteria.getExonName() != null)
			query = query.equals(Mutation.EXON_NAME, "Exon " + criteria.getExonName()).or().equals(Mutation.EXON_NAME, "IVS" + criteria.getExonName());
		if (criteria.getExonNumber() != null)
			//TODO: cheap and bad trick to avoid join
			query = query.equals(Mutation.EXON_NAME, "Exon " + criteria.getExonNumber()).or().equals(Mutation.EXON_NAME, "IVS" + criteria.getExonNumber());
		if (criteria.getMid() != null)
			query = query.equals(Mutation.IDENTIFIER, criteria.getMid());
		if (StringUtils.length(criteria.getInheritance()) > 2)
			query = query.like(Mutation.INHERITANCE, criteria.getInheritance() + "%");
		if (criteria.getMutationId() != null)
			query = query.equals(Mutation.ID, criteria.getMutationId());
		if (criteria.getPhenotypeId() != null)
		{
			//TODO: add proper join capability
			List<Patient> patients    = this.db.query(Patient.class).equals(Patient.PHENOTYPE, criteria.getPhenotypeId()).find();
			List<Integer> mutationIds = new ArrayList<Integer>();
			for (Patient patient : patients)
			{
				mutationIds.add(patient.getMutation1_Id());
				mutationIds.add(patient.getMutation2_Id());
			}
			if (mutationIds.size() > 0)
				query = query.in(Mutation.ID, mutationIds);
		}
		if (StringUtils.length(criteria.getPhenotypeName()) > 2)
		{
			//TODO: add proper join capability
			List<MutationPhenotype> phenotypes =
				this.db.query(MutationPhenotype.class)
				.like(MutationPhenotype.NAME, criteria.getPhenotypeName() + "%").or()
				.like(MutationPhenotype.MAJORTYPE, criteria.getPhenotypeName() + "%").or()
				.like(MutationPhenotype.SUBTYPE, criteria.getPhenotypeName() + "%")
				.find();
			List<Integer> phenotypeIds = new ArrayList<Integer>();
			for (MutationPhenotype phenotype : phenotypes)
				phenotypeIds.add(phenotype.getId());
			if (phenotypeIds.size() > 0)
			{
				List<Patient> patients    = this.db.query(Patient.class).in(Patient.PHENOTYPE, phenotypeIds).find();
				List<Integer> mutationIds = new ArrayList<Integer>();
				for (Patient patient : patients)
				{
					mutationIds.add(patient.getMutation1_Id());
					mutationIds.add(patient.getMutation2_Id());
				}
				if (mutationIds.size() > 0)
					query = query.in(Mutation.ID, mutationIds);
			}
		}
		if (criteria.getPid() != null)
		{
			//TODO: add proper join capability
			List<Patient> patients    = this.db.query(Patient.class).equals(Patient.IDENTIFIER, criteria.getPid()).find();
			List<Integer> mutationIds = new ArrayList<Integer>();
			for (Patient patient : patients)
			{
				mutationIds.add(patient.getMutation1_Id());
				mutationIds.add(patient.getMutation2_Id());
			}
			if (mutationIds.size() > 0)
				query = query.in(Mutation.ID, mutationIds);
		}
		if (criteria.getProteinDomainId() != null)
		{
			//TODO: add proper join capability
//			List<Exon_ProteinDomain> epds = this.db.query(Exon_ProteinDomain.class).equals(Exon_ProteinDomain.PROTEINDOMAIN, criteria.getProteinDomainId()).find();
//			//List<Tuple> epds      = this.db.sql(String.format("SELECT Exon FROM Exon_proteinDomain WHERE proteinDomain = %d", criteria.getProteinDomainId()));
//			List<Integer> exonIds = new ArrayList<Integer>();
//			for (Exon_ProteinDomain epd : epds)
//				exonIds.add(epd.getExon_Id());
//			if (exonIds.size() > 0)
//				query = query.in(Mutation.EXON, exonIds);
			List<Exon> exons = this.db.query(Exon.class).equals(Exon.PROTEINDOMAIN, criteria.getProteinDomainId()).find();
			List<Integer> exonIds = new ArrayList<Integer>();
			for (Exon exon : exons)
				exonIds.add(exon.getId());
			if (exonIds.size() > 0)
				query = query.in(Mutation.EXON, exonIds);
		}
		if (StringUtils.length(criteria.getPublication()) > 2)
		{
			List<Publication> publications = this.db.query(Publication.class).like(Publication.AUTHORLIST, "%" + criteria.getPublication() + "%").or().like(Publication.TITLE, "%" + criteria.getPublication() + "%").find();
			List<Integer> publicationIds   = new ArrayList<Integer>();
			for (Publication publication : publications)
				publicationIds.add(publication.getId());
			if (publicationIds.size() > 0)
			{
				List<Patient> patients    = this.db.query(Patient.class).in(Patient.PUBLICATIONS, publicationIds).find();
				List<Integer> mutationIds = new ArrayList<Integer>();
				for (Patient patient : patients)
				{
					mutationIds.add(patient.getMutation1_Id());
					mutationIds.add(patient.getMutation2_Id());
				}
				if (mutationIds.size() > 0)
					query = query.in(Mutation.ID, mutationIds);
			}
		}
		if (criteria.getReportedAsSNP() != null)
			query = query.equals(Mutation.REPORTEDSNP, criteria.getReportedAsSNP());
		if (StringUtils.length(criteria.getType()) > 2)
			query = query.like(Mutation.TYPE_, criteria.getType() + "%");
		if (StringUtils.length(criteria.getVariation()) > 0)
			query = query.equals(Mutation.CDNA_NOTATION, criteria.getVariation()).or().equals(Mutation.CDNA_NOTATION, "c." + criteria.getVariation()).or().equals(Mutation.AA_NOTATION, criteria.getVariation()).or().equals(Mutation.AA_NOTATION, "p." + criteria.getVariation());

		if (query.getRules().length > 0)
			return this.toMutationSummaryVOList(query.find());
		else		
			return new ArrayList<MutationSummaryVO>();
	}

	private MutationSummaryVO toMutationSummaryVO(Mutation mutation) throws DatabaseException, java.text.ParseException
	{
		if (this.cache.containsKey(mutation.getId()))
			return this.cache.get(mutation.getId());

		PatientService patientService             = PatientService.getInstance(db);
		ProteinDomainService proteinDomainService = ProteinDomainService.getInstance(this.db);
		List<Mutation> allMutations               = this.getAllMutations();
		ProteinDomain proteinDomain               = proteinDomainService.findProteinDomain(mutation);

		MutationSummaryVO mutationSummaryVO       = new MutationSummaryVO();
		mutationSummaryVO.setMutation(mutation);
		if (StringUtils.isNotEmpty(mutation.getAa_Notation()))
			mutationSummaryVO.setNiceNotation(mutation.getCdna_Notation() + " (" + mutation.getAa_Notation() + ")");
		else
			mutationSummaryVO.setNiceNotation(mutation.getCdna_Notation());
		mutationSummaryVO.setCodonChange(this.getCodonChange(mutation));
		mutationSummaryVO.setProteinDomain(proteinDomain);
		mutationSummaryVO.setFirstMutation(allMutations.get(0));
		mutationSummaryVO.setPrevMutation(this.findPrevMutation(mutation));
		mutationSummaryVO.setNextMutation(this.findNextMutation(mutation));
		mutationSummaryVO.setLastMutation(allMutations.get(allMutations.size() - 1));

		PatientSearchCriteriaVO criteria = new PatientSearchCriteriaVO();
		criteria.setMutationId(mutation.getId());
		List<PatientSummaryVO> patientSummaryVOs  = patientService.findPatients(criteria);

		mutationSummaryVO.setPatients(patientSummaryVOs);

		mutationSummaryVO.setPubmedURL(PublicationService.PUBMED_URL);
		
		HashMap<Integer, MutationPhenotype> phenotypeHash = new HashMap<Integer, MutationPhenotype>();
		HashMap<Integer, Publication> publicationHash     = new HashMap<Integer, Publication>();
		for (PatientSummaryVO patientSummaryVO : patientSummaryVOs)
		{
			if (patientSummaryVO.getPhenotype() != null)
				phenotypeHash.put(patientSummaryVO.getPhenotype().getId(), patientSummaryVO.getPhenotype());
			if (patientSummaryVO.getPublications() != null)
				for (Publication publication : patientSummaryVO.getPublications())
					publicationHash.put(publication.getId(), publication);
		}

		mutationSummaryVO.setPhenotypes(Arrays.asList(phenotypeHash.values().toArray(new MutationPhenotype[0])));
		mutationSummaryVO.setPublications(Arrays.asList(publicationHash.values().toArray(new Publication[0])));

//		Mutation positionMutation                 = new Mutation();
//		logger.debug(">>> cdna_position==" + mutation.getCdna_position());
//		positionMutation.setCdna_position(mutation.getCdna_position());
//		logger.debug(">>> positionMutations==" + this.db.findByExample(positionMutation));
//		logger.debug(">>> positionMutations==" + this.db.query(Mutation.class).equals("cdna_position", mutation.getCdna_position()).find());
		mutationSummaryVO.setPositionMutations(this.db.query(Mutation.class).equals(Mutation.CDNA_POSITION, mutation.getCdna_Position()).find());

//		Mutation codonMutation                    = new Mutation();
//		codonMutation.setCdna_position(mutation.getCdna_position());
		Exon exon = this.db.findById(Exon.class, mutation.getExon_Id());
		if (!exon.getIsIntron())
			mutationSummaryVO.setCodonMutations(this.db.query(Mutation.class).equals(Mutation.AA_POSITION, mutation.getAa_Position()).find());

		// cache value
		this.cache.put(mutation.getId(), mutationSummaryVO);

		return mutationSummaryVO;
	}

	private List<MutationSummaryVO> toMutationSummaryVOList(List<Mutation> mutations) throws DatabaseException, java.text.ParseException
	{
		List<MutationSummaryVO> result            = new ArrayList<MutationSummaryVO>();

		for (Mutation mutation : mutations)
			result.add(this.toMutationSummaryVO(mutation));
		
		return result;
	}

	public Mutation findMutation(Integer id) throws DatabaseException
	{
		Mutation mutation = this.db.findById(Mutation.class, id);
		
		if (mutation == null)
			mutation = new Mutation();

		return mutation;
	}

	private Mutation findPrevMutation(Mutation mutation) throws DatabaseException, java.text.ParseException
	{
		List<Mutation> mutations = this.db.query(Mutation.class).lt(Mutation.CDNA_POSITION, mutation.getCdna_Position()).sortDESC(Mutation.CDNA_POSITION).limit(1).find();

		if (mutations.size() > 0)
			return mutations.get(0);
		else
			return mutation;
	}

	private Mutation findNextMutation(Mutation mutation) throws DatabaseException, java.text.ParseException
	{
		List<Mutation> mutations = this.db.query(Mutation.class).gt(Mutation.CDNA_POSITION, mutation.getCdna_Position()).sortASC(Mutation.CDNA_POSITION).limit(1).find();

		if (mutations.size() > 0)
			return mutations.get(0);
		else
			return mutation;
	}

	/**
	 * Get all mutations sorted by their position
	 * @return mutations
	 * @throws java.text.ParseException 
	 * @throws DatabaseException 
	 */
	public List<Mutation> getAllMutations() throws DatabaseException, java.text.ParseException
	{
		return this.db.query(Mutation.class).sortASC(Mutation.CDNA_POSITION).find();
	}

	/**
	 * Get number of unpublished mutations.
	 * @return number of unpublished mutations
	 * @throws DatabaseException
	 */
	public int getNumUnpublishedMutations() throws DatabaseException
	{
//		//TODO: Outer join is faster, but is syntax standardized?
//		return this.db.sql("SELECT DISTINCT id FROM Mutation WHERE NOT EXISTS (SELECT id FROM Patient, Patient_publications WHERE Patient.id = Patient_publications.Patient AND (Patient.Mutation1 = Mutation.id OR Patient.Mutation2 = Mutation.id))").size();
		
		
		
		javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT COUNT(id) FROM Mutation WHERE NOT EXISTS (SELECT id FROM Patient, Patient_publications WHERE Patient.id = Patient_publications.Patient AND (Patient.Mutation1 = Mutation.id OR Patient.Mutation2 = Mutation.id))");
		return ((Long)q.getSingleResult()).intValue();
	}

	/**
	 * Get the biggest identifier without the leading 'P'
	 * @return biggest identifier without the leading 'P'
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public Integer getMaxIdentifier() throws DatabaseException, ParseException
	{
		List<Mutation> mutations = this.db.query(Mutation.class).find();
		
		if (CollectionUtils.isEmpty(mutations))
			return 0;

		Collections.sort(mutations, new MutationComparator());
		return Integer.valueOf(mutations.get(mutations.size() - 1).getIdentifier().substring(1));
	}

	/**
	 * Get all mutations sorted by their position
	 * @return mutations
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<MutationSummaryVO> getAllMutationSummaries() throws DatabaseException, ParseException
	{
		return this.toMutationSummaryVOList(this.db.query(Mutation.class).sortASC(Mutation.CDNA_POSITION).find());
	}

	/**
	 * Get all mutation consequences
	 * @return mutation consequences
	 * @throws SQLException
	 * @throws DatabaseException
	 */
	public List<String> getConsequences() throws SQLException, DatabaseException
	{
//		ArrayList<String> consequences = new ArrayList<String>();
//		
//		ResultSet rs = db.executeQuery("SELECT DISTINCT consequence FROM Mutation", (QueryRule[]) null);
//		while (rs.next())
//			consequences.add(rs.getString(1));
//
//		return consequences;
		
		
		javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT DISTINCT consequence FROM Mutation");
		return q.getResultList();
	}

	/**
	 * Get all mutation types
	 * @return mutation types
	 * @throws DatabaseException 
	 * @throws SQLException 
	 * @throws Exception
	 */
	public List<String> getMutationTypes() throws SQLException, DatabaseException
	{
//		ArrayList<String> types = new ArrayList<String>();
//
//		ResultSet rs = db.executeQuery("SELECT DISTINCT type_ FROM Mutation", (QueryRule[]) null);
//		while (rs.next())
//			types.add(rs.getString(1));
//
//		return types;
		
		
		javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT DISTINCT type_ FROM Mutation");
		return q.getResultList();
	}

	/**
	 * Insert mutation and set primary key
	 * @param mutation
	 * @return number of mutations inserted
	 * @throws IOException 
	 * @throws Exception 
	 */
	public int insert(MutationUploadVO mutationUploadVO) throws Exception
	{
		int count = 0;
		try
		{
			// Exons are never inserted via mutations, just select
			logger.debug(">>> exon==" + mutationUploadVO.getExon());
			mutationUploadVO.getMutation().setExon(this.db.findById(Exon.class, mutationUploadVO.getExon().getId()));

			// Genes are never inserted via mutations, just select
			mutationUploadVO.getMutation().setGene(this.db.query(MutationGene.class).equals(MutationGene.NAME, mutationUploadVO.getGene().getName()).find().get(0));

			// Insert mutation and set primary key
			count = this.db.add(mutationUploadVO.getMutation());

			List<Mutation> mutations = this.db.findByExample(mutationUploadVO.getMutation());
			if (mutations.size() == 1)
			{
				mutationUploadVO.getMutation().setId(mutations.get(0).getId());
			}

			return count;
		}
		catch (Exception e)
		{
			logger.debug(">>> insert: e==" + e.toString());
//			try {
//				this.db.rollbackTx();
//			} catch (DatabaseException e1) {
//				e1.printStackTrace();
//			}
//			throw e;
		}
		return count;
	}

	/**
	 * get the notation of the codon change for a given mutation
	 * @param mutation
	 * @return codon change
	 * @throws DatabaseException
	 */
	private String getCodonChange(Mutation mutation) throws DatabaseException
	{
		if (mutation.getAa_Position() == null || mutation.getAa_Position() == 0 || StringUtils.isEmpty(mutation.getCodonchange()))
			return "";

		MutationGene gene = this.db.findById(MutationGene.class, mutation.getGene_Id());
		String splicedSeq = SequenceUtils.splice(gene.getSeq());
		return SequenceUtils.getCodon(splicedSeq, mutation.getAa_Position()) + ">" + mutation.getCodonchange();
	}
	
	public void assignValuesFromNotation(MutationUploadVO mutationUploadVO) throws DatabaseException, ParseException, RESyntaxException
	{
//		logger.debug(">>> assignValuesFromNotation: cdnaNotation==" + mutationUploadVO.getMutation().getCdna_notation());
		if (mutationUploadVO.getMutation().getCdna_Notation() != null)
		{
			String cdnaNotation = mutationUploadVO.getMutation().getCdna_Notation();
//			RE reIndel          = new RE("^c.([\\d+-]+)([_]*)([\\d+-]*)del([ACGTacgt\\d]*)ins([ACGTacgt]*)$");
			RE reIndel          = new RE("^c.([\\d+-_]+)del([ACGTacgt\\d]*)ins([ACGTacgt]*)$");
			RE reDeletion       = new RE("^c.([\\d+-_]+)del([ACGTacgt\\d]*)$");
			RE reDuplication    = new RE("^c.([\\d+-_]+)dup([ACGTacgt\\d]*)$");
//			RE reInsertion      = new RE("^c.([\\d+-]+)([_]*)([\\d+-]*)ins([ACGTacgt]*)$");
			RE reInsertion      = new RE("^c.([\\d+-_]+)ins([ACGTacgt]*)$");
			RE reSubstitution   = new RE("^c.([\\d+-]+)([ACGTacgt]+)>([ACGTacgt]+)$");

			if (reIndel.match(cdnaNotation))
			{
				mutationUploadVO.getMutation().setEvent("insertion/deletion");
				
				//deletion

				String[] position = reIndel.getParen(1).split("_");
				String deletion   = reIndel.getParen(2);
				String insertion  = reIndel.getParen(3);

//				logger.debug(">>> position==" + position + ", deletion==" + deletion + ", insertion==" + insertion);

				mutationUploadVO.getMutation().setMutationPosition(position[0]);
				if (position.length == 2)
					mutationUploadVO.getMutation().setLength(Integer.valueOf(position[1]) - Integer.valueOf(position[0]) + 1);
				else if (StringUtils.isNotEmpty(deletion))
					if (StringUtils.isNumeric(deletion))
						mutationUploadVO.getMutation().setLength(Integer.valueOf(deletion));
					else
						mutationUploadVO.getMutation().setLength(deletion.length());
				else
					mutationUploadVO.getMutation().setLength(1);
				
				// insertion

				mutationUploadVO.getMutation().setNtchange(insertion);
			}
			else if (reDeletion.match(cdnaNotation))
			{
				String[] position = reDeletion.getParen(1).split("_");
				String deletion   = reDeletion.getParen(2);
				
				mutationUploadVO.getMutation().setEvent("deletion");
				mutationUploadVO.getMutation().setMutationPosition(position[0]);
				if (position.length == 2)
					mutationUploadVO.getMutation().setLength(Integer.valueOf(position[1]) - Integer.valueOf(position[0]) + 1);
				else if (StringUtils.isNotEmpty(deletion))
					if (StringUtils.isNumeric(deletion))
						mutationUploadVO.getMutation().setLength(Integer.valueOf(deletion));
					else
						mutationUploadVO.getMutation().setLength(deletion.length());
				else
					mutationUploadVO.getMutation().setLength(1);
//				mutationUploadVO.getMutation().setPosition(reDeletion.getParen(1));
//				if (StringUtils.isNotEmpty(reDeletion.getParen(3)))
//					mutationUploadVO.getMutation().setLength(Integer.valueOf(reDeletion.getParen(3)) - Integer.valueOf(reDeletion.getParen(1)) + 1);
//				else if (StringUtils.isNotEmpty(reDeletion.getParen(4)))
//					if (StringUtils.isNumeric(reDeletion.getParen(4)))
//						mutationUploadVO.getMutation().setLength(Integer.valueOf(reDeletion.getParen(4)));
//					else
//						mutationUploadVO.getMutation().setLength(reDeletion.getParen(4).length());
//				else
//					mutationUploadVO.getMutation().setLength(1);
			}
			else if (reDuplication.match(cdnaNotation))
			{
				String[] position  = reDuplication.getParen(1).split("_");
				String duplication = reDuplication.getParen(2);
				
				mutationUploadVO.getMutation().setEvent("duplication");
				mutationUploadVO.getMutation().setMutationPosition(position[0]);
				if (position.length == 2)
					mutationUploadVO.getMutation().setLength(Integer.valueOf(position[1]) - Integer.valueOf(position[0]) + 1);
				else if (StringUtils.isNotEmpty(duplication))
					if (StringUtils.isNumeric(duplication))
						mutationUploadVO.getMutation().setLength(Integer.valueOf(duplication));
					else
						mutationUploadVO.getMutation().setLength(duplication.length());
				else
					mutationUploadVO.getMutation().setLength(1);
				
//				if (StringUtils.isNotEmpty(reDuplication.getParen(3)))
//					mutationUploadVO.getMutation().setLength(Integer.valueOf(reDuplication.getParen(3)) - Integer.valueOf(reDuplication.getParen(1)) + 1);
//				else if (StringUtils.isNotEmpty(reDuplication.getParen(4)))
//					if (StringUtils.isNumeric(reDeletion.getParen(4)))
//						mutationUploadVO.getMutation().setLength(Integer.valueOf(reDeletion.getParen(4)));
//					else
//						mutationUploadVO.getMutation().setLength(reDuplication.getParen(4).length());
//				else
//					mutationUploadVO.getMutation().setLength(1);
			}
			else if (reInsertion.match(cdnaNotation))
			{
				String[] position = reInsertion.getParen(1).split("_");
				String insertion  = reInsertion.getParen(2);
				mutationUploadVO.getMutation().setEvent("insertion");
				mutationUploadVO.getMutation().setMutationPosition(position[0]);
				mutationUploadVO.getMutation().setLength(insertion.length());
				mutationUploadVO.getMutation().setNtchange(insertion);
			}
			else if (reSubstitution.match(cdnaNotation))
			{
				mutationUploadVO.getMutation().setEvent("point mutation");
				mutationUploadVO.getMutation().setMutationPosition(reSubstitution.getParen(1));
				mutationUploadVO.getMutation().setLength(reSubstitution.getParen(2).length());
				mutationUploadVO.getMutation().setNtchange(reSubstitution.getParen(3));
			}
//			logger.debug(">>> assignValuesFromNotation: cdnaNotation==" + cdnaNotation + ", event==" + mutationUploadVO.getMutation().getEvent() + ", pos==" + mutationUploadVO.getMutation().getPosition() + ", len==" + mutationUploadVO.getMutation().getLength() + ", ntchange==" + mutationUploadVO.getMutation().getNtchange());
		}
		this.assignValuesFromPosition(mutationUploadVO);
	}

	public void assignValuesFromPosition(MutationUploadVO mutationUploadVO) throws DatabaseException, ParseException, RESyntaxException
	{
		if (StringUtils.isEmpty(mutationUploadVO.getMutation().getMutationPosition()) || "0".equals(mutationUploadVO.getMutation().getMutationPosition()))
			return;
		
//		MutationGene gene = this.db.query(MutationGene.class).equals(MutationGene.NAME, "COL7A1").find().get(0);
		MutationGene gene = this.db.query(MutationGene.class).equals(MutationGene.NAME, "CHD7").find().get(0);
		mutationUploadVO.setGene(gene);
		mutationUploadVO.getMutation().setGene(gene);

		//logger.debug("gene==" + this.mutationVO.getGene());
		//HashMap<String, String> aminoAcidHash = this.mutationService.getAminoAcids();

		String nuclSequence      = mutationUploadVO.getGene().getSeq();
		StringBuffer mutSequence = new StringBuffer(nuclSequence);

		// find corresponding exon
		ExonSearchCriteriaVO criteria = new ExonSearchCriteriaVO();
		criteria.setPosition(mutationUploadVO.getMutation().getMutationPosition());
		if (mutationUploadVO.getMutation().getMutationPosition().indexOf("+") > -1 || mutationUploadVO.getMutation().getMutationPosition().indexOf("-") > -1)
			criteria.setIsIntron(true);
		else
			criteria.setIsIntron(false);
//		logger.debug(">>> assignValues: vor findExons");
		
		ExonService exonService = ExonService.getInstance(this.db);
//		logger.debug(">>> position==" + mutationUploadVO.getMutation().getPosition() + ", criteria==" + criteria.toString());
		List<ExonSummaryVO> exonSummaryVOs = exonService.findExons(criteria);
//		logger.debug(">>> assignValues: nach findExons: exonSummaryVOs==" + exonSummaryVOs.size());
		Exon exon                = exonSummaryVOs.get(0).getExon();
//		logger.debug(">>> exon==" + exon.toString());

		mutationUploadVO.setExon(exon);
		mutationUploadVO.getMutation().setExon(exon); // this is crap, use navigable objects
		mutationUploadVO.getMutation().setCdna_Position(SequenceUtils.getCDNAPosition(mutationUploadVO.getMutation().getMutationPosition()));
		mutationUploadVO.getMutation().setGdna_Position(SequenceUtils.getGDNAPosition(mutationUploadVO.getMutation().getMutationPosition(), exon));

		int mutationStart;
		if ("F".equals(mutationUploadVO.getGene().getOrientation()))
			mutationStart = mutationUploadVO.getMutation().getGdna_Position() - mutationUploadVO.getGene().getBpStart().intValue();
		else
			mutationStart = mutationUploadVO.getGene().getBpStart().intValue() - mutationUploadVO.getMutation().getGdna_Position();
		
		if (mutationUploadVO.getMutation().getLength() == null)
			mutationUploadVO.getMutation().setLength(1); // default value

//		logger.debug(">>> vor assignNt: mutationStart==" + mutationStart);
		mutationUploadVO.assignNt(nuclSequence, mutationStart);
//		logger.debug(">>> nt==" + mutationUploadVO.getNt());
		//this.mutationVO.setNt(nuclSequence.substring(mutationStart, mutationStart + 1).toUpperCase());
		//logger.debug("mutationStart==" + mutationStart + ".");
		//logger.debug("nt==" + nuclSequence.substring(mutationStart, mutationStart + 1).toUpperCase() + ".");

		if (mutationUploadVO.getMutation().getEvent().equals("deletion"))
		{
			int mutationEnd      = mutationStart + mutationUploadVO.getMutation().getLength();
			//this.mutationVO.setNt(nuclSequence.substring(mutationStart, mutationEnd).toUpperCase());
			mutationUploadVO.assignNt(nuclSequence, mutationStart);
//			logger.debug(">>> nt==" + mutationUploadVO.getNt());
			mutSequence.delete(mutationStart, mutationEnd);
			mutationUploadVO.getMutation().setNtchange("");
		}
		else if (mutationUploadVO.getMutation().getEvent().equals("duplication"))
		{
			//int mutationEnd      = mutationStart + mutationUploadVO.getMutation().getLength();
			mutationUploadVO.assignNt(nuclSequence, mutationStart);
			mutSequence.insert(mutationStart, mutationUploadVO.getNt());
			mutationUploadVO.getMutation().setNtchange("");
		}
		else if (mutationUploadVO.getMutation().getEvent().equals("insertion"))
		{
			mutationStart++; // insert *after* position
			mutSequence.insert(mutationStart, mutationUploadVO.getMutation().getNtchange());
			mutationUploadVO.getMutation().setLength(mutationUploadVO.getMutation().getNtchange().length());
		}
		else if (mutationUploadVO.getMutation().getEvent().equals("point mutation"))
		{
			int mutationEnd      = mutationStart + mutationUploadVO.getMutation().getNtchange().length();
			//this.mutationVO.setNt(nuclSequence.substring(mutationStart, mutationEnd).toUpperCase());
			if (mutationUploadVO.getMutation().getNtchange() != null)
				mutSequence.replace(mutationStart, mutationEnd, mutationUploadVO.getMutation().getNtchange());
			mutationUploadVO.getMutation().setLength(mutationUploadVO.getMutation().getNtchange().length());
		}
		else if (mutationUploadVO.getMutation().getEvent().equals("insertion/deletion"))
		{
			// deletion
			
			int mutationEnd      = mutationStart + mutationUploadVO.getMutation().getLength();
			//this.mutationVO.setNt(nuclSequence.substring(mutationStart, mutationEnd).toUpperCase());
			mutationUploadVO.assignNt(nuclSequence, mutationStart);
//			logger.debug(">>> nt==" + mutationUploadVO.getNt());
			mutSequence.delete(mutationStart, mutationEnd);
			
			// insertion
			
			mutationStart++; // insert *after* position
			mutSequence.insert(mutationStart, mutationUploadVO.getMutation().getNtchange());
		}

		logger.debug(">>> assignValuesFromPosition: mutation==" + mutationUploadVO.getMutation().toString());
		mutationUploadVO.assignCdna_notation();
		mutationUploadVO.assignGdna_notation();

		// Calculations for transcribed part
		
		if (exon.getIsIntron())
		{
			mutationUploadVO.setCodon("");
			mutationUploadVO.setAa("");
			mutationUploadVO.setAachange("");
			mutationUploadVO.getMutation().setCodonchange("");
			mutationUploadVO.getMutation().setAa_Position(0);
			mutationUploadVO.getMutation().setAa_Notation("");
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

			mutationUploadVO.setCodon(SequenceUtils.getCodonByPosition(splNuclSeq, mutTripletPos)); //StringUtils.substring(splNuclSeq, mutTripletPos, mutTripletPos + 3));
			mutationUploadVO.getMutation().setCodonchange(SequenceUtils.getCodonByPosition(splMutSeq, mutTripletPos)); //StringUtils.substring(splMutSeq, mutTripletPos, mutTripletPos + 3));
			mutationUploadVO.setAa(SequenceUtils.getCodonByPosition(trlNuclSeq, trlTripletPos)); //StringUtils.substring(trlNuclSeq, trlTripletPos, trlTripletPos + 3));
			mutationUploadVO.setAachange(SequenceUtils.getCodonByPosition(trlMutSeq, trlTripletPos)); //StringUtils.substring(trlMutSeq, trlTripletPos, trlTripletPos + 3));

			mutationUploadVO.getMutation().setAa_Position(changeCodonNum);
			
			mutationUploadVO.assignAa_notation(trlMutSeq, changeAaNum);
		}

		mutationUploadVO.assignType();
		mutationUploadVO.assignConsequence();
//		logger.debug(">>> assignValuesFromPosition: mutation==" + mutationUploadVO.getMutation().toString());
//		logger.debug(">>> end of assignValues: nt==" + mutationUploadVO.getNt());
	}
	
	public void setDefaults(MutationUploadVO mutationUploadVO) throws DatabaseException, ParseException
	{
		// set default gene

//		MutationGene gene = this.db.query(MutationGene.class).equals(MutationGene.NAME, "COL7A1").find().get(0);
		MutationGene gene = this.db.query(MutationGene.class).equals(MutationGene.NAME, "CHD7").find().get(0);

		mutationUploadVO.setGene(gene);
		mutationUploadVO.getMutation().setGene(gene);
		
		mutationUploadVO.getMutation().setEvent("NA");
	}

	public int getNumMutations() throws DatabaseException
	{
		return this.db.count(Mutation.class);
	}
}
