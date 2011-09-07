package org.molgenis.lifelines.loaders;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.molgenis.framework.db.DatabaseException;
import org.molgenis.model.jaxb.Field;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;

import app.JpaDatabase;

/**
 *
 * @author joris lops
 */
public class OracleToLifelinesPheno {

    public class Column {

        private String name;
        private String type;
        private int length;
        private int precision;
        private String comment;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPrecision() {
            return precision;
        }

        public void setPrecision(int precision) {
            this.precision = precision;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Column{" + "name=" + name + ", type=" + type + ", length=" + length + ", precision=" + precision + ", comment=" + comment + '}';
        }
    }
    private final Integer investigationId;
    private final String schemaName;
    private final String tableName;
    private final String fields;
    private final String columnQuery =
            "SELECT atc.column_name, atc.data_type, atc.data_length, atc.data_precision,  comments.comments "
            + "FROM all_col_comments comments "
            + "JOIN all_tab_columns atc ON (comments.column_name = atc.column_name AND comments.table_name = atc.table_name) "
            + "WHERE comments.table_name = '%s' "
            + "ORDER BY atc.column_id";
    private final String commentQuery =
            "SELECT comments.column_name, comments.comments "
            + "FROM all_col_comments comments "
            + "WHERE comments.table_name = '%s'";
    private Investigation investigation;
    
    private List<Measurement> measurements;
    private EntityManager em;

    public OracleToLifelinesPheno(EntityManager em, String schemaName, String tableName, String fields, Integer investigationId) throws Exception {
    	this.em = em;
    	this.schemaName = schemaName;
    	this.tableName = tableName;
    	this.fields = fields;
    	this.investigationId = investigationId;
    	load();
    }
    //Key = columnName, Value = comment
    private Map<String, String> columnComment = new HashMap<String, String>();
    
    private void loadComments() throws Exception {
        Connection con = LoaderUtils.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format(commentQuery, tableName));
        while (rs.next()) {
            columnComment.put(rs.getString(1), rs.getString(2));
        }
        rs.close();
        stmt.close();
        con.close();
    }

    private void load() throws Exception {
        Connection con = LoaderUtils.getConnection();
        loadComments();

        measurements  = loadInvestigationAndMeasurement(con, em);
    }

    private List<Measurement> loadInvestigationAndMeasurement(Connection con, EntityManager em) throws SQLException {
        investigation = em.find(Investigation.class, investigationId);

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT %s FROM %s.%s", fields, schemaName, tableName));
        ResultSetMetaData rsm = rs.getMetaData();

        List<Measurement> measurements = new ArrayList<Measurement>();
        for (int i = 1; i <= rsm.getColumnCount(); ++i) {
        	Long count = em.createQuery("SELECT Count(m) FROM Measurement m WHERE m.investigation.id = :invId AND m.name = :name", Long.class)
        		.setParameter("invId", investigationId)
        		.setParameter("name", rsm.getColumnName(i))        		
        		.getSingleResult();
        	if(count == 1) {
        		continue;
        	} else if(count > 1) {
        		throw new IllegalArgumentException("two measurments with same name in investigation");
        	}
        	
            Measurement m = new Measurement();
            measurements.add(m);

            m.setInvestigation(investigation);
            investigation.getInvestigationMeasurementCollection().add(m);
            m.setName(rsm.getColumnName(i));

            System.out.println(rsm.getColumnName(i) + "\t" + rsm.getPrecision(i) + "\t"   + rsm.getScale(i));
            
            m.setDataType(Field.Type.getType(rsm.getColumnType(i)).toString());
            if(m.getDataType().equals("decimal")) {
            	int precision = rsm.getPrecision(i);           
            	if(precision == 0) {
            		m.setDataType(Field.Type.INT.toString());
            	}
            }
            
            m.setDescription(columnComment.get(rsm.getColumnName(i)));
        }

        em.getTransaction().begin();
        em.merge(investigation);
        em.getTransaction().commit();
        System.out.println(investigation.getId());
        return measurements;
    }

    public List<Column> getColumns() throws Exception {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        List<Column> result = new ArrayList<Column>();
        try {
            con = LoaderUtils.getConnection();
            st = con.createStatement();
            rs = st.executeQuery(String.format(columnQuery, tableName));
            while (rs.next()) {
                Column col = new Column();
                col.setName(rs.getString(1));
                col.setType(rs.getString(2));
                col.setLength(rs.getInt(3));
                col.setPrecision(rs.getInt(4));
                col.setComment(rs.getString(5));
                System.out.println(col.toString());
                result.add(col);
            }
            rs.close();
            con.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                rs.close();
            }
            if (con != null) {
                con.close();
            }
        }
        return result;
    }
    
    public int getInvestigationId() {
    	return investigation.getId();
    }
    
    public List<Measurement> getMeasurements() {
    	return this.measurements;
    }
    
    public BigDecimal getRowCount() throws DatabaseException {
    	String sql = "SELECT Count(*) FROM %s.%s";
    	EntityManager em = null;
    	try {
	        JpaDatabase db = new JpaDatabase();
	        em = db.getEntityManager();
	        
	        sql = String.format(sql, schemaName, tableName);
	        Object result = em.createNativeQuery(sql).getSingleResult();
	        return ((BigDecimal)result);
    	} catch (DatabaseException ex) {
    		throw ex;
    	} finally {
    		em.close();
    	}
    }    
}
