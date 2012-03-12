/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.molgenis.matrix.component.sqlbackend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.james.mime4j.field.datetime.parser.DateTimeParser;
import org.junit.experimental.categories.Categories;
import org.molgenis.matrix.component.Column;
import org.molgenis.matrix.component.Column.ColumnType;
import org.molgenis.matrix.component.general.MatrixQueryRule;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.protocol.Protocol;

import com.ibm.icu.text.SimpleDateFormat;

/**
 *
 * @author jorislops
 */
public class BackendUtils {
    public static String createQuery(Investigation investigation, Protocol protocol, 
            List<Measurement> measurements, EntityManager em, boolean tableInAlias) {
        String column = "max(case when o.feature = %d then %s end) %s \n";
        StringBuilder query = new StringBuilder("SELECT ");    
            for(int i = 0; i < measurements.size(); ++i) {
                Measurement m = measurements.get(i);
                String castPart = getCast(m.getDataType());      
                String fieldAlias = null;
                if(tableInAlias) {
                    fieldAlias = String.format("%s_%s", protocol.getName(), m.getName());
                } else {
                    fieldAlias = String.format("%s", m.getName());
                }
                
                fieldAlias = StringUtils.substring(fieldAlias, 0, 30);
           
                query.append(String.format(column, m.getId(), String.format(castPart, "value"), fieldAlias));
                if(i + 1 < measurements.size()) {
                    query.append(",");
                }
            }

        query.append(String.format(" FROM \n  observedvalue o join protocolapplication pa on (o.protocolapplication = pa.id) \n WHERE o.investigation = %d AND pa.protocol = %d \n GROUP BY o.protocolapplication", investigation.getId(), protocol.getId()));
        return query.toString();
    }	
	
    public static String getCast(String dataType) {
    	int idx = dataType.indexOf("(");
    	if(idx != -1) {
    		dataType = dataType.substring(0, idx);
    	}
    	dataType = dataType.toLowerCase();
    	
        if (dataType.equals("code")) {
            return "cast(%s as number)";
        } else if (dataType.equals("int")) {
            return "cast(%s as number)";
        } else if (dataType.equals("datetime") || dataType.equals("datum")) {
            return "to_date(substr(value,1, 19), 'yyyy-mm-dd hh24:mi:ss') ";
        } else if (dataType.equals("decimal")) {
            return "cast(%s as number)";
        } else if (dataType.equals("string") || dataType.equals("text") || dataType.equals("tekst")) {
            return "%s";
        } else if(dataType.equals("long")) {
        	return "cast(%s as number)";
        } else if(dataType.equals("number") || dataType.equals("nummer")) {
            return "cast(%s as number)";            
            
        } else {
            throw new IllegalArgumentException("DataType not supported!" + dataType);
        }        
    }    
    
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
                
                String dataType = m.getDataType();
                //remove precision
       //         dataType = StringUtils.substringBefore(dataType, "(");
                Column.ColumnType ct = Column.getColumnType(dataType); 
                	//Column.getColumnType(dataType);
                String value = rule.getValue().toString();
                
                
                Collection<Category> categories = m.getCategories();
                final Map<String, Integer> labelToCode = new HashMap<String, Integer>();
                CollectionUtils.forAllDo(categories, new Closure() {
					@Override
					public void execute(Object arg0) {
						Category c = (Category) arg0;
						labelToCode.put(c.getLabel().toLowerCase(), Integer.parseInt(c.getCode_String()));
					}
				});
                if(!labelToCode.isEmpty()) {
                	final String key = value.toLowerCase();
					if(labelToCode.containsKey(key)) {
						value = labelToCode.get(key).toString();	
                	} else {               	
                		System.out.println("Invalid Label"); 
                	}
                } else {
                	if(ct == ColumnType.Date) {
                		Date date = null;
                		try {
                			date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
                		} catch(Exception ex) {
                			try {
                				date = new SimpleDateFormat("yyyy-MMM-dd").parse(value);
                			} catch (Exception ex1) {
                				try {
                					date = new SimpleDateFormat("yyyy-M-dd").parse(value);
                				} catch (Exception ex2) {
                					//display error 
                				}
                			}
                		}
                		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");                		
                		value = String.format("to_date('%s','yyyy-mm-dd')", formatter.format(date));
                	} else {
                		if (ct.isQuote()) {
                			value = "'" + value + "'";
                		}
                	}
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
    
    
    public static HashMap<String, List<String>> buildAmbiguityTable(LinkedHashMap<Protocol, List<Measurement>> mesurementsByProtocol, String leftJoinTable, String joinColumn) {
    	HashMap<String, List<String>> ambiguityTable = new HashMap<String, List<String>>();
    	
    	if(StringUtils.isNotEmpty(joinColumn) && StringUtils.isNotEmpty(leftJoinTable)) {
    		ambiguityTable.put(joinColumn, new ArrayList<String>(Arrays.asList(leftJoinTable)));
    	}
    	
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
