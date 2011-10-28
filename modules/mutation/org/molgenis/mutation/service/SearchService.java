package org.molgenis.mutation.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.ProteinDomain;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;

public class SearchService implements Serializable
{
	private static final long serialVersionUID = 6768968931746646894L;
	private Database db;
	
	public SearchService()
	{
	}

	public void setDatabase(Database db)
	{
		this.db = db;
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
	 * Get all phenotypes sorted by their name
	 * @return phenotypes
	 * @throws DatabaseException
	 */
	public List<MutationPhenotype> getAllPhenotypes() throws DatabaseException
	{
		return this.db.query(MutationPhenotype.class).sortASC(MutationPhenotype.NAME).find();
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

		ExonService exonService = new ExonService();
		exonService.setDatabase(db);
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
}
