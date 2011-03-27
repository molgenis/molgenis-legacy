package lifelines.loaders;

import java.sql.Date;
import java.sql.Timestamp;
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

public class EAVToRelational {
	public static void main(String[] args) throws Exception {
		new EAVToRelational();
	}
	
	public EAVToRelational() throws Exception {
		DatabaseLogin databaseLogin = new DatabaseLogin();
		databaseLogin.setConnectionString("jdbc:mysql://localhost/molgenis_test");
		databaseLogin.setUserName("molgenis");
		databaseLogin.setPassword("molgenis");
		databaseLogin.setDatabaseName("molgenis_test");
		databaseLogin.setDriverClass(com.mysql.jdbc.Driver.class);
		
		
		databaseLogin.setDatasourcePlatform(new MySQLPlatform());
		Project project = new Project(databaseLogin);
		
		DatabaseSession dbSession = project.createDatabaseSession();
		
		dbSession.login();
				
		DefaultTableGenerator tableGenerator = new DefaultTableGenerator(project);
		TableCreator creator = tableGenerator.generateDefaultTableCreator();
		creator.setName("Joris");
		TableDefinition tableDefinition = new TableDefinition();
		tableDefinition.setName("Joris");

		JpaDatabase jpaDatabase = new JpaDatabase();
		EntityManager em = jpaDatabase.getEntityManager();
		//EntityManagerImpl impl = (EntityManagerImpl)em;
		Investigation investigation = new Investigation();
		investigation.setName("Life Lines Study Dev1");
		List<Investigation> investigations = jpaDatabase.findByExample(investigation);
		investigation = investigations.get(0);
	
		//List<String> columnNames = new ArrayList<String>();
		
		String qlObservableFeature = "SELECT m FROM Measurement m WHERE m.investigation = :investigation";
		List<Measurement> features = em.createQuery(qlObservableFeature, Measurement.class)
			.setParameter("investigation", investigation)
			.getResultList();
		for(Measurement f : features) {
			String columnName = f.getName().split("\\.")[1].trim().replace("_", "");
			
			System.out.println(columnName + "\t" + f.getDataType());
			
			if(f.getDataType().equals("code")) {
				tableDefinition.addField(new FieldDefinition(columnName, Integer.class));
			} else if(f.getDataType().equals("int")) {
				tableDefinition.addField(new FieldDefinition(columnName, Integer.class));
			} else if(f.getDataType().equals("datetime")) {
				tableDefinition.addField(new FieldDefinition(columnName, Date.class));
			} else if(f.getDataType().equals("decimal")) {
				tableDefinition.addField(new FieldDefinition(columnName, Float.class));
			} else if(f.getDataType().equals("string")) {
				tableDefinition.addField(new FieldDefinition(columnName, String.class, 255));
			} else {
				throw new Exception("DataType not supported!" + f.getDataType());
			}
		}
		
		creator.addTableDefinition(tableDefinition);
		creator.createTables(dbSession);		
		
		DBMatrix<String> matrix = new DBMatrix<String>(String.class, -1);
		for(Measurement m : features) {
			matrix.addColumn(m.getName());	
		}
		matrix.setInvestigation(investigation);
		matrix.loadData(matrix.getNumberOfRows(), 0);
		Object[][] data = matrix.getData();
		
		String sqlInsert = "INSERT INTO joris VALUES (%s)";
			
		
		for(Object[] dArr : data) {
			StringBuilder values = new StringBuilder();
			int i = 0;
			for(Measurement f : features) {
				if(f.getDataType().equals("code")) {
					values.append(dArr[i]);
				} else if(f.getDataType().equals("int")) {
					values.append(dArr[i]);
				} else if(f.getDataType().equals("datetime")) {
					if(((String)dArr[i]).toLowerCase().contains("null")) {
						values.append("NULL");
					} else {			
						SimpleDateFormat dateFormat = new SimpleDateFormat("y/M/d H:m:s");
						java.util.Date d = dateFormat.parse((String)dArr[i]);
						Timestamp t = new Timestamp(d.getDate());
						values.append("'" +t.toString() + "'");
						
					}
					//values.append(dArr[i]);
				} else if(f.getDataType().equals("decimal")) {
					values.append(dArr[i]);
				} else if(f.getDataType().equals("string")) {
					values.append("'" + dArr[i] + "'");
				} else {
					throw new Exception("DataType not supported!" + f.getDataType());
				}				
				
				if(i+1 < features.size()) {
					values.append(",");
				}
				
				++i;
			}
			String insert = String.format(sqlInsert, values);
			em.getTransaction().begin();
			em.createNativeQuery(insert).executeUpdate();
			em.getTransaction().commit();
		}
	}
}
