package org.molgenis.util;

import java.nio.charset.Charset;

/**
 * CSV input/output defaults
 */
public interface CsvParser
{
	/** default CSV input/output charset */
	public static final Charset CSV_DEFAULT_CHARSET = Charset.forName("UTF-8");
	/** default CSV input/output column separator */
	public static final char CSV_DEFAULT_SEPARATOR = '\t';
	/** default CSV input/output line separator */
	public static final String CSV_DEFAULT_LINE_SEPARATOR = "\n";
	/** default character to enclose values */
	public static final char CSV_DEFAULT_QUOTE_CHARACTER = '"';
}
