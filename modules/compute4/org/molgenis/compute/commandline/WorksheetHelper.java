package org.molgenis.compute.commandline;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.molgenis.framework.db.Database;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
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
	Logger logger = Logger.getLogger(WorksheetHelper.class);

	static List<String> fieldsToInclude = Arrays
			.asList(new String[]
			{ "sample_name", "status", "sampletype", "investigation_name",
					"flowcell_name", "lane", "barcode_barcode",
					"capturing_capturing" });

	/** Read tuples of user parameters from a file */
	public List<Tuple> readTuplesFromFile(File worksheetFile) throws Exception
	{
		// load the worksheet file into a list of tuple
		// todo: make work for Excel.
		CsvReader reader = new CsvFileReader(worksheetFile);
		final List<Tuple> tuples = new ArrayList<Tuple>();
		int line_number = 1;
		for (Tuple tuple : reader)
		{
			tuple.set("line_number", line_number++);
			tuples.add(tuple);

			// logger.warn("renamed externalSampleId to sample");
			// tuple.set("sample",tuple.getString("externalSampleId"));
			// tuple.set("machine",tuple.getString("sequencer"));
			// tuple.set("date",tuple.getString("sequencingStartDate"));
			// tuple.set("capturing",tuple.getString("capturingKit"));
		}

		return tuples;
	}
