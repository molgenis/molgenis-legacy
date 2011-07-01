/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifelines.loaders;

import app.JpaDatabase;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import org.molgenis.framework.db.jpa.JpaUtil;
import org.molgenis.model.jaxb.Field;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservationTarget;

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
    private Investigation investigation = new Investigation();
    private String schemaName = "llpoper";
    private String tableName = "LL_DATASET9";
    private String pk = "pa_id";
    private String columnQuery =
            "SELECT atc.column_name, atc.data_type, atc.data_length, atc.data_precision,  comments.comments "
            + "FROM all_col_comments comments "
            + "JOIN all_tab_columns atc ON (comments.column_name = atc.column_name AND comments.table_name = atc.table_name) "
            + "WHERE comments.table_name = '%s' "
            + "ORDER BY atc.column_id";
    private String commentQuery =
            "SELECT comments.column_name, comments.comments "
            + "FROM all_col_comments comments "
            + "WHERE comments.table_name = '%s'";

    public static void main(String[] args) throws Exception {
        OracleToLifelinesPheno oracleToLifelinesPheno = new OracleToLifelinesPheno();
        oracleToLifelinesPheno.load();
    }

    public OracleToLifelinesPheno() throws Exception {
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

    public void load() throws Exception {
        
        JpaDatabase db = new JpaDatabase();
        JpaUtil.dropAndCreateTables(db);
        EntityManager em = db.getEntityManager();
        Connection con = LoaderUtils.getConnection();
        loadComments();

        List<Measurement> measurements = loadInvestigationAndMeasurement(con, em);
        loadTargets(con, em);

        //writeCSVFile(con, em, measurements);

        //loadDataWithUnion(measurements, em, con);
    }

    private List<Measurement> loadInvestigationAndMeasurement(Connection con, EntityManager em) throws SQLException {
        investigation.setName(tableName + new Date().toString());

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM %s.%s", schemaName, tableName));
        ResultSetMetaData rsm = rs.getMetaData();

        List<Measurement> measurements = new ArrayList<Measurement>();
        for (int i = 1; i <= rsm.getColumnCount(); ++i) {
            Measurement m = new Measurement();
            measurements.add(m);

            m.setInvestigation(investigation);
            investigation.getInvestigationObservationElementCollection().add(m);
//            investigation.getObservationElementCollection().add(m);
            m.setName(rsm.getColumnName(i));

            m.setDataType(Field.Type.getType(rsm.getColumnType(i)).toString());
            m.setDescription(columnComment.get(rsm.getColumnName(i)));
        }

        em.getTransaction().begin();
        em.persist(investigation);
        em.getTransaction().commit();
        System.out.println(investigation.getId());
        return measurements;
    }
    private HashMap<String, Integer> paId = new HashMap<String, Integer>();

    public void loadTargets(Connection con, EntityManager em) throws SQLException {
        String sql = "SELECT %s FROM LLPOPER.%s";
        sql = String.format(sql, pk, tableName);
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        List<ObservationTarget> targets = new ArrayList<ObservationTarget>();

        while (rs.next()) {
            ObservationTarget target = new ObservationTarget();
            target.setInvestigation(investigation);
            target.setName("" + rs.getBigDecimal(1).intValue());
            targets.add(target);
        }
        rs.close();
        stmt.close();

        em.getTransaction().begin();
        for (ObservationTarget target : targets) {
            em.persist(target);
        }
        em.getTransaction().commit();
        for (ObservationTarget target : targets) {
            paId.put(target.getName(), target.getId());
        }

        em.clear();
    }

    private void writeCSVFile(Connection con, EntityManager em, List<Measurement> measurements) throws SQLException, IOException {



        String sql = "SELECT * FROM LLPOPER.%s";
        sql = String.format(sql, tableName);

//        String insertSQL = "INSERT INTO ObservedValue (target, feature, value, investigation) VALUES (?, ?, ?, ?)";
//        //con.setAutoCommit(false);
//        PreparedStatement ps = con.prepareCall(insertSQL);

        int w = 0;

        BufferedWriter bf = new BufferedWriter(new FileWriter("observedvalue.csv"));
        bf.append("target,feature,value,investigation\n");
        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

//        ps.setInt(4, investigation.getId());

        while (rs.next()) {
            String paIDValue = rs.getString(1);
            Integer targetId = paId.get(paIDValue);
//            ps.setInt(1, targetId);
            for (int i = 0; i < measurements.size(); ++i) {
                Measurement m = measurements.get(i);
//                ps.setInt(2, m.getId());
                Object value = rs.getObject(i + 1);
                if (value != null) {
//                    ps.setString(3, value.toString());
//                    ps.addBatch();
                }

                bf.append(String.format("%d,%d,%s,%d\n", targetId, m.getId(), value, investigation.getId()));
            }
            ++w;
            if (w % 1000 == 0) {
//                ps.executeBatch();
                System.out.println("" + w);
            }
        }
//        ps.executeBatch();

        bf.flush();
        bf.close();

        System.out.println("END OF INSERT");
    }

    //preformance is bad
    public void loadDataWithUnion(List<Measurement> measurements, EntityManager em, Connection con) throws SQLException {
        String featureSQL = "SELECT %s, '' || %s as value, %d as feature FROM %s.%s";

        StringBuilder sql = new StringBuilder();
        for (int i = 0; i < measurements.size(); ++i) {
            Measurement m = measurements.get(i);
            sql.append(String.format(featureSQL, pk, m.getName(), m.getId(), schemaName, tableName));
            if (i + 1 < measurements.size()) {
                sql.append(" union all ");
            }
        }

        String insert = "INSERT INTO ObservedValue (value, feature, target, investigation) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(insert);
        ps.setInt(4, investigation.getId());

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql.toString());

        int w = 0;
        while (rs.next()) {
            Integer targetId = paId.get(rs.getString(1));

            Object value = rs.getObject(2);
            if (value != null) {
                Integer feature = rs.getBigDecimal(3).intValue();

                ps.setString(1, value.toString());
                ps.setInt(2, feature);
                ps.setInt(3, targetId);

                ps.addBatch();
                if (++w % 10000 == 0) {
                    ps.executeBatch();
                    System.out.println("w:" +w);
                }
            }

        }

        ps.executeBatch();
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
}
