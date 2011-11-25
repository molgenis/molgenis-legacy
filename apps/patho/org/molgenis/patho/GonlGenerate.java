package org.molgenis.patho;



import org.molgenis.Molgenis;


public class GonlGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/patho/gonl.properties").generate();
	}
}