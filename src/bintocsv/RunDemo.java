package bintocsv;

public class RunDemo {

	/**
	 * Run the BinToCsv tool on the example dataset
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String[] args2 = new String[]{
				
				"examples/QtlToGFF/age3_qtl.bin",
				"L",
				
				};
		BinToCsv.main(args2);

	}

}
