package org.molgenis.mutation.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Exon_ProteinDomain;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.ProteinDomain;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;

public class ProteinDomainService implements Serializable
{
	private static final long serialVersionUID        = -9009198952894773375L;
	private Database db                               = null;
	private static ProteinDomainService domainService = null;

	// private constructor, use singleton instance
	private ProteinDomainService(Database db)
	{
		this.db = db;
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

		return proteinDomainSummaryVO;
	}
	
	private List<ProteinDomainSummaryVO> toProteinDomainSummaryVOList(List<ProteinDomain> proteinDomains) throws DatabaseException, ParseException
	{
		List<ProteinDomainSummaryVO> result = new ArrayList<ProteinDomainSummaryVO>();

		for (ProteinDomain proteinDomain : proteinDomains)
			result.add(this.toProteinDomainSummaryVO(proteinDomain, true));
		
		return result;
	}

	public void reverseExons(List<ProteinDomainSummaryVO> proteinDomainList)
	{
		System.out.println(">>> Reversing exons.");
		Iterator<ProteinDomainSummaryVO> it = proteinDomainList.iterator();
		while (it.hasNext())
		{
			ProteinDomainSummaryVO proteinDomainSummaryVO = it.next();
			this.reverseExons(proteinDomainSummaryVO);
//			Collections.reverse(proteinDomainSummaryVO.getAllExons());
//			System.out.println(">>> inside: exons==" + proteinDomainSummaryVO.getExons());
//			Collections.reverse(proteinDomainSummaryVO.getExons());
//			System.out.println(">>> inside: exons==" + proteinDomainSummaryVO.getExons());
		}
	}
	
	public void reverseExons(ProteinDomainSummaryVO proteinDomainSummaryVO)
	{
//		Collections.reverse(proteinDomainSummaryVO.getAllExons());
		Collections.reverse(proteinDomainSummaryVO.getExons());
	}
}
