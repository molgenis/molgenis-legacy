package tritoplinkslice.sources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Subclass of CsvWriter to write csv/tab to a file
 */
public class CsvFileWriter extends CsvWriter
{
	public CsvFileWriter(File f) throws IOException
	{
		super(new PrintWriter(new BufferedWriter(new FileWriter(f))));
	}
	
	/**
	 * @param f the file to be written to
	 * @throws IOException
	 */
	public CsvFileWriter(File f, List<String> fields) throws IOException
	{
		super(new PrintWriter(new BufferedWriter(new FileWriter(f))), fields);
	}

	/**
	 * Append to existing csv using custom headers.
	 * @param file
	 * @param fields
	 * @param append
	 * @throws IOException 
	 */
	public CsvFileWriter(File file, List<String> fields, boolean append) throws IOException
	{
		super(new PrintWriter(new BufferedWriter(new FileWriter(file,append))), fields);
	}

}
