package matrix.general;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.molgenis.util.CsvFileReader;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestPreProcessMatrix
{

	File testMatrix;

	@BeforeClass
	public void setup() throws Exception
	{
		String testContent = "\tcol1$\t456col2\tcol3\n";
		testContent += "234row1\tval1\tval2\tval3\n";
		testContent += "&row2\tval4\tval5\tval6\n";
		testContent += "678row3#\tval7\tval8\tval9\n";
		File testMatrix = new File(System.getProperty("java.io.tmpdir") + File.separator + "testMatrix"
				+ System.nanoTime() + ".txt");
		BufferedWriter out = new BufferedWriter(new FileWriter(testMatrix));
		out.write(testContent);
		out.close();
		this.testMatrix = testMatrix;
	}

	@Test
	public void testPrependUnderscoreToRowNames() throws Exception
	{
		PreProcessMatrix pm = new PreProcessMatrix(this.testMatrix);
		pm.prependUnderscoreToRowNames();
		File result = pm.getResult();
		String actual = readFile(result);
		String expected = "\tcol1$\t456col2\tcol3\n";
		expected += "_234row1\tval1\tval2\tval3\n";
		expected += "_&row2\tval4\tval5\tval6\n";
		expected += "_678row3#\tval7\tval8\tval9\n";
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testPrependUnderscoreToColNames() throws Exception
	{
		PreProcessMatrix pm = new PreProcessMatrix(this.testMatrix);
		pm.prependUnderscoreToColNames();
		File result = pm.getResult();
		String actual = readFile(result);
		String expected = "\t_col1$\t_456col2\t_col3\n";
		expected += "234row1\tval1\tval2\tval3\n";
		expected += "&row2\tval4\tval5\tval6\n";
		expected += "678row3#\tval7\tval8\tval9\n";
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testEscapeRowNames() throws Exception
	{
		PreProcessMatrix pm = new PreProcessMatrix(this.testMatrix);
		pm.escapeRowNames();
		File result = pm.getResult();
		String actual = readFile(result);
		String expected = "\tcol1$\t456col2\tcol3\n";
		expected += "row1\tval1\tval2\tval3\n";
		expected += "row2\tval4\tval5\tval6\n";
		expected += "row3\tval7\tval8\tval9\n";
		Assert.assertEquals(actual, expected);
	}
	
	@Test
	public void testEscapeColNames() throws Exception
	{
		PreProcessMatrix pm = new PreProcessMatrix(this.testMatrix);
		pm.escapeColNames();
		File result = pm.getResult();
		String actual = readFile(result);
		String expected = "\tcol1\tcol2\tcol3\n";
		expected += "234row1\tval1\tval2\tval3\n";
		expected += "&row2\tval4\tval5\tval6\n";
		expected += "678row3#\tval7\tval8\tval9\n";
		Assert.assertEquals(actual, expected);
	}

	@Test
	public void testPrependAndEscapeAll() throws Exception
	{
		PreProcessMatrix pm = new PreProcessMatrix(this.testMatrix);
		pm.prependUnderscoreToRowNames();
		pm.prependUnderscoreToColNames();
		pm.escapeRowNames();
		pm.escapeColNames();
		File result = pm.getResult();
		String actual = readFile(result);
		String expected = "\t_col1\t_456col2\t_col3\n";
		expected += "_234row1\tval1\tval2\tval3\n";
		expected += "_row2\tval4\tval5\tval6\n";
		expected += "_678row3\tval7\tval8\tval9\n";
		Assert.assertEquals(actual, expected);
	}

	private String readFile(File theFile) throws java.io.IOException
	{
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(theFile));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1)
		{
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();
	}

}
