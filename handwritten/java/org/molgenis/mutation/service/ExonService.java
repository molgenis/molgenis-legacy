package org.molgenis.mutation.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.jdbc.JDBCConnectionHelper;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Gene;

import org.molgenis.mutation.Exon;
import org.molgenis.mutation.MutationGene;
import org.molgenis.mutation.util.SequenceUtils;
import org.molgenis.mutation.vo.ExonSearchCriteriaVO;
import org.molgenis.mutation.vo.ExonSummaryVO;

public class ExonService implements Serializable
{
	private static final long serialVersionUID   = -6713716877840714621L;
	private static ExonService exonService       = null;
	private Database db                          = null;
	private static final transient Logger logger = Logger.getLogger(JDBCConnectionHelper.class.getSimpleName());
	
	// private constructor, use singleton instance
	private ExonService(Database db)
	{
		this.db =db;
	}
	
	public static ExonService getInstance(Database db)
	{
		if (exonService == null)
			exonService = new ExonService(db);
		
		return exonService;
	}

	public List<ExonSummaryVO> findExons(ExonSearchCriteriaVO criteria) throws DatabaseException, ParseException, RESyntaxException
	{
		Query<Exon> query = this.db.query(Exon.class);

		if (criteria.getCdnaPosition() != null)
		{
			query = query.lessOrEqual(Exon.CDNA_POSITION, criteria.getPosition());
			query = query.greaterOrEqual("cdna_position + length - 1", criteria.getPosition());
		}
			//query = query.equals("id", this.findExonIdByCdna_position(criteria.getCdnaPosition()));
		if (criteria.getExonId() != null)
			query = query.equals(Exon.ID, criteria.getExonId());
		if (criteria.getGdnaPosition() != null)
		{
			query = query.lessOrEqual(Exon.GDNA_POSITION, criteria.getGdnaPosition());
			query = query.greaterOrEqual("gdna_position + length - 1", criteria.getGdnaPosition());
		}
			//query = query.equals("id", this.findExonIdByGdna_position(criteria.getGdnaPosition()));
		if (criteria.getNumber() != null)
			query = query.equals(Exon.NUMBER_, criteria.getNumber());
		if (criteria.getPosition() != null)
		{
//			logger.debug(">>> findExons: position==" + criteria.getPosition());
			RE reExon   = new RE("^(\\d+)$");
			RE reIntron = new RE("^(\\d+)([+-])(\\d+)$");
			
			if (reExon.match(criteria.getPosition()))
			{
				query = query.lessOrEqual(Exon.CDNA_POSITION, Integer.valueOf(criteria.getPosition()));
				query = query.greaterOrEqual("cdna_position + length - 1", Integer.valueOf(criteria.getPosition()));
				//return this.findExonIdByCdna_position(Integer.valueOf(criteria.getPosition()));
			}
			else if (reIntron.match(criteria.getPosition()))
			{
				// position is: 1234 + 5 (intron)
				Integer exonId = this.findExonIdByCdna_position(Integer.valueOf(reIntron.getParen(1)));
				Exon exon      = this.db.findById(Exon.class, exonId);
//				logger.debug("exon==" + exon.getName());
				Integer gDNA_position = exon.getGdna_Position();
				if (reIntron.getParen(2).equals("+"))
				{
					// Intron right (downstream) to this exon
//					logger.debug(">>> findExons: gdnaPostion==" + gDNA_position + ", len==" + exon.getLength() + ", pos==" + reIntron.getParen(3));
					query = query.lessOrEqual("gdna_position - length", gDNA_position - exon.getLength() - Integer.valueOf(reIntron.getParen(3)));
					query = query.greaterOrEqual(Exon.GDNA_POSITION, gDNA_position - exon.getLength() - Integer.valueOf(reIntron.getParen(3)));
					//return this.findExonIdByGdna_position(gDNA_position - exon.getLength() - Integer.valueOf(reIntron.getParen(3)));
				}
				else if (reIntron.getParen(2).equals("-"))
				{
					// Intron left (upstream) to this exon
					query = query.lessOrEqual("gdna_position - length", gDNA_position + Integer.valueOf(reIntron.getParen(3)));
					query = query.greaterOrEqual(Exon.GDNA_POSITION, gDNA_position + Integer.valueOf(reIntron.getParen(3)));
					//return this.findExonIdByGdna_position(gDNA_position + Integer.valueOf(reIntron.getParen(3)));
				}
				else
					throw new RESyntaxException("Invalid mutation notation: " + criteria.getPosition());
			}
			else
				throw new RESyntaxException("Invalid mutation notation: " + criteria.getPosition());
		}
		if (criteria.getProteinDomainId() != null)
			query = query.equals(Exon.PROTEINDOMAIN, criteria.getProteinDomainId()).sortASC("gdna_position");
		if (criteria.getIsIntron() != null)
			query = query.equals(Exon.ISINTRON, criteria.getIsIntron() ? 1 : 0); // TODO: remove db specific!!!!

		List<Exon> exons = query.find();
		
		return this.toExonSummaryVOList(exons);
	}

