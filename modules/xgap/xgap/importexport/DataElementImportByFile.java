package xgap.importexport;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.core.Nameable;
import org.molgenis.data.Data;
import org.molgenis.data.DecimalDataElement;
import org.molgenis.data.TextDataElement;
import org.molgenis.framework.db.Database;
import org.molgenis.framework.db.DatabaseException;
import org.molgenis.framework.db.QueryRule;
import org.molgenis.framework.db.QueryRule.Operator;
import org.molgenis.organization.Investigation;
import org.molgenis.organization.InvestigationElement;
import org.molgenis.pheno.ObservationTarget;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

public class DataElementImportByFile
{

	// Logger logger = Logger.getLogger(this.class.getSimpleName());

	private final static int BATCH_SIZE = 10000;
	private Database db = null;

	public DataElementImportByFile(Database db)
	{
		this.db = db;
	}

	private String addedInvestigationElements;

	public String ImportByFile(File file, final Data selectedMatrix,
			boolean useTx, boolean addNewDataObject, boolean allowAdding,
			boolean onlyHead) throws Exception
	{

		if (useTx)
		{
			db.beginTx();
		}

		try
		{

			addedInvestigationElements = "";

			// Extract row/col names needed from file
			CsvFileReader csvFile = new CsvFileReader(file);

			// note that the first colname is empty because it is above the
			// rownames
			final List<String> colNames = csvFile.colnames();
			final List<String> rowNames = csvFile.rownames();

			Class colClass = null;
			if (selectedMatrix.getFeatureType().equals("AUTO_DETECT"))
			{

				// get first row element, query db and find the type
				// FIXME: does ObservationTarget work for this ?
				ObservationTarget firstColElem = null;
				List<ObservationTarget> firstCol = db.find(
						ObservationTarget.class, new QueryRule("name",
								Operator.EQUALS, colNames.get(1)));
				if (firstCol.size() > 0)
				{
					firstColElem = firstCol.get(0);
				}
				else
				{
					throw new DatabaseException(
							"Autodetect columns failed. Did you import the correct annotations?");
				}

				// get type, set class to type
				String firstColElemType = firstColElem.get__Type();
				// logger.debug("type: # " + firstColElemType + " #");
				colClass = db.getClassForName(firstColElemType);
				selectedMatrix.setFeatureType(colClass.getSimpleName());
				// logger.debug("colclass: " + colClass.getSimpleName());
			}
			else
			{
				colClass = db.getClassForName(selectedMatrix.getFeatureType());
			}

			Class rowClass = null;
			if (selectedMatrix.getTargetType().equals("AUTO_DETECT"))
			{
				// get first row element, query db and find the type
				// FIXME: does ObservationTarget work for this ?
				ObservationTarget firstRowElem = null;
				List<ObservationTarget> firstRow = db.find(
						ObservationTarget.class, new QueryRule("name",
								Operator.EQUALS, rowNames.get(1)));
				if (firstRow.size() > 0)
				{
					firstRowElem = firstRow.get(0);
				}
				else
				{
					throw new DatabaseException(
							"Autodetect rows failed. Did you import the correct annotations?");
				}

				// get type, set class to type
				String firstRowElemType = firstRowElem.get__Type();
				rowClass = db.getClassForName(firstRowElemType);
				selectedMatrix.setTargetType(rowClass.getSimpleName());
				// logger.debug("rowclass: " + rowClass.getSimpleName());
			}
			else
			{
				rowClass = db.getClassForName(selectedMatrix.getTargetType());
			}

			if (selectedMatrix.getValueType().equals("AUTO_DETECT"))
			{
				selectedMatrix.setValueType(probeFirstFileElement(file));
			}

			// TODO: add check whether it already exists in the db? is this
			// allowed?
			if (addNewDataObject)
			{
				if (db.find(
						Data.class,
						new QueryRule("name", Operator.EQUALS, selectedMatrix
								.getName()),
						new QueryRule("study", Operator.EQUALS, selectedMatrix
								.getInvestigation())).size() > 0)
				{
					// selectedMatrix is already in the database (identified by
					// 'name' plus 'investigation'), throw error
					throw new DatabaseException("Data object with name '"
							+ selectedMatrix.getName() + "' already exists.");
				}
				else
				{
					// add selectedMatrix
					db.add(selectedMatrix);
				}
			}
			else
			{
				// do not add selectedMatrix, but check whether the matrix is
				// actually in the database (identified by 'name' plus
				// 'investigation')
				if (db.find(
						Data.class,
						new QueryRule(Data.NAME, Operator.EQUALS,
								selectedMatrix.getName()),
						new QueryRule(Data.INVESTIGATION, Operator.EQUALS,
								selectedMatrix.getInvestigation_Id())).size() > 0)
				{
					// it's present in the database
				}
				else
				{
					// it's not present, throw error
					throw new DatabaseException("Data object with name '"
							+ selectedMatrix.getName() + "' does not exist.");
				}
			}

			final Map<String, Integer> colIds = getDimensionIds(allowAdding,
					selectedMatrix, colClass,
					colNames.subList(1, colNames.size()));

			final Map<String, Integer> rowIds = getDimensionIds(allowAdding,
					selectedMatrix, rowClass, rowNames);

			// ADD DATA elements
			final List<TextDataElement> tde = new ArrayList<TextDataElement>();
			final List<DecimalDataElement> dde = new ArrayList<DecimalDataElement>();

			final FinalInteger row_index = new FinalInteger(0);

			final Investigation inv = db.find(
					Investigation.class,
					new QueryRule(Investigation.ID, Operator.EQUALS,
							selectedMatrix.getInvestigation_Id())).get(0);

			int line_number = 1;
			for (Tuple line : csvFile)
			{
				// count+= 1;

				// for each line, add the data elements
				for (int column_index = 1; column_index < line.size(); column_index++)
				{
					if (selectedMatrix.getValueType().equals("Text"))
					{
						// //logger.debug("creating TextDataElement");
						TextDataElement e = new TextDataElement();
						e.setInvestigation(inv);
						e.setTarget(rowIds.get(rowNames.get(
								row_index.getValue()).toLowerCase()));
						e.setFeature(colIds.get(colNames.get(column_index)
								.toLowerCase()));
						// e.setValue(row.split(seperator)[u + 1]);
						e.setValue(line.getString(column_index));
						e.setTargetIndex(row_index.getValue());
						e.setFeatureIndex(column_index - 1);
						e.setData(selectedMatrix.getId());
						tde.add(e);

						// if the list is to big, add to database
						if (tde.size() >= BATCH_SIZE)
						{
							db.add(tde);
							tde.clear();
						}

					}
					else if (selectedMatrix.getValueType().equals("Decimal"))
					{
						// //logger.debug("creating DecimalDataElement");
						DecimalDataElement e = new DecimalDataElement();
						e.setInvestigation(inv);
						e.setTarget(rowIds.get(rowNames.get(
								row_index.getValue()).toLowerCase()));
						e.setFeature(colIds.get(colNames.get(column_index)
								.toLowerCase()));
						e.setTargetIndex(row_index.getValue());
						e.setFeatureIndex(column_index - 1);
						e.setValue(line.getDecimal(column_index));
						e.setData(selectedMatrix.getId());
						dde.add(e);

						// if the list is to big, add to database
						if (dde.size() >= BATCH_SIZE)
						{
							db.add(dde);
							dde.clear();
						}

					}
				}

				// doh!
				row_index.setValue(row_index.getValue() + 1);

				if (onlyHead && line_number++ >= 10) break;
			}

			// add remaining tde/dde elemens
			if (selectedMatrix.getValueType().equals("Text"))
			{
				db.add(tde);
			}
			else if (selectedMatrix.getValueType().equals("Decimal"))
			{
				db.add(dde);
			}
			else
			{
				// paniek
			}

			// update the dimensions of the matrix
			// selectedMatrix.setTotalCols(colIds.size());
			// selectedMatrix.setTotalRows(rowIds.size());

			db.update(selectedMatrix);

			String addReport = "";
			if (addedInvestigationElements.length() > 0)
			{
				addReport = " Details:"
						+ makeDIV("Added InvestigationElements",
								addedInvestigationElements);
			}

			if (useTx)
			{
				db.commitTx();
			}
			return "Imported " + (colIds.size() * rowIds.size())
					+ " dataelements." + addReport;

		}
		catch (Exception e)
		{
			if (useTx)
			{
				db.rollbackTx();
			}
			throw e;
		}

	}

