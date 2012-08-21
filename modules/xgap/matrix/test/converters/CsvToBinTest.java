package matrix.test.converters;

import java.io.File;

import matrix.general.CsvToBin;

public class CsvToBinTest
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
		String fileName = "randomdecimaldata.txt";
		//"metaboliteexpression.txt";
		//"randomtextdata.txt";
		
		//set args and run CsvToBin
		args = new String[]{"DataName", "InvName", "RowType", "ColType", "Decimal", path+fileName};
		CsvToBin.main(args);
		
		//result checking
		File dest = new File((fileName.substring(0, (fileName).length()-4)) + ".bin");
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
