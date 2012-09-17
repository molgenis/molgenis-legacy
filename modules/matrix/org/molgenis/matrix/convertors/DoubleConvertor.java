package org.molgenis.matrix.convertors;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DoubleConvertor implements ValueConvertor<Double>
{

	@Override
	public Double read(String value)
	{
		if (value == null) return null;
		return Double.parseDouble(value);
	}

	@Override
	public String write(Double value)
	{
		if (value == null) return null;
		return value.toString();
	}

	@Override
	public Double read(RandomAccessFile raf) throws IOException
	{
		byte[] arr = new byte[8];
		raf.read(arr);
		double d = byteArrayToDouble(arr);
		if (d == Double.MAX_VALUE)
		{
			return null;
		}
		return d;
	}

	@Override
	public Class<Double> getValueType()
	{
		return Double.class;
	}

	public static double byteArrayToDouble(byte[] arr)
	{
		long longBits = 0;
		for (int i = 0; i < arr.length; i++)
		{
			longBits <<= 8;
			longBits |= (long) arr[i] & 255;
		}
		return Double.longBitsToDouble(longBits);
	}

}
