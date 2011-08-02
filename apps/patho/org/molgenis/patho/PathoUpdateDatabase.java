package org.molgenis.patho;



import org.molgenis.Molgenis;

public class PathoUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/patho/patho.properties").updateDb();
	}
}