//
//	/**
//	 * Default convertor. Assumes the worksheet is build up based on Lane only.
//	 * 
//	 * @param worksheet
//	 * @param sampleList
//	 * @param laneList
//	 * @param trioList
//	 * @throws Exception
//	 */
//	public void convertTuplesToEntites(List<Tuple> worksheet,	List<NgsSample> sampleList, List<LibraryLane> laneList, List<Trio> trioList)
//			throws Exception
//	{
//		this.convertTuplesToEntites(worksheet, LibraryLane.class, sampleList,
//				laneList, trioList);
//	}
//
//	/**
//	 * Load a worksheet into list of NgsSampe and LaneList. We need that for
//	 * generation of the workflow to allow both Sample and Lane iteration.
//	 * 
//	 * @param trioList
//	 * 
//	 * @throws Exception
//	 */
//	public void convertTuplesToEntites(List<Tuple> worksheet,
//			Class<? extends ObservationElement> iterationLevel,
//			List<NgsSample> sampleList, List<LibraryLane> laneList,
//			List<Trio> trioList) throws Exception
//	{
//		// problem is that LibraryLane don't have unique names. To be solved
//		// using decorator.
//		// LibraryLane.name = '${flowcell}_${lane}' and after barcoding
//		// '${flowcell}_${lane}_${barcode}'
//
//		// TODO TODO TODO
//		if (iterationLevel.equals(LibraryLane.class))
//		{
//			Map<String, NgsSample> uniqueSamples = new TreeMap<String, NgsSample>();
//
//			for (Tuple t : worksheet)
//			{
//				// rename 'flowcell' to 'flowcell_name'
//				t.set("flowcell_name", t.getString("flowcell"));
//				t.set("flowcell", null);
//
//				// rename 'library' to 'library_name'
//				t.set("library_name", t.getString("library"));
//				t.set("library", null);
//
//				// rename 'externalSampleId' to 'sample_name'
//				t.set("sample_name", t.getString("externalSampleId"));
//				t.set("externalSampleId", null);
//
//				LibraryLane lane = new LibraryLane();
//				lane.set(t);
//				laneList.add(lane);
//
//				// only add sample if not there yet
//				NgsSample s = new NgsSample();
//				s.set(t);
//				s.setName(t.getString("sample_name"));
//				uniqueSamples.put(s.getName(), s);
//
//			}
//			sampleList.addAll(uniqueSamples.values());
//		}
//		else
//		{
//			throw new RuntimeException("iterationLevel "
//					+ iterationLevel.getSimpleName() + " not yet supported");
//		}
//
//	}
//
//	/**
//	 * Project all data on lanes. Each row is one laneBarcode. Sample info
//	 * associated to LaneBarcode are repeated over each associated lane.
//	 * 
//	 * @param fieldsToInclude
//	 */
//	public List<Tuple> convertLanesToTuples(List<LibraryLane> laneList,
//			List<NgsSample> sampleList, List<Trio> trioList)
//	{
//		// load trios into hashmap
//		Map<Integer, Trio> trios = this.createMap(trioList);
//		// load samples into hashmap of id -> Sample
//		Map<Integer, NgsSample> samples = this.createMap(sampleList);
//		// are we already regretting not having libraries?
//		Map<Integer, LibraryLane> lanes = this.createMap(laneList);
//		// do we need separate lane vs laneBarcode???
//
//		List<Tuple> result = new ArrayList<Tuple>();
//
//		for (LibraryLane l : lanes.values())
//		{
//			Tuple t = new SimpleTuple();
//			for (String field : l.getFields())
//			{
//				if (field.equals(l.getIdField()))
//				{
//					// ignore id
//				}
//				else if (fieldsToInclude.contains(field))
//				{
//					t.set(field, l.get(field));
//				}
//			}
//
//			// attach sample info
//			NgsSample s = samples.get(l.getSample_Id());
//			for (String field : s.getFields())
//			{
//				// ignore name
//				if (field.equalsIgnoreCase("name"))
//				{
//					//
//				}
//				else if (fieldsToInclude.contains(field))
//				{
//					t.set(field, s.get(field));
//				}
//			}
//
//			result.add(t);
//
//		}
//
//		return result;
//	}
//
//	/**
//	 * Project all data one samples. That means each row is a sample; if there
//	 * are multiple lane/barcodes for a sample then they there as list for
//	 * iteration purposes.
//	 * 
//	 * @param fieldsToInclude
//	 * 
//	 * @param samples
//	 * @param lanes
//	 * @param dir
//	 * @return
//	 */
//	public List<Tuple> convertSamplesToTuples(List<NgsSample> sampleList,
//			List<LibraryLane> laneList)
//	{
//		// load samples into hashmap of id -> Sample
//		Map<Integer, NgsSample> samples = this.createMap(sampleList);
//		// are we already regretting not having libraries?
//		Map<Integer, LibraryLane> lanes = this.createMap(laneList);
//		// do we need separate lane vs laneBarcode???
//
//		List<Tuple> result = new ArrayList<Tuple>();
//
//		for (NgsSample s : samples.values())
//		{
//			Tuple currentRow = new SimpleTuple();
//
//			for (String field : s.getFields())
//			{
//				// alias the 'name' to 'sample_name' and 'sample.
//				if (field.equalsIgnoreCase("name"))
//				{
//					currentRow.set("sample", s.getName());
//					currentRow.set("sample_name", s.getName());
//				}
//				else if (fieldsToInclude.contains(field))
//				{
//					currentRow.set(field, s.get(field));
//				}
//			}
//
//			// generaly you would not need but just for the case of add lane
//			// details
//			for (LibraryLane l : lanes.values())
//			{
//
//				if (l.getSample_Name().equals(s.getName()))
//
//				{
//					for (String field : l.getFields())
//					{
//						if (fieldsToInclude.contains(field.toLowerCase())
//								&& l.get(field) != null)
//						{
//							List<String> values;
//							if (currentRow.getList(field) != null)
//							{
//								values = (List<String>) currentRow
//										.getList(field);
//
//							}
//							else
//							{
//								values = new ArrayList<String>();
//
//							}
//							values.add(l.get(field).toString());
//
//							currentRow.set(field, values);
//						}
//					}
//				}
//			}
//			result.add(currentRow);
//		}
//
//		return result;
//	}
//
//	private <E extends ObservationElement> Map<Integer, E> createMap(
//			List<E> entities)
//	{
//		Map<Integer, E> entityMap = new TreeMap<Integer, E>();
//		for (E e : entities)
//		{
//			entityMap.put(e.getId(), e);
//		}
//		return entityMap;
//	}
//
//	public List<Tuple> convertTriosToTuples(List<Trio> trios,
//			List<NgsSample> samples, List<LibraryLane> lanes)
//	{
//		throw new UnsupportedOperationException(
//				"This method is not yet implemented but planned");
//	}
//
//	/**
//	 * JUST FOR TESTING
//	 * 
//	 * @throws Exception
//	 */
//	public static void main(String[] args) throws Exception
//	{
//		Database db = DatabaseFactory.create();
//
//		WorksheetHelper test = new WorksheetHelper();
//
//		System.out
//				.println("Part 1: load from file into Trio, NgsSample, LaneLibrary");
//
//		System.out.println("Load worksheet");
//		File f = new File(
//				"/Users/mswertz/Dropbox/NGS quality report/compute/New_Molgenis_Compute_for_GoNL/TestSampleList.csv");
//		List<Tuple> tuples = test.readTuplesFromFile(f);
//		System.out.println("Tuples in file (" + f.getPath() + ")");
//		for (Tuple t : tuples)
//		{
//			System.out.println(t);
//		}
//
//		System.out.println("Convert into Trio, NgsSample, LaneLibrary");
//		List<Trio> trioList = new ArrayList<Trio>();
//		List<NgsSample> sampleList = new ArrayList<NgsSample>();
//		List<LibraryLane> laneList = new ArrayList<LibraryLane>();
//
//		test.convertTuplesToEntites(tuples, sampleList, laneList, trioList);
//
//		sampleList = db.find(NgsSample.class);
//		laneList = db.find(LibraryLane.class);
//		trioList = db.find(Trio.class);
//
//		tuples = test.convertSamplesToTuples(sampleList, laneList);
//		System.out.println("SAMPLES (tuples)");
//		for (Tuple t : tuples)
//		{
//			System.out.println(t);
//		}
//
//		System.out.println("LANES (tuples)");
//		tuples = test.convertLanesToTuples(laneList, sampleList, trioList);
//		for (Tuple t : tuples)
//		{
//			System.out.println(t);
//		}
//
//		// convert worksheet back to lanes
//		System.out.println("converting to NgsSample and LibraryLane");
//
//		// reset the lists
//		sampleList = new ArrayList<NgsSample>();
//		laneList = new ArrayList<LibraryLane>();
//
//		test.convertTuplesToEntites(tuples, LibraryLane.class, sampleList,
//				laneList, trioList);
//
//		System.out.println("LANES (entities)");
//		for (LibraryLane l : laneList)
//		{
//			System.out.println(l);
//		}
//
//		System.out.println("SAMPLES (entities)");
//		for (NgsSample s : sampleList)
//		{
//			System.out.println(s);
//		}
//
//	}
}
