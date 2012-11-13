package org.molgenis.generators.csv;

import org.molgenis.generators.ForEachEntityGenerator;

public class CsvReaderGen extends ForEachEntityGenerator
{
	@Override
	public String getDescription()
	{
		return "Generates CsvReaders for each entity";
	}

	@Override
	public Boolean skipSystem()
	{
		return false;
	}
}
