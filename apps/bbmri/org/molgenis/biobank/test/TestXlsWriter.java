package org.molgenis.biobank.test;

import java.io.File;
import java.io.IOException;
import jxl.write.WriteException;

public class TestXlsWriter {
	
	public static void main(String[] args) throws WriteException, IOException {

		String inputFile = 	System.getProperty("java.io.tmpdir");
		File file = new File(inputFile + "xlswriter.xls");
		
		//XlsWriter xlswriter = new XlsWriter();  //TODO : 	public XlsWriter(PrintWriter writer, List<String> headers) {

		
		//xlswriter.write(file);


		System.out.println("Please check the result file .xls "+ inputFile + "xlswriter.xls");

	}
	
	

}
