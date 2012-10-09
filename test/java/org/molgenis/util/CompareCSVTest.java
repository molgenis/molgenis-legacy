package org.molgenis.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CompareCSVTest
{
	@Test
	public void testCompareCSVFilesByContent_equalData() throws IOException
	{
		File file0 = File.createTempFile("CompareCSVTest_file0", null);
		File file1 = File.createTempFile("CompareCSVTest_file1", null);
		try
		{
			FileUtils.write(file0, "col0,col1\nval0,val1\nval2,val3", Charset.forName("UTF-8"));
			FileUtils.write(file1, "col0,col1\nval0,val1\nval2,val3", Charset.forName("UTF-8"));
			Assert.assertEquals(true, CompareCSV.compareCSVFilesByContent(file0, file1));
			Assert.assertEquals(true, CompareCSV.compareCSVFilesByContent(file1, file0));
		}
		finally
		{
			file1.delete();
			file0.delete();
		}
	}

	@Test
	public void testCompareCSVFilesByContent_notEqualData() throws IOException
	{
		File file0 = File.createTempFile("CompareCSVTest_file0", null);
		File file1 = File.createTempFile("CompareCSVTest_file1", null);
		try
		{
			FileUtils.write(file0, "col0,col1\nval0,val1\nval2,val3", Charset.forName("UTF-8"));
			FileUtils.write(file1, "col0,col1\nval4,val5\nval6,val7", Charset.forName("UTF-8"));
			Assert.assertEquals(false, CompareCSV.compareCSVFilesByContent(file0, file1));
			Assert.assertEquals(false, CompareCSV.compareCSVFilesByContent(file1, file0));
		}
		finally
		{
			file1.delete();
			file0.delete();
		}
	}

	@Test
	public void testCompareCSVFilesByContent_notEqualNumberRows() throws IOException
	{
		File file0 = File.createTempFile("CompareCSVTest_file0", null);
		File file1 = File.createTempFile("CompareCSVTest_file1", null);
		try
		{
			FileUtils.write(file0, "col0,col1\nval0,val1\nval2,val3", Charset.forName("UTF-8"));
			FileUtils.write(file1, "col0,col1\nval4,val5\nval6,val7\nval8,val9", Charset.forName("UTF-8"));
			Assert.assertEquals(false, CompareCSV.compareCSVFilesByContent(file0, file1));
			Assert.assertEquals(false, CompareCSV.compareCSVFilesByContent(file1, file0));
		}
		finally
		{
			file1.delete();
			file0.delete();
		}
	}

	@Test
	public void testCompareCSVFilesByContent_notEqualNumberCols() throws IOException
	{
		File file0 = File.createTempFile("CompareCSVTest_file0", null);
		File file1 = File.createTempFile("CompareCSVTest_file1", null);
		try
		{
			FileUtils.write(file0, "col0,col1\nval0,val1\nval2,val3", Charset.forName("UTF-8"));
			FileUtils.write(file1, "col0\nval0\nval2", Charset.forName("UTF-8"));
			Assert.assertEquals(false, CompareCSV.compareCSVFilesByContent(file0, file1));
			Assert.assertEquals(false, CompareCSV.compareCSVFilesByContent(file1, file0));
		}
		finally
		{
			file1.delete();
			file0.delete();
		}
	}
}
