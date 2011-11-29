package convertors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ChopFile {

	public static int LINES_BLOCK_SIZE = 10000;
	
	public static void chopFile(File inputFile){
		chopFile(inputFile, LINES_BLOCK_SIZE);
	}
	
	public static void chopFile(File inputFile, int linesBlockSize){
		chopFile(inputFile, linesBlockSize, false);
	}
	
	public static void chopFile(File inputFile, boolean headOnly){
		chopFile(inputFile, LINES_BLOCK_SIZE, headOnly);
	}
	
	public static void chopFile(File inputFile, int linesBlockSize, boolean headOnly){
		try {
		  BufferedReader input = new BufferedReader(new FileReader(inputFile));
		  BufferedWriter output = new BufferedWriter(new FileWriter(inputFile.getAbsolutePath() + ".part1"));
		  try {
			String line = null;
			int count = 0;
			while (( line = input.readLine()) != null && count < linesBlockSize){
			output.write(line.substring(0, 2002)); //# chars per line
			output.write(System.getProperty("line.separator"));
			count++;
			}
		  }
		  finally {
			input.close();
			output.close();
		  }
		}
		catch (IOException ex){
		  ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		String fileLocation = "D:/data/xgapdata/HumanPublicSets/193sgenome.ped";
		File inputFile = new File(fileLocation);
		ChopFile.chopFile(inputFile, 1000, true);
	}

}