	private static String probeFirstFileElement(File file) throws Exception
	{
		CsvFileReader csvFile = new CsvFileReader(file);
		final FinalBoolean doubleCastSucces = new FinalBoolean(false);
		final FinalInteger row_index = new FinalInteger(0);

		for (Tuple line : csvFile)
		{
			try
			{
				Double.parseDouble(line.getObject(2).toString());
				doubleCastSucces.setValue(true);
			}
			// catch NumberFormatException or NullPointerException
			catch (Exception e)
			{
				// it's text :)
			}
			row_index.setValue(row_index.getValue() + 1);

			break;
		}

		if (doubleCastSucces.getValue() == false)
		{
			return "Text";
		}
		else
		{
			return "Decimal";
		}
	}

	private Map<String, Integer> getDimensionIds(boolean allowAdding,
			Data selectedMatrix, final Class klass, List<String> neededNames)
			throws DatabaseException, ParseException, InstantiationException,
			IllegalAccessException, IOException
	{

		// logfile.write("\tClass = " + klass.getSimpleName() + "\n");

		Map<String, Integer> ids = new LinkedHashMap<String, Integer>();

		List<String> absentNames = new ArrayList<String>();

		// find the absent names
		for (int i = 0; i < neededNames.size(); i += BATCH_SIZE)
		{
			List<String> batchCopyNeededNames = neededNames.subList(i,
					Math.min(i + BATCH_SIZE, neededNames.size()));

			// we search the instances of klass, with name 'name', within
			// investigation i?
			List availableInvestigationElements = db
					.query(klass)
					.in("name", batchCopyNeededNames)
					.equals("investigation",
							selectedMatrix.getInvestigation_Id()).find();

			// create a list of available names
			List<String> presentNames = new ArrayList<String>();
			for (Object present : availableInvestigationElements)
			{
				Nameable rawPresentName = (Nameable) present;
				presentNames.add(rawPresentName.getName().toLowerCase());
			}

			// find the difference to see which names are absent
			for (String needed : batchCopyNeededNames)
			{
				if (!presentNames.contains(needed.toLowerCase()))
				{
					absentNames.add(needed);
				}
			}

		}

		// create the absent dimension objects to add
		List<Nameable> absentObjects = new ArrayList<Nameable>();
		for (int i = 0; i < absentNames.size(); i++)
		{
			InvestigationElement absent = (InvestigationElement) klass
					.newInstance();

			absent.setInvestigation_Id(selectedMatrix.getInvestigation_Id());
			absent.setName(absentNames.get(i));
			absentObjects.add(absent);

			if (absentObjects.size() > BATCH_SIZE)
			{
				if (allowAdding == true)
				{
					db.add(absentObjects);
					addedInvestigationElements = createMissingElements(absentObjects);
					// logger.debug("added " + absentObjects.size() + absent
					// objects");
					absentObjects.clear();
				}
				else
				{
					throw new DatabaseException(
							"Missing InvestigationElements: "
									+ createMissingElements(absentObjects));
				}
			}
		}
		// restje
		if (absentObjects.size() != 0 && allowAdding == true)
		{
			db.add(absentObjects);
			addedInvestigationElements = createMissingElements(absentObjects);
			// //logger.debug("added " + absentObjects.size() +
			// " absent objects");
			absentObjects.clear();
		}
		else if (absentObjects.size() != 0 && allowAdding == false)
		{
			throw new DatabaseException("Missing InvestigationElements: "
					+ createMissingElements(absentObjects));
		}

		// Now get all IDs needed and return them
		for (int i = 0; i < neededNames.size(); i += BATCH_SIZE)
		{
			List batchCopyNeededNames = neededNames.subList(i,
					Math.min(i + BATCH_SIZE, neededNames.size()));

			// we search the instances of klass, with name 'name', within
			// study i?
			List availableInvestigationElements = db
					.query(klass)
					.in("name", batchCopyNeededNames)
					.equals("investigation",
							selectedMatrix.getInvestigation_Id()).find();

			// create a list of available names
			for (Object present : availableInvestigationElements)
			{
				ids.put(((Nameable) present).getName().toLowerCase(),
						((Nameable) present).getId());
			}
		}
		return ids;
	}

