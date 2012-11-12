/* 
 * 
 * generator:   org.molgenis.generators.csv.CsvExportGen 4.0.0-testing
 *
 * 
 * THIS FILE HAS BEEN GENERATED, PLEASE DO NOT EDIT!
 */
package org.molgenis.generators.csv;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.model.Category;
import org.molgenis.model.Characteristic;
import org.molgenis.model.Feature;
import org.molgenis.model.MolgenisModelException;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.Entity;

public class CsvExport
{
	static Logger logger = Logger.getLogger(CsvExport.class.getSimpleName());

	/**
	 * Default export all using a target directory and a database to export
	 * 
	 * @param directory
	 * @param db
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db) throws Exception
	{
		exportAll(directory, db, true, new QueryRule[]
		{});
	}

	/**
	 * Export all using a set of QueryRules used for all entities if applicable
	 * to that entity
	 * 
	 * @param directory
	 * @param db
	 * @param rules
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db, QueryRule... rules) throws Exception
	{
		exportAll(directory, db, true, rules);
	}

	/**
	 * Export all where a boolean skip autoid fields forces an ignore of the
	 * auto id field ("id")
	 * 
	 * @param directory
	 * @param db
	 * @param skipAutoId
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db, boolean skipAutoId) throws Exception
	{
		exportAll(directory, db, skipAutoId, new QueryRule[]
		{});
	}

	/**
	 * Export all with both a boolean skipAutoId and a set of QueryRules to
	 * specify both the skipping of auto id, and applying of a filter
	 * 
	 * @param directory
	 * @param db
	 * @param skipAutoId
	 * @param rules
	 * @throws Exception
	 */
	public void exportAll(File directory, Database db, boolean skipAutoId, QueryRule... rules) throws Exception
	{
		exportCharacteristic(db, new File(directory + "/characteristic.txt"), skipAutoId ? Arrays.asList(new String[]
		{ "Identifier", "Name", "__Type", "description" }) : null, rules);
		exportFeature(db, new File(directory + "/feature.txt"), skipAutoId ? Arrays.asList(new String[]
		{ "Identifier", "Name", "__Type", "description", "dataType" }) : null, rules);
		exportCategory(db, new File(directory + "/category.txt"), skipAutoId ? Arrays.asList(new String[]
		{ "feature_Identifier" }) : null, rules);

		logger.debug("done");
	}

	/**
	 * Export without system tables.
	 */
	public void exportRegular(File directory, Database db, boolean skipAutoId) throws Exception
	{
		exportRegular(directory, db, skipAutoId, new QueryRule[]
		{});
	}

	/**
	 * Export without system tables.
	 */
	public void exportRegular(File directory, Database db, boolean skipAutoId, QueryRule... rules) throws Exception
	{
		exportCharacteristic(db, new File(directory + "/characteristic.txt"), skipAutoId ? Arrays.asList(new String[]
		{ "Identifier", "Name", "__Type", "description" }) : null, rules);
		exportFeature(db, new File(directory + "/feature.txt"), skipAutoId ? Arrays.asList(new String[]
		{ "Identifier", "Name", "__Type", "description", "dataType" }) : null, rules);
		exportCategory(db, new File(directory + "/category.txt"), skipAutoId ? Arrays.asList(new String[]
		{ "feature_Identifier" }) : null, rules);

		logger.debug("done");
	}

	public void exportAll(File directory, List... entityLists) throws Exception
	{
		for (List<? extends Entity> l : entityLists)
			if (l.size() > 0)
			{
				if (l.get(0).getClass().equals(Characteristic.class)) exportCharacteristic(l, new File(directory
						+ "/characteristic.txt"));
				if (l.get(0).getClass().equals(Feature.class)) exportFeature(l, new File(directory + "/feature.txt"));
				if (l.get(0).getClass().equals(Category.class)) exportCategory(l, new File(directory + "/category.txt"));
			}

		logger.debug("done");
	}

