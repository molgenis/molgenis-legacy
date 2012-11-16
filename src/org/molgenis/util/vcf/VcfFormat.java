package org.molgenis.util.vcf;

import org.molgenis.util.Tuple;

public class VcfFormat
{
	public enum InfoType
	{
		INTEGER, FLOAT, CHARACTER, STRING, UNKNOWN;

		public static InfoType getValue(String value)
		{
			if ("Integer".equals(value)) return INTEGER;
			if ("Float".equals(value)) return FLOAT;
			if ("Character".equals(value)) return CHARACTER;
			if ("String".equals(value)) return STRING;
			return UNKNOWN;
		}
	};

	public VcfFormat()
	{

	}

	public VcfFormat(Tuple settings)
	{
		this.id = settings.getString("ID");
		try
		{
			this.number = settings.getInt("Number");
		}
		catch (Exception e)
		{
			this.number = -1;
		}
		this.type = InfoType.getValue(settings.getString("Type"));
		this.description = settings.getString("Description");
	}

	private String id;
	private InfoType type;
	private Integer number;
	private String description;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public InfoType getType()
	{
		return type;
	}

	public void setType(InfoType type)
	{
		this.type = type;
	}

	public Integer getNumber()
	{
		return number;
	}

	public void setNumber(Integer number)
	{
		this.number = number;
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
		return String.format("VcfFormat(ID=%s,Number=%s,Type=%s,Description=\"%s\")", getId(), getNumber(), getType(),
				getDescription());
	}
}
