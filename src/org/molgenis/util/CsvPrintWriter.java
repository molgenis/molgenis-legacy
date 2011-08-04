package org.molgenis.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jxl.write.WritableSheet;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

/**
 * Delimited writer to write {@link org.molgenis.util.Tuple Tuple} or
 * {@link org.molgenis.util.Entity Entity} objects to a print stream.
 * <p>
 * This writer is typically used to produce a comma-separated-values (CSV) or
 * tab-delimited-values (TAB) file.
 * <p>
 * FIXME make Tuple and Entity both inherit from the same interface so this
 * writer can be simplified.
 */
public class CsvPrintWriter implements SpreadsheetWriter
{
	/** to send log messages on progress to */
	private static final transient Logger logger = Logger.getLogger(SpreadsheetWriter.class.getSimpleName());
	/** writer the output is written to */
	protected PrintWriter writer = null;
	/** separator used to separate columns, default "\t" */
	private String separator = "\t";
	/** separator used to separate lists, default "|" */
	private String listSeparator = "|";
	/** number of rows written */
	private int count = 0;
	/** value to use for missing/null values such as "NULL" or "NA", default "" */
	private String missingValue = "";
	/** headers to be written out */
	private List<String> headers = new ArrayList<String>();

	/**
	 * Construct the Writer, wrapping another writer.
	 */
	public CsvPrintWriter(PrintWriter writer, List<String> headers)
	{
		this(writer);
		this.headers = headers;
	}

	public CsvPrintWriter(PrintWriter writer)
	{
		this.writer = writer;
	}

	/**
	 * Write out an XGAP matrix. The inputs can be retrieved from any
	 * implementation of the XGAP matrix interface class.
	 * 
	 * @param rowNames
	 * @param colNames
	 * @param elements
	 */
	public void writeMatrix(List<String> rowNames, List<String> colNames, Object[][] elements)
	{
		logger.info("writeMatrix called");
		String cols = "";
		for (String col : colNames)
		{
			cols += "\t" + col;
		}
		writer.println(cols);
		logger.info("printing: " + cols);
		for (int rowIndex = 0; rowIndex < rowNames.size(); rowIndex++)
		{
			String row = rowNames.get(rowIndex);
			for (int colIndex = 0; colIndex < colNames.size(); colIndex++)
			{
				if (elements[rowIndex][colIndex] == null)
				{
					row += "\t";
				}
				else
				{
					row += "\t" + elements[rowIndex][colIndex];
				}
			}
			writer.println(row);
			logger.info("printing: " + row);
		}
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeHeader()
	 */
	@Override
	public void writeHeader()
	{
		for (int i = 0; i < headers.size(); i++)
		{
			if (i < headers.size() - 1)
			{
				writer.print(headers.get(i) + separator);
			}
			else
			{
				writer.print(headers.get(i));
			}
		}
		writer.println();
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeRow(org.molgenis.util.Entity)
	 */
	@Override
	public void writeRow(Entity e)
	{
		boolean first = true;
		for (String col : headers)
		{
			//print separator unless first element
			if (first)
			{
				first = false;
			}
			else
			{
				writer.print(separator);
			}
			//print value
			writeValue(e.get(col));

		}
		//newline
		writer.println();
		// writer.println(e.getValues(separator));
		if (++count % 10000 == 0) logger.debug("wrote line " + count + ": " + e);
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeRow(org.molgenis.util.Tuple)
	 */
	@Override
	public void writeRow(Tuple t)
	{
		boolean first = true;
		for (String col : headers)
		{
			//print separator unless first element
			if (first)
			{
				first = false;
			}
			else
			{
				writer.print(separator);
			}
			//print value
			writeValue(t.getObject(col));
		}
		writer.println();
		if (count++ % 10000 == 0) logger.debug("wrote tuple to line " + count + ": " + t);
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeValue(java.lang.Object)
	 */
	// public void writeRow(Object[] values)
	// {
	// // FIXME: this is probably unnecessarily slow
	// for (int i = 0; i < values.length; i++)
	// {
	// if(i > 0) writer.print(separator);
	// writeValue(values[i], writer);
	// }
	// writer.println();
	// if (count++ % 10000 == 0) logger.debug("wrote values array to line " +
	// count + " ");
	// }

	@Override
	public void writeValue(Object object)
	{
		if (object == null)
		{
			writer.print(this.missingValue);
		}

		else
		{
			if (object instanceof List<?>)
			{
				List<?> list = (List<?>) object;
				for (int i = 0; i < list.size(); i++)
				{
					// FIXME, what about escaping???
					if (i != 0) writer.print(listSeparator);
					if (list.get(i) != null)
					{
						writer.print(list.get(i).toString());
					}
					else
					{
						writer.print(this.getMissingValue());
					}
				}

			}
			else
			{
				//writer.print(StringEscapeUtils.escapeCsv(object.toString().trim().replace("\n", "")));
				writer.print(StringEscapeUtils.escapeCsv(object.toString()));
			}
		}

	}

	/**
	 * Get the String that is used for missing or null values, default 'NA'.
	 */
	public String getMissingValue()
	{
		return missingValue;
	}

	/**
	 * Set the String that is used for missingValues such as null, default 'NA'.
	 * 
	 * @param missingValue
	 *            new missing value String.
	 */
	public void setMissingValue(String missingValue)
	{
		this.missingValue = missingValue;
	}

	/**
	 * Get the separator used to separate columns that are outputed, default
	 * '\t'.
	 */
	public String getSeparator()
	{
		return separator;
	}

	/**
	 * Set the String that is used to separate columns that are outputed,
	 * default '\t'.
	 * 
	 * @param separator
	 *            new separator String.
	 */
	public void setSeparator(String separator)
	{
		this.separator = separator;
	}

	/** Close the writer */
	public void close()
	{
		writer.flush();
		writer.close();

	}

	public String getListSeparator()
	{
		return listSeparator;
	}

	public void setListSeparator(String listSeparator)
	{
		this.listSeparator = listSeparator;
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#setHeaders(java.util.List)
	 */
	@Override
	public void setHeaders(List<String> fields)
	{
		this.headers = fields;
	}

	public List<String> getHeaders()
	{
		return this.headers;
	}

	public void writeSeparator()
	{
		this.writer.print(this.getSeparator());
	}

	/* (non-Javadoc)
	 * @see org.molgenis.util.CsvWriter#writeEndOfLine()
	 */
	@Override
	public void writeEndOfLine()
	{
		this.writer.println();
	}

	@Override
	public void writeRow(Entity e, WritableSheet sheet)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRow(Tuple t, WritableSheet sheet)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeHeader(WritableSheet excelSheet)
	{
		// TODO Auto-generated method stub
		
	}
}
