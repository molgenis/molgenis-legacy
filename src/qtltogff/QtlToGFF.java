package qtltogff;

import java.io.File;
import java.util.zip.DataFormatException;

import qtltogff.sources.QtlToGFFConverter;

public class QtlToGFF
{
	public static void main(String[] args) throws Exception
	{
		if (args.length != 10)
		{
			throw new DataFormatException("\n\nYou must supply 10 arguments:\n" +
					"\n" +
					" - Data matrix file (binary xQTL format, Trait x Marker or vice versa)\n"+
					" - Chromosome annotation file, no headers, containing: name[TAB]gffname[TAB]bplength[TAB]ordernr (order number matters for cumulative positioning)\n"+
					" - Marker file, no headers, containing: name[TAB]bppos[TAB]chromosome_name\n"+
					" - Trait file, no headers, containing: name[TAB]bppos[TAB]chromosome_name (PLEASE NOTE: may be missing if boolean (a) is set to False, fill in 'null' or whatever)\n"+
					" - Boolean (a): trait has genomic locations, T/F.\n"+
					" - Boolean (b): trait basepair positions are cumulative, T/F.\n"+
					" - Boolean (c): marker basepair positions are cumulative, T/F.\n"+
					" - LOD threshold: minimum value for a LOD score to be considered a significant QTL (usually 3.5)\n"+
					" - LOD drop: the amount of LOD score a peak must drop to form a confidence interval (usually 1.5)\n"+
					" - LOD limit: the amount of LOD score that forms the roof of a '1000 score' in the GFF file (usually 10.0)\n"+
					"\n" +
					"Output: data matrix file name + .gff, e.g. 'qtl_results.bin' becomes 'qtl_results.gff'\n" +
					"\n" +
					"Example:\n" +
					"java -jar -Xmx1g QtlToGFF.jar qtlmatrix.bin chromo.txt markers.txt probes.txt T F F 3.5 1.5 10.0\n");
		}
		
		//track source: QtlToGFF
		//track name: data matrix, minus '.txt'
		//track description: track name + " QTL result track, created by QtlToGFF".
		
		/**
		 * Example GFF:
		 	browser position chrII:1000000-1005000
			browser hide all
			track name=QTLs	description="WormQTL mapping results" visibility=2	useScore=1
			chrII	WormQTL	qtl_interval	1000000	1000500	500	.	.	AGIUSA0001
			chrII	WormQTL	probe_loc	1001000	1001030	.	.	.	AGIUSA0001
			chrII	WormQTL	qtl_interval	1002100	1002600	1000	.	.	AGIUSA0002
			chrII	WormQTL	probe_loc	1004000	1004030	.	.	.	AGIUSA0002
			chrII	WormQTL	qtl_interval	1002000	1002500	900	.	.	WSU00456
			chrII	WormQTL	probe_loc	1002200	1002230	.	.	.	WSU00456
		 */
		
		//See: http://genome.ucsc.edu/FAQ/FAQformat.html#format3

		String matrixFileString = args[0];
		String chromosomeFileString = args[1];
		String markerFileString = args[2];
		String traitFileString = args[3];
		String trait_has_bppos = args[4];
		String cumu_trait_bppos = args[5];
		String cumu_marker_bppos = args[6];
		String lod_thres = args[7];
		String lod_drop = args[8];
		String lod_limit = args[9];

		// check if matrix file exists and ends with '.bin'
		File matrixFile = new File(matrixFileString);
		if (matrixFile == null || !matrixFile.exists())
		{
			throw new Exception("Matrix file '" + matrixFileString + "' not found at location '"
					+ matrixFile.getAbsolutePath() + "'");
		}
		if (!matrixFile.getName().endsWith(".bin"))
		{
			throw new Exception("Matrix file name '" + matrixFileString
					+ "' does not end with '.bin'");
		}
		System.out.println("Matrix file exists and ends with '.bin'..");
		
		//check if output already exists!
		String matrixName = matrixFile.getName().substring(0, matrixFile.getName().length()-4);
		File output = new File(matrixName + ".gff");
		if (output.exists())
		{
			throw new Exception("ERROR: Output file '" + output + "' already exists at location '"+ output.getAbsolutePath() + "'");
		}
		
		//other checks..
		File chromosomeFile = new File(chromosomeFileString);
		if (chromosomeFile == null || !chromosomeFile.exists())
		{
			throw new Exception("Chromosome file '" + chromosomeFileString + "' not found at location '"
					+ chromosomeFile.getAbsolutePath() + "'");
		}
		System.out.println("Chromosome file exists..");
		
		File markerFile = new File(markerFileString);
		if (markerFile == null || !markerFile.exists())
		{
			throw new Exception("Marker file '" + markerFileString + "' not found at location '"
					+ markerFile.getAbsolutePath() + "'");
		}
		System.out.println("Marker file exists..");
		
		if(!(trait_has_bppos.equals("T") || trait_has_bppos.equals("F")))
		{
			throw new Exception("ERROR: Bad argument. Please use 'T' for true or 'F' for false to indicate whether your traits have basepair locations or not (and subsequently, if a trait file must be present or not)");
		}
		
		File traitFile = new File(traitFileString);
		if(trait_has_bppos.equals("T"))
		{
			if (traitFile == null || !traitFile.exists())
			{
				throw new Exception("Trait file '" + traitFileString + "' not found at location '"
						+ traitFile.getAbsolutePath() + "'");
			}
			System.out.println("Trait file exists..");
		}
		else
		{
			System.out.println("Trait file not required when traits have not genomic locations, skipping check..");
		}
		
		if(!(cumu_trait_bppos.equals("T") || cumu_trait_bppos.equals("F")))
		{
			throw new Exception("ERROR: Bad argument. Please use 'T' for true or 'F' for false to indicate whether your trait basepair positions are cumulative or not (even if you don't have them)");
		}
		
		if(!(cumu_marker_bppos.equals("T") || cumu_marker_bppos.equals("F")))
		{
			throw new Exception("ERROR: Bad argument. Please use 'T' for true or 'F' for false to indicate whether your marker basepair positions are cumulative or not");
		}
		
		try
		{
			Double.parseDouble(lod_thres);
		}
		catch(Exception e)
		{
			throw new Exception("ERROR: Bad argument. LOD threshold is not a double");
		}
		
		try
		{
			Double.parseDouble(lod_drop);
		}
		catch(Exception e)
		{
			throw new Exception("ERROR: Bad argument. LOD drop is not a double");
		}
		
		try
		{
			Double.parseDouble(lod_limit);
		}
		catch(Exception e)
		{
			throw new Exception("ERROR: Bad argument. LOD limit is not a double");
		}
		
		System.out.println("All other arguments OK...");
		
		boolean a = trait_has_bppos.equals("T") ? true : false;
		boolean b = cumu_trait_bppos.equals("T") ? true : false;
		boolean c = cumu_marker_bppos.equals("T") ? true : false;
		
		new QtlToGFFConverter(matrixFile, chromosomeFile, markerFile, traitFile, a, b, c, Double.parseDouble(lod_thres), Double.parseDouble(lod_drop), Double.parseDouble(lod_limit), output);
	
		
	}
}