	/**
	 * Export while excluding or including certain entity types. Defaults set:
	 * skip autoId, no QueryRules. If exclusion is set to true, the specialCases
	 * are used to exlude those entities from the export (entities not in list
	 * are exported). If exclusion is set to false, the specialCases are used to
	 * include those entities in the export (only entities in list are
	 * exported).
	 */
	public void exportSpecial(File directory, Database db, List<Class<? extends Entity>> specialCases, boolean exclusion)
			throws Exception
	{
		exportSpecial(directory, db, true, specialCases, exclusion, new QueryRule[]
		{});
	}

	/**
	 * Export while excluding or including certain entity types. If exclusion is
	 * set to true, the specialCases are used to exlude those entities from the
	 * export (entities not in list are exported). If exclusion is set to false,
	 * the specialCases are used to include those entities in the export (only
	 * entities in list are exported). TODO: Could maybe replace exportAll(File
	 * directory, List ... entityLists) ?
	 */
	public void exportSpecial(File directory, Database db, boolean skipAutoId,
			List<Class<? extends Entity>> specialCases, boolean exclusion, QueryRule... rules) throws Exception
	{
		if ((exclusion && !specialCases.contains(Characteristic.class))
				|| (!exclusion && specialCases.contains(Characteristic.class)))
		{
			exportCharacteristic(db, new File(directory + "/characteristic.txt"),
					skipAutoId ? Arrays.asList(new String[]
					{ "Identifier", "Name", "__Type", "description" }) : null, rules);
		}
		if ((exclusion && !specialCases.contains(Feature.class))
				|| (!exclusion && specialCases.contains(Feature.class)))
		{
			exportFeature(db, new File(directory + "/feature.txt"), skipAutoId ? Arrays.asList(new String[]
			{ "Identifier", "Name", "__Type", "description", "dataType" }) : null, rules);
		}
		if ((exclusion && !specialCases.contains(Category.class))
				|| (!exclusion && specialCases.contains(Category.class)))
		{
			exportCategory(db, new File(directory + "/category.txt"), skipAutoId ? Arrays.asList(new String[]
			{ "feature_Identifier" }) : null, rules);
		}

		logger.debug("done");
	}

	private QueryRule[] matchQueryRulesToEntity(org.molgenis.model.elements.Entity e, QueryRule... rules)
			throws MolgenisModelException
	{
		ArrayList<QueryRule> tmpResult = new ArrayList<QueryRule>();
		for (QueryRule q : rules)
		{
			if (!(e.getAllField(q.getField()) == null))
			{
				tmpResult.add(q); // field is okay for this entity
			}
			// special case: eg. investigation.name -> if current entity is
			// 'investigation', use field 'name'
			String[] splitField = q.getField().split("\\.");
			if (splitField.length == 2)
			{
				if (e.getName().equals(splitField[0]))
				{
					QueryRule copy = new QueryRule(q);
					copy.setField(splitField[1]);
					tmpResult.add(copy);
				}
			}
		}
		QueryRule[] result = new QueryRule[tmpResult.size()];
		for (int i = 0; i < result.length; i++)
		{
			result[i] = tmpResult.get(i);
		}
		return result;
	}

	/**
	 * export Characteristic to file.
	 * 
	 * @param db
	 *            the database to export from.
	 * @param f
	 *            the file to export to.
	 */
	public void exportCharacteristic(Database db, File f, List<String> fieldsToExport, QueryRule... rules)
			throws DatabaseException, IOException, ParseException, MolgenisModelException
	{
		if (db.count(Characteristic.class, new QueryRule("__Type", Operator.EQUALS, "Characteristic")) > 0)
		{

			org.molgenis.framework.db.Query<Characteristic> query = db.query(Characteristic.class);
			QueryRule type = new QueryRule("__Type", Operator.EQUALS, "Characteristic");
			query.addRules(type);
			QueryRule[] newRules = matchQueryRulesToEntity(db.getMetaData().getEntity("Characteristic"), rules);
			query.addRules(newRules);
			int count = query.count();
			if (count > 0)
			{
				CsvFileWriter characteristicWriter = new CsvFileWriter(f);
				query.find(characteristicWriter, fieldsToExport);
				characteristicWriter.close();
			}
		}
	}

