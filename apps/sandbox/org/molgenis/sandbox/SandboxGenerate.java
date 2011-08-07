package org.molgenis.sandbox;


import org.molgenis.Molgenis;


public class SandboxGenerate
{
	public static void main(String[] args) throws Exception
	{
		new Molgenis("apps/sandbox/sandbox.properties").generate();
	}
}