	public Exon findFirstExon(ExonSearchCriteriaVO criteria) throws DatabaseException, ParseException
	{
		if (criteria.getGdnaPosition() == null)
			return null;

		Query<Exon> query = this.db.query(Exon.class);

		if (criteria.getIsIntron() != null)
			query = query.equals(Exon.ISINTRON, criteria.getIsIntron());
		
		if ("F".equals(criteria.getOrientation()))
			query = query.sortASC(Exon.GDNA_POSITION);
		else if ("R".equals(criteria.getOrientation()))
			query = query.sortDESC(Exon.GDNA_POSITION);
		else
			return null;

		List<Exon> exons = query.limit(1).find();
		
		if (exons.size() > 0)
			return exons.get(0);
		else
			return null;
	}

	public Exon findPrevExon(ExonSearchCriteriaVO criteria) throws DatabaseException, ParseException
	{
		if (criteria.getGdnaPosition() == null)
			return null;

		Query<Exon> query = this.db.query(Exon.class);

		if (criteria.getIsIntron() != null)
			query = query.equals(Exon.ISINTRON, criteria.getIsIntron());
		
		if ("F".equals(criteria.getOrientation()))
			query = query.lt(Exon.GDNA_POSITION, criteria.getGdnaPosition()).sortDESC(Exon.GDNA_POSITION);
		else if ("R".equals(criteria.getOrientation()))
			query = query.gt(Exon.GDNA_POSITION, criteria.getGdnaPosition()).sortASC(Exon.GDNA_POSITION);
		else
			return null;
		
		List<Exon> exons = query.limit(1).find();
		
		if (exons.size() > 0)
			return exons.get(0);
		else
			return null;
	}

	public Exon findNextExon(ExonSearchCriteriaVO criteria) throws DatabaseException, ParseException
	{
		if (criteria.getGdnaPosition() == null)
			return null;

		Query<Exon> query = this.db.query(Exon.class);

		if (criteria.getIsIntron() != null)
			query = query.equals(Exon.ISINTRON, criteria.getIsIntron());

		if ("F".equals(criteria.getOrientation()))
			query = query.gt(Exon.GDNA_POSITION, criteria.getGdnaPosition()).sortASC(Exon.GDNA_POSITION);
		else if ("R".equals(criteria.getOrientation()))
			query = query.lt(Exon.GDNA_POSITION, criteria.getGdnaPosition()).sortDESC(Exon.GDNA_POSITION);
		else
			return null;

		List<Exon> exons = query.limit(1).find();
		
		if (exons.size() > 0)
			return exons.get(0);
		else
			return null;
	}

	public Exon findLastExon(ExonSearchCriteriaVO criteria) throws DatabaseException, ParseException
	{
		if (criteria.getGdnaPosition() == null)
			return null;

		Query<Exon> query = this.db.query(Exon.class);

		if (criteria.getIsIntron() != null)
			query = query.equals(Exon.ISINTRON, criteria.getIsIntron());
		
		if ("F".equals(criteria.getOrientation()))
			query = query.sortDESC(Exon.GDNA_POSITION);
		else if ("R".equals(criteria.getOrientation()))
			query = query.sortASC(Exon.GDNA_POSITION);
		else
			return null;

		List<Exon> exons = query.limit(1).find();
		
		if (exons.size() > 0)
			return exons.get(0);
		else
			return null;
	}

