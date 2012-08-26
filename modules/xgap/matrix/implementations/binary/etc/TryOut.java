package matrix.implementations.binary.etc;

import java.io.File;

import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixInstance_NEW;

public class TryOut
{

	public TryOut() throws Exception
	{
		// rnai_fc_phe
		// cbxn2_wur_nils
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
		
		System.out.println(nieuw.getSubMatrix(new int[]{ 0,1,2,3 }, new int[]{ 0,1,2,3 }).toString());
		
//		long now = System.currentTimeMillis();
//		nieuw.getSubMatrixByOffset(10000, 1000, 300, 1000);
//		System.out.println("NEW TOOK: " + (System.currentTimeMillis()-now));
//		
//		now = System.currentTimeMillis();
//		old.getSubMatrixByOffset(10000, 1000, 300, 1000);
//		System.out.println("OLD TOOK: " + (System.currentTimeMillis()-now));
		
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
