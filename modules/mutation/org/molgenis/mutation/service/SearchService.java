package org.molgenis.mutation.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.mutation.Exon;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.ProteinDomain;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;
import org.molgenis.pheno.Measurement;
import org.molgenis.util.Tuple;

public class SearchService implements Serializable
{
	private static final long serialVersionUID = 6768968931746646894L;
	private Database db;
	private HashMap<Integer, ProteinDomainSummaryVO> cache = new HashMap<Integer, ProteinDomainSummaryVO>();
	
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
	public List<String> getAllPhenotypes() throws DatabaseException
	{
		List<Measurement> features = this.db.query(Measurement.class).equals(Measurement.NAME, "Phenotype").find();
		if (features.size() != 1)
			return new ArrayList<String>();

		if (this.db instanceof JDBCDatabase)
		{
			List<Tuple> values  = ((JDBCDatabase) this.db).sql("SELECT DISTINCT value FROM ObservedValue WHERE Feature = " + features.get(0).getId() + " ORDER BY value");
			List<String> result = new ArrayList<String>();
			for (Tuple value : values)
				result.add(value.getString(1));
			return result;
		}
		else if (this.db instanceof JpaDatabase)
		{
			javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT DISTINCT value FROM ObservedValue WHERE Feature = " + features.get(0).getId() + " ORDER BY value");
			return q.getResultList();
		}
		else
			throw new DatabaseException("Unsupported database mapper");
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
	 * @param orientation 
	 * @return protein domains
	 * @throws ParseException 
	 * @throws DatabaseException 
	 */
	public List<ProteinDomainSummaryVO> getAllProteinDomains() throws DatabaseException, ParseException
	{
		return this.toProteinDomainSummaryVOList(this.db.query(ProteinDomain.class).sortASC(ProteinDomain.NAME).find());
	}
	
	private ProteinDomainSummaryVO toProteinDomainSummaryVO(ProteinDomain proteinDomain, Boolean noIntrons) throws DatabaseException
	{
		if (this.cache.containsKey(proteinDomain.getId()))
			return this.cache.get(proteinDomain.getId());

		ProteinDomainSummaryVO proteinDomainSummaryVO = new ProteinDomainSummaryVO();
		proteinDomainSummaryVO.setProteinDomain(proteinDomain);

		List<Exon> exons = this.db.query(Exon.class).equals(Exon.PROTEINDOMAIN, proteinDomain.getId()).equals(Exon.ISINTRON, !noIntrons).sortASC(Exon.GDNA_POSITION).find();
		proteinDomainSummaryVO.setExons(exons);

		// cache value
		this.cache.put(proteinDomain.getId(), proteinDomainSummaryVO);

		return proteinDomainSummaryVO;
	}
	
	private List<ProteinDomainSummaryVO> toProteinDomainSummaryVOList(List<ProteinDomain> proteinDomains) throws DatabaseException
	{
		List<ProteinDomainSummaryVO> result = new ArrayList<ProteinDomainSummaryVO>();

		for (ProteinDomain proteinDomain : proteinDomains)
			result.add(this.toProteinDomainSummaryVO(proteinDomain, true));
		
		return result;
	}
}
