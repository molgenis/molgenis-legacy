package org.molgenis.hgvbaseg2p;


import org.molgenis.Molgenis;


public class HgvbaseGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/gwascentral/org/molgenis/hgvbaseg2p/hgvbase.properties").generate();
	
	//	/molgenis_apps/apps/gwascentral/org/molgenis/hgvbaseg2p/hgvbase.properties
	
	}
}