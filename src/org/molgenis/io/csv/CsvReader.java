package org.molgenis.io.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.molgenis.io.TupleReader;
import org.molgenis.io.processor.AbstractCellProcessor;
import org.molgenis.io.processor.CellProcessor;
import org.molgenis.util.tuple.Tuple;
import org.molgenis.util.tuple.ValueIndexTuple;
import org.molgenis.util.tuple.ValueTuple;

/**
 * Comma-Separated Values reader
 * 
 * @see <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
 */
public class CsvReader implements TupleReader
{
	private static final char DEFAULT_SEPARATOR = ',';

	private final au.com.bytecode.opencsv.CSVReader csvReader;
	private final boolean hasHeader;

	/** process cells after reading */
	private List<CellProcessor> cellProcessors;

	public CsvReader(Reader reader)
	{
		this(reader, DEFAULT_SEPARATOR);
	}

	public CsvReader(Reader reader, char separator)
	{
		this(reader, separator, true);
	}

	public CsvReader(Reader reader, char separator, boolean hasHeader)
	{
		if (reader == null) throw new IllegalArgumentException("reader is null");
		this.csvReader = new au.com.bytecode.opencsv.CSVReader(reader, separator);
		this.hasHeader = hasHeader;
	}

	@Override
	public boolean hasColNames()
	{
		return hasHeader;
	}

	@Override
	public Iterator<Tuple> iterator()
	{
		try
		{
			// create column header index once and reuse
			final Map<String, Integer> colNamesMap = hasHeader ? toColNamesMap(csvReader.readNext()) : null;

			return new Iterator<Tuple>()
			{
				private Tuple next;
				private boolean getNext = true;

				@Override
				public boolean hasNext()
				{
					return get() != null;
				}

				@Override
				public Tuple next()
				{
					Tuple tuple = get();
					getNext = true;
					return tuple;
				}

				private Tuple get()
				{
					if (getNext)
					{
						try
						{

							String[] values = csvReader.readNext();
							if (values != null)
							{
								for (int i = 0; i < values.length; ++i)
									values[i] = processCell(values[i], false);
								if (colNamesMap != null) next = new ValueIndexTuple(Arrays.asList(values), colNamesMap);
								else
									next = new ValueTuple(Arrays.asList(values));
							}
							else
							{
								next = null;
							}
							getNext = false;

						}
						catch (IOException e)
						{
							throw new RuntimeException(e);
						}
					}
					return next;
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private Map<String, Integer> toColNamesMap(String[] headers)
	{
		if (headers == null) return null;
		if (headers.length == 0) return Collections.emptyMap();

		int capacity = (int) (headers.length / 0.75) + 1;
		Map<String, Integer> columnIdx = new HashMap<String, Integer>(capacity);
		for (int i = 0; i < headers.length; ++i)
		{
			String header = processCell(headers[i], true);
			columnIdx.put(header, i);
		}
		return columnIdx;
	}

	private String processCell(String value, boolean isHeader)
	{
		return AbstractCellProcessor.processCell(value, isHeader, this.cellProcessors);
	}

	@Override
	public void addCellProcessor(CellProcessor cellProcessor)
	{
		if (cellProcessors == null) cellProcessors = new ArrayList<CellProcessor>();
		cellProcessors.add(cellProcessor);
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			csvReader.close();
		}
		catch (IOException e)
		{
		}
	}
}
