package org.molgenis.util.vcf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

import org.apache.commons.io.IOUtils;
import org.molgenis.util.CsvFileReader;
import org.molgenis.util.CsvReader;
import org.molgenis.util.SimpleTuple;
import org.molgenis.util.Tuple;

public class VcfReader
{
	private CsvReader reader;
	private List<String> fileHeaders = new ArrayList<String>();
	private static List<String> normalHeaders = Arrays.asList(new String[]
	{ "#CHROM", "POS", "ID", "REF", "ALT", "QUAL", "FILTER", "INFO", "FORMAT", "" });

	private static List<String> Infofields = new ArrayList<String>();

	public VcfReader(File f) throws IOException, DataFormatException
	{
		// iterate through file to find the last line with ##, that is the
		// blockstart
		String blockStart = null;
		BufferedReader br = new BufferedReader(new FileReader(f));
		try
		{
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null)
			{
				if (strLine.startsWith("##"))
				{
					fileHeaders.add(strLine);
					blockStart = strLine;

					if (strLine.startsWith("##INFO=<"))
					{
						String[] tmp = strLine.substring(11, 30).split(",");
						System.out.println(tmp[0]);

						if (!Infofields.contains(tmp[0])) Infofields.add(tmp[0]);
					}
				}
				else
					break;
			}
		}
		finally
		{
			IOUtils.closeQuietly(br);
		}
		reader = new CsvFileReader(f, blockStart);
	}

	public List<VcfFilter> getFilters()
	{
		List<VcfFilter> result = new ArrayList<VcfFilter>();
		for (String header : this.fileHeaders)
		{
			if (header.startsWith("##FILTER"))
			{
				VcfFilter info = new VcfFilter(this.parseHeader(header));
				result.add(info);
			}
		}
		return result;
	}

	public List<VcfFormat> getFormats()
	{
		List<VcfFormat> result = new ArrayList<VcfFormat>();
		for (String header : this.fileHeaders)
		{
			if (header.startsWith("##FORMAT"))
			{
				VcfFormat info = new VcfFormat(this.parseHeader(header));
				result.add(info);
			}
		}
		return result;
	}

	public List<VcfInfo> getInfos()
	{
		List<VcfInfo> result = new ArrayList<VcfInfo>();
		for (String header : this.fileHeaders)
		{
			if (header.startsWith("##INFO"))
			{
				VcfInfo info = new VcfInfo(this.parseHeader(header));
				result.add(info);
			}
		}
		return result;
	}

	public List<String> getColumnHeaders() throws Exception
	{
		return reader.colnames();
	}

	public List<String> getMetaData()
	{
		return this.fileHeaders;
	}

	public int parse(final VcfReaderListener listener) throws Exception
	{
		final VcfReader vcf = this;

		int lineNumber = 0;
		for (Tuple tuple : reader)
		{
			VcfRecord record = new VcfRecord(vcf, tuple);
			listener.handleLine(lineNumber++, record);
		}
		return lineNumber;
	}

	private Tuple parseHeader(String settings)
	{
		Tuple result = new SimpleTuple();

		// header block starts with < and ends with >
		boolean inHeaderBlock = false;
		// values can be quoted to allow for = and ,
		boolean inQuotes = false;
		// header is divided in key and value using '='
		boolean inKey = true;
		// to store a key while parsing
		String key = "";
		// to store a value while parsing
		String value = "";
		for (Character c : settings.toCharArray())
		{
			if (!inHeaderBlock)
			{
				if ('<' == c)
				{
					inHeaderBlock = true;
				}
			}
			else
			{
				// check for seperator between key and value
				if (inKey && '=' == c)
				{
					inKey = false;
				}
				// parse quotes
				else if (!inKey && '"' == c)
				{
					if (inQuotes)
					{
						inQuotes = false;
						result.set(key, value);
						key = "";
						value = "";
						inKey = true;
					}
					else
						inQuotes = true;
				}
				// close the key/value pair
				else if (!inQuotes && ',' == c)
				{
					result.set(key, value);
					key = "";
					value = "";
					inKey = true;
				}
				// otherwise just add key/value char
				else
				{
					if (inKey) key += c;
					else
						value += c;
				}
			}
		}

		return result;
	}

	public List<String> getSampleList()
	{
		List<String> result = new ArrayList<String>();

		try
		{
			for (String header : this.getColumnHeaders())
			{
				if (!normalHeaders.contains(header)) result.add(header);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public List<String> getInfoFields()
	{
		return Infofields;
	}
}