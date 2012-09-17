package matrix.general;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.Tuple;

import decorators.NameConvention;

public class PreProcessMatrix
{

	private List<String> colNames;
	private List<String> rowNames;
	private Object[][] elements;

	public PreProcessMatrix(File in) throws Exception
	{
		CsvFileReader reader = new CsvFileReader(in);
		this.colNames = reader.colnames();
		if (this.colNames.get(0).isEmpty()) this.colNames.remove(0);
		this.rowNames = reader.rownames();
		this.elements = getElementsFromCsv(in, rowNames.size(), colNames.size());
		reader.close();
	}

	public void prependUnderscoreToRowNames() throws Exception
	{
		ArrayList<String> newRowNames = new ArrayList<String>();
		for (String s : this.rowNames)
		{
			newRowNames.add("_" + s);
		}
		this.rowNames = newRowNames;
	}

	public void prependUnderscoreToColNames() throws Exception
	{
		ArrayList<String> newColNames = new ArrayList<String>();
		for (String s : this.colNames)
		{
			newColNames.add("_" + s);
		}
		this.colNames = newColNames;
	}

	public void escapeRowNames() throws Exception
	{
		ArrayList<String> newRowNames = new ArrayList<String>();
		for (String s : this.rowNames)
		{
			newRowNames.add(NameConvention.escapeEntityNameStrict(s));
		}
		this.rowNames = newRowNames;
	}

	public void escapeColNames() throws Exception
	{
		ArrayList<String> newColNames = new ArrayList<String>();
		for (String s : this.colNames)
		{
			newColNames.add(NameConvention.escapeEntityNameStrict(s));
			this.colNames = newColNames;
		}
	}

	public void trimTextElements() throws Exception
	{
		Object[][] newElements = new Object[this.rowNames.size()][this.colNames
				.size()];
		for (int row = 0; row < this.rowNames.size(); row++)
		{
			for (int col = 0; col < this.colNames.size(); col++)
			{
				Object o = this.elements[row][col];
				if (o != null)
				{
					newElements[row][col] = o.toString().length() > 127 ? o
							.toString().substring(0, 127) : o;
				}
				else
				{
					newElements[row][col] = "";
				}
			}
		}
		this.elements = newElements;
	}

	public File getResult() throws IOException
	{
		return writeOutMatrix(this.rowNames, this.colNames, this.elements);
	}

	private File writeOutMatrix(List<String> newRowNames,
			List<String> newColNames, Object[][] newElements)
			throws IOException
	{
		File out = new File(System.getProperty("java.io.tmpdir")
				+ File.separator + "tmpMatrix" + System.nanoTime() + ".txt");
		CsvFileWriter writer = new CsvFileWriter(out);
		writer.writeMatrix(newRowNames, newColNames, newElements);
		writer.close();
		return out;
	}

	private Object[][] getElementsFromCsv(File in, int nRow, int nCol)
			throws FileNotFoundException, Exception
	{
		final Object[][] elements = new Object[nRow][nCol];
		int line_number = 1;
		for (Tuple line : new CsvFileReader(in))
		{
			for (int columnIndex = 1; columnIndex < line.size(); columnIndex++)
			{
				elements[line_number - 1][columnIndex - 1] = line
						.getObject(columnIndex);
				
			}
			line_number++;
		}

		return elements;
	}

}
