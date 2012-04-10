package qtltogff;

public class RunDemo {

	/**
	 * Run the QtlToGFF tool on the example dataset
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String[] args2 = new String[]{
				
				"examples/QtlToGFF/age3_qtl.bin",
				"examples/QtlToGFF/chromosomes.txt",
				"examples/QtlToGFF/markers.txt",
				"examples/QtlToGFF/probes.txt",
				"T",
				"T",
				"T",
				"3.5",
				"1.5",
				"10",
				
				};
		QtlToGFF.main(args2);

	}

}
