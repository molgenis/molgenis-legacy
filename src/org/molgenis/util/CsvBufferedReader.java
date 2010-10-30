package org.molgenis.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Generic superclass that implements CsvReader based on BufferedReader. Users
 * can implement the "reset" and "parse" to make it work for other types of
 * Readers.
 * 
 * @see CsvReader
 */
public class CsvBufferedReader implements CsvReader
{
	/** default separators */
	public static char[] separators = {',', '\t', ';', ' '};

	/** Wrapper around the resource that is read */
	public BufferedReader reader = null;

	/** for log messages */
	private static final transient Logger logger = Logger.getLogger(CsvFileReader.class.getSimpleName());

	/**
	 * a matching String that indicates where the Csv starts; empty means first
	 * line
	 */
	private String blockStart = "";

	/**
	 * a matching String that inidicates where the Csv is terminated; empty
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
	private boolean isParsing = false;

	/** booleain indicating that the resource parsed has headers... */
	private boolean hasHeader = true;

	/**
	 * Constructor
	 * 
	 * @param reader
	 */
	public CsvBufferedReader(BufferedReader reader) {
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

		goToBlockStart(reader);
		String line = reader.readLine(); // skip first line
		if (separator == 0)
		{
			separator = guessSeparator(line); // guess the separator
			// logger.info("ROWNAMES guessed separator: '" + separator + "'");
		}

		while ((line = reader.readLine()) != null && !isBlockEnd(line))
		{
			// get first element
			result.add(split(line, separator)[0]);
		}

		this.reset();
		return result;
	}

	// @Override
	public List<String> colnames() throws Exception
	{
		// use cached...
		if (this.columnnames != null)
		{
			return this.columnnames;
		}

		// the column names
		List<String> result = new ArrayList<String>();

		// skip to start
		goToBlockStart(reader);

		String headerLine = reader.readLine();
		String dataLine = reader.readLine();

		// use cached
		if (this.separator == 0)
		{
			separator = guessSeparator(headerLine);
		}
		// logger.info("guessed separator: '" + separator + "'");

		// get rid of trailing separator
		if (!missingValueIndicator.equals("") && dataLine.lastIndexOf(separator) == dataLine.length() - 1)
		{
			dataLine = dataLine.substring(0, dataLine.length() - 1);
		}
		if (headerLine.lastIndexOf(separator) == headerLine.length() - 1)
		{
			headerLine = headerLine.substring(0, headerLine.length() - 1);
		}

		String[] colnames = split(headerLine, separator);
		String[] dataline = split(dataLine, separator);

		// if data line is longer, then add empty first column header
		if (colnames.length + 1 == dataline.length)
		{
			logger.debug("data line is longer, assuming matrix with rownames and added first column header ''");
			result.add("");
		}
		// if first element is nameless, assume first column header
		else if (colnames[0].length() == 0)
		{
			// colnames[0] = ROWNAME_COLUMN;
		} else if (dataline.length > colnames.length + 1)
		{
			throw new Exception("Data has more columns than there are headers (" + dataline.length + ">" + colnames.length
					+ "). Only the first column may be empty. Check whether you have data separators in your data values");
		}

		for (String header : colnames)
		{
			result.add(header);
		}

		// validate headers
		for (String header : result)
		{
			if (header.length() == 0 && result.indexOf(header) != 0)
				throw new IOException("nameless header found at index " + result.indexOf(header) + ": " + result);
		}

		this.reset();

		// cache for later use
		this.columnnames = result;

		return result;
	}

	public List<Object> getForHeader(String header)
	{
		// TODO: very simple function, just return a single column in the form
		// of a list by a header name
		return null;
	}

	public int parse(int noElements, CsvReaderListener... listeners) throws Exception
	{
		return this.parse(noElements, null, listeners);
	}

