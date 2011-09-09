package org.molgenis.lifelines.loaders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.pheno.Measurement;

import app.DatabaseFactory;

/**
 * Creates an View on top of EAV table to reconstruct original table,
 * This is useful for querying & testing data in original format!
 * @author joris lops
 */
public class EAVToView {
	private static Logger log = Logger.getLogger(EAVToView.class);
	
    private final String schemaName;
    private final String tableName;
    private final String[] fields;
    private final int investigationId;
    private final String schemaToExportView;
    //private final String databaseTarget;
    private final int protocolId;
    private final int studyId;
    
    public EAVToView(int studyId, String schemaName, String tableName, String fields, String schemaToExportView, int protocolId, int investigationId) throws DatabaseException, Exception {
    	this.studyId = studyId; 
    	this.schemaName = schemaName;
    	this.tableName = tableName;
    	this.schemaToExportView = schemaToExportView;
    	this.fields = fields.split(",");
    	this.protocolId = protocolId;
//    	this.databaseTarget = databaseTarget;
    	this.investigationId = investigationId;
    	load();
    }    
    
    private void load() throws DatabaseException, Exception {
        Database db = DatabaseFactory.create();
        EntityManager em = db.getEntityManager();
            
        String column = "max(case when feature = %d then %s end) as %s \n";
                
        StringBuilder query = new StringBuilder("SELECT ");    
        
        List<Measurement> measurements = em.createQuery("SELECT m FROM Measurement m where m.name IN (:measurementNames)", Measurement.class)
        	.setParameter("measurementNames", Arrays.asList(fields))
        	.getResultList();
        	
        	//LoaderUtils.getMeasurementsByInvestigationId(investigationId, em, this.schemaName, this.tableName);
        for(int i = 0; i < measurements.size(); ++i) {
            Measurement m = measurements.get(i);
            String castPart = LoaderUtils.getCast(m.getDataType());
//            if(databaseTarget.equals("mysql")) {
//            	if(castPart.contains("number")) {
//            		castPart = castPart.replace("number", "DECIMAL");
//            	} else if(castPart.contains("to_date")) {
//            		castPart = "CAST(substr(value,1, 19) AS DATETIME)";
//            	}
//            }
            query.append(String.format(column, m.getId(), String.format(castPart, "value"), m.getName()));
            if(i + 1 < measurements.size()) {
                query.append(",");
            }
        }        
        query.append(String.format(" FROM \n observedvalue \n WHERE investigation = %d AND protocolId = %d \n GROUP BY recordId", investigationId, protocolId));
        String viewQuery = "";
    	String tempTableName = (schemaToExportView != null) ? "" + schemaToExportView + "." : "";
    	tempTableName += tableName;        
//        if(databaseTarget.equals("mysql")) {
//        	viewQuery = String.format("CREATE TABLE %s %S", tempTableName, query.toString()); 
//        } else {
        	//String materializedView = "CREATE MATERIALIZED VIEW %s NOCACHE NOPARALLEL BUILD IMMEDIATE USING INDEX REFRESH ON DEMAND COMPLETE DISABLE QUERY REWRITE AS %s";
        	String view = "CREATE VIEW %s AS %s";
        	viewQuery = String.format(view, tempTableName, query.toString());;
//        }
        
        	
//        Object result = db.getEntityManager().createNativeQuery(viewQuery).getResultList();
//        if(result != null)
//        	System.out.println("view created");
//        else
//        	System.out.println("view created failed!");
        
        
        Session s = db.getEntityManager().unwrap(Session.class);
        Transaction t = s.beginTransaction();
//        if(databaseTarget.equals("mysql")) {
//        	s.createSQLQuery(String.format("DROP TABLE IF EXISTS %s", tableName)).executeUpdate();
//        } 
//        else //oracle 
//        {
        	String viewExists = "select count(*) "
            					+"from user_objects "
            					+"where object_type = 'VIEW' "
            				    +"and object_name = :viewName";
        	log.debug(String.format("[%d-%s] %s", studyId, tableName, viewExists));
        	
        	//figure out if it exists first
        	int viewCount = ((BigDecimal)em.createNativeQuery(viewExists)
        		.setParameter("viewName", tableName)
        		.getSingleResult()).intValue();
        	if(viewCount == 1) {
        		s.createSQLQuery(String.format("DROP VIEW %s", tableName)).executeUpdate();	
        	}
        	log.debug(String.format("[%d-%s] %s", studyId, tableName, viewQuery.toString()));
        	s.createSQLQuery(viewQuery).executeUpdate();
//        }        
        t.commit();
        
        
    }
}
