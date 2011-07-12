package org.molgenis.matrix.convertors;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Value convertors to support different valueTypes. This is used by CsvMatrtix
 * to convert Strings from CSV to, for example, Double instances.
 * 
 * NB this should be merged with org.molgenis.fieldtypes???
 */
public interface ValueConvertor<E>
{
	public E read(String value);
	
	public String write(E value);

	public Class<E> getValueType();

	public E read(RandomAccessFile raf) throws IOException;
}