	// @Override
	public int parse(int noElements, List<Integer> rows, CsvReaderListener... listeners) throws Exception
	{
		List<String> headers = null;
		
		if (hasHeader)
			headers = colnames();
		
		String line;
		if (this.separator == 0)
		{
			goToBlockStart(reader);
			line = reader.readLine();
			separator = guessSeparator(line);
			reset();
			// logger.info("PARSE guessed separator: '" + separator + "'");
		}

		// on first call
		if (!isParsing)
		{
			// skip to start
			goToBlockStart(reader);
			if (hasHeader)
				line = reader.readLine(); // skip header line
			this.isParsing = true;

			logger.debug("parsing with separator = '" + separator + "' and headers =" + headers);
		} else
		{
			logger.debug("restarted parsing with limit " + noElements);
		}

		// logger.debug("found: " + t.toString());
		int lineCount = 0;
		// int index;

		while (lineCount < noElements && (line = reader.readLine()) != null && !isBlockEnd(line))
		{

			// template of the tuple
			Tuple t;
			if (hasHeader)
				t = new SimpleTuple(headers);
			else
				t = new SimpleTuple();
			
			lineCount++;

			// warn for empty line and return
			if (line.trim().equals(""))
			{
				logger.warn("found empty line: " + lineCount);
			} 
			else if (rows == null || rows.contains(lineCount))
			{

				// get rid of trailing separator
				if (!missingValueIndicator.equals("") && line.lastIndexOf(separator) == line.length() - 1)
				{
					line = line.substring(0, line.length() - 1);
				}

				// parse the row into a tuple
				String[] values = this.split(line, separator);
				if (hasHeader && values.length > headers.size())
				{
					throw new Exception("Row " + lineCount + " has more columns than there are headers (" + values.length + ">" + headers.size() + "). Put double \" around columns that have '"
							+ separator + "' in their value. \nRow is: " + line);
				}

				// change MISSING_VALUES to null, trim values
				for (int i = 0; i < values.length; i++)
				{
					if (values[i].equals(this.getMissingValues()))
					{
						values[i] = null;
					}
				}

				// FIX:
				// if the last character is empty; split does not return the
				// last character, values[] is one character too short; then
				// missing character is not overwritten with null, causing wrong
				// values in subsequent last columns: previous values are
				// copied into empty next ones until a new non-empty one
				// FIX 2: in fact, this applies to any character shorter than
				// header length.. ie. whole null lines are copied over with
				// values from the previous line! hopefully fixed now.
				if (hasHeader && (values.length < headers.size()))
				{
					int diff = headers.size() - values.length;
					String[] valuesNew = new String[values.length + diff];
					for (int i = 0; i < values.length; i++)
					{
						valuesNew[i] = values[i];
					}
					for(int i = values.length; i < diff; i++){
						valuesNew[i] = null;
					}
					values = valuesNew;
				}

				t.set(values);
				//logger.info("found: " + t.toString());

				// handle the tuple by all handlers
				for (CsvReaderListener listener : listeners)
				{
					if (line.trim().length() == 0)
					{
						logger.warn("EMPTY LINE on " + lineCount + ", skipped.");
					} 
					else
					{
						try
						{
							listener.handleLine(lineCount, t);
						} catch (Exception e)
						{
							e.printStackTrace();
							logger.error("parsing of row " + lineCount + " failed: " + e);
							logger.error("parse error on line " + lineCount + ": " + e.getMessage());
							throw e;
						}
					}
				}
			}
		}

		return lineCount;
	}

	@Override
	public int parse(CsvReaderListener... listeners) throws Exception
	{
		return this.parse(Integer.MAX_VALUE, listeners);
	}

	@Override
	public int parse(List<Integer> rows, CsvReaderListener... listeners) throws Exception
	{
		return this.parse(Integer.MAX_VALUE, rows, listeners);
	}

	public void close() throws IOException
	{
		this.reader.close();
	}

	/**
	 * Efficient split. Also removes any " that may surround each block.
	 * 
	 * @param string
	 *            the string to be split into tokens
	 * @param separator
	 *            the separator used to split the string
	 * @return stringarray with splitted string
	 */
	private String[] split(String string, char separator)
	{
		// FIXME: need to trim esp if there are spaces leading up to a \"
		char escape = '"';
		int size = string.length();

		// count the nr of occurences
		int nrtokens = 0;
		char chars[] = string.toCharArray();
		boolean isEscaped = chars[0] == escape;

		for (int i = 0; i < size; i++)
		{
			if (chars[i] == separator && !isEscaped)
			{
				nrtokens++;

				// check whether value is escaped using quotes
				if (i < (size - 1) && chars[i + 1] == escape)
				{
					i += 1;
					isEscaped = true;
				}
			}
			// escape quotes again
			else if (isEscaped && chars[i] == escape)
			{
				isEscaped = false;
			}
		}

		String array[] = new String[nrtokens + 1];

		// parse the string
		int indx = 0;
		int prev = 0;
		isEscaped = chars[0] == escape;
		for (int i = 0; i < size; i++)
		{
			if (chars[i] == separator && !isEscaped)
			{
				array[indx++] = string.substring(prev, i);
				// logger.debug("found: "+array[indx-1]);
				prev = i + 1;

				// escape token using quotes
				if (i < size - 1 && chars[i + 1] == escape)
				{
					i += 1;
					isEscaped = true;
				}

			}
			// escape quotes again
			else if (chars[i] == escape && isEscaped)
			{
				isEscaped = false;
			}
		}
		array[indx] = string.substring(prev);

		// replace beginning and ending "
		for (int i = 0; i < array.length; ++i)
		{
			if (array[i].length() >= 2 && array[i].charAt(0) == '"' && array[i].charAt(array[i].length() - 1) == '"')
			{
				array[i] = array[i].substring(1, array[i].length() - 1);
			}
		}

		return array;
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

		if (aLine == null || aLine.length() == 0)
			throw new IOException("could not guess separator from line '" + aLine + "'");

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
				} else
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
			logger.warn("could not guess separator from line '" + aLine + "'. Assume one column.");
		}

		return result;
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
				if (line.startsWith(blockStart))
					return;
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
	private boolean isBlockEnd(String line)
	{
		if (line.equals(blockEnd))
			return true;
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
		this.isParsing = false;
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
		if (colnames.contains(from))
			colnames.set(colnames.indexOf(from), to);
		else
		{
			logger.warn("renameField(" + from + "," + to + ") failed. Known columns are: " + colnames);
		}
		this.setColnames(colnames);

	}

	@Override
	public void disableHeader(boolean header)
	{
		this.hasHeader = header;

	}
}
