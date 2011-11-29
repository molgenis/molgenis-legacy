package org.molgenis.matrix.component.sqlbackend;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.molgenis.matrix.component.SliceablePhenoMatrixMV;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

/**
 * This backend will function as a Multi-value backend for the Pheno (EAV) model.
 * The model works by constructing select queries based on group by and max to recreate
 * relational table look a like
 * @author jorislops
 */
public class EAVRelationalBackend implements Backend {
    private final String joinColumn;
    private final LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol;
    private final Investigation investigation;
    private final EntityManager em;
    private HashMap<String, List<String>> ambiguityTable;    
    
    private final SliceablePhenoMatrixMV matrix;
    
    public EAVRelationalBackend(SliceablePhenoMatrixMV matrix) {
        this.matrix = matrix;
        
        this.em = matrix.getEm();
        this.joinColumn = matrix.getJOIN_COLUMN();
        this.mesurementsByProtocol = matrix.getMesurementsByProtocol();
        this.investigation = matrix.getInvestigation();
        
        ambiguityTable = BackendUtils.buildAmbiguityTable(mesurementsByProtocol);  
    }
    
    @Override
    public String createQuery(boolean count, List<MatrixQueryRule> rules) throws Exception {
        StringBuilder query = new StringBuilder();
        
        boolean first = true;

        String orderByColumn = null;
        String prevAliasName = null;

        StringBuilder fields = new StringBuilder();
        
        for (Map.Entry<Protocol, List<Measurement>> entry : mesurementsByProtocol.entrySet()) {
            List<Measurement> measurements = entry.getValue();

            boolean firstM = first;
            for(Measurement m : measurements) {
                if(!firstM) {
                    fields.append(", ");
                } else {
                    firstM = false;
                }
                
                
                
                //do something with ambugity table
                //fields.append(String.format("%s.%s", entry.getKey().getName(), m.getName()));
                if(!count) {
                    fields.append(String.format("%s_%s", entry.getKey().getName(), m.getName()));
//                    if(mesurementsByProtocol.size() > 1) {
//                        fields.append(String.format("%s_%s", entry.getKey().getName(), m.getName()));
//                    } else {
//                        fields.append(String.format("%s.%s", entry.getKey().getName(), m.getName()));
//                    }
//                    if(ambiguityTable.get(m.getName()).size() > 1) {
//                        fields.append(String.format("%s.%s %s_%s", entry.getKey().getName(), m.getName(), entry.getKey().getName(), m.getName()));
//                    } else {
//                        fields.append(String.format("%s", m.getName()));
//                    }
                }                    
            }
            
            //boolean paIDExists = exists(entry.getValue(), having(on(Measurement.class).getName(), IsEqualIgnoringCase.equalToIgnoringCase(JOIN_COLUMN)));
            boolean paIDExists = false;
            for (Measurement m : entry.getValue()) {
                if (m.getName().equalsIgnoreCase(joinColumn)) {
                    paIDExists = true;
                    break;
                }
            }

            List<Measurement> ms = (List<Measurement>)(List)entry.getKey().getFeatures();
            ms.toString();
            if (!paIDExists) {
                Measurement paIdMes = em.createQuery("SELECT m FROM Measurement m JOIN m.featuresCollection p WHERE m.name = :name and p = :protocol", Measurement.class).setParameter("name", joinColumn).setParameter("protocol", entry.getKey()).getSingleResult();
                measurements.add(paIdMes);
            }

            String aliasName = entry.getKey().getName();
            String sql = BackendUtils.createQuery(investigation, entry.getKey(), measurements, em, true);


            if (first) {
                orderByColumn = aliasName;
                first = false;
                query.append(String.format("(%s) %s", sql, aliasName));
            } else {
                query.append(
                        String.format(" left join (%s) %s on (%s = %s)",
                        sql, aliasName, aliasName + "_" + "PA_ID", prevAliasName + "_" +"PA_ID"));

            }
            prevAliasName = aliasName;
        }

        String whereFilter = BackendUtils.getFilterCondition(rules, em);
        if (!StringUtils.isEmpty(whereFilter)) {
            if(count) {
                query = new StringBuilder(String.format("%s WHERE %s", query.toString(), whereFilter));
            } else {
                query = new StringBuilder(String.format("SELECT %s FROM (%s) WHERE %s", fields, query.toString(), whereFilter));    
            }            
        }
        
        if (!count) {
            Protocol sortProtocol = matrix.getSortProtocol();
            if(sortProtocol != null) {
                Measurement measurementProtocol = matrix.getSortMeasurement();
                String sortOrder = matrix.getSortOrder();
                
                query.append(String.format(" ORDER BY %s_%s %S", 
                        sortProtocol.getName(), measurementProtocol.getName(), sortOrder));  
            }            
        }        
        
        
        StringBuilder result = new StringBuilder("SELECT ");
        if (count) {
            result.append(" COUNT(*) FROM ");
            //todo: optimalisation, remove columns that are not needed for count!
//                kkfw
////                for(List<Measurement> m : pM.values()) {
////                    List<Measurement> result = filter(having(on(Measurement.class).getName(), IsIn.isIn(asList("PA_ID"))), m);
////                                        
////                }
        } else {
            result.append(String.format(" %s FROM ", fields));
        }        
        if(query.toString().startsWith("(")) {
            result.append(query);        
        } else {
            result.append(String.format("(%s)",query));        
        }
        return result.toString();
    }
    
  
}