	public void exportCharacteristic(List<? extends Entity> entities, File file) throws IOException,
			MolgenisModelException
	{
		if (entities.size() > 0)
		{
			// filter nulls
			List<String> fields = entities.get(0).getFields();
			List<String> notNulls = new ArrayList<String>();

			for (String f : fields)
			{
				for (Entity e : entities)
				{
					if (e.get(f) != null)
					{
						notNulls.add(f);
						break;
					}
				}
			}

			// write
			CsvFileWriter characteristicWriter = new CsvFileWriter(file, notNulls);
			characteristicWriter.writeHeader();
			for (Entity e : entities)
			{
				characteristicWriter.writeRow((org.molgenis.util.Entity) e);
			}
			characteristicWriter.close();
		}
	}

	/**
	 * export Feature to file.
	 * 
	 * @param db
	 *            the database to export from.
	 * @param f
	 *            the file to export to.
	 */
	public void exportFeature(Database db, File f, List<String> fieldsToExport, QueryRule... rules)
			throws DatabaseException, IOException, ParseException, MolgenisModelException
	{
		if (db.count(Feature.class, new QueryRule("__Type", Operator.EQUALS, "Feature")) > 0)
		{

			org.molgenis.framework.db.Query<Feature> query = db.query(Feature.class);
			QueryRule type = new QueryRule("__Type", Operator.EQUALS, "Feature");
			query.addRules(type);
			QueryRule[] newRules = matchQueryRulesToEntity(db.getMetaData().getEntity("Feature"), rules);
			query.addRules(newRules);
			int count = query.count();
			if (count > 0)
			{
				CsvFileWriter featureWriter = new CsvFileWriter(f);
				query.find(featureWriter, fieldsToExport);
				featureWriter.close();
			}
		}
	}

	public void exportFeature(List<? extends Entity> entities, File file) throws IOException, MolgenisModelException
	{
		if (entities.size() > 0)
		{
			// filter nulls
			List<String> fields = entities.get(0).getFields();
			List<String> notNulls = new ArrayList<String>();

			for (String f : fields)
			{
				for (Entity e : entities)
				{
					if (e.get(f) != null)
					{
						notNulls.add(f);
						break;
					}
				}
			}

			// write
			CsvFileWriter featureWriter = new CsvFileWriter(file, notNulls);
			featureWriter.writeHeader();
			for (Entity e : entities)
			{
				featureWriter.writeRow((org.molgenis.util.Entity) e);
			}
			featureWriter.close();
		}
	}

	/**
	 * export Category to file.
	 * 
	 * @param db
	 *            the database to export from.
	 * @param f
	 *            the file to export to.
	 */
	public void exportCategory(Database db, File f, List<String> fieldsToExport, QueryRule... rules)
			throws DatabaseException, IOException, ParseException, MolgenisModelException
	{
		if (db.count(Category.class) > 0)
		{

			org.molgenis.framework.db.Query<Category> query = db.query(Category.class);

			QueryRule[] newRules = matchQueryRulesToEntity(db.getMetaData().getEntity("Category"), rules);
			query.addRules(newRules);
			int count = query.count();
			if (count > 0)
			{
				CsvFileWriter categoryWriter = new CsvFileWriter(f);
				query.find(categoryWriter, fieldsToExport);
				categoryWriter.close();
			}
		}
	}

	public void exportCategory(List<? extends Entity> entities, File file) throws IOException, MolgenisModelException
	{
		if (entities.size() > 0)
		{
			// filter nulls
			List<String> fields = entities.get(0).getFields();
			List<String> notNulls = new ArrayList<String>();

			for (String f : fields)
			{
				for (Entity e : entities)
				{
					if (e.get(f) != null)
					{
						notNulls.add(f);
						break;
					}
				}
			}

			// write
			CsvFileWriter categoryWriter = new CsvFileWriter(file, notNulls);
			categoryWriter.writeHeader();
			for (Entity e : entities)
			{
				categoryWriter.writeRow((org.molgenis.util.Entity) e);
			}
			categoryWriter.close();
		}
	}
}