package matrix.test.implementations.binary;

import java.util.ArrayList;
import java.util.List;

import matrix.DataMatrixInstance;
import matrix.general.DataMatrixHandler;
import matrix.implementations.binary.BinaryDataMatrixInstance;

import org.molgenis.data.Data;
import org.molgenis.framework.db.Database;

import app.DatabaseFactory;

public class TestBinSubMatrix {

	public TestBinSubMatrix() throws Exception {
		Database db = DatabaseFactory.create("handwritten/properties/gcc.test.properties");

		List<BinaryDataMatrixInstance> bmList = new ArrayList<BinaryDataMatrixInstance>();

		for (Data data : db.find(Data.class)) {
			BinaryDataMatrixInstance bm = (BinaryDataMatrixInstance) new DataMatrixHandler(db).createInstance(data, db);
			bmList.add(bm);
		}
		
		for(BinaryDataMatrixInstance bm : bmList){
			//check rownames/colnames order
			int halfCols = bm.getNumberOfCols()/2;
			int halfRows = bm.getNumberOfRows()/2;
			
			DataMatrixInstance sub = bm.getSubMatrixByOffset(0, halfRows, 0, halfCols);
			
			List<String> originalComplete = bm.getColNames();
			List<String> originalHalf = bm.getColNames().subList(0, halfCols);
			List<String> subbed = sub.getColNames();
			
			
			
			for(int i = 0; i < halfCols; i ++){
				String o = originalHalf.get(i);
				String s = subbed.get(i);
				String c = originalComplete.get(i);
				if(!o.equals(s)){
					throw new Exception("Not equal: " + o + " vs. " + s + " at index " + i);
				}
			}
			
		}
		

	}

	public static void main(String[] args) throws Exception {
		new TestBinSubMatrix();
	}

}
