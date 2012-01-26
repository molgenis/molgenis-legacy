package tritoplinklifelines;

import java.io.File;
import java.io.IOException;

import tritobin.sources.TriTyperGenotypeData;
import tritoplinklifelines.sources.TriTyperToPlinkLifeLines;

public class TriToPlinkLifeLines
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
		
		if (args.length != 4)
		{
			throw new Exception(
					"You must supply 4 arguments:\n" +
					"* 'P' for TPED only, 'F' for TFAM only, or 'B' for both\n" +
					"* input directory location\n" +
					"* output file name (e.g. 'result' for result"+tp+" / result"+tf+")\n" +
					"* pseudonymisation+pheno file (used to subselect and pseudonymise Individuals.txt)\n" +
					"PLEASE NOTE: The pseudonymisation+pheno file must be of format: [individual name] [TAB] [new pseudonym] [TAB] [phenotype value], without headers.");
		}

		// get args
		String makeWhat = args[0];
		String inputDir = args[1];
		String outputName = args[2];
		String sliceFile = args[3];

		// print args
		System.out.println("TriToPlinkLifeLines called with arguments:");
		System.out.println("makeWhat = " + makeWhat);
		System.out.println("inputDir = " + inputDir);
		System.out.println("outputName = " + outputName);
		System.out.println("slicePseudoFile = " + sliceFile);
		
		//check makeWhat
		if(!(makeWhat.equals("P") || makeWhat.equals("F") || makeWhat.equals("B")))
		{
			throw new Exception("Bad argument: Please specify 'P' for TPED only, 'F' for TFAM only, or 'B' for both");
		}

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

		// check if output TPED already exists
		File tpedDest = new File(outputName + tp);
		if(makeWhat.equals("P") || makeWhat.equals("B")){
			
			if (tpedDest.exists())
			{
				throw new Exception("Output TPED file '" + tpedDest.getName() + "' already exists at location '"
						+ tpedDest.getAbsolutePath() + "'");
			}
		}
		
		// check if output TFAM already exists
		File tfamDest = new File(outputName + tf);
		if(makeWhat.equals("F") || makeWhat.equals("B")){
			if (tfamDest.exists())
			{
				throw new Exception("Output TFAM file '" + tfamDest.getName() + "' already exists at location '"
						+ tfamDest.getAbsolutePath() + "'");
			}
		}

		//create TriTyperGenotypeData
		TriTyperGenotypeData data = new TriTyperGenotypeData();
		data.load(inputDir);

		//convert to PLINK
		TriTyperToPlinkLifeLines toPlink = new TriTyperToPlinkLifeLines(data, tpedDest, tfamDest, slice);
		boolean success = toPlink.makeTPEDAndFAM(makeWhat);
		
		//finish up
		if (!success)
		{
			throw new Exception("ERROR: Creation of Plink files failed!");
		}
		else
		{
			System.out.println("Plink files successfully created! Results: " + tpedDest.getAbsolutePath() + " and " + tfamDest.getAbsolutePath());
		}
		
	}

}
