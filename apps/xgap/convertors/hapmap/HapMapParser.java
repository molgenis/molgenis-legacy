package convertors.hapmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class HapMapParser

{
	String SEP = "\t";

	public HapMapParser(String filePath) throws IOException
	{
		String basePath = filePath.substring(0, filePath.lastIndexOf("/"));
		File xgapDir = new File(basePath + "/xgapnized/"
				+ filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length() - 4));
		xgapDir.mkdirs();

		parse(filePath, xgapDir.getAbsolutePath());

		// speciesFile(filePath, xgapDir.getAbsolutePath());
	}

	//TODO: Danny: Use the function and then remove the warning suppression
	@SuppressWarnings("unused")
	private void speciesFile(String filePath, String xgapDir) throws IOException
	{
		List<String> file = new ArrayList<String>();
		file.add("name" + SEP + "term" + SEP + "termaccession");
		file.add("Homo sapiens" + SEP + "Human" + SEP + "NEWT:9606");
		writeFile(file, xgapDir + "/species.txt");
	}

	private void parse(String filePath, String basePath) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(filePath));

		String firstLine = in.readLine();
		writeFile(findIndividuals(firstLine, true), basePath + "/individual.txt");

		PrintWriter markers = new PrintWriter(new BufferedWriter(new FileWriter(basePath + "/marker.txt")));
		addToFile(markers, "name\tchr\tbpstart\tspecies_name\tseq");

		PrintWriter matrix = new PrintWriter(new BufferedWriter(new FileWriter(basePath + "/matrix.txt")));
		addToFile(matrix, concat(findIndividuals(firstLine, false), SEP));

		String line;
		while ((line = in.readLine()) != null)
		{
			String[] split = line.split(" ");
			addToFile(markers, split[0] + SEP + split[2].replace("chr", "") + SEP + split[3] + SEP + "Homo sapiens"
					+ SEP + split[1]);

			String genotypes = split[0] + SEP;
			for (int i = 11; i < split.length; i++)
			{
				genotypes += split[i] + SEP;
			}
			addToFile(matrix, genotypes);
		}
		markers.close();
		matrix.close();
	}

	private String concat(List<String> input, String sep)
	{
		String output = "";
		for (String s : input)
		{
			output += s + sep;
		}
		return output;
	}

	private List<String> findIndividuals(String line, boolean addName)
	{
		List<String> indiv = new ArrayList<String>();
		if (addName)
		{
			indiv.add("name");
		}
		for (String split : line.split(" "))
		{
			if (split.substring(0, 2).equals("NA"))
			{
				indiv.add(split);
			}
		}
		return indiv;
	}

	private void writeFile(List<String> lines, String filePath) throws IOException
	{
		PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));
		for (String line : lines)
		{
			printWriter.println(line);

		}
		printWriter.flush();
		printWriter.close();

	}

	private void addToFile(PrintWriter printWriter, String line)
	{
		printWriter.println(line);
		printWriter.flush();
	}

	public static void main(String[] args) throws IOException
	{
		// new HapMapParser(
		// "D:/data/xgapdata/HumanPublicSets/genotypes_chr1_ASW_r27_nr.b36_fwd.txt"
		// );
		new HapMapParser("D:/data/xgapdata/HumanPublicSets/genotypes_chr1_CHD_r27_nr.b36_fwd.txt");
		new HapMapParser("D:/data/xgapdata/HumanPublicSets/genotypes_chr8_LWK_r27_nr.b36_fwd.txt");
		new HapMapParser("D:/data/xgapdata/HumanPublicSets/genotypes_chr19_ASW_r27_nr.b36_fwd.txt");
		new HapMapParser("D:/data/xgapdata/HumanPublicSets/genotypes_chr17_TSI_r27_nr.b36_fwd.txt");
		new HapMapParser("D:/data/xgapdata/HumanPublicSets/genotypes_chr13_MKK_r27_nr.b36_fwd.txt");

	}

}
