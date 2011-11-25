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
import org.apache.commons.lang.StringUtils;
import org.molgenis.organization.Investigation;
import org.molgenis.protocol.Protocol;

/**
 * Creates an View on top of EAV table to reconstruct original table,
 * This is useful for querying & testing data in original format!
 * @author joris lops
 */
public class EAVToView {
	private static Logger log = Logger.getLogger(EAVToView.class);
	
    private final String schemaName;
    private final String tableName;
    private final List<Measurement> fields;
    
    private final String schemaToExportView;
    private final Protocol protocol;
    private final Investigation investigation;
    private final int studyId;
    
    public EAVToView(int studyId, String schemaName, String tableName, List<Measurement> fields, 
                String schemaToExportView, Protocol protocol, Investigation investigation) throws DatabaseException, Exception {
    	this.studyId = studyId; 
    	this.schemaName = schemaName;
    	this.tableName = tableName.toUpperCase();
    	this.schemaToExportView = schemaToExportView;
    	this.fields = fields;
    	this.protocol = protocol;
//    	this.databaseTarget = databaseTarget;
    	this.investigation = investigation;
    	load();
    }    

    public static String createQuery(Investigation investigation, Protocol protocol, 
            List<Measurement> measurements, EntityManager em, LoaderUtils.eDatabase database) throws Exception {
        return createQuery(investigation, protocol, measurements, em, false, database);
    }    
    
    public static String createQuery(Investigation investigation, Protocol protocol, 
            List<Measurement> measurements, EntityManager em, boolean tableInAlias, LoaderUtils.eDatabase database) throws Exception {
        String column = "max(case when o.feature = %d then %s end) %s \n";
        StringBuilder query = new StringBuilder("SELECT ");    
//        List<Measurement> measurements = em.createQuery("SELECT m FROM Measurement m where m.name IN (:measurementNames) AND investigation.id = :invId", Measurement.class)
//        	.setParameter("measurementNames", Arrays.asList(fields))
//        	.setParameter("invId", investigationId)
//        	.getResultList();
            for(int i = 0; i < measurements.size(); ++i) {
                Measurement m = measurements.get(i);
                String castPart = LoaderUtils.getCast(m.getDataType(), database);
    //            if(databaseTarget.equals("mysql")) {
    //            	if(castPart.contains("number")) {
    //            		castPart = castPart.replace("number", "DECIMAL");
    //            	} else if(castPart.contains("to_date")) {
    //            		castPart = "CAST(substr(value,1, 19) AS DATETIME)";
    //            	}
    //            }
                
                String fieldAlias = null;
                if(tableInAlias) {
                    fieldAlias = String.format("%s_%s", protocol.getName(), m.getName());
                } else {
                    fieldAlias = String.format("%s", m.getName());
                }
                
                fieldAlias = StringUtils.substring(fieldAlias, 0, 30);
                
                //fieldAlias = StringUtils.substring(fieldAlias, 0, 28);                
                query.append(String.format(column, m.getId(), String.format(castPart, "value"), fieldAlias));
                if(i + 1 < measurements.size()) {
                    query.append(",");
                }
            }

        query.append(String.format(" FROM \n  observedvalue o join protocolapplication pa on (o.protocolapplication = pa.id) \n WHERE o.investigation = %d AND pa.protocol = %d \n GROUP BY o.protocolapplication", investigation.getId(), protocol.getId()));
        return query.toString();
    }
    
    private void load() throws DatabaseException, Exception {
    	Database db = DatabaseFactory.create();
    	EntityManager em = db.getEntityManager();
    	String query = createQuery(investigation, protocol, fields, em, false, LoaderUtils.eDatabase.ORACLE);
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
        	System.out.println(viewQuery);
        	s.createSQLQuery(viewQuery).executeUpdate();
//        }        
        t.commit();
        
        
    }
}
