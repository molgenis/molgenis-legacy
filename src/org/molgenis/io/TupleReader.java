package org.molgenis.io;

import java.io.Closeable;

import org.molgenis.io.processor.CellProcessor;
import org.molgenis.util.tuple.Tuple;

public interface TupleReader extends Iterable<Tuple>, Closeable
{
	/**
	 * Returns whether tuples have corresponding column names
	 * 
	 * @return
	 */
	public boolean hasColNames();

	/**
	 * Add a cell processor to process cell values
	 * 
	 * @param cellProcessor
	 */
	public void addCellProcessor(CellProcessor cellProcessor);
}
