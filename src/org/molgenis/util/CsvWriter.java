package org.molgenis.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Delimited writer to write {@link org.molgenis.util.Tuple Tuple} or
 * {@link org.molgenis.util.Entity Entity} objects to a print stream.
 * <p>
 * This writer is typically used to produce a comma-separated-values (CSV) or
 * tab-delimited-values (TAB) file.
 * <p>
 */
public class CsvWriter implements TupleWriter
{
	private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	private static final char DEFAULT_SEPARATOR = '\t';
	private static final char DEFAULT_LIST_SEPARATOR = '|';
	private static final String DEFAULT_LINE_SEPARATOR = "\n";

	/** writer the output is written to */
	private Writer writer;
	/** column separator (default: \t) */
	private char separator = DEFAULT_SEPARATOR;
	/** line separator (default: \n) */
	private String lineSeparator = DEFAULT_LINE_SEPARATOR;
	/** list value separator (default: |) */
	private char listSeparator = DEFAULT_LIST_SEPARATOR;
	/**
	 * value to use for missing/null values such as "NULL" or "NA", default
	 * write nothing
	 */
	private String missingValue;
	/** headers to be written out */
	private List<String> headers;

	public CsvWriter(OutputStream os)
	{
		this(os, CHARSET_UTF8);
	}

	public CsvWriter(OutputStream os, Charset charset)
	{
		this(new OutputStreamWriter(os, charset));
	}

	public CsvWriter(OutputStream os, char separator)
	{
		this(os, CHARSET_UTF8, separator);
	}

	public CsvWriter(OutputStream os, Charset charset, char separator)
	{
		this(new OutputStreamWriter(os, charset), separator);
	}

	public CsvWriter(OutputStream os, List<String> headers)
	{
		this(os, CHARSET_UTF8, headers);
	}

	public CsvWriter(OutputStream os, Charset charset, List<String> headers)
	{
		this(new OutputStreamWriter(os, charset), headers);
	}

	public CsvWriter(OutputStream os, char separator, List<String> headers)
	{
		this(os, CHARSET_UTF8, separator, headers);
	}

	public CsvWriter(OutputStream os, Charset charset, char separator, List<String> headers)
	{
		this(new OutputStreamWriter(os, charset), separator, headers);
	}

	public CsvWriter(Writer writer)
	{
		this(writer, DEFAULT_SEPARATOR);
	}

	public CsvWriter(Writer writer, char separator)
	{
		this(writer, separator, null);
	}

	public CsvWriter(Writer writer, List<String> headers)
	{
		this(writer, DEFAULT_SEPARATOR, headers);
	}

	public CsvWriter(Writer writer, char separator, List<String> headers)
	{
		if (writer == null) throw new IllegalArgumentException("writer is null");
		this.writer = new BufferedWriter(writer);
		this.separator = separator;
		this.headers = headers;
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
		if (obj == null)
		{
			if (this.missingValue != null) this.writer.write(this.missingValue);
		}
		else if (obj instanceof List<?>)
		{
			List<?> list = (List<?>) obj;
			final int size = list.size();
			for (int i = 0; i < size; ++i)
			{
				// FIXME list separator in list values not escaped
				if (i > 0) this.writer.write(this.listSeparator);
				Object value = list.get(i);
				if (value != null)
				{
					StringEscapeUtils.escapeCsv(this.writer, value.toString());
				}
				else if (this.missingValue != null)
				{
					this.writer.write(this.missingValue);
				}
			}
		}
		else
		{
			this.writer.write(StringEscapeUtils.escapeCsv(obj.toString()));
		}
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

	public char getListSeparator()
	{
		return listSeparator;
	}

	public void setListSeparator(char listSeparator)
	{
		this.listSeparator = listSeparator;
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

	public void setLineSeparator(String lineSeparator)
	{
		this.lineSeparator = lineSeparator;
	}

	@Override
	public void close() throws IOException
	{
		writer.close();
	}
}
