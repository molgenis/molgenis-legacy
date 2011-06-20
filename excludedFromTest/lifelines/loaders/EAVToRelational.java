package lifelines.loaders;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import lifelines.matrix.DBMatrix;

import org.eclipse.persistence.internal.jpa.EntityManagerImpl;
import org.eclipse.persistence.platform.database.MySQLPlatform;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.tools.schemaframework.DefaultTableGenerator;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Measurement;

import app.JpaDatabase;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import org.eclipse.persistence.internal.databaseaccess.DatabaseAccessor;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.Session;

public class EAVToRelational {

    private static final String INVESTIGATION_NAME = "LL_DATASET9";
    private static final int investigationId = 1485227;
    private static final String targetTable = INVESTIGATION_NAME + investigationId;

    public static void main(String[] args) throws Exception {
        new EAVToRelational();
    }

    public EAVToRelational() throws Exception {
        DatabaseLogin databaseLogin = new DatabaseLogin();
        databaseLogin.setConnectionString("jdbc:oracle:thin:@//localhost:2000/llp");
        databaseLogin.setUserName("molgenis");
        databaseLogin.setPassword("molTagtGen24Ora");
//		databaseLogin.setDatabaseName("lifelines");
        databaseLogin.setDriverClass(oracle.jdbc.OracleDriver.class);
        //databaseLogin.setDriverClass(com.mysql.jdbc.Driver.class);


        databaseLogin.setDatasourcePlatform(new MySQLPlatform());
        Project project = new Project(databaseLogin);

        DatabaseSession dbSession = project.createDatabaseSession();

        dbSession.login();

        JpaDatabase jpaDatabase = new JpaDatabase();
        EntityManager em = jpaDatabase.getEntityManager();
//EntityManagerImpl impl = (EntityManagerImpl)em;
//		Investigation investigation = new Investigation();
//		investigation.setName("Life Lines Study Dev1");
        String findByNameAndInvestigation = "SELECT i FROM Investigation i WHERE i.id = :id";
        List<Investigation> investigations = em.createQuery(findByNameAndInvestigation, Investigation.class).setParameter("id", investigationId).getResultList();
//		List<Investigation> investigations = jpaDatabase.findByExample(investigation);
        Investigation investigation = investigations.get(0);
        ;

        DefaultTableGenerator tableGenerator = new DefaultTableGenerator(project);
        TableCreator creator = tableGenerator.generateDefaultTableCreator();
        creator.setName(targetTable);
        TableDefinition tableDefinition = new TableDefinition();
        tableDefinition.setName(targetTable);

        String qlObservableFeature = "SELECT m FROM Measurement m WHERE m.investigation = :investigation ORDER BY m.id";
        List<Measurement> features = em.createQuery(qlObservableFeature, Measurement.class).setParameter("investigation", investigation).getResultList();
        StringBuilder insertSQL = new StringBuilder(String.format("INSERT INTO %s ", targetTable));
        for (Measurement f : features) {
            String columnName = f.getName();
            System.out.println(columnName + "\t" + f.getDataType());

            if (f.getDataType().equals("code")) {
                tableDefinition.addField(new FieldDefinition(columnName, Integer.class));
            } else if (f.getDataType().equals("integer") || f.getDataType().equals("int")) {
                tableDefinition.addField(new FieldDefinition(columnName, Integer.class));
            } else if (f.getDataType().equals("datetime")) {
                tableDefinition.addField(new FieldDefinition(columnName, Date.class));
            } else if (f.getDataType().equals("decimal")) {
                tableDefinition.addField(new FieldDefinition(columnName, Float.class));
            } else if (f.getDataType().equals("string")) {
                tableDefinition.addField(new FieldDefinition(columnName, String.class, 255));
            } else {
                throw new Exception("DataType not supported!" + f.getDataType());
            }
        }
        //insertSQL.deleteCharAt(insertSQL.length()-1);
        insertSQL.append("VALUES (");
        for (int i = 0; i < features.size() - 1; ++i) {
            insertSQL.append("?,");
        }
        insertSQL.append("?)");


        Connection conn = getConnection(em);
        PreparedStatement ps = conn.prepareStatement(insertSQL.toString());

        creator.addTableDefinition(tableDefinition);
        creator.createTables(dbSession);




//                
//		DBMatrix<String> matrix = new DBMatrix<String>(String.class, -1);
//		for(Measurement m : features) {
//			matrix.addColumn(m.getName());	
//		}
//		matrix.setInvestigation(investigation);
        //matrix.loadData(matrix.getNumberOfRows(), 0);
        //Object[][] data = matrix.getData();


        Object[] insertRow = new Object[features.size()];


        JpaDatabase db = new JpaDatabase();
        String sql = "SELECT value, Feature, Target FROM ObservedValue "
                + "WHERE investigation = %s ORDER BY target, feature";

        int w = 0;
        HashMap<Integer, Integer> featureIndex = new HashMap<Integer, Integer>();
        for (Measurement m : features) {
            featureIndex.put(m.getId(), w++);
        }

        int targetIndx = 0;
        
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(String.format(sql, investigation.getId()));

        rs.next();
        targetIndx = rs.getBigDecimal(3).intValue();

        int x = 0;
        Object[] row = new Object[3];
        do {
            row[0] = rs.getString(1);
            row[1] = rs.getBigDecimal(2).intValue();
            row[2] = rs.getBigDecimal(3).intValue();
            
            if (targetIndx != (Integer)row[2]) {
                targetIndx = (Integer)row[2];
                InsertRow(ps, insertRow, features);
                insertRow = new Object[features.size()];
                
                if(x++ % 100 == 0) {
                    ps.executeBatch();
                }
            }
            int fIdx = featureIndex.get((Integer)row[1]);
            insertRow[fIdx] = row[0];
        } while(rs.next());
        
        InsertRow(ps, insertRow, features);
        ps.executeBatch();
        
        conn.commit();
    }

    private void InsertRow(PreparedStatement ps, Object[] insertRow, List<Measurement> features) throws SQLException, ParseException, Exception {
        for (int i = 0; i < insertRow.length; ++i) {
            ps.setObject(i + 1, getSqlValue(insertRow[i], features.get(i)));
        }
        try {
            ps.addBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private Object getSqlValue(Object value, Measurement f) throws ParseException, Exception {
        if (value == null) {
            return null;
        }
        if (f.getDataType().equals("code")) {
            return value.toString();
        } else if (f.getDataType().equals("int")) {
            return new Integer(value.toString());
        } else if (f.getDataType().equals("datetime")) {
            if (((String) value).toLowerCase().contains("null")) {
                return null;
            } else {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("y-M-d H:m:s");
                    java.util.Date d = dateFormat.parse(value.toString());
                    Timestamp t = new Timestamp(d.getDate());
                    return t;
                } catch (ParseException pe) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("y/M/d H:m");
                    java.util.Date d = dateFormat.parse(value.toString());
                    Timestamp t = new Timestamp(d.getDate());
                    return t;
                }
            }
            //values.append(dArr[i]);
        } else if (f.getDataType().equals("decimal")) {
            return new Float(value.toString());
        } else if (f.getDataType().equals("string")) {
            return value;
        } else {
            throw new Exception("DataType not supported!" + f.getDataType());
        }
    }

    private Connection getConnection(EntityManager em) {
        Session session = ((EntityManagerImpl) em).getSession();
        UnitOfWorkImpl uow = (UnitOfWorkImpl) session;
        DatabaseAccessor dbAcc = (DatabaseAccessor) uow.getAccessor();
        return dbAcc.getConnection();
    }
}