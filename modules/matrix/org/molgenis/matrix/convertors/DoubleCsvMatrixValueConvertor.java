package org.molgenis.matrix.convertors;

public class DoubleCsvMatrixValueConvertor implements CsvMatrixValueConvertor
{

	@Override
	public Double convert(String value)
	{
		if(value == null) return null;
		return Double.parseDouble(value);
	}

}
