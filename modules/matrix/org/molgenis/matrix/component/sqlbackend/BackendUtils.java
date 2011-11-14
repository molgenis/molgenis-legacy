/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.matrix.component.sqlbackend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.persistence.EntityManager;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

/**
 *
 * @author jorislops
 */
public class BackendUtils {
    public static String getFilterCondition(List<MatrixQueryRule> rules, EntityManager em) {
        return getFilterCondition(rules, em, false, null);
    }
    
    public static String getFilterCondition(List<MatrixQueryRule> rules, EntityManager em, boolean view, String prefix) {
        StringBuilder where = new StringBuilder();
        boolean prev = false;
        for (MatrixQueryRule rule : rules) {
            if (rule.getFilterType().equals(MatrixQueryRule.Type.colValueProperty)) {
                if (prev) {
                    where.append(" AND ");
                }
                prev = true;

                Measurement m = em.find(Measurement.class, rule.getDimIndex());
                Protocol p = em.find(Protocol.class, rule.getProtocolId());
                Column.ColumnType ct = Column.getColumnType(m.getDataType());
                String value = rule.getValue().toString();
                if (ct.isQuote()) {
                    value = "'" + value + "'";
                }
                if(view) {
                    where.append(String.format("%s%s.%s %s %s", prefix, p.getName(), m.getName(), rule.getOperator(), value));
                } else {
                    where.append(String.format("%s_%s %s %s", p.getName(), m.getName(), rule.getOperator(), value));
                }
            }
        }
        return where.toString();
    }  
    
    
    public static HashMap<String, List<String>> buildAmbiguityTable(LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol) {
        HashMap<String, List<String>> ambiguityTable = new HashMap<String, List<String>>();
        for(Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
            for(Measurement m : entry.getValue()) {
                if(ambiguityTable.containsKey(m.getName())) {
                    ambiguityTable.get(m.getName()).add(entry.getKey().getName());
                } else {
                    ambiguityTable.put(m.getName(), new ArrayList<String>(Arrays.asList(entry.getKey().getName())));
                }
            }
        }
        return ambiguityTable;
    }
}
