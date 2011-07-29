package tmp;

import java.util.ArrayList;
import java.util.List;

import org.molgenis.framework.db.Database;

import matrix.test.implementations.general.Helper;
import app.DatabaseFactory;

public class AddRandomBinMatrix
{

	List<String> uniqueNames = new ArrayList<String>();
	
	public AddRandomBinMatrix() throws Exception{
		int matrixDimension1 = 500;
		int matrixDimension2 = 600;
		int maxTextLength = 10;
		boolean fixedTextLength = false;
		boolean sparse = false;
	
		Database db = DatabaseFactory.create("handwritten/properties/gcc.properties");
		
		Helper h = new Helper(db);

		h.prepareDatabaseAndFiles("Binary", matrixDimension1, matrixDimension2, maxTextLength, fixedTextLength, sparse);

	//	new BinaryDataMatrixWriter(h.getDataList(), h.getInputFilesDir(), db);
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		new AddRandomBinMatrix();
	}

	
	
}
