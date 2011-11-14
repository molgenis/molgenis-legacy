/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.matrix.component.sqlbackend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.apache.commons.lang.StringUtils;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

/**
 * This backend will function as a Multi-value backend for the Pheno (EAV) model.
 * The model works by constructing select queries on views that are generated when
 * data is loaded into pheno model
 * @author jorislops
 */
public class EAVViewBackend implements Backend {

    private final String joinColumn;
    private final String tablePrefix;
    private final LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol;
    private HashMap<String, List<String>> ambiguityTable;
    private final EntityManager em;

    private final SliceablePhenoMatrixMV matrix;
    
    public EAVViewBackend(SliceablePhenoMatrixMV matrix, String tablePrefix) {
        this.matrix = matrix;
        this.em = matrix.getEm();
        this.mesurementsByProtocol = matrix.getMesurementsByProtocol();
        this.tablePrefix = tablePrefix;
        this.joinColumn = matrix.getJOIN_COLUMN();
        
        ambiguityTable = BackendUtils.buildAmbiguityTable(mesurementsByProtocol);        
    }

    @Override
    public String createQuery(boolean count, List<MatrixQueryRule> rules) throws Exception {
        StringBuilder sql = new StringBuilder("SELECT ");
        String firstTableName = tablePrefix + mesurementsByProtocol.keySet().toArray(new Protocol[1])[0].getName();

        if (count) {
            sql.append(" COUNT(*) ");
        } else {
            //Select part
            int cnt = 0;
            for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
                for (Measurement m : entry.getValue()) {
                    String tableName = String.format("%s%s", tablePrefix, entry.getKey().getName());
                    
                    String column = String.format("%s.%s", tableName, m.getName());
                    if(ambiguityTable.get(m.getName()).size() > 1)
                    {
                        column = String.format("%s %s_%s", column, tableName, m.getName());
                    }

                    if (cnt > 0) {
                        sql.append(", ");
                    }

                    sql.append(column);

                    ++cnt;
                }
            }
        }

        sql.append(" FROM ");

        //From part
        int cnt = 0;
        for (Protocol p : mesurementsByProtocol.keySet()) {
            String tableName = String.format("%s%s", tablePrefix, p.getName());
            if (cnt > 0) {
                sql.append(String.format(" JOIN %s ON (%s.%s = %s.%s)",
                        tableName, firstTableName, joinColumn, tableName, joinColumn));
            } else {
                sql.append(tableName);
            }
            ++cnt;
        }

        //Where part

        String where = BackendUtils.getFilterCondition(rules, em, true, tablePrefix);
        if(StringUtils.isNotEmpty(where)) {
            sql.append(" WHERE ").append(where);            
        }        
        
        
        Protocol sortProtocol = matrix.getSortProtocol();
        if(sortProtocol != null) {
            sql.append(
                String.format(" ORDER BY %s%s.%s %s", tablePrefix, sortProtocol.getName(), matrix.getSortMeasurement().getName(), matrix.getSortOrder())
            );
        }
        
        
        return sql.toString();
    }
    
    

}
