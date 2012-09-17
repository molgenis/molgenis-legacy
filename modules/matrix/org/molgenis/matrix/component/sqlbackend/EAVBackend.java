package org.molgenis.matrix.component.sqlbackend;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;
import org.molgenis.protocol.Protocol;


/**
 * Backend on EAV model,
 * no tricks involved
 * @author joris lops
 */
public class EAVBackend {
    private final EntityManager em;
    
    public static void main(String[] args) {
        EntityManager em = Persistence.createEntityManagerFactory("molgenis").createEntityManager();
        
        new EAVBackend(em);
        
        em.close();
    }
    
    public EAVBackend(EntityManager em) {
        this.em = em;
        
        Protocol protocol = em.find(Protocol.class, 50);
        Measurement gewicht = em.find(Measurement.class, 51);
        Measurement lengte = em.find(Measurement.class, 53);
      
        List<Measurement> features = Arrays.asList(gewicht, lengte);

        MatrixQueryRule gewichtRule = new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, protocol.getId(), gewicht.getId(), Operator.EQUALS, "100.0");
        gewichtRule.setDimIndex(gewicht.getId());
        MatrixQueryRule lengteRule = new MatrixQueryRule(MatrixQueryRule.Type.colValueProperty, protocol.getId(), lengte.getId(), Operator.EQUALS, "141.8");
        lengteRule.setDimIndex(lengte.getId());
        
        List<MatrixQueryRule> rules = Arrays.asList(gewichtRule, lengteRule);
        
        String sql = limitOffset(features, lengte, rules, 10);
        
        List<Object[]> records = em.createNativeQuery(sql).getResultList();
        
        //protocolApplication
        ObservedValue[][] matrix = new ObservedValue[10][2];
        
        HashMap<Integer, Integer> featureIds = new HashMap<Integer, Integer>();
        featureIds.put(51, 0);
        featureIds.put(53, 1);
        
        int prevPA = -1;
        int iRow = -1;
        for(Object[] record : records) {
            ObservedValue ov = new ObservedValue();    
            ov.setTarget(((BigDecimal)record[0]).intValue());
            ov.setProtocolApplication(((BigDecimal)record[1]).intValue());
            ov.setFeature(((BigDecimal)record[2]).intValue());
            ov.setValue((String)record[3]);            
            
            if(prevPA != ov.getProtocolApplication_Id()) {
                ++iRow;
            }
            int iCol = featureIds.get(ov.getFeature_Id());
            
            matrix[iRow][iCol] = ov;
            
            prevPA = ov.getProtocolApplication_Id();            
        }
        
        for(int row = 0; row < matrix.length; ++row) {
            for(int col = 0; col < matrix[row].length; ++col) {
                System.out.print(matrix[row][col].getValue() + "\t");
            }
            System.out.println();
        }
    }

    
//SELECT target, protocolapplication, feature, value, 
//    CASE
//      WHEN feature = 51
//      THEN CAST(value AS NUMBER)
//    END AS sortCol 
//FROM observedvalue 
//WHERE protocolapplication IN (  
//  --limit and/or offset by x
//  SELECT protocolapplication 
//  FROM (
//      orderBy(....)
//  )
//  --limit and offset, 
//  WHERE rownum <= 10
//) AND
//feature IN (51, 53)    
    public String limitOffset(List<Measurement> feature, Measurement sortColumn, List<MatrixQueryRule> whereRules, int limit) {
        StringBuilder q = new StringBuilder();

        q.append("SELECT target, protocolapplication, feature, value, ");
        q.append("    CASE ");
        q.append(String.format("      WHEN feature = %d ", sortColumn.getId()));
        q.append("      THEN CAST(value AS NUMBER) ");
        q.append("    END AS sortCol ");
        q.append(" FROM observedvalue ");
        q.append(" WHERE protocolapplication IN ( "); 
        //q.append("  --limit and/or offset by x ");
        q.append("  SELECT protocolapplication ");
        q.append("  FROM ( ");
        q.append(orderBy(sortColumn, whereRules));
        q.append("  ) ");
        //q.append("  --limit and offset, ");
        q.append(String.format("  WHERE rownum <= %d ", limit));
        q.append(") AND ");
        
        
        String featureIds = ""; //join(extract(feature, on(Measurement.class).getId()));
        // Lambdaj import doesn't work and "on" method cannot be found; Joris please fix!
        q.append(String.format(" feature IN (%s) ", featureIds));            
        q.append("ORDER BY sortCol");
        
        return q.toString();
    }
    
    
//  SELECT protocolapplication 
//  FROM (
//    --order by gewicht
//    SELECT protocolapplication, value
//    FROM observedvalue
//    WHERE protocolapplication IN (
//      SELECT protocolapplication 
//      FROM (
//          createWhereForProtocol(.... )
//      )
//    ) AND feature = 51
//    ORDER BY value
    
    public String orderBy(Measurement sortColumn, List<MatrixQueryRule> whereRules) {
        StringBuilder order = new StringBuilder();
        order.append("SELECT protocolapplication ");
        order.append("FROM ( ");
        //order.append("  --order by gewicht ");
        order.append("  SELECT protocolapplication, value ");
        order.append("  FROM observedvalue ");
        order.append("  WHERE protocolapplication IN ( ");
        order.append("    SELECT protocolapplication ");
        order.append("    FROM ( ");
            order.append(createWhereForProtocol(whereRules));
        order.append("    ) ");
        order.append(String.format("  ) AND feature = %s ", sortColumn.getId()));
        order.append("  ORDER BY value ) ");
        return order.toString();
    }

    
//  SELECT protocolapplication
//  FROM observedvalue 
//  WHERE feature = 51 AND CAST(value as NUMBER) > 100.0
//  INTERSECT
//  SELECT protocolapplication
//  FROM observedvalue 
//  WHERE feature = 53 AND CAST(value as NUMBER) < 168.4  
    
     public String createWhereForProtocol(List<MatrixQueryRule> rules) {
        StringBuilder where = new StringBuilder();
        boolean prev = false;
        for (MatrixQueryRule rule : rules) {
            if (rule.getFilterType().equals(MatrixQueryRule.Type.colValueProperty)) {
                if (prev) {
                    where.append(" INTERSECT ");
                }
                prev = true;

                Measurement m = em.find(Measurement.class, rule.getDimIndex());
                Protocol p = em.find(Protocol.class, rule.getProtocolId());
                Column.ColumnType ct = Column.getColumnType(m.getDataType());
                String value = rule.getValue().toString();
                if (ct.isQuote()) {
                    value = "'" + value + "'";
                }

                StringBuilder wherePart = new StringBuilder();
                wherePart.append("SELECT protocolapplication ");
                wherePart.append("FROM observedvalue ");
                wherePart.append(String.format("WHERE feature = %d AND CAST(value as NUMBER) > %s",
                        m.getId(), value));
                
                where.append(wherePart);
            }
        }               
        return where.toString();
    }
}
