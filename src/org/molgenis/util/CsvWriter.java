package org.molgenis.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Delimited writer to write {@link org.molgenis.util.Tuple Tuple} or
 * {@link org.molgenis.util.Entity Entity} objects to a print stream.
 * <p>
 * This writer is typically used to produce a comma-separated-values (CSV) or
 * tab-delimited-values (TAB) file.
 * <p>
 */
public class CsvWriter implements CsvParser, TupleWriter
{
	private Writer writer;
	/** column separator */
	private char separator;
	/** line separator */
	private String lineSeparator;
	/** column headers */
	private List<String> headers;
	/** default output value for null values (e.g. NA or NULL) */
	private String missingValue;
	/** list separator */
	private char listSeparator;

	public CsvWriter(OutputStream os)
	{
		this(os, CSV_DEFAULT_CHARSET);
	}

	public CsvWriter(OutputStream os, Charset charset)
	{
		this(new OutputStreamWriter(os, charset));
	}

	public CsvWriter(OutputStream os, char separator)
	{
		this(os, CSV_DEFAULT_CHARSET, separator);
	}

	public CsvWriter(OutputStream os, Charset charset, char separator)
	{
		this(new OutputStreamWriter(os, charset), separator);
	}

	public CsvWriter(OutputStream os, List<String> headers)
	{
		this(os, CSV_DEFAULT_CHARSET, headers);
	}

	public CsvWriter(OutputStream os, Charset charset, List<String> headers)
	{
		this(new OutputStreamWriter(os, charset), headers);
	}

	public CsvWriter(OutputStream os, char separator, List<String> headers)
	{
		this(os, CSV_DEFAULT_CHARSET, separator, headers);
	}

	public CsvWriter(OutputStream os, Charset charset, char separator, List<String> headers)
	{
		this(new OutputStreamWriter(os, charset), separator, headers);
	}

	public CsvWriter(Writer writer)
	{
		this(writer, CSV_DEFAULT_SEPARATOR);
	}

	public CsvWriter(Writer writer, char separator)
	{
		this(writer, separator, null);
	}

	public CsvWriter(Writer writer, List<String> headers)
	{
		this(writer, CSV_DEFAULT_SEPARATOR, headers);
	}

	public CsvWriter(Writer writer, char separator, List<String> headers)
	{
		if (writer == null) throw new IllegalArgumentException("writer is null");
		this.writer = new BufferedWriter(writer);
		this.separator = separator;
		this.headers = headers;
		this.lineSeparator = CSV_DEFAULT_LINE_SEPARATOR;
		this.listSeparator = ListEscapeUtils.DEFAULT_SEPARATOR;
	}

	@Override
	public void writeMatrix(List<String> rowNames, List<String> colNames, Object[][] values) throws IOException
	{
		// write header
		for (String colName : colNames)
		{
			writeSeparator();
			writeValue(colName);
		}
		writeEndOfLine();

		final int nrRows = rowNames.size();
		final int nrCols = colNames.size();
		for (int row = 0; row < nrRows; ++row)
		{
			writeValue(rowNames.get(row));
			for (int col = 0; col < nrCols; ++col)
			{
				writeSeparator();
				writeValue(values[row][col]);
			}
			writeEndOfLine();
		}
	}

	@Override
	public void writeHeader() throws IOException
	{
		final int nrHeaders = this.headers.size();
		for (int i = 0; i < nrHeaders; ++i)
		{
			if (i > 0) writeSeparator();
			writeValue(this.headers.get(i));
		}
		writeEndOfLine();
	}

	@Override
	public void writeRow(Entity e) throws IOException
	{
		final int nrHeaders = this.headers.size();
		for (int i = 0; i < nrHeaders; ++i)
		{
			if (i > 0) writeSeparator();
			writeValue(e.get(this.headers.get(i)));
		}
		writeEndOfLine();
	}

	@Override
	public void writeRow(Tuple t) throws IOException
	{
		final int nrHeaders = this.headers.size();
		for (int i = 0; i < nrHeaders; ++i)
		{
			if (i > 0) writeSeparator();
			writeValue(t.getObject(this.headers.get(i)));
		}
		writeEndOfLine();
	}

	@Override
	public void writeValue(Object obj) throws IOException
	{
		String value = null;
		if (obj == null)
		{
			if (this.missingValue != null) value = this.missingValue;
		}
		else if (obj instanceof List<?>)
		{
			value = ListEscapeUtils.toString((List<?>) obj, listSeparator);
		}
		else
		{
			value = obj.toString();
		}
		if (value != null) this.writer.write(escapeCsv(value));
	}

	public void writeSeparator() throws IOException
	{
		this.writer.write(this.separator);
	}

	@Override
	public void writeEndOfLine() throws IOException
	{
		this.writer.write(this.lineSeparator);
	}

	private String escapeCsv(String value)
	{
		if (value.isEmpty()) return value;
		if (doEscapeCsv(value)) return CSV_DEFAULT_QUOTE_CHARACTER + value + CSV_DEFAULT_QUOTE_CHARACTER;
		else
			return value;
	}

	private boolean doEscapeCsv(String value)
	{
		boolean doEscape = false;
		final int length = value.length();
		for (int i = 0; i < length && !doEscape; ++i)
		{
			char c = value.charAt(i);
			if (c == this.separator) doEscape = true;
			else if (c == CSV_DEFAULT_QUOTE_CHARACTER) doEscape = true;
			else if (c == this.lineSeparator.charAt(0))
			{
				final int lineLength = this.lineSeparator.length();
				if (lineLength == 1) doEscape = true;
				else if (lineLength == 2 && i + 1 < length && value.charAt(i + 1) == this.lineSeparator.charAt(1)) doEscape = true;
			}
		}
		return doEscape;
	}

	public String getMissingValue()
	{
		return missingValue;
	}

	public void setMissingValue(String missingValue)
	{
		this.missingValue = missingValue;
	}

	public char getSeparator()
	{
		return separator;
	}

	public void setSeparator(char separator)
	{
		this.separator = separator;
	}

	@Override
	public void setHeaders(List<String> headers)
	{
		this.headers = headers;
	}

	public List<String> getHeaders()
	{
		return this.headers;
	}

	public String getLineSeparator()
	{
		return lineSeparator;
	}

	/**
	 * Valid line separators: \r \n \r\n
	 * 
	 * @param lineSeparator
	 */
	public void setLineSeparator(String lineSeparator)
	{
		this.lineSeparator = lineSeparator;
	}

	public char getListSeparator()
	{
		return listSeparator;
	}

	public void setListSeparator(char listSeparator)
	{
		this.listSeparator = listSeparator;
	}

	@Override
	public void close() throws IOException
	{
		writer.close();
	}
}