	private ExonSummaryVO toExonSummaryVO(Exon exon) throws DatabaseException, ParseException
	{
		ExonSummaryVO exonSummaryVO = new ExonSummaryVO();
		exonSummaryVO.setExon(exon);
		exonSummaryVO.setNumFullAminoAcids(this.getNumFullAminoAcids(exon));
		exonSummaryVO.setNumPartAminoAcids(this.getNumPartAminoAcids(exon));
		exonSummaryVO.setNumGlyXYRepeats(this.getNumGlyXYRepeats(exon));
		exonSummaryVO.setMultiple3Nucl(exon.getLength() % 3 == 0 ? true : false);
		exonSummaryVO.setNuclSequenceFlankLeft(this.getNuclSequenceFlankLeft(exon));
		exonSummaryVO.setNuclSequenceFlankRight(this.getNuclSequenceFlankRight(exon));
		exonSummaryVO.setNuclSequence(this.getNuclSequence(exon));
		exonSummaryVO.setAaSequence(this.getAaSequence(exon));
		
		Gene gene = this.db.findById(Gene.class, exon.getGene_Id());

		exonSummaryVO.setOrientation(gene.getOrientation());

		ExonSearchCriteriaVO firstExonCriteria = new ExonSearchCriteriaVO();
		firstExonCriteria.setGdnaPosition(exonSummaryVO.getExon().getGdna_Position());
		firstExonCriteria.setOrientation(gene.getOrientation());
		exonSummaryVO.setFirstExon(exonService.findFirstExon(firstExonCriteria));

		ExonSearchCriteriaVO prevExonCriteria = new ExonSearchCriteriaVO();
		prevExonCriteria.setGdnaPosition(exonSummaryVO.getExon().getGdna_Position());
		prevExonCriteria.setOrientation(gene.getOrientation());
		exonSummaryVO.setPrevExon(exonService.findPrevExon(prevExonCriteria));
		
		ExonSearchCriteriaVO nextExonCriteria = new ExonSearchCriteriaVO();
		nextExonCriteria.setGdnaPosition(exonSummaryVO.getExon().getGdna_Position());
		nextExonCriteria.setOrientation(gene.getOrientation());
		exonSummaryVO.setNextExon(exonService.findNextExon(nextExonCriteria));

		ExonSearchCriteriaVO lastExonCriteria = new ExonSearchCriteriaVO();
		lastExonCriteria.setGdnaPosition(exonSummaryVO.getExon().getGdna_Position());
		lastExonCriteria.setOrientation(gene.getOrientation());
		exonSummaryVO.setLastExon(exonService.findLastExon(lastExonCriteria));

		return exonSummaryVO;
	}

	private List<ExonSummaryVO> toExonSummaryVOList(List<Exon> exons) throws DatabaseException, ParseException
	{
		List<ExonSummaryVO> result = new ArrayList<ExonSummaryVO>();

		for (Exon exon : exons)
			result.add(this.toExonSummaryVO(exon));
		
		return result;
	}

	private Integer findExonIdByCdna_position(Integer cdna_position) throws DatabaseException
	{
//		List<Tuple> result = this.db.sql(String.format("SELECT id FROM Exon WHERE cdna_position <= %d AND %d <= cdna_position + length - 1", cdna_position, cdna_position));
//		// Should be only one result if exons have been entered correctly
//		return result.get(0).getInt("id");
		
		
		javax.persistence.Query q = this.db.getEntityManager().createNativeQuery(String.format("SELECT id FROM Exon WHERE cdna_position <= %d AND %d <= cdna_position + length - 1", cdna_position, cdna_position));
		return (Integer) q.getSingleResult();
	}

