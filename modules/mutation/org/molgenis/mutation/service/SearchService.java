package org.molgenis.mutation.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.mutation.Mutation;
import org.molgenis.mutation.ProteinDomain;
import org.molgenis.mutation.vo.ProteinDomainSummaryVO;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;

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
	public List<String> getAllPhenotypes() throws DatabaseException
	{
		List<Measurement> features = this.db.query(Measurement.class).equals(Measurement.NAME, "Phenotype").find();
		if (features.size() != 1)
			return new ArrayList<String>();

		List<ObservedValue> phenotypes         = this.db.query(ObservedValue.class).equals(ObservedValue.FEATURE, features.get(0).getId()).sortASC(ObservedValue.VALUE).find();
		HashMap<String, String> phenotypeNames = new HashMap<String, String>();
		for (ObservedValue phenotype : phenotypes)
			phenotypeNames.put(phenotype.getValue(), phenotype.getValue());
		return Arrays.asList(phenotypeNames.keySet().toArray(new String[0]));
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
	
	private ProteinDomainSummaryVO toProteinDomainSummaryVO(ProteinDomain proteinDomain, Boolean noIntrons) throws DatabaseException
	{
		ProteinDomainSummaryVO proteinDomainSummaryVO = new ProteinDomainSummaryVO();
		proteinDomainSummaryVO.setProteinDomain(proteinDomain);

		ExonService exonService = new ExonService();
		exonService.setDatabase(db);
		proteinDomainSummaryVO.setExons(exonService.findExonsByProteinDomainId(proteinDomain.getId(), noIntrons));

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
