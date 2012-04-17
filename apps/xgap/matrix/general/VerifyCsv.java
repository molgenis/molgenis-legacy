package matrix.general;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileUploadException;
import org.molgenis.framework.db.CsvToDatabase.IntegerWrapper;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.Tuple;

import app.servlet.UsedMolgenisOptions;

import decorators.NameConvention;

public class VerifyCsv
{
	private static int maxStringLength = 127;

	public static int[] verify(File inputFile, String valueType)
			throws Exception
	{
		// try
		// {
		int[] dims = verifyAndGetRowColDimensions(inputFile);

		verifyHeaderUniqueness(inputFile);

		verifyHeaderNamesAllowed(inputFile);

		verifyHeaderLenghtsAllowed(inputFile);

		if (valueType.equals("Decimal"))
		{
			verifyDoubleValueType(inputFile);
		}
		else
		{
			verifyTextElementLenghtsAllowed(inputFile);
		}
		return dims;
		// }
		// catch (Exception e)
		// {
		// throw new VerifyCsvException(e.getMessage());
		// }
	}

	private static int[] verifyAndGetRowColDimensions(File inputFile)
			throws Exception
	{
		int[] rowAndColLength = new int[2];

		// final IntegerWrapper elementLength = new IntegerWrapper(0);

		final IntegerWrapper nrOfCols = new IntegerWrapper(-1);
		final IntegerWrapper nrOfRows = new IntegerWrapper(0);

		int line_number = 1;
		for (Tuple line : new CsvFileReader(inputFile))
		{

			if (nrOfCols.get() == -1)
			{
				nrOfCols.set(line.size() - 1);
			}
			else
			{
				if (nrOfCols.get() != line.size() - 1)
				{
					throw new ParseException(
							"Number of columns unequal. Expected "
									+ nrOfCols.get() + " but found "
									+ (line.size() - 1) + " at line "
									+ line_number, line_number);
				}
				// else: continue
			}
			nrOfRows.set(nrOfRows.get() + 1);
			line_number++;
		}

		rowAndColLength[0] = nrOfRows.get();
		rowAndColLength[1] = nrOfCols.get();

		if (rowAndColLength[0] < 1)
		{
			throw new FileUploadException(
					"Number of rows must be greater than 0. Found: "
							+ rowAndColLength[0]);
		}
		if (rowAndColLength[1] < 1)
		{
			throw new FileUploadException(
					"Number of columns must be greater than 0. Found: "
							+ rowAndColLength[1]);
		}
		
		CsvFileReader csvFile = new CsvFileReader(inputFile);
		List<String> colNames = csvFile.colnames();
		List<String> rowNames = csvFile.rownames();
		
		if (rowAndColLength[1] < colNames.size() && !(colNames.get(0).equals("")))
		{
			throw new Exception("Too many column headers (" + colNames.size() + ") in relation to the amount of columns (" + rowAndColLength[1] + ")");
		}
		if (rowAndColLength[0] < rowNames.size())
		{
			throw new Exception("Too many row headers (" + rowNames.size() + ") in relation to the amount of rows (" + rowAndColLength[0] + ")");
		}

		return rowAndColLength;
	}

	private static void verifyDoubleValueType(File inputFile) throws Exception
	{
		for (Tuple line : new CsvFileReader(inputFile))
		{
			for (int i = 1; i < line.size(); i++)
			{
				line.getDouble(i);
			}
		}

	}

	private static void verifyTextElementLenghtsAllowed(File inputFile)
			throws Exception
	{
		for (Tuple line : new CsvFileReader(inputFile))
		{

			for (int i = 1; i < line.size(); i++)
			{
				if (line.getString(i) != null
						&& line.getString(i).length() > maxStringLength)
				{
					throw new FileUploadException("Text element bigger than "
							+ maxStringLength + " characters: "
							+ line.getString(i));
				}

			}
		}

	}

	private static void verifyHeaderLenghtsAllowed(File inputFile)
			throws Exception
	{
		CsvFileReader csvFile = new CsvFileReader(inputFile);
		List<String> colNames = csvFile.colnames();
		List<String> rowNames = csvFile.rownames();
		for (String colName : colNames.subList(1, colNames.size()))
		{
			if (colName.length() > maxStringLength)
			{
				throw new FileUploadException("Column header bigger than "
						+ maxStringLength + " characters: " + colName);
			}
		}
		for (String rowName : rowNames)
		{
			if (rowName.length() > maxStringLength)
			{
				throw new FileUploadException("Row header bigger than "
						+ maxStringLength + " characters: " + rowName);
			}
		}
	}

	private static void verifyHeaderUniqueness(File inputFile) throws Exception
	{

		// TODO: it is now allowed to have two different types of entities with
		// the same name
		// this is okay: Individual x Individual, refs to ind1 x ind1
		// this is not okay: Individual x Marker, refs to bla1 x bla1, since the
		// names are unique

		CsvFileReader csvFile = new CsvFileReader(inputFile);
		List<String> colNames = csvFile.colnames();
		List<String> rowNames = csvFile.rownames();

		List<String> uniqueColNames = new ArrayList<String>();
		List<String> uniqueRowNames = new ArrayList<String>();

		for (String colName : colNames.subList(1, colNames.size()))
		{
			if (!uniqueColNames.contains(colName))
			{
				uniqueColNames.add(colName);
			}
			else
			{
				throw new FileUploadException("Duplicate column header: "
						+ colName);
			}
		}
		for (String rowName : rowNames)
		{
			if (!uniqueRowNames.contains(rowName))
			{
				uniqueRowNames.add(rowName);
			}
			else
			{
				throw new FileUploadException("Duplicate row header: "
						+ rowName);
			}
		}

	}

	private static void verifyHeaderNamesAllowed(File inputFile)
			throws Exception
	{
		CsvFileReader csvFile = new CsvFileReader(inputFile);
		List<String> colNames = csvFile.colnames();
		List<String> rowNames = csvFile.rownames();

		for (String colName : colNames.subList(1, colNames.size()))
		{
			if(new UsedMolgenisOptions().decorator_overriders.equals("org.molgenis.xgap.decoratoroverriders"))
			{
				NameConvention.validateEntityNameStrict(colName);
			}
		}
		for (String rowName : rowNames)
		{
			if(new UsedMolgenisOptions().decorator_overriders.equals("org.molgenis.xgap.decoratoroverriders"))
			{
				NameConvention.validateEntityNameStrict(rowName);
			}
		}
	}
}