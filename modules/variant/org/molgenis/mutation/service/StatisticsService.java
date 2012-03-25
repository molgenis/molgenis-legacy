package org.molgenis.mutation.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.jdbc.JDBCDatabase;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.pheno.Patient;
import org.molgenis.util.Tuple;
import org.molgenis.variant.SequenceCharacteristic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService extends MolgenisVariantService
{
	@Autowired
	public StatisticsService(Database db)
	{
		super(db);
	}

	/**
	 * Get number of mutations in the database
	 * 
	 * @return number of mutations
	 * @throws DatabaseException
	 */
	public int getNumMutations() throws DatabaseException
	{
		return this.db.count(SequenceCharacteristic.class);
	}
	
	/**
	 * Get number of mutations by pathogenicity
	 * @param pathogenicity
	 * @return number of mutations
	 * @throws DatabaseException
	 */
	public Map<String, Integer> getNumMutationsByPathogenicity() throws DatabaseException
	{
//		if (this.db instanceof JpaDatabase)
//		{
//			String sql            = "SELECT pathogenicity, COUNT(id) FROM Mutation GROUP BY pathogenicity";
//			List<Object[]> counts = this.db.getEntityManager().createNativeQuery(sql).getResultList();
//			
//			HashMap<String, Integer> result = new HashMap<String, Integer>();
//
//			for (Object[] entry : counts)
//				result.put((String) entry[0], Integer.parseInt(entry[1].toString()));
//
//			return result;
//		}
//		else
//			throw new DatabaseException("Unsupported database mapper");
		return new HashMap<String, Integer>();
	}

	/**
	 * Get number of patients in the database
	 * 
	 * @return number of patients
	 * @throws DatabaseException
	 */
	public int getNumPatients() throws DatabaseException
	{
		return this.db.count(Patient.class);
	}

	/**
	 * Get number of patients by type of mutation pathogenicity
	 * @param pathogenicity
	 * @return number of patients
	 * @throws DatabaseException
	 */
	public Map<String, Integer> getNumPatientsByPathogenicity()
	{
//		if (this.db instanceof JDBCDatabase)
//			throw new DatabaseException("Unsupported database mapper");
////			return ((JDBCDatabase) this.db).sql("SELECT DISTINCT p.id FROM Patient p LEFT JOIN Patient_mutations pm ON (p.id = pm.Patient) LEFT JOIN Mutation m ON (m.id = pm.mutations) WHERE m.pathogenicity = '" + pathogenicity + "'").size();
//		else if (this.db instanceof JpaDatabase)
//		{
//			String sql            = "SELECT m.pathogenicity, COUNT(DISTINCT p.id) FROM Patient p LEFT JOIN Patient_mutations pm ON (p.id = pm.Patient) LEFT JOIN Mutation m ON (m.id = pm.mutations) GROUP BY m.pathogenicity";
//			List<Object[]> counts = this.db.getEntityManager().createNativeQuery(sql).getResultList();
//			
//			HashMap<String, Integer> result = new HashMap<String, Integer>();
//
//			for (Object[] entry : counts)
//				result.put((String) entry[0], Integer.parseInt(entry[1].toString()));
//
//			return result;
//		}
//		else
//			throw new DatabaseException("Unsupported database mapper");
		return new HashMap<String, Integer>();
	}
	
	/*
	 * Get number of phenotypes
	 * @return hash with name of phenotypes as keys and counts of p henotypes as values
	 * @throws DatabaseException
	 */
	public HashMap<String, Integer> getPhenotypeCounts() throws DatabaseException
	{
		if (this.db instanceof JDBCDatabase)
		{
			List<Tuple> counts = ((JDBCDatabase) this.db).sql("SELECT v.value, COUNT(v.value) FROM ObservedValue v JOIN ObservationElement e ON (e.id = v.Feature) WHERE e.name = 'Phenotype' GROUP BY value");
			HashMap<String, Integer> result = new HashMap<String, Integer>();
		
			for (Tuple entry : counts)
				result.put(entry.getString(0), entry.getInt(1));

			return result;
		}
		else if (this.db instanceof JpaDatabase)
		{
			String sql                      = "SELECT v.value, COUNT(v.value) FROM ObservedValue v JOIN ObservationElement e ON (e.id = v.Feature) WHERE e.name = 'Phenotype' GROUP BY value";
			List<Object[]> counts           = this.db.getEntityManager().createNativeQuery(sql).getResultList();

			HashMap<String, Integer> result = new HashMap<String, Integer>();

			for (Object[] entry : counts)
				result.put((String) entry[0], Integer.valueOf(entry[1].toString()));

			return result;
		}
		else
			throw new DatabaseException("Unsupported database mapper");
	}

	/**
	 * Get number of unpublished patients.
	 * 
	 * @return number of unpublished patients
	 * @throws DatabaseException
	 * @throws ParseException
	 */
	public int getNumUnpublishedPatients() throws DatabaseException, ParseException
	{
		if (this.db instanceof JDBCDatabase)
			return ((JDBCDatabase) this.db).sql("SELECT DISTINCT id FROM Patient WHERE NOT EXISTS (SELECT id FROM Patient_patientreferences WHERE Patient.id = Patient_patientreferences.Patient)").size();
		else if (this.db instanceof JpaDatabase)
		{
			javax.persistence.Query q = this.db.getEntityManager().createNativeQuery("SELECT COUNT(DISTINCT p.id) FROM Patient p LEFT OUTER JOIN Patient_patientreferences pp ON (p.id = pp.Patient) WHERE pp.patientreferences IS NULL");
			return Integer.valueOf(q.getSingleResult().toString());
		}
		else
			throw new DatabaseException("Unsupported database mapper");
	}
}
