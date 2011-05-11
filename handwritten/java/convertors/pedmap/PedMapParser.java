package convertors.pedmap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PedMapParser
{

	public PedMapParser(String pedFilePath, String mapFilePath) throws IOException
	{
		String basePath = pedFilePath.substring(0, pedFilePath.lastIndexOf("/"));
		File xgapDir = new File(basePath + "/xgapnized/"
				+ pedFilePath.substring(pedFilePath.lastIndexOf("/") + 1, pedFilePath.length() - 4));
		xgapDir.mkdirs();

		createStrain("WGACON", xgapDir.getAbsolutePath());
		String markerNames = parseMap(mapFilePath, xgapDir.getAbsolutePath());
		parsePed(pedFilePath, xgapDir.getAbsolutePath(), markerNames);
		
		
		
	}

	private void createStrain(String name, String dir) throws IOException
	{
		PrintWriter strain = new PrintWriter(new BufferedWriter(new FileWriter(dir + "/strain.txt")));
		strain.println("name\tstraintype");
		strain.println(name+"\tNatural");
		strain.flush();
		strain.close();
	}

	private String parseMap(String mapFilePath, String dir) throws IOException
	{
		PrintWriter markers = new PrintWriter(new BufferedWriter(new FileWriter(dir + "/marker.txt")));
		addToFile(markers, "name\tchr\tbpstart\tspecies_name\tseq");
		
		BufferedReader in = new BufferedReader(new FileReader(mapFilePath));
		String markerNames = "";

		String line;
		int index = 0;
		while ((line = in.readLine()) != null && index < 496) //bad! but other file is chopped up
		{
			String[] split = line.split(" ");
			addToFile(markers, split[1] + "\t" + split[0] + "\t" + split[3] + "\t" + "Homo sapiens" + "\t" + split[2]);
			markerNames += split[1] + "\t";
			index++;
		}
		markers.close();
		in.close();
		return markerNames;
	}

	private void parsePed(String pedFilePath, String dir, String markerNames) throws IOException
	{
		PrintWriter individuals = new PrintWriter(new BufferedWriter(new FileWriter(dir + "/individual.txt")));
		addToFile(individuals, "name" + "\t" + "strain_name" + "\t" + "father_name" + "\t" + "mother_name");
		
		PrintWriter matrix = new PrintWriter(new BufferedWriter(new FileWriter(dir + "/matrix.txt")));
		addToFile(matrix, markerNames);

		BufferedReader in = new BufferedReader(new FileReader(pedFilePath));

		String line;
		while ((line = in.readLine()) != null){
			String[] splitTab = line.split("\t");
			addToFile(individuals, "Ind"+splitTab[1] + "\t" + splitTab[0] + "\t" + "Ind"+splitTab[2] + "\t" + "Ind"+splitTab[3]);

			String genotype = "\t";
			String[] splitSpace = splitTab[5].split(" ");
			for(int i=0; i<splitSpace.length-1;i++){
				if(i%2==0){
				genotype += splitSpace[i+1];
				}else{
					genotype += splitSpace[i+1] + "\t";
				}
			}
			addToFile(matrix, "Ind"+splitTab[1] + genotype);
		}
		individuals.close();
		matrix.close();
		in.close();
	}
	
	private void addToFile(PrintWriter printWriter, String line)
	{
		printWriter.println(line);
		printWriter.flush();
	}

	public static void main(String[] args) throws IOException
	{
		new PedMapParser("D:/data/xgapdata/HumanPublicSets/193sgenome_sample.ped", "D:/data/xgapdata/HumanPublicSets/193sgenome.map");

	}

}
