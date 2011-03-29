package org.molgenis.mutation.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Exon_ProteinDomain;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.ProteinDomain;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;

import app.JDBCDatabase;

public class ProteinDomainService
{
	private JDBCDatabase db                           = null;
	private static ProteinDomainService domainService = null;
	//TODO:Danny: Use or loose
	/*private static final transient Logger logger      = Logger.getLogger(JDBCConnectionHelper.class.getSimpleName());*/

	// private constructor, use singleton instance
	private ProteinDomainService(Database db)
	{
		this.db = (JDBCDatabase) db;
	}
	
	public static ProteinDomainService getInstance(Database db)
	{
		if (domainService == null)
			domainService = new ProteinDomainService(db);
		
		return domainService;
	}

	/**
	 * Find a protein domain by a mutation
	 * @param mutation
	 * @return protein domain
	 * @throws DatabaseException 
	 * @throws ParseException 
	 * @throws Exception
	 */
	public ProteinDomain findProteinDomain(Mutation mutation) throws DatabaseException, ParseException
	{
		Exon exon                   = this.db.findById(Exon.class, mutation.getExon());
		Integer proteinDomainId     = this.db.query(Exon_ProteinDomain.class).equals(Exon_ProteinDomain.EXON, exon.getId()).find().get(0).getProteinDomain_Id();
		ProteinDomain proteinDomain = this.db.findById(ProteinDomain.class, proteinDomainId);
		return proteinDomain;
	}

//	/**
//	 * Find a protein domain by its id
//	 * @param id
//	 * @return protein domain
//	 * @throws Exception
//	 */
//	public ProteinDomain findProteinDomain(Integer id) throws Exception
//	{
//		return this.db.findById(ProteinDomain.class, id);
//	}

	/**
	 * Find a protein domain by its id
	 * @param id
	 * @param noIntrons
	 * @return protein domain
	 * @throws Exception
	 */
	public ProteinDomainSummaryVO findProteinDomain(Integer id, Boolean noIntrons) throws Exception
	{
		return this.toProteinDomainSummaryVO(this.db.findById(ProteinDomain.class, id), noIntrons);
	}

	/**
	 * Get all protein domains
	 * @return protein domains
	 * @throws ParseException 
	 * @throws DatabaseException 
	 */
	public List<ProteinDomainSummaryVO> getAllProteinDomains() throws DatabaseException, ParseException
	{
		return this.toProteinDomainSummaryVOList(this.db.query(ProteinDomain.class).find());
	}
	
	private ProteinDomainSummaryVO toProteinDomainSummaryVO(ProteinDomain proteinDomain, Boolean noIntrons) throws DatabaseException, ParseException
	{
		ProteinDomainSummaryVO proteinDomainSummaryVO = new ProteinDomainSummaryVO();
		proteinDomainSummaryVO.setProteinDomain(proteinDomain);
		
		ExonService exonService = ExonService.getInstance(this.db);
		proteinDomainSummaryVO.setExons(exonService.findExonsByProteinDomainId(proteinDomain.getId(), noIntrons));
//		proteinDomainSummaryVO.setExons(exonService.findExonsByProteinDomainId(null, noIntrons));
		proteinDomainSummaryVO.setAllExons(exonService.getAllExons());
		
		return proteinDomainSummaryVO;
	}
	
	private List<ProteinDomainSummaryVO> toProteinDomainSummaryVOList(List<ProteinDomain> proteinDomains) throws DatabaseException, ParseException
	{
		List<ProteinDomainSummaryVO> result = new ArrayList<ProteinDomainSummaryVO>();

		for (ProteinDomain proteinDomain : proteinDomains)
			result.add(this.toProteinDomainSummaryVO(proteinDomain, true));
		
		return result;
	}
}
