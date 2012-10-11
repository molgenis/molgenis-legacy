package org.molgenis.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;

public class TextFileUtils
{

	/**
	 * Count number of lines in the file. Add 1 extra because this only counts
	 * newlines, therefore 1 newline = 2 lines in the file. Consider using
	 * fileEndsWithNewlineChar() in combination with this function. See:
	 * http://stackoverflow
	 * .com/questions/453018/number-of-lines-in-a-file-in-java
	 * 
	 * @param inFile
	 * 
	 * @return
	 * @throws IOException
	 */
	public static int getNumberOfLines(File inFile) throws IOException
	{
		LineNumberReader lnr = null;
		try
		{
			lnr = new LineNumberReader(new FileReader(inFile));
			lnr.skip(Long.MAX_VALUE);
			return lnr.getLineNumber() + 1;
		}
		finally
		{
			if (lnr != null)
			{
				lnr.close();
			}
		}
	}

	/**
	 * Find out if the source file ends with a newline character. Useful in
	 * combination with getNumberOfLines().
	 * 
	 * @param inFile
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean fileEndsWithNewlineChar(File inFile) throws Exception
	{
		RandomAccessFile raf = new RandomAccessFile(inFile, "r");
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
	 * @param inFile
	 * 
	 * @return
	 * @throws Exception
	 */
	public static int getAmountOfNewlinesAtFileEnd(File inFile) throws Exception
	{
		RandomAccessFile raf = new RandomAccessFile(inFile, "r");

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

		// replace \r\n combinations with \n (note: separators are added in
		// reverse
		// order)
		terminatorSequence = terminatorSequence.replaceAll("nr", "n");

		return terminatorSequence.length();

	}
}
