package org.molgenis.xqtl.minigui;

import java.io.File;

import org.apache.commons.io.FileUtils;
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

			File src = new File("apps/xqtl/org/molgenis/xqtl/minigui/MiniApplicationView.ftl");
			File dest = new File("../molgenis/src/org/molgenis/framework/ui/ApplicationView.ftl");
			FileUtils.copyFile(src, dest);

			new Molgenis("apps/xqtl/org/molgenis/xqtl/minigui/xqtl_minigui.properties").generate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
