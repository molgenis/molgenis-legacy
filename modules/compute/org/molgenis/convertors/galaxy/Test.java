package org.molgenis.convertors.galaxy;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Test
{
	@XmlElement(name = "param")
	List<TestParam> params = new ArrayList<TestParam>();

	public List<TestParam> getParams()
	{
		return params;
	}

	public void setParams(List<TestParam> params)
	{
		this.params = params;
	}
	
	@XmlElement
	public TestOutput output;

	public TestOutput getOutput()
	{
		return output;
	}

	public void setOutput(TestOutput output)
	{
		this.output = output;
	}
	
	public String toString()
	{
		String result = "";
		for(TestParam p: params) result += "\t"+p.toString()+"\n";
		result+="\t"+output.toString()+"\n";
		return "Test(\n"+result+")";
	}
}
