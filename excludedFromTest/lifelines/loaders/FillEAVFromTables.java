package lifelines.loaders;

import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.poi.hssf.util.HSSFColor.ORCHID;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.jpa.JpaDatabase;
import org.molgenis.organization.Investigation;

import app.DatabaseFactory;

public class FillEAVFromTables {
    public static void main(String[] args) throws Exception {
//    	String investigation = "OV027";
//    	String schemaName = "llpoper";
//        String schemaToExportView = null;
//        String[] tableNames = new String[]{"OV027LABDATA", "LL_BLOEDDRUKAVG"};
    	
    	String investigation = "Dataset9";
    	String schemaName = "llpoper";
        String schemaToExportView = null;
        String[] tableNames = new String[]{"LL_DATASET9"};    	
    	
        String databaseTarget = "oracle";
        
        Investigation inv = new Investigation();
        inv.setName(String.format("%s %s",investigation, new Date().toString()));        
        
        Database db = DatabaseFactory.create();
        EntityManager em = db.getEntityManager();
        em.getTransaction().begin();
        em.persist(inv);
        em.getTransaction().commit();
        
        long start = System.currentTimeMillis();
        
        for(String tableName : tableNames) {
	    	OracleToLifelinesPheno oracleToLifelinesPheno = new OracleToLifelinesPheno(schemaName, tableName, inv.getId());
	        
	        OracleToPheno oracleToPheno = new OracleToPheno(tableName, inv.getId());
	        
	        
	        EAVToView eavToView = new EAVToView(schemaName, tableName, schemaToExportView, oracleToPheno.getProtocolId(), databaseTarget, inv.getId());
        }
        
        long end = System.currentTimeMillis();
        
        System.out.println("Time: " + (start - end) / 1000);
        
        
    }
}
