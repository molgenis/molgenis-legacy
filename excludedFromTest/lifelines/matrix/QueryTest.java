//package lifelines.Matrix;
//
//import java.util.Iterator;
//import java.util.List;
//
//import javax.persistence.EntityManager;
//
//import org.molgenis.framework.db.DatabaseException;
//
//import app.JpaDatabase;
//
//public class QueryTest {
//
//	/**
//	 * @param args
//	 * @throws DatabaseException 
//	 */
//	public static void main(String[] args) throws DatabaseException {
//		String query = "SELECT DISTINCT SUBSTRING(oFeature.name, LOCATE('.',oFeature.name) + 1) " +
//				"FROM ObservableFeature oFeature " +
//				"WHERE SUBSTRING(oFeature.name, 1, LOCATE('.',oFeature.name) - 1) = :featureGroupName"
//				;
////String query = "SELECT ov.observationTarget.id FROM ObservedValue ov WHERE ov.observableFeature.name = 'patient.geslacht' AND ov.value = '1'";
//		
//		
////		String query = "SELECT ov.observationTarget.id FROM ObservedValue ov " +
////				"WHERE ov.observableFeature.name = 'patient.geslacht' " +
////				"AND ov.value = 1 "  +				
////				"AND ov.observationTarget.id IN (11872, 11929, 11986)";
//		JpaDatabase db = new JpaDatabase();
//		EntityManager em = db.getEntityManager();
//		List rs = em.createQuery(query).setParameter("featureGroupName", "huisarts").
//		getResultList();
//		
//		
//		for(Iterator i = rs.iterator(); i.hasNext();) {
//			System.out.println(i.next().toString());
//		}
//		System.out.println("EIND");
//	}
//
//}
