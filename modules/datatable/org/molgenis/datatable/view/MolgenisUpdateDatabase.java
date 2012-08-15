package org.molgenis.datatable.view;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.pheno.Category;
import org.molgenis.pheno.Measurement;
import org.molgenis.pheno.ObservedValue;

public class MolgenisUpdateDatabase {

	public MolgenisUpdateDatabase() {
		// TODO Auto-generated constructor stub
	}

	public void UpdateDatabase(Database db, String targetID, String value,
			String measurement) throws DatabaseException {

		List<QueryRule> filterRules = new ArrayList<QueryRule>();
		filterRules.add(new QueryRule(ObservedValue.FEATURE_NAME,
				Operator.EQUALS, measurement));
		filterRules.add(new QueryRule(ObservedValue.TARGET_NAME,
				Operator.EQUALS, targetID));

		if (db.find(ObservedValue.class, new QueryRule(filterRules)).size() > 0) {

			ObservedValue newValue = db.find(ObservedValue.class,
					new QueryRule(filterRules)).get(0);

			String[] splitvalue = value.split("\\.");

			if (!newValue.getValue().equals(splitvalue[0])) {

				String featureName = newValue.getFeature_Name();
				Measurement m = db.find(
						Measurement.class,
						new QueryRule(Measurement.NAME, Operator.EQUALS,
								featureName)).get(0);
				if ("categorical".equals(m.getDataType())) {

					List<QueryRule> rules = new ArrayList<QueryRule>();
					rules.add(new QueryRule(Category.CODE_STRING,
							Operator.EQUALS, splitvalue[0]));
					rules.add(new QueryRule(Category.DESCRIPTION,
							Operator.EQUALS, splitvalue[1]));
					Category updatedCategory = db.find(Category.class,
							new QueryRule(rules)).get(0);

					m.setCategories(updatedCategory);

					db.update(m);

				} else {
					if (!newValue.getValue().equals(value)) {
						newValue.setValue(value);
						db.update(newValue);
					}
				}
			}
		}
	}
}
