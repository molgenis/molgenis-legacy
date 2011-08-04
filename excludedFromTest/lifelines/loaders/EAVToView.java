/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifelines.loaders;

import app.JpaDatabase;
import java.util.List;
import javax.persistence.EntityManager;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;

/**
 *
 * @author jorislops
 */
public class EAVToView {
    private final String schemaName = "LLPOPER";
    private final String tableName = "LL_DATASET9";
    private final int investigationId = 1;
    
    private final String databaseTarget = "mysql";
    
    public static void main(String[] args) throws Exception {
        new EAVToView();
    }

    public EAVToView() throws DatabaseException, Exception {
        JpaDatabase db = new JpaDatabase();
        EntityManager em = db.getEntityManager();
            
        String column = "max(case when feature = %d then %s end) as %s \n";
                
        StringBuilder query = new StringBuilder("SELECT ");    
        List<Measurement> measurements = LoaderUtils.getMeasurementsByInvestigationId(investigationId, em, schemaName, tableName);
        for(int i = 0; i < measurements.size(); ++i) {
            Measurement m = measurements.get(i);
            String castPart = LoaderUtils.getCast(m.getDataType());
            if(databaseTarget.equals("mysql")) {
            	if(castPart.contains("number")) {
            		castPart = castPart.replace("number", "DECIMAL");
            	} else if(castPart.contains("to_date")) {
            		castPart = "CAST(substr(value,1, 19) AS DATETIME)";
            	}
            }
            query.append(String.format(column, m.getId(), String.format(castPart, "value"), m.getName()));
            if(i + 1 < measurements.size()) {
                query.append(",");
            }
        }        
        query.append(String.format(" FROM \n observedvalue \n WHERE investigation = %d \n GROUP BY target", investigationId));
        
        System.out.println(query.toString());
        
        
    
    }
    
    
}
