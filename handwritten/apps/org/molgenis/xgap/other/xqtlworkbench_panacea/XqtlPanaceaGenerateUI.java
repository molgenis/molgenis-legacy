package org.molgenis.xgap.other.xqtlworkbench_panacea;


import org.molgenis.Molgenis;
import org.molgenis.generators.server.MolgenisServletGen;
import org.molgenis.generators.ui.EasyPluginControllerGen;
import org.molgenis.generators.ui.EasyPluginModelGen;
import org.molgenis.generators.ui.EasyPluginViewGen;
import org.molgenis.generators.ui.FormControllerGen;
import org.molgenis.generators.ui.PluginControllerGen;

public class XqtlPanaceaGenerateUI
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			new Molgenis("handwritten/apps/org/molgenis/xgap/xqtlworkbench_panacea/xqtlpanacea.properties",FormControllerGen.class, FormControllerGen.class, EasyPluginControllerGen.class, EasyPluginModelGen.class, EasyPluginViewGen.class, PluginControllerGen.class).generate();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
