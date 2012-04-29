package csvtobinondir;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;

import javax.naming.NamingException;

import csvtobin.sources.CsvFileReader;
import csvtobin.sources.CsvReaderListener;
import csvtobin.sources.Data;
import csvtobin.sources.MakeBinary;
import csvtobin.sources.NameConvention;
import csvtobin.sources.Tuple;
import csvtobin.sources.VerifyCsv;

public class CsvToBinOnDir
{
	
	public static ArrayList<Data> getDataObjFromFile(File dataDesc) throws Exception
	{
		CsvFileReader reader = new CsvFileReader(dataDesc);
		final ArrayList<Data> result = new ArrayList<Data>();
		reader.parse(new CsvReaderListener()
		{
			public void handleLine(int line_number, Tuple tuple) throws Exception
			{
				//parse object, setting defaults and values from file
				Data object = new Data();
				object.set(tuple, false);				
				result.add(object);		
			}
		});
		return result;
	}
	
	public static void main(String[] args) throws Exception
	{
		if(args.length != 1){
			throw new Exception("You must supply 1 argument: data description file location.\nThis is a MOLGENIS/XGAP format file for 'Data' entities.\n" +
					"In this file, use at least the following column headers:\n" +
					"\tname\n" +
					"\tinvestigation_Name\n" +
					"\ttargetType\n" +
					"\tfeatureType\n" +
					"\tvalueType\n" +
					"\tstorage\n" +
					"Furthermore, for each 'Data' name X, we expect a file called X.txt which will be converted to X.bin, all in your current working directory.");
		}
		
		//get args
		String dataDescriptionFile = args[0];
		
		//print args
		System.out.println("CsvToBinOnDir called with argument:");
		System.out.println("data description file = " + dataDescriptionFile);
		
		//check if data description file exists
		File dataDesc = new File(dataDescriptionFile);
		if (dataDesc == null || !dataDesc.exists()){
			throw new Exception("Source file '"+dataDescriptionFile+"' not found at location '"+dataDesc.getAbsolutePath()+"'");
		}
		
		System.out.println("Data description file exists, reading..");
		
		ArrayList<Data> datas = getDataObjFromFile(dataDesc);
		
		System.out.println("All 'Data' objects created, starting validation process..");
		
		//FROM NOW ON WE DO END THE PROGRAM ON EXCEPTIONS, BUT GENERATE A REPORT FOR EACH 'DATA' ENTRY

		Map<String,String> report = new HashMap<String,String>(); //no value = no error yet!
		
		System.out.println("Start: " + report.size() + " of " + datas.size() + " failed so far..");
		
		//check if each name is unique
		ArrayList<String> names = new ArrayList<String>();
		for(Data d : datas)
		{
			if(!names.contains(d.getName()))
			{
				names.add(d.getName());
			}
			else
			{
				report.put(d.getName(), "FAIL: Name is not unique");
			}
		}
		
		System.out.println("Name unique check: " + report.size() + " of " + datas.size() + " failed so far..");
		
		//check if the name is OK
		for(Data d : datas)
		{
			if(report.get(d.getName()) == null)
			{
				try
				{
					NameConvention.validateEntityNameStrict(d.getName());
				}
				catch(Exception e)
				{
					report.put(d.getName(), "FAIL: " + e);
				}
			}
		}
		
		System.out.println("Name convention check: " + report.size() + " of " + datas.size() + " failed so far..");
		
		//check if the investigation name is OK
		for(Data d : datas)
		{
			if(report.get(d.getName()) == null)
			{
				try
				{
					NameConvention.validateEntityNameStrict(d.getInvestigation_Name());
				}
				catch(Exception e)
				{
					report.put(d.getName(), "FAIL: " + e);
				}
			}
		}
		
		System.out.println("Investigation name convention check: " + report.size() + " of " + datas.size() + " failed so far..");
		
		//check if the value types are OK
		for(Data d : datas)
		{
			if(report.get(d.getName()) == null)
			{
				if (!d.getValueType().equals("Text") && !d.getValueType().equals("Decimal"))
				{
					report.put(d.getName(), "FAIL: Value type '"+d.getValueType()+"' not reckognized. Use 'Text' or 'Decimal'.");
				}
			}
		}
		
		System.out.println("Value type check: " + report.size() + " of " + datas.size() + " failed so far..");
		
		//check if the src file is there
		for(Data d : datas)
		{
			if(report.get(d.getName()) == null)
			{
				File srcTxt = new File(d.getName() + ".txt");
				if (srcTxt == null || !srcTxt.exists()){
					report.put(d.getName(), "FAIL: Source file '" + d.getName() + ".txt"+"' not found at location '" + srcTxt.getAbsolutePath() + "'");
				}
			}
		}
		
		System.out.println("Source file check: " + report.size() + " of " + datas.size() + " failed so far..");
		
		
		//check if the output file is NOT there
		for(Data d : datas)
		{
			if(report.get(d.getName()) == null)
			{
				File outBin = new File(d.getName() + ".bin");
				if (outBin.exists()){
					report.put(d.getName(), "FAIL: Output file '" + d.getName() + ".bin"+"' already present at location '" + outBin.getAbsolutePath() + "'");
				}
			}
		}
				
		System.out.println("Output file check: " + report.size() + " of " + datas.size() + " failed so far..");
		
		System.out.println("Starting conversion to binary..");
		
		for(Data d : datas)
		{
			if(report.get(d.getName()) == null)
			{
				System.out.println("Working on '" + d.getName() + "'");
				
				File srcTxt = new File(d.getName() + ".txt");
				File outbin = new File(d.getName().toLowerCase() + ".bin"); //FIXME: good behaviour?
				
				System.out.println("Source: " + srcTxt.getAbsolutePath());
				System.out.println("Target: " + outbin.getAbsolutePath());
				
				try
				{
					System.out.println("Reading and checking dimensions..");
					
					int[] dims = VerifyCsv.verify(srcTxt, d.getValueType());
					
					System.out.println("Dimensions verified! Starting conversion..");
					
					new MakeBinary().makeBinaryBackend(d, srcTxt, outbin, dims[0], dims[1]);
					
					System.out.println("..done with converting " + d.getName() + "!");
					
				}
				catch(Exception e)
				{
					report.put(d.getName(), "FAIL: Conversion error - " + e);
				}
				
			}
			else
			{
				System.out.println("Skipped '" + d.getName() + "' due to previous failure");
			}
		}
		
		System.out.println("All checks and/or conversions completed. Final report:");
		
		System.out.println("*****************************************");
		
		for(Data d : datas)
		{
			if(report.get(d.getName()) == null)
			{
				System.out.println(d.getName() + " -> SUCCESS");
			}
			else
			{
				System.out.println(d.getName() + " -> " + report.get(d.getName()));
			}
		}
		System.out.println("*****************************************");
	}

}
