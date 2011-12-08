package tritobinslice;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import tritobin.sources.TriTyperGenotypeData;
import tritobinslice.sources.TriTyperSliceToBinaryMatrix;

public class TriToBinSlice
{

	/**
	 * @param args
	 * @throws Exception
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, Exception
	{

		if (args.length != 5)
		{
			throw new DataFormatException(
					"You must supply 5 arguments: input directory, output file, slice file (used to subselect Individuals.txt), data name and investigation name.");
		}

		// get args
		String inputDir = args[0];
		String outputFile = args[1];
		String sliceFile = args[2];
		String dataName = args[3];
		String invName = args[4];

		// print args
		System.out.println("TriToBinSlice called with arguments:");
		System.out.println("inputDir = " + inputDir);
		System.out.println("outputFile = " + outputFile);
		System.out.println("sliceFile = " + sliceFile);
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
		
		// check if slice file exists
		File slice = new File(sliceFile);
		if (slice == null || !slice.exists())
		{
			throw new Exception("Slice file '" + sliceFile + "' not found at location '"
					+ slice.getAbsolutePath() + "'");
		}

		//create TriTyperGenotypeData
		TriTyperGenotypeData data = new TriTyperGenotypeData();
		data.load(inputDir);

		//convert to xQTL binary
		TriTyperSliceToBinaryMatrix toBin = new TriTyperSliceToBinaryMatrix(data, dest, slice);
		toBin.makeBinaryBackend(dataName, invName);
	}

}
