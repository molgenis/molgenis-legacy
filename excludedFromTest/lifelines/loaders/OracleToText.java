/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lifelines.loaders;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;
import lifelines.loaders.OracleToLifelinesPheno.Column;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.pheno.ObservedValue;

/**
 *
 * @author jorislops
 */
public class OracleToText {
    private String sql = "SELECT * FROM LLPOPER.%s";
    private String tableName = "LL_DATASET9";
    
    public static void main(String[] args) throws Exception {
        OracleToText oracleToText = new OracleToText();
    }

    OracleToLifelinesPheno otlp = new OracleToLifelinesPheno();
    
    public OracleToText() throws Exception {
        StringBuilder header = new StringBuilder();
        List<Column> columns = otlp.getColumns();
        for(Column c : columns) {
           header.append(c.getName() + ",");
        }
        header.deleteCharAt(header.length()-1);
        System.out.println(header.toString());
        
        StringBuilder data = new StringBuilder();
        
        Connection con = OracleToLifelinesPheno.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(String.format(sql, tableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        
        while(rs.next()) {
            ObservationElement target = null;
            for(int i = 1; i <= rsmd.getColumnCount(); ++i) {
                Object value = rs.getObject(i);
                data.append(value);
                if(i != rsmd.getColumnCount()) {
                    data.append(",");
                }
            }
            data.append("\n");
        }
        
        System.out.println(data.toString());
        
    }
    
    
}
