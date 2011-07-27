package org.molgenis.util;

import java.io.File;
import java.util.List;

/**
 * Write values to an Excel file
 * @author despoina
 */

public class XlsWriter implements CsvWriter
{

	public XlsWriter(File excelFile)
	{

	}

	@Override
	public void writeHeader()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeRow(Entity e)
	{
		//write all values of this entity to the current row
		//keep order as used in the headers
		
	}

	@Override
	public void writeRow(Tuple t)
	{
		//write all values of this tuple to the current row
		//keep order as via writeHeaders
		
	}

	@Override
	public void setHeaders(List<String> fields)
	{
		//create the header row in excel and then writeEndOfLine 
		
	}
	
	@Override
	public void writeValue(Object object)
	{
		//put this object in the current cell and go to next cell
		
	}

	@Override
	public void writeEndOfLine()
	{
		//go to next row
		
	}

	@Override
	public void close()
	{
		//close the excel file; no writing allowed anymore
		
	}

	@Override
	public void writeMatrix(List<String> rowNames, List<String> colNames,
			Object[][] elements)
	{
		//first write the headers (colnames), first cell is empty because this is a matrix
		//than for each row first write the rowname
		//than use the row[i] to write the rest of the row
		
	}
	
	
}
