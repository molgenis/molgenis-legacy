/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.matrix.component.sqlbackend;

import java.util.HashMap;
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
    private HashMap<String, List<String>> ambiguityTable;
    private final EntityManager em;

    private final SliceablePhenoMatrixMV<?, ?, ?> matrix;
	private String leftJoinTable;
    
    public EAVViewBackend(SliceablePhenoMatrixMV<?, ?, ?> matrix, String tablePrefix, String leftJoinTable) {
        this.matrix = matrix;
        this.em = matrix.getEntityManager();
        this.tablePrefix = tablePrefix;
        this.joinColumn = matrix.JOIN_COLUMN;
        this.leftJoinTable = leftJoinTable;        
    }

    @Override
    public String createQuery(boolean count, List<MatrixQueryRule> rules) {
        StringBuilder sql = new StringBuilder("SELECT ");
        
        LinkedHashMap<Protocol, List<Measurement>> measurementsByProtocol = matrix.getMeasurementsByProtocol();
        
        String firstTableName = tablePrefix;
                
        boolean hasJoinTable = StringUtils.isNotEmpty(leftJoinTable);
        if(hasJoinTable) {
        	firstTableName += leftJoinTable;
        } else {
        	firstTableName += tablePrefix + measurementsByProtocol.keySet().toArray(new Protocol[1])[0].getName();
        }
        final String firstTableAlias = firstTableName+"_PK";
        
        if (count) {
            sql.append(" COUNT(*) ");
        } else {
            //Select part        	
        	ambiguityTable = BackendUtils.buildAmbiguityTable(measurementsByProtocol, leftJoinTable, joinColumn);
        	
        	if(hasJoinTable) {
        		sql.append(String.format("%s.%s", firstTableAlias, joinColumn));
        	}
        	
            int cnt = 0;            
			for (Map.Entry<Protocol, List<Measurement>> entry : measurementsByProtocol.entrySet()) {
                for (Measurement m : entry.getValue()) {
                    if(!(hasJoinTable && m.getName().equalsIgnoreCase(joinColumn))) {
                    	String tableName = String.format("%s%s", tablePrefix, entry.getKey().getName());
                        
                        String column = String.format("%s.%s", tableName, m.getName());
                        if(ambiguityTable.containsKey(m.getName()) && ambiguityTable.get(m.getName()).size() > 1)
                        {
                            column = String.format("%s %s_%s", column, tableName, m.getName());
                        }

                        if (cnt > 0 || hasJoinTable) {
                            sql.append(", ");
                        }

                        sql.append(column);                    	
                    }
                    ++cnt;
                }
            }
        }

        sql.append(" FROM ");
        
		if(hasJoinTable) {
        	sql.append(String.format("%s %s", firstTableName, firstTableAlias));
        }
        
        //From part
        int cnt = 0;
        for (Protocol p : measurementsByProtocol.keySet()) {
            String tableName = String.format("%s%s", tablePrefix, p.getName());
            if (cnt > 0 || hasJoinTable) {
                sql.append(String.format(" %s JOIN %s ON (%s.%s = %s.%s)",
                		hasJoinTable ? "LEFT" : "",                		
                        tableName, firstTableAlias, joinColumn, tableName, joinColumn));
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
