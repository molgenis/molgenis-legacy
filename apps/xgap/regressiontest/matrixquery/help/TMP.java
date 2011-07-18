package regressiontest.matrixquery.help;

import java.io.File;
import java.io.FileNotFoundException;

import matrix.test.implementations.general.Helper;

public class TMP
{

public TMP() throws FileNotFoundException, Exception{
	File testAgainstFile = new File(this.getClass().getResource("expectedoutput/filterbyrowentityvalues").getFile().replace("%20", " "));
	String testAgainst = Helper.readFileToString(testAgainstFile);
	System.out.println(testAgainst);
	
}
	public static void main(String[] args) throws FileNotFoundException, Exception
	{
		new TMP();
	
	}

}
