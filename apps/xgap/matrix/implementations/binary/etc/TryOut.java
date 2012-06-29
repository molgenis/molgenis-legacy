package matrix.implementations.binary.etc;

import java.io.File;

import matrix.implementations.binary.BinaryDataMatrixInstance;
import matrix.implementations.binary.BinaryDataMatrixInstance_NEW;

public class TryOut
{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		
		File loc = new File("/data/xqtl_panacea/binarydatamatrix/rock_qtl.bin");
		
		System.out.println("loc : " + loc.getAbsolutePath());
		BinaryDataMatrixInstance old = new BinaryDataMatrixInstance(loc);
		System.out.println(old.getElement(0, 0));
		
		BinaryDataMatrixInstance_NEW nieuw = new BinaryDataMatrixInstance_NEW(loc);
		System.out.println(nieuw.getElement(0, 0));
	}

}
