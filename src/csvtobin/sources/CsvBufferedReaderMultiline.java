package csvtobin.sources;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;

/**
 * Generic superclass that implements CsvReader based on BufferedReader. Users
 * can implement the "reset" and "parse" to make it work for other types of
 * Readers.
 * 
 * @see CsvReader
 */
public class CsvBufferedReaderMultiline implements CsvReader
{
	/** default separators */
	public static char[] separators =
	{ ',', '\t', ';', ' ' };

	/** Wrapper around the resource that is read */
	public BufferedReader reader = null;

	/**
	 * a matching String that indicates where the Csv starts; empty means first
	 * line
	 */
	private String blockStart = "";

	/**
	 * a matching String that indicates where the Csv is terminated; empty
	 * means last line
	 */
	private String blockEnd = "";

	/** a string that translates to a null value when parsed */
	private String missingValueIndicator = "";

	/** cache of the column names, may have duplicates */
	private List<String> columnnames;

	/** guessed separator */
	private char separator = 0;

	/** boolean indicating the parser is working */
	//private boolean isParsing = false;

	/** boolean indicating that the resource parsed has headers... */
	private boolean hasHeader = true;

	/** character for escaping " */
	private char quoteEscape = '"';

	/**
	 * Constructor
	 * 
	 * @param reader
	 */
	public CsvBufferedReaderMultiline(BufferedReader reader)
	{
		this.reader = reader;
	}

	// @Override
	public String getBlockEnd()
	{
		return blockEnd;
	}

	// @Override
	public void setBlockEnd(String blockEnd)
	{
		this.blockEnd = blockEnd;
	}

	// @Override
	public String getBlockStart()
	{
		return blockStart;
	}

	// @Override
	public void setBlockStart(String blockStart)
	{
		this.blockStart = blockStart;
	}

	// @Override
	public List<String> rownames() throws IOException
	{
		List<String> result = new ArrayList<String>();

		//go to start of reading block
		goToBlockStart(reader);
		
		//skip line with the headers
		List<String> row = this.getRow(); 

		//parse all other lines and collect first column value
		while ((row = this.getRow()) != null && row.size()>0 && !isBlockEnd(row))
		{
			// get first element
			result.add(row.get(0));
		}

		//reset the reader
		this.reset();
		return result;
	}

	// @Override
	public List<String> colnames() throws IOException, DataFormatException
	{
		// use cached...
		if (this.columnnames != null)
		{
			return this.columnnames;
		}

		// where we collect the column names
		List<String> result = new ArrayList<String>();

		// skip to reading block
		goToBlockStart(reader);

		//get the column names as in file
		List<String> colnames = this.getRow();
		
		//get data line as comparison to see if we need to add anonymous first column for data matrix
		List<String> dataline = this.getRow();
		
		//empty file has empty headers
		if(dataline == null) return new ArrayList<String>();

		// if data line is longer, then add empty first column header
		if (colnames.size() + 1 == dataline.size())
		{
			System.out.println("data line is longer, assuming matrix with rownames and added first column header ''");
			result.add("");
		}
		// if first element is nameless, assume first column header
		else if (colnames.size() > 0 && colnames.get(0).length() == 0)
		{
			// colnames[0] = ROWNAME_COLUMN;
		}
		else if (dataline.size() > colnames.size() + 1)
		{
			throw new DataFormatException(
					"Data has more columns than there are headers ("
							+ dataline.size()
							+ ">"
							+ colnames.size()
							+ "). Only the first column may be empty. Check whether you have data separators in your data values");
		}

		//add all other column names to the result
		for (String header : colnames)
		{
			result.add(header);
		}

		// validate headers
		for (String header : result)
		{
			if (header.length() == 0 && result.indexOf(header) != 0) throw new IOException(
					"nameless header found at index " + result.indexOf(header)
							+ ": " + result);
		}

		//reset reader
		this.reset();

		// cache for later use
		this.columnnames = result;

		return result;
	}

	public int parse(int noElements, CsvReaderListener... listeners)
			throws Exception
	{
		return this.parse(noElements, null, listeners);
	}

