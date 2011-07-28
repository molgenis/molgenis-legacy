package org.molgenis.biobank.test;

import java.io.File;
import java.io.IOException;

import org.molgenis.util.XlsWriter;

import jxl.write.WriteException;

public class TestXlsWriter {
	
	public static void main(String[] args) throws WriteException, IOException {

		String inputFile = 	System.getProperty("java.io.tmpdir");
		File file = new File(inputFile + "xlswriter.xls");
		
		XlsWriter xlswriter = new XlsWriter();
		
		xlswriter.write(file);


		System.out.println("Please check the result file .xls "+ inputFile + "xlswriter.xls");

	}
	
	

}
