package matrix.general;

import matrix.implementations.csv.CSVDataMatrixWriter;

public class BinToCsv
{

	/**
	 * @param args
	 * @throws Exception 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		CSVDataMatrixWriter c = new CSVDataMatrixWriter();
		c.BinToCsv(args);
	}
}
