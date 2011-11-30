package bintocsv;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.zip.DataFormatException;

import bintocsv.sources.BinaryDataMatrixInstance;

public class BinToCsv
{
	public static void main(String[] args) throws Exception
	{
		if (args.length != 1)
		{
			throw new DataFormatException("You must supply 1 argument: source file name.");
		}

		String fileString = args[0];

		// check if source file exists and ends with '.bin'
		File src = new File(fileString);
		if (src == null || !src.exists())
		{
			throw new Exception("Source file '" + fileString + "' not found at location '"
					+ src.getAbsolutePath() + "'");
		}
		if (!src.getName().endsWith(".bin"))
		{
			throw new Exception("Source file name '" + fileString
					+ "' does not end with '.bin', are you sure it is a Binary matrix?");
		}

		System.out.println("Source file exists and ends with '.bin'..");

		BinaryDataMatrixInstance instance = new BinaryDataMatrixInstance(src);

		File dest = new File(src.getName().substring(0, (src.getName().length() - 4)) + ".txt");
		if (dest.exists())
		{
			throw new IOException("Destination file '" + dest.getName() + "' already exists");
		}

		System.out.println("Starting conversion..");

		PrintStream p = new PrintStream(new BufferedOutputStream(new FileOutputStream(dest)));
		instance.toPrintStream(p);
		p.close();

		System.out.println("..done!");
	}
}
