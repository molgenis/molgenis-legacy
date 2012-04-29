package csvtobin.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;

/**
 * CsvReader for delimited text files.
 * 
 * @see org.molgenis.util.CsvReader#parse
 */
public class CsvFileReader extends CsvBufferedReaderMultiline
{
	/** the File that is being read */
	private File file;

	public CsvFileReader(File file) throws FileNotFoundException
	{
		super(new BufferedReader(new FileReader(file)));
		this.file = file;
	}

	/**
	 * Count number of lines in the file. Add 1 extra because this only counts
	 * newlines, therefore 1 newline = 2 lines in the file. Consider using
	 * fileEndsWithNewlineChar() in combination with this function. See:
	 * http://stackoverflow
	 * .com/questions/453018/number-of-lines-in-a-file-in-java
	 * 
	 * @return
	 * @throws IOException
	 */
	public int getNumberOfLines() throws IOException
	{
		LineNumberReader lnr = new LineNumberReader(new FileReader(this.file));
		lnr.skip(Long.MAX_VALUE);
		return lnr.getLineNumber() + 1;
	}

	/**
	 * Find out if the source file ends with a newline character. Useful in
	 * combination with getNumberOfLines().
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean fileEndsWithNewlineChar() throws Exception
	{
		RandomAccessFile raf = new RandomAccessFile(this.file, "r");
		raf.seek(raf.length() - 1);
		char c = (char) raf.readByte();
		raf.close();
		if (c == '\n' || c == '\r')
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Get the amount of newline characters at the end of a file. Can be of
	 * great help when you want to judge the amount of elements in a file based
	 * on the number of lines, when the file might contain (many) empty trailing
	 * newlines. The amount of \r and \n terminators are counted. The
	 * combination \r\n is reduced to \n before counting. You will probably want
	 * to use this in combination with the more lightweight check of
	 * fileEndsWithNewlineChar().
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getAmountOfNewlinesAtFileEnd() throws Exception
	{
		RandomAccessFile raf = new RandomAccessFile(this.file, "r");

		int nrOfNewLines = 1;
		boolean countingNewlines = true;
		String terminatorSequence = "";

		while (countingNewlines)
		{
			raf.seek(raf.length() - nrOfNewLines);
			char c = (char) raf.readByte();

			if (c == '\r')
			{
				terminatorSequence += "r";
				nrOfNewLines++;
			}
			else if (c == '\n')
			{
				terminatorSequence += "n";
				nrOfNewLines++;
			}
			else
			{
				countingNewlines = false;
			}
		}

		raf.close();

		// replace \r\n combinations with \n
		terminatorSequence.replaceAll("rn", "n");

		return terminatorSequence.length();

	}

	// @Override
	public void reset() throws IOException
	{
		super.reset();
		this.reader.close();
		this.reader = new BufferedReader(new FileReader(file));
	}
}
