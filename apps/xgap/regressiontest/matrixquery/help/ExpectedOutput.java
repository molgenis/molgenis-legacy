package regressiontest.matrixquery.help;

import java.io.File;
import java.util.HashMap;

public class ExpectedOutput
{
	public HashMap<String, String> expectedOutputs(){
		
		File pkg = new File(this.getClass().getResource("/expectedoutput").getFile());
		
		
		
		System.out.println(pkg.getAbsolutePath());
		
		System.out.println(pkg.exists());
		
		
		return null;
		
	}
}
