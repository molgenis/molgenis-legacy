package tritoplinkdosage;

import java.io.File;
import java.io.IOException;

import umcg.genetica.io.trityper.converters.TriTyperToPlinkDosage;

public class TriToPlinkDosage
{

	/**
	 * Output: http://pngu.mgh.harvard.edu/~purcell/plink/data.shtml "Transposed filesets"
	 * @param args
	 * @throws Exception
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException, Exception
	{

		if (args.length != 3)
		{
			throw new Exception(
					"You must supply 3 arguments:\n" +
					"* input TriTyper directory location (must exist)\n" +
					"* output directory location (must NOT exist yet)\n" +
					"* whether you want to split on chromosome: 'T' for true, 'F' for false\n" +
					"EXAMPLE: java -jar -Xmx1g TriToPlinkDosage.jar LifeLinesTriTyper/ myOutPutDir/ T\n");
		}
		
		// get args
		String inputDir = args[0];
		String outputDir = args[1];
		String splitChromo = args[2];
		
		// check if input dir does exists
		File input = new File(inputDir);
		if (input == null || !input.exists())
		{
			throw new Exception("ERROR: Input dir '" + inputDir + "' not found at location '"
					+ input.getAbsolutePath() + "'");
		}
		
		// check if output dir does NOT exist
		File output = new File(outputDir);
		if (output.exists())
		{
			throw new Exception("ERROR: Output dir '" + outputDir + "' already exists at location '"+ output.getAbsolutePath() + "'");
		}
		
		if(!(splitChromo.equals("T") || splitChromo.equals("F")))
		{
			throw new Exception("ERROR: Bad argument. Please use 'T' for true or 'F' for false to indicate whether you want to split on chromosomes");
		}
		
		//create output dir and check
		if (!output.mkdirs())
		{
			throw new Exception("ERROR: Failed to create'" + outputDir + "' on location '"+ output.getAbsolutePath() + "'");
		}
		
		//run converter
		TriTyperToPlinkDosage td = new TriTyperToPlinkDosage();
		td.outputDosageInformation(inputDir, outputDir, null, splitChromo.equals("T") ? true : false);

	}

}
