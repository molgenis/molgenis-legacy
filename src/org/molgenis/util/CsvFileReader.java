package org.molgenis.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.zip.DataFormatException;

/**
 * CSV reader for <a href="http://tools.ietf.org/html/rfc4180">comma-separated
 * value files</a>
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvFileReader extends CsvBufferedReaderMultiline
{
	/** Input comma-separated value file */
	private File file;
	/** Input file encoding */
	private Charset charset;

	/**
	 * Creates a CsvFileReader that uses the UTF-8 charset
	 * 
	 * @param file
	 *            comma-separated values file
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public CsvFileReader(File file) throws IOException
	{
		this(file, CSV_DEFAULT_CHARSET, true);
	}

	/**
	 * Creates a CsvFileReader that uses the UTF-8 charset
	 * 
	 * @param file
	 *            comma-separated values file
	 * @param blockStart
	 *            last line before header block starts
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public CsvFileReader(File file, String blockStart) throws IOException
	{
		this(file, CSV_DEFAULT_CHARSET, true, blockStart);
	}

	/**
	 * Creates a CsvFileReader that uses the UTF-8 charset
	 * 
	 * @param file
	 *            comma-separated values file
	 * @param hasHeader
	 *            whether or not this file starts with a header
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public CsvFileReader(File file, boolean hasHeader) throws IOException
	{
		this(file, CSV_DEFAULT_CHARSET, hasHeader);
	}

	/**
	 * Creates a CsvFileReader that uses the given charset
	 * 
	 * @param file
	 *            comma-separated values file
	 * @param charset
	 *            file encoding
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public CsvFileReader(File file, Charset charset) throws IOException
	{
		this(file, charset, true);
	}

	/**
	 * Creates a CsvFileReader that uses the given charset
	 * 
	 * @param file
	 *            comma-separated values file
	 * @param charset
	 *            file encoding
	 * @param hasHeader
	 *            whether or not this file starts with a header
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public CsvFileReader(File file, Charset charset, boolean hasHeader) throws IOException
	{
		this(file, charset, hasHeader, "");
	}

	/**
	 * Creates a CsvFileReader that uses the given charset
	 * 
	 * @param file
	 *            comma-separated values file
	 * @param charset
	 *            file encoding
	 * @param hasHeader
	 *            whether or not this file starts with a header
	 * @param blockStart
	 *            last line before header block starts
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public CsvFileReader(File file, Charset charset, boolean hasHeader, String blockStart) throws IOException
	{
		super();
		if (file == null) throw new IllegalArgumentException("file is null");
		this.file = file;
		this.charset = charset;
		this.hasHeader = hasHeader;
		this.blockStart = blockStart;
		this.reset();
	}

	@Override
	public void reset() throws IOException
	{
		if (this.reader != null) this.reader.close();
		this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
		super.reset();
	}
}
