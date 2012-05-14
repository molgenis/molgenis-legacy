package org.molgenis.util.plink.drivers;

import static org.molgenis.util.TextFileUtils.*;

import java.io.File;
import java.io.IOException;

import org.molgenis.util.CsvFileReader;

// TODO : Probably possible to gain a lot of performance by using buffered streams instead of files.

public class AbstractFileDriver
{
	protected CsvFileReader reader;
	protected long nrOfElements;

	public AbstractFileDriver(File inFile) throws Exception {
		reader = new CsvFileReader(inFile);
		reader.disableHeader(false);

		if (fileEndsWithNewlineChar(inFile))
		{
			this.nrOfElements = getNumberOfLines(inFile)
					- getAmountOfNewlinesAtFileEnd(inFile);
		}
		else
		{
			this.nrOfElements = getNumberOfLines(inFile);
		}
	}

	/**
	 * Get the number of retrievable annotation elements of this MAP file.
	 * 
	 * @return
	 */
	public long getNrOfElements()
	{
		return nrOfElements;
	}

	/**
	 * Close the underlying file reader
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		this.reader.close();
	}
	
}
