//package org.molgenis.xgap.other.xqtlworkbench_lifelines.listeners;
//
//import javax.persistence.EntityManager;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Map;
//import java.util.List;
//import org.molgenis.framework.db.Database;
//import org.molgenis.organization.Investigation;
//import org.molgenis.pheno.Category;
//import org.molgenis.pheno.Measurement;
//import org.molgenis.pheno.ObservableFeature;
//import org.molgenis.protocol.Protocol;
//import org.molgenis.util.Tuple;
//import static ch.lambdaj.Lambda.*;
//import static org.hamcrest.Matchers.*;
////import static org.hamcrest.text.IsEqualIgnoringCase.*;
//
//public class VWCategoryListener extends ImportTupleListener {
//
//	private final EntityManager em;
//	private final Map<String, Protocol> protocols;
//	private final List<Category> categories = new ArrayList<Category>();
//	private final Investigation investigation;
//	
//	public VWCategoryListener(Map<String, Protocol> protocols, Investigation investigation, String name, Database db) {
//		super(name, db);
//		
//		this.em = db.getEntityManager();
//		this.protocols = protocols;
//		this.investigation = investigation;
//	}
//
//	@Override
//	public void handleLine(int line_number, Tuple tuple) throws Exception {
//		String tableName = tuple.getString("TABNAAM");
//		Protocol protocol = protocols.get(tableName);
//		
//		String fieldName = tuple.getString("VELD");
//		List<ObservableFeature> measurements = protocol.getFeatures();
//		List<ObservableFeature> filterMeasurements = filter(having(on(Measurement.class).getName(), equalToIgnoringCase(fieldName)), measurements);
//		Measurement measurement = (Measurement) filterMeasurements.get(0);
//		
//		Category category = new Category();
//		category.setInvestigation(investigation);
//		category.setCode_String(tuple.getString("VALLABELVAL"));
//		category.setLabel(tuple.getString("VALLABELABEL"));
//		category.setDescription(tuple.getString("VALLABELABEL"));
//		category.getCategoriesCollection().add(measurement); //add to measurment
//		category.setName(fieldName);
//		categories.add(category);
//	}
//
//	@Override
//	public void commit() throws Exception {
//		em.getTransaction().begin();
//		for(Category c : categories) {
//			em.persist(c);
//		}
//		em.flush();
//		em.getTransaction().commit();
//	}
//}
