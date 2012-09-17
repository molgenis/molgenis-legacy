package org.molgenis.patho;



import org.molgenis.Molgenis;


public class PathoGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/patho/patho.properties").generate();
	}
}