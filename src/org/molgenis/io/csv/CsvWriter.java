package org.molgenis.io.csv;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.molgenis.io.TupleWriter;
import org.molgenis.io.processor.AbstractCellProcessor;
import org.molgenis.io.processor.CellProcessor;
import org.molgenis.util.ListEscapeUtils;
import org.molgenis.util.tuple.Tuple;

public class CsvWriter implements TupleWriter
{
	private final au.com.bytecode.opencsv.CSVWriter csvWriter;
	private Tuple header;

	/** process cells before writing */
	private List<CellProcessor> cellProcessors;

	public CsvWriter(Writer writer)
	{
		this(writer, ',');
	}

	public CsvWriter(Writer writer, char separator)
	{
		if (writer == null) throw new IllegalArgumentException("writer is null");
		this.csvWriter = new au.com.bytecode.opencsv.CSVWriter(writer, separator);
	}

	@Override
	public void writeColNames(Tuple tuple) throws IOException
	{
		if (header == null)
		{
			// write header
			int size = tuple.getNrCols();
			String[] values = new String[size];
			Iterator<String> it = tuple.getColNames();
			// get and process header values
			for (int i = 0; i < size; ++i)
				values[i] = AbstractCellProcessor.processCell(it.next(), true, this.cellProcessors);

			this.csvWriter.writeNext(values);
			if (this.csvWriter.checkError()) throw new IOException();

			// store header
			header = tuple;
		}
	}

	@Override
	public void write(Tuple tuple) throws IOException
	{
		String[] values;
		if (header != null && tuple.hasColNames())
		{
			int i = 0;
			values = new String[tuple.getNrCols()];
			for (Iterator<String> it = header.getColNames(); it.hasNext();)
				values[i++] = toValue(tuple.get(it.next()));
		}
		else
		{
			values = new String[tuple.getNrCols()];
			for (int i = 0; i < tuple.getNrCols(); ++i)
				values[i] = toValue(tuple.get(i));
		}

		this.csvWriter.writeNext(values);
		if (this.csvWriter.checkError()) throw new IOException();
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
			this.csvWriter.close();
		}
		catch (IOException e)
		{
		}
	}

	private String toValue(Object obj)
	{
		String value;
		if (obj == null)
		{
			value = null;
		}
		else if (obj instanceof List<?>)
		{
			// TODO apply cell processors to list elements?
			value = ListEscapeUtils.toString((List<?>) obj);
		}
		else
		{
			value = obj.toString();
		}
		return AbstractCellProcessor.processCell(value, false, this.cellProcessors);
	}
}