	private Integer findExonIdByGdna_position(Integer gdna_position) throws DatabaseException
	{
//		List<Tuple> result = this.db.sql(String.format("SELECT id FROM Exon WHERE (gdna_position - length) <= %d AND %d <= gdna_position", gdna_position, gdna_position));
//		// Should be only one result if exons and introns have been entered correctly
//		return result.get(0).getInt("id");
		
		
		javax.persistence.Query q = this.db.getEntityManager().createNativeQuery(String.format("SELECT id FROM Exon WHERE (gdna_position - length) <= %d AND %d <= gdna_position", gdna_position, gdna_position));
		return (Integer) q.getSingleResult();
	}

	public Integer findExonIdByPosition(String position) throws DatabaseException, RESyntaxException
	{
		RE reExon   = new RE("^([0-9]+)$");
		RE reIntron = new RE("^([0-9]+)([+-])([0-9])$");
		
		if (reExon.match(position))
			// position is: 1234 (exon)
			return this.findExonIdByCdna_position(Integer.valueOf(position));
		else if (reIntron.match(position))
		{
			// position is: 1234 + 5 (intron)
			Integer exonId = this.findExonIdByCdna_position(Integer.valueOf(reIntron.getParen(1)));
			Exon exon      = this.db.findById(Exon.class, exonId);
			logger.debug("exon==" + exon.getName());
			Integer gDNA_position = exon.getGdna_Position();
			if (reIntron.getParen(2).equals("+"))
				// Intron right (downstream) to this exon
				return this.findExonIdByGdna_position(gDNA_position - exon.getLength() - Integer.valueOf(reIntron.getParen(3)));
			else if (reIntron.getParen(2).equals("-"))
				// Intron left (upstream) to this exon
				return this.findExonIdByGdna_position(gDNA_position + Integer.valueOf(reIntron.getParen(3)));
			else
				throw new RESyntaxException("Invalid mutation notation: " + position);
		}
		else
			throw new RESyntaxException("Invalid mutation notation: " + position);
	}

	//TODO:Danny: Use or loose
//	private List<Exon> findExonsByNumber(Integer number) throws DatabaseException
//	{
//		Exon search = new Exon();
//		search.setNumber(number);
//		return this.db.findByExample(search);
//	}


	public List<Exon> findExonsByProteinDomainId(Integer proteinDomainId, Boolean noIntrons) throws DatabaseException, ParseException
	{
		Query<Exon> query =
			this.db.query(Exon.class)
			.equals(Exon.PROTEINDOMAIN, proteinDomainId)
			.sortASC(Exon.GDNA_POSITION);
		if (noIntrons)
			query.equals(Exon.ISINTRON, false);
		return query.find();
	}

	/**
	 * Get all exons sorted by their gDNA position
	 * @return exons
	 * @throws ParseException 
	 * @throws DatabaseException
	 */
	public List<Exon> getAllExons() throws DatabaseException, ParseException
	{
		return this.db.query(Exon.class).sortASC(Exon.GDNA_POSITION).find();
	}

	//TODO:Danny: Use or loose
//	private List<Exon> findExonsByIsIntron(Boolean isIntron) throws DatabaseException, ParseException
//	{
//		Query<Exon> query =
//			this.db.query(Exon.class).equals(Exon.ISINTRON, isIntron).sortDESC(Exon.GDNA_POSITION);
//
//		return query.find();
//	}

	/**
	 * Get exons and/or introns depending on flag noIntrons
	 * @param noIntrons
	 * @return exons
	 * @throws Exception
	 */
	//TODO:Danny: Use or loose
//	private List<Exon> getExons(Boolean noIntrons) throws Exception
//	{
//		Query<Exon> query =
//			this.db.query(Exon.class)
//			.sortDESC(Exon.GDNA_POSITION);
//		if (noIntrons)
//			query.equals(Exon.ISINTRON, false);
//		return query.find();
//	}

