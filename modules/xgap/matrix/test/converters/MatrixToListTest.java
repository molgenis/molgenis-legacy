package matrix.test.converters;

import java.io.File;
import java.util.List;

import matrix.implementations.csv.CSVDataMatrixInstance;

import org.molgenis.data.Data;

public class MatrixToListTest
{
	
	public MatrixToListTest(){
		
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		//get the example file
		String path = "handwritten/java/matrix/test/converters/files/";
		String fileName = "tinymatrix.txt";
		File src = new File(path+fileName);
		
		//define a dataset, set some properties
		Data def = new Data();
		def.setName("mydata");
//		def.setFeature_Name("measurement");
//		def.setTarget_Name("patient");
		
		//create an instance of CSV matrix using the file and the data definition
		CSVDataMatrixInstance matrix = new CSVDataMatrixInstance(def, src);
		
		//get the matrix data as a list
		List<String> list = matrix.getAsObservedValueList();
		
		//print the list
		for(String s : list){

		}
	}

}
