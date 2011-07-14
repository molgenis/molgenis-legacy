package org.molgenis.xgap.xqtlworkbench;


import org.molgenis.Molgenis;
import org.molgenis.generators.ui.EasyPluginControllerGen;
import org.molgenis.generators.ui.EasyPluginModelGen;
import org.molgenis.generators.ui.EasyPluginViewGen;
import org.molgenis.generators.ui.FormControllerGen;
import org.molgenis.generators.ui.PluginControllerGen;

public class XqtlGenerateGUI
{
	
	public static void main(String[] args) throws Exception
	{
		new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench/xqtl.properties",FormControllerGen.class, FormControllerGen.class, EasyPluginControllerGen.class, EasyPluginModelGen.class, EasyPluginViewGen.class, PluginControllerGen.class).generate();
		
	}
}
