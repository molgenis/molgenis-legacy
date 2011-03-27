package org.molgenis.matrix.convertors;

/**
 * Value convertors to support different valueTypes. This is used by CsvMatrtix
 * to convert Strings from CSV to, for example, Double instances.
 */
public interface CsvMatrixValueConvertor
{
	Object convert(String value);
}
