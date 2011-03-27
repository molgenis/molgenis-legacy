package matrix.test.converters;

import java.io.File;

import matrix.general.BinToCsv;

public class BinToCsvTest
{

	/**
	 * @param args
	 * @throws Exception 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		//the input file
		String path = "handwritten/java/matrix/test/converters/files/";
		String fileName = "randomdecimaldata.bin";
		//"metaboliteexpression.txt";
		//"randomtextdata.txt";
		
		//set args and run CsvToBin
		args = new String[]{path+fileName};
		BinToCsv.main(args);
		
		//result checking
		File dest = new File((fileName.substring(0, (fileName).length()-4)) + ".txt");
		System.out.println("Source file size = " + new File(path+fileName).length() + " bytes");
		if(dest.exists()){
			System.out.println("Destination file present at '"+dest.getAbsolutePath()+"'");
			System.out.println("Destination file size = " + dest.length() + " bytes");
		}else{
			System.out.println("FAILURE - Destination file NOT present at '"+dest.getAbsolutePath()+"'");
		}
		
		boolean delete = dest.delete();
		
		if(delete){
			System.out.println("Destination file removed");
		}else{
			System.out.println("WARNING: Destination file not removed");
		}

	}

}
