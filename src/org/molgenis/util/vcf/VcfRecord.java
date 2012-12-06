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
	private Map<String, List<String>> infoMap = null;

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

	public List<String> getInfo(String key)
	{
		if (this.infoMap == null)
		{
			this.infoMap = new LinkedHashMap<String, List<String>>();

			String[] keyvalues = this.getInfo().split(";");
			for (String keyvalue : keyvalues)
			{
				String[] kv = keyvalue.split("=");
				if (kv.length == 1) this.infoMap.put(kv[0], Arrays.asList(new String[]
				{ "TRUE" }));
				else
					this.infoMap.put(kv[0], Arrays.asList(kv[1].split(",")));
			}
		}
		return this.infoMap.get(key);
	}

	public List<String> getFormat()
	{
		return Arrays.asList(record.getString("FORMAT").split(":"));
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
	@Override
	public String toString()
	{
		StringBuilder infoBuilder = new StringBuilder().append('[');
		boolean first = true;
		for (VcfInfo i : this.getInfoMetadata())
		{
			if (!first) infoBuilder.append(',');
			infoBuilder.append(i.getId()).append('=').append(this.getInfo(i.getId()));
			first = false;
		}
		infoBuilder.append(']');

		StringBuilder sampleInfoBuilder = new StringBuilder();
		for (String sample : this.getSamples())
		{
			sampleInfoBuilder.append(", ").append(sample).append("=[");
			first = true;
			for (String format : this.getFormat())
			{
				if (!first) sampleInfoBuilder.append(',');
				sampleInfoBuilder.append(format).append('=').append(this.getSampleValue(sample, format));
				first = false;
			}
			sampleInfoBuilder.append(']');
		}

		return String.format("VcfRecord(chrom=%s,pos=%s,id=%s,ref=%s,alt=%s,qual=%s,filter=%s,info=%s,format=%s%s )",
				getChrom(), getPos(), getId(), getRef(), getAlt(), getQual(), getFilter(), infoBuilder.toString(),
				getFormat(), sampleInfoBuilder.toString());
	}
}
