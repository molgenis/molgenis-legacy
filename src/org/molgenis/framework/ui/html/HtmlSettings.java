package org.molgenis.framework.ui.html;

import org.molgenis.framework.ui.html.HtmlElement.UiToolkit;
import org.molgenis.framework.ui.html.render.LinkoutRenderDecorator;
import org.molgenis.framework.ui.html.render.RenderDecorator;

public class HtmlSettings
{	
	public static UiToolkit uiToolkit = UiToolkit.JQUERY;
	//public static UiToolkit uiToolkit = UiToolkit.ORIGINAL;
	public static RenderDecorator defaultRenderDecorator = new LinkoutRenderDecorator();
	
	public static boolean showDescription = false;
}