	// @Override
	public int parse(int noElements, List<Integer> rows,
			CsvReaderListener... listeners) throws Exception
	{
		//this.reset();
		
		List<String> headers = null;
		List<String> row = null;

		if (hasHeader) headers = colnames();
		// this.reset();
		this.goToBlockStart(this.reader);
		if (hasHeader) row = this.getRow();

		// logger.debug("found: " + t.toString());
		int lineCount = 0;
		// int index;

		while (lineCount < noElements && (row = this.getRow()) != null && row.size() > 0
				&& !isBlockEnd(row))



		{

			// template of the tuple
			Tuple t;
			if (hasHeader)
			{
				t = new SimpleTuple(headers);
			}
			else
			{
				t = new SimpleTuple();
			}

			lineCount++;

			// warn for empty line and return
			if (row.size() == 0)
			{
				System.out.println("found empty line: " + lineCount);
			}
			else if (rows == null || rows.contains(lineCount))
			{
				// parse the row into a tuple
				if (hasHeader && row.size() > headers.size())
				{
					throw new Exception("Row " + lineCount
							+ " has more columns than there are headers ("
							+ row.size() + ">" + headers.size()
							+ "). Put double \" around columns that have '"
							+ separator + "' in their value. \nRow is: " + this.rowToString(row));
				}

				// change MISSING_VALUES to null
				for (int i = 0; i < row.size(); i++)
				{
					if (row.get(i).equals(this.getMissingValues()))
					{
						row.set(i, null);
					}
				}
				t.set(row);
				// logger.info("found: " + t.toString());

				// handle the tuple by all handlers
				for (CsvReaderListener listener : listeners)
				{
					if (row.size() == 0)
					{
						System.out.println("EMPTY LINE on " + lineCount
										+ ", skipped.");
					}
					else
					{
						try
						{
							listener.handleLine(lineCount, t);
						}
						catch (Exception e)
						{
							e.printStackTrace();
							System.out.println("parsing of row " + lineCount
									+ " failed: " + e);
							System.out.println("parse error on line " + lineCount
									+ ": " + e.getMessage());
							throw e;
						}
					}
				}
			}
		}

		return lineCount;
	}

	private String rowToString(List<String> row)
	{
		StringBuffer result = new StringBuffer();
		if(row == null) return "";
		boolean firstRow = true;
		for(String s: row)
		{
			if(firstRow)
			{	
				result.append("\""+s+"\"");
				firstRow = false;
			}
			else
			{
				result.append(", \""+result+"\"");
			}
		}
		return result.toString();
	}

	@Override
	public int parse(CsvReaderListener... listeners) throws Exception
	{
		return this.parse(Integer.MAX_VALUE, listeners);
	}

	@Override
	public int parse(List<Integer> rows, CsvReaderListener... listeners)
			throws Exception
	{
		return this.parse(Integer.MAX_VALUE, rows, listeners);
	}

	public void close() throws IOException
	{
		this.reader.close();
	}

	/**
	 * Guesses the separator of a String, such as ',' or '\t' by counting the
	 * occurence of well known separators.
	 * 
	 * @see CsvFileReader#separators
	 * @param aLine
	 *            from the CSV file
	 * @return the probable separator of this line
	 * @throws Exception
	 */
	private char guessSeparator(String aLine) throws IOException
	{
		// if(separator != 0) return separator;

		if (aLine == null || aLine.length() == 0) throw new IOException(
				"could not guess separator from line '" + aLine + "'");

		char result = '\t';
		int result_occurences = 0;

		for (char sep : separators)
		{
			// logger.debug("testing: '" + sep + "'");
			int startIndex = 0;
			int sep_count = 0;
			while (startIndex < aLine.length())
			{
				// logger.debug("testing: '" + sep + "' in "
				// + aLine.substring(startIndex));
				int index = aLine.indexOf(sep, startIndex);
				if (index > -1)
				{
					sep_count++;
					startIndex = index + 1;
				}
				else
				{
					break; // no more separators found
				}
			}

			// found better sep?
			// logger.info("found " + sep_count + " times '" + sep + "'");
			if (sep_count > result_occurences)
			{
				result = sep;
				result_occurences = sep_count;
			}
		}

		if (result_occurences == 0)
		{
			System.out.println("could not guess separator from line '" + aLine
					+ "'. Assume one column.");
		}

		return result;
	}

