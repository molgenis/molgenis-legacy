package csvtobin;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.naming.NamingException;

import csvtobin.sources.CsvFileReader;
import csvtobin.sources.Data;
import csvtobin.sources.MakeBinary;
import csvtobin.sources.NameConvention;
import csvtobin.sources.VerifyCsv;

public class CsvToBin
{
	
	public static void main(String[] args) throws Exception
	{
		if(args.length != 6){
			throw new DataFormatException("You must supply 6 arguments: data name, investigation name, row type, column type, value type, and source file name.");
		}
		
		//get args
		String dataName = args[0];
		String invName = args[1];
		String rowType = args[2];
		String colType = args[3];
		String valType = args[4];
		String fileString = args[5];

		//print args
		System.out.println("CsvToBin called with arguments:");
		System.out.println("data name = " + dataName);
		System.out.println("investigation name = " + invName);
		System.out.println("row type = " + rowType);
		System.out.println("column type = " + colType);
		System.out.println("value type = " + valType);
		System.out.println("source file = " + fileString);
		
		//check if source file exists and ends with '.txt'
		File src = new File(fileString);
		if (src == null || !src.exists()){
			throw new Exception("Source file '"+fileString+"' not found at location '"+src.getAbsolutePath()+"'");
		}
		if(!src.getName().endsWith(".txt")){
			throw new Exception("Source file name '"+fileString+"' does not end with '.txt', are you sure it is a CSV matrix?");
		}
		
		System.out.println("Source file exists and ends with '.txt'..");
		
		//create Data object, validate the names and valuetype
		Data d = new Data();
		d.setName(dataName);
		d.setInvestigation_Name(invName);
		d.setTargetType(rowType);
		d.setFeatureType(colType);
		d.setValueType(valType);
		
		//FIXME: strict should only be applied when application is an XGAP
		NameConvention.validateEntityNameStrict(dataName);
		NameConvention.validateEntityNameStrict(invName);
		
		System.out.println("'Data' object created..");
		
		if (!valType.equals("Text") && !valType.equals("Decimal"))
		{
			throw new NamingException("Value type '"+valType+"' not reckognized. Use 'Text' or 'Decimal'.");
		}
		
		System.out.println("Valuetype OK..");
		
		//verify the CSV file to be a correct matrix and get the dimensions
		int[] dims = VerifyCsv.verify(src, valType);
		
		System.out.println("CSV input file verified..");
		
		//convert to binary
		File dest = new File(src.getName().substring(0, (src.getName().length()-4)) + ".bin");
		
		System.out.println("Starting conversion..");
		
		new MakeBinary().makeBinaryBackend(d, src, dest, dims[0], dims[1]);
		
		System.out.println("..done!");
	}

}
