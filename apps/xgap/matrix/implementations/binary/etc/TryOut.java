package matrix.implementations.binary.etc;

import java.io.File;

import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixInstance_NEW;

public class TryOut
{

	public TryOut() throws Exception
	{
		// rnai_FC_phe
		// CBxN2_USA_RILs
		// rock_qtl
		// age_lsp_phe
		File loc = new File("/data/xqtl_panacea/binarydatamatrix/rock_qtl.bin");

		System.out.println("loc : " + loc.getAbsolutePath());
		BinaryDataMatrixInstance old = new BinaryDataMatrixInstance(loc);
		BinaryDataMatrixInstance_NEW nieuw = new BinaryDataMatrixInstance_NEW(loc);

//		System.out.println("old, element = '" + old.getElement(0, 0)+"'");
//		System.out.println("new, element = '" + nieuw.getElement(0, 0)+"'");


//		System.out.println("old, row = " + printObjArr(old.getRow(5)));
//		System.out.println("new, row = " + printObjArr(nieuw.getRow(5)));
		
//		System.out.println("old, col = " + printObjArr(old.getCol(35)));
		System.out.println("new, col = " + printObjArr(nieuw.getCol(5)));
	}
	
	private String printObjArr(Object[] arr)
	{
		String printMe = "";
		for(Object o : arr)
		{
			printMe += "'" + o.toString() + "', ";
		}
		return printMe;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		new TryOut();

	}

}
