package org.molgenis.lifelines.listeners;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import org.molgenis.framework.db.Database;
import org.molgenis.organization.Investigation;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservableFeature;
import org.molgenis.protocol.Protocol;
import org.molgenis.util.Tuple;
import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.*;

//import static org.hamcrest.text.IsEqualIgnoringCase.*;

public class VWCategoryListener extends ImportTupleListener {

	private final EntityManager em;
	private final Map<String, Protocol> protocols;
	private final List<Category> categories = new ArrayList<Category>();
	private final Investigation investigation;
	private final boolean shareMeasurements;

	public VWCategoryListener(Map<String, Protocol> protocols,
			Investigation investigation, String name, Database db,
			boolean shareMeasurements) {
		super(name, db);

		this.em = db.getEntityManager();
		this.protocols = protocols;
		this.investigation = investigation;
		this.shareMeasurements = shareMeasurements;
	}

	@Override
	public void handleLine(int line_number, Tuple tuple) throws Exception {
		String tableName = tuple.getString("TABNAAM");
		Protocol protocol = protocols.get(tableName);
		if(protocol == null) {
			return;
		}

		String fieldName = tuple.getString("VELD");
		if (!shareMeasurements) {
			fieldName = tableName + "_" + tuple.getString("VELD");
		}

		List<ObservableFeature> measurements = (List<ObservableFeature>) protocol.getFeatures();
		List<ObservableFeature> filterMeasurements = filter(
				having(on(Measurement.class).getName(),
						equalToIgnoringCase(fieldName)), measurements);
		Measurement measurement = (Measurement) filterMeasurements.get(0);

		Category category = new Category();
		category.setInvestigation(investigation);
		category.setCode_String(tuple.getString("VALLABELVAL"));
		category.setLabel(tuple.getString("VALLABELABEL"));
		category.setDescription(tuple.getString("VALLABELABEL"));
		category.getCategoriesMeasurementCollection().add(measurement); // add to
		//measurement.getCategories(db)

		measurement.getCategories().add(category);
																// measurment
		category.setName(fieldName + "_" + category.getLabel());
		categories.add(category);
	}

	@Override
	public void commit() throws Exception {
		try {
			em.getTransaction().begin();
			for (Category c : categories) {
				em.persist(c);
			}
			em.flush();
			em.getTransaction().commit();
		} catch (Exception ex) {
			throw ex;
		}
	}
}
