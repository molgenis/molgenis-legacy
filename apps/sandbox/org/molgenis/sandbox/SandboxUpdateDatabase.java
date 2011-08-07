package org.molgenis.sandbox;


import org.molgenis.Molgenis;

public class SandboxUpdateDatabase
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/sandbox/sandbox.properties").updateDb(true);
	}
}