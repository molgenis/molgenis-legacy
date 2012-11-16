package org.molgenis.generators.excel;

import org.molgenis.generators.ForEachEntityGenerator;

public class ExcelReaderGen extends ForEachEntityGenerator
{
	@Override
	public String getDescription()
	{
		return "Generates ExcelReaders for each entity";
	}

	@Override
	public Boolean skipSystem()
	{
		return false;
	}
}