	@Deprecated
	private static String makeDIV(String caption, String content)
	{
		return "<div style=\"display: inline;\" onmouseover=\"return overlib('"
				+ content
				+ "', CAPTION, '"
				+ caption
				+ "')\" onmouseout=\"return nd();\"><img src=\"res/img/filter.png\"></div>";
	}

	private static String createMissingElements(List<Nameable> absentObjects)
	{
		String missingElements = "";
		int length = 0;
		int lines = 0;
		boolean stopped = false;
		for (Nameable ident : absentObjects)
		{
			missingElements += ident.getName() + ", ";
			length += ident.getName().length() + 2;
			if (length > 100)
			{
				// cut off space at end of line
				missingElements = missingElements.substring(0,
						missingElements.length() - 1);
				// add html newline
				missingElements += "<br>";
				length = 0;
				lines++;
			}
			if (lines == 10)
			{
				stopped = true;
				break;
			}
		}
		// cut off possible ", "
		if (missingElements.length() > 2
				&& missingElements.substring(missingElements.length() - 2,
						missingElements.length()).equals(", "))
		{
			missingElements = missingElements.substring(0,
					missingElements.length() - 2);
		}
		// add cutoff indication
		if (stopped)
		{
			missingElements += "...more";
		}
		// escape html
		missingElements = org.apache.commons.lang.StringEscapeUtils
				.escapeHtml(missingElements);
		return missingElements;
	}

	private static class FinalInteger
	{
		private int value;

		public FinalInteger(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}

		public void setValue(int value)
		{
			this.value = value;
		}

	}

	private static class FinalBoolean
	{
		private boolean value;

		public FinalBoolean(boolean value)
		{
			this.value = value;
		}

		public boolean getValue()
		{
			return value;
		}

		public void setValue(boolean value)
		{
			this.value = value;
		}

	}
}