	/**
	 * Get the amino acid sequence of a given exon
	 * @param exon
	 * @return Amino acid sequence
	 * @throws DatabaseException 
	 * @throws Exception
	 */
	private String getAaSequence(Exon exon) throws DatabaseException
	{
		if (exon.getIsIntron())
			return "";

		MutationGene gene   = this.db.findById(MutationGene.class, exon.getGene_Id());
		Integer cdnaStart = Math.abs(exon.getCdna_Position() - 1);
		Integer cdnaEnd   = cdnaStart + exon.getLength();
		
		return StringUtils.substring(gene.getAaSequence(), cdnaStart, cdnaEnd);
	}

	/**
	 * Get the number of amino acids in given exon, excluding the ones only partially present
	 * @param exon
	 * @return Number of amino acids
	 * @throws DatabaseException 
	 * @throws Exception
	 */
	private Integer getNumFullAminoAcids(Exon exon) throws DatabaseException
	{
		return SequenceUtils.getNumFullAminoAcids(this.getAaSequence(exon));
	}
	
	/**
	 * Get the number of amino acids in given exon, including the ones only partially present
	 * @param exon
	 * @return Number of amino acids
	 * @throws DatabaseException 
	 * @throws Exception
	 */
	private Integer getNumPartAminoAcids(Exon exon) throws DatabaseException
	{
		return SequenceUtils.getNumPartAminoAcids(this.getAaSequence(exon));
	}
	
	/**
	 * Get the nucleotide sequence of a given exon
	 * @param exon
	 * @return Nucleotide sequence
	 * @throws DatabaseException 
	 */
	private String getNuclSequence(Exon exon) throws DatabaseException
	{
		MutationGene gene     = this.db.findById(MutationGene.class, exon.getGene_Id());
		Integer gdnaStart = Math.abs(exon.getGdna_Position() - gene.getBpStart().intValue());
		Integer gdnaEnd   = gdnaStart + exon.getLength();
		return StringUtils.substring(gene.getSeq(), gdnaStart, gdnaEnd);
	}

	/**
	 * Get the left flanking nucleotide sequence of given exon
	 * @param exon
	 * @return Left flanking sequence 
	 * @throws DatabaseException 
	 */
	private String getNuclSequenceFlankLeft(Exon exon) throws DatabaseException
	{
		MutationGene gene    = this.db.findById(MutationGene.class, exon.getGene_Id());
		Integer gdnaStart  = Math.abs(exon.getGdna_Position() - gene.getBpStart().intValue());
		Integer flankEnd   = Math.abs(gdnaStart);
		Integer flankStart = Math.abs(flankEnd - 10);
		// TODO: hardcoded length: fix this!
//		if (exon.getNumber_() == 1)
//			flankStart = Math.abs(flankEnd - 108);

		return StringUtils.substring(gene.getSeq(), flankStart, flankEnd);
	}

	/**
	 * Get the right flanking nucleotide sequence of given exon
	 * @param exon
	 * @return Right flanking sequence
	 * @throws DatabaseException 
	 */
	private String getNuclSequenceFlankRight(Exon exon) throws DatabaseException
	{
		MutationGene gene    = this.db.findById(MutationGene.class, exon.getGene_Id());
		Integer gdnaStart  = Math.abs(exon.getGdna_Position() - gene.getBpStart().intValue());
		Integer gdnaEnd    = gdnaStart + exon.getLength();
		Integer flankStart = gdnaEnd;
		Integer flankEnd   = flankStart + 10;
		// TODO: hardcoded length: fix this!
//		if (exon.getNumber_() == 118)
//			flankEnd = flankStart + 333;

		return StringUtils.substring(gene.getSeq(), flankStart, flankEnd);
	}
	
	/**
	 * Get the number of Gly-X-Y repeats, i.e. the number of GlyXxxYyy repeats.
	 * @param exon
	 * @return number of Gly-X-Y repeats
	 * @throws DatabaseException 
	 * @throws Exception
	 */
	private Integer getNumGlyXYRepeats(Exon exon) throws DatabaseException
	{
		return SequenceUtils.getNumGlyXYRepeats(this.getAaSequence(exon));
	}
}
