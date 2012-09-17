/* Date:        November 11, 2009
 * Template:	PluginScreenJavaTemplateGen.java.ftl
 * generator:   org.molgenis.generators.ui.PluginScreenJavaTemplateGen 3.3.2-testing
 * 
 * THIS FILE IS A TEMPLATE. PLEASE EDIT :-)
 */

package org.molgenis.wormqtl.header;

import org.molgenis.framework.ui.ScreenController;


/**
 * A simple plugin to create the header of the MOLGENIS application. This
 * includes the header logo as well as the top level menu items for
 * documentation, services etc (replaces the hardcoded header).
 * 
 * @author Morris Swertz
 */
public class MolgenisHeader extends org.molgenis.xgap.xqtlworkbench.header.MolgenisHeader
{
	private static final long serialVersionUID = 7628452789847569319L;

	public MolgenisHeader(String name, ScreenController<?> parent)
	{
		super(name, parent);
	}

	@Override
	public String getViewName()
	{
		return "org_molgenis_wormqtl_header_MolgenisHeader";
	}

	@Override
	public String getViewTemplate()
	{
		return "org/molgenis/wormqtl/header/MolgenisHeader.ftl";
	}
	
	@Override
	public String getCustomHtmlHeaders()
	{
		return "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/xqtlpanaceacolors.css\">" + "\n" +
			   "<link rel=\"stylesheet\" style=\"text/css\" href=\"clusterdemo/main_override.css\">" ;
	}
}
