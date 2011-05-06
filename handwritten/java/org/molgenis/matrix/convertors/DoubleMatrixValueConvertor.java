package org.molgenis.matrix.convertors;

public class DoubleMatrixValueConvertor implements MatrixValueConvertor
{

	@Override
	public Double convert(String value)
	{
		if(value == null) return null;
		return Double.parseDouble(value);
	}

}
