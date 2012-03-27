package org.molgenis.omicsconnect;


import org.molgenis.Molgenis;

public class OmicsConnectUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/gwascentral/org/molgenis/hgvbaseg2p/hgvbase.properties").updateDb(true);
	}
}