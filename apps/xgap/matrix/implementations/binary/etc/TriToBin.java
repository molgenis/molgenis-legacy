package matrix.implementations.binary.etc;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.molgenis.util.trityper.reader.TriTyperGenotypeData;

public class TriToBin
{

	/**
	 * @param args
	 * @throws Exception
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, Exception
	{

		if (args.length != 4)
		{
			throw new DataFormatException(
					"You must supply 4 arguments: input directory, output file, data name and investigation name.");
		}

		// get args
		String inputDir = args[0];
		String outputFile = args[1];
		String dataName = args[2];
		String invName = args[3];

		// print args
		System.out.println("TriToBin called with arguments:");
		System.out.println("inputDir = " + inputDir);
		System.out.println("outputFile = " + outputFile);
		System.out.println("dataName = " + dataName);
		System.out.println("invName = " + invName);

		// check if source dir exists
		File src = new File(inputDir);
		if (src == null || !src.exists())
		{
			throw new Exception("Input dir '" + inputDir + "' not found at location '"
					+ src.getAbsolutePath() + "'");
		}

		// check if output already exists
		File dest = new File(outputFile);
		if (dest.exists())
		{
			throw new Exception("Output file '" + outputFile + "' already exists at location '"
					+ dest.getAbsolutePath() + "'");
		}

		//create TriTyperGenotypeData
		TriTyperGenotypeData data = new TriTyperGenotypeData();
		data.load(inputDir);

		//convert to xQTL binary
		TriTyperToBinaryMatrix toBin = new TriTyperToBinaryMatrix(data, dest);
		toBin.makeBinaryBackend(dataName, invName);
	}

}
