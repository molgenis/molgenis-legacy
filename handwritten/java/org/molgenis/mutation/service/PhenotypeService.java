package org.molgenis.mutation.service;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.Query;
import org.molgenis.mutation.MutationPhenotype;
import org.molgenis.mutation.PhenotypeDetails;
import org.molgenis.mutation.vo.PhenotypeSearchCriteriaVO;

public class PhenotypeService implements Serializable
{
	private static final long serialVersionUID       = -476712719557155269L;
	private static PhenotypeService phenotypeService = null;
	private Database db                              = null;
	
	// private constructor, use singleton instance
	private PhenotypeService(Database db)
	{
		this.db = db;
	}
	
	public static PhenotypeService getInstance(Database db)
	{
		if (phenotypeService == null)
			phenotypeService = new PhenotypeService(db);
		
		return phenotypeService;
	}

	public List<MutationPhenotype> getAllPhenotypes() throws DatabaseException, ParseException
	{
		return this.db.query(MutationPhenotype.class).sortASC(MutationPhenotype.NAME).find();
	}

	/**
	 * Find phenotypes according to search criteria
	 * @param criteria
	 * @return list of phenotypes
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public List<MutationPhenotype> findPhenotypes(PhenotypeSearchCriteriaVO criteria) throws DatabaseException, ParseException
	{
		Query<MutationPhenotype> query = this.db.query(MutationPhenotype.class);

		if (criteria.getPhenotypeId() != null)
			query = query.eq(MutationPhenotype.ID, criteria.getPhenotypeId());
		if (criteria.getName() != null)
			query = query.equals(MutationPhenotype.NAME, criteria.getName());

		List<MutationPhenotype> phenotypes = query.find();
		
		return phenotypes;
	}

	/**
	 * Find a phenotype by its id
	 * @param id
	 * @return phenotype
	 * @throws DatabaseException 
	 */
	public MutationPhenotype findPhenotypeById(Integer id) throws DatabaseException
	{
		return this.db.findById(MutationPhenotype.class, id);
	}
	
	/**
	 * Find phenotype details by their id (one record with details)
	 * @param id
	 * @return phenotype details
	 * @throws DatabaseException
	 */
	public PhenotypeDetails findPhenotypeDetailsById(Integer id) throws DatabaseException
	{
		return this.db.findById(PhenotypeDetails.class, id);
	}
}
