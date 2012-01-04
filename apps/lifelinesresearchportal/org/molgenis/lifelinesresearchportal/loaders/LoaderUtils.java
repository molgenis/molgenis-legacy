package org.molgenis.lifelinesresearchportal.loaders;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;

/**
 *
 * @author joris lops
 */
public class LoaderUtils {
    
    public static List<Measurement> getMeasurementsByInvestigationId(final int investigationId, EntityManager em, final String schemaName, final String tableName) throws Exception {
        Investigation investigation = em.createQuery("SELECT i FROM Investigation i WHERE i.id = :id", Investigation.class)
                .setParameter("id", investigationId)
                .getSingleResult();
        return getMeasurements(em, investigation, schemaName, tableName);
    }
    
    public static List<Measurement> getMeasurements(EntityManager em, Investigation investigation, String schemaName, String tableName) throws Exception {
        String sql = "SELECT * FROM %s%s";
        if(StringUtils.isEmpty(schemaName)) {
            sql = String.format(sql, "", tableName);
        } else {
            sql = String.format(sql, schemaName + ".", tableName);
        }
        
        Connection con = LoaderUtils.getConnection();
        Statement stm = con.createStatement();
        ResultSet rs = stm.executeQuery(String.format(sql, tableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        
        List<Measurement> measurements = new ArrayList<Measurement>();
        
        
        for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
            String columnName = rsmd.getColumnName(i);        
            Measurement m = em.createQuery("SELECT m FROM Measurement m WHERE m.name = :name AND m.investigation = :investigation", Measurement.class)
              .setParameter("name", columnName)
              .setParameter("investigation", investigation)
              .getSingleResult();
            measurements.add(m);
        } 
        return measurements;
    }
    
    public static Connection getConnection() throws Exception {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@//localhost:2000/llptest";
        String username = "molgenis";
        String password = "molTagtGen24Ora";

//	      String driver = "com.mysql.jdbc.Driver";
//	      String url = "jdbc:mysql://localhost/lifelines";
//	      String username = "molgenis";
//	      String password = "molgenis";    	
    	
        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }    
    
    public enum eDatabase {
    	ORACLE,
    	MYSQL
    }
    
    public static String getCast(String dataType, eDatabase database) throws Exception {
        dataType = dataType.toLowerCase();
        int idx = dataType.indexOf("(");
        if(idx != -1) {
        	dataType = dataType.substring(0, idx);
        }
    	
        if (dataType.equals("code")) {
        	if(database == eDatabase.MYSQL) {
        		return "cast(%s as DECIMAL)";	
        	} else if(database == eDatabase.ORACLE) {
        		return "cast(%s as number)";
        	}
        } else if (dataType.equals("int") || dataType.equals("nummer")) {
        	if(database == eDatabase.MYSQL) {
        		return "cast(%s as DECIMAL)";	
        	} else if(database == eDatabase.ORACLE) {
        		return "cast(%s as number)";
        	}
        } else if (dataType.equals("datetime") || dataType.equals("datum")) {
        	if(database == eDatabase.MYSQL) {
        		return "cast(substr(value,1, 19) AS DATETIME) ";
        	} else {
        		return "to_date(substr(value,1, 19), 'yyyy-mm-dd hh24:mi:ss') ";
    		}
        } else if (dataType.equals("decimal")) {
        	if(database == eDatabase.MYSQL) {
        		return "cast(%s as DECIMAL)";	
        	} else if(database == eDatabase.ORACLE) {
        		return "cast(%s as number)";
        	}
        } else if (dataType.equals("string") || dataType.equals("tekst")) {
            return "%s";
        } else if(dataType.equals("long")) {
        	if(database == eDatabase.MYSQL) {
        		return "cast(%s as DECIMAL)";	
        	} else if(database == eDatabase.ORACLE) {
        		return "cast(%s as number)";
        	}
        } else {
            throw new Exception("DataType not supported!" + dataType);
        }   
        
        throw new Exception("DataType/database not supported!" + dataType);
    }
    
}
