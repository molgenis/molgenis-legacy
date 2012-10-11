package org.molgenis.util;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

public class TextFileUtilsTest
{

	@Test
	public void getAmountOfNewlinesAtFileEnd_LF() throws Exception
	{
		File file0 = File.createTempFile("TextFileUtilsTest_file0", null);
		try
		{
			FileUtils.write(file0, "a\nb\n", Charset.forName("UTF-8"));
			assertEquals(1, TextFileUtils.getAmountOfNewlinesAtFileEnd(file0));
		}
		finally
		{
			file0.delete();
		}
	}

	@Test
	public void getAmountOfNewlinesAtFileEnd_CRLF() throws Exception
	{
		File file0 = File.createTempFile("TextFileUtilsTest_file0", null);
		try
		{
			FileUtils.write(file0, "a\r\nb\r\n", Charset.forName("UTF-8"));
			assertEquals(1, TextFileUtils.getAmountOfNewlinesAtFileEnd(file0));
		}
		finally
		{
			file0.delete();
		}
	}
}
