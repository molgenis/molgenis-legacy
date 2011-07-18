package org.molgenis.hgvbaseg2p;


import org.molgenis.Molgenis;

public class HgvbaseUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/hgvbaseg2p/hgvbase.properties").updateDb();
	}
}