	/**
	 * This method reads a line but also takes into consideration escaping using
	 * " (or other escape if set). It returns null if no line is found.
	 * 
	 * @throws IOException
	 */
	public List<String> getRow() throws IOException
	{
		List<String> result = new ArrayList<String>();
		// read until newline, consider escaping.
		boolean inQuotes = false;

		// readline, optionally, read more lines if escaping forces it
		String line;
		String currentRecord = "";
		while ((line = reader.readLine()) != null)
		{
            if(!inQuotes && this.getBlockEnd().equals(line.trim()))
            {
            	return null;
            }

			if (!inQuotes && this.separator == 0)
			{
				separator = guessSeparator(line);
			}
			
			char[] chars = line.toCharArray();
			for (int i = 0; i < chars.length; i++)
			{
				if (inQuotes) 
				{
					//skip escaping of quotes
					if(chars[i] == this.quoteEscape && (i+1) < chars.length && chars[i+1] == '"')
					{
						//escape quote once and skip next
						currentRecord += chars[i];
						i++;
						
					}
					//finish current record if closing quotes
					else if(chars[i] == '"')
					{
						//skip the quotes but say inQuotes = false
						inQuotes = false;
					}
					else
					{
						//add the character to the record
						currentRecord += chars[i];
					}
				}
				else 
				{
					if(chars[i] == '"')
					{
						//if quotes, get into them
						inQuotes = true;
					}
					//if seperator, then add value to record and start with new currentRecord
					else if(chars[i] == this.separator)
					{
						//skip the separator, but add the record to our result
						result.add(currentRecord.trim());
						currentRecord = "";
					}
					else
					{
						//read normal character into our record
						currentRecord += chars[i];
					}
				}
			}
			
			//if we reach this point, and we are still inQuotes, we have a multiline value, otherwise we can break and are done
			if(!inQuotes)
			{
				result.add(currentRecord.trim());
				return result;
			}
			else
			{
				currentRecord += "\n";
			}
		}
		
		return null;
	}

	/**
	 * Helper method that parses the file until it reaches the
	 * {@link CsvFileReader#blockStart} that was set during construction.
	 * Default: first line.
	 * 
	 * @param in
	 *            the input stream
	 * @throws IOException
	 */
	private void goToBlockStart(BufferedReader in) throws IOException
	{
		if (!blockStart.equals(""))
		{
			String line = "";
			while ((line = in.readLine()) != null)
			{
				if (line.startsWith(blockStart)) return;
				// FIXME: make regexp?
			}
		}
	}

	/**
	 * Helper method that indicates whether a line matches the
	 * {@link CsvFileReader#blockEnd}. This would terminate parsing.
	 * 
	 * @param line
	 *            a csv line
	 * @return boolean indicating whether the line matches block end.
	 * @throws IOException
	 */
	private boolean isBlockEnd(List<String> line)
	{
		if (line.size() == 1 && blockEnd.equals(line.get(0))) return true;
		return false;
	}

	// @Override
	public void setMissingValues(String missingValue)
	{
		this.missingValueIndicator = missingValue;
	}

	// @Override
	public String getMissingValues()
	{
		return this.missingValueIndicator;
	}

	// @Override
	public void reset() throws IOException
	{
		this.columnnames = null; // force to read colnames also!
		//this.isParsing = false;
	}

	@Override
	public void setColnames(List<String> fields)
	{
		this.columnnames = fields;
	}

	@Override
	public void setSeparator(char string)
	{
		this.separator = string;

	}

	public void renameField(String from, String to) throws Exception
	{
		List<String> colnames = this.colnames();
		if (colnames.contains(from)) colnames.set(colnames.indexOf(from), to);
		else
		{
			System.out.println("renameField(" + from + "," + to
					+ ") failed. Known columns are: " + colnames);
		}
		this.setColnames(colnames);

	}

	@Override
	public void disableHeader(boolean header)
	{
		this.hasHeader = header;

	}
}
