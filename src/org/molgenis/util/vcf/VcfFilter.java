package org.molgenis.util.vcf;

import org.molgenis.util.Tuple;

public class VcfFilter
{
	public enum InfoType
	{
		INTEGER, FLOAT, FLAG, CHARACTER, STRING, UNKNOWN;

		public static InfoType getValue(String value)
		{
			if ("Integer".equals(value)) return INTEGER;
			if ("Float".equals(value)) return FLOAT;
			if ("Flag".equals(value)) return FLAG;
			if ("Character".equals(value)) return CHARACTER;
			if ("String".equals(value)) return STRING;
			return UNKNOWN;
		}
	};

	public VcfFilter()
	{

	}

	public VcfFilter(Tuple settings)
	{
		this.id = settings.getString("ID");
		this.description = settings.getString("Description");
	}

	private String id;
	private String description;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	@Override
	public String toString()
	{
		return String.format("VcfFilter(ID=%s,Description=\"%s\")", getId(), getDescription());
	}
}
