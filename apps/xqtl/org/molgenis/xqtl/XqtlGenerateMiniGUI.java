package org.molgenis.xqtl;

import org.molgenis.Molgenis;

public class XqtlGenerateMiniGUI
{

	/**
	 * 
	 * Experimental GUI, development mock-up.
	 * 
	 * Don't forget to add "modules/minigui" to the build path!
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("apps/xqtl/org/molgenis/xqtl/xqtl_minigui.properties").generate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
