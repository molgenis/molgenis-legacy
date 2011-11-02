package org.molgenis.compute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.ngs.LibraryLane;
import org.molgenis.ngs.NgsSample;
import org.molgenis.pheno.ObservationElement;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

import app.DatabaseFactory;

/**
 * This class can convert from Lists of entities to worksheet and back. Used for
 * preparing variables for Freemarker (but can also be used for import/export).
 * 
 * See 'fieldsToInclude' to see what fields are included. You can choose to
 * create the worksheet with each row = NgsSample or each row = LibraryLane.
 * 
 * Each NgsSample can have multiple LibraryLane attached, so when each row is
 * NgsSample you will have repeated values.
 * 
 * NB: in the future we will want to simply include the entities directly and
 * then use ${sample.name} and ${lane.barcode} etc.!!!
 */
public class WorksheetHelper
{

	/**
	 * Convert lists of samples and lists of lanes into a worksheet (list of
	 * tuples)
	 */
	public List<Tuple> entitiesToWorkheet(
			Class<? extends ObservationElement> iterationLevel,
			List<NgsSample> sampleList, List<LibraryLane> laneList)
	{
		List<String> fieldsToInclude = Arrays.asList(new String[]
		{ "sample_name", "status", "sampletype", "investigation_name",
				"flowcell_name", "lane", "barcode_barcode",
				"capturing_capturing" });

		// TODO: create Trio entity

		// load samples into hashmap of id -> Sample
		Map<Integer, NgsSample> samples = this.createMap(sampleList);
		// are we already regretting not having libraries?
		Map<Integer, LibraryLane> lanes = this.createMap(laneList);
		// do we need separate lane vs laneBarcode???

		// ugly, TODO is generalization using JDBCMetaDatabase
		List<Tuple> worksheet = new ArrayList<Tuple>();
		if (iterationLevel.equals(NgsSample.class))
		{
			worksheet = this.samplesToTuples(fieldsToInclude, samples, lanes);
		}
		else if (iterationLevel.equals(LibraryLane.class))
		{
			worksheet = this.lanesToTuples(fieldsToInclude, lanes, samples);
		}
		else
		{
			throw new RuntimeException("iterationLevel "
					+ iterationLevel.getSimpleName() + " not yet supported");
		}

		return worksheet;
	}

	/**
	 * Load a worksheet into list of NgsSampe and LaneList. We need that for
	 * generation of the workflow to allow both Sample and Lane iteration.
	 * 
	 * @throws Exception
	 */
	public void worksheetToEntites(List<Tuple> worksheet,
			Class<? extends ObservationElement> iterationLevel,
			List<NgsSample> sampleList, List<LibraryLane> laneList)
			throws Exception
	{
		// problem is that LibraryLane don't have unique names. To be solved
		// using decorator.
		// LibraryLane.name = '${flowcell}_${lane}' and after barcoding
		// '${flowcell}_${lane}_${barcode}'

		// TODO TODO TODO
		if (iterationLevel.equals(LibraryLane.class))
		{
			Map<String, NgsSample> uniqueSamples = new TreeMap<String, NgsSample>();

			for (Tuple t : worksheet)
			{
				LibraryLane lane = new LibraryLane();
				lane.set(t);
				laneList.add(lane);

				// only add sample if not there yet
				NgsSample s = new NgsSample();
				s.set(t);
				s.setName(t.getString("sample_name"));
				uniqueSamples.put(s.getName(), s);

			}
			sampleList.addAll(uniqueSamples.values());
		}
		else
		{
			throw new RuntimeException("iterationLevel "
					+ iterationLevel.getSimpleName() + " not yet supported");
		}

	}

	/**
	 * Same method as entitiesToWorksheet but then loading al data data from
	 * database
	 */
	public List<Tuple> databaseToWorksheet(
			Class<? extends ObservationElement> iterationLevel, Database db)
			throws DatabaseException
	{
		List<NgsSample> samples = db.find(NgsSample.class);
		List<LibraryLane> lanes = db.find(LibraryLane.class);
		return entitiesToWorkheet(iterationLevel, samples, lanes);
	}

