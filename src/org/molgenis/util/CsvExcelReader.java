package org.molgenis.util;

import java.io.IOException;
import java.util.List;

public class CsvExcelReader implements CsvReader
{

	
	
	@Override
	public void close() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> colnames() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disableHeader(boolean b)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getBlockEnd()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBlockStart()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMissingValues()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int parse(CsvReaderListener... listeners) throws Exception
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int parse(int noElements, CsvReaderListener... listeners)
			throws Exception
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int parse(List<Integer> rows, CsvReaderListener... listeners)
			throws Exception
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void renameField(String from, String to) throws Exception
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reset() throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> rownames() throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBlockEnd(String blockEnd)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlockStart(String blockStart)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColnames(List<String> fields)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMissingValues(String missingValue)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSeparator(char string)
	{
		// TODO Auto-generated method stub
		
	}

}
