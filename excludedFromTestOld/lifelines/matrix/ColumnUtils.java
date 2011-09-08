package lifelines.matrix;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;


import app.JpaDatabase;


public class ColumnUtils {
	//@Override
	public static List<String> getColumnGroups() throws DatabaseException {
		String ql = "SELECT DISTINCT SUBSTRING(oFeature.name, 1, LOCATE('.',oFeature.name) - 1) " +
		"FROM ObservableFeature oFeature";
		return (new JpaDatabase()).getEntityManager().createQuery(ql, String.class).getResultList();
	}
	
	//@Override
	public static List<String> getColumnsGroup(String columnGroupName) throws DatabaseException {
		String ql = "SELECT DISTINCT SUBSTRING(oFeature.name, LOCATE('.',oFeature.name) + 1) " +
		"FROM ObservableFeature oFeature " +
		"WHERE SUBSTRING(oFeature.name, 1, LOCATE('.',oFeature.name) - 1) = :featureGroupName";
		return (new JpaDatabase()).getEntityManager().createQuery(ql, String.class)
					.setParameter("featureGroupName", columnGroupName)
					.getResultList();
	}

	public static List<String> getColumnNames() throws DatabaseException {
		String ql = "SELECT DISTINCT SUBSTRING(oFeature.name, LOCATE('.',oFeature.name) + 1) " +
					"FROM ObservableFeature oFeature";
		return (new JpaDatabase()).getEntityManager().createQuery(ql, String.class)
					.getResultList();
	}
	
	public static List<String> getGroupsByName(String columnName) throws DatabaseException {
		String ql = "SELECT DISTINCT SUBSTRING(oFeature.name, 1, LOCATE('.',oFeature.name) - 1) " +
					"FROM ObservableFeature oFeature " +
					"WHERE SUBSTRING(oFeature.name, LOCATE('.',oFeature.name) + 1) = :columnName";
		
		return (new JpaDatabase()).getEntityManager().createQuery(ql, String.class)
		.setParameter("columnName", columnName)
		.getResultList();	
	}

    public static List<Investigation> getInvestigation() throws DatabaseException {
        String ql = "SELECT i FROM Investigation i";
        return (new JpaDatabase()).getEntityManager().createQuery(ql, Investigation.class).getResultList();
    }
    
    public static Measurement getMeasurementByName(Investigation investigation, String measurementName) throws DatabaseException {
    	String ql = "SELECT m FROM Measurement m WHERE m.investigation = :investigation AND m.name = :measurementName";
    	return (new JpaDatabase()).getEntityManager()
    			.createQuery(ql, Measurement.class)
    			.setParameter("investigation", investigation)
    			.setParameter("measurementName", measurementName)
    			.getSingleResult();
    }
    
    public static List<Measurement> getMeasurements(Investigation investigation) throws DatabaseException {
    	String ql = "SELECT m FROM Measurement m WHERE m.investigation = :investigation";
    	return (new JpaDatabase()).getEntityManager()
		.createQuery(ql, Measurement.class)
		.setParameter("investigation", investigation)
		.getResultList();    	
    }
    
	public static List<String> getColNames(List<Column> columns) {
		List<String> result = new ArrayList<String>();
		for(Column c : columns) {
			result.add(c.getName());
		}
		return result;
	}
    
}