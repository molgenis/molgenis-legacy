package tritoplinkslice;

import java.io.File;
import java.io.IOException;
import java.util.zip.DataFormatException;

import tritobin.sources.TriTyperGenotypeData;
import tritobinslice.sources.TriTyperSliceToBinaryMatrix;
import tritoplinkslice.sources.TriTyperSliceToPlink;

public class TriToPlink
{

	/**
	 * Output: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml "Transposed filesets"
	 * @param args
	 * @throws Exception
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, Exception
	{

		String tp = ".tped";
		String tf = ".tfam";
		
		if (args.length != 3)
		{
			throw new DataFormatException(
					"You must supply 3 arguments: input directory, slice file (used to subselect Individuals.txt) and output file name. (e.g. 'result' for result"+tp+" / result"+tf+")");
		}

		// get args
		String inputDir = args[0];
		String outputName = args[1];
		String sliceFile = args[2];

		// print args
		System.out.println("TriToPlink called with arguments:");
		System.out.println("inputDir = " + inputDir);
		System.out.println("outputName = " + outputName);
		System.out.println("sliceFile = " + sliceFile);

		// check if source dir exists
		File src = new File(inputDir);
		if (src == null || !src.exists())
		{
			throw new Exception("Input dir '" + inputDir + "' not found at location '"
					+ src.getAbsolutePath() + "'");
		}
		
		// check if slice file exists
		File slice = new File(sliceFile);
		if (slice == null || !slice.exists())
		{
			throw new Exception("Slice file '" + sliceFile + "' not found at location '"
					+ slice.getAbsolutePath() + "'");
		}
		
		//output name checks
		if(outputName.contains("."))
		{
			throw new Exception("Output file name may not contain a '.'");
		}
		if(outputName.endsWith(tp) || outputName.endsWith(tf))
		{
			throw new Exception("Output file name may not end with "+tp+" or "+tf);
		}

		// check if output PED already exists
		File pedDest = new File(outputName + tp);
		if (pedDest.exists())
		{
			throw new Exception("Output TPED file '" + pedDest.getName() + "' already exists at location '"
					+ pedDest.getAbsolutePath() + "'");
		}
		
		// check if output MAP already exists
		File mapDest = new File(outputName + tf);
		if (mapDest.exists())
		{
			throw new Exception("Output TFAM file '" + mapDest.getName() + "' already exists at location '"
					+ mapDest.getAbsolutePath() + "'");
		}

		//create TriTyperGenotypeData
		TriTyperGenotypeData data = new TriTyperGenotypeData();
		data.load(inputDir);

		//convert to xQTL binary
		TriTyperSliceToPlink toPlink = new TriTyperSliceToPlink(data, pedDest, mapDest, slice);
		boolean success = toPlink.makePedAndMap();
		
		//finish up
		if (!success)
		{
			throw new Exception("ERROR: Creation of Plink files failed!");
		}
		else
		{
			System.out.println("Plink files successfully created! Results: " + pedDest.getAbsolutePath() + " and " + mapDest.getAbsolutePath());
		}
		
	}

}
