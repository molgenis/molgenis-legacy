package org.molgenis.io;

import static org.testng.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FilenameUtils;
import org.testng.annotations.Test;

public class TableReaderFactoryTest
{

	@Test
	public void createFile_csv() throws IOException
	{
		File file = File.createTempFile("file", ".csv");
		try
		{
			TableReader tableReader = TableReaderFactory.create(file);
			try
			{
				assertNotNull(tableReader.getTupleReader(FilenameUtils.getBaseName(file.getName())));
			}
			finally
			{
				tableReader.close();
			}
		}
		finally
		{
			file.delete();
		}
	}

	@Test
	public void createFile_txt() throws IOException
	{
		File file = File.createTempFile("file", ".txt");
		try
		{
			TableReader tableReader = TableReaderFactory.create(file);
			try
			{
				assertNotNull(tableReader.getTupleReader(FilenameUtils.getBaseName(file.getName())));
			}
			finally
			{
				tableReader.close();
			}
		}
		finally
		{
			file.delete();
		}
	}

	@Test
	public void createFile_tsv() throws IOException
	{
		File file = File.createTempFile("file", ".tsv");
		try
		{
			TableReader tableReader = TableReaderFactory.create(file);
			try
			{
				assertNotNull(tableReader.getTupleReader(FilenameUtils.getBaseName(file.getName())));
			}
			finally
			{
				tableReader.close();
			}
		}
		finally
		{
			file.delete();
		}
	}

	@Test(expectedExceptions = IOException.class)
	public void createFile_unknownFormat() throws IOException
	{
		File file = File.createTempFile("file", ".burp");
		try
		{
			TableReader tableReader = TableReaderFactory.create(file);
			try
			{
				assertNotNull(tableReader.getTupleReader(FilenameUtils.getBaseName(file.getName())));
			}
			finally
			{
				tableReader.close();
			}
		}
		finally
		{
			file.delete();
		}
	}

	@Test
	public void createListFile() throws IOException
	{
		File file1 = File.createTempFile("file1", ".csv");
		File file2 = File.createTempFile("file2", ".tsv");
		try
		{
			TableReader tableReader = TableReaderFactory.create(Arrays.asList(file1, file2));
			try
			{
				assertNotNull(tableReader.getTupleReader(FilenameUtils.getBaseName(file1.getName())));
				assertNotNull(tableReader.getTupleReader(FilenameUtils.getBaseName(file2.getName())));
			}
			finally
			{
				tableReader.close();
			}
		}
		finally
		{
			file1.delete();
			file2.delete();
		}
	}
}
