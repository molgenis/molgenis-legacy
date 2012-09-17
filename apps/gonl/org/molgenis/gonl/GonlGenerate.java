package org.molgenis.gonl;



import org.molgenis.Molgenis;


public class GonlGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/gonl/gonl.properties").generate();
	}
}