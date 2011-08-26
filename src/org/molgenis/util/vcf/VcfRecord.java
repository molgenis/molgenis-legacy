package org.molgenis.util.vcf;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.molgenis.util.Tuple;

public class VcfRecord
{
	VcfReader reader;
	Tuple record;
	// cache for the info map
	private Map<String, Object> infoMap = null;

	public VcfRecord(VcfReader reader, Tuple record)
	{
		this.reader = reader;
		this.record = record;
	}

	public String getChrom()
	{
		return record.getString("#CHROM");
	}

	public Integer getPos()
	{
		return record.getInt("POS");
	}

	public List<String> getId()
	{
		return Arrays.asList(record.getString("ID").split(";"));
	}

	public String getRef()
	{
		return record.getString("REF");
	}

	public List<String> getAlt()
	{
		return Arrays.asList(record.getString("ALT").split(","));
	}

	public Double getQual()
	{
		return record.getDecimal("QUAL");
	}

	public List<String> getFilter()
	{
		return Arrays.asList(record.getString("FILTER").split(";"));
	}

	public String getInfo()
	{
		return record.getString("INFO");
	}

	public List<VcfInfo> getInfoMetadata()
	{
		return reader.getInfos();
	}

	public Object getInfo(String key)
	{
		if (this.infoMap == null)
		{
			this.infoMap = new LinkedHashMap<String, Object>();

			String[] keyvalues = this.getInfo().split(";");
			for (String keyvalue : keyvalues)
			{
				String[] kv = keyvalue.split("=");
				// boolean value
				if (kv.length == 1) this.infoMap.put(kv[0], true);
				else
				{
					this.infoMap.put(kv[0], kv[1]);
				}
			}
		}
		return this.infoMap.get(key);
	}

	public List<String> getFormat()
	{
		return Arrays.asList(record.getString("FORMAT"));
	}

	public List<String> getSamples()
	{
		return this.reader.getSampleList();
	}

	public String getSampleValue(String sample, String key)
	{
		// first get the position from the key
		int index = getFormat().indexOf(key);

		// then get the sample from the tuple
		String sampleRecord = record.getString(sample);

		// and parse out the value
		if (sampleRecord != null)
		{
			String[] values = sampleRecord.split(":");
			if (index < values.length) return values[index];
		}
		return null;
	}

	// just for testing
	public String toString()
	{
		String info = "[";
		boolean first = true;
		for (VcfInfo i : this.getInfoMetadata())
		{
			if (!first) info += ",";
			info += i.getId() + "=" + this.getInfo(i.getId());
			first = false;
		}
		info += "]";

		String sampleInfo = "";
		for (String sample : this.getSamples())
		{
			sampleInfo += ", "+sample + "=[";
			first = true;
			for (String format : this.getFormat())
			{
				if (!first) sampleInfo += ",";
				sampleInfo += format + "="
						+ this.getSampleValue(sample, format);
				first = false;
			}
			sampleInfo += "]";
		}

		return String
				.format(
						"VcfRecord(chrom=%s,pos=%s,id=%s,ref=%s,alt=%s,qual=%s,filter=%s,info=%s,format=%s%s )",
						getChrom(), getPos(), getId(), getRef(), getAlt(),
						getQual(), getFilter(), info, getFormat(), sampleInfo);
	}
}