	/**
	 * Project all data on lanes. Each row is one laneBarcode. Sample info
	 * associated to LaneBarcode are repeated over each associated lane.
	 * 
	 * @param fieldsToInclude
	 */
	private List<Tuple> lanesToTuples(List<String> fieldsToInclude,
			Map<Integer, LibraryLane> lanes, Map<Integer, NgsSample> samples)
	{
		List<Tuple> result = new ArrayList<Tuple>();

		for (LibraryLane l : lanes.values())
		{
			Tuple t = new SimpleTuple();
			for (String field : l.getFields())
			{
				if (field.equals(l.getIdField()))
				{
					// ignore id
				}
				else if (fieldsToInclude.contains(field))
				{
					t.set(field, l.get(field));
				}
			}

			// attach sample info
			NgsSample s = samples.get(l.getSample_Id());
			for (String field : s.getFields())
			{
				// ignore name
				if (field.equalsIgnoreCase("name"))
				{
					//
				}
				else if (fieldsToInclude.contains(field))
				{
					t.set(field, s.get(field));
				}
			}

			result.add(t);

		}

		return result;
	}

	/**
	 * Project all data one samples. That means each row is a sample; if there
	 * are multiple lane/barcodes for a sample then they there as list for
	 * iteration purposes.
	 * 
	 * @param fieldsToInclude
	 * 
	 * @param samples
	 * @param lanes
	 * @param dir
	 * @return
	 */
	private List<Tuple> samplesToTuples(List<String> fieldsToInclude,
			Map<Integer, NgsSample> samples, Map<Integer, LibraryLane> lanes)
	{

		List<Tuple> result = new ArrayList<Tuple>();

		for (NgsSample s : samples.values())
		{
			Tuple currentRow = new SimpleTuple();

			for (String field : s.getFields())
			{
				// alias the 'name' to 'sample_name' and 'sample.
				if (field.equalsIgnoreCase("name"))
				{
					currentRow.set("sample", s.getName());
					currentRow.set("sample_name", s.getName());
				}
				else if (fieldsToInclude.contains(field))
				{
					currentRow.set(field, s.get(field));
				}
			}

			// generaly you would not need but just for the case of add lane
			// details
			for (LibraryLane l : lanes.values())
			{

				if (l.getSample_Name().equals(s.getName()))

				{
					for (String field : l.getFields())
					{
						if (fieldsToInclude.contains(field.toLowerCase())
								&& l.get(field) != null)
						{
							List<String> values;
							if (currentRow.getList(field) != null)
							{
								values = (List<String>) currentRow
										.getList(field);

							}
							else
							{
								values = new ArrayList<String>();

							}
							values.add(l.get(field).toString());

							currentRow.set(field, values);
						}
					}
				}
			}
			result.add(currentRow);
		}

		return result;
	}

	private <E extends ObservationElement> Map<Integer, E> createMap(
			List<E> entities)
	{
		Map<Integer, E> entityMap = new TreeMap<Integer, E>();
		for (E e : entities)
		{
			entityMap.put(e.getId(), e);
		}
		return entityMap;
	}

	/**
	 * JUST FOR TESTING
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		Database db = DatabaseFactory.create();

		List<Tuple> worksheet = new WorksheetHelper().databaseToWorksheet(
				NgsSample.class, db);
		System.out.println("SAMPLES (tuples)");
		for (Tuple t : worksheet)
		{
			System.out.println(t);
		}

		System.out.println("LANES (tuples)");
		worksheet = new WorksheetHelper().databaseToWorksheet(
				LibraryLane.class, db);
		for (Tuple t : worksheet)
		{
			System.out.println(t);
		}

		// convert worksheet back to lanes
		System.out.println("converting to NgsSample and LibraryLane");
		List<NgsSample> sampleList = new ArrayList<NgsSample>();
		List<LibraryLane> laneList = new ArrayList<LibraryLane>();
		new WorksheetHelper().worksheetToEntites(worksheet, LibraryLane.class,
				sampleList, laneList);

		System.out.println("LANES (entities)");
		for (LibraryLane l : laneList)
		{
			System.out.println(l);
		}

		System.out.println("SAMPLES (entities)");
		for (NgsSample s : sampleList)
		{
			System.out.println(s);
		}

	}
}
