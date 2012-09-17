package matrix.general;

import matrix.implementations.binary.BinaryDataMatrixWriter;

public class CsvToBin
{

	/**
	 * @param args
	 * @throws Exception 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		BinaryDataMatrixWriter b = new BinaryDataMatrixWriter();
		b.CsvToBin(args);
	}
}
