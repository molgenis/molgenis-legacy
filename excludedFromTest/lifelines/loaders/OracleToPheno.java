/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifelines.loaders;

import app.JpaDatabase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

/**
 *
 * @author jorislops
 */
public class OracleToPheno {
    private String sql = "SELECT * FROM LLPOPER.%s WHERE rownum < 10";
    private String tableName = "LL_DATASET9";
    private String investigationName = tableName;
    
    public static void main(String[] args) throws Exception {
        new OracleToPheno();
    }

    public OracleToPheno() throws Exception {
        Connection con = LoaderUtils.getConnection();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(String.format(sql, tableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        
        JpaDatabase db = new JpaDatabase();
        EntityManager em = db.getEntityManager();
        
        Investigation investigation = em.createQuery("SELECT Inv FROM Investigation Inv WHERE Inv.name = :name", Investigation.class)
                .setParameter("name", investigationName)
                .getSingleResult();
        
        List<Measurement> measurements = new ArrayList<Measurement>();
        for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
            String columnName = rsmd.getColumnName(i);        
            Measurement m = em.createQuery("SELECT m FROM Measurement m WHERE m.name = :name AND m.investigation = :investigation", Measurement.class)
              .setParameter("name", columnName)
              .setParameter("investigation", investigation)
              .getSingleResult();
            measurements.add(m);
        }
        
        
        while(rs.next()) {
            ObservationElement target = null;
        
            em.getTransaction().begin();
            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
                Object value = rs.getObject(i);
                if(i == 1) {
                    target = new ObservationElement();
                    target.setInvestigation(investigation);
                    target.setName(value.toString());
                    em.persist(target);
                }                
                
                ObservedValue ov = new ObservedValue();
                ov.setFeature(measurements.get(i-1));
                if(value != null) {
                    ov.setValue(value.toString());
                } 
                ov.setInvestigation(investigation);
                ov.setTarget(target);
                if(value != null) {
                    em.persist(ov);
                }
            }
            em.getTransaction().commit();
        }
        
    }
}