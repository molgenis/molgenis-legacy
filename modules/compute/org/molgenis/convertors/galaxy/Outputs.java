package org.molgenis.convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Outputs
{
	@XmlElement(name="data")
	List<Data> data = new ArrayList<Data>();

	public List<Data> getData()
	{
		return data;
	}
}
