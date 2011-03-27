package plugins.LuceneIndex;
import java.io.File;
//import java.io.IOException;


public class TestLuceneConfFilePathClass {

	/**
	 * @param args
	 */
	
	public TestLuceneConfFilePathClass() {
		File argh = new File(this.getClass().getResource("LuceneIndexConfiguration.properties").getFile().replace("%20", " "));
		System.out.println(argh.getAbsolutePath());
		System.out.println(argh.exists());
	}
	
	public static void main(String[] args) {
		//System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaa" + (new File("LuceneIndexConfiguration.properties").getAbsolutePath()));
		TestLuceneConfFilePathClass myTestLuceneConfFilePathClass = new TestLuceneConfFilePathClass();
	}

}
