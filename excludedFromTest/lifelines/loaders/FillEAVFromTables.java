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
    	String investigation = "OV027";
    	String schemaName = "llpoper";
        String schemaToExportView = "testViews";
        String[] tableNames = new String[]{"OV027LABDATA", "LL_BLOEDDRUKAVG"};
        
        Investigation inv = new Investigation();
        inv.setName(String.format("%s %s",investigation, new Date().toString()));        
        
        Database db = DatabaseFactory.create();
        EntityManager em = db.getEntityManager();
        em.getTransaction().begin();
        em.persist(inv);
        em.getTransaction().commit();
        
        for(String tableName : tableNames) {
	    	OracleToLifelinesPheno oracleToLifelinesPheno = new OracleToLifelinesPheno(schemaName, tableName, inv.getId());
	        int investigationId = oracleToLifelinesPheno.getInvestigationId();
	        
	        OracleToPheno oracleToPheno = new OracleToPheno(tableName, investigationId);
	        
	        EAVToView eavToView = new EAVToView(schemaName, tableName, schemaToExportView, oracleToPheno.getProtocolId());
        }
        
        
        
    }
}
