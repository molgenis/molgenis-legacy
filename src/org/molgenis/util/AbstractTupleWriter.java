package org.molgenis.util;

import java.util.List;

public abstract class AbstractTupleWriter implements TupleWriter
{

	@Override
	public void writeHeader() throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void writeRow(Entity e) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void writeRow(Tuple t)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void writeValue(Object object)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setHeaders(List<String> fields)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void writeEndOfLine()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void close() throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void writeMatrix(List<String> rowNames, List<String> colNames,
			Object[][] elements)
	{
		// TODO Auto-generated method stub

	}

}
