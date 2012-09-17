package convertors.gerard;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.molgenis.pheno.Individual;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvFileWriter;
import org.molgenis.util.CsvReader;
import org.molgenis.util.Tuple;
import org.molgenis.xgap.Marker;

/**
 * Gerards format is as follows:
 * 
 * rs604860343 19 217034 10000000000 rs805420609 19 218039 12001111002 etc, one
 * file per chromosome
 * 
 * @author Morris Swertz
 * 
 */
public class GerardParser
{

	public static void main(String[] args) throws Exception
	{
		File inputDir = new File("D:/Data/athma/source");
		File outputDir = new File("D:/Data/athma/xgap/");

		writeMarkerFile(inputDir, outputDir);
		List<String> colNames = writeIndividualFile(inputDir, outputDir);
		writeMatrixFiles(inputDir, outputDir, colNames);
	}

	public static List<String> writeIndividualFile(File inputDir, File outputDir)
			throws Exception
	{
		final List<String> result = new ArrayList<String>();

		for (File f : inputDir.listFiles())
		{

			File outfile = new File(outputDir.getCanonicalFile()
					+ "/individual.txt");
			CsvReader reader = new CsvFileReader(f);
			CsvFileWriter writer = new CsvFileWriter(outfile);

			// each line is:
			// rs604860343 19 217034 10000000000
			char[] chars = reader.colnames().get(3).toCharArray();
			for (int i = 0; i < chars.length; i++)
			{
				Individual ind = new Individual();
				ind.setName("Ind" + i);
				writer.writeRow(ind);

				result.add(ind.getName());
			}

			writer.close();
			return result;
		}
		return result;
	}

	public static void writeMarkerFile(final File inputDir, final File outputDir)
			throws Exception
	{
		File outfile = new File(outputDir.getCanonicalFile() + "/marker.txt");
		final CsvFileWriter writer = new CsvFileWriter(outfile);

		for (File f : inputDir.listFiles())
		{
			CsvReader reader = new CsvFileReader(f);
			for (Tuple tuple : reader)
			{
				// each line is:
				// rs604860343 19 217034 10000000000

				Marker m = new Marker();
				m.setName(tuple.getString(0));
				m.setChromosome_Name(tuple.getString(1));
				m.setBpStart(tuple.getLong(2));
				writer.writeRow(m);

				// fixme: only write the columns that are not null!!!
				// can we not write the column headers at the end to enable
				// this?
			}

		}
		writer.close();
	}

	public static void writeMatrixFiles(final File inputDir,
			final File outputDir, final List<String> colNames) throws Exception
	{
		for (File f : inputDir.listFiles())
		{
			CsvReader reader = new CsvFileReader(f);

			File outfile = new File(outputDir.getCanonicalFile() + "/"
					+ f.getName() + ".txt");
			final PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(outfile)));

			int line_number = 1;
			for (Tuple tuple : reader)
			{
				// each line is:
				// rs604860343 19 217034 10000000000
				char[] chars = tuple.getString(3).toCharArray();

				// write header once
				if (line_number == 1)
				{
					for (int i = 0; i < chars.length; i++)
					{
						writer.print("\t");
						writer.print(colNames.get(i));
					}
					writer.println();
				}

				// write values
				writer.print(tuple.getString(0));
				for (int i = 0; i < chars.length; i++)
					writer.print("\t" + chars[i]);
				writer.println();

				line_number++;
			}
			writer.close();
		}
	}

}
