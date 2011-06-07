/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifelines.loaders;

import app.JpaDatabase;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.molgenis.model.jaxb.Field;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;

/**
 *
 * @author jorislops
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
    //SELECT * FROM llpoper.LL_DATASET9
    private String schemaName = "llpoper";
    private String tableName = "LL_DATASET9";
    
    private String columnQuery = 
            "SELECT atc.column_name, atc.data_type, atc.data_length, atc.data_precision,  comments.comments "
            +"FROM all_col_comments comments "
            +"JOIN all_tab_columns atc ON (comments.column_name = atc.column_name AND comments.table_name = atc.table_name) "
            +"WHERE comments.table_name = '%s' "
            +"ORDER BY atc.column_id";
    
    private String commentQuery = 
            "SELECT comments.column_name, comments.comments "
            +"FROM all_col_comments comments "
            +"WHERE comments.table_name = '%s'";
    
    public static void main(String[] args) throws Exception {
        OracleToLifelinesPheno oracleToLifelinesPheno = new OracleToLifelinesPheno();
        oracleToLifelinesPheno.load();
    }
    
    public OracleToLifelinesPheno() throws Exception {     
    }
    
    //Key = columnName, Value = comment
    private Map<String, String> columnComment = new HashMap<String, String>();
    
    private void loadComments() throws Exception {
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format(commentQuery, tableName));
        while(rs.next()) {
            columnComment.put(rs.getString(1), rs.getString(2));
        }
        rs.close();
        stmt.close();
        con.close();
    }
    
    public void load() throws Exception {
        loadComments();
        
        Investigation inv = new Investigation();
        inv.setName(tableName);
        
        Connection con = getConnection();        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM %s.%s", schemaName, tableName));
        ResultSetMetaData rsm = rs.getMetaData();
        
        for(int i = 1; i <= rsm.getColumnCount(); ++i) {
            Measurement m = new Measurement();
            m.setInvestigation(inv);
            inv.getObservationElementCollection().add(m);
            m.setName(rsm.getColumnName(i));
            
            m.setDataType(Field.Type.getType(rsm.getColumnType(i)).toString());
            m.setDescription(columnComment.get(rsm.getColumnName(i)));            
        }
        
//        List<Column> columns = getColumns();
//        //List<Measurement> measurements = new ArrayList<Measurement>();
//        for(Column c : columns) {
//            Measurement m = new Measurement();
//            m.setInvestigation(inv);
//            inv.getObservationElementCollection().add(m);
//            m.setName(c.getName());
//            m.setDataType(c.getType());
//            m.setDescription(c.getComment());
//            //measurements.add(m);
//        }
        
        JpaDatabase db = new JpaDatabase();
        EntityManager em = db.getEntityManager();
        em.getTransaction().begin();
        em.persist(inv);
        em.getTransaction().commit();
    }
    
    public List<Column> getColumns() throws Exception {
        Connection con = null;
        ResultSet rs = null;
        Statement st = null;
        List<Column> result = new ArrayList<Column>();
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery(String.format(columnQuery, tableName));
            while(rs.next()) {
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
          if(rs != null) {
              rs.close();
          }  
          if(st != null) {
              rs.close();
          }
          if(con != null) {
              con.close();
          }
        }
        return result;        
    }
    
    public static Connection getConnection() throws Exception {
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@//localhost:2000/llp";
        String username = "molgenis";
        String password = "molTagtGen24Ora";

        Class.forName(driver);
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
