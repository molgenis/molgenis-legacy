///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package lifelines.loaders;
//
//import app.JpaDatabase;
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Hashtable;
//import java.util.List;
//import java.util.Map;
//import javax.persistence.EntityManager;
//import lifelines.loaders.OracleToLifelinesPheno.Column;
//import org.apache.commons.collections.CollectionUtils;
//import org.molgenis.organization.Investigation;
//import org.molgenis.organization.db.InvestigationJpaMapper;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.pheno.ObservationElement;
//import org.molgenis.pheno.ObservedValue;
//
///**
// *
// * @author jorislops
// */
//public class OracleToText {
//    private String sql = "SELECT * FROM LLPOPER.%s ORDER BY pa_id";
//    private String schemaName = "LLPOPER";
//    private String tableName = "LL_DATASET9";
//    
//    //Key = pa_id Values = Target.id
//    private Map<Integer, Integer> paidMap = new Hashtable<Integer, Integer>();
//    
//    public static void main(String[] args) throws Exception {
//        OracleToText oracleToText = new OracleToText();
//    }
//
//    OracleToLifelinesPheno otlp = new OracleToLifelinesPheno();
//    
//    public OracleToText() throws Exception {
//        JpaDatabase db = new JpaDatabase();
//        EntityManager em = db.getEntityManager();
//
//        Investigation investigation = Investigation.findByName(db, tableName);
//        List<Measurement> measurements = LoaderUtils.getMeasurements(em, investigation, schemaName, tableName);
//        
//        StringBuilder header = new StringBuilder();
//        List<Column> columns = otlp.getColumns();
//        for(Column c : columns) {
//           header.append(c.getName() + ",");
//        }
//        header.deleteCharAt(header.length()-1);
//        System.out.println(header.toString());
//        
//        StringBuilder data = new StringBuilder();
//        
//        Connection con = OracleToLifelinesPheno.getConnection();
//        con.setAutoCommit(false);
//        
//        insertInvestigations(con, investigation.getId());
//        fillPaIdMap(con, investigation.getId());
//
//        Statement stmt = con.createStatement();
//        ResultSet rs = stmt.executeQuery(String.format(sql, tableName));
//
//        String sqlInsert = "INSERT INTO ObservedValue (DTYPE, INVESTIGATION, TARGET, FEATURE, VALUE) "
//                +" VALUES (?, ?, ?, ?, ?)";
//        PreparedStatement ps = con.prepareStatement(sqlInsert);
//        ps.setString(1, "ObservedValue");
//        ps.setInt(2, investigation.getId());
//                
////        BufferedWriter bw = new BufferedWriter(new FileWriter("test.csv"));
//        
//        while(rs.next()) {
//            int targetId = rs.getBigDecimal(1).intValue();
//            ps.setInt(3, targetId);
//            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
//                int measurementId = measurements.get(i-1).getId();
//                ps.setInt(4, measurementId);                
//                
//                Object value = rs.getObject(i);
//                if(value != null) {
////                    bw.append(targetId + "," +measurementId + "," +rs.getObject(i) + "\n");    
//                    ps.setString(5, rs.getObject(i).toString());                                       
//                    ps.addBatch();
//                }
//            }           
//        }
//        ps.executeBatch();
//        con.commit();
//        
////        bw.flush();
////        bw.close();
//        
////        System.out.println(data.toString());
//        
//    }
//    
//    
//    private void insertInvestigations(Connection con, int investigationId) throws SQLException {
//        Statement stmt = con.createStatement();
//        ResultSet rs = stmt.executeQuery(String.format("SELECT pa_id FROM LLPOPER.%s ORDER BY pa_id", tableName));
//        
//        String sql = "INSERT INTO ObservationElement (DTYPE, NAME, INVESTIGATION)  VALUES (?, ?, ?, ?)";
//
//        con.setAutoCommit(false);
//        PreparedStatement ps = con.prepareStatement(sql);
//        
//        ps.setString(1, "ObservationTarget");
//        ps.setInt(3, investigationId);
//        while(rs.next()) {
//            ps.setString(2, "" +rs.getInt(1));
//            ps.addBatch();
//        }        
//        ps.executeBatch();
//        rs.close();
//        stmt.close();
//    }
//    
//    private void fillPaIdMap(Connection con, int investigationId) throws SQLException {
//        String sql = "SELECT name, id FROM ObservationElement WHERE investigation = %s";
//        sql = String.format(sql, investigationId);
//        
//        Statement stmt = con.createStatement();
//        
//        ResultSet rs = stmt.executeQuery(sql);
//        
//        while(rs.next()) {
//            paidMap.put(Integer.parseInt(rs.getString(1)), rs.getBigDecimal(2).intValue());    
//        }
//
//        rs.close();
//        stmt.close();
//    }
//    
